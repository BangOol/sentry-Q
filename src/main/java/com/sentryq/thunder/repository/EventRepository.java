package com.sentryq.thunder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sentryq.thunder.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
