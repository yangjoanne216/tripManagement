package fr.dauphine.miageIf.minh.yang.route_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

@AllArgsConstructor
@Builder
@Data
@RelationshipProperties
public class Edge {
    @RelationshipId @GeneratedValue
    private Long id;
    private int distanceKm;
    private int travelTimeMin;
    @TargetNode
    private City city;
}
