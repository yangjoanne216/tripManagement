package fr.dauphine.miageIf.minh.yang.route_service.controller.handle;

import fr.dauphine.miageIf.minh.yang.route_service.controller.CityController;
import fr.dauphine.miageIf.minh.yang.route_service.controller.EdgeController;
import fr.dauphine.miageIf.minh.yang.route_service.dto.ApiError;
import fr.dauphine.miageIf.minh.yang.route_service.exceptions.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一捕获并处理各类自定义异常，转换成合适的 HTTP Status + ApiError JSON。
 */
@Hidden
@ControllerAdvice(assignableTypes = {
        CityController.class,
        EdgeController.class
})
public class GlobalDefaultExceptionHandler {

    // 404: City Not Found
    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ApiError> handleCityNotFound(CityNotFoundException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 404: Edge Not Found
    @ExceptionHandler(EdgeNotFoundException.class)
    public ResponseEntity<ApiError> handleEdgeNotFound(EdgeNotFoundException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 409: City Already Exists
    @ExceptionHandler(CityAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleCityAlreadyExists(CityAlreadyExistsException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 409: Edge Already Exists
    @ExceptionHandler(EdgeAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEdgeAlreadyExists(EdgeAlreadyExistsException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 400: City 数据或参数不合法
    @ExceptionHandler(InvalidCityDataException.class)
    public ResponseEntity<ApiError> handleInvalidCityData(InvalidCityDataException ex) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 400: Edge 属性不合法
    @ExceptionHandler(InvalidEdgePropertiesException.class)
    public ResponseEntity<ApiError> handleInvalidEdgeProperties(InvalidEdgePropertiesException ex) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 400: 通用参数校验/BadRequest
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 409: 通用资源冲突（删除冲突、循环冲突等）
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiError> handleResourceConflict(ResourceConflictException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 503: 服务不可用（例如 Neo4j 宕机、第三方依赖不可用等）
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(ServiceUnavailableException ex) {
        ApiError error = new ApiError(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // 处理所有未被捕获的 RuntimeException，返回 HTTP 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUnhandledExceptions(Exception ex) {
        // 这里可以打印日志：ex.printStackTrace() 或者 logger.error("...", ex);
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}