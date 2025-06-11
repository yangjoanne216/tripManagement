package fr.dauphine.miageIf.minh.yang.info_service.controller;

import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.info_service.service.ActivityService;
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
@RequestMapping("/activities")
@Tag(name = "Activity API", description = "Endpoints for managing activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService service;

    @Operation(summary = "Create a new activity",
            description = "Creates a new activity document in MongoDB and returns the created ActivityDto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Activity successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body")
    })
    @PostMapping
    public ResponseEntity<ActivityDto> create(
            @Parameter(description = "ActivityDto containing name, poi reference, photos, seasons, and price", required = true)
            @Valid @RequestBody ActivityDto dto
    ) {
        ActivityDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update an existing activity",
            description = "Updates the activity identified by the given ID with the provided data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activity successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the request body"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDto> update(
            @Parameter(description = "ID of the activity to update", required = true) @PathVariable String id,
            @Parameter(description = "ActivityDto containing updated fields", required = true) @Valid @RequestBody ActivityDto dto
    ) {
        ActivityDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete an activity",
            description = "Deletes the activity identified by the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Activity successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the activity to delete", required = true) @PathVariable String id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all activities",
            description = "Retrieves a list of all activities.")
    @ApiResponse(responseCode = "200", description = "List of ActivityDto returned")
    @GetMapping
    public ResponseEntity<List<ActivityDto>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get activity by ID",
            description = "Retrieves the activity identified by the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ActivityDto returned"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDto> getOne(
            @Parameter(description = "ID of the activity to retrieve", required = true) @PathVariable String id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }
}
