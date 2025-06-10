package fr.dauphine.miageIf.minh.yang.info_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CityDto {
    private String id;
    @NotBlank(message = "name must not be blank")
    @Size(max = 200)
    private String name;
    private GeoInfoDto geoInfo;
    private List<String> photos;
    private List<AccommodationRefDto> accommodations;
    private List<PointOfInterestRefDto> pointsOfInterest;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoInfoDto { double lat, lon; }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccommodationRefDto { String id, name; double price; boolean available; }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointOfInterestRefDto { String id, name; }
}

