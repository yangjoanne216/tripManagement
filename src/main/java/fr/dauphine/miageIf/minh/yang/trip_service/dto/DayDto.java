package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import java.time.LocalDate;
import java.util.List;

public class DayDto {
    private Integer day;
    private String accommodationId;
    private List<ActivityDto> activities;

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(String accommodationId) {
        this.accommodationId = accommodationId;
    }

    public List<ActivityDto> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDto> activities) {
        this.activities = activities;
    }
}
