package fr.dauphine.miageIf.minh.yang.trip_service.controller;

import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripDetail;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripRequestDto;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripSummary;
import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import fr.dauphine.miageIf.minh.yang.trip_service.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/trips")
@Tag(
        name = "trip service API",
        description = "trips endPoints"
)

public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    @Operation(
            summary = "Get all trips",
            description = "Retrieves a list of all events"
    )
    public List<TripSummary> getAllTrips() {
        return tripService.findAll().stream()
                .map(TripSummary::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{tripId}")
    @Operation(
            summary = "Get a trip by its ID",
            description = "Fetches a single trip by it's ID"
    )
    public TripDetail getTrip(@PathVariable Long tripId) {
        return TripDetail.fromEntity(tripService.findByIdWithDetails(tripId));
    }

    @PostMapping
    @Operation(
            summary = "Create a new trip",
            description = "Creates a new trip with the provided details."
    )
    public ResponseEntity<Void> create(@RequestBody TripRequestDto body) {
        Trip trip = tripService.createTrip(body);
        URI loc = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(trip.getId())
                .toUri();
        return ResponseEntity.created(loc).build();
    }

    @PutMapping("/{tripId}")
    @Operation(
            summary = "update an existing trip",
            description = "updates a trip identified by it's ID with new details"
    )
    public ResponseEntity<Void> update(
            @PathVariable Long tripId,
            @RequestBody TripRequestDto body
    ) {
        tripService.updateTrip(tripId, body);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{tripId}")
    @Operation(
            summary = "Delete a trip",
            description = "Deletes a trip based on its ID"
    )
    public ResponseEntity<Void> delete(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId);
        return ResponseEntity.noContent().build();
    }
}
