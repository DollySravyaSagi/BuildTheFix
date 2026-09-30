package com.buildthefix.app.exception;

import com.buildthefix.app.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler catching custom and system exceptions across all controllers.
 * Formats standardized JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), "Resource Not Found", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateProblemException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicate(DuplicateProblemException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                "Duplicate Problem Detected",
                ex.getMessage(),
                ex.getSimilarProblemId()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponseDto> handlePaymentError(PaymentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), "Payment Failure", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(MethodArgumentNotValidException ex) {
        String firstErrorMsg = ex.getBindingResult().getAllErrors().isEmpty() ?
                "Invalid input parameters" : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), "Validation Error", firstErrorMsg);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
