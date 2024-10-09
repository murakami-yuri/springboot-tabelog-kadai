package com.example.kadai002.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai002.entity.Category;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.ShopCategory;

public interface ShopCategoryRepository extends JpaRepository<ShopCategory, Integer> {
	public List<ShopCategory> findAllByShop(Shop shop);
	public List<ShopCategory> findAllByCategory(Category category);
}
