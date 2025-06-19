package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {
    private String id;

    @NotBlank(message = "name must not be blank")
    @Size(max = 200, message = "name length must not exceed 200 characters")
    private String name;

    private GeoInfoDto geoInfo;
    private List<String> photos;
    private List<AccommodationRefDto> accommodations;
    private List<PointOfInterestRefDto> pointsOfInterest;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoInfoDto {
        private double lat;
        private double lon;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccommodationRefDto {
        private String id;
        private String name;
        private double price;
        private boolean available;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointOfInterestRefDto {
        private String id;
        private String name;
    }
}