package fr.dauphine.miageIf.minh.yang.info_service.dao;

import fr.dauphine.miageIf.minh.yang.info_service.model.Accommodation;
import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccommodationRepository extends MongoRepository<Accommodation, String> {
    /** 根据 cityId 查询所有住宿 */
    List<Accommodation> findByCityId(ObjectId cityId);

    boolean existsByName(@NotBlank(message = "name must not be blank") String name);
}