package fr.dauphine.miageIf.minh.yang.info_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AccommodationUpdateOrCreateDto {
    @NotBlank(message = "name must not be blank")
    private String name;

    @NotBlank(message = "address must not be blank")
    private String address;

    @Size(min = 1, message = "photos must contain at least one URL")
    private List<
            @Pattern(
                    regexp = "^https?://.*",
                    message = "each photo URL must start with http:// or https://"
            )
                    String
            > photos;

    @Min(value = 0, message = "price must be ≥ 0")
    private double price;

    private boolean available;

    @NotBlank(message = "cityId must not be blank")
    private String cityId;
}

