package fr.dauphine.miageIf.minh.yang.info_service.service;

import com.mongodb.DuplicateKeyException;
import fr.dauphine.miageIf.minh.yang.info_service.dao.AccommodationRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.CityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.PointOfInterestRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.BadRequestException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ConflictException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.CityMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepo;
    private final CityMapper cityMapper;
    private final AccommodationRepository accommodationRepository;
    private final PointOfInterestRepository pointOfInterestRepository;

    public CityDto create(CityUpdateOrCreateDto dto) {
        try {
            City entity = cityMapper.toEntity(dto);
            City saved = cityRepo.save(entity);
            return cityMapper.toDto(saved);
        } catch (DuplicateKeyException ex) {
            throw new ConflictException("City name must be unique: " + dto.getName());
        }
    }

    public CityDto update(String id, CityUpdateOrCreateDto dto) {
        City existing = cityRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
        try {
            cityMapper.updateEntityFromDto(dto, existing);
            City saved = cityRepo.save(existing);
            return cityMapper.toDto(saved);
        } catch (DuplicateKeyException ex) {
            throw new ConflictException("City name must be unique: " + dto.getName());
        }
    }

    public void delete(String id) {
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
}
