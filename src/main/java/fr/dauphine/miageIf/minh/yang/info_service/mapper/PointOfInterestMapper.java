package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.PointOfInterestUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import fr.dauphine.miageIf.minh.yang.info_service.model.PointOfInterest;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PointOfInterestMapper {
    /**
     * 字符串 cityId -> ObjectId
     */
    default ObjectId map(String id) {
        return id != null ? new ObjectId(id) : null;
    }

    /**
     * ObjectId -> 字符串 cityId
     */
    default String map(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }

    /**
     * DTO -> Entity，自动使用 map(String) 转换
     */
    PointOfInterest toEntity(PointOfInterestUpdateOrCreateDto dto);

    /**
     * Entity -> DTO，自动使用 map(ObjectId) 转换
     */
    PointOfInterestDto toDto(PointOfInterest entity);

    /**
     * 更新实体时，同样自动映射 cityId
     */
    void updateEntityFromDto(PointOfInterestUpdateOrCreateDto dto, @MappingTarget PointOfInterest entity);
}
