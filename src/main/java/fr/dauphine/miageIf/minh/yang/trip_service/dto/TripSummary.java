package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;

import java.time.LocalDate;

public class TripSummary {
    public Long id;
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;

    public static TripSummary fromEntity(Trip t) {
        TripSummary dto = new TripSummary();
        dto.id = t.getId();
        dto.name = t.getName();
        dto.startDate = t.getStartDate();
        dto.endDate = t.getEndDate();
        return dto;
    }
}
