package fr.dauphine.miageIf.minh.yang.trip_service.exceptions;

import jakarta.ws.rs.BadRequestException;

public class InvalidTripDataException extends BadRequestException {
    public InvalidTripDataException(String msg) {
        super(msg);
    }
}
