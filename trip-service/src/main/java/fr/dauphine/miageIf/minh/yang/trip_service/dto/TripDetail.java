package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用于在“获取单个行程详情”接口中完整返回某个行程的结构：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDetail {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TripDayDto> days;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripDayDto {
        private int day;                         // 第几天，从 1 开始
        private String cityName;                // 当天所在城市名称
        private String accommodationName;       // 当天住宿名称
        private List<String> activityNames;     // 当天所有活动名称，按顺序
        private ToNext toNext;                  // 当天到下一天城市的距离/时间（最后一天为 null）
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToNext {
        private double distanceKm;              // 相邻两城公里数
        private int travelTimeMin;              // 预计分钟数
    }


}

