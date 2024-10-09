package com.example.kadai002.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai002.entity.User;
import com.example.kadai002.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository< VerificationToken, Integer> {
	public VerificationToken findByToken(String token);
	public VerificationToken findByUser(User user);
}
