package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
public class ActivityDto {

    /**
     * 活动 ID
     */

    private String id;

    /**
     * 活动名称
     */
    @NotBlank(message = "name must not be blank")
    private String name;

    /**
     * 活动照片 URL 列表
     */
    private List<String> photos;

    /**
     * 活动开放季节
     */
    private List<String> seasons;

    /**
     * 价格信息
     */
    private PriceDto price;

    /**
     * 关联的兴趣点 ID
     */
    @NotBlank(message = "pointOfInterestId must not be blank")
    private String pointOfInterestId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceDto {
        private double adult;
        private double child;
    }
}
