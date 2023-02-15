package com.example.mybloguserfinal.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j //Logging 을 위한 Logger를 생성
@RestControllerAdvice // 스프링에서 예외처리를 위한 어노테이션으로, @ControllerAdvice 와 @ResponseBody 가 합쳐진것, RESTful 웹서비스에서 예외가 발생하면 예외 메시지를 JSON 형태로 반환.
//ResponseEntityExceptionHandler를 상속받아서 스프링에서 제공하는 기본적인 예외처리 기능을 사용할 수 있음.
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {CustomException.class}) // CustomException 이 발생했을때, 해당 예외를 처리하는 메서드 정의.
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        // Lombok으로 생성된 Logger를 사용하여 로그를 출력한다.
        log.error("handleDataException throw Exception : {}", e.getErrorCode()); // error 레벨로 로그를 출력하며, 예외 처리 중 발생한 에러 코드를 출력한다.
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }
}


