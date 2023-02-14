package com.example.mybloguserfinal.dto;


import com.example.mybloguserfinal.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private String username;
    private LocalDateTime createdat;
    private LocalDateTime modifiedat;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdat = comment.getCreatedAt();
        this.modifiedat = comment.getModifiedAt();
        this.username = comment.getUser().getUsername();
    }
}
