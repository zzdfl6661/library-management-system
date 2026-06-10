package com.library.service.impl;

import com.library.dto.request.LoginRequest;
import com.library.dto.response.LoginResponse;
import com.library.entity.Admin;
import com.library.entity.LibraryCard;
import com.library.exception.BusinessException;
import com.library.mapper.AdminMapper;
import com.library.mapper.LibraryCardMapper;
import com.library.service.AuthService;
import com.library.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private LibraryCardMapper libraryCardMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse adminLogin(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!password.equals(admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String role = admin.getAdminType() != null ? admin.getAdminType().name() : "ADMIN";
        String token = jwtUtil.generateToken(Long.valueOf(admin.getId()), username, role);

        LoginResponse response = new LoginResponse();
        response.setId(Long.valueOf(admin.getId()));
        response.setUsername(username);
        response.setRole(role);
        response.setToken(token);

        return response;
    }

    @Override
    public LoginResponse readerLogin(String cardNo, String password) {
        LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
        if (card == null) {
            throw new BusinessException("借书证号或密码错误");
        }
        if (!password.equals(card.getPassword())) {
            throw new BusinessException("借书证号或密码错误");
        }

        String token = jwtUtil.generateToken(Long.valueOf(card.getCardNo().hashCode()), card.getSno(), "READER");

        LoginResponse response = new LoginResponse();
        response.setId(Long.valueOf(card.getCardNo().hashCode()));
        response.setUsername(card.getSname());
        response.setRole("READER");
        response.setToken(token);
        response.setStudentNo(card.getSno());

        return response;
    }

    @Override
    public Long verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        if (!jwtUtil.validateToken(token)) {
            return null;
        }
        if (jwtUtil.isTokenExpired(token)) {
            return null;
        }
        return jwtUtil.extractId(token);
    }
}