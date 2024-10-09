package com.example.kadai002.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai002.entity.Favorite;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.User;
import com.example.kadai002.repository.FavoriteRepository;

@Service
public class FavoriteService {

	@Autowired
	private FavoriteRepository favoriteRepository;
	
	// 指定した店舗とユーザーのレビューを取得する
    public List<Favorite> findFavoriteByUser(User user) {
    	return favoriteRepository.findAllByUser(user);
    }
    
    // 指定したユーザーのお気に入り店舗を取得する
    public List<Shop> findFavoriteShopByUser(User user) {
    	List<Favorite> favorites = findFavoriteByUser(user);
    	List<Shop> shops = new ArrayList<Shop>();
    	for (Favorite favorite : favorites) {
    		shops.add(favorite.getShop());
    	}
    	return shops;
    }
    
	// 指定した店舗とユーザーのレビューを取得する
    public List<Favorite> findFavoriteByShopByUser(Shop shop, User user) {
    	return favoriteRepository.findByShopAndUser(shop, user);
    }
    
	@Transactional
	public void registerFavorite (Shop shop, User user) {
		Favorite favorite = new Favorite();
		favorite.setShop(shop);
		favorite.setUser(user);
		favoriteRepository.save(favorite);
	}
	
	@Transactional
    public void deleteFavorite(Favorite favorite) {
        favoriteRepository.delete(favorite);
    }
}
