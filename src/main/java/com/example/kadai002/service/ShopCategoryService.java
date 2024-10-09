package com.example.kadai002.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai002.entity.Category;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.ShopCategory;
import com.example.kadai002.repository.ShopCategoryRepository;

@Service
public class ShopCategoryService {
	private final ShopCategoryRepository shopCategoryRepository;
	 
    public ShopCategoryService(ShopCategoryRepository shopCategoryRepository) {
        this.shopCategoryRepository = shopCategoryRepository;
    }
    
	// 指定したshopを持つ店舗-カテゴリを取得する
    public List<ShopCategory> findShopCategoryByShop(Shop shop) {
        return shopCategoryRepository.findAllByShop(shop);
    }
    
    // 指定したshopを持つカテゴリIDのStringのリストを取得する
    public List<Integer> findShopCategoryIdByShop(Shop shop) {
    	List<ShopCategory> shopCategories = findShopCategoryByShop(shop);
    	// 空のリストに追加していく
    	List<Integer> categoryIds = new ArrayList<Integer>();
    	for (ShopCategory shopCategory : shopCategories) {
    		categoryIds.add(shopCategory.getCategory().getId());
    	}
        return categoryIds;
    }
    
    // 指定したshopを持つカテゴリのリストを取得する
    public List<Category> findCategoryByShop(Shop shop) {
    	List<ShopCategory> shopCategories = findShopCategoryByShop(shop);
    	// 空のリストに追加していく
    	List<Category> categories = new ArrayList<Category>();
    	for (ShopCategory shopCategory : shopCategories) {
    		categories.add(shopCategory.getCategory());
    	}
        return categories;
    }
    
	@Transactional
	public void updateShopCategory (Shop shop, List<Category> categories) {
		
		//　まず当該shopに関するデータを全て削除
		List<ShopCategory> shopCategories = shopCategoryRepository.findAllByShop(shop);
		
		for (ShopCategory shopCategory : shopCategories) {
			shopCategoryRepository.delete(shopCategory);
		}
		// 新しいカテゴリデータを登録
		for (Category category : categories) {
			ShopCategory shopCategory = new ShopCategory();
			shopCategory.setShop(shop);
			shopCategory.setCategory(category);
			shopCategoryRepository.save(shopCategory);
		}
	}

}
