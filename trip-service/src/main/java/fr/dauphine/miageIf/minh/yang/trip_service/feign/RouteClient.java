package fr.dauphine.miageIf.minh.yang.trip_service.feign;

import fr.dauphine.miageIf.minh.yang.trip_service.dto.RouteSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "ROUTE-SERVICE",path ="")
/*@FeignClient(
        name = "route-service",
        url  = "${route.service.url:http://localhost:8081}"
)*/
public interface RouteClient {
    /**
     * 调用 itinerary-service 的 /itineraries/summary 接口，
     * 返回 source 到 destination 的总 distanceKm 和 travelTimeMin
     */
    @GetMapping("/itineraries/summary")
    RouteSummaryResponse getRouteSummary(
            @RequestParam("source") String sourceCityId,
            @RequestParam("destination") String destinationCityId
    );
}
