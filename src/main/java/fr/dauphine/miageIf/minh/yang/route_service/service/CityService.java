package fr.dauphine.miageIf.minh.yang.route_service.service;

import fr.dauphine.miageIf.minh.yang.route_service.dao.CityDao;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.CityNotFoundException;
import fr.dauphine.miageIf.minh.yang.route_service.model.City;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityDao cityDao;

    public List<City> findAll() {
        return cityDao.findAll();
    }

    public List<City> findNeighbours(String cityId, Double maxDistanceKm) {
       return null;
    }

    public City create(String cityId, String name) {
        return cityDao.save(new City(cityId,name));
    }

    public City changeName(String cityId, String newName) throws CityNotFoundException {
        City city = cityDao.findById(cityId).orElseThrow(CityNotFoundException::new);
        city.setName(newName);
        return cityDao.save(city);
    }

    public boolean delete(String cityId) throws CityNotFoundException{
        if(!cityDao.existsById(cityId)){
            throw new CityNotFoundException();
        }
        cityDao.deleteById(cityId);
        return true;
    }
}
