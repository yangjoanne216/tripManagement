package fr.dauphine.miageIf.minh.yang.route_service.service;

import fr.dauphine.miageIf.minh.yang.route_service.dao.CityDao;
import fr.dauphine.miageIf.minh.yang.route_service.dto.NeighborDto;
import fr.dauphine.miageIf.minh.yang.route_service.dto.UpdateCityRequest;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.CityAlreadyExistsException;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.CityNotFoundException;
import fr.dauphine.miageIf.minh.yang.route_service.model.City;
import fr.dauphine.miageIf.minh.yang.route_service.dto.CreationCityRequest;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * City 节点的增删改查，以及“查邻居”逻辑都在这里实现。
 */
@Service
@RequiredArgsConstructor
public class CityService {

    private final CityDao cityDao;
    private final Neo4jClient neo4jClient;

    /**
     * 创建一个新的 City 节点。如果已存在同样 cityId，则会直接覆盖（可根据业务需求调整为抛出 409）。
     */
    @Transactional
    public City createCity(CreationCityRequest request) {
        if (cityDao.existsByCityId(request.getCityId())) {
            throw new CityAlreadyExistsException(
                    "City with id '" + request.getCityId() + "' already exists");
        }
        if (cityDao.existsByName(request.getName())) {
            throw new CityAlreadyExistsException(
                    "City with name '" + request.getName() + "' already exists");
        }
        City city = new City(request.getCityId(), request.getName());
        return cityDao.save(city);
    }

    /**
     * 查询所有 City 节点，返回一个 List<City>。
     */
    @Transactional(readOnly = true)
    public List<City> getAllCities() {
        return cityDao.findAll()
                .stream()
                .toList();
    }

    /**
     * 根据 cityId 查询单个 City。不存在则抛 CityNotFoundException。
     */
    @Transactional(readOnly = true)
    public City getCityByCityId(String cityId) {
        return cityDao.findByCityId(cityId)
                .orElseThrow(() -> new CityNotFoundException(cityId));
    }

    /**
     * 更新 name/latitude/longitude，任一字段不为 null 则更新。
     * 更新后重新计算所有和此 city 相连的 LOCATED_AT 距离属性。
     */
    @Transactional
    public City updateCity(String cityId, UpdateCityRequest req) {
        City city = cityDao.findByCityId(cityId)
                .orElseThrow(() -> new CityNotFoundException(cityId));

        if (req.getName() != null) {
            city.setName(req.getName());
        }
        if (req.getLatitude() != null) {
            city.setLatitude(req.getLatitude());
        }
        if (req.getLongitude() != null) {
            city.setLongitude(req.getLongitude());
        }
        city = cityDao.save(city);

        // 重新计算所有相关边的属性
        String cypher = ""
                + "MATCH (c:City {cityId:$cid})-[r:LOCATED_AT]-(n:City)\n"
                + "WITH c, n,\n"
                + "     point({latitude:c.latitude, longitude:c.longitude}) AS p1,\n"
                + "     point({latitude:n.latitude, longitude:n.longitude}) AS p2,\n"
                + "     r\n"
                + "WITH r,\n"
                + "     round(point.distance(p1,p2) / 1000.0, 1) AS dKm,\n"
                + "     toInteger(round((point.distance(p1,p2) / 1000.0) / 80 * 60, 0)) AS tMin\n"
                + "SET r.distanceKm = dKm,\n"
                + "    r.travelTimeMin = tMin";

        neo4jClient.query(cypher)
                .bind(cityId).to("cid")
                .run();

        return city;
    }

    /**
     * 更新单个 City（这里只允许更新 name 字段，如果不存在则抛 404）。
     
    @Transactional
    public City updateCityName(String cityId, String newName) {
        City city = cityDao.findByCityId(cityId)
                .orElseThrow(() -> new CityNotFoundException(cityId));
        city.setName(newName);
        return cityDao.save(city);
    }*/

    /**
     * 删除单个 City 节点 (同时会删除所有与之相关的 LOCATED_AT 关系)。
     */
    @Transactional
    public void deleteCity(String cityId) {
        if (!cityDao.existsByCityId(cityId)) {
            throw new CityNotFoundException(cityId);
        }
        // DETACH DELETE 会自动删除所有关系
        cityDao.deleteByCityId(cityId);
    }

    /**
     * 查询某 City 在关系 LOCATED_AT 中的所有邻居，且 r.distanceKm <= maxDistanceKm。
     * 返回 List<NeighborDto>。
     */
    @Transactional(readOnly = true)
    public List<NeighborDto> getNeighbors(String cityId, int maxDistanceKm) {
        if (!cityDao.existsByCityId(cityId)) {
            throw new CityNotFoundException(cityId);
        }
        // 自定义 Cypher 查询：MATCH (c)-[r:LOCATED_AT]- (n) WHERE r.distanceKm <= maxDistanceKm RETURN n, r
        String cypher = ""
                + "MATCH (c:City {cityId: $cityIdParam})-[r:LOCATED_AT]-(n:City) "
                + "WHERE r.distanceKm <= $maxDistanceParam "
                + "RETURN n.cityId AS neighborId, n.name AS neighborName, r.distanceKm AS dKm, r.travelTimeMin AS tMin";

        return (List<NeighborDto>) neo4jClient.query(cypher)
                .bind(cityId).to("cityIdParam")
                .bind(maxDistanceKm).to("maxDistanceParam")
                .fetchAs(NeighborDto.class) //确定返回的类型
                .mappedBy((typeSystem, record) -> {
                    String nid = record.get("neighborId").asString();
                    String nname = record.get("neighborName").asString();
                    double dKm = record.get("dKm").asDouble();
                    int tMin = record.get("tMin").asInt();
                    return new NeighborDto(nid, nname, dKm, tMin); //把每一条查询结果（Record 对象）映射到一个 NeighborDto 实例。
                })
                .all();
    }
}
