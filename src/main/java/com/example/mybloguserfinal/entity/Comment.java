package com.example.mybloguserfinal.entity;


import com.example.mybloguserfinal.dto.CommentRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BLOG_ID", nullable = false)
    private Blog blog;

    @ManyToOne
    @JoinColumn (name = "USER_ID",nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    // 생성자.
    @Builder
    public Comment (CommentRequestDto commentRequestDto, User user, Blog blog)
    {
        this.content = commentRequestDto.getContent();
        this.user = user;
        this.blog = blog;
    }
    public void update (CommentRequestDto commentRequestDto, User user)
    {
        this.content = commentRequestDto.getContent();
        this.user = user;
    }
}
