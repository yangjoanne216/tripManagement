package fr.dauphine.miageIf.minh.yang.route_service.controller;

import fr.dauphine.miageIf.minh.yang.route_service.dto.CreationCityRequest;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.CityNotFoundException;
import fr.dauphine.miageIf.minh.yang.route_service.model.City;
import fr.dauphine.miageIf.minh.yang.route_service.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/edges")
@RequiredArgsConstructor
public class EdgeController {
    private final EdgeService edgeService;

    @GetMapping
    public ResponseEntity<List<City>> findAll() {
        return ResponseEntity.ok(cityService.findAll());
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<List<City>> findNeighbours(@PathVariable("cityId") String cityId, @RequestParam Double maxDistanceKm) {
        return ResponseEntity.ok(cityService.findNeighbours(cityId, maxDistanceKm));
    }

    @PostMapping
    public ResponseEntity<City> create(@RequestBody CreationCityRequest body) {
        return ResponseEntity.ok(cityService.create(body.cityId(), body.name()));
    }

    @PutMapping("/{cityId}")
    public ResponseEntity<City> changeName( @PathVariable("cityId") String cityId, @RequestBody String name) throws CityNotFoundException {
        return ResponseEntity.ok(cityService.changeName(cityId, name));
    }

    @DeleteMapping("/{cityId}")
    public ResponseEntity<?> delete(@PathVariable("cityId") String cityId) throws CityNotFoundException {
        return ResponseEntity.ok(cityService.delete(cityId));
    }
}
