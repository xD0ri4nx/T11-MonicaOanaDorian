package com.example.smartlibrary.controller;

import com.example.smartlibrary.model.LoanOverdueNotification;
import com.example.smartlibrary.service.LoanOverdueNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications/overdue")
public class LoanOverdueNotificationController {
    @Autowired
    private LoanOverdueNotificationService service;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanOverdueNotification>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getForUser(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
