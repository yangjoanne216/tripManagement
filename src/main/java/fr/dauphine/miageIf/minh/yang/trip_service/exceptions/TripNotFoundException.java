package fr.dauphine.miageIf.minh.yang.trip_service.exceptions;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String id) {
        super("Trip not found: " + id);
    }
}
