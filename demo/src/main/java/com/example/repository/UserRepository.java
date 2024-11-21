package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findAllByOrderByPointsDesc();
}
