package com.sam.ross.smoovie.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ServiceProxyException.class)
    public ResponseEntity handleServiceProxyException(ServiceProxyException e) {
        return ResponseEntity
                .status(e.httpStatus)
                .body(e.message);
    }

    @ExceptionHandler(EmptyResponseException.class)
    public ResponseEntity handleEmptyResponseException(EmptyResponseException e) {
        return ResponseEntity
                .status(204)
                .body(null);    // 204 responses don't contain a body
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity handleGeneralException(GeneralException e) {
        return ResponseEntity
                .status(500)
                .body(e.message);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleRunTimeExceptions(RuntimeException e) {
        return ResponseEntity
                .status(500)
                .body("General runtime exception thrown: " + e.getMessage());
    }
}
