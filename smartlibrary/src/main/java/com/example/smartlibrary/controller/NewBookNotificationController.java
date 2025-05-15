package com.example.smartlibrary.controller;

import com.example.smartlibrary.model.NewBookNotification;
import com.example.smartlibrary.service.NewBookNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications/new-book")
public class NewBookNotificationController {

    @Autowired
    private NewBookNotificationService service;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NewBookNotification>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getForUser(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}

