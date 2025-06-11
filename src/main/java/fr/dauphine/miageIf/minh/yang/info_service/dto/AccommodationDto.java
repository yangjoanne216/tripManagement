package fr.dauphine.miageIf.minh.yang.info_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AccommodationDto {
    private String id;
    @NotBlank
    private String name;
    private String address;
    private List<String> photos;
    private CityRefDto city;
    private double price;
    private boolean available;
    @Data
    public static class CityRefDto { String id,name; }
}
