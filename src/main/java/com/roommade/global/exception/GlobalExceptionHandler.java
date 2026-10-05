package com.roommade.global.exception;

import com.roommade.global.response.ApiResponse;
import com.roommade.global.response.ValidationError;
import com.roommade.global.response.ValidationErrorData;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ValidationErrorData>> handleValidationException(
            MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.validationError(
                CommonErrorCode.INVALID_INPUT_VALUE,
                new ValidationErrorData(toValidationErrors(exception.getBindingResult()))));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(
            HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(CommonErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException exception) {
        return ResponseEntity.status(CommonErrorCode.REQUEST_TOO_LARGE.getStatus())
                .body(ApiResponse.error(CommonErrorCode.REQUEST_TOO_LARGE));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity.status(CommonErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ApiResponse.error(CommonErrorCode.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("Unhandled exception occurred", exception);
        return ResponseEntity.status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }

    private List<ValidationError> toValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
    }
}
