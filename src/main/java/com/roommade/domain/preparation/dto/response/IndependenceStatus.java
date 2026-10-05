package com.roommade.domain.preparation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum IndependenceStatus {
    PREPARING,
    MOVE_IN_SCHEDULED,
    MOVED_IN;

    public static IndependenceStatus from(
            LocalDate moveInDate, LocalDateTime movedInAt) {
        if (movedInAt != null) {
            return MOVED_IN;
        }
        if (moveInDate != null) {
            return MOVE_IN_SCHEDULED;
        }
        return PREPARING;
    }
}
