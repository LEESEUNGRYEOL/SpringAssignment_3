package com.example.mybloguserfinal.dto;


import com.example.mybloguserfinal.entity.Blog;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 문제의 조건에서 반환을 무조건 DTO로만 해달라고 했으므로, 다음과 같이 Response DTO 를 생성해서
// 보내줘야 한다. (아마 이유는 보안상, 즉 비밀번호를 줄 수도 있으므로 이에 대한 보안이라고 생각한다.)
@Getter
@NoArgsConstructor
public class BlogResponseDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdat;
    private LocalDateTime modifiedat;
    private List<CommentResponseDto> commentList = new ArrayList<>();

    public BlogResponseDto(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.createdat = blog.getCreatedAt();
        this.modifiedat = blog.getModifiedAt();
    }

    public BlogResponseDto(Blog blog, List<CommentResponseDto> commentList) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.createdat = blog.getCreatedAt();
        this.modifiedat = blog.getModifiedAt();
        this.commentList = commentList;
    }

}
