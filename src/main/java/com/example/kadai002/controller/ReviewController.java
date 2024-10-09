package com.example.kadai002.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.entity.Category;
import com.example.kadai002.entity.Review;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.User;
import com.example.kadai002.form.ReviewEditForm;
import com.example.kadai002.form.ReviewPostForm;
import com.example.kadai002.security.UserDetailsImpl;
import com.example.kadai002.service.FavoriteService;
import com.example.kadai002.service.ReviewService;
import com.example.kadai002.service.ShopCategoryService;
import com.example.kadai002.service.ShopService;
import com.example.kadai002.service.UserService;

@Controller
public class ReviewController {
	private final ShopService shopService;
	private final UserService userService;
	private final ReviewService reviewService;
	private final ShopCategoryService shopCategoryService;
	private final FavoriteService favoriteService;
	
	public ReviewController(ShopService shopService, UserService userService, ReviewService reviewService, 
			ShopCategoryService shopCategoryService, FavoriteService favoriteService) {
		this.shopService = shopService;
		this.userService = userService;
		this.reviewService = reviewService;
		this.shopCategoryService = shopCategoryService;
		this.favoriteService = favoriteService;
	}
	
	@GetMapping("review/{shopId}/show")
	public String show(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PathVariable(name = "shopId") Integer shopId,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
			@RequestParam(name = "page", required = false) Integer page,
			RedirectAttributes redirectAttributes, Model model) {
        
		// review/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定

		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		Integer userId = userDetailsImpl.getUser().getId();
        Optional<User> optionalUser  = userService.findUserById(userId);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
            return "redirect:/";
        }
        User user = optionalUser.get();
		
		Optional<Shop> optionalShop = shopService.findShopById(shopId);
        if (optionalShop.isEmpty()) {
        	redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/";
        }
        Shop shop = optionalShop.get();
        
		//　対象のreviewを取得(投稿が最新→昔）
		List<Review> reviews = reviewService.findReviewByShopOrderByCreatedAtDesc(shop);

        // カテゴリ情報を取得
		List<Category> categories = shopCategoryService.findCategoryByShop(shop);
        
		//　お気に入りをフラグで渡すかインスタンスで渡すか考える
		Boolean isFavorite = false;
		// お気に入り情報
		if (eligible) {
			List<Shop> favoriteShops = favoriteService.findFavoriteShopByUser(user);
			if (favoriteShops.contains(shop)) {
				isFavorite = true;
			}
		}
        // ビューへ引き渡す
        model.addAttribute("shop", shop);
        model.addAttribute("reviews", reviews);
        model.addAttribute("categories", categories);
        model.addAttribute("eligible", eligible); 
        model.addAttribute("userId", userId);
        model.addAttribute("isFavorite", isFavorite);
        // 店舗一覧にリダイレクトするときに検索条件とページ位置を保持するために引き渡し
        model.addAttribute("shopId", shopId);
        model.addAttribute("categoryIds", categoryIds);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        
        return "review/show";
	        
    }
	
	@GetMapping("review/{shopId}/post")
	public String post(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PathVariable(name = "shopId") Integer shopId,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
			@RequestParam(name = "page", required = false) Integer page,
			RedirectAttributes redirectAttributes, Model model) {
		
		// review/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定
				
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		// 有料ユーザー時のみ表示
		if (eligible) {
			
			Optional<Shop> optionalShop  = shopService.findShopById(shopId);
	        if (optionalShop.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
	            return "redirect:/";
	        }
	        String shopName = optionalShop.get().getName();
	        
	        Integer userId = userDetailsImpl.getUser().getId();
	        
			ReviewPostForm reviewPostForm = new ReviewPostForm();
			
			reviewPostForm.setShopId(shopId);
			reviewPostForm.setShopName(shopName);
			reviewPostForm.setUserId(userId);
			
			// フォームをビューに渡す
	        model.addAttribute("reviewPostForm", reviewPostForm);
	        model.addAttribute("shopName", shopName);
	        
	        // 店舗一覧にリダイレクトするときに検索条件とページ位置を保持するために引き渡し
	        model.addAttribute("categoryIds", categoryIds);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("page", page);
	        model.addAttribute("shopId", shopId);
	        
			return "review/post";
	        
		} else {
			
			// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
		}
	}
	
	@PostMapping("review/{shopId}/register")
	public String register(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@ModelAttribute @Validated ReviewPostForm reviewPostForm,
			BindingResult bindingResult, //これはModelAttributeの直後に必要
			@PathVariable(name = "shopId") Integer shopId,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
			@RequestParam(name = "page", required = false) Integer page,
			RedirectAttributes redirectAttributes, Model model) {
		
		// review/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定
		
		// 入力エラー対応（レビュー投稿画面に戻る）
		if (bindingResult.hasErrors()) {
			model.addAttribute("reviewPostForm", reviewPostForm);
            model.addAttribute("categoryIds", categoryIds);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("page", page);
	        model.addAttribute("shopId", shopId);
            
            return "review/post";
        }
		
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		// 有料ユーザー時のみ表示
		if (eligible) {
			
			Optional<Shop> optionalShop  = shopService.findShopById(shopId);
	        if (optionalShop.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
	            return "redirect:/";
	        }
	        Shop shop = optionalShop.get();
	        
	        Integer userId = userDetailsImpl.getUser().getId();
	        Optional<User> optionalUser  = userService.findUserById(userId);
	        if (optionalUser.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
	            return "redirect:/";
	        }
	        User user = optionalUser.get();
	        
			//　レビューをDBに登録
			reviewService.reviewRegister(reviewPostForm, shop, user);

			// リダイレクトURL作成（ここで検索条件とページの引き継ぎを行う）
			String redirectUrl = reviewService.createReviewUrl(shopId, keyword, categoryIds, page); 
	     	
	     	return redirectUrl;
	     	
			
		} else {
			
			// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
		}
	}
	
	@PostMapping("review/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
    		@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
			@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "shopId", required = false) Integer shopId
    		//@ModelAttribute("shopId") String shopId, 
    		//Model model,
    		//@RequestParam(name = "shopId") String shopId
    		) {
		
		// review/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定
				
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		// 有料ユーザー時のみ表示
		if (eligible) {
			
	        Optional<Review> optionalReview  = reviewService.findReviewById(id);
	
	        if (optionalReview.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "レビューが存在しません。");
	
	            return "redirect:/";
	        }
	
	        Review review = optionalReview.get();
	        
	        //shopIdをreviewから取得する方法に変更できる
	        
	        // レビューを削除
	        reviewService.deleteReview(review);
	        redirectAttributes.addFlashAttribute("successMessage", "レビューを１件削除しました。");
	        
	        // リダイレクトURL作成（ここで検索条件とページの引き継ぎを行う）
	        String redirectUrl = reviewService.createReviewUrl(shopId, keyword, categoryIds, page); 
	     	
	     	return redirectUrl;
	     	
		} else {
			
			// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
		}
    }
	
	@GetMapping("review/{id}/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PathVariable(name = "id") Integer id,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
			@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "shopId", required = false) Integer shopId,
			RedirectAttributes redirectAttributes, Model model) {
		
		// review/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定
				
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		// 有料ユーザー時のみ表示
		if (eligible) {
			
			Optional<Shop> optionalShop  = shopService.findShopById(shopId);
	        if (optionalShop.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");

	            return "redirect:/";
	        }
	        String shopName = optionalShop.get().getName();
	        
	        Optional<Review> optionalReview  = reviewService.findReviewById(id);
	    	
	        if (optionalReview.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "レビューが存在しません。");
	
	            return "redirect:/";
	        }
	
	        Review review = optionalReview.get();
	        
	        Integer userId = userDetailsImpl.getUser().getId();
	        
	        ReviewEditForm reviewEditForm = new ReviewEditForm(id, shopId, shopName, userId, review.getReview());
			
			// フォームをビューに渡す
	        model.addAttribute("reviewEditForm", reviewEditForm);
	        model.addAttribute("shopName", shopName);
	        
	        // 店舗一覧にリダイレクトするときに検索条件とページ位置を保持するために引き渡し
	        model.addAttribute("categoryIds", categoryIds);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("page", page);
	        model.addAttribute("shopId", shopId);
	        
	        
			return "review/edit";
	        
		} else {
			
			// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
		}
	}
	
	@PostMapping("review/{id}/update")
    public String update(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    					@ModelAttribute @Validated ReviewEditForm reviewEditForm,
    					BindingResult bindingResult,
    					@PathVariable(name = "id") Integer id,
             			@RequestParam(name = "keyword", required = false) String keyword,
             			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
             			@RequestParam(name = "page", required = false) Integer page,
             			@RequestParam(name = "shopId", required = false) Integer shopId,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {     
		// review/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定
				
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		// 有料ユーザー時のみ表示
		if (eligible) {
			
			Optional<Shop> optionalShop  = shopService.findShopById(shopId);
	        if (optionalShop.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
	
	            return "redirect:/";
	        }
	        Shop shop = optionalShop.get();
	        
	        Integer userId = userDetailsImpl.getUser().getId();
	        Optional<User> optionalUser  = userService.findUserById(userId);
	        if (optionalUser.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
	
	            return "redirect:/";
	        }
	        User user = optionalUser.get();
	           
	        if (bindingResult.hasErrors()) {
	            model.addAttribute("shopEditForm", reviewEditForm);
	            model.addAttribute("shop", shop);
	            
	            return "admin/shops/edit";
	        }
	        
	        // reviewテーブル更新
	        reviewService.updateReview(reviewEditForm, shop, user);
	        redirectAttributes.addFlashAttribute("successMessage", "レビューを更新しました。");
	
	        // リダイレクトURL作成（ここで検索条件とページの引き継ぎを行う）
	        String redirectUrl = reviewService.createReviewUrl(shopId, keyword, categoryIds, page); 
	     	
	     	return redirectUrl;
	     	
		} else {
			
			// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
		}
    }
	
	
}
