package com.homecraft.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartQtyDTO {

    @NotNull
    private Integer orderId;

    private Integer delta;
}