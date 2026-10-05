package com.roommade.global.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrorData {

    private final List<ValidationError> errors;
}
