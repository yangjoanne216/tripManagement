package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 用于返回某个 trip 中所有涉及的 activityId & accommodationId 列表（去重）。
 */
//Todo ： return pointsOfInterests
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPointsOfInterestDto {
    /**
     * 去重后的所有 activityId
     */
    private Set<String> activityIds;

    /**
     * 去重后的所有 accommodationId
     */
    private Set<String> accommodationIds;
}
