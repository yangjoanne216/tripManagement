package fr.dauphine.miageIf.minh.yang.info_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ActivityDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank
    private String name;

    private List<String> photos;
    private List<String> seasons;

    private PriceDto price;

    @NotBlank
    private String pointOfInterestId;

    @Data
    public static class PriceDto {
        private double adult;
        private double child;
    }
}