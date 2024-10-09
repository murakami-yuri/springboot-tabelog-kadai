package com.example.kadai002.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.kadai002.entity.Shop;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ShopSpecification implements Specification<Shop> {

	private String keyword;
	private List<Integer> categoryId;
	
	
	public ShopSpecification(String keyword, List<Integer> categoryId) {
        this.keyword = keyword;
        this.categoryId = categoryId;
    }
	
	@Override
    public Predicate toPredicate(Root<Shop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		List<Predicate> predicates = new ArrayList<>();
		
		// 　店舗名にkeywordを含む条件
		if (keyword != null && !keyword.isEmpty()) {
			predicates.add(builder.like(root.get("name"), "%" + keyword + "%"));
		}
		// 選択カテゴリに含まれる条件
		if (categoryId != null && !categoryId.isEmpty()) {
			predicates.add(root.get("shopCategories").get("category").get("id").in(categoryId));
		}
		//　店舗をユニーク化
		query.distinct(true);
		
		return builder.and(predicates.toArray(new Predicate[0]));
	}
	
	
}
