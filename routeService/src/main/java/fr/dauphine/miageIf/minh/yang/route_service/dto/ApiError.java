package fr.dauphine.miageIf.minh.yang.route_service.dto;


import java.time.LocalDateTime;

/**
 * 统一的错误消息格式，用来在异常处理器里返回给客户端
 */
public class ApiError {
    private int status;
    private String error;       // HTTP Reason Phrase，比如 "Not Found"、"Conflict"
    private String message;     // 详细的异常消息
    private String timestamp;   // 出错时间戳（字符串形式）

    public ApiError() { }

    public ApiError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Getter & Setter 省略
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
