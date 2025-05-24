package fr.dauphine.miageIf.minh.yang.trip_service.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class TripAccommodationKey implements Serializable {
    private Long tripId;
    private Integer day;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}
