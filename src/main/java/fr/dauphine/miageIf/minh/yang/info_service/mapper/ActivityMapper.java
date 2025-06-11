package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.Activity;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityDto toDto(Activity entity);
    Activity toEntity(ActivityUpdateOrCreateDto dto);
    void updateEntityFromDto(ActivityUpdateOrCreateDto dto, @MappingTarget Activity entity);
}
