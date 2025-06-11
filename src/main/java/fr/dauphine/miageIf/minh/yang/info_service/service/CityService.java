package fr.dauphine.miageIf.minh.yang.info_service.service;

import fr.dauphine.miageIf.minh.yang.info_service.dao.CityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceConflictException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.CityMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepo;
    private final CityMapper cityMapper;

    public CityDto create(CityUpdateOrCreateDto dto) {
        // MapStruct 生成的 toEntity 会给你一个没有 id 的实体
        City entity = cityMapper.toEntity(dto);
        City saved = cityRepo.save(entity);              // MongoDB 会分配 ObjectId
        return cityMapper.toDto(saved);
    }

    public CityDto update(String id, @Valid CityUpdateOrCreateDto cityUpdateOrCreateDto) {
        City existing = cityRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
        cityMapper.updateEntityFromDto(cityUpdateOrCreateDto, existing);
        City saved = cityRepo.save(existing);
        return cityMapper.toDto(saved);
    }

    public void delete(String id) {
        if (!cityRepo.existsById(id)) throw new ResourceNotFoundException(id);
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
}
