package org.lab5.common.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lab5.common.Model.Entities.Role;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerDto {
    private Long id;
    private String username;
    private String password;
    private Role role;
    private LocalDate dateOfBirth;
    private List<Long> catIds;
}
