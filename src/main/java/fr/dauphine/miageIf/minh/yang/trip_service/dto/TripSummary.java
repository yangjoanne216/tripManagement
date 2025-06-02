package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用于“搜索行程”接口中返回的行程摘要：
 */
@Data
public class TripSummary {
    public Long id;
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;
    /**
     * 行程总天数，等于 (endDate - startDate + 1)
     */
    private Integer daysCount;

    public static TripSummary fromEntity(Trip t) {
        TripSummary dto = new TripSummary();
        dto.id = t.getId();
        dto.name = t.getName();
        dto.startDate = t.getStartDate();
        dto.endDate = t.getEndDate();
        return dto;
    }
}
