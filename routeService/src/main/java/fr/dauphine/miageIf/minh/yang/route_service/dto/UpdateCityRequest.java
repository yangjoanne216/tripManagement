package fr.dauphine.miageIf.minh.yang.route_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCityRequest {
    private String name;
    private Double latitude;
    private Double longitude;
}
