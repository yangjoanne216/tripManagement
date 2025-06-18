package fr.dauphine.miageIf.minh.yang.trip_service.dao;

import fr.dauphine.miageIf.minh.yang.trip_service.model.TripActivity;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripActivityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TripActivity 的 Repository，
 * 并提供根据 tripId 查找活动列表的方法。
 */
@Repository
public interface TripActivityDao extends JpaRepository<TripActivity, TripActivityKey> {
    List<TripActivity> findByTrip_Id(Long tripId);
    /*查询某天的活动*/
    List<TripActivity> findByTrip_IdAndId_Day(Long tripId, Integer day);
}