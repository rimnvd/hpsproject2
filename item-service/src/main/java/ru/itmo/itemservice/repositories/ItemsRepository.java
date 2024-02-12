package ru.itmo.itemservice.repositories;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.itemservice.model.entity.ItemEntity;

@Repository
public interface ItemsRepository extends ReactiveCrudRepository<ItemEntity, Long> {

    @Query(value = "select * from items where user_id = :userId")
    Flux<ItemEntity> findItemEntitiesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM items WHERE user_id = :userId")
    Mono<Void> deleteItemEntitiesByUserId(@Param("userId") Long userId);

    default Mono<ItemEntity> deleteItemById(Long itemId) {
        return findById(itemId)
                .flatMap(item -> delete(item)
                        .thenReturn(item)
                );
    }
}
