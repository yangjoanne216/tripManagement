package fr.dauphine.miageIf.minh.yang.trip_service.dao;

import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodation;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * TripAccommodation 的 Repository，
 * 提供根据 tripId 查找住宿列表的方法。
 */
@Repository
public interface TripAccommodationDao extends JpaRepository<TripAccommodation, TripAccommodationKey> {
    List<TripAccommodation> findByTrip_Id(Long tripId);
    /*增加查询某天住宿*/
    Optional<TripAccommodation> findByTrip_IdAndId_Day(Long tripId, Integer day);

}
