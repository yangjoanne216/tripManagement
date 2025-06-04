package fr.dauphine.miageIf.minh.yang.trip_service.feign;

import fr.dauphine.miageIf.minh.yang.trip_service.dto.City;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("ROUTE-SERVICE")
public interface TripInterface {
    @GetMapping("/cities/{cityId}")
    public ResponseEntity<City> getCityById(@PathVariable String cityId);
}
