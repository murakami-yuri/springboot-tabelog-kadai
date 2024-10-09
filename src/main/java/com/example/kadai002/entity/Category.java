package com.example.kadai002.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "categories")
@Data
@ToString(exclude = "shopCategories")
public class Category {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
        
    @Column(name = "name")
    private String name;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<ShopCategory> shopCategories;
}