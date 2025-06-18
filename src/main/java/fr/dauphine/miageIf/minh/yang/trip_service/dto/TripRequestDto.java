package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TripRequestDto {


    /**
     * 行程名称，例如 “Montreal & Toronto Tour”。
     * 不可为 null 或空白，最小长度 1，最大长度 200（示例）。
     */
    @NotBlank(message = "name must not be blank")
    @Size(max = 200, message = "name length must not exceed 200 characters")
    private String name;

    /**
     * 行程开始日期，格式为 YYYY-MM-DD，不能为空，且必须是今天或将来的日期。
     */
    @NotNull(message = "startDate must not be null")
    @FutureOrPresent(message = "startDate must be today or a future date")
    private LocalDate startDate;

    /**
     * 行程结束日期，不能为空，且必须 >= startDate。
     * 在 Service 层进行“endDate >= startDate”的校验。
     */
    @NotNull(message = "endDate must not be null")
    private LocalDate endDate;

    /**
     * 行程中每天的详细安排列表。列表可以为空或 null，代表不修改 day 列表。
     * 如果前端传来不为 null，则其 size = (endDate - startDate + 1)。
     * 同时，这个 List 内部的每个 DayDto 都会被递归校验（@Valid）。
     */
    //@Size(min = 0, message = "days list size must be ≥ 0")
    //@Valid
    //private List<DayDto> days;
    private List<TripDayInput> days;

    @Data
    public static class TripDayInput {
        private String cityName;
        private String accommodationName;
        private List<String> activityNames;
    }
}
