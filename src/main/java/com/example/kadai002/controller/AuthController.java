package com.example.kadai002.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.entity.User;
import com.example.kadai002.entity.VerificationToken;
import com.example.kadai002.event.ResetEventPublisher;
import com.example.kadai002.event.SignupEventPublisher;
import com.example.kadai002.form.AuthUpdateForm;
import com.example.kadai002.form.NicknameUpdateForm;
import com.example.kadai002.form.SignupForm;
import com.example.kadai002.security.UserDetailsImpl;
import com.example.kadai002.service.UserService;
import com.example.kadai002.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;
	private final ResetEventPublisher resetEventPublisher;
	
    public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, 
    		VerificationTokenService verificationTokenService, ResetEventPublisher resetEventPublisher) {
        this.userService = userService;
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
        this.resetEventPublisher = resetEventPublisher;
    }  

	@GetMapping("/login")
    public String login() {
        return "auth/login";
    }
	
	 /***** 会員登録 *****/
	
	@GetMapping("/signup")
    public String signup(@RequestParam(name = "type") String type, Model model) {
        model.addAttribute("signupForm", new SignupForm());
        model.addAttribute("type", type);
        return "auth/signup";
    } 
	
	@PostMapping("/signup")
    public String signup(@ModelAttribute @Validated SignupForm signupForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         @RequestParam(name = "type") String type,
                         HttpServletRequest httpServletRequest,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
		// 管理者ユーザー登録申請の場合、ログインユーザーが管理者ユーザーでない場合には許可しない
        if (type.equals("admin")) {
        	String loginRoleName = userDetailsImpl.getUser().getRole().getName();
        	if (!loginRoleName.equals("ROLE_ADMIN")) {
        		return "redirect:/";
        	}
        }
        
        // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("signupForm", signupForm);
            model.addAttribute("type", type);
            return "auth/signup";
        }
        
        String roleName = "ROLE_GENERAL";
        // 管理者ユーザー登録申請の場合には
        if ( type.equals("admin") ) {
        	roleName = "ROLE_ADMIN";
        }
        User createdUser = userService.createUser(signupForm, roleName);
        	
        // 管理ユーザー登録申請の場合はcreateAdminUserを用いる
        // ログインユーザーが管理者である場合のみに申請可能にする。
        
        String requestUrl = new String(httpServletRequest.getRequestURL());
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");        

        return "redirect:/";
    } 
	
	@GetMapping("/signup/verify")
    public String verify(
    		@RequestParam(name = "token") String token, Model model,
    		HttpServletRequest request) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        
        if (verificationToken != null) {
            User user = verificationToken.getUser();  
            userService.enableUser(user);
            String successMessage = "会員登録が完了しました。";
            model.addAttribute("successMessage", successMessage);  
            
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
        }
        
        return "redirect:/";
    }
	
	/***** 会員情報変更（再認証が必要なケース） *****/
	@GetMapping("/reset")
    public String reset(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		@RequestParam(name = "type") String type,
    		Model model) {
		
		AuthUpdateForm authUpdateForm = new AuthUpdateForm();
			
		if (type.equals("passwordupdate") ) {
			String email = userDetailsImpl.getUser().getEmail();
			authUpdateForm.setEmail(email);
		}
		
        model.addAttribute("authUpdateForm", authUpdateForm);
        model.addAttribute("type", type);
        return "auth/reset";
    } 
	
	@PostMapping("/reset")
    public String reset(@ModelAttribute @Validated AuthUpdateForm authUpdateForm, 
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		@RequestParam(name = "type") String type,
    		HttpServletRequest httpServletRequest,
    		RedirectAttributes redirectAttributes,
    		Model model)
    {	
		// パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!userService.isSamePassword(authUpdateForm.getPassword(), authUpdateForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }
        
        //　パスワード変更の場合には、メールアドレスは自動入力なのでメールアドレスに関するチェックをスキップ
        
        // パスワードリセットの場合（メールアドレスは登録済みの前提）
     	if ( type.equals("passwordreset") ) {
	     	// メールアドレスが登録済みでなければ、エラーを追加する
	        if (!authUpdateForm.getEmail().isEmpty() && !userService.isEmailRegistered(authUpdateForm.getEmail())) {
	            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "ご入力いただいたメールアドレスは登録がありません。会員登録からおすすみください。");
	            bindingResult.addError(fieldError);
	        }
     	}
     	
     	// メールアドレス登録しなおしの場合（メールアドレスは登録済みではない前提）
     	if ( type.equals("emailupdate") ) {
     		// メールアドレスが登録済みの場合には、エラーを追加
     		if (userService.isEmailRegistered(authUpdateForm.getEmail())) {
     			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "ご入力いただいたメールアドレスはすでに登録されています。");
	            bindingResult.addError(fieldError);
     		}
     	}
     	
     	// 入力エラーがある場合は登録画面に戻る
     	if (bindingResult.hasErrors()) {
        	model.addAttribute("authUpdateForm", authUpdateForm);
            model.addAttribute("type", type);
            
            return "auth/reset";
        }
     	
     	User updatedUser = new User();
     	if ( type.equals("emailupdate") ) { //　メールアドレス変更の場合
     		
     		// 書き換え前のユーザー情報
     		User user = userService.findUserByEmail(userDetailsImpl.getUser().getEmail());
     		// 新しいユーザー情報
     		updatedUser = userService.updateEmail(authUpdateForm, user);
     		
     	} else { // パスワードリセット　もしくは　パスワード変更の場合
     		
     		updatedUser = userService.updateUser(authUpdateForm);
     		
     	}
     	
     	// 認証プロセスへ引き渡し
        String requestUrl = new String(httpServletRequest.getRequestURL());
        resetEventPublisher.publishResetEvent(updatedUser, requestUrl);
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックして、パスワード変更処理を完了してください。");        

        return "redirect:/";
    } 
	
	@GetMapping("/reset/verify")
    public String reverify(@RequestParam(name = "token") String token,
    		RedirectAttributes redirectAttributes) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        
        if (verificationToken != null) {
            User user = verificationToken.getUser();  
            userService.enableUser(user);
            String successMessage = "パスワードの更新が完了しました。";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } else {
            String errorMessage = "トークンが無効です。";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        
        return "redirect:/";
    }
	
	/***** 会員情報変更（再認証が必要でないケース） *****/
	@GetMapping("/nicknameupdate")
    public String nicknameupdate(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		
		String email = userDetailsImpl.getUser().getEmail();
		NicknameUpdateForm nicknameUpdateForm = new NicknameUpdateForm(email, null);
        model.addAttribute("nicknameUpdateForm", nicknameUpdateForm);
        
        return "auth/nicknameupdate";
    } 
	
	@PostMapping("/nicknameupdate")
	public String nicknameupdate(@ModelAttribute @Validated NicknameUpdateForm nicknameUpdateForm,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		userService.updateNickname(nicknameUpdateForm);  
		redirectAttributes.addFlashAttribute("successMessage", "ニックネームを更新しました。");
		
		return "redirect:/user";
	}
}
