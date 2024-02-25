package ru.itmo.marketplaceservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.marketplaceservice.model.entity.MarketplaceItemEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketplaceItemsRepository extends JpaRepository<MarketplaceItemEntity, Long> {

    Page<MarketplaceItemEntity> findAllByPriceBetween(int minPrice, int maxPrice, Pageable pageable);

    List<MarketplaceItemEntity> findByItemNameStartingWith(String name);

    List<MarketplaceItemEntity> findByUserName(String userName);

    boolean existsByItemId(Long itemId);

    default Optional<MarketplaceItemEntity> deleteMarketplaceItemById(Long itemId) {
        Optional<MarketplaceItemEntity> optionalItem = findById(itemId);

        if (optionalItem.isPresent()) {
            deleteById(itemId);
        }

        return optionalItem;
    }
}
