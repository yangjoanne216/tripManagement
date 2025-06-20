package fr.dauphine.miageIf.minh.yang.trip_service.controller.handler;

import fr.dauphine.miageIf.minh.yang.trip_service.dto.ApiError;
import fr.dauphine.miageIf.minh.yang.trip_service.exceptions.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandle {
    // 404: Trip 不存在
    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(TripNotFoundException ex) {
        ApiError err = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    //404:Accommodation/Activity/POI/CITY 不存在
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        ApiError err = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    // 400: DTO 校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                errors.put(fe.getField(), fe.getDefaultMessage())
        );
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed"
        );
        err.setDetails(errors);
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(IncompleteInputDataException.class)
    public ResponseEntity<ApiError> handleValidation(IncompleteInputDataException ex) {
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(err);
    }

    // 400: 参数类型不匹配
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format(
                "Parameter '%s' is invalid: expected type %s but got '%s'",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown",
                ex.getValue()
        );
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                msg
        );
        return ResponseEntity.badRequest().body(err);
    }

    // 400: 迷你模糊
    @ExceptionHandler(AmbiguousNameException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(AmbiguousNameException ex) {
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(err);
    }

    // 400: 业务校验失败
    @ExceptionHandler(InvalidTripDataException.class)
    public ResponseEntity<ApiError> handleInvalidTrip(InvalidTripDataException ex) {
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(err);
    }

    // 409: 冲突（重复创建等）
    @ExceptionHandler(TripAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleConflict(TripAlreadyExistsException ex) {
        ApiError err = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }
    // 400: JSON 语法错误，比如输入非 JSON 或严重格式有问题
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(HttpMessageNotReadableException ex) {
        String msg = "Malformed JSON: ";
        Throwable cause = ex.getMostSpecificCause();
        if (cause != null) {
            msg += cause.getMessage();
        } else {
            msg += ex.getMessage();
        }
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                msg
        );
        return ResponseEntity.badRequest().body(err);
    }

    // 400: 传了不认识的字段（unrecognized field）
    @ExceptionHandler(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class)
    public ResponseEntity<ApiError> handleUnrecognizedField(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException ex) {
        String field = ex.getPropertyName();
        String msg = String.format("Unrecognized field '%s'. Please remove or correct it.", field);
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                msg
        );
        return ResponseEntity.badRequest().body(err);
    }

    // 400: 字段类型不匹配，比如把字符串传给日期、数字等
    @ExceptionHandler(com.fasterxml.jackson.databind.exc.InvalidFormatException.class)
    public ResponseEntity<ApiError> handleInvalidFormat(com.fasterxml.jackson.databind.exc.InvalidFormatException ex) {
        String field = ex.getPath().stream()
                .map(ref -> ref.getFieldName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining("."));
        String targetType = ex.getTargetType().getSimpleName();
        String msg = String.format("Invalid value for field '%s': expected type %s.", field, targetType);
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                msg
        );
        return ResponseEntity.badRequest().body(err);
    }

    // 503: 外部服务不可用
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(ServiceUnavailableException ex) {
        ApiError err = new ApiError(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err);
    }
    // 500: DB 未知约束或其他内部错误
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        // 记录日志后，返回泛化提示
        ApiError err = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal database error, please contact support"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
    // 500: 兜底
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {
        ApiError err = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }


}
