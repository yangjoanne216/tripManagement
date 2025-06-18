package fr.dauphine.miageIf.minh.yang.info_service.service;

import com.mongodb.DuplicateKeyException;
import fr.dauphine.miageIf.minh.yang.info_service.dao.AccommodationRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.ActivityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.CityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.PointOfInterestRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ConflictException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.AccommodationMapper;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.ActivityMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.Accommodation;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accRepo;
    private final CityRepository cityRepo;
    private final CityService cityService;

    @Qualifier("accommodationMapperImpl")
    private final AccommodationMapper mapper;

    public AccommodationDto create(AccommodationUpdateOrCreateDto dto) {
        // 1) 名称唯一检查
        if (accRepo.existsByName(dto.getName())) {
            throw new ConflictException("Accommodation name must be unique: " + dto.getName());
        }
        // 2) 外键 city 校验
        if (!cityRepo.existsById(dto.getCityId())) {
            throw new ResourceNotFoundException("City not found: " + dto.getCityId());
        }
        Accommodation entity = mapper.toEntity(dto);
        Accommodation saved = accRepo.save(entity);
        return mapper.toDto(saved);
    }

    public AccommodationDto update(String id, AccommodationUpdateOrCreateDto dto) {
        Accommodation existing = accRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found: " + id));
        // 1) 名称唯一检查
        if (!existing.getName().equals(dto.getName()) && accRepo.existsByName(dto.getName())) {
            throw new ConflictException("Accommodation name must be unique: " + dto.getName());
        }
        // 2) 外键 city 校验
        if (!cityRepo.existsById(dto.getCityId())) {
            throw new ResourceNotFoundException("City not found: " + dto.getCityId());
        }
        mapper.updateEntityFromDto(dto, existing);
        Accommodation saved = accRepo.save(existing);
        return mapper.toDto(saved);

    }

    public void delete(String id) {
        if (!accRepo.existsById(id)) {
            throw new ResourceNotFoundException("Accommodation not found: " + id);
        }
        accRepo.deleteById(id);
    }

    public List<AccommodationDto> findAll() {
        return accRepo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public AccommodationDto findById(String id) {
        Accommodation e = accRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found: " + id));
        return mapper.toDto(e);
    }

    public List<AccommodationDto> findByCity(String cityId) {
        ObjectId oid = new ObjectId(cityId);
        return accRepo.findByCityId(oid).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /** 新：按城市名称查所有住宿 */
    public List<AccommodationDto> findByCityName(String cityName) {
        ObjectId cid = new ObjectId(cityService.findByNameOrThrow(cityName).getId());
        return accRepo.findByCityId(cid).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}

