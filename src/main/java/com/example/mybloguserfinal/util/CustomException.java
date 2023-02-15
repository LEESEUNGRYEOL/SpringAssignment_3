package com.example.mybloguserfinal.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// RuntimeException 을 상속받아서 Unchecked Exception 으로 활용.
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}
