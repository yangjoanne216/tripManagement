package fr.dauphine.miageIf.minh.yang.route_service.service;

import fr.dauphine.miageIf.minh.yang.route_service.dao.CityDao;
import fr.dauphine.miageIf.minh.yang.route_service.dao.EdgeDao;
import fr.dauphine.miageIf.minh.yang.route_service.dto.CreationEdgeRequest;
import fr.dauphine.miageIf.minh.yang.route_service.dto.EdgeResponse;
import fr.dauphine.miageIf.minh.yang.route_service.dto.UpdateEdgeRequest;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.CityNotFoundException;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.EdgeNotFoundException;
import fr.dauphine.miageIf.minh.yang.route_service.model.City;
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
                    double dKm = record.get("dKm").asDouble();
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
        // 1. （可选）先检查 edgeDao.existsByCityId(routeId)，如果不存在直接 404
        //    也可以不检查，直接让下面的 fetch().first() 抛 404。这里我保留检查，效率也不错。
        if (!edgeDao.existsById(routeId)) {
            throw new EdgeNotFoundException(routeId);
        }

        // 2. 下面的 Cypher 一定要写成“无向匹配”且节点两侧都用 ()，不能写成 ( )：
        String cypher = ""
                + "MATCH ()-[r:LOCATED_AT]-() "         // ← 必须写 ()，而不是 ( )
                + "WHERE id(r) = $rid "
                + "RETURN "
                + "  id(r) AS routeId, "
                + "  startNode(r).cityId AS sourceCityId, "
                + "  endNode(r).cityId AS destinationCityId, "
                + "  r.distanceKm AS distanceKm, "
                + "  r.travelTimeMin AS travelTimeMin";

        // 3. 直接把查询结果拿成 Map<String,Object>：
        Map<String, Object> rec = neo4jClient.query(cypher)
                .bind(routeId).to("rid")
                .fetch()
                .first()
                .orElseThrow(() -> new EdgeNotFoundException(routeId));

        // 4. 从 Map 中取值——注意类型转换：
        long id = ((Number) rec.get("routeId")).longValue();
        String src = (String) rec.get("sourceCityId");
        String dst = (String) rec.get("destinationCityId");
        int dKm  = ((Number) rec.get("distanceKm")).intValue();
        int tMin = ((Number) rec.get("travelTimeMin")).intValue();

        return new EdgeResponse(id, src, dst, dKm, tMin);
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
    /**
     * 只传 sourceCityName、destinationCityName，Neo4j 端计算 distanceKm 和 travelTimeMin
     */
    @Transactional
    public EdgeResponse createEdge(CreationEdgeRequest req) {
        // 检查城市存在
        cityDao.findByCityId(req.getSourceCityId())
                .orElseThrow(() -> new CityNotFoundException(req.getSourceCityId()));
        cityDao.findByCityId(req.getDestinationCityId())
                .orElseThrow(() -> new CityNotFoundException(req.getDestinationCityId()));

        String cypher = ""
                + "MATCH (s:City {cityId:$src}), (d:City {cityId:$dst})\n"
                + "WITH s, d,\n"
                + "     point({latitude: s.latitude,  longitude: s.longitude}) AS p1,\n"
                + "     point({latitude: d.latitude,  longitude: d.longitude}) AS p2\n"
                + "CREATE (s)-[r:LOCATED_AT {\n"
                + "    distanceKm:    round( point.distance(p1,p2) / 1000.0, 1),\n"
                + "    travelTimeMin: toInteger(round((point.distance(p1,p2) / 1000.0) / 80 * 60, 0))\n"
                + "}]->(d)\n"
                + "RETURN id(r) AS routeId, r.distanceKm AS dKm, r.travelTimeMin AS tMin";

        var rec = neo4jClient.query(cypher)
                .bind(req.getSourceCityId()).to("src")
                .bind(req.getDestinationCityId()).to("dst")
                .fetch().first()
                .orElseThrow(() -> new RuntimeException("failed"));

        long id     = ((Number)rec.get("routeId")).longValue();
        double dKm  = ((Number) rec.get("dKm")).doubleValue();
        int tMin    = ((Number) rec.get("tMin")).intValue();
        return new EdgeResponse(id, req.getSourceCityId(), req.getDestinationCityId(), dKm, tMin);
    }

    /**
     * 用 “sourceCityId + destinationCityId” 组合来更新边：
     * 若 (s)-[r:LOCATED_AT]-(d) 不存在，抛 404；否则更新 distanceKm/travelTimeMin。
     */
    @Transactional
    public EdgeResponse updateEdgeByCities(String sourceCityId,
                                           String destinationCityId,
                                           UpdateEdgeRequest request) {
        if (!cityDao.existsByCityId(sourceCityId)) {
            throw new CityNotFoundException(sourceCityId);
        }
        if (!cityDao.existsByCityId(destinationCityId)) {
            throw new CityNotFoundException(destinationCityId);
        }

        boolean setDistance = request.getDistanceKm() != null;
        boolean setTime = request.getTravelTimeMin() != null;
        if (!setDistance && !setTime) {
            throw new IllegalArgumentException("At least one of distanceKm or travelTimeMin must be provided.");
        }

        // 无向匹配
        StringBuilder cypher = new StringBuilder();
        cypher.append("MATCH (s:City {cityId: $srcId})-[r:LOCATED_AT]-(d:City {cityId: $dstId}) ");

        // 动态拼 SET
        if (setDistance && setTime) {
            cypher.append("SET r.distanceKm = $newD, r.travelTimeMin = $newT ");
        } else if (setDistance) {
            cypher.append("SET r.distanceKm = $newD ");
        } else {
            cypher.append("SET r.travelTimeMin = $newT ");
        }

        cypher.append("RETURN id(r) AS routeId, r.distanceKm AS dKm, r.travelTimeMin AS tMin, ")
                .append("startNode(r).cityId AS srcId, endNode(r).cityId AS dstId");

        var binder = neo4jClient.query(cypher.toString())
                .bind(sourceCityId).to("srcId")
                .bind(destinationCityId).to("dstId");
        if (setDistance) { binder.bind(request.getDistanceKm()).to("newD"); }
        if (setTime)    { binder.bind(request.getTravelTimeMin()).to("newT"); }

        Map<String, Object> rec = binder.fetch()
                .first()
                .orElseThrow(() -> new EdgeNotFoundException(
                        sourceCityId, destinationCityId));

        long id = ((Number) rec.get("routeId")).longValue();
        String src = (String) rec.get("srcId");
        String dst = (String) rec.get("dstId");
        int dKm = ((Number) rec.get("dKm")).intValue();
        int tMin = ((Number) rec.get("tMin")).intValue();
        return new EdgeResponse(id, src, dst, dKm, tMin);
    }


    /**
     * 用 “sourceCityId + destinationCityId” 组合来删除边：
     * 若 (s)-[r:LOCATED_AT]-(d) 不存在，则抛 404；否则删除并返回 void。
     */
    @Transactional
    public void deleteEdgeByCities(String sourceCityId, String destinationCityId) {
        if (!cityDao.existsByCityId(sourceCityId)) {
            throw new CityNotFoundException(sourceCityId);
        }
        if (!cityDao.existsByCityId(destinationCityId)) {
            throw new CityNotFoundException(destinationCityId);
        }

        // 先检查这条边是否存在
        String matchCypher = "MATCH (s:City {cityId: $srcId})-[r:LOCATED_AT]-(d:City {cityId: $dstId}) "
                + "RETURN id(r) AS routeId";
        Long foundId = (Long) neo4jClient.query(matchCypher)
                .bind(sourceCityId).to("srcId")
                .bind(destinationCityId).to("dstId")
                .fetch()
                .first()
                .map(record -> record.get("routeId"))
                .orElse(null);

        if (foundId == null) {
            throw new EdgeNotFoundException(sourceCityId,destinationCityId);
        }

        // 真正删除
        String deleteCypher = "MATCH (s:City {cityId: $srcId})-[r:LOCATED_AT]-(d:City {cityId: $dstId}) "
                + "DELETE r";
        neo4jClient.query(deleteCypher)
                .bind(sourceCityId).to("srcId")
                .bind(destinationCityId).to("dstId")
                .run();
    }

}