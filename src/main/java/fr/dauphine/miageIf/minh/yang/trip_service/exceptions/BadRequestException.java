package fr.dauphine.miageIf.minh.yang.trip_service.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
