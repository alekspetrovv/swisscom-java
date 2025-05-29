package com.swisscom.crud.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleRecordNotFoundException(RecordNotFoundException e, ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        logger.error(e.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ErrorResponse(status.value(), status.getReasonPhrase(), e.getMessage(), path);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingConflict(
            OptimisticLockingFailureException ex, ServerWebExchange exchange) {
        logger.warn("Optimistic locking conflict for path {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse apiErrorResponse = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                status.getReasonPhrase(),
                exchange.getRequest().getPath().value()
        );
        return new ResponseEntity<>(apiErrorResponse, status);
    }


    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodNotValidException(WebExchangeBindException e, ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        logger.error(e.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<ValidationErrorDetails> errorDetails = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            ValidationErrorDetails validationErrorDetails = new ValidationErrorDetails(fieldError.getField(), fieldError.getDefaultMessage());
            errorDetails.add(validationErrorDetails);
        }

        return new ErrorResponse(status.value(), status.getReasonPhrase(), e.getBody().getDetail(), path, errorDetails);
    }
}
