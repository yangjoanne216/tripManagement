package fr.dauphine.miageIf.minh.yang.trip_service.exceptions;

/**
 * Service 层找不到 Trip 时抛出。
 * 全局异常处理器映射成 404。
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
