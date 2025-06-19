package fr.dauphine.miageIf.minh.yang.route_service.controller;

import fr.dauphine.miageIf.minh.yang.route_service.dto.CreationCityRequest;
import fr.dauphine.miageIf.minh.yang.route_service.dto.NeighborDto;
import fr.dauphine.miageIf.minh.yang.route_service.dto.UpdateCityRequest;
import fr.dauphine.miageIf.minh.yang.route_service.model.City;
import fr.dauphine.miageIf.minh.yang.route_service.service.CityService;
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
@RequestMapping("/cities")
@RequiredArgsConstructor
@Tag(
        name = "city service API",
        description = "city endPoints"
)
public class CityController {
    private final CityService cityService;

    /**
     * GET /cities
     * 返回所有 City 列表
     * 200 OK + JSON 数组
     */
    @Operation(
            summary = "Get all cities",
            description = "Retrieve a list of all City nodes currently stored in the database. " +
                    "Returns HTTP 200 OK and a JSON array of City objects."
    )
    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        List<City> all = cityService.getAllCities();
        return ResponseEntity.ok(all);
    }

    /**
     * GET /cities/{cityId}
     * 返回单个 City 详情
     * 200 OK + City JSON；若 cityId 不存在，抛 404
     */
    @Operation(
            summary = "Get city by ID",
            description = "Retrieve a single City node by its unique cityId. " +
                    "If the cityId does not exist, returns HTTP 404 Not Found."
    )
    @GetMapping("/{cityId}")
    public ResponseEntity<City> getCityById(@PathVariable String cityId) {
        City city = cityService.getCityByCityId(cityId);
        return ResponseEntity.ok(city);
    }

    /**
     * GET /cities/{cityId}/neighbors?maxDistanceKm={n}
     * 查询某 City 的所有邻居，且关系 distanceKm ≤ n。n 可选，默认 0 表示“全部邻居”。
     * 200 OK + JSON 数组；若 cityId 不存在，抛 404
     */
    @Operation(
            summary = "Get city neighbors within distance",
            description = "Retrieve all neighboring City nodes connected by a LOCATED_AT relationship, " +
                    "where the relationship's distanceKm <= maxDistanceKm. " +
                    "If maxDistanceKm is not provided, defaults to 0 (return all neighbors). " +
                    "Returns HTTP 200 OK and a JSON array of NeighborDto. " +
                    "If the cityId does not exist, returns HTTP 404 Not Found."
    )
    @GetMapping("/{cityId}/neighbors")
    public ResponseEntity<List<NeighborDto>> getNeighbors(
            @PathVariable String cityId,
            @RequestParam(name = "maxDistanceKm", defaultValue = "0") int maxDistanceKm
    ) {
        List<NeighborDto> neighbors = cityService.getNeighbors(cityId, maxDistanceKm);
        return ResponseEntity.ok(neighbors);
    }

    /**
     * POST /cities
     * 创建新 City。请求 JSON：
     * {
     *   "cityId": "...",
     *   "name": "..."
     * }
     * 成功返回 201 Created + Location 头指向 /cities/{cityId} + 响应体为创建后的 City JSON
     */
    @Operation(
            summary = "Create a new city",
            description = "Create a new City node in the database. " +
                    "Request body must contain 'cityId' (String) and 'name' (String). " +
                    "Returns the created City object along with HTTP 201 Created."
    )
    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody CreationCityRequest request) {
        City created = cityService.createCity(request);
        // 构造 Location: /cities/{cityId}
        URI location = URI.create("/cities/" + created.getCityId());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(created, headers, HttpStatus.CREATED);
    }

    /**
     * PUT /cities/{cityId}
     * 更新单个 City 的 name（请求体只需包含 name）
     * 200 OK + 更新后 City JSON；若 cityId 不存在，抛 404
     */
    @Operation(
            summary = "Update city",
            description = "Update the 'name'/latitude/longtitude field of an existing City node identified by cityId. " +
                    "Request body must contain the new 'name',latitude,longitude. Returns the updated City object. " +
                    "If the cityId does not exist, returns HTTP 404 Not Found."
    )
    @PutMapping("/{cityId}")
    public ResponseEntity<City> updateCity(
            @PathVariable String cityId,
            @RequestBody UpdateCityRequest req
    ) {
        City updated = cityService.updateCity(cityId, req);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /cities/{cityId}
     * 删除 City 节点（及其所有关联边）。成功返回 204 No Content；若 cityId 不存在，抛 404
     */
    @Operation(
            summary = "Delete city",
            description = "Delete the City node identified by cityId, along with all LOCATED_AT relationships connected to it. " +
                    "Returns HTTP 204 No Content. If the cityId does not exist, returns HTTP 404 Not Found."
    )
    @DeleteMapping("/{cityId}")
    public ResponseEntity<Void> deleteCity(@PathVariable String cityId) {
        cityService.deleteCity(cityId);
        return ResponseEntity.noContent().build();
    }
}
