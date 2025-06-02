package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import fr.dauphine.miageIf.minh.yang.trip_service.model.Trip;
import fr.dauphine.miageIf.minh.yang.trip_service.model.TripAccommodation;
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
    public Long id;
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;
    //public Map<Integer, List<Activity>> dailyActivities;
    //public Map<Integer, String> dailyAccommodations;
    /**
     * 行程中每一天的安排列表。列表中的第一个元素 day = 1 表示行程第一天。
     * 如果某天没有任何活动或未安排住宿，DayDto 对应字段可以为空或空列表。
     */
    private List<DayDto> days;

    /*public static class Activity {
        public Integer sequence;
        public String activityId;
    }

    public static TripDetail fromEntity(Trip t) {
        TripDetail dto = new TripDetail();
        dto.id = t.getId();
        dto.name = t.getName();
        dto.startDate = t.getStartDate();
        dto.endDate = t.getEndDate();

        dto.dailyActivities = t.getActivities().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getId().getDay(),
                        Collectors.mapping(a -> {
                            Activity act = new Activity();
                            act.sequence = a.getId().getSequence();
                            act.activityId = a.getActivityId();
                            return act;
                        }, Collectors.toList())
                ));

        dto.dailyAccommodations = t.getAccommodations().stream()
                .collect(Collectors.toMap(
                        a -> a.getId().getDay(),
                        TripAccommodation::getAccommodationId
                ));

        return dto;
    }*/
}

