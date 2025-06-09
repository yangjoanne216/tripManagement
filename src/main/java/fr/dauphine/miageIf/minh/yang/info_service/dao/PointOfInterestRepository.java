package fr.dauphine.miageIf.minh.yang.info_service.dao;

import fr.dauphine.miageIf.minh.yang.info_service.model.PointOfInterest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PointOfInterestRepository extends MongoRepository<PointOfInterest,String> {
    List<PointOfInterest> findByCity_Id(String cityId);
}
