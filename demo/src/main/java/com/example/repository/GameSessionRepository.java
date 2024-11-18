package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.GameSession;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByStatus(String status);
}
