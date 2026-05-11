package org.lab5.apiGateway.Security.Model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {
    private String username;

    @Size(max = 20, message = "The password length must be no more than 20 characters.")
    private String password;
}
