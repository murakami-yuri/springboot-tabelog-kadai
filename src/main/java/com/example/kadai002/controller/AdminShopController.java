package com.example.kadai002.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.entity.Category;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.form.ShopEditForm;
import com.example.kadai002.form.ShopRegisterForm;
import com.example.kadai002.service.CategoryService;
import com.example.kadai002.service.ShopCategoryService;
import com.example.kadai002.service.ShopService;

@Controller
@RequestMapping("/admin/shops")
public class AdminShopController {
	private final ShopService shopService;
	private final CategoryService categoryService;
	private final ShopCategoryService shopCategoryService;
	
	public AdminShopController(ShopService shopService, CategoryService categoryService,
			ShopCategoryService shopCategoryService) {
		this.shopService = shopService;
		this.categoryService = categoryService;
		this.shopCategoryService = shopCategoryService;
	}
	
	// 管理者用店舗リスト表示
	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, 
			Model model) {
		
		Page<Shop> shopPage;
		// keyword検索に対応して店舗リスト取得
		if (keyword != null && !keyword.isEmpty()) {
			shopPage = shopService.findShopsByNameLike(keyword, pageable);
		} else {
			shopPage = shopService.findAllShops(pageable);
		} 
		
		//　ビューに引き渡し
		model.addAttribute("shopPage", shopPage);
		model.addAttribute("keyword", keyword);
		
		return "admin/shops/index";
	}
	
	// 管理者用店舗詳細ページ表示
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes, Model model) {
		
		//　対象のshopを取得
		Optional<Shop> optionalShop  = shopService.findShopById(id);
		if (optionalShop.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
			return "redirect:/admin/shops";
		}
		Shop shop = optionalShop.get();

		// カテゴリ情報を取得
		List<Category> categories = shopCategoryService.findCategoryByShop(shop);
        
		// ビューへ引き渡す
		model.addAttribute("shop", shop);
		model.addAttribute("categories", categories);

		return "admin/shops/show";
	}
	
	// 管理者用　店舗の登録（フォームの引き渡し）
	@GetMapping("/register")
    public String register(Model model) {
		
		// フォームに空のインスタンスを渡す
		model.addAttribute("shopRegisterForm", new ShopRegisterForm());
		
		// カテゴリーをID順で取得
		List<Category> categories = categoryService.findAllCategoryByOrderById();
		
		// フォームに表示するためにカテゴリリストを渡す
		model.addAttribute("categories", categories);
		
		return "admin/shops/register";
	}
	
	// 管理者用　店舗の登録（登録処理）
	@PostMapping("/create")
    public String create(@ModelAttribute @Validated ShopRegisterForm shopRegisterForm,
    		BindingResult bindingResult,
    		RedirectAttributes redirectAttributes, Model model) {
		
		// 店舗名が登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (shopService.isShopRegistered(shopRegisterForm.getName())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "name", "すでに登録済みの店舗名です。");
            bindingResult.addError(fieldError);
        }
        
        // エラーの場合には入力画面に戻る
        if (bindingResult.hasErrors()) {
        	// フォームのセレクトボックス用にカテゴリリストを取得
        	List<Category> categories = categoryService.findAllCategoryByOrderById();
        				
            model.addAttribute("shopRegisterForm", shopRegisterForm);
            model.addAttribute("categories", categories);
            return "admin/shops/register";
        }
        
        // shopsテーブルに登録
        // カテゴリ登録もserviceの中で行って、1つのトランザクションにする。
        shopService.createShop(shopRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");
		
		return "redirect:/admin/shops";
    }
	
	// 管理者用　店舗情報の編集（フォームの引き渡し）
	@GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id, 
    		RedirectAttributes redirectAttributes, Model model) {
        
		// フォームに現在の店舗情報を反映させるため、店舗情報を取得
		Optional<Shop> optionalShop  = shopService.findShopById(id);
		if (optionalShop.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
			return "redirect:/admin/shops";
		}
		Shop shop = optionalShop.get();
		
		// フォームに現在のカテゴリ情報を反映させるため、店舗ーカテゴリ情報を取得
		List<Integer> shopCategoryIds  = shopCategoryService.findShopCategoryIdByShop(shop);
		
		//　現在の情報を埋め込んだ状態でフォームを取得
		ShopEditForm shopEditForm = new ShopEditForm(shop.getName(), null, shop.getDescription(), 
				shop.getPostalCode(), shop.getAddress(), shop.getPhoneNumber(), shopCategoryIds);
		
		// フォームのセレクトボックス用にカテゴリリストを取得
		List<Category> categories = categoryService.findAllCategoryByOrderById();
		
		//　ビューに引き渡す
		model.addAttribute("shopEditForm", shopEditForm);
		model.addAttribute("shop", shop);
		model.addAttribute("categories", categories);
		
		return "admin/shops/edit";
	}
	
	// 管理者用　店舗情報の編集（登録処理）
	@PostMapping("{id}/update")
    public String create(@PathVariable(name = "id") Integer id,
    		@ModelAttribute @Validated ShopEditForm shopEditForm,
    		BindingResult bindingResult,
    		RedirectAttributes redirectAttributes, Model model) {
		
		// IDから店舗を取得
		Optional<Shop> optionalShop = shopService.findShopById(id);
		if (optionalShop.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
			return "redirect:/admin/shops";
		}
		Shop shop = optionalShop.get();
		
		// 店舗名が登録済み（かつ自分自身の名前でない）であれば、BindingResultオブジェクトにエラー内容を追加する
		if (shopService.isShopRegistered(shopEditForm.getName()) && !(shop.getName().equals(shopEditForm.getName()))){
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "name", "すでに登録済みの店舗名です。");
			bindingResult.addError(fieldError);
		}
		
		// 入力エラーがあれば入力画面に戻る
		if (bindingResult.hasErrors()) {
			// フォームのセレクトボックス用にカテゴリリストを取得
			List<Category> categories = categoryService.findAllCategoryByOrderById();
			
			model.addAttribute("shopEditForm", shopEditForm);
			model.addAttribute("shop", shop);
			model.addAttribute("categories", categories);
			return "admin/shops/edit";
		}
		
		// shopsテーブル更新処理
		shopService.updateShop(shopEditForm, shop);
		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を更新しました。");
		
		return "redirect:/admin/shops";
    }
	
	// 管理者用　店舗削除
	@PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        
		// IDから店舗を取得
		Optional<Shop> optionalShop  = shopService.findShopById(id);
        if (optionalShop.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/admin/shops";
        }
        Shop shop = optionalShop.get();
        
        // 店舗を削除する
        shopService.deleteShop(shop);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");

        return "redirect:/admin/shops";
    }
}

