package com.homecraft.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class AddProductDTO {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String category;

    @NotBlank
    private String type;

    @NotNull
    @Min(1)
    private BigDecimal price;

    private String customizations;

    @NotNull
    private MultipartFile image;
}