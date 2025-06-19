package fr.dauphine.miageIf.minh.yang.route_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 边（Edge）CRUD 相关的响应体。包含 Neo4j 内部关系 ID (routeId)、两个节点 ID 及属性。
 */
@Data
@AllArgsConstructor
public class EdgeResponse {
    private Long routeId;
    private String sourceCityId;
    private String destinationCityId;
    private double distanceKm;
    private int travelTimeMin;
}
