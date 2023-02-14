package com.example.mybloguserfinal.controller;


import com.example.mybloguserfinal.dto.CommentRequestDto;
import com.example.mybloguserfinal.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    // 1) CommentService 의존성 주입.
    private final CommentService commentService;

    // 2) 댓글 작성 API (POST)
    @PostMapping("/comment/{id}")
    public ResponseEntity<Object> createComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return commentService.createComment(id, commentRequestDto, request);
    }

    // 3) 댓글 수정 API (PUT)
    @PutMapping("/comment/{id}")
    public ResponseEntity<Object> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return commentService.updateComment(id, commentRequestDto, request);
    }

    // 4) 댓글 삭제 API (DEL)
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        return commentService.deleteComment(id, request);
    }


}
