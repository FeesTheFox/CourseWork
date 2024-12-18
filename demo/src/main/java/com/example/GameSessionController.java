package com.example;

import com.example.model.GameSession;
import com.example.service.GameSessionService;
import com.example.service.UserService;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class GameSessionController {
    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private UserService userService;

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

    @PostMapping("/{sessionId}/upload-video")
    public ResponseEntity<GameSession> uploadVideo(@PathVariable Long sessionId, @RequestParam("file") MultipartFile file) throws IOException {
        GameSession gameSession = gameSessionService.uploadVideo(sessionId, file);
        return ResponseEntity.ok(gameSession);
    }

    @GetMapping("/{sessionId}/video")
    public ResponseEntity<byte[]> getVideo(@PathVariable Long sessionId) {
        GameSession gameSession = gameSessionService.getSessionDetails(sessionId);
        if (gameSession.getVideoData() != null) {
            MediaType videoMp4 = new MediaType("video", "mp4"); // Создание MediaType вручную
            return ResponseEntity.ok()
                    .contentType(videoMp4) // Установите правильный MIME-тип
                    .body(gameSession.getVideoData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<GameSession> saveSessionAnswer(@PathVariable Long sessionId, @RequestBody String answer) {
        String currentUsername = getCurrentUsername();
        GameSession gameSession = gameSessionService.getSessionDetails(sessionId);

        if (!gameSession.getCreator().equals(currentUsername)) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        // Удалить кавычки перед сохранением
        String sanitizedAnswer = answer.trim().replaceAll("^\"|\"$", ""); // Убирает кавычки только с начала и конца строки
        GameSession updatedSession = gameSessionService.saveSessionAnswer(sessionId, sanitizedAnswer);
        return ResponseEntity.ok(updatedSession);
    }

    @PostMapping("/{sessionId}/submit-answer")
    public ResponseEntity<Map<String, String>> submitUserAnswer(@PathVariable Long sessionId, @RequestBody Map<String, String> request) {
        String userAnswer = request.get("answer").trim();
        GameSession gameSession = gameSessionService.getSessionDetails(sessionId);

        // Убрать кавычки с правильного ответа и пользовательского ответа
        String correctAnswer = gameSession.getSessionAnswer().trim().replaceAll("^\"|\"$", "");
        userAnswer = userAnswer.replaceAll("^\"|\"$", "");

        if (correctAnswer.equalsIgnoreCase(userAnswer)) {
            String currentUsername = getCurrentUsername();
            userService.updateUserPoints(currentUsername, 1);
            gameSessionService.updateSessionWinner(sessionId, currentUsername);
            gameSessionService.endSessionAfterDelay(sessionId, 10000); // End session after 10 seconds
            return ResponseEntity.ok(Map.of("message", "Correct! You won!"));
        } else {
            return ResponseEntity.ok(Map.of("message", "Nope, not quite right, try again later"));
        }
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<GameSession> getSessionById(@PathVariable Long sessionId) {
        GameSession session = gameSessionService.getSessionDetails(sessionId);
        return ResponseEntity.ok(session);
    }
}
