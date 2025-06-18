package fr.dauphine.miageIf.minh.yang.info_service.controller;

import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.service.ActivityService;
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
@Validated
@RequestMapping("/activities")
@Tag(name = "Activity API", description = "Endpoints for managing activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService service;

    @Operation(summary = "Create a new activity")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Activity successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<ActivityDto> create(
            @Parameter(description = "Activity data", required = true)
            @Valid @RequestBody ActivityUpdateOrCreateDto dto
    ) {
        ActivityDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing activity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activity successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDto> update(
            @Parameter(description = "Activity ID", required = true) @PathVariable String id,
            @Parameter(description = "Updated activity data", required = true) @Valid @RequestBody ActivityUpdateOrCreateDto dto
    ) {
        ActivityDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete an activity")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Activity successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all activities")
    @ApiResponse(responseCode = "200", description = "List of activities returned")
    @GetMapping
    public ResponseEntity<List<ActivityDto>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get activity by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activity returned"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDto> getOne(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Get activities by POI IDs",
            description = "Retrieves activities associated with one or more point-of-interest IDs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of ActivityDto returned"),
            @ApiResponse(responseCode = "400", description = "Invalid POI ID format")
    })
    @GetMapping("/poiIds")
    public ResponseEntity<List<ActivityDto>> getByPoiIds(
            @Parameter(description = "Comma-separated list of POI IDs (24-hex strings)", required = true)
            @RequestParam List<String> poiIds
    ) {
        List<ActivityDto> dtos = service.findByPoiIds(poiIds);
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "Get activity by name",
            description = "Retrieves a single activity DTO by exact name match."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ActivityDto returned"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<ActivityDto> getByName(
            @Parameter(description = "Exact name of the activity to retrieve", required = true)
            @PathVariable String name
    ) {
        ActivityDto dto = service.findByName(name);
        return ResponseEntity.ok(dto);
    }

}
