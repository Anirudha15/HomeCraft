package com.homecraft.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomizeDTO {

    @NotNull
    private Integer orderId;

    @NotBlank
    private String info;
}