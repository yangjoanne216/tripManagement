package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用于“搜索行程”接口中返回的行程摘要：
 */
@AllArgsConstructor
@Data
public class TripSummary {
    public Long id;
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;
    private int dayCount;

}
