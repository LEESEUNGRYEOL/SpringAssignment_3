package com.example.mybloguserfinal.service;


import com.example.mybloguserfinal.dto.*;
import com.example.mybloguserfinal.entity.Blog;
import com.example.mybloguserfinal.entity.Comment;
import com.example.mybloguserfinal.entity.User;
import com.example.mybloguserfinal.jwt.JwtUtil;
import com.example.mybloguserfinal.repository.BlogRepository;
import com.example.mybloguserfinal.repository.UserRepository;
import com.example.mybloguserfinal.util.CustomException;
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

import static com.example.mybloguserfinal.util.ErrorCode.*;


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
            for (Comment comment : blog.getComments()) {
                commentList.add(new CommentResponseDto(comment));
            }
            allResponseDtoList.add(new AllResponseDto(blog, commentList));
        }
        // 3) ResponseEntity에 Body 부분에 allResponeseDtoList 를 넣음.
        return ResponseEntity.ok()
                .body(allResponseDtoList);
    }

    // 요구사항 2) 게시글 작성
    @Transactional
    public ResponseEntity<BlogResponseDto> createBlog(BlogRequestDto blogrequestDto, HttpServletRequest request) {
        // 1) Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims; // token 의 정보를 임시로 저장하는 곳.

        // 2) 토큰이 있는 경우에만 게시글 추가 가능.
        if (token != null) {
            // 2-1) 토큰이 휴효한 토큰인지 판별
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token); // 토큰에서 사용자 정보 가져오기
            } else {
                throw new CustomException(INVALID_TOKEN);
            }

            // 2-2) 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 및 유무판단.
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new CustomException(NOT_FOUND_USER)
            );

            // 3) 요청받은 DTO 로 DB에 저장할 객체 만들기
            Blog blog = blogRepository.saveAndFlush(Blog.builder()
                    .blogRequestDto(blogrequestDto)
                    .user(user)
                    .build());

            // 4) ResponseEntity에 Body 부분에 만든 객체 전달.
            return ResponseEntity.ok()
                    .body(new BlogResponseDto(blog));
        } else { // 토큰이 없는 경우.
            throw new CustomException(NOT_FOUND_TOKEN);
        }
    }


    // 요구사항 3)  선택한 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<BlogResponseDto> getBlogs(Long id) {
        // 1) id 를 사용하여 DB 조회 및 유무 판단.
        Blog blog = blogRepository.findById(id).orElseThrow(
                () -> new CustomException(NOT_FOUND_BLOG)
        );
        // 2) 가져온 blog 에 Comment들을 CommentList 에 추가.
        List<CommentResponseDto> commentList = new ArrayList<>();
        for (Comment comment : blog.getComments()) {
            commentList.add(new CommentResponseDto(comment));
        }
        // 3) ResponseEntity에 Body 부분에 만든 객체 전달.
        return ResponseEntity.ok()
                .body(new BlogResponseDto(blog, commentList));
    }

    // 요구사항4. 선택한 게시글 수정
    @Transactional
    public ResponseEntity<BlogResponseDto> updateBlog(Long id, BlogRequestDto blogRequestDto, HttpServletRequest request) {
        // 1) Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 2) 토큰이 있는 경우에만 게시글 수정 가능.
        if (token != null) {
            // 2-1) 토큰이 휴효한 토큰인지 판별.
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token); // 토큰에서 사용자 정보 가져오기.
            } else {
                throw new CustomException(INVALID_TOKEN);
            }

            // 2-2) 토큰에서 가져온 사용자 정보를 사용하여 userRepoDB 조회 및 유무판단.
            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                throw new CustomException(NOT_FOUND_USER);
            }
            // 2-2) id 와 user를 사용하여 blogRepoDB 조회 및 유무판단.
            Optional<Blog> blog = blogRepository.findByIdAndUser(id, user.get());
            if (blog.isEmpty()) { // 일치하는 게시물이 없다면
                throw new CustomException(AUTHORIZATION);
            }

            // 3) blogRepo DB update
            blog.get().update(blogRequestDto, user.get());

            // 4) 가져온 blog 에 Comment들을 CommentList 에 추가.
            List<CommentResponseDto> commentList = new ArrayList<>();
            for (Comment comment : blog.get().getComments()) {
                commentList.add(new CommentResponseDto(comment));
            }

            // 5) ResponseEntity에 Body 부분에 만든 객체 전달.
            return ResponseEntity.ok()
                    .body(new BlogResponseDto(blog.get(), commentList));
        } else { // 토큰이 존재하지 않을 경우.
            throw new CustomException(NOT_FOUND_TOKEN);
        }
    }

    // 요구사항5. 선택한 게시글 삭제
    @Transactional
    public ResponseEntity<MessageResponseDto> deleteBlog(Long id, HttpServletRequest request) {

        // 1) Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        // 2) 토큰이 있는 경우에만 선택한 게시글 삭제 가능.
        if (token != null) {
            // 2-1) 토큰이 휴효한 토큰인지 판별.
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
            //  2-3) id 와 user를 사용하여 DB 조회 및 유무 판단.
            Optional<Blog> blog = blogRepository.findByIdAndUser(id, user.get());
            if (blog.isEmpty()) { // 일치하는 게시물이 없다면
                throw new CustomException(AUTHORIZATION);
            }
            // 3) id를 통해서 DB 삭제.
            blogRepository.deleteById(id);

            // 4) ResponseEntity에 Body 부분에 만든 객체 전달.
            return ResponseEntity.ok()
                    .body(MessageResponseDto.builder()
                            .statusCode(HttpStatus.OK.value())
                            .msg("게시글 삭제 성공.")
                            .build()
                    );
        } else { // 토큰이 존재하지 않는 경우.
            throw new CustomException(NOT_FOUND_TOKEN);
        }

    }

}
