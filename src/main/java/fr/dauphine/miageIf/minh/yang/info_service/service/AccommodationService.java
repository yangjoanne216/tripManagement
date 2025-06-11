package fr.dauphine.miageIf.minh.yang.info_service.service;

import fr.dauphine.miageIf.minh.yang.info_service.dao.AccommodationRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.AccommodationMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.Accommodation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accRepo;
    private final AccommodationMapper mapper;

    public AccommodationDto create(AccommodationDto dto) {
        Accommodation entity = mapper.toEntity(dto);
        Accommodation saved = accRepo.save(entity);
        return mapper.toDto(saved);
    }

    public AccommodationDto update(String id, AccommodationDto dto) {
        if (!accRepo.existsById(id)) {
            throw new ResourceNotFoundException("Accommodation not found: " + id);
        }
        Accommodation entity = mapper.toEntity(dto);
        entity.setId(id);
        Accommodation saved = accRepo.save(entity);
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
        return accRepo.findByCity_Id(cityId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
