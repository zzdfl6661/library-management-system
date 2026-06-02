
package com.library.service.impl;

import com.library.dto.request.LoginRequest;
import com.library.dto.response.LoginResponse;
import com.library.entity.SysUser;
import com.library.entity.Student;
import com.library.exception.BusinessException;
import com.library.mapper.StudentMapper;
import com.library.mapper.SysUserMapper;
import com.library.service.AuthService;
import com.library.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        SysUser user = sysUserMapper.selectByUsername(username);
        
        if (user == null && isStudentNo(username)) {
            user = findUserByStudentNo(username);
        }
        
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setToken(token);
        
        if ("STUDENT".equals(user.getRole())) {
            String studentNo = getStudentNo(user, username);
            response.setStudentNo(studentNo);
        }
        
        return response;
    }
    
    private boolean isStudentNo(String username) {
        return username.matches("^20\\d{6}$");
    }
    
    private SysUser findUserByStudentNo(String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student != null) {
            int num = Integer.parseInt(studentNo.substring(6));
            String username = "student" + num;
            return sysUserMapper.selectByUsername(username);
        }
        return null;
    }
    
    private String getStudentNo(SysUser user, String loginUsername) {
        if (isStudentNo(loginUsername)) {
            return loginUsername;
        }
        if (user.getUsername().startsWith("student")) {
            String num = user.getUsername().substring(7);
            return "202100" + num;
        }
        return user.getUsername();
    }
}
