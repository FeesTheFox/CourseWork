package com.example.repository;

import com.example.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByStatus(String status);
    long countByCreator(String creator);
    List<GameSession> findByWinner(String winner);
}
