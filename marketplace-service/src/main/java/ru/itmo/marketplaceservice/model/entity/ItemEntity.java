package ru.itmo.marketplaceservice.model.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.marketplaceservice.model.enums.Rarity;

//@Entity(name = "items")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class ItemEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    @NotBlank
//    private String name;
//
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Rarity rarity;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private UserEntity user;
//}