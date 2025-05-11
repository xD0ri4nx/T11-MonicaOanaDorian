package com.example.smartlibrary.model.dto;

import java.time.LocalDate;

public record LoanRequestDTO(Long userId,
                             Long copyId,
                             LocalDate borrowDate,
                             LocalDate dueDate,
                             LocalDate returnDate) {
}
