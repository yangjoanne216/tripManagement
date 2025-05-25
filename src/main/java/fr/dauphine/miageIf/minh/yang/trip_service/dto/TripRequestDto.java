package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import java.time.LocalDate;
import java.util.List;

public class TripRequestDto {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DayDto> days;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<DayDto> getDays() {
        return days;
    }

    public void setDays(List<DayDto> days) {
        this.days = days;
    }
}
