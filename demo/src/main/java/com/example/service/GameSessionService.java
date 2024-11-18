package com.example.service;


import com.example.model.GameSession;
import com.example.repository.GameSessionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GameSessionService {
    @Autowired
    private GameSessionRepository gameSessionRepository;

    public GameSession createSession(GameSession gameSession) {
        return gameSessionRepository.save(gameSession);
    }

    public List<GameSession> getActiveSessions() {
        return gameSessionRepository.findByStatus("ACTIVE");
    }

    public GameSession endSession(Long id) {
        GameSession session = gameSessionRepository.findById(id).orElseThrow();
        session.setStatus("ENDED");
        return gameSessionRepository.save(session);
    }
}
