package com.example.mybloguserfinal.service;



import com.example.mybloguserfinal.dto.*;
import com.example.mybloguserfinal.entity.Blog;
import com.example.mybloguserfinal.entity.Comment;
import com.example.mybloguserfinal.entity.User;
import com.example.mybloguserfinal.jwt.JwtUtil;
import com.example.mybloguserfinal.repository.BlogRepository;
import com.example.mybloguserfinal.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BlogService {

    // 0) DI
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 요구사항 1)  전체 게시글 목록 조회
    @Transactional(readOnly = true)

    public ResponseEntity<List<AllResponseDto>> getBlogs() {
        // 1) 객체먼저 선언.
        List<Blog> blogList = blogRepository.findAllByOrderByCreatedAtAsc();
        List<AllResponseDto> allResponseDtoList = new ArrayList<>();

        // 2) 하나의 블로그글 마다 그 comment들을 전부 가져옴.
        for (Blog blog : blogList) {
            List<CommentResponseDto> commentList = new ArrayList<>(); // 위에다가 선언을할시에는 객체 초기화가 안되서 중복이됨.
            for(Comment comment: blog.getComments()){
                commentList.add(new CommentResponseDto(comment));
            }
            allResponseDtoList.add(new AllResponseDto(blog ,commentList));
        }
        // 3) ResponseEntity에 Body 부분에 allResponeseDtoList 를 넣음.
        return ResponseEntity.ok()
                .body(allResponseDtoList);
    }

    // 요구사항 2) 게시글 작성
    @Transactional
    public ResponseEntity<Object> createBlog(BlogRequestDto blogrequestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        // token 의 정보를 임시로 저장하는 곳.

        Claims claims;

        // 토큰이 있는 경우에만 관심상품 추가 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            // 요청받은 DTO 로 DB에 저장할 객체 만들기
            Blog blog = blogRepository.saveAndFlush(Blog.builder()
                    .blogRequestDto(blogrequestDto)
                    .user(user)
                    .build());

            return ResponseEntity.ok()
                    .body(new BlogResponseDto(blog));
        } else {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .msg("토큰이 존재하지 않습니다.")
                            .build());
        }
    }


    // 요구사항 3)  선택한 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getBlogs(Long id) {
        Blog blog = blogRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당글이 없습니다.")
        );
        List<CommentResponseDto> commentList = new ArrayList<>();
        for(Comment comment: blog.getComments()){
            commentList.add(new CommentResponseDto(comment));
        }
        return ResponseEntity.ok()
                .body(new BlogResponseDto(blog,commentList));
    }

    // 요구사항4. 선택한 게시글 수정
    @Transactional
    public ResponseEntity<Object> updateBlog(Long id, BlogRequestDto blogRequestDto, HttpServletRequest request) {
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
            Optional <User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                return responseException("사용자가 존재하지 않습니다.");
            }

            Optional<Blog> blog = blogRepository.findByIdAndUser(id, user.get());
            if (blog.isEmpty()) { // 일치하는 게시물이 없다면
                return responseException("본인이 작성한 게시글만 수정이 가능합니다.");
            }

            blog.get().update(blogRequestDto, user.get());

            List<CommentResponseDto> commentList = new ArrayList<>();
            for(Comment comment: blog.get().getComments()){
                commentList.add(new CommentResponseDto(comment));
            }
            return ResponseEntity.ok()
                    .body(new BlogResponseDto(blog.get(),commentList));
        } else {
            return ResponseEntity.badRequest()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .msg("토큰이 존재하지 않습니다.")
                            .build());
        }
    }

    // 요구사항5. 선택한 게시글 삭제
    @Transactional
    public ResponseEntity<Object> deleteBlog(Long id, HttpServletRequest request) {

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
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Blog blog = blogRepository.findByIdAndUser(id, user).orElseThrow(
                    () -> new NullPointerException("본인이 작성한 게시글이 아닙니다.")
            );

            blogRepository.deleteById(id);
            return ResponseEntity.ok()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.OK.value())
                            .msg("게시글 삭제 성공.")
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

    private static ResponseEntity<Object> responseException(String message) {
        return ResponseEntity.badRequest()
                .body(MessageResponseDto.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .msg(message)
                        .build());
    }

}
