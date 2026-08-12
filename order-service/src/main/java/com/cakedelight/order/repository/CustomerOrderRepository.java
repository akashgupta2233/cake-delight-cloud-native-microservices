package com.cakedelight.order.repository;

import com.cakedelight.order.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for customer order data access.
 */
@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
}
