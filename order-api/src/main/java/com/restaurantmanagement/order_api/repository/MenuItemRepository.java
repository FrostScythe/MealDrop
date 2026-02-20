package com.restaurantmanagement.order_api.repository;

import com.restaurantmanagement.order_api.model.MenuItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Lock the row when reading
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MenuItem m WHERE m.id = :id")
    Optional<MenuItem> findByIdWithLock(@Param("id") Long id);

    List<MenuItem> findByRestaurantId(Long restaurantId);
}
