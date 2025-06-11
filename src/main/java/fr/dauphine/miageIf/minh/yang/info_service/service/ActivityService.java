package fr.dauphine.miageIf.minh.yang.info_service.service;

import fr.dauphine.miageIf.minh.yang.info_service.dao.ActivityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.ActivityMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.Activity;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository actRepo;
    @Qualifier("activityMapperImpl")
    private final ActivityMapper mapper;

    public ActivityDto create(ActivityUpdateOrCreateDto dto) {
        Activity entity = mapper.toEntity(dto);
        Activity saved = actRepo.save(entity);
        return mapper.toDto(saved);
    }

    public ActivityDto update(String id, ActivityUpdateOrCreateDto dto) {
        Activity existing = actRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id));
        mapper.updateEntityFromDto(dto, existing);
        Activity saved = actRepo.save(existing);
        return mapper.toDto(saved);
    }

    public void delete(String id) {
        if (!actRepo.existsById(id)) {
            throw new ResourceNotFoundException("Activity not found: " + id);
        }
        actRepo.deleteById(id);
    }

    public List<ActivityDto> findAll() {
        return actRepo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ActivityDto findById(String id) {
        Activity e = actRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id));
        return mapper.toDto(e);
    }
}
