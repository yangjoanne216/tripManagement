package fr.dauphine.miageIf.minh.yang.trip_service.dao;

import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodation;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripAccomodationDao  extends JpaRepository<TripAccommodation, TripAccommodationKey> { }
