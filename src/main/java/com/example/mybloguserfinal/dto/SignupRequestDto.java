package com.example.mybloguserfinal.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SignupRequestDto {

    @NotNull(message = "아이디는 필수 값입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,10}")
    private String username;

    @NotNull(message = "비밀번호는 필수 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9\\d`~!@#$%^&*()-_=+]{8,24}$")
    private String password;
    private boolean admin = false;
    private String adminToken = "";
}