package fr.dauphine.miageIf.minh.yang.route_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * GET /cities/{cityId}/neighbors 返回的单个邻居对象。
 * 包含：目标 cityId、name，以及关联关系的 distanceKm、travelTimeMin。
 */
@Data
@AllArgsConstructor
public class NeighborDto {
    private String cityId;
    private String name;
    private int distanceKm;
    private int travelTimeMin;
}