package fr.dauphine.miageIf.minh.yang.route_service.dao;

import fr.dauphine.miageIf.minh.yang.route_service.model.Edge;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EdgeDao extends Neo4jRepository<Edge,String> {
}
