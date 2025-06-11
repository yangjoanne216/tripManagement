package fr.dauphine.miageIf.minh.yang.info_service.controller;

import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.service.AccommodationService;
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
@RequestMapping("/accommodations")
@Tag(name = "Accommodation API", description = "Endpoints for managing accommodations")
@RequiredArgsConstructor
public class AccmmodationController {

    private final AccommodationService service;

    @Operation(summary = "Create a new accommodation",
            description = "Creates a new accommodation document in MongoDB and returns the created AccommodationDto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Accommodation successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body")
    })
    @PostMapping
    public ResponseEntity<AccommodationDto> create(
            @Parameter(description = "AccommodationDto containing name, address, photos, city, price, and availability", required = true)
            @Valid @RequestBody AccommodationUpdateOrCreateDto dto
    ) {
        AccommodationDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing accommodation",
            description = "Updates the accommodation identified by the given ID with the provided data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accommodation successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDto> update(
            @Parameter(description = "ID of the accommodation to update", required = true) @PathVariable String id,
            @Parameter(description = "AccommodationDto containing updated fields", required = true) @Valid @RequestBody AccommodationUpdateOrCreateDto dto
    ) {
        AccommodationDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete an accommodation",
            description = "Deletes the accommodation identified by the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Accommodation successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the accommodation to delete", required = true) @PathVariable String id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all accommodations",
            description = "Retrieves a list of all accommodations.")
    @ApiResponse(responseCode = "200", description = "List of AccommodationDto returned")
    @GetMapping
    public ResponseEntity<List<AccommodationDto>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get accommodation by ID",
            description = "Retrieves the accommodation identified by the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AccommodationDto returned"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDto> getOne(
            @Parameter(description = "ID of the accommodation to retrieve", required = true) @PathVariable String id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "List accommodations in a city",
            description = "Retrieves all accommodations in the specified city.")
    @ApiResponse(responseCode = "200", description = "List of AccommodationDto returned")
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<AccommodationDto>> listByCity(
            @Parameter(description = "ID of the city to filter accommodations", required = true) @PathVariable String cityId
    ) {
        return ResponseEntity.ok(service.findByCity(cityId));
    }
}
