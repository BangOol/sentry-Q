package com.sentryq.thunder.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users") // [cite: 39]
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // [cite: 44]
    private Long id;

    @Column(nullable = false, unique = true) // [cite: 45]
    private String email; // [cite: 45]

    private String username; // [cite: 46]
    
    // (문서에 따라 기타 정보는 생략) [cite: 47]
}