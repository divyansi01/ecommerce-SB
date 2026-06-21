package com.ecommerce.exception;

import com.ecommerce.model.ErrorDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDetail> handleResponseStatusException(final ResponseStatusException exception){
        ErrorDetail errorDetail = new ErrorDetail();
        if(exception.getCause() != null)
            errorDetail.setMessage(exception.getCause().getMessage());
        else
            errorDetail.setMessage(exception.getMessage());
        errorDetail.setReason(exception.getReason());
        return ResponseEntity.status(exception.getStatusCode()).body(errorDetail);
    }
}
