package com.example.kadai002.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.kadai002.entity.Shop;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationDTO {
	private Shop shop;
	 
    private LocalDate visitDate;

    private LocalTime VisitTime;

    private Integer numberOfPeople;

}
