package fr.dauphine.miageIf.minh.yang.info_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CityUpdateOrCreateDto {
    @NotBlank(message = "name must not be blank")
    private String name;

    @Size(min = 1, message = "photos must contain at least one URL")
    private List<
            @Pattern(
                    regexp = "^https?://.*",
                    message = "each photo URL must start with http:// or https://"
            )
                    String
            > photos;

    @Valid
    private GeoInfo geoInfo;

    @Data
    public static class GeoInfo {
        @DecimalMin(value = "-90.0", inclusive = true, message = "lat must be ≥ -90")
        @DecimalMax(value = "90.0",  inclusive = true, message = "lat must be ≤ 90")
        private double lat;

        @DecimalMin(value = "-180.0", inclusive = true, message = "lon must be ≥ -180")
        @DecimalMax(value = "180.0",  inclusive = true, message = "lon must be ≤ 180")
        private double lon;
    }
}