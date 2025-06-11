package fr.dauphine.miageIf.minh.yang.info_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class PointOfInterestUpdateOrCreateDto {
    @NotBlank
    private String name;
    private List<String> photos;
    private String address;
    private CityRefDto city;
    private GeoInfoDto geoInfo;
    @Data
    public static class CityRefDto { String id, name; }
    @Data public static class GeoInfoDto { double lat, lon; }
}
