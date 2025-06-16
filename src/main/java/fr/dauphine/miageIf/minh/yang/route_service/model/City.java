package fr.dauphine.miageIf.minh.yang.route_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.util.HashSet;
import java.util.Set;



@Node
@Builder
@Data                            //生成 getter/setter/toString
@NoArgsConstructor               // 生成无参构造
@AllArgsConstructor              // 生成全参构造（cityId, name, neighbours）
//@EqualsAndHashCode(exclude = {"outgoingNeighbours", "incomingNeighbours"})  //Lombok 在生成 equals()/hashCode() 时会排除 outgoing/incoming 两个字段，
public class City {
    @Id
    @GeneratedValue              // ← 用 internal id
    @JsonIgnore                      // ← 不把它序列化到外面，免得和 cityId 混淆
    private Long id;

    @Property("cityId")          // ← 改成普通字段
    private String cityId;

    private String name;
    //@Relationship(type = "LOCATED_AT")
    //private Set<Edge> neighbours = new HashSet<>();

    public City(String cityId, String name) {
        this.cityId = cityId;
        this.name = name;
    }

    /**
     * this → other
     */
  /*  @JsonIgnore
    @Relationship(type = "LOCATED_AT", direction = Relationship.Direction.OUTGOING)
    private Set<Edge> outgoingNeighbours = new HashSet<>();*/

    /**
     * other → this
     */
/*    @JsonIgnore
    @Relationship(type = "LOCATED_AT", direction = Relationship.Direction.INCOMING)
    private Set<Edge> incomingNeighbours = new HashSet<>();*/



    /**
     * 新增一个“虚拟” getter，将 incoming 和 outgoing 合并为一个集合，
     * 并标记为 @JsonProperty("neighbours")，替代原先序列化时的 neighbours。
     */
/*    @Transient
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
       return Stream.concat(
                        (outgoingNeighbours != null ? outgoingNeighbours.stream() : Stream.empty()),
                        (incomingNeighbours != null ? incomingNeighbours.stream() : Stream.empty())
                )
                .collect(Collectors.toSet());
    }*/
}
