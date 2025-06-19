package fr.dauphine.miageIf.minh.yang.info_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class ApiError {
    private int status;
    private String error;       // HTTP Reason Phrase，比如 "Not Found"、"Conflict"
    private String message;     // 详细的异常消息
    private String timestamp;   // 出错时间戳（字符串形式）

    public ApiError(String message) {
        this.message = message;
    }
    public ApiError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }


}
