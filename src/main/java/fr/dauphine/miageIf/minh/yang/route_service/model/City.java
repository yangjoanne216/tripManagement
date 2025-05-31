package fr.dauphine.miageIf.minh.yang.route_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Node
@Builder
@Data
@NoArgsConstructor               // 生成无参构造
@AllArgsConstructor              // 生成全参构造（cityId, name, neighbours）
public class City {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private String cityId;
    private String name;
    @Relationship(type = "LOCATED_AT")
    private Set<Edge> neighbours = new HashSet<>();

    public City(String cityId, String name) {
        this.cityId = cityId;
        this.name = name;
    }

    /**
     * this → other
     */
    @JsonIgnore
    @Relationship(type = "LOCATED_AT", direction = Relationship.Direction.OUTGOING)
    private Set<Edge> outgoingNeighbours = new HashSet<>();

    /**
     * other → this
     */
    @JsonIgnore
    @Relationship(type = "LOCATED_AT", direction = Relationship.Direction.INCOMING)
    private Set<Edge> incomingNeighbours = new HashSet<>();



    /**
     * 新增一个“虚拟” getter，将 incoming 和 outgoing 合并为一个集合，
     * 并标记为 @JsonProperty("neighbours")，替代原先序列化时的 neighbours。
     */
    @Transient
    @JsonProperty("neighbours")
    public Set<Edge> getNeighbours() {
       Set<Edge> all = new HashSet<>();
        if (outgoingNeighbours != null) {
            all.addAll(outgoingNeighbours);
        }
        if (incomingNeighbours != null) {
            incomingNeighbours.forEach(edge -> {
                City otherCity = edge.getCity();
                Edge reversed = new Edge(
                        edge.getId(),
                        edge.getDistanceKm(),
                        edge.getTravelTimeMin(),
                        new City(otherCity.getCityId(), otherCity.getName())
                );
                all.add(reversed);
            });
        }
        return all;
         /* return Stream.concat(
                        (outgoingNeighbours != null ? outgoingNeighbours.stream() : Stream.empty()),
                        (incomingNeighbours != null ? incomingNeighbours.stream() : Stream.empty())
                )
                .collect(Collectors.toSet());*/
    }
}
