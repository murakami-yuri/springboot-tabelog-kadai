package com.example.kadai002.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai002.entity.Category;
import com.example.kadai002.entity.ShopCategory;
import com.example.kadai002.form.CategoryRegisterForm;
import com.example.kadai002.form.SearchConditionForm;
import com.example.kadai002.form.ShopEditForm;
import com.example.kadai002.form.ShopRegisterForm;
import com.example.kadai002.repository.CategoryRepository;
import com.example.kadai002.repository.ShopCategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final ShopCategoryRepository shopCategoryRepository;
	
	
	public CategoryService(CategoryRepository categoryRepository, ShopCategoryRepository shopCategoryRepository) {
		this.categoryRepository = categoryRepository;
        this.shopCategoryRepository = shopCategoryRepository;
    }
    
    // 指定されたキーワードをカテゴリ名に含むカテゴリを、ページングされた状態で取得する
    public Page<Category> findCategoriesByNameLike(String keyword, Pageable pageable) {
        return categoryRepository.findByNameLike("%" + keyword + "%", pageable);
    }
    
    // すべてのカテゴリーを取得する
    public List<Category> findAllCategoryByOrderById() {
        return categoryRepository.findAllByOrderById();
    }
    
	// すべてのカテゴリーページで取得する
    public Page<Category> findAllCategory(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
	// カテゴリー名が登録済みかどうかをチェックする
    public boolean isCategoryRegistered(String name) {
        Category category = categoryRepository.findByName(name);
        return category != null;
    }
    
    // 店舗のregisterフォームのカテゴリチェックボックス（カテゴリid）を取得する
    public List<Integer> getCheckedCategories(ShopRegisterForm shopRegisterForm) {
        List<Integer> categoryIds = shopRegisterForm.getCategoryIds();
        return categoryIds;
    }
    
    // 店舗のeditフォームのカテゴリチェックボックス（カテゴリid）を取得する
    public List<Integer> getCheckedCategories(ShopEditForm shopEditForm) {
        List<Integer> categoryIds = shopEditForm.getCategoryIds();
        return categoryIds;
    }
    
    public List<Category> getCategoriesByIds(List<Integer> categoryIds) {

		List<Category> categories = new ArrayList<Category>();
		for(Integer categoryId : categoryIds) {
			Optional<Category> optionalCategory  = findCategoryById(categoryId);

	        if (!optionalCategory.isEmpty()) {
	        	categories.add(optionalCategory.get());
	        }
		}
		return categories;
    }
    public List<Integer> getCheckedCategories(SearchConditionForm searchConditionForm) {
        List<Integer> categoryIds = searchConditionForm.getCategoryIds();
        return categoryIds;
    }
    
    // 指定したidを持つカテゴリーを取得する
    public Optional<Category> findCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }
    
	//　カテゴリの追加
    @Transactional
    public void createCategory(CategoryRegisterForm categoryRegisterForm) {
        Category category = new Category();

        category.setName(categoryRegisterForm.getName());

        categoryRepository.save(category);
    }
    
    // カテゴリの削除
    @Transactional
    public void deleteCategory(Category category) {
    	//　まずShopCategoryテーブルの関連レコードを削除する
    	List<ShopCategory> shopCategories = shopCategoryRepository.findAllByCategory(category);
    	for (ShopCategory shopCategory : shopCategories) {
			shopCategoryRepository.delete(shopCategory);
		}
    	// 最後にshopレコードを削除する
        categoryRepository.delete(category);
    }
    
}
