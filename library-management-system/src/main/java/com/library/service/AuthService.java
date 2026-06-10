package com.library.service;

import com.library.dto.request.LoginRequest;
import com.library.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse adminLogin(LoginRequest request);

    LoginResponse readerLogin(String cardNo, String password);

    Long verifyToken(String token);
}