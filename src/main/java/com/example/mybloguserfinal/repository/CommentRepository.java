package com.example.mybloguserfinal.repository;

import com.example.mybloguserfinal.entity.Comment;
import com.example.mybloguserfinal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Optional<Comment> findByIdAndUser(Long id, User user);
}
