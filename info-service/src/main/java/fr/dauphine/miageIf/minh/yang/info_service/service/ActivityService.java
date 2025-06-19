package fr.dauphine.miageIf.minh.yang.info_service.service;

import com.mongodb.DuplicateKeyException;
import fr.dauphine.miageIf.minh.yang.info_service.dao.ActivityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.PointOfInterestRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ConflictException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.ActivityMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.Activity;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository actRepo;
    private final PointOfInterestRepository poiRepo;
    @Qualifier("activityMapperImpl")
    private final ActivityMapper mapper;
    private final PointOfInterestService poiService;

    public ActivityDto create(ActivityUpdateOrCreateDto dto) {
        // 检查 pointOfInterestId 引用是否存在
        if (!poiRepo.existsById(dto.getPointOfInterestId())) {
            throw new ResourceNotFoundException("PointOfInterest not found: " + dto.getPointOfInterestId());
        }
        if (actRepo.existsByName(dto.getName())) {
            throw new ConflictException("Activity name must be unique: " + dto.getName());
        }
        try{
            Activity saved = actRepo.save(mapper.toEntity(dto));
            return mapper.toDto(saved);
        } catch (DuplicateKeyException ex){
            throw new ConflictException("Activity name must be unique: " + dto.getName());
        }
    }

    public ActivityDto update(String id, ActivityUpdateOrCreateDto dto) {
        Activity existing = actRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id));
        // 1) 名称唯一检查（改名才检查）
        if (!existing.getName().equals(dto.getName()) && actRepo.existsByName(dto.getName())) {
            throw new ConflictException("Activity name must be unique: " + dto.getName());
        }
        // 2) 外键 poi 校验
        if (!poiRepo.existsById(dto.getPointOfInterestId())) {
            throw new ResourceNotFoundException("PointOfInterest not found: " + dto.getPointOfInterestId());
        }
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

    /** 新：按城市名称查所有活动 */
    public List<ActivityDto> findByCityName(String cityName) {
        // 先拿到这个城市的所有 POI
        List<PointOfInterestDto> pois = poiService.findByCityName(cityName);
        List<ObjectId> poiIds = pois.stream()
                .map(dto -> new ObjectId(dto.getId()))
                .collect(Collectors.toList());
        // 再批量查活动
        return actRepo.findByPointOfInterestIdIn(poiIds).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}

