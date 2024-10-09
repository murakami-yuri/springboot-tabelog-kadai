package com.example.kadai002.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "shop_category")
@Data
public class ShopCategory {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
        
	@ManyToOne
    @JoinColumn(name = "shop_id")
	private Shop shop;
	
	@ManyToOne
    @JoinColumn(name = "category_id")
	private Category category;
}
