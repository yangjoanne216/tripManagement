package fr.dauphine.miageIf.minh.yang.route_service.service;

import fr.dauphine.miageIf.minh.yang.route_service.dao.CityDao;
import fr.dauphine.miageIf.minh.yang.route_service.dto.ItineraryResponse;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.CityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import java.util.List;
import java.util.Map;

/**
 * 路径查询（Itinerary）相关的业务逻辑：最短路径 & 所有符合条件的路径。
 */
@Service
@RequiredArgsConstructor
public class ItineraryService {

    private final CityDao cityDao;
    private final Neo4jClient neo4jClient;

    /**
     * 查询 sourceCityId → destinationCityId 的最短路径（hop 最少），
     * 返回一个 ItineraryResponse。如果没有路径，则 cityIds 列表为空。
     */
    @Transactional(readOnly = true)
    public ItineraryResponse getShortestPath(String sourceCityId, String destinationCityId) {
        if (!cityDao.existsById(sourceCityId)) {
            throw new CityNotFoundException(sourceCityId);
        }
        if (!cityDao.existsById(destinationCityId)) {
            throw new CityNotFoundException(destinationCityId);
        }

        String cypher = ""
                + "MATCH (s:City {cityId: $srcId}), (d:City {cityId: $dstId}), "
                + "p = shortestPath((s)-[:LOCATED_AT*]->(d)) "
                + "RETURN [n IN nodes(p) | n.cityId] AS cityList";

        Map<String,Object> rec = neo4jClient.query(cypher)
                .bind(sourceCityId).to("srcId")
                .bind(destinationCityId).to("dstId")
                .fetch().first()
                .orElse(null);

        if (rec == null) {
            // 无可达路径
            return new ItineraryResponse(List.of());
        }

        @SuppressWarnings("unchecked")
        List<String> cityIds = (List<String>) rec.get("cityList");
        return new ItineraryResponse(cityIds);
    }

    /**
     * 查询 sourceCityId → destinationCityId 的所有路径，hop 数 ≤ (maxStops + 1)。
     * 其中 maxStops 表示中转点个数，若 maxStops=0，即 hop ≤ 1（直达）。
     */
    @Transactional(readOnly = true)
    public List<ItineraryResponse> getAllRoutesWithMaxStops(
            String sourceCityId,
            String destinationCityId,
            int maxStops
    ) {
        if (!cityDao.existsById(sourceCityId)) {
            throw new CityNotFoundException(sourceCityId);
        }
        if (!cityDao.existsById(destinationCityId)) {
            throw new CityNotFoundException(destinationCityId);
        }

        int maxHops = maxStops + 1;
        String cypher = String.format(
                "MATCH (s:City {cityId: $srcId}), (d:City {cityId: $dstId}), "
                        + "p = (s)-[:LOCATED_AT*..%d]->(d) "
                        + "RETURN [n IN nodes(p) | n.cityId] AS cityList",
                maxHops
        );

        return (List<ItineraryResponse>) neo4jClient.query(cypher)
                .bind(sourceCityId).to("srcId")
                .bind(destinationCityId).to("dstId")
                .fetchAs(ItineraryResponse.class)
                .mappedBy((typeSystem, record) -> {
                    List<String> cityIds = record.get("cityList").asList(Value::asString);
                    return new ItineraryResponse(cityIds);
                })
                .all();
    }
}