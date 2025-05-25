package fr.dauphine.miageIf.minh.yang.trip_service.service;

import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripDao;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.DayDto;
import fr.dauphine.miageIf.minh.yang.trip_service.dto.TripRequestDto;
import fr.dauphine.miageIf.minh.yang.trip_service.model.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TripService {
    private final TripDao tripDao;
    private final EntityManager entityManager;

    public TripService(TripDao tripDao, EntityManager entityManager) {
        this.tripDao = tripDao;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<Trip> findAll() {
        return tripDao.findAll();
    }

    @Transactional(readOnly = true)
    public Trip findByIdWithDetails(Long id) {
        return tripDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + id));
    }

    @Transactional
    public Trip createTrip(TripRequestDto request) {
        Trip trip = new Trip();
        trip.setName(request.getName());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());

        for (DayDto day : request.getDays()) {

            //accommodation
            TripAccommodationKey tripAccommodationKey = new TripAccommodationKey();
            tripAccommodationKey.setDay(day.getDay());

            TripAccommodation tripAccommodation = new TripAccommodation();
            tripAccommodation.setTrip(trip);
            tripAccommodation.setId(tripAccommodationKey);
            tripAccommodation.setTrip(trip);
            tripAccommodation.setAccommodationId(day.getAccommodationId());
            trip.getAccommodations().add(tripAccommodation);

            //activities
            for (ActivityDto a : day.getActivities()) {
                TripActivityKey tripActivityKey = new TripActivityKey();
                tripActivityKey.setDay(day.getDay());
                tripActivityKey.setSequence(a.getOrder());

                TripActivity tripActivity = new TripActivity();
                tripActivity.setId(tripActivityKey);
                tripActivity.setTrip(trip);
                tripActivity.setActivityId(a.getActivityId());
                trip.getActivities().add(tripActivity);
            }
        }
        return tripDao.save(trip);
    }

    @Transactional
    public Trip updateTrip(Long tripId, TripRequestDto request) {
        Trip t = findByIdWithDetails(tripId);
        t.setName(request.getName());
        t.setStartDate(request.getStartDate());
        t.setEndDate(request.getEndDate());

        t.getActivities().clear();
        t.getAccommodations().clear();

        entityManager.flush();

        for (DayDto day : request.getDays()) {
            TripAccommodation acc = new TripAccommodation();
            TripAccommodationKey accKey = new TripAccommodationKey();
            accKey.setDay(day.getDay());
            acc.setId(accKey);
            acc.setTrip(t);
            acc.setAccommodationId(day.getAccommodationId());
            t.getAccommodations().add(acc);

            for (ActivityDto a : day.getActivities()) {
                TripActivity act = new TripActivity();
                TripActivityKey actKey = new TripActivityKey();
                actKey.setDay(day.getDay());
                actKey.setSequence(a.getOrder());
                act.setId(actKey);
                act.setTrip(t);
                act.setActivityId(a.getActivityId());
                t.getActivities().add(act);
            }
        }
        return tripDao.save(t);
    }

    @Transactional
    public void deleteTrip(Long id) {
        tripDao.deleteById(id);
    }

}
