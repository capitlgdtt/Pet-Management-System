package org.lab5.common.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CatDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String breed;
    private String color;
    private Long ownerId;
    private List<Long> friendIds;
    private Integer tailLength;
}
