package fr.dauphine.miageIf.minh.yang.info_service.controller;

import fr.dauphine.miageIf.minh.yang.info_service.dto.*;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.PointOfInterestMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import fr.dauphine.miageIf.minh.yang.info_service.service.AccommodationService;
import fr.dauphine.miageIf.minh.yang.info_service.service.ActivityService;
import fr.dauphine.miageIf.minh.yang.info_service.service.CityService;
import fr.dauphine.miageIf.minh.yang.info_service.service.PointOfInterestService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/cities")
@Tag(name = "City API", description = "Endpoints for managing cities")
@RequiredArgsConstructor
@Validated
public class CityController {
    private final CityService service;
    private final PointOfInterestService pointOfInterestService;
    private final AccommodationService accommodationService;
    private final ActivityService activityService;

    @Operation(summary = "Create a new city")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "City successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<CityDto> create(
            @Parameter(description = "City data", required = true)
            @Valid @RequestBody CityUpdateOrCreateDto dto
    ) {
        CityDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CityDto> update(
            @Parameter(description = "City ID", required = true) @PathVariable String id,
            @Parameter(description = "Updated city data", required = true) @Valid @RequestBody CityUpdateOrCreateDto dto
    ) {
        CityDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a city")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "City successfully deleted"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all cities")
    @ApiResponse(responseCode = "200", description = "List of cities returned")
    @GetMapping
    public ResponseEntity<List<CityDto>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get city by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City returned"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CityDto> getOne(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Get accommodations by city name",
            description = "Retrieve all accommodations located in the specified city."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of accommodations returned"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @GetMapping("/name/{name}/accommodations")
    public List<AccommodationDto> getAccommodationsByCityName(@PathVariable String name) {
        return accommodationService.findByCityName(name);
    }

    @Operation(
            summary = "Get points of interest by city name",
            description = "Retrieve all points of interest located in the specified city."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of points of interest returned"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @GetMapping("/name/{name}/points-of-interest")
    public List<PointOfInterestDto> getPoisByCityName(@PathVariable String name) {
        return pointOfInterestService.findByCityName(name);
    }

    /*@Operation(
            summary = "Get activities by city name",
            description = "Retrieve all activities available in the specified city."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of activities returned"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @GetMapping("/name/{name}/activities")
    public List<ActivityDto> getActivitiesByCityName(@PathVariable String name) {
        return activityService.findByCityName(name);
    }*/

    @Operation(
            summary = "Get city by name",
            description = "Retrieves a single city by its exact name. Returns 404 if not found."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CityDto returned"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<CityDto> getByName(@PathVariable String name) {
        CityDto dto = service.findByName(name);
        return ResponseEntity.ok(dto);
    }

}