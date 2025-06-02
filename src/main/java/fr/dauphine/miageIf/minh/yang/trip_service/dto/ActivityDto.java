package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ActivityDto {

    /**
     * 活动的唯一标识字符串，不可为空或空白。
     */
    @NotBlank(message = "activityId must not be blank")
    private String activityId;

    /**
     * 活动在当天的排序序号，从 1 开始。必须不为 null 且 ≥ 1。
     */
    @NotNull(message = "sequence must not be null")
    @Min(value = 1, message = "sequence must be at least 1")
    private Integer sequence;

}
