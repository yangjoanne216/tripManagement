package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationDto {
    private String id;

    @NotBlank(message = "name must not be blank")
    private String name;

    private String address;
    private List<String> photos;
    private CityDto.AccommodationRefDto city;
    private double price;
    private boolean available;
}

