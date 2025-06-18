package fr.dauphine.miageIf.minh.yang.trip_service.feign;

import fr.dauphine.miageIf.minh.yang.trip_service.dto.AccommodationDto;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.CityDto;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.PointOfInterestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "INFO-SERVICE", path = "")
public interface InfoClient {

    // —— City ——
    @GetMapping("/cities/name/{name}")
    CityDto getCityByName(@PathVariable String name);

    @GetMapping("/cities/{id}")
    CityDto getCityById(@PathVariable String id);              // 保留若仍需要

    // —— Point of Interest ——
    @GetMapping("/cities/name/{name}/points-of-interest")
    List<PointOfInterestDto> getPoisByCityName(@PathVariable String name);

    @GetMapping("/points-of-interest/city/{cityId}")
    List<PointOfInterestDto> getPoisByCityId(@PathVariable String cityId);

    @GetMapping("/points-of-interest/{id}")
    PointOfInterestDto getPoiById(@PathVariable("id") String id);
    // —— Activity ——
    @GetMapping("/activities/city/{cityName}")
    List<ActivityDto> getActivitiesByCityName(@PathVariable String cityName);

    @GetMapping("/activities/poiIds")
    List<ActivityDto> getActivitiesByPoiIds(@RequestParam("poiIds") List<String> poiIds);

    @GetMapping("/activities/{id}")
    ActivityDto getActivityById(@PathVariable("id") String id);
    // —— Accommodation ——
    @GetMapping("/accommodations/city/{cityId}")
    List<AccommodationDto> getAccommodationsByCityId(@PathVariable String cityId);

    @GetMapping("/accommodations/{id}")
    AccommodationDto getAccommodationById(@PathVariable("id") String id);
}
