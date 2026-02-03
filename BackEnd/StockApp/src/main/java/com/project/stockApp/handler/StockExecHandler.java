package com.project.stockApp.handler;

import com.project.stockApp.dto.Response;
import com.project.stockApp.exception.NoRefreshAvailable;
import com.project.stockApp.exception.ResourceNotFound;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StockExecHandler {

    @ExceptionHandler
    public ResponseEntity<Response> handlerResourceNotFound (ResourceNotFound e) {
        Response response = new Response();
        response.setHttpCode(404);
        response.setMessage(e.getMessage());
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<Response> handlerRefreshNotAvailable (NoRefreshAvailable e) {
        Response response = new Response();
        response.setHttpCode(404);
        response.setMessage(e.getMessage());
        return ResponseEntity.status(response.getHttpCode()).body(response);
    }
}
