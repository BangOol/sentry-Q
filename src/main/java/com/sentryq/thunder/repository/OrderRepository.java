package com.sentryq.thunder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.sentryq.thunder.domain.Event;
import com.sentryq.thunder.domain.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * uk_event_user (event_id, user_id) 인덱스를 기반으로
     * 해당 이벤트에 해당 사용자의 당첨(주문) 기록이 있는지 확인합니다.
     * (문서 5페이지 'Read Path' 최적화에서 사용됩니다)
     *
     * @param event 이벤트 엔티티
     * @param user 사용자 엔티티
     * @return 주문 존재 여부 (true/false)
     */
    boolean existsByEventAndUser(Event event, User user);

    /**
     * (문서 7페이지 '데이터 재조정 배치'에서 사용될 수 있습니다)
     * 특정 이벤트와 사용자에 대한 주문을 찾는 메서드
     */
    Optional<Order> findByEventAndUser(Event event, User user);
}
