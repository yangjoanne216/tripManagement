package fr.dauphine.miageIf.minh.yang.info_service.dao;

import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityRepository extends MongoRepository<City, String> {}
