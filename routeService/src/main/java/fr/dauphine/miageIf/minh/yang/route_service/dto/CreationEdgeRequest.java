package fr.dauphine.miageIf.minh.yang.route_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /edges 所需的请求体：在两个 City 之间创建一条 LOCATED_AT 关系
 */
@Data
@NoArgsConstructor
public class CreationEdgeRequest {
    private String sourceCityId;
    private String destinationCityId;
}