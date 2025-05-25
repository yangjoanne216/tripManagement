package fr.dauphine.miageIf.minh.yang.route_service.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
@Builder
@Data
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
}
