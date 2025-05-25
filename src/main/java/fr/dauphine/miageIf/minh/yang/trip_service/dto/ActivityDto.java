package fr.dauphine.miageIf.minh.yang.trip_service.dto;

public class ActivityDto {
    private String activityId;
    private Integer order;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
