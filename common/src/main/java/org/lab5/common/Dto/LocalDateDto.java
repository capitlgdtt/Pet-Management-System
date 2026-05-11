package org.lab5.common.Dto;

import java.time.LocalDate;

public class LocalDateDto {
    private String dateOfBirth;

    public LocalDate toLocalDate() {
        return LocalDate.parse(dateOfBirth);
    }

    public LocalDateDto(LocalDate date) {
        this.dateOfBirth = date.toString();
    }
}
