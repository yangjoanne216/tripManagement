package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.Activity;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    /** String → ObjectId */
    default ObjectId map(String id) {
        return id != null ? new ObjectId(id) : null;
    }
    /** ObjectId → String */
    default String map(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }

    Activity toEntity(ActivityUpdateOrCreateDto dto);
    ActivityDto toDto(Activity entity);
    void updateEntityFromDto(ActivityUpdateOrCreateDto dto, @MappingTarget Activity entity);
}
