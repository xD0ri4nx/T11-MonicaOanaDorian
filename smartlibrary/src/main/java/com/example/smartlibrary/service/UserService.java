package com.example.smartlibrary.service;

import com.example.smartlibrary.model.User;
import com.example.smartlibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }

    public User register(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            user.setRole(updatedUser.getRole());
            return userRepository.save(user);
        }).orElseThrow();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public Map<String, Long> getUserActivityStats(LocalDate month) {
        YearMonth yearMonth = YearMonth.from(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Map<String, Long> stats = new HashMap<>();

        // Total active users (users with at least one loan)
        long activeUsers = userRepository.countActiveUsers(startDate, endDate);
        stats.put("Active Users", activeUsers);

        // New registrations
        long newUsers = userRepository.countByCreatedAtBetween(startDate, endDate);
        stats.put("New Users", newUsers);

        // Inactive users (registered but no activity)
        long inactiveUsers = userRepository.countInactiveUsers(startDate, endDate);
        stats.put("Inactive Users", inactiveUsers);

        return stats;
    }
}
