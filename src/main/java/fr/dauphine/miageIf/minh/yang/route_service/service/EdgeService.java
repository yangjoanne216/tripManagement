package fr.dauphine.miageIf.minh.yang.route_service.service;

import fr.dauphine.miageIf.minh.yang.route_service.dao.CityDao;
import fr.dauphine.miageIf.minh.yang.route_service.dao.EdgeDao;
import fr.dauphine.miageIf.minh.yang.route_service.dto.CreationEdgeRequest;
import fr.dauphine.miageIf.minh.yang.route_service.dto.EdgeResponse;
import fr.dauphine.miageIf.minh.yang.route_service.dto.UpdateEdgeRequest;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.CityNotFoundException;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.EdgeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * Edge (LOCATED_AT) 关系的增删改查逻辑都放在这里。
 */
@Service
@RequiredArgsConstructor
public class EdgeService {

    private final CityDao cityDao;
    private final EdgeDao edgeDao;
    private final Neo4jClient neo4jClient;

    /**
     * 查询所有 LOCATED_AT 关系（Edge），返回 List<EdgeResponse>。
     * 每条记录包含：routeId(Neo4j内部ID)、sourceCityId、destinationCityId、distanceKm、travelTimeMin。
     */
    @Transactional(readOnly = true)
    public List<EdgeResponse> getAllEdges() {
        String cypher = ""
                + "MATCH (s:City)-[r:LOCATED_AT]-(d:City) "
                + "RETURN id(r) AS routeId, s.cityId AS srcId, d.cityId AS dstId, "
                + "r.distanceKm AS dKm, r.travelTimeMin AS tMin";

        return (List<EdgeResponse>) neo4jClient.query(cypher)
                // 将每一行映射成 EdgeResponse
                .fetchAs(EdgeResponse.class)
                .mappedBy((typeSystem, record) -> {
                    long routeId = record.get("routeId").asLong();
                    String srcId = record.get("srcId").asString();
                    String dstId = record.get("dstId").asString();
                    int dKm = record.get("dKm").asInt();
                    int tMin = record.get("tMin").asInt();
                    return new EdgeResponse(routeId, srcId, dstId, dKm, tMin);
                })
                .all();
    }

    /**
     * 查询两个 City 之间是否存在直接的 LOCATED_AT 关系，若存在返回 EdgeResponse，否则抛 EdgeNotFoundException。
     */
    @Transactional(readOnly = true)
    public EdgeResponse getEdgeBetween(String sourceCityId, String destinationCityId) {
        if (sourceCityId == null || destinationCityId == null) {
            throw new IllegalArgumentException("Both sourceCityId and destinationCityId must be provided.");
        }

        String cypher = ""
                + "MATCH (s:City {cityId: $srcId})-[r:LOCATED_AT]-(d:City {cityId: $dstId}) "
                + "RETURN id(r) AS relId, r.distanceKm AS dKm, r.travelTimeMin AS tMin";

        Map<String,Object> rec = neo4jClient.query(cypher)
                .bind(sourceCityId).to("srcId")
                .bind(destinationCityId).to("dstId")
                .fetch().first()
                .orElseThrow(() -> new EdgeNotFoundException(-1L));

        long relId = ((Number)rec.get("relId")).longValue();
        int dKm = ((Number)rec.get("dKm")).intValue();
        int tMin =((Number)rec.get("tMin")).intValue();

        return new EdgeResponse(relId, sourceCityId, destinationCityId, dKm, tMin);
    }

    /**
     * 根据 routeId 获取 Edge 详情并返回。
     */
    @Transactional(readOnly = true)
    public EdgeResponse getEdgeById(Long routeId) {
        if (!edgeDao.existsById(routeId)) {
            throw new EdgeNotFoundException(routeId);
        }

        // 通过 Neo4jClient 或者 fetch relationship 节点都行。此处用 Cypher：
        String cypher = ""
                + "MATCH ()-[r:LOCATED_AT]-() WHERE id(r) = $rid "
                + "RETURN id(r) AS relId, r.distanceKm AS dKm, r.travelTimeMin AS tMin, "
                + "startNode(r).cityId AS srcId, endNode(r).cityId AS dstId";

        Map<String,Object>rec = neo4jClient.query(cypher)
                .bind(routeId).to("rid")
                .fetch().first()
                .orElseThrow(() -> new EdgeNotFoundException(routeId));

        return getEdgeResponse(rec);
    }

    private EdgeResponse getEdgeResponse(Map<String, Object> rec) {
        long relId = ((Number)rec.get("relId")).longValue();
        int dKm = ((Number)rec.get("dKm")).intValue();
        int tMin = ((Number)rec.get("tMin")).intValue();
        String srcId = (String) rec.get("srcId");
        String dstId = (String) rec.get("dstId");

        return new EdgeResponse(relId, srcId, dstId, dKm, tMin);
    }

    /**
     * 创建一条新的 LOCATED_AT 关系，返回 EdgeResponse。
     */
    @Transactional
    public EdgeResponse createEdge(CreationEdgeRequest request) {
        String srcId = request.getSourceCityId();
        String dstId = request.getDestinationCityId();
        int dKm = request.getDistanceKm();
        int tMin = request.getTravelTimeMin();

        // 检查两个 City 是否存在
        cityDao.findById(srcId)
                .orElseThrow(() -> new CityNotFoundException(srcId));
        cityDao.findById(dstId)
                .orElseThrow(() -> new CityNotFoundException(dstId));

        // Cypher: CREATE 关系并返回 r 的内部 ID
        String cypher = ""
                + "MATCH (s:City {cityId: $srcId}), (d:City {cityId: $dstId}) "
                + "CREATE (s)-[r:LOCATED_AT {distanceKm: $dKm, travelTimeMin: $tMin}]->(d) "
                + "RETURN id(r) AS relId";

        Map<String,Object> rec = neo4jClient.query(cypher)
                .bind(srcId).to("srcId")
                .bind(dstId).to("dstId")
                .bind(dKm).to("dKm")
                .bind(tMin).to("tMin")
                .fetch().first()
                .orElseThrow(() -> new RuntimeException("Failed to create Edge via Cypher."));

        long relId = ((Number)rec.get("relId")).longValue();
        return new EdgeResponse(relId, srcId, dstId, dKm, tMin);
    }

    /**
     * 根据 routeId 更新已有的 LOCATED_AT 关系的 distanceKm / travelTimeMin 中的一个或两个字段。
     * 返回更新后的 EdgeResponse。
     */
    @Transactional
    public EdgeResponse updateEdge(Long routeId, UpdateEdgeRequest request) {
        if (!edgeDao.existsById(routeId)) {
            throw new EdgeNotFoundException(routeId);
        }

        // 动态拼接 SET 子句
        boolean setDistance = request.getDistanceKm() != null;
        boolean setTime = request.getTravelTimeMin() != null;

        StringBuilder cypher = new StringBuilder();
        cypher.append("MATCH ()-[r:LOCATED_AT]-() WHERE id(r) = $rid ");

        if (setDistance && setTime) {
            cypher.append("SET r.distanceKm = $newD, r.travelTimeMin = $newT ");
        } else if (setDistance) {
            cypher.append("SET r.distanceKm = $newD ");
        } else if (setTime) {
            cypher.append("SET r.travelTimeMin = $newT ");
        }
        cypher.append("RETURN id(r) AS relId, r.distanceKm AS dKm, r.travelTimeMin AS tMin, ")
                .append("startNode(r).cityId AS srcId, endNode(r).cityId AS dstId");

        var binder = neo4jClient.query(cypher.toString())
                .bind(routeId).to("rid");
        if (setDistance) {
            binder.bind(request.getDistanceKm()).to("newD");
        }
        if (setTime) {
            binder.bind(request.getTravelTimeMin()).to("newT");
        }

        Map<String,Object> rec = binder.fetch().first()
                .orElseThrow(() -> new EdgeNotFoundException(routeId));

        return getEdgeResponse(rec);
    }

    /**
     * 根据 routeId 删除一条 LOCATED_AT 关系。
     */
    @Transactional
    public void deleteEdge(Long routeId) {
        if (!edgeDao.existsById(routeId)) {
            throw new EdgeNotFoundException(routeId);
        }
        edgeDao.deleteById(routeId);
    }

}