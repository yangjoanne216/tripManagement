package fr.dauphine.miageIf.minh.yang.route_service.exceptions;

public class EdgeAlreadyExistsException extends RuntimeException {
    public EdgeAlreadyExistsException(String sourceCityId, String destinationCityId) {
        super("Edge already exists between " + sourceCityId + " and " + destinationCityId);
    }
}
