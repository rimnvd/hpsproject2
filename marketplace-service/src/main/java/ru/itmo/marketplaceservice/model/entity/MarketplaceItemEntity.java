package ru.itmo.marketplaceservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Entity(name = "marketplace_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Range(min = 1, max = 1000000)
    private int price;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "rarity", nullable = false)
    private String rarity;
}
