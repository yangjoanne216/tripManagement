package fr.dauphine.miageIf.minh.yang.info_service.service;

import fr.dauphine.miageIf.minh.yang.info_service.dao.CityRepository;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateDto;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import fr.dauphine.miageIf.minh.yang.info_service.mapper.CityMapper;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepo;
    private final CityMapper cityMapper;

    public CityDto create(CityDto dto) {
        City entity = cityMapper.toEntity(dto);
        entity = cityRepo.save(entity);
        return cityMapper.toDto(entity);
    }

    public CityDto update(String id, @Valid CityUpdateDto cityUpdateDto) {
        City existing = cityRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
        //Set the ID so save() will perform an upsert
        CityDto cityDto = cityMapper.addId(existing.getId(),cityUpdateDto);
        City entity = cityMapper.toEntity(cityDto);
        entity.setId(id);
        City saved = cityRepo.save(entity);
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
