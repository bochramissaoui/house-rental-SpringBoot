package com.example.location.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PropertyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String latitude;
    private String longitude;
    private String imageUrl;

    @ManyToOne
    private UserEntity owner;

    // Getters and Setters
}
