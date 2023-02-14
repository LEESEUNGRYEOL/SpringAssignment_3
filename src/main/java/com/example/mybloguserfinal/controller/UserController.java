package com.example.mybloguserfinal.controller;


import com.example.mybloguserfinal.dto.LoginRequestDto;
import com.example.mybloguserfinal.dto.MessageResponseDto;
import com.example.mybloguserfinal.dto.SignupRequestDto;
import com.example.mybloguserfinal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    // DI 주입
    private final UserService userService;

    // 요구사항1. 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signup(@RequestBody @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        return userService.signup(signupRequestDto,bindingResult);
    }


    // 요구사항2. 로그인
    @PostMapping("/login")
    public ResponseEntity<MessageResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto);
    }
}
