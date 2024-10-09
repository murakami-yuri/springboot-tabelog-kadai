package com.example.kadai002.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai002.dto.ReservationDTO;
import com.example.kadai002.entity.Reservation;
import com.example.kadai002.entity.User;
import com.example.kadai002.repository.ReservationRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // 指定されたユーザーに紐づく予約を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<Reservation> findReservationsByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Transactional
    public void createReservation(ReservationDTO reservationDTO, User user) {
        Reservation reservation = new Reservation();
       
        reservation.setShop(reservationDTO.getShop());
        reservation.setUser(user);
        reservation.setVisitDate(reservationDTO.getVisitDate());
        reservation.setVisitTime(reservationDTO.getVisitTime());
        reservation.setNumberOfPeople(reservationDTO.getNumberOfPeople());

        reservationRepository.save(reservation);
    }
    
    @Transactional
    public void deleteReservation(Integer reservationId) {
    	Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
    	Reservation reservation = optionalReservation.orElseThrow(() -> new EntityNotFoundException("指定されたIDの予約が存在しません。"));
    	reservationRepository.delete(reservation);
    }
    
}
