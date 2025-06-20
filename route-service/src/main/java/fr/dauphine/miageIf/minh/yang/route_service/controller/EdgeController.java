package fr.dauphine.miageIf.minh.yang.route_service.controller;

import fr.dauphine.miageIf.minh.yang.route_service.dto.CreationEdgeRequest;
import fr.dauphine.miageIf.minh.yang.route_service.dto.EdgeResponse;
import fr.dauphine.miageIf.minh.yang.route_service.dto.UpdateEdgeRequest;
import fr.dauphine.miageIf.minh.yang.route_service.service.EdgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/edges")
@RequiredArgsConstructor
@Tag(
        name = "edge service API",
        description = "edge endPoints"
)
public class EdgeController {
    private final EdgeService edgeService;

    /**
     * GET /edges/all
     * 查询所有 Edge，返回 List<EdgeResponse>。
     * 200 OK + JSON 数组
     */
    @Operation(
            summary = "Get all edges",
            description = "Retrieve a list of all LOCATED_AT relationships (edges) between City nodes. " +
                    "Returns HTTP 200 OK and a JSON array of EdgeResponse objects."
    )
    @GetMapping
    public ResponseEntity<List<EdgeResponse>> getAllEdges() {
        List<EdgeResponse> edges = edgeService.getAllEdges();
        return ResponseEntity.ok(edges);
    }

    /**
     * GET /edges?source={src}&destination={dst}
     * 查询两 City 之间的直接 LOCATED_AT 关系。
     * 200 OK + EdgeResponse JSON；若关系不存在，则 404
     */
    @Operation(
            summary = "Get edge by source and destination",
            description = "Retrieve the direct LOCATED_AT relationship between sourceCityId and destinationCityId. " +
                    "Returns HTTP 200 OK and a single EdgeResponse. " +
                    "If no such relationship exists, returns HTTP 404 Not Found."
    )
    @GetMapping("/{sourceCityId}/{destinationCityId}")
    public ResponseEntity<EdgeResponse> getEdgeByCities(
            @PathVariable String sourceCityId,
            @PathVariable String destinationCityId
    ) {
        EdgeResponse edge = edgeService.getEdgeBetween(sourceCityId, destinationCityId);
        return ResponseEntity.ok(edge);
    }

    /**
     * POST /edges
     * 创建一个新的 LOCATED_AT 关系。
     * 请求 JSON：
     * {
     * "sourceCityId": "...",
     * "destinationCityId": "...",
     * "distanceKm": 541,
     * "travelTimeMin": 360
     * }
     * 成功：201 Created + Location: /edges/{routeId} + 响应体 EdgeResponse JSON
     * 若 sourceCityId 或 destinationCityId 不存在，则 404
     */
    @Operation(
            summary = "Create a new edge",
            description = "Create a new LOCATED_AT relationship between sourceCityId and destinationCityId. " +
                    "Request body must include sourceCityId, destinationCityId, distanceKm, and travelTimeMin. " +
                    "Returns HTTP 201 Created and the created EdgeResponse. " +
                    "If either City does not exist, returns HTTP 404 Not Found."
    )
    @PostMapping
    public ResponseEntity<EdgeResponse> createEdge(@RequestBody CreationEdgeRequest request) {
        EdgeResponse created = edgeService.createEdge(request);
        URI location = URI.create("/edges/" + created.getRouteId());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(created, headers, HttpStatus.CREATED);
    }

    /**
     * PUT /edges/{routeId}
     * 更新已有 LOCATED_AT 关系的 distanceKm / travelTimeMin 中的一个或两个。
     * 请求 JSON，比如 { "distanceKm": 600 } 或 { "travelTimeMin": 400 }，也可以两者同时更新。
     * 200 OK + 更新后 EdgeResponse；若 routeId 不存在，则 404

    @Operation(
            summary = "Update edge",
            description = "Update the properties (distanceKm and/or travelTimeMin) of an existing LOCATED_AT relationship " +
                    "identified by ids of two cities. Request body can contain one or both fields. " +
                    "Returns HTTP 200 OK and the updated EdgeResponse. " +
                    "If edge does not exist, returns HTTP 404 Not Found."
    )
    @PutMapping("/{sourceCityId}/{destinationCityId}")
    public ResponseEntity<EdgeResponse> updateEdgeByCities(
            @PathVariable String sourceCityId,
            @PathVariable String destinationCityId,
            @RequestBody UpdateEdgeRequest request
    ) {
        EdgeResponse updated = edgeService.updateEdgeByCities(sourceCityId, destinationCityId, request);
        return ResponseEntity.ok(updated);
    } */

    @Operation(
            summary = "Delete edge",
            description = "Delete the LOCATED_AT relationship identified by twoCitys. " +
                    "Returns HTTP 204 No Content if deletion succeeds. " +
                    "If edge does not exist, returns HTTP 404 Not Found."
    )
    @DeleteMapping("/{sourceCityId}/{destinationCityId}")
    public ResponseEntity<Void> deleteEdgeByCities(
            @PathVariable String sourceCityId,
            @PathVariable String destinationCityId
    ) {
        edgeService.deleteEdgeByCities(sourceCityId, destinationCityId);
        return ResponseEntity.noContent().build();
    }
}
