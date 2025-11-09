package com.sentryq.thunder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sentryq.thunder.domain.User;

public interface userRepository extends JpaRepository<User, Long> {

}
