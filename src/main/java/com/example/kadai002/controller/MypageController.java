package com.example.kadai002.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.entity.User;
import com.example.kadai002.security.UserDetailsImpl;
import com.example.kadai002.service.StripeService;
import com.example.kadai002.service.UserService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MypageController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StripeService stripeService;
	
	//　マイページの表示
	@GetMapping("/user")
	public String show(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@ModelAttribute("successMessage") String successMessage,
			RedirectAttributes redirectAttributes, Model model) {
		
		Integer userId = userDetailsImpl.getUser().getId();

		Optional<User> optionalUser  = userService.findUserById(userId);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");

            return "redirect:/";
        }
        // Optional型からUser型へ変換
        User user = optionalUser.get();
		
        // ビューへ引き渡す
        model.addAttribute("user", user);
        model.addAttribute("successMessage", successMessage);
        
		return "/user/show";
		
    }
	
	// 有料会員へアップグレード
	@GetMapping("/user/upgrade")
	public String upgrade(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
			RedirectAttributes redirectAttributes, Model model) {
		
		Integer id = userDetailsImpl.getUser().getId();
		Optional<User> optionalUser  = userService.findUserById(id);
		if (optionalUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");

			return "redirect:/";
		}
		// Optional型からUserp型へ変換
		User user = optionalUser.get();
		
		// 有料会員は登録できない
		if (user.getEligible()) {
			redirectAttributes.addFlashAttribute("errorMessage", user.getNickname() + "さんはすでに有料会員です。");
			return "redirect:/";
		}
		
		// StripeのセッションIdを取得
		String sessionId = stripeService.createStripeSession(user);
		
		model.addAttribute("user", user);
		model.addAttribute("sessionId", sessionId);
		
		// 確認画面へ
		return "user/upgrade";
	}
	
	// クレジットカード変更
	@GetMapping("/user/card")
	public String card(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
			RedirectAttributes redirectAttributes, Model model) {
		
		Integer id = userDetailsImpl.getUser().getId();
		Optional<User> optionalUser  = userService.findUserById(id);
		if (optionalUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");

			return "redirect:/";
		}
		// Optional型からUserp型へ変換
		User user = optionalUser.get();
		
		// 有料会員しか次に進めない
		if (!user.getEligible()) {
			redirectAttributes.addFlashAttribute("errorMessage", "有料会員用メニューはご利用いただけません。");
			return "redirect:/";
		}
		
		// StripeのセッションIdを取得
		String sessionId = stripeService.createStripeSessionUpdate(user);

		model.addAttribute("user", user);
		model.addAttribute("sessionId", sessionId);
    
		// 確認画面へ
		return "user/card";
	}
	
	// 有料会員を退会確認画面へ
	@GetMapping("/user/cancelconfirm")
	public String cancelconfirm(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
			RedirectAttributes redirectAttributes, Model model) {
		
		// 有料会員でないと解約できない
		if (!userDetailsImpl.getUser().getEligible()) {
			redirectAttributes.addFlashAttribute("errorMessage", "有料会員用メニューはご利用いただけません。");
			return "redirect:/";
		}

		// 確認画面へ
		return "user/cancel";
	}
	
	// 有料会員を退会
	@GetMapping("/user/cancel")
	public String cancel(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
		
		Integer userId = userDetailsImpl.getUser().getId();
		String email = userDetailsImpl.getUser().getEmail();
		
		// StripeのサブスクリプションIDを取得する
		String subscriptionId = stripeService.getSubscriptionIdByEmail(email);
		
		// サブスクリプションの解約
		try {
			stripeService.cancelSubscription(subscriptionId, userId);
		} catch (StripeException e) {
			redirectAttributes.addFlashAttribute("errorMessage",  "有料会員のサブスクリプションを解約中にエラーが発生しました。");
		    return "redirect:/user";
		}
		
		// userを取得
		Optional<User> optionalUser  = userService.findUserById(userId);
		if (optionalUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");

			return "redirect:/";
		}
		// Optional型からUser型へ変換
		User user = optionalUser.get();
		
		// 無料会員の状態にアップデート（これを行わないと、メイン画面で有料会員用が表示されてしまう）
		String userRoleName = user.getRole().getName();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRoleName));
        UserDetailsImpl updatedUserDetailsImpl = new UserDetailsImpl(user, authorities);
		
		Authentication newAuth = new UsernamePasswordAuthenticationToken(
				updatedUserDetailsImpl, 
				updatedUserDetailsImpl.getPassword(), 
				updatedUserDetailsImpl.getAuthorities());
		    SecurityContextHolder.getContext().setAuthentication(newAuth);
		
		return "redirect:/user?canceled";
	}
	
	// Nagoyameshiを退会確認画面へ
	@GetMapping("/user/quit")
	public String quit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		
		Boolean eligible = userDetailsImpl.getUser().getEligible();
		
		// 有料会員である場合は情報を付帯して
		if (eligible) {
			model.addAttribute("eligible", eligible);
		}
		
		// 確認画面へ
		return "user/quit";
	}
	
	// Nagoyameshiを退会
	@GetMapping("/user/delete")
	public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
			RedirectAttributes redirectAttributes, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		
		Integer id = userDetailsImpl.getUser().getId();
		Optional<User> optionalUser  = userService.findUserById(id);
		if (optionalUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "会員が存在しません。");

			return "redirect:/";
		}
		// Optional型からUser型へ変換
		User user = optionalUser.get();
		
		// 有料会員の場合にはまずはサブスクリプションのキャンセル
		if (user.getEligible()) {
			
			// StripeのサブスクリプションIDを取得する
			String subscriptionId = stripeService.getSubscriptionIdByEmail(user.getEmail());
			
			try {
				stripeService.cancelSubscription(subscriptionId, id);
			} catch (StripeException e) {
				redirectAttributes.addFlashAttribute("errorMessage",  "有料会員のサブスクリプションを解約中にエラーが発生しました。");
			    return "redirect:/user";
			}
		}
		
		// 退会
		userService.deleteUser(user);
		
		// 強制的にログアウトにする
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		new SecurityContextLogoutHandler().logout(request, response, auth);
		
		return "redirect:/?quit";
	}
}
