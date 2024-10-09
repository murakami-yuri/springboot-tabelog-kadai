package com.example.kadai002.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai002.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	public Category findByName(String name);
	public List<Category> findAllByOrderById();
	public Page<Category> findByNameLike(String keyword, Pageable pageable);
}
