package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TripDetail {
    public Long id;
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;
    public Map<Integer, List<Activity>> dailyActivities;
    public Map<Integer, String> dailyAccommodations;

    public static class Activity {
        public Integer sequence;
        public String activityId;
    }

    public static TripDetail fromEntity(Trip t) {
        TripDetail dto = new TripDetail();
        dto.id = t.getId();
        dto.name = t.getName();
        dto.startDate = t.getStartDate();
        dto.endDate = t.getEndDate();

        dto.dailyActivities = t.getActivities().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getId().getDay(),
                        Collectors.mapping(a -> {
                            Activity act = new Activity();
                            act.sequence = a.getId().getSequence();
                            act.activityId = a.getActivityId();
                            return act;
                        }, Collectors.toList())
                ));

        dto.dailyAccommodations = t.getAccommodations().stream()
                .collect(Collectors.toMap(
                        a -> a.getId().getDay(),
                        TripAccommodation::getAccommodationId
                ));

        return dto;
    }
}

