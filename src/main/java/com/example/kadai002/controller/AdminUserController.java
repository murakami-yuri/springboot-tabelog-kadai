package com.example.kadai002.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.entity.User;
import com.example.kadai002.form.UserEditForm;
import com.example.kadai002.service.UserService;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

	@Autowired
	private UserService userService;
	
	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, 
			Model model) {
			
		Page<User> userPage;
		
		// keywordがある場合にはメールアドレスをkeywordで検索
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userService.findUsersByEmailLike(keyword, pageable);
        } else {
            userPage = userService.findAllUsers(pageable);
        } 
		
		model.addAttribute("userPage", userPage);
		
		return "admin/user/index";
	}
	
	@PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        
		// 対象のユーザーを取得
		Optional<User> optionalUser  = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
            return "redirect:/admin/user";
        }        
        User user = optionalUser.get();
        String email = user.getEmail();
        
        // ユーザーを削除
        userService.deleteUser(user);
        
        redirectAttributes.addFlashAttribute("successMessage", String.format("ユーザー(%s)を削除しました。", email));

        return "redirect:/admin/user";
    }
	
	@GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, 
    		RedirectAttributes redirectAttributes, Model model) {

		//　対象のuserを取得
		Optional<User> optionalUser  = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");
            return "redirect:/admin/user";
        }
        User user = optionalUser.get(); // Optional型からUserp型へ変換
        
        // ビューへ引き渡す
        model.addAttribute("user", user);

        return "admin/user/show";
    }
	
	@GetMapping("edit/{id}")
	public String edit(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes, 
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, 
			Model model) {
	
		//　対象のuserを取得
		Optional<User> optionalUser  = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");
            return "redirect:/admin/user";
        }
        User user = optionalUser.get();
        
        // フォームインスタンス作成
        UserEditForm userEditForm = new UserEditForm();
        
        //　ビューへ引き渡す
        model.addAttribute("userEditForm", userEditForm);
        model.addAttribute("user", user);
        
		return "admin/user/edit";
	}
	
	@PostMapping("edit/{id}")
	public String edit(@PathVariable(name = "id") Integer id, 
			@ModelAttribute @Validated UserEditForm userEditForm,
			RedirectAttributes redirectAttributes) {
		
		//　対象のuserを取得
		Optional<User> optionalUser  = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");
            return "redirect:/admin/user";
        }
        User user = optionalUser.get();
        
		// ユーザー情報を更新
        userService.updateUserByAdmin(userEditForm, user);
        
        redirectAttributes.addFlashAttribute("successMessage", "ユーザー情報を更新しました。");

        return "redirect:/admin/user";
	}
}
