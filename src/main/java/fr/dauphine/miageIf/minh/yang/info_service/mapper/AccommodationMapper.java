package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.AccommodationUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateOrCreateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.Accommodation;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AccommodationMapper {
    /** String → ObjectId */
    default ObjectId map(String id) {
        return id != null ? new ObjectId(id) : null;
    }
    /** ObjectId → String */
    default String map(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }

    Accommodation toEntity(AccommodationUpdateOrCreateDto dto);
    AccommodationDto toDto(Accommodation entity);
    void updateEntityFromDto(AccommodationUpdateOrCreateDto dto, @MappingTarget Accommodation entity);
}
