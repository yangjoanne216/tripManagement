package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.HashSet;
import java.util.Set;

// TODO: change name of this DTO
@Data
public class City {

    private String cityId;
    private String name;
    private Set<Edge> neighbours = new HashSet<>();
}