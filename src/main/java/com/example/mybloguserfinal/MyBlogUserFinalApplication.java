package com.example.mybloguserfinal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MyBlogUserFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBlogUserFinalApplication.class, args);
    }

}
