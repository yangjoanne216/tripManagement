package fr.dauphine.miageIf.minh.yang.info_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CityDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank(message = "name must not be blank")
    @Size(max = 200)
    private String name;

    private List<String> photos;
    private GeoInfoDto geoInfo;

    @Data
    public static class GeoInfoDto {
        private double lat;
        private double lon;
    }
}


