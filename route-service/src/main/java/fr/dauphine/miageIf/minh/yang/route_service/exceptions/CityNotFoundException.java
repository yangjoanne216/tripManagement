package fr.dauphine.miageIf.minh.yang.route_service.exceptions;

import fr.dauphine.miageIf.minh.yang.route_service.model.City;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当请求的 City ID 在数据库中不存在时，抛出此异常。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException() {

    }
    public CityNotFoundException(String cityId) {
        super("City with id '" + cityId + "' not found.");
    }
}
