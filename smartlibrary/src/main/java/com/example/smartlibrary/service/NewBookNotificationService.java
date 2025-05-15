package com.example.smartlibrary.service;

import com.example.smartlibrary.model.Book;
import com.example.smartlibrary.model.NewBookNotification;
import com.example.smartlibrary.model.User;
import com.example.smartlibrary.repository.NewBookNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewBookNotificationService {
    @Autowired
    private NewBookNotificationRepository repository;

    public NewBookNotification create(User user, Book book) {
        NewBookNotification n = new NewBookNotification();
        n.setUser(user);
        n.setBook(book);
        n.setTimestamp(LocalDateTime.now());
        return repository.save(n);
    }

    public List<NewBookNotification> getForUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public void markAsRead(Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }
}
