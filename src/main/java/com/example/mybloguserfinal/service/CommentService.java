package com.example.mybloguserfinal.service;

import com.example.mybloguserfinal.dto.CommentRequestDto;
import com.example.mybloguserfinal.dto.CommentResponseDto;
import com.example.mybloguserfinal.dto.MessageResponseDto;
import com.example.mybloguserfinal.entity.Blog;
import com.example.mybloguserfinal.entity.Comment;
import com.example.mybloguserfinal.entity.User;
import com.example.mybloguserfinal.entity.UserRoleEnum;
import com.example.mybloguserfinal.jwt.JwtUtil;
import com.example.mybloguserfinal.repository.BlogRepository;
import com.example.mybloguserfinal.repository.CommentRepository;
import com.example.mybloguserfinal.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentService {
    // 1) CommentRepo 의존성 주입.
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final JwtUtil jwtUtil;

    // 요구사항 1) 댓글 작성
    @Transactional
    public ResponseEntity<Object> createComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        // token 의 정보를 임시로 저장하는 곳.
        Claims claims;

        // 2) 토큰을 검사하여, 유효한 토큰일 경우에만 댓글 작성 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
            }
            // 3) 토큰존재 + 유효한 토큰 -> 게시글이 존재하는지 확인.
            Optional<Blog> blog = blogRepository.findById(id);
            if (blog.isEmpty()) {
                return responseException("게시글이 존재하지 않습니다.");
            }

            // 4) 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 -> user 엔티티 get
            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                return responseException("사용자가 존재하지 않습니다.");
            }

            // 5) 요청받은 DTO 로 DB에 저장할 객체 만들기
            Comment comment = commentRepository.save(Comment.builder()
                    .commentRequestDto(commentRequestDto)
                    .user(user.get())
                    .blog(blog.get())
                    .build());

            return ResponseEntity.ok()
                    .body(new CommentResponseDto(comment));
        } else {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .msg("토큰이 존재하지 않습니다.")
                            .build());
        }
    }

    // 요구사항 2) 댓글 수정
    @Transactional
    public ResponseEntity<Object> updateComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 2) 토큰이 존재하는 경우 댓글 수정 가능.
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
            }

//            Optional<Blog> blog = blogRepository.findById(blogId);
//            if (blog.isEmpty()) {
//                return responseException("게시글이 존재하지 않습니다.");
//            }
            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                return responseException("사용자가 존재하지 않습니다.");
            }
            // 3) Admin 권한이 있는 친구는 전부 수정.
            UserRoleEnum userRoleEnum = user.get().getRole();
            Optional<Comment> comment;

            if (userRoleEnum == UserRoleEnum.ADMIN) {
                comment = commentRepository.findById(id);
                if (comment.isEmpty()) { // 일치하는 댓글이 없다면
                    return responseException("댓글이 존재하지 않습니다.");
                }

            } else {
                comment = commentRepository.findByIdAndUser(id, user.get());
                if (comment.isEmpty()) { // 일치하는 댓글이 없다면
                    return responseException("댓글이 존재하지 않습니다.");
                }
            }

            comment.get().update(commentRequestDto, user.get());

            return ResponseEntity.ok()
                    .body(new CommentResponseDto(comment.get()));
        } else {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .msg("토큰이 존재하지 않습니다.")
                            .build());
        }

    }

    // 요구사항 3) 댓글 삭제
    @Transactional
    public ResponseEntity<Object> deleteComment(Long id, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 관심상품 최저가 업데이트 가능
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);

            } else {
                throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                return responseException("사용자가 존재하지 않습니다.");
            }

            UserRoleEnum userRoleEnum = user.get().getRole();
            Optional<Comment> comment;

            if (userRoleEnum == UserRoleEnum.ADMIN) {
                comment = commentRepository.findById(id);
                if (comment.isEmpty()) { // 일치하는 댓글이 없다면
                    return responseException("댓글이 존재하지 않습니다.");
                }

            } else {
                comment = commentRepository.findByIdAndUser(id, user.get());
                if (comment.isEmpty()) { // 일치하는 댓글이 없다면
                    return responseException("댓글이 존재하지 않습니다.");
                }
            }

            commentRepository.deleteById(id);
            return ResponseEntity.ok()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.OK.value())
                            .msg("댓글 삭제 성공.")
                            .build()
                    );
        } else {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .msg("토큰이 존재하지 않습니다.")
                            .build());
        }
    }

    // 에러메세지 출력하는 메서드
    private static ResponseEntity<Object> responseException(String message) {
        return ResponseEntity.badRequest()
                .body(MessageResponseDto.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .msg(message)
                        .build());
    }

}
