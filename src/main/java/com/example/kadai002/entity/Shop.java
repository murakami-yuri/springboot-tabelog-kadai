package com.example.kadai002.entity;

import java.sql.Timestamp;
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
@Table(name = "shops")
@Data
@ToString(exclude = {"shopCategories", "reviews", "favorites", "reservations"})
public class Shop {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "description")
    private String description;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE)
    private List<ShopCategory> shopCategories;
    
    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE)
    private List<Review> reviews;
    
    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE)
    private List<Favorite> favorites;
    
    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE)
    private List<Reservation> reservations;

}
