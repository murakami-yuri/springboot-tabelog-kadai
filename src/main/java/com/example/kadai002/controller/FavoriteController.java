package com.example.kadai002.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.entity.Favorite;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.User;
import com.example.kadai002.security.UserDetailsImpl;
import com.example.kadai002.service.FavoriteService;
import com.example.kadai002.service.ReviewService;
import com.example.kadai002.service.ShopService;
import com.example.kadai002.service.UserService;

@Controller
public class FavoriteController {

	@Autowired
	private ShopService shopService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FavoriteService favoriteService;
	
	@Autowired
	private ReviewService reviewService;
	
	@GetMapping("favorite")
	public String show(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			RedirectAttributes redirectAttributes, Model model) {
		
		// favorite/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定
		
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		Integer userId = userDetailsImpl.getUser().getId();
		
		// 有料ユーザーの時のみ表示
		if (eligible) {
			
	        Optional<User> optionalUser  = userService.findUserById(userId);
	        if (optionalUser.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
	            return "redirect:/";
	        }
	        User user = optionalUser.get();
	        
	        List<Favorite> favorites = favoriteService.findFavoriteByUser(user);
	        List<Shop> shops = shopService.findShopByFavorite(favorites);
	        // favoriteではなくshopを
	        model.addAttribute("shops", shops);
			
		} else {
			
			// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
		}
		
		return "favorite/index";
	}
	@GetMapping("favorite/{id}/register")
	public String favorite(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PathVariable(name = "id") Integer id,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
			@RequestParam(name = "page", required = false) Integer page,
			//@RequestParam(name = "userId", required = false) Integer userId,
			RedirectAttributes redirectAttributes, Model model) {
		
		// favorite/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定
		
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		// 有料ユーザー時のみ表示
		if (eligible) {
			
			Optional<Shop> optionalShop  = shopService.findShopById(id);
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
			
			// お気に入りを登録
	        //　すでに登録済みか確認すること！
	        favoriteService.registerFavorite(shop, user);
	        redirectAttributes.addFlashAttribute("successMessage", "お気に入りに登録しました。");
	        
	        // リダイレクトURL作成（ここで検索条件とページの引き継ぎを行う）
	        String redirectUrl = reviewService.createReviewUrl(id, keyword, categoryIds, page);
	     	
	     	return redirectUrl;
	     	
		} else {
			
			// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
		}
	}
	
	@GetMapping("favorite/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
    		@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds,
			@RequestParam(name = "page", required = false) Integer page) {
				
		// favorite/ 下は未ログインユーザーはアクセスできないようWebSecurityConfigで設定
		
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		// 有料ユーザー時のみ表示
		if (eligible) {
			
			Integer userId = userDetailsImpl.getUser().getId();
			Optional<User> optionalUser  = userService.findUserById(userId);
	        if (optionalUser.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
	            return "redirect:/";
	        }
	        User user = optionalUser.get();
	        
	        Optional<Shop> optionalShop  = shopService.findShopById(id);
	        if (optionalShop.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
	            return "redirect:/";
	        }
	        Shop shop = optionalShop.get();
	        
	        List<Favorite> favorites = favoriteService.findFavoriteByShopByUser(shop, user);
	        // お気に入りを削除（本来は１件のはず）
	        Integer favoriteCount = 0;
	        for (Favorite favorite : favorites) {
	        	favoriteService.deleteFavorite(favorite);
	        	favoriteCount ++;
	        }
	        redirectAttributes.addFlashAttribute("successMessage", String.format("お気に入りを%d件解除しました。", favoriteCount));
	        
	        // リダイレクトURL作成（ここで検索条件とページの引き継ぎを行う）
	        String redirectUrl = reviewService.createReviewUrl(id, keyword, categoryIds, page);
	     	
	     	return redirectUrl;
	     	
		} else {
			
			// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
		}
    }
}
