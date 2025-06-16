package fr.dauphine.miageIf.minh.yang.route_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /cities 所需的请求体：创建一个新的 City 节点
 */
@Data
@NoArgsConstructor
public class CreationCityRequest {
    @NotBlank
    private String cityId;
    @NotBlank
    private String name;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}