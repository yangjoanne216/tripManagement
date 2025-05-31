package fr.dauphine.miageIf.minh.yang.route_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /cities 所需的请求体：创建一个新的 City 节点
 */
@Data
@NoArgsConstructor
public class CreationCityRequest {
    private String cityId;
    private String name;
}