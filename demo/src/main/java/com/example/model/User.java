package com.example.model;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email; 

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private int points;

    public static boolean isValidRole(String role) {
        List<String> validRoles = Arrays.asList("Player", "Host");
        return validRoles.contains(role);
    }
    
}
