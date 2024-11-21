package com.example;

import com.example.model.GameSession;
import com.example.model.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Проверяем, что роль допустима
        if (!User.isValidRole(user.getRole())) {
            return ResponseEntity.badRequest().body("Invalid role. Allowed values are: Player, Host");
        }

        // Хэшируем пароль и сохраняем пользователя
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }


    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());
        userData.put("points", user.getPoints());

        if ("Host".equals(user.getRole())) {
            long createdSessionsCount = userService.getCreatedSessionsCount(username);
            userData.put("createdSessionsCount", createdSessionsCount);
        }

        return ResponseEntity.ok(userData);
    }

    @GetMapping("/{username}/won-sessions")
    public ResponseEntity<List<GameSession>> getWonSessions(@PathVariable String username) {
        List<GameSession> wonSessions = userService.getWonSessions(username);
        return ResponseEntity.ok(wonSessions);
    }

    @GetMapping("/{username}/created-sessions")
    public ResponseEntity<List<GameSession>> getCreatedSessions(@PathVariable String username) {
        List<GameSession> createdSessions = userService.getCreatedSessions(username);
        return ResponseEntity.ok(createdSessions);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> getLeaderboard() {
        List<User> leaderboard = userService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
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