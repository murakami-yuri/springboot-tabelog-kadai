package com.example.kadai002.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.kadai002.entity.Category;
import com.example.kadai002.entity.Favorite;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.form.ShopEditForm;
import com.example.kadai002.form.ShopRegisterForm;
import com.example.kadai002.repository.ShopRepository;
import com.example.kadai002.specification.ShopSpecification;

@Service
public class ShopService {
	private final ShopRepository shopRepository;
	private final ShopCategoryService shopCategoryService;
	private final CategoryService categoryService;
	 
    public ShopService(ShopRepository shopRepository, ShopCategoryService shopCategoryService,
    		CategoryService categoryService) {
        this.shopRepository = shopRepository;
        this.shopCategoryService = shopCategoryService;
        this.categoryService = categoryService;
    }

    // すべての店舗を取得する
    public Page<Shop> findAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable);
    }
    
    // 指定されたキーワードを店舗名に含む店舗を、ページングされた状態で取得する
    public Page<Shop> findShopsByNameLike(String keyword, Pageable pageable) {
        return shopRepository.findByNameLike("%" + keyword + "%", pageable);
    }
    
    // カスタムクエリでshopを検索する。keywordとカテゴリのAND条件。クエリはShopSpecification.javaで定義
    public Page<Shop> searchShopsCustom(String keyword, List<Integer> categoryId, Pageable pageable) {
        ShopSpecification spec = new ShopSpecification(keyword, categoryId);
        return shopRepository.findAll(spec, pageable);
    }

    // 指定したidを持つ店舗を取得する
    public Optional<Shop> findShopById(Integer id) {
        return shopRepository.findById(id);
    }
    
    // 指定した名前を持つ店舗を取得する
    public Shop findShopByName(String name) {
        return shopRepository.findByName(name);
    }
    
    // 店舗名が登録済みかどうかをチェックする
    public boolean isShopRegistered(String name) {
        Shop shop = shopRepository.findByName(name);
        return shop != null;
    }
    
    // お気に入りの店舗を取得する
    public List<Shop> findShopByFavorite(List<Favorite> favorites) {
    	List<Shop> shops = new ArrayList<Shop>();
    	for (Favorite favorite : favorites) {
    		shops.add(shopRepository.findByName(favorite.getShop().getName()));
    	}

    	return shops;
    }
    
    // 店舗登録
    @Transactional
    public void createShop(ShopRegisterForm shopRegisterForm) {
    	
    	// 店舗登録の準備
    	Shop shop = new Shop();
        MultipartFile imageFile = shopRegisterForm.getImageFile();

        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/images/shops/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            shop.setImageName(hashedImageName);
        }

        shop.setName(shopRegisterForm.getName());
        shop.setDescription(shopRegisterForm.getDescription());
        shop.setPostalCode(shopRegisterForm.getPostalCode());
        shop.setAddress(shopRegisterForm.getAddress());
        shop.setPhoneNumber(shopRegisterForm.getPhoneNumber());
        
        // 店舗テーブルの登録
        shopRepository.save(shop);
        
        // ここからShopCategoryテーブルへの登録処理
        // 入力されたカテゴリIDを取得
		List<Integer> categoryIds = categoryService.getCheckedCategories(shopRegisterForm);
		
		// カテゴリ型のリストとして取得
		List<Category> categories = categoryService.getCategoriesByIds(categoryIds);
		
		// カテゴリ情報の登録
		shopCategoryService.updateShopCategory(shop, categories);

    }
    
    // 店舗情報の更新
    @Transactional
    public void updateShop(ShopEditForm shopEditForm, Shop shop) {
    	
    	// 更新の準備
        MultipartFile imageFile = shopEditForm.getImageFile();

        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/images/shops/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            shop.setImageName(hashedImageName);
        }

        shop.setName(shopEditForm.getName());
        shop.setDescription(shopEditForm.getDescription());
        shop.setPostalCode(shopEditForm.getPostalCode());
        shop.setAddress(shopEditForm.getAddress());
        shop.setPhoneNumber(shopEditForm.getPhoneNumber());
        
        // 店舗情報の更新処理
        shopRepository.save(shop);
        
        // ここからShopCategoryテーブルへの登録処理
        // 入力されたカテゴリIDを取得
     	List<Integer> categoryIds = categoryService.getCheckedCategories(shopEditForm);
     	
     	// カテゴリ型のリストとして取得
     	List<Category> categories = categoryService.getCategoriesByIds(categoryIds);
		
		// カテゴリ情報の登録
		shopCategoryService.updateShopCategory(shop, categories);
    }
    
    // 店舗の削除
    @Transactional
    public void deleteShop(Shop shop) {
    	
        shopRepository.delete(shop);
        
    }
    
    // UUIDを使って生成したファイル名を返す
    public String generateNewFileName(String fileName) {
        String[] fileNames = fileName.split("\\.");

        for (int i = 0; i < fileNames.length - 1; i++) {
            fileNames[i] = UUID.randomUUID().toString();
        }

        String hashedFileName = String.join(".", fileNames);

        return hashedFileName;
    }

    // 画像ファイルを指定したファイルにコピーする
    public void copyImageFile(MultipartFile imageFile, Path filePath) {
        try {
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
