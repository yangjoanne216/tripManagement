package fr.dauphine.miageIf.minh.yang.trip_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ApiError {
    private int status;
    private String error;     // HTTP Reason Phrase
    private String message;   // 详细消息
    private String timestamp; // 时间戳
    private Map<String,String> details; // 可选：字段级错误

    public ApiError() {}

    public ApiError(int status, String error, String message) {
        this.status    = status;
        this.error     = error;
        this.message   = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    // getters / setters omitted for brevity

    public Map<String,String> getDetails() { return details; }
    public void setDetails(Map<String,String> details) { this.details = details; }
}
