package com.homecraft.api.service;

import com.homecraft.api.entity.AuthResult;

public interface AuthService {

    AuthResult login(String email, String password);
}