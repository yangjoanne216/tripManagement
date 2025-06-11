package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.Accommodation;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation entity);
    Accommodation toEntity(AccommodationUpdateOrCreateDto dto);
    void updateEntityFromDto(AccommodationUpdateOrCreateDto dto, @MappingTarget Accommodation entity);
}
