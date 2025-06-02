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
        name = "trip service API",
        description = "trips endPoints"
)
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

//Todo: get中的DayCount 需要处理，可能不需要
//Todo：Creat trip中需要考虑sequece，accommondation和activityId的合理性，并考虑创建过程中 由用户选择 accommondation,也就是说由用户来设计

    /**
     * POST /trips
     * 创建一个新的 Trip。请求体为一个完整的行程 DTO（TripRequestDto）。
     * 返回值：201 + TripDetail（新创建的行程）
     *
     * @Valid 触发 DTO 校验
     */
    @Operation(
            summary = "Create a new trip",
            description = "Creates a new trip including all days, activities and accommodations. " +
                    "Returns the created TripDetail with assigned trip ID."
    )
    @PostMapping
    public ResponseEntity<TripDetail> createTrip(@Valid @RequestBody TripRequestDto tripRequestDto) {
        if (tripRequestDto.getEndDate().isBefore(tripRequestDto.getStartDate())) {
            throw new IllegalArgumentException("endDate must be equal or after startDate");
        }
        TripDetail created = tripService.createTrip(tripRequestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PUT /trips/{tripId}
     * 更新指定 ID 的 Trip（全部或部分字段）。
     * 请求体为 TripRequestDto（可以只传部分字段）。返回更新后的 TripDetail。
     *
     * @Valid 触发 DTO 校验
     */
    @Operation(
            summary = "Update an existing trip",
            description = "Updates the trip identified by tripId. You can update full or partial fields " +
                    "(name, startDate, endDate, or replace days list). Returns the updated TripDetail."
    )
    @PutMapping("/{tripId}")
    public ResponseEntity<TripDetail> updateTrip(@PathVariable Long tripId,
                                                 @Valid @RequestBody TripRequestDto tripRequestDto) {
        TripDetail updated = tripService.updateTrip(tripId, tripRequestDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /trips/{tripId}
     * 删除指定 ID 的 Trip。返回 204 无 Content。
     */
    @Operation(
            summary = "Delete a trip",
            description = "Deletes the trip identified by tripId. Returns HTTP 204 No Content on success."
    )
    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /trips/{tripId}
     * 根据 tripId 查询完整行程，返回 TripDetail。
     */
    @Operation(
            summary = "Get a trip's full itinerary by Id",
            description = "Retrieves a trip by ID, including days, activities and accommodations."
    )
    @GetMapping("/{tripId}")
    public ResponseEntity<TripDetail> getTrip(
            @Parameter(description = "ID of the trip to retrieve")
            @PathVariable Long tripId) {
        TripDetail detail = tripService.getTrip(tripId);
        return ResponseEntity.ok(detail);
    }

    /**
     * GET /trips
     * 搜索行程列表，返回 List<TripSummary>。
     * 支持查询参数：?startCity=&endCity=&minDays=&maxDays=
     * 目前示例仅对 minDays/maxDays 生效
     */
    //Todo: 扩展startCity/endCity查询
    @Operation(
            summary = "Search trips",
            description = "Search for trips filtered by optional parameters.Currently filters by minDays and maxDays startCity and endCity. " +
                    "If it doesn't have any parameter, return all trips."
    )
    @GetMapping
    public ResponseEntity<List<TripSummary>> searchTrips(
            //Todo : communicate with MongoDB to get startCity and city by activitie ID
            @Parameter(description = "Filter by trip's starting city (currently not applied)")
            @RequestParam(required = false) String startCity,

            @Parameter(description = "Filter by trip's ending city (currently not applied)")
            @RequestParam(required = false) String endCity,

            @Parameter(description = "Minimum number of days (inclusive)")
            @RequestParam(required = false) Integer minDays,

            @Parameter(description = "Maximum number of days (inclusive)")
            @RequestParam(required = false) Integer maxDays) {

        List<TripSummary> results = tripService.searchTrips(startCity, endCity, minDays, maxDays);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /trips/{tripId}/points-of-interest
     * 返回该 trip 下所有涉及的 activityId 与 accommodationId 以及 pointsOfInterests的去重集合。
     */
    //TODO：return pointsOfInterests
    @Operation(
            summary = "Get points of interest for a trip",
            description = "Returns all involved pointsOfInterests (not applied) activityIds and accommodationIds in a trip, as de-duplicated sets."
    )
    @GetMapping("/{tripId}/points-of-interest")
    public ResponseEntity<TripPointsOfInterestDto> getPointsOfInterest(
            @Parameter(description = "ID of the trip to fetch points of interest for")
            @PathVariable Long tripId) {
        TripPointsOfInterestDto poi = tripService.getPointsOfInterest(tripId);
        return ResponseEntity.ok(poi);
    }

    /**
     * GET /trips/{tripId}/days/count
     * 返回该行程的总天数（end_date - start_date + 1）。
     */
    @Operation(
            summary = "Get total days count of a trip",
            description = "Returns the number of days in the trip (inclusive of start and end date)."
    )
    @GetMapping("/{tripId}/days/count")
    public ResponseEntity<Integer> getDaysCount(
            @Parameter(description = "ID of the trip to calculate days count")
            @PathVariable Long tripId) {
        int count = tripService.getDaysCount(tripId);
        return ResponseEntity.ok(count);
    }
}
