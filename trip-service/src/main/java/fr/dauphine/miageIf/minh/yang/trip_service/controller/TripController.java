package fr.dauphine.miageIf.minh.yang.trip_service.controller;

import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripDetail;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripPointsOfInterestDto;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripRequestDto;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripSummary;
import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import fr.dauphine.miageIf.minh.yang.trip_service.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/trips")
@Tag(
        name = "Trip Service API",
        description = "Endpoints for creating, retrieving, updating, deleting and searching trips"
)
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @Operation(
            summary = "Create a new trip",
            description = "Creates a new trip from city/accommodation/activity names. Automatically generates day numbers and deduces start/end cities."
    )
    @PostMapping
    public ResponseEntity<TripDetail> createTrip(
            @Valid @RequestBody TripRequestDto tripRequestDto
    ) {
        TripDetail created = tripService.createTrip(tripRequestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an existing trip",
            description = "Updates the trip identified by tripId. Automatically rechecks location consistency and updates computed fields."
    )
    @PutMapping("/{tripId}")
    public ResponseEntity<TripDetail> updateTrip(
            @PathVariable Long tripId,
            @Valid @RequestBody TripRequestDto tripRequestDto
    ) {
        TripDetail updated = tripService.updateTrip(tripId, tripRequestDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete a trip",
            description = "Deletes the trip identified by tripId. Returns HTTP 204 No Content on success."
    )
    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get a trip's full itinerary by ID",
            description = "Retrieves a trip by ID, including days, accommodations, activities and computed travel segments."
    )
    @GetMapping("/{tripId}")
    public ResponseEntity<TripDetail> getTrip(@PathVariable Long tripId) {
        TripDetail detail = tripService.getTrip(tripId);
        return ResponseEntity.ok(detail);
    }

    @Operation(
            summary = "Search trips",
            description = "Search for trips filtered by optional parameters:  minDays, maxDays."
    )
    @GetMapping
    public ResponseEntity<List<TripSummary>> searchTrips(
            @RequestParam(required = false) Integer minDays,
            @RequestParam(required = false) Integer maxDays
    ) {
        List<TripSummary> results = tripService.searchTrips(minDays, maxDays);
        return ResponseEntity.ok(results);
    }

    @Operation(
            summary = "Get points of interest/activities/accommodations for a trip",
            description = "Returns all involved POI names, activity names and accommodation names in a trip."
    )
    @GetMapping("/{tripId}/POI_activity_accommodation")
    public ResponseEntity<TripPointsOfInterestDto> getPointsOfInterest(
            @PathVariable Long tripId
    ) {
        TripPointsOfInterestDto poi = tripService.getPointsOfInterest(tripId);
        return ResponseEntity.ok(poi);
    }
}

