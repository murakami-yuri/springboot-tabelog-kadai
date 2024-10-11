package com.example.kadai002.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.entity.Category;
import com.example.kadai002.entity.Favorite;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.User;
import com.example.kadai002.form.SearchConditionForm;
import com.example.kadai002.security.UserDetailsImpl;
import com.example.kadai002.security.UserDetailsServiceImpl;
import com.example.kadai002.service.CategoryService;
import com.example.kadai002.service.FavoriteService;
import com.example.kadai002.service.ShopService;
import com.example.kadai002.service.UserService;

@Controller
public class HomeController {
	private final ShopService shopService;
	private final UserService userService;
	private final CategoryService categoryService;
	private final FavoriteService favoriteService;
	
	public HomeController(ShopService shopService, UserService userService,
			CategoryService categoryService, FavoriteService favoriteService) {
		this.shopService = shopService;
		this.userService = userService;
		this.categoryService = categoryService;
		this.favoriteService = favoriteService;
	}
	
	@Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;
	
	@GetMapping("/")
	public String mainpage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
			RedirectAttributes redirectAttributes, Model model,
			@PageableDefault(page = 0, size = 8, sort = "id", direction = Direction.ASC) Pageable pageable) {

		Boolean eligible = false;
		
		if (userDetailsImpl != null) {
			Integer id = userDetailsImpl.getUser().getId();
			Optional<User> optionalUser  = userService.findUserById(id);
	        if (optionalUser.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");
	
	            return "redirect:/"; // エラー画面を準備するか考える
	        }
	        // Optional型からUser型へ変換
	        User user = optionalUser.get();
	        eligible = user.getEligible();
		}
		
		// 検索条件入力用のフォームを準備
		SearchConditionForm searchConditionForm = new SearchConditionForm();
		
		// パラメータとして引き渡された検索条件をセット
		searchConditionForm.setKeyword(keyword);
		searchConditionForm.setCategoryIds(categoryIds);
		
		// pageで店舗情報を作成
		Page<Shop> shopPage;
		
		// keywordとカテゴリのAND検索条件で店舗を取得
        if ((keyword != null && !keyword.isEmpty()) || (categoryIds != null) ) {
        	shopPage = shopService.searchShopsCustom(keyword, categoryIds, pageable);
        } else {
            shopPage = shopService.findAllShops(pageable);
        }

        // フォームでのチェックボックス作成用にカテゴリーをID順で取得
        List<Category> categories = categoryService.findAllCategoryByOrderById();
              
        //　有料会員の時はお気に入り情報をビューに引き渡す
        if (eligible) {
			Integer userId = userDetailsImpl.getUser().getId();
	        Optional<User> optionalUser  = userService.findUserById(userId);
	        if (optionalUser.isEmpty()) {
	            model.addAttribute("errorMessage", "ユーザーが存在しません。");

	            return "redirect:/main";
	        }
	        User user = optionalUser.get();
	        
	        List<Favorite> favorites = favoriteService.findFavoriteByUser(user);
	        
	        model.addAttribute("favorites", favorites);
        }
        
        // ビューに引き渡し
        model.addAttribute("shopPage", shopPage);
        model.addAttribute("categories", categories);
        model.addAttribute("searchConditionForm", searchConditionForm);
        model.addAttribute("eligible", eligible);

		return "main";

    }
}
