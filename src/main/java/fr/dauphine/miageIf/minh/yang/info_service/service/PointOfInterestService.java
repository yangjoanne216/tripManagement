package fr.dauphine.miageIf.minh.yang.info_service.service;

import com.mongodb.DuplicateKeyException;
import fr.dauphine.miageIf.minh.yang.info_service.dao.ActivityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.CityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dao.PointOfInterestRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.BadRequestException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ConflictException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.PointOfInterestMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import fr.dauphine.miageIf.minh.yang.info_service.model.PointOfInterest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointOfInterestService {
    private final PointOfInterestRepository poiRepo;
    private final ActivityRepository actRepo;
    private final CityRepository cityRepo;
    @Qualifier("pointOfInterestMapperImpl")
    private final PointOfInterestMapper mapper;

    private final CityService cityService;

    public PointOfInterestDto create(PointOfInterestUpdateOrCreateDto dto) {    // 1) 名称唯一检查
        if (poiRepo.existsByName(dto.getName())) {
            throw new ConflictException("PointOfInterest name must be unique: " + dto.getName());
        }
        // 2) 外键 city 校验
        if (!cityRepo.existsById(dto.getCityId())) {
            throw new ResourceNotFoundException("City not found: " + dto.getCityId());
        }
        PointOfInterest entity = mapper.toEntity(dto);
        PointOfInterest saved = poiRepo.save(entity);
        return mapper.toDto(saved);
    }

    public PointOfInterestDto update(String id, PointOfInterestUpdateOrCreateDto dto) {
        PointOfInterest existing = poiRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Point of interest not found: " + id));

        // 1) 名称唯一检查（改名才检查）
        if (!existing.getName().equals(dto.getName()) && poiRepo.existsByName(dto.getName())) {
            throw new ConflictException("PointOfInterest name must be unique: " + dto.getName());
        }
        // 2) 外键 city 校验
        if (!cityRepo.existsById(dto.getCityId())) {
            throw new ResourceNotFoundException("City not found: " + dto.getCityId());
        }

        mapper.updateEntityFromDto(dto, existing);
        PointOfInterest saved = poiRepo.save(existing);
        return mapper.toDto(saved);
    }

    public void delete(String id) {
        if (!poiRepo.existsById(id)) {
            throw new ResourceNotFoundException("Point of interest not found: " + id);
        }
        // 有活动则拒绝删除
        ObjectId poiOid = new ObjectId(id);
        if (!actRepo.findByPointOfInterestIdIn(List.of(poiOid)).isEmpty()) {
            throw new BadRequestException("Cannot delete point of interest: activities still exist");
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
                .orElseThrow(() -> new ResourceNotFoundException("Point of interest not found: " + id));
        return mapper.toDto(e);
    }

    public List<PointOfInterestDto> findByCity(String cityId) {
        // 外键校验（可选），或者直接返回空列表
        if (!cityRepo.existsById(cityId)) {
            throw new ResourceNotFoundException("City not found: " + cityId);
        }
        ObjectId oid = new ObjectId(cityId);
        return poiRepo.findByCityId(oid).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /** 新：按城市名称查所有兴趣点 */
    public List<PointOfInterestDto> findByCityName(String cityName) {
        ObjectId cid = new ObjectId(cityService.findByNameOrThrow(cityName).getId());
        return poiRepo.findByCityId(cid).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
