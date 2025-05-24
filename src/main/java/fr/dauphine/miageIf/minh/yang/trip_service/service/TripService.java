package fr.dauphine.miageIf.minh.yang.trip_service.service;

import fr.dauphine.miageIf.minh.yang.trip_service.dao.TripDao;
import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TripService {
    private final TripDao tripDao;

    public TripService(TripDao tripDao) {
        this.tripDao = tripDao;
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
}
