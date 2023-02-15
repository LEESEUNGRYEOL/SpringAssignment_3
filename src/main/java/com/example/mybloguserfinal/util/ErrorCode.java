package com.example.mybloguserfinal.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
// ResponseStatusException 과 비슷해 보이지만, 개발자가 한번에 관리,재사용 할 수 있게 정리.
public enum ErrorCode {
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다."),
    AUTHORIZATION(HttpStatus.BAD_REQUEST, "작성자만 수정/삭제할 수 있습니다."),
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "중복된 username 입니다"),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."),
    NOT_FOUND_BLOG(HttpStatus.BAD_REQUEST, "게시글을 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.BAD_REQUEST, "댓글을 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_MATCH_USERNAME(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_ADMIN_TOKEN(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_TOKEN (HttpStatus.BAD_REQUEST, "토큰을 찾을 수 없습니다.");





    private final HttpStatus httpStatus;
    private final String msg;
}
