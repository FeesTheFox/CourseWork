package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "game_sessions")
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionName;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String creator;

    @Column(nullable = false)
    private String joinedUsers; // Список пользователей, присоединенных к сессии

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] videoData; // Новая колонка для хранения видео данных

    @Column
    private String sessionAnswer; // Новое поле для хранения ответа на вопрос

    @Column
    private String winner;
}
