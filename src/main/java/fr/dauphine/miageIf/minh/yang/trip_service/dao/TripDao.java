package fr.dauphine.miageIf.minh.yang.trip_service.dao;

import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripDao extends JpaRepository<Trip, Long> {
    // fetch with activities & accommodations in one query
    @EntityGraph(attributePaths = {"activities", "accommodations"})
    Optional<Trip> findById(Long id);
}
