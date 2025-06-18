package fr.dauphine.miageIf.minh.yang.info_service.dao;

import fr.dauphine.miageIf.minh.yang.info_service.model.Activity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    /** 增：根据多个 POI ID 一次拉所有活动 */
    List<Activity> findByPointOfInterestIdIn(List<ObjectId> poiIds);
}