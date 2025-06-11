package fr.dauphine.miageIf.minh.yang.info_service.exceptions;

import org.bson.types.ObjectId;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(String.valueOf(message));} }
