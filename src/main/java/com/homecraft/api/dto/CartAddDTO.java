package com.homecraft.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartAddDTO {

    @NotNull
    private Integer productId;

    @Min(1)
    private Integer quantity;
}