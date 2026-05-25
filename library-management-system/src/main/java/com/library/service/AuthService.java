
package com.library.service;

import com.library.dto.request.LoginRequest;
import com.library.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
