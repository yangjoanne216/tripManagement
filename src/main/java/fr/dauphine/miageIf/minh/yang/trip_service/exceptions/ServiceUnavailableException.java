package fr.dauphine.miageIf.minh.yang.trip_service.exceptions;


public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
    public ServiceUnavailableException(String message) {
        super(message);
    }
}

