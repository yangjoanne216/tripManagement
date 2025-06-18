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

    public PointOfInterestDto create(PointOfInterestUpdateOrCreateDto dto) {
        // 1) 外键校验：cityId 必须存在，否则 404
        if (!cityRepo.existsById(dto.getCityId())) {
            throw new ResourceNotFoundException("City not found: " + dto.getCityId());
        }
        // 2) 唯一索引冲突（如 name + cityId 组合唯一，可自行添加索引）
        //    这里演示捕获 DuplicateKeyException
        try {
            PointOfInterest entity = mapper.toEntity(dto);
            PointOfInterest saved = poiRepo.save(entity);
            return mapper.toDto(saved);
        } catch (DuplicateKeyException ex) {
            throw new ConflictException("A POI with the same unique key already exists.");
        }
    }

    public PointOfInterestDto update(String id, PointOfInterestUpdateOrCreateDto dto) {
        // 1) 外键校验
        if (!cityRepo.existsById(dto.getCityId())) {
            throw new ResourceNotFoundException("City not found: " + dto.getCityId());
        }
        // 2) 确保被更新记录存在
        PointOfInterest existing = poiRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Point of interest not found: " + id));
        // 3) 处理唯一索引冲突
        try {
            mapper.updateEntityFromDto(dto, existing);
            PointOfInterest saved = poiRepo.save(existing);
            return mapper.toDto(saved);
        } catch (DuplicateKeyException ex) {
            throw new ConflictException("Updating POI would violate unique constraint.");
        }
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
