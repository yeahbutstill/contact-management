package com.yeahbutstill.restful.controller.advice;

import com.yeahbutstill.restful.model.WebResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@RestControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<String>> constraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse
                        .<String>builder()
                        .errors(Collections.singletonList(exception.getMessage()))
                        .build()
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> apiException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(WebResponse
                        .<String>builder()
                        .errors(Collections.singletonList(exception.getReason()))
                        .build()
                );
    }

}
