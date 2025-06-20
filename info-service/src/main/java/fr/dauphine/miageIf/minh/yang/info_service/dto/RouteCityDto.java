package fr.dauphine.miageIf.minh.yang.info_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteCityDto {
    private String cityId;
    private String name;
    private double latitude;
    private double longitude;
}