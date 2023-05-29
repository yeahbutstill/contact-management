package com.yeahbutstill.restful.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserRequest {

    @NotBlank
    @NotEmpty
    @Size(max = 100)
    private String username;

    @NotBlank
    @NotEmpty
    @Size(max = 100)
    private String password;

}
