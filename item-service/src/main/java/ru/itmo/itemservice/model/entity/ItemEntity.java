package ru.itmo.itemservice.model.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.itmo.itemservice.model.enums.Rarity;
import reactor.util.annotation.NonNull;

@Table("items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity {

    @Id
    private Long id;

    @NonNull
    @Column("name")
    private String name;

    @NonNull
    @Column("rarity")
    private Rarity rarity;

    @NonNull
    @Column("user_id")
    private Long userId;
}