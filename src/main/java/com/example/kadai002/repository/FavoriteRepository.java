package com.example.kadai002.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai002.entity.Favorite;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.User;

public interface FavoriteRepository  extends JpaRepository<Favorite, Integer> {
	//public List<Favorite> findAllByShopByUser(Shop shop, User user);
	public List<Favorite> findAllByUser(User user);
	public List<Favorite> findByShopAndUser(Shop shop, User user);


}
