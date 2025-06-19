package fr.dauphine.miageIf.minh.yang.info_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AccommodationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank
    private String name;

    private String address;
    private List<String> photos;

    @NotBlank
    private String cityId;

    private double price;
    private boolean available;
}

