package com.homecraft.api.entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResult {

    private boolean success;
    private Integer userId;
    private String role;
    private String email;
    private String errorMessage;

}