package fr.dauphine.miageIf.minh.yang.info_service.service;

import com.mongodb.DuplicateKeyException;
import feign.FeignException;
import fr.dauphine.miageIf.minh.yang.info_service.Feign.RouteServiceClient;
import fr.dauphine.miageIf.minh.yang.info_service.dao.AccommodationRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.CityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.PointOfInterestRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.RouteCityDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.BadRequestException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ConflictException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.CityMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepo;
    private final CityMapper cityMapper;
    private final AccommodationRepository accommodationRepository;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final RouteServiceClient routeClient;

    public CityDto create(CityUpdateOrCreateDto dto) throws ServiceUnavailableException {
        if (cityRepo.existsByName(dto.getName())) {
            throw new ConflictException("City name must be unique: " + dto.getName());
        }
        City entity = cityMapper.toEntity(dto);
        City saved = cityRepo.save(entity);
        // 同步到 Route-service
        try {
            routeClient.createCity(new RouteCityDto(
                    saved.getId(),
                    saved.getName(),
                    saved.getGeoInfo().getLat(),
                    saved.getGeoInfo().getLon()
            ));
        } catch (FeignException fe) {
            // 回滚刚才的 Mongo 插入
            cityRepo.deleteById(saved.getId());
            throw new ServiceUnavailableException(
                    "Unable to sync to Route-service: " + fe.getMessage()
            );
        }
        return cityMapper.toDto(saved);
    }

    public CityDto update(String id, CityUpdateOrCreateDto dto) throws ServiceUnavailableException {
        City existing = cityRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
        if (!existing.getName().equals(dto.getName()) && cityRepo.existsByName(dto.getName())) {
            throw new ConflictException("City name must be unique: " + dto.getName());
        }
        cityMapper.updateEntityFromDto(dto, existing);
        City saved = cityRepo.save(existing);
        try {
            routeClient.updateCity(id, new RouteCityDto(
                    saved.getId(),
                    saved.getName(),
                    saved.getGeoInfo().getLat(),
                    saved.getGeoInfo().getLon()
            ));
        } catch (FeignException fe) {
            throw new ServiceUnavailableException(
                    "Unable to sync to Route-service: " + fe.getMessage()
            );
        }
        return cityMapper.toDto(saved);
    }

    public void delete(String id) throws ServiceUnavailableException {
        if (!cityRepo.existsById(id)) {
            throw new ResourceNotFoundException("City not found: " + id);
        }
        ObjectId oid = new ObjectId(id);
        if (!accommodationRepository.findByCityId(oid).isEmpty()){
            throw new BadRequestException("Cannot delete city: accommodations still exist");
        }
        if(!pointOfInterestRepository.findByCityId(oid).isEmpty()) {
            throw new BadRequestException("Cannot delete city: points of interest still exist");
        }
        cityRepo.deleteById(id);

        // 同步删除 Route-service
        try {
            routeClient.deleteCity(id);
        } catch (FeignException fe) {
            throw new ServiceUnavailableException(
                    "Unable to sync to Route-service: " + fe.getMessage()
            );
        }
    }

    public List<CityDto> findAll() {
        return cityRepo.findAll().stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
    }

    public CityDto findById(String id) {
        City entity = cityRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
        return cityMapper.toDto(entity);
    }

    public City findByNameOrThrow(String name) {
        return cityRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + name));
    }

    public CityDto findByName(String name) {
        City city = cityRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + name));
        return cityMapper.toDto(city);
    }
}
