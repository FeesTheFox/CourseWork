package com.example.service;

import com.example.model.GameSession;
import com.example.repository.GameSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class GameSessionService {
    @Autowired
    private GameSessionRepository gameSessionRepository;

    public GameSession createSession(GameSession gameSession) {
        validateSessionDates(gameSession.getStartTime(), gameSession.getEndTime());
        String creator = getCurrentUsername();
        gameSession.setCreator(creator);
        gameSession.setJoinedUsers(creator); // Добавляем создателя в список присоединенных пользователей
        return gameSessionRepository.save(gameSession);
    }

    public List<GameSession> getActiveSessions() {
        return gameSessionRepository.findByStatus("active");
    }

    public GameSession endSession(Long id) {
        GameSession gameSession = gameSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Session not found"));
        gameSession.setStatus("ended");
        return gameSessionRepository.save(gameSession);
    }

    public void joinSession(Long sessionId, String username) {
    GameSession gameSession = gameSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    // Создаем изменяемый список пользователей
    List<String> joinedUsers = new ArrayList<>(List.of(gameSession.getJoinedUsers().split(",")));

    // Проверяем, что пользователь еще не присоединился
    if (joinedUsers.contains(username)) {
        throw new RuntimeException("User already joined this session");
    }

    // Добавляем нового пользователя
    joinedUsers.add(username);
        gameSession.setJoinedUsers(String.join(",", joinedUsers));

    // Сохраняем обновленную сессию
        gameSessionRepository.save(gameSession);
    }

    public GameSession getSessionDetails(Long sessionId) {
        return gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private void validateSessionDates(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }
}
