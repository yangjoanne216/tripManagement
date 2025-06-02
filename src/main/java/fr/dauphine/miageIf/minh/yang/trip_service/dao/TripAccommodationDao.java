package fr.dauphine.miageIf.minh.yang.trip_service.dao;

import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodation;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TripAccommodation 的 Repository，
 * 提供根据 tripId 查找住宿列表的方法。
 */
@Repository
public interface TripAccommodationDao extends JpaRepository<TripAccommodation, TripAccommodationKey> {
    List<TripAccommodation> findByTrip_Id(Long tripId);
}
