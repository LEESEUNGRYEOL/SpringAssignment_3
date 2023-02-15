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
import com.example.mybloguserfinal.util.CustomException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.example.mybloguserfinal.util.ErrorCode.*;


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
    public ResponseEntity<CommentResponseDto> createComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        // 1) Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;  // token 의 정보를 임시로 저장하는 곳.

        // 2) 토큰을 검사하여, 유효한 토큰일 경우에만 댓글 작성 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token); // 토큰에서 사용자 정보 가져오기
            } else {
                throw new CustomException(INVALID_TOKEN);
            }
            // 3) id 와 user를 사용하여 blogRepoDB 조회 및 유무판단.
            Optional<Blog> blog = blogRepository.findById(id);
            if (blog.isEmpty()) {
                throw new CustomException(NOT_FOUND_BLOG);
            }

            // 4) 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 -> user 엔티티 get
            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                throw new CustomException(NOT_FOUND_USER);
            }

            // 5) 요청받은 DTO 로 DB에 저장할 객체 만들기
            Comment comment = commentRepository.save(Comment.builder()
                    .commentRequestDto(commentRequestDto)
                    .user(user.get())
                    .blog(blog.get())
                    .build());

            // 6) ResponseEntity에 Body 부분에 만든 객체 전달.
            return ResponseEntity.ok()
                    .body(new CommentResponseDto(comment));
        } else {
            throw new CustomException(NOT_FOUND_TOKEN);
        }
    }

    // 요구사항 2) 댓글 수정
    @Transactional
    public ResponseEntity<CommentResponseDto> updateComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        // 1) Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 2) 토큰이 존재하는 경우 댓글 수정 가능.
        if (token != null) {
            // 2-1) 토큰이 휴효한 토큰인지 판별.
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);   // 토큰에서 사용자 정보 가져오기
            } else {
                throw new CustomException(INVALID_TOKEN);
            }
//            Optional<Blog> blog = blogRepository.findById(blogId);
//            if (blog.isEmpty()) {
//                return responseException("게시글이 존재하지 않습니다.");
//            }
            // 2-2) 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 및 유무 판단.
            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                throw new CustomException(NOT_FOUND_USER);
            }

            // 3) Admin 권한이 있는 친구는 전부 수정, 아닌경우 일부수정.
            UserRoleEnum userRoleEnum = user.get().getRole();
            Optional<Comment> comment;
            // 3-1) Admin 권환인 경우.
            if (userRoleEnum == UserRoleEnum.ADMIN) {
                comment = commentRepository.findById(id);
                if (comment.isEmpty()) { // 일치하는 댓글이 없다면
                    throw new CustomException(NOT_FOUND_COMMENT);
                }

            } else { // 3-2) User 권한인 경우.
                comment = commentRepository.findByIdAndUser(id, user.get());
                if (comment.isEmpty()) { // 일치하는 댓글이 없다면
                    throw new CustomException(NOT_FOUND_COMMENT);
                }
            }
            // 4) Comment Update
            comment.get().update(commentRequestDto, user.get());

            // 5) ResponseEntity에 Body 부분에 만든 객체 전달.
            return ResponseEntity.ok()
                    .body(new CommentResponseDto(comment.get()));
        } else {
            throw new CustomException(NOT_FOUND_TOKEN);
        }

    }

    // 요구사항 3) 댓글 삭제
    @Transactional
    public ResponseEntity<MessageResponseDto> deleteComment(Long id, HttpServletRequest request) {
        // 1) Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 2) 토큰이 있는 경우에만 선택한 댓글 삭제 가능.
        if (token != null) {
            //  2-1) 토큰이 휴효한 토큰인지 판별.
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token); // 토큰에서 사용자 정보 가져오기

            } else {
                throw new CustomException(INVALID_TOKEN);
            }

            //  2-2) 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 및 유무 판단.
            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                throw new CustomException(NOT_FOUND_USER);
            }
            // 3) Admin 권한이 있는 친구는 전부 수정, 아닌경우 일부수정.
            UserRoleEnum userRoleEnum = user.get().getRole();
            Optional<Comment> comment;

            // 3-1) Admin 권환인 경우.
            if (userRoleEnum == UserRoleEnum.ADMIN) {
                comment = commentRepository.findById(id);
                if (comment.isEmpty()) { // 일치하는 댓글이 없다면
                    throw new CustomException(NOT_FOUND_COMMENT);
                }

            } else {    // 3-2) User 권한인 경우.
                comment = commentRepository.findByIdAndUser(id, user.get());
                if (comment.isEmpty()) { // 일치하는 댓글이 없다면
                    throw new CustomException(NOT_FOUND_COMMENT);
                }
            }

            // 4) Comment Delete
            commentRepository.deleteById(id);

            // 5) ResponseEntity에 Body 부분에 만든 객체 전달.
            return ResponseEntity.ok()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.OK.value())
                            .msg("댓글 삭제 성공.")
                            .build()
                    );
        } else { // 토큰이 없는 경우
            throw new CustomException(NOT_FOUND_TOKEN);
        }
    }

}
