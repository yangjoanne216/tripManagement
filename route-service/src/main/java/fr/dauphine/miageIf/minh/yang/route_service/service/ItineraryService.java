package fr.dauphine.miageIf.minh.yang.route_service.service;

import fr.dauphine.miageIf.minh.yang.route_service.dao.CityDao;
import fr.dauphine.miageIf.minh.yang.route_service.dto.ItineraryResponse;
import fr.dauphine.miageIf.minh.yang.route_service.dto.RouteSummaryResponse;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.CityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        if (!cityDao.existsByCityId(sourceCityId)) {
            throw new CityNotFoundException(sourceCityId);
        }
        if (!cityDao.existsByCityId(destinationCityId)) {
            throw new CityNotFoundException(destinationCityId);
        }

        String cypher = ""
                + "MATCH (s:City {cityId: $srcId}), (d:City {cityId: $dstId}), "
                + "p = shortestPath((s)-[:LOCATED_AT*]-(d)) "
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
        if (!cityDao.existsByCityId(sourceCityId)) {
            throw new CityNotFoundException(sourceCityId);
        }
        if (!cityDao.existsByCityId(destinationCityId)) {
            throw new CityNotFoundException(destinationCityId);
        }

        int maxHops = maxStops + 1;
        String cypher = String.format(
                "MATCH (s:City {cityId: $srcId}), (d:City {cityId: $dstId}), "
                        + "p = (s)-[:LOCATED_AT*..%d]-(d) "
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

    /**
     * 计算 source → destination 最短路径（最少 hops）上所有关系的
     * distanceKm 总和和 travelTimeMin 总和。
     * 逻辑：
     *   1) source == destination → {0.0, 0}
     *   2) path 不存在 → {99999.0, 99999}
     *   3) 否则 → sums
     */
    @Transactional(readOnly = true)
    public RouteSummaryResponse getRouteSummary(String sourceCityId, String destinationCityId) {
        // 1) 自身相等
        if (sourceCityId.equals(destinationCityId)) {
            return new RouteSummaryResponse(0.0, 0);
        }
        // 2) 验证存在
        if (!cityDao.existsByCityId(sourceCityId)) {
            throw new CityNotFoundException(sourceCityId);
        }
        if (!cityDao.existsByCityId(destinationCityId)) {
            throw new CityNotFoundException(destinationCityId);
        }

        // 3) 用 CASE+reduce 明确区分 “无路径” / “有路径” 两种情况
        String cypher = """
            MATCH (s:City {cityId: $src}), (d:City {cityId: $dst})
            OPTIONAL MATCH p = shortestPath((s)-[:LOCATED_AT*]-(d))
            RETURN
              CASE
                WHEN p IS NULL
                THEN 99999.0
                ELSE reduce(acc = 0.0, r IN relationships(p) | acc + r.distanceKm)
              END AS distanceSum,
              CASE
                WHEN p IS NULL
                THEN 99999
                ELSE reduce(acc = 0, r IN relationships(p) | acc + r.travelTimeMin)
              END AS timeSum
            """;

        Map<String,Object> rec = neo4jClient.query(cypher)
                .bind(sourceCityId).to("src")
                .bind(destinationCityId).to("dst")
                .fetch().first()
                .orElseThrow();  // 理论上不会抛，因为 MATCH s,d 一定有记录

        double dist = ((Number) rec.get("distanceSum")).doubleValue();
        int    tmin = ((Number) rec.get("timeSum")).intValue();
        return new RouteSummaryResponse(dist, tmin);
    }
}



