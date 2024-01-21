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

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(nullable = false)
    @Range(min = 1, max = 1000000)
    private int price;
}
