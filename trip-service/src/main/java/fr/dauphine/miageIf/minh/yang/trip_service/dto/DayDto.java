package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DayDto {

    /**
     * 行程中的第几天，从 1 开始计数，必须为正整数（正号整数）。
     */
    @NotNull(message = "day must not be null")
    @Positive(message = "day must be a positive integer")
    private Integer day;

    /**
     * 不可以为空，表示当天没有安排住宿。
     */
    @NotBlank(message = "accommodation must not be null")
    private String accommodationId;

    /**
     * 当天安排的所有活动列表。可以为 null 或空列表。
     * 如果不为 null，则其中的每个 ActivityDto 都要被校验（@Valid）。
     */
    @Valid
    private List<ActivityDto> activities;

}
