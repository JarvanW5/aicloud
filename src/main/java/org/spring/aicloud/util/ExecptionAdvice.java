package org.spring.aicloud.util;


import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author: JarvanW
 * @Date: 2024/7/31
 * @Description:
 * @Requirements:
 */

@RestControllerAdvice
public class ExecptionAdvice {

    @ExceptionHandler(BindException.class)
    public ResponseEntity handleBindException(BindException e) {
        return ResponseEntity.error(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        return ResponseEntity.error(e.getMessage());
    }
}
