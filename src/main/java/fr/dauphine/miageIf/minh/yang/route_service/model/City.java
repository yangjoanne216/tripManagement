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

    private double latitude;
    private double longitude;

    public City(String cityId, String name) {
        this.cityId = cityId;
        this.name = name;
    }

}
