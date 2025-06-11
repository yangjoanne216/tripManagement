package fr.dauphine.miageIf.minh.yang.info_service.mapper;

import fr.dauphine.miageIf.minh.yang.info_service.dto.CityDto;
import fr.dauphine.miageIf.minh.yang.info_service.dto.CityUpdateDto;
import fr.dauphine.miageIf.minh.yang.info_service.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CityMapper {
    CityMapper INSTANCE = Mappers.getMapper(CityMapper.class);

    CityDto toDto(City entity);

    City toEntity(CityDto dto);

    default CityDto addId(String id, CityUpdateDto cityUpdateDto){
        return new CityDto(id,cityUpdateDto.getName(),cityUpdateDto.getGeoInfo(),cityUpdateDto.getPhotos(),cityUpdateDto.getAccommodations(),cityUpdateDto.getPointsOfInterest());
    }
}
