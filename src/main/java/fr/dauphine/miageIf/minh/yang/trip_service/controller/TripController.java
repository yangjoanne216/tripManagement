package fr.dauphine.miageIf.minh.yang.trip_service.controller;

import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripDetail;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripSummary;
import fr.dauphine.miageIf.minh.yang.trip_service.service.TripService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public List<TripSummary> getAllTrips() {
        return tripService.findAll().stream()
                .map(TripSummary::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{tripId}")
    public TripDetail getTrip(@PathVariable Long tripId) {
        return TripDetail.fromEntity(tripService.findByIdWithDetails(tripId));
    }
}
