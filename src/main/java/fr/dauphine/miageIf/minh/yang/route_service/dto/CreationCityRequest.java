package fr.dauphine.miageIf.minh.yang.route_service.dto;

import fr.dauphine.miageIf.minh.yang.route_service.model.City;

public record CreationCityRequest (String cityId, String name, String description, City city) {}
