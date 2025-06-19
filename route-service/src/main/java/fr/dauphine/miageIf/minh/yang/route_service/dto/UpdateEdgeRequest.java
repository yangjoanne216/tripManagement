package fr.dauphine.miageIf.minh.yang.route_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PUT /edges/{routeId} 所需的请求体：更新已有 LOCATED_AT 关系的 distanceKm 或 travelTimeMin
 * 可以仅传其中一个字段
 */
@Data
@NoArgsConstructor
public class UpdateEdgeRequest {
    private Integer distanceKm;
    private Integer travelTimeMin;
}
