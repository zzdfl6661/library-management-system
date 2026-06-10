package com.library.controller;

import com.library.dto.request.LoginRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.LoginResponse;
import com.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ApiResponse.error(400, "用户名和密码不能为空");
        }
        LoginResponse response = authService.adminLogin(request);
        if (response == null) {
            return ApiResponse.error(401, "用户名或密码错误");
        }
        return ApiResponse.success(response);
    }

    @PostMapping("/reader-login")
    public ApiResponse<LoginResponse> readerLogin(@RequestBody Map<String, String> request) {
        String cardNo = request.get("cardNo");
        String password = request.get("password");
        if (cardNo == null || password == null) {
            return ApiResponse.error(400, "借书证号和密码不能为空");
        }
        LoginResponse response = authService.readerLogin(cardNo, password);
        if (response == null) {
            return ApiResponse.error(401, "借书证号或密码错误");
        }
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success("登出成功", null);
    }
}