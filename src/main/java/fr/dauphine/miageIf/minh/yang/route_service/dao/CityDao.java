package fr.dauphine.miageIf.minh.yang.route_service.dao;

import fr.dauphine.miageIf.minh.yang.route_service.model.City;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CityDao extends Neo4jRepository<City,String> {
}
