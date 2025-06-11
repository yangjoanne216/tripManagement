package fr.dauphine.miageIf.minh.yang.info_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ActivityUpdateOrCreateDto {
    @NotBlank
    private String name;
    private PoiRefDto pointOfInterest;
    private List<String> photos;
    private List<String> seasons;
    private PriceDto price;
    @Data public static class PoiRefDto { String id,name; }
    @Data public static class PriceDto { double adult,child; }
}
