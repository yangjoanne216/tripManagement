package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterestDto {
    private String id;

    @NotBlank(message = "name must not be blank")
    private String name;

    private List<String> photos;
    private String address;
    private CityDto.PointOfInterestRefDto city;
    private CityDto.GeoInfoDto geoInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoiRefDto {
        private String id;
        private String name;
    }
}
