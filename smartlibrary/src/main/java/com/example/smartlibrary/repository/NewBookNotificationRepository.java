package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.NewBookNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewBookNotificationRepository extends JpaRepository<NewBookNotification, Long> {
    List<NewBookNotification> findByUserId(Long userId);
}
