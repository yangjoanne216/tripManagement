package fr.dauphine.miageIf.minh.yang.trip_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TripDayKey implements Serializable {
    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "day")
    private Integer day;
}
