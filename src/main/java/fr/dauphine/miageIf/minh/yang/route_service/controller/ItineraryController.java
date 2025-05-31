package fr.dauphine.miageIf.minh.yang.route_service.controller;

import fr.dauphine.miageIf.minh.yang.route_service.dto.ItineraryResponse;
import fr.dauphine.miageIf.minh.yang.route_service.service.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;


/**
 * Itinerary (路径查询) 相关的只读接口：最短路径 & 所有路径（最多中转 maxStops）。
 */
@RestController
@RequestMapping("/itineraries")
@RequiredArgsConstructor
@Tag(
        name = "itinerary service API",
        description = "itinerary endPoints"
)
public class ItineraryController {
    private final ItineraryService itineraryService;

    /**
     * GET /itineraries?source={src}&destination={dst}
     * 查询最短路径（hop 数最少），返回一个长度恒为 1 的数组：
     * [
     *   { "cityIds": [src, ..., dst] }
     * ]
     * 如果无路径，则返回 [ { "cityIds": [] } ]。
     * 若 source 或 destination 不存在，则 404。
     */
    @Operation(
            summary = "Get itineraries (shortest path or all paths up to maxStops)",
            description = "Retrieve a list of itineraries (paths) between sourceCityId and destinationCityId. "
                    + "If 'maxStops' query parameter is omitted, returns exactly one itinerary—the shortest path "
                    + "(minimum number of hops). "
                    + "If 'maxStops' is provided (an integer), returns all possible paths where the number of intermediate nodes "
                    + "is less than or equal to maxStops. "
                    + "Responses are returned as a JSON array of ItineraryResponse objects, each containing a 'cityIds' list. "
                    + "If no path exists, returns an empty list (for maxStops case) or a single ItineraryResponse with an empty 'cityIds' list (for shortest‐path case). "
                    + "If either sourceCityId or destinationCityId does not exist, returns HTTP 404 Not Found."
    )
    @GetMapping
    public ResponseEntity<List<ItineraryResponse>> getItineraries(
            @RequestParam("source") String sourceCityId,
            @RequestParam("destination") String destinationCityId,
            @RequestParam(name = "maxStops", required = false) Integer maxStops
    ) {
        if (maxStops == null) {
            // 查询最短路径
            ItineraryResponse single = itineraryService.getShortestPath(sourceCityId, destinationCityId);
            return ResponseEntity.ok(Collections.singletonList(single));
        } else {
            // 查询所有路径，hop ≤ maxStops + 1
            List<ItineraryResponse> allPaths =
                    itineraryService.getAllRoutesWithMaxStops(sourceCityId, destinationCityId, maxStops);
            return ResponseEntity.ok(allPaths);
        }
    }
}
