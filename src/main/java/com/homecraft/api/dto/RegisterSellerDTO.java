package com.homecraft.api.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RegisterSellerDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotBlank
    private String craft;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Size(min = 10, max = 10)
    private String phone;

    @NotBlank
    private String licenseNumber;

    @NotNull
    private MultipartFile profileImage;

    @NotNull
    private MultipartFile aadharImage;
}