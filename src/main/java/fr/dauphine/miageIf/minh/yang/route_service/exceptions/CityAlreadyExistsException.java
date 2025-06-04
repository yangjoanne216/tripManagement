package fr.dauphine.miageIf.minh.yang.route_service.exceptions;

public class CityAlreadyExistsException extends RuntimeException {
    public CityAlreadyExistsException(String cityId) {
        super("City already exists with id: " + cityId);
    }

    public CityAlreadyExistsException(String field, String value) {
        super("City already exists with " + field + ": " + value);
    }
}
