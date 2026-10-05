package com.roommade.global.response;

import org.springframework.http.HttpStatus;

public interface SuccessCode {

    HttpStatus getStatus();

    String getCode();

    String getMessage();
}
