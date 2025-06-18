package fr.dauphine.miageIf.minh.yang.info_service.dao;

import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CityRepository extends MongoRepository<City, String> {
    Optional<City> findByName(String name);

}
