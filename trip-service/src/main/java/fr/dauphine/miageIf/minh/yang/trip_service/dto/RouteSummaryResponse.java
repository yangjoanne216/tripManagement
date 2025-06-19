package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteSummaryResponse {
    private double distanceKm;
    private int travelTimeMin;
}
