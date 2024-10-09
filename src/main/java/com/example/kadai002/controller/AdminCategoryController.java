package com.example.kadai002.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.kadai002.form.CategoryRegisterForm;
import com.example.kadai002.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	//　管理者用　カテゴリ一覧の表示
	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, 
			Model model) {
		
		Page<Category> categoryPage;
		
		// keywordによる検索に対応してカテゴリーを取得
        if (keyword != null && !keyword.isEmpty()) {
        	categoryPage = categoryService.findCategoriesByNameLike(keyword, pageable);
        } else {
        	categoryPage = categoryService.findAllCategory(pageable);
        } 
		
        // ビューに引き渡し
		model.addAttribute("categoryPage", categoryPage);
		model.addAttribute("keyword", keyword);
		
		return "admin/categories/index";
	}
	
	// 管理者用　カテゴリーの登録（フォームの引き渡し）
	@GetMapping("/register")
    public String register(Model model) {
		
		// ビューに空のカテゴリ登録フォームを引き渡し
        model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());

        return "admin/categories/register";
	}
	
	// 管理者用　カテゴリの登録（登録処理）
	@PostMapping("/create")
    public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {	
		// カテゴリ名が登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (categoryService.isCategoryRegistered(categoryRegisterForm.getName())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "name", "すでに登録済みのカテゴリ名です。");
            bindingResult.addError(fieldError);
        }
        
        // エラーの場合にはリスト一覧に戻る
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryRegisterForm", categoryRegisterForm);

            return "admin/categories/register";
        }
        // categoryテーブルに登録
        categoryService.createCategory(categoryRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");

        return "redirect:/admin/categories";
    }
	
	// 管理者用　カテゴリの削除
	@PostMapping("/{id}/delete")
    public String edit(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        
		// Idからカテゴリを取得
		Optional<Category> optionalCategory  = categoryService.findCategoryById(id);
        if (optionalCategory.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリーが存在しません。");
            return "redirect:/admin/categories";
        }
        Category category = optionalCategory.get();
        
        // カテゴリを削除
        categoryService.deleteCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");

        return "redirect:/admin/categories";
    }
}
