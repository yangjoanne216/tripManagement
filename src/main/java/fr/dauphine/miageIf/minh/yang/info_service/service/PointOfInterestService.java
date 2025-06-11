package fr.dauphine.miageIf.minh.yang.info_service.service;

import fr.dauphine.miageIf.minh.yang.info_service.dao.PointOfInterestRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.PointOfInterestMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.PointOfInterest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointOfInterestService {

    private final PointOfInterestRepository poiRepo;
    private final PointOfInterestMapper mapper;

    public PointOfInterestDto create(PointOfInterestDto dto) {
        PointOfInterest entity = mapper.toEntity(dto);
        PointOfInterest saved = poiRepo.save(entity);
        return mapper.toDto(saved);
    }

    public PointOfInterestDto update(String id, PointOfInterestDto dto) {
        if (!poiRepo.existsById(id)) {
            throw new ResourceNotFoundException("POI not found: " + id);
        }
        PointOfInterest entity = mapper.toEntity(dto);
        entity.setId(id);
        PointOfInterest saved = poiRepo.save(entity);
        return mapper.toDto(saved);
    }

    public void delete(String id) {
        if (!poiRepo.existsById(id)) {
            throw new ResourceNotFoundException("POI not found: " + id);
        }
        poiRepo.deleteById(id);
    }

    public List<PointOfInterestDto> findAll() {
        return poiRepo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public PointOfInterestDto findById(String id) {
        PointOfInterest e = poiRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("POI not found: " + id));
        return mapper.toDto(e);
    }

    public List<PointOfInterestDto> findByCity(String cityId) {
        return poiRepo.findByCity_Id(cityId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
