package fr.dauphine.miageIf.minh.yang.trip_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trip_accommodation")
public class TripAccommodation {
    @EmbeddedId
    private TripAccommodationKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    private Trip trip;

    @Column(name = "accommodation_id", nullable = false)
    private String accommodationId;

    public TripAccommodationKey getId() {
        return id;
    }

    public void setId(TripAccommodationKey id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(String accommodationId) {
        this.accommodationId = accommodationId;
    }
}
