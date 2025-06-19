package fr.dauphine.miageIf.minh.yang.trip_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "trip_day")
@AllArgsConstructor
@NoArgsConstructor
public class TripDay {

    @EmbeddedId
    private TripDayKey id;

    @ManyToOne
    @MapsId("tripId")
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(name = "city_id", nullable = false, length = 24)
    private String cityId;

}
