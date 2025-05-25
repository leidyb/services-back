package com.bernate.services_back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    @Column(nullable = false)
    private Integer score;

    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
    @Column(length = 500)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "El usuario que califica es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_user_id", referencedColumnName = "id", nullable = false)
    private User rater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = true)
    private ServiceEntity service;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


    public Rating(User rater, Integer score, String comment, Product product, ServiceEntity service) {
        this.rater = rater;
        this.score = score;
        this.comment = comment;
        this.product = product;
        this.service = service;
    }
}