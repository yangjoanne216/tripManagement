package fr.dauphine.miageIf.minh.yang.info_service.dto;

import fr.dauphine.miageIf.minh.yang.info_service.model.Accommodation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccommodationRepository extends MongoRepository<Accommodation,String> {
    List<Accommodation> findByCity_Id(String cityId);
}
