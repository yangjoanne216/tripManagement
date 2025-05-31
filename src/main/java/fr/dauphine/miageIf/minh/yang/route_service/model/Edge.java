package fr.dauphine.miageIf.minh.yang.route_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // 只包含 getId() 参与 equals/hashCode
@ToString(onlyExplicitlyIncluded = true)          // 只包含 getId()、getDistanceKm()、getTravelTimeMin() 参与 toString
@RelationshipProperties
public class Edge {
    @RelationshipId
    @GeneratedValue
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ToString.Include
    private int distanceKm;

    @ToString.Include
    private int travelTimeMin;

    /**
     * 指向的 city 节点。
     * 为了防止无限递归，把 city 本身的 neighbours 等都忽略掉。
     */
    @TargetNode
    @JsonIgnoreProperties({ "outgoingNeighbours", "incomingNeighbours" })
    private City city;
}
