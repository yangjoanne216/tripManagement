package fr.dauphine.miageIf.minh.yang.route_service.controller.handle;

import fr.dauphine.miageIf.minh.yang.route_service.controller.CityController;
import fr.dauphine.miageIf.minh.yang.route_service.controller.EdgeController;
import fr.dauphine.miageIf.minh.yang.route_service.dto.ApiError;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.neo4j.driver.exceptions.ClientException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 统一捕获并处理各类自定义异常，转换成合适的 HTTP Status + ApiError JSON。
 */
@Hidden
@ControllerAdvice(assignableTypes = {
        CityController.class,
        EdgeController.class
})
@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

    // 404: City Not Found
    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ApiError> handleCityNotFound(CityNotFoundException ex) {
        ApiError err = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    // 404: Edge Not Found
    @ExceptionHandler(EdgeNotFoundException.class)
    public ResponseEntity<ApiError> handleEdgeNotFound(EdgeNotFoundException ex) {
        ApiError err = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    // 409: City Already Exists
    @ExceptionHandler(CityAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleCityAlreadyExists(CityAlreadyExistsException ex) {
        ApiError err = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    // 409: Edge Already Exists
    @ExceptionHandler(EdgeAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEdgeAlreadyExists(EdgeAlreadyExistsException ex) {
        ApiError err = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    // 409: Neo4j 唯一性或其他约束冲突
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable root = ex.getRootCause();
        if (root instanceof ClientException
                && root.getMessage().contains("ConstraintValidationFailed")) {
            ApiError err = new ApiError(
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase(),
                    "Database constraint violation: " + root.getMessage()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
        // 不是约束冲突则继续抛出，让最下面的 Exception 处理
        throw ex;
    }

    // 400: JSON 反序列化失败（类型错误），如 latitude/longitude 不是 double
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonParseError(HttpMessageNotReadableException ex) {
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Malformed JSON or invalid field type: " + ex.getMostSpecificCause().getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // 400: 方法参数类型不匹配，比如请求参数类型错误
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // 400: City 数据或参数不合法
    @ExceptionHandler(InvalidCityDataException.class)
    public ResponseEntity<ApiError> handleInvalidCityData(InvalidCityDataException ex) {
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // 400: Edge 属性不合法
    @ExceptionHandler(InvalidEdgePropertiesException.class)
    public ResponseEntity<ApiError> handleInvalidEdgeProperties(InvalidEdgePropertiesException ex) {
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // 400: 通用参数校验/BadRequest
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // 409: 通用资源冲突（删除冲突、循环冲突等）
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiError> handleResourceConflict(ResourceConflictException ex) {
        ApiError err = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    // 503: 服务不可用（Neo4j 宕机、第三方依赖不可用等）
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(ServiceUnavailableException ex) {
        ApiError err = new ApiError(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err);
    }

    // 处理所有未被捕获的 Exception，返回 HTTP 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUnhandledExceptions(Exception ex) {
        // 可记录日志：logger.error("Unhandled exception", ex);
        ApiError err = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

}






