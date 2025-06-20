package fr.dauphine.miageIf.minh.yang.trip_service.dao;

import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripDao extends JpaRepository<Trip, Long> {
    // fetch with activities & accommodations in one query
    @EntityGraph(attributePaths = {"activities", "accommodations"})
    Optional<Trip> findById(Long id);
    /**
     * Search by optional start/end dates.
     */
    @Query("""
       select t from Trip t
        where (:startDate is null or t.startDate >= :startDate)
          and (:endDate   is null or t.endDate   <= :endDate)
    """)
    List<Trip> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );

    boolean existsByName(@NotBlank(message = "name must not be blank") @Size(max = 200, message = "name length must not exceed 200 characters") String name);
}
