package fr.dauphine.miageIf.minh.yang.info_service.controller;

import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.service.PointOfInterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points-of-interest")
@Tag(name = "Point of Interest API", description = "Endpoints for managing POIs")
@RequiredArgsConstructor
@Validated
public class PointOfInterestController {
    private final PointOfInterestService service;

    @Operation(summary = "Create a new point of interest")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "POI successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<PointOfInterestDto> create(
            @Parameter(description = "POI data", required = true)
            @Valid @RequestBody PointOfInterestUpdateOrCreateDto dto
    ) {
        PointOfInterestDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing point of interest")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "POI successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PointOfInterestDto> update(
            @Parameter(description = "POI ID", required = true) @PathVariable String id,
            @Parameter(description = "Updated POI data", required = true) @Valid @RequestBody PointOfInterestUpdateOrCreateDto dto
    ) {
        PointOfInterestDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a point of interest")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "POI successfully deleted"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all points of interest")
    @ApiResponse(responseCode = "200", description = "List of POIs returned")
    @GetMapping
    public ResponseEntity<List<PointOfInterestDto>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get point of interest by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "POI returned"),
            @ApiResponse(responseCode = "404", description = "POI not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PointOfInterestDto> getOne(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "List POIs by city")
    @ApiResponse(responseCode = "200", description = "List of POIs returned")
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<PointOfInterestDto>> listByCity(@PathVariable String cityId) {
        return ResponseEntity.ok(service.findByCity(cityId));
    }
}
