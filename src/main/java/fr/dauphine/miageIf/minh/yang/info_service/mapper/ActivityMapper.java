package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.ActivityDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.Activity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityDto toDto(Activity entity);
    Activity toEntity(ActivityDto dto);
}
