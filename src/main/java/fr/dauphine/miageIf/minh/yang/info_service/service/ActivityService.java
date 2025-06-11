package fr.dauphine.miageIf.minh.yang.info_service.service;

import fr.dauphine.miageIf.minh.yang.info_service.dao.ActivityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.ActivityMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.Activity;
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

    public ActivityDto create(ActivityDto dto) {
        Activity entity = mapper.toEntity(dto);
        Activity saved = actRepo.save(entity);
        return mapper.toDto(saved);
    }

    public ActivityDto update(String id, ActivityDto dto) {
        if (!actRepo.existsById(id)) {
            throw new ResourceNotFoundException("Activity not found: " + id);
        }
        Activity entity = mapper.toEntity(dto);
        entity.setId(id);
        Activity saved = actRepo.save(entity);
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
