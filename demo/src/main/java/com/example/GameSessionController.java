package com.example;

import com.example.model.GameSession;
import com.example.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}
