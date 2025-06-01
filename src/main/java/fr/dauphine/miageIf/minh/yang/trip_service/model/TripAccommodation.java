package fr.dauphine.miageIf.minh.yang.trip_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_accommodation")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TripAccommodation {
    @EmbeddedId
    private TripAccommodationKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    private Trip trip;

    @Column(name = "accommodation_id", nullable = false)
    private String accommodationId;

}
