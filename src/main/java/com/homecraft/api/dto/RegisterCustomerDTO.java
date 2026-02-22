package com.homecraft.api.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RegisterCustomerDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotBlank
    private String interests;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Size(min = 10, max = 10)
    private String phone;

    @NotNull
    private MultipartFile profileImage;
}