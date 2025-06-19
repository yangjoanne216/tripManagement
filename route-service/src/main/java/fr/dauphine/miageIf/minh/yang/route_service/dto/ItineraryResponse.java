package fr.dauphine.miageIf.minh.yang.route_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ItineraryResponse {
    private List<String> cityIds;
}
