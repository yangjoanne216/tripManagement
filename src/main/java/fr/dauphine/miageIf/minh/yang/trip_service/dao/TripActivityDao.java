package fr.dauphine.miageIf.minh.yang.trip_service.dao;

import fr.dauphine.miageIf.minh.yang.trip_service.model.TripActivity;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripActivityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripActivityDao
        extends JpaRepository<TripActivity, TripActivityKey> {
}