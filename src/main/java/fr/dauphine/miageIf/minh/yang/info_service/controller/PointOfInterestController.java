package fr.dauphine.miageIf.minh.yang.info_service.controller;

import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.service.PointOfInterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points-of-interest")
@Tag(name = "Point of Interest API", description = "Endpoints for managing points of interest")
@RequiredArgsConstructor
public class PointOfInterestController {

    private final PointOfInterestService service;

    @Operation(summary = "Create a new point of interest",
            description = "Creates a new POI document in MongoDB and returns the created PointOfInterestDto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "POI successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body")
    })
    @PostMapping
    public ResponseEntity<PointOfInterestDto> create(
            @Parameter(description = "PointOfInterestDto containing name, photos, address, geoInfo, and city ref", required = true)
            @Valid @RequestBody PointOfInterestDto dto
    ) {
        PointOfInterestDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing point of interest",
            description = "Updates the POI identified by the given ID with the provided data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "POI successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PointOfInterestDto> update(
            @Parameter(description = "ID of the POI to update", required = true) @PathVariable String id,
            @Parameter(description = "PointOfInterestDto containing updated fields", required = true) @Valid @RequestBody PointOfInterestDto dto
    ) {
        PointOfInterestDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a point of interest",
            description = "Deletes the POI identified by the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "POI successfully deleted"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the POI to delete", required = true) @PathVariable String id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all points of interest",
            description = "Retrieves a list of all POIs.")
    @ApiResponse(responseCode = "200", description = "List of PointOfInterestDto returned")
    @GetMapping
    public ResponseEntity<List<PointOfInterestDto>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get point of interest by ID",
            description = "Retrieves the POI identified by the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PointOfInterestDto returned"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PointOfInterestDto> getOne(
            @Parameter(description = "ID of the POI to retrieve", required = true) @PathVariable String id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "List POIs by city",
            description = "Retrieves all points of interest in the specified city.")
    @ApiResponse(responseCode = "200", description = "List of PointOfInterestDto returned")
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<PointOfInterestDto>> listByCity(
            @Parameter(description = "ID of the city to filter POIs", required = true) @PathVariable String cityId
    ) {
        return ResponseEntity.ok(service.findByCity(cityId));
    }
}
