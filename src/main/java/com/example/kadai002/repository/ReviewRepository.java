package com.example.kadai002.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai002.entity.Review;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.User;

public interface ReviewRepository  extends JpaRepository<Review, Integer> {
	public List<Review> findByShop(Shop shop);
	public List<Review> findByUser(User user);
	public List<Review> findByShopOrderByCreatedAtDesc(Shop shop);
}
