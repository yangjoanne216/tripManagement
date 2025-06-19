package fr.dauphine.miageIf.minh.yang.trip_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_activity")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TripActivity {
    @EmbeddedId
    private TripActivityKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    private Trip trip;

    @Column(name = "activity_id", nullable = false)
    private String activityId;

    public TripActivity(TripActivityKey tripActivityKey, String id) {
        this.id = tripActivityKey;
        this.activityId = id;
    }

    public TripActivityKey getId() {
        return id;
    }

    public void setId(TripActivityKey id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
}
