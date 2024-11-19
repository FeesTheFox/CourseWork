package com.example;

import com.example.model.GameSession;
import com.example.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class GameSessionController {
    @Autowired
    private GameSessionService gameSessionService;

    @PostMapping("/create")
    public ResponseEntity<GameSession> createSession(@RequestBody GameSession gameSession) {
        GameSession createdSession = gameSessionService.createSession(gameSession);
        return ResponseEntity.ok(createdSession);
    }

    @GetMapping("/active")
    public ResponseEntity<List<GameSession>> getActiveSessions() {
        List<GameSession> activeSessions = gameSessionService.getActiveSessions();
        return ResponseEntity.ok(activeSessions);
    }

    @PutMapping("/end/{id}")
    public ResponseEntity<GameSession> endSession(@PathVariable Long id) {
        String currentUsername = getCurrentUsername();
        GameSession gameSession = gameSessionService.getSessionDetails(id);

        if (!gameSession.getCreator().equals(currentUsername)) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        GameSession endedSession = gameSessionService.endSession(id);
        return ResponseEntity.ok(endedSession);
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinSession(@RequestParam Long sessionId, @RequestParam String username) {
        gameSessionService.joinSession(sessionId, username);
        return ResponseEntity.ok("Joined session successfully");
    }

    @GetMapping("/details")
    public ResponseEntity<GameSession> getSessionDetails(@RequestParam Long sessionId) {
        GameSession session = gameSessionService.getSessionDetails(sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/ended")
    public ResponseEntity<List<GameSession>> getEndedSessions() {
        List<GameSession> endedSessions = gameSessionService.getEndedSessions();
        return ResponseEntity.ok(endedSessions);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
