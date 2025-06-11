package fr.dauphine.miageIf.minh.yang.info_service.controller.handler;

import fr.dauphine.miageIf.minh.yang.info_service.exceptions.BadRequestException;
import fr.dauphine.miageIf.minh.yang.info_service.exceptions.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(ResourceNotFoundException ex){
        Map<String,Object> b= Map.of("status",404,"error","Not Found","message",ex.getMessage());
        return ResponseEntity.status(404).body(b);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String,Object>> handleBadReq(BadRequestException ex){
        return ResponseEntity.status(400).body(Map.of("status",400,"error","Bad Request","message",ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleVal(MethodArgumentNotValidException ex){
        var fld= new HashMap<String,String>();
        ex.getBindingResult().getFieldErrors().forEach(fe->fld.put(fe.getField(),fe.getDefaultMessage()));
        Map<String,Object> b=new HashMap<>(); b.put("status",400); b.put("error","Validation Failed"); b.put("fields",fld);
        return ResponseEntity.badRequest().body(b);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> handleJsonErr(HttpMessageNotReadableException ex){
        return ResponseEntity.badRequest().body(Map.of("status",400,"error","Malformed JSON","message",ex.getMessage()));
    }
}
