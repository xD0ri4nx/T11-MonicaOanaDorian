package com.example.smartlibrary.service;

import com.example.smartlibrary.model.*;
import com.example.smartlibrary.model.utils.ReservationStatus;
import com.example.smartlibrary.repository.BookCopyRepository;
import com.example.smartlibrary.repository.NotificationRepository;
import com.example.smartlibrary.repository.ReservationRepository;
import com.example.smartlibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              BookCopyRepository bookCopyRepository, NotificationRepository notificationRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public Reservation createReservation(Long userId, Long copyId) {
        User user = userRepository.findById(userId).orElseThrow();
        BookCopy copy = bookCopyRepository.findById(copyId).orElseThrow();

        Reservation reservation = new Reservation(
                user,
                copy,
                LocalDate.now(),
                ReservationStatus.PENDING
        );

        return reservationRepository.save(reservation);
    }

    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

//    public void completeReservation(Long id) {
//        Reservation reservation = reservationRepository.findById(id).orElseThrow();
//        reservation.setStatus(ReservationStatus.COMPLETED);
//        reservationRepository.save(reservation);
//    }


    public void completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        Notification notification = new Notification();
        notification.setUser(reservation.getUser());
        notification.setReservation(reservation);
        notification.setBookCopy(reservation.getBookCopy());
        notification.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notification);
    }

}
