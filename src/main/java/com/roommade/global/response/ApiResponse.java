package com.roommade.global.response;

import com.roommade.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    public static ApiResponse<Void> success(SuccessCode successCode) {
        return success(successCode, null);
    }

    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return new ApiResponse<>(true, successCode.getCode(), successCode.getMessage(), data);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static ApiResponse<ValidationErrorData> validationError(
            ErrorCode errorCode,
            ValidationErrorData data) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }
}
