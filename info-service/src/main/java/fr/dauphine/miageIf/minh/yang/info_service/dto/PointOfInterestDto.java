package fr.dauphine.miageIf.minh.yang.info_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;


@Data
public class PointOfInterestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank
    private String name;

    private List<String> photos;
    private String address;

    @NotBlank
    private String cityId;

    private GeoInfoDto geoInfo;

    @Data
    public static class GeoInfoDto {
        private double lat;
        private double lon;
    }
}
