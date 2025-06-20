package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.CityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CityMapper {
    /**
     * 纯属性映射，自动映射 id, name, photos, geoInfo
     */
    CityDto toDto(City entity);

    /**
     * DTO -> Entity，自动映射 name, photos, geoInfo
     */
    City toEntity(CityUpdateOrCreateDto dto);

    /**
     * 更新实体的基本属性
     */
    void updateEntityFromDto(CityUpdateOrCreateDto dto, @MappingTarget City entity);
}

