package com.example.kadai002.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai002.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	public Page<User> findByEmailLike(String keyword, Pageable pageable);
	public User findByEmail(String email);
	public List<User> findAll();
}
