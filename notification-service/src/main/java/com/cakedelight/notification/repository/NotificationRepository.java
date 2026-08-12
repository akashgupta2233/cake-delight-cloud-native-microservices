package com.cakedelight.notification.repository;

import com.cakedelight.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for notification data access.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
