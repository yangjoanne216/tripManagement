package fr.dauphine.miageIf.minh.yang.info_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ActivityUpdateOrCreateDto {
    @NotBlank(message = "name must not be blank")
    private String name;

    @Size(min = 1, message = "photos must contain at least one URL")
    private List<
            @Pattern(
                    regexp = "^https?://.*",
                    message = "each photo URL must start with http:// or https://"
            )
                    String
            > photos;

    @Size(min = 1, message = "seasons must contain at least one season")
    private List<
            @Pattern(
                    // 枚举12个月
                    regexp = "^(January|February|March|April|May|June|July|August|September|October|November|December)$",
                    message = "season must be a valid month name"
            )
                    String
            > seasons;

    @Valid
    private Price price;

    @NotBlank(message = "pointOfInterestId must not be blank")
    private String pointOfInterestId;

    @Data
    public static class Price {
        @Min(value = 0, message = "adult price must be ≥ 0")
        private double adult;

        @Min(value = 0, message = "child price must be ≥ 0")
        private double child;
    }
}