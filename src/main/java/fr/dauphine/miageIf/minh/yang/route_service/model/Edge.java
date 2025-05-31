package fr.dauphine.miageIf.minh.yang.route_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@AllArgsConstructor
@Builder
@Data
@RelationshipProperties
public class Edge {
    @RelationshipId
    @GeneratedValue
    private Long id;
    private int distanceKm;
    private int travelTimeMin;

    @TargetNode
    private City city;
}
