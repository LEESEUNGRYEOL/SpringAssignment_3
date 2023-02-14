package com.example.mybloguserfinal.repository;

import com.example.mybloguserfinal.entity.Blog;
import com.example.mybloguserfinal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    // 요청사항 : 수정된순이 아니라, 글이 작성된 순으로 정렬바람.
    List<Blog> findAllByOrderByCreatedAtAsc();
    Optional<Blog> findByIdAndUser(Long id, User user);
}
