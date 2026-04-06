package com.project.backend.handler;

import com.project.backend.dto.Response;
import com.project.backend.exception.NoRefreshAvailable;
import com.project.backend.exception.ResourceNotFound;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class FinStockExecHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Response> handlerResourceNotFound (ResourceNotFound e) {
        Response response = new Response();
        response.setStatus(404);
        response.setMessage(e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(NoRefreshAvailable.class)
    public ResponseEntity<Response> handlerRefreshNotAvailable (NoRefreshAvailable e) {
        Response response = new Response();
        response.setStatus(404);
        response.setMessage(e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Response> handlerMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Response response = new Response();
        response.setStatus(400);
        response.setMessage(e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handlerDataIntegrityViolation(DataIntegrityViolationException e) {
        Response response = new Response();
        response.setStatus(400);
        response.setMessage(e.getMessage());
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
