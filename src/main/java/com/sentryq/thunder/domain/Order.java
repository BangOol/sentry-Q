package com.sentryq.thunder.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders", // [cite: 20]
    // 한 사용자는 한 이벤트에 중복 당첨될 수 없습니다.
    // DB 레벨에서 중복 저장을 막는 최후의 방어선입니다.
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_event_user", 
            columnNames = {"event_id", "user_id"} 
        )
    }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id") 
    private Long id;

    // ManyToOne 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false) 
    private Event event;

    // ManyToOne 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); 

    // (정적 팩토리 메서드 등을 사용하여 생성자 대신 사용)
    public static Order createOrder(Event event, User user) {
        Order order = new Order();
        order.event = event;
        order.user = user;
        return order;
    }
}