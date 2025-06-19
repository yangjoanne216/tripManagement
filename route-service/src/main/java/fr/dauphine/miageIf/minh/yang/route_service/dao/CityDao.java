package fr.dauphine.miageIf.minh.yang.route_service.dao;

import fr.dauphine.miageIf.minh.yang.route_service.model.City;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityDao extends Neo4jRepository<City,Long> {
    /**
     * 根据业务主键 cityId 判断节点是否存在
     */
    boolean existsByCityId(String cityId);
    /**
     * 根据业务主键 cityId 查询节点
     */
    Optional<City> findByCityId(String cityId);

    /**
     * 根据业务主键 cityId 删除节点
     */
    void deleteByCityId(String cityId);

    boolean existsByName(@NotBlank String name);
}
