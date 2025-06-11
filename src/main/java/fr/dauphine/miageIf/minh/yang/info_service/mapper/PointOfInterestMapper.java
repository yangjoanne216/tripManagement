package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import fr.dauphine.miageIf.minh.yang.info_service.model.PointOfInterest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PointOfInterestMapper {
    PointOfInterestMapper INSTANCE = Mappers.getMapper(PointOfInterestMapper.class);
    PointOfInterestDto toDto(PointOfInterest entity);
    PointOfInterest toEntity(PointOfInterestUpdateOrCreateDto dto);
    void updateEntityFromDto(PointOfInterestUpdateOrCreateDto dto, @MappingTarget PointOfInterest entity);
}
