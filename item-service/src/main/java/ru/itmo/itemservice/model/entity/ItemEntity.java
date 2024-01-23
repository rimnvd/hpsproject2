package ru.itmo.itemservice.model.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.itemservice.model.enums.Rarity;

@Entity(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    @Column(nullable = false)
    @Positive
    private Long userId;
}