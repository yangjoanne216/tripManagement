package fr.dauphine.miageIf.minh.yang.route_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当请求的 Edge (LOCATED_AT 关系) ID 在数据库中不存在时，抛出此异常。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EdgeNotFoundException extends RuntimeException {
    public EdgeNotFoundException(Long routeId) {
        super("Edge (route) with id " + routeId + " not found.");
    }

    public EdgeNotFoundException(String sourceCityId,String destinationCityId) {
        super("Edge (route) with cityId : "+ sourceCityId+" and cityId : "+destinationCityId+" not found.");
    }
}