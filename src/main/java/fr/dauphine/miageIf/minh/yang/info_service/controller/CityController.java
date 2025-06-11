package fr.dauphine.miageIf.minh.yang.info_service.controller;

import fr.dauphine.miageIf.minh.yang.info_service.dto.CityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import fr.dauphine.miageIf.minh.yang.info_service.service.CityService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @Operation(
            summary = "Create a new city",
            description = "Creates a new city document in MongoDB and returns the created CityDto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "City successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body")
    })
    @PostMapping
    public ResponseEntity<CityDto> create(
            @Parameter(description = "CityDto object containing name, photos, geoInfo, accommodations and pointsOfInterest", required = true)
            @Valid @RequestBody CityDto dto
    ) {
        CityDto created = cityService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(
            summary = "Update an existing city",
            description = "Updates the city identified by the given ID with the provided CityDto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body"),
            @ApiResponse(responseCode = "404", description = "City not found with the given ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CityDto> update(
            @Parameter(description = "ID of the city to update", required = true) @PathVariable String id,
            @Parameter(description = "Updated CityDto object", required = true) @Valid @RequestBody CityUpdateDto dto
    ) {
        CityDto updated = cityService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete a city",
            description = "Deletes the city identified by the given ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "City successfully deleted"),
            @ApiResponse(responseCode = "404", description = "City not found with the given ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the city to delete", required = true) @PathVariable String id
    ) {
        cityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List all cities",
            description = "Retrieves a list of all cities."
    )
    @ApiResponse(responseCode = "200", description = "List of CityDto returned")
    @GetMapping
    public ResponseEntity<List<CityDto>> list() {
        List<CityDto> all = cityService.findAll();
        return ResponseEntity.ok(all);
    }

    @Operation(
            summary = "Get city by ID",
            description = "Retrieves a single city by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CityDto returned"),
            @ApiResponse(responseCode = "404", description = "City not found with the given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CityDto> get(
            @Parameter(description = "ID of the city to retrieve", required = true) @PathVariable String id
    ) {
        CityDto dto = cityService.findById(id);
        return ResponseEntity.ok(dto);
    }
}