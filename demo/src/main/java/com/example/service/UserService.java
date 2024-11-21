package com.example.service;

import com.example.model.GameSession;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.repository.GameSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already taken");
        }
        user.setPoints(0); // Initialize points to 0
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public void updateUserPoints(String username, int points) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);
    }

    public long getCreatedSessionsCount(String username) {
        return gameSessionRepository.countByCreator(username);
    }

    public List<GameSession> getWonSessions(String username) {
        return gameSessionRepository.findByWinner(username);
    }

    public List<GameSession> getCreatedSessions(String username) {
        return gameSessionRepository.findByCreator(username);
    }

    public List<User> getLeaderboard() {
        return userRepository.findAllByOrderByPointsDesc();
    }
}