package fr.dauphine.miageIf.minh.yang.trip_service.exceptions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TripAlreadyExistsException extends RuntimeException{
    public TripAlreadyExistsException(String field, String value) {
        super("Trip already exists with " + field + ": " + value);
    }
}
