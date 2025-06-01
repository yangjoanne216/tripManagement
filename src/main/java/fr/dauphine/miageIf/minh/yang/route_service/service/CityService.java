package fr.dauphine.miageIf.minh.yang.route_service.service;

import fr.dauphine.miageIf.minh.yang.route_service.dao.CityDao;
import fr.dauphine.miageIf.minh.yang.route_service.dto.NeighborDto;
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
    public City getCityById(String cityId) {
        return cityDao.findById(cityId)
                .orElseThrow(() -> new CityNotFoundException(cityId));
    }

    /**
     * 更新单个 City（这里只允许更新 name 字段，如果不存在则抛 404）。
     */
    @Transactional
    public City updateCityName(String cityId, String newName) {
        City city = cityDao.findById(cityId)
                .orElseThrow(() -> new CityNotFoundException(cityId));
        city.setName(newName);
        return cityDao.save(city);
    }

    /**
     * 删除单个 City 节点 (同时会删除所有与之相关的 LOCATED_AT 关系)。
     */
    @Transactional
    public void deleteCity(String cityId) {
        if (!cityDao.existsById(cityId)) {
            throw new CityNotFoundException(cityId);
        }
        cityDao.deleteById(cityId);
    }

    /**
     * 查询某 City 在关系 LOCATED_AT 中的所有邻居，且 r.distanceKm <= maxDistanceKm。
     * 返回 List<NeighborDto>。
     */
    @Transactional(readOnly = true)
    public List<NeighborDto> getNeighbors(String cityId, int maxDistanceKm) {
        if (!cityDao.existsById(cityId)) {
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
                    int dKm = record.get("dKm").asInt();
                    int tMin = record.get("tMin").asInt();
                    return new NeighborDto(nid, nname, dKm, tMin); //把每一条查询结果（Record 对象）映射到一个 NeighborDto 实例。
                })
                .all();
    }
}
