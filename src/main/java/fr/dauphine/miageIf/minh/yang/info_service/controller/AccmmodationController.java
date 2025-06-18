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
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/accommodations")
@Tag(name = "Accommodation API", description = "Endpoints for managing accommodations")
@RequiredArgsConstructor
public class AccmmodationController {private final AccommodationService service;

    @Operation(summary = "Create a new accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Accommodation successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<AccommodationDto> create(
            @Parameter(description = "Accommodation data", required = true)
            @Valid @RequestBody AccommodationUpdateOrCreateDto dto
    ) {
        AccommodationDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accommodation successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDto> update(
            @Parameter(description = "Accommodation ID", required = true) @PathVariable String id,
            @Parameter(description = "Updated accommodation data", required = true) @Valid @RequestBody AccommodationUpdateOrCreateDto dto
    ) {
        AccommodationDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete an accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Accommodation successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all accommodations")
    @ApiResponse(responseCode = "200", description = "List of accommodations returned")
    @GetMapping
    public ResponseEntity<List<AccommodationDto>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get accommodation by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accommodation returned"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDto> getOne(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "List accommodations by city")
    @ApiResponse(responseCode = "200", description = "List of accommodations returned")
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<AccommodationDto>> listByCity(@PathVariable String cityId) {
        return ResponseEntity.ok(service.findByCity(cityId));
    }
}
