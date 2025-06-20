package fr.dauphine.miageIf.minh.yang.route_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@AllArgsConstructor
@Builder
@Data
@RelationshipProperties
@EqualsAndHashCode(exclude = "city")  //确保 Edge.hashCode() 时不会去访问 city.hashCode()
public class Edge {
    @RelationshipId
    @GeneratedValue
    private Long id;
    private double distanceKm;
    private int travelTimeMin;

    @TargetNode
    @JsonIgnoreProperties("neighbours")
    private City city;
}
