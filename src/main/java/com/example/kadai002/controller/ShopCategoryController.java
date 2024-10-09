package com.example.kadai002.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.entity.Category;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.service.ShopCategoryService;

@Controller
public class ShopCategoryController {
	private final ShopCategoryService shopCategoryService;
	
	public ShopCategoryController(ShopCategoryService shopCategoryService) {
		this.shopCategoryService = shopCategoryService;
	}
	
	@GetMapping("/admin/shopcategory")
	public String addcategory(RedirectAttributes redirectAttributes,
			@ModelAttribute("categories") List<Category> categories,
			@ModelAttribute("shop") Shop shop,
			@ModelAttribute("successMessage") String successMessage) {
		
		
		shopCategoryService.updateShopCategory(shop, categories);
		
		redirectAttributes.addFlashAttribute("successMessage", successMessage);
		
		return "redirect:/admin/shops";
	}
}
