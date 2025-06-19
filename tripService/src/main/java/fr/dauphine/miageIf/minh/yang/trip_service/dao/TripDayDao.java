package fr.dauphine.miageIf.minh.yang.trip_service.dao;

import fr.dauphine.miageIf.minh.yang.trip_service.model.TripDay;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripDayKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripDayDao extends JpaRepository<TripDay, TripDayKey> {

    List<TripDay> findByTrip_IdOrderById_Day(Long tripId);

    Optional<TripDay> findByTrip_IdAndId_Day(Long tripId, Integer day);

    Iterable<? extends TripDay> findByTrip_Id(Long tripId);
}
