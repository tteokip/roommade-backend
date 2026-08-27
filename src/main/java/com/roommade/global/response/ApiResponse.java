package com.roommade.global.response;

import com.roommade.global.exception.CommonErrorCode;
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

    public static <T> ApiResponse<T> success(T data) {
        return success(CommonErrorCode.SUCCESS, data);
    }

    public static <T> ApiResponse<T> success(ErrorCode errorCode, T data) {
        return new ApiResponse<>(true, errorCode.getCode(), errorCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return error(errorCode, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }
}
