package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.PointOfInterest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PointOfInterestMapper {
    PointOfInterestMapper INSTANCE = Mappers.getMapper(PointOfInterestMapper.class);
    PointOfInterestDto toDto(PointOfInterest entity);
    PointOfInterest toEntity(PointOfInterestDto dto);
}
