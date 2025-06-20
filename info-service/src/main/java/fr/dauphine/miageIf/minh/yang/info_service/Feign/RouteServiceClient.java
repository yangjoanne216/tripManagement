package fr.dauphine.miageIf.minh.yang.info_service.Feign;

import fr.dauphine.miageIf.minh.yang.info_service.dto.RouteCityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "route-service",
        url  = "${route.service.url:http://localhost:8081/cities}"
)
public interface RouteServiceClient{
    @PostMapping
    RouteCityDto createCity(@RequestBody RouteCityDto city);

    @PutMapping("/{cityId}")
    RouteCityDto updateCity(
            @PathVariable("cityId") String cityId,
            @RequestBody RouteCityDto city
    );

    @DeleteMapping("/{cityId}")
    void deleteCity(@PathVariable("cityId") String cityId);
}
