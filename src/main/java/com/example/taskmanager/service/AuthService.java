package com.example.taskmanager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.taskmanager.dto.AuthResponse;
import com.example.taskmanager.dto.LoginRequest;
import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.BusinessException;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (findByUsername(request.username()) != null) {
            throw new BusinessException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);

        String token = jwtService.createToken(user.getId(), user.getUsername());
        return new AuthResponse(user.getId(), user.getUsername(), token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = findByUsername(request.username());
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        String token = jwtService.createToken(user.getId(), user.getUsername());
        return new AuthResponse(user.getId(), user.getUsername(), token);
    }

    private User findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}
