package com.example.mybloguserfinal.dto;

import com.example.mybloguserfinal.entity.Blog;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class AllResponseDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdat;
    private LocalDateTime modifiedat;
    private List<CommentResponseDto> commentList = new ArrayList<>();


    public AllResponseDto(Blog blog, List<CommentResponseDto> commentList) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.createdat = blog.getCreatedAt();
        this.modifiedat = blog.getModifiedAt();
        this.commentList = commentList;
    }

}
