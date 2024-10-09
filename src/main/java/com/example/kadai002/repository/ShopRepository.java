package com.example.kadai002.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kadai002.entity.Favorite;
import com.example.kadai002.entity.Shop;

public interface ShopRepository extends JpaRepository<Shop, Integer>,
		JpaSpecificationExecutor<Shop> {
	public Page<Shop> findByNameLike(String keyword, Pageable pageable);
	public Shop findByName(String name);
	public List<Shop> findAllByFavorites(List<Favorite> favorites);
		
	// 実際には使わなかった
	@Query("SELECT s FROM Shop s WHERE s.name LIKE %:keyword% ")
	public Page<Shop> searchShops(@Param("keyword") String keyword, Pageable pageable);

	// 実際には使わなかった
	// 複数カテゴリの場合
	// カテゴリ指定がない場合
	@Query("SELECT s FROM Shop s LEFT JOIN s.shopCategories c WHERE "
			+ "c.category.name = :category AND s.name LIKE %:keyword%")
	public Page<Shop> searchShopsCondition(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);
	
}
