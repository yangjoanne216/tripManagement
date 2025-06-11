package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.PointOfInterest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointOfInterestMapper {
    PointOfInterestDto toDto(PointOfInterest entity);
    PointOfInterest toEntity(PointOfInterestDto dto);
}
