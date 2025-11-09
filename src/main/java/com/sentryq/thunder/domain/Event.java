package com.sentryq.thunder.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필요합니다.
@Table(name = "events") 
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id") 
    private Long id;

    @Column(nullable = false) 
    private String eventName; 

    @Column(nullable = false)
    private LocalDateTime startTime; 

    @Column(nullable = false)
    private int totalStock; 

    // created_at, updated_at은 공통 BaseEntity로 분리할 수 있으나,
    // 여기서는 단순함을 위해 포함합니다.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); 

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); 

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // (이벤트 생성 로직 등은 추후 서비스 레이어에서 구현)
}