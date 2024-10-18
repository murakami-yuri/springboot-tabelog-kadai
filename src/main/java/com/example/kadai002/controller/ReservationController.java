package com.example.kadai002.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kadai002.dto.ReservationDTO;
import com.example.kadai002.entity.Reservation;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.User;
import com.example.kadai002.form.ReservationInputForm;
import com.example.kadai002.security.UserDetailsImpl;
import com.example.kadai002.service.ReservationService;
import com.example.kadai002.service.ShopService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReservationController {
	private final ReservationService reservationService;
	private final ShopService shopService;
	
    public ReservationController(ReservationService reservationService, ShopService shopService) {
        this.reservationService = reservationService;
        this.shopService = shopService;
    }

    @GetMapping("/reservation")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model)
    {
        User user = userDetailsImpl.getUser();
        Page<Reservation> reservationPage = reservationService.findReservationsByUserOrderByCreatedAtDesc(user, pageable);

        model.addAttribute("reservationPage", reservationPage);

        return "reservation/index";
    }
    
    @GetMapping("/reservation/{shopId}")
    public String input(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    			@PathVariable(name = "shopId") Integer shopId,
    			RedirectAttributes redirectAttributes, Model model)
    {

    	Optional<Shop> optionalShop = shopService.findShopById(shopId);
        if (optionalShop.isEmpty()) {
        	redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/";
        }
        Shop shop = optionalShop.get();
        
    	model.addAttribute("reservationInputForm", new ReservationInputForm());
    	model.addAttribute("shop", shop);
        
        return "reservation/input";
    }
    
    @PostMapping("/reservation/{shopId}")
    public String input(@PathVariable(name = "shopId") Integer shopId,
            @ModelAttribute @Validated ReservationInputForm reservationInputForm,
            BindingResult bindingResult, 
            HttpSession httpSession,
            RedirectAttributes redirectAttributes, Model model) {
    	
    	if (bindingResult.hasErrors()) {
            model.addAttribute("reservationInputForm", reservationInputForm);

            return "reservaton/input";
        }
    	
    	Optional<Shop> optionalShop = shopService.findShopById(shopId);
        if (optionalShop.isEmpty()) {
        	redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/";
        }
        Shop shop = optionalShop.get();
        
        LocalDate visitDate = reservationInputForm.getVisitDate();
        LocalTime visitTime = reservationInputForm.getVisitTime();
        Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
        
        ReservationDTO reservationDTO = new ReservationDTO(shop, visitDate, visitTime, numberOfPeople);
        
        // セッションにDTOを保存する
        httpSession.setAttribute("reservationDTO", reservationDTO);
    	
        return "redirect:/reservation/confirm";
    }
    
    @GetMapping("/reservation/confirm")
    public String confirm(RedirectAttributes redirectAttributes, HttpSession httpSession, Model model) {
        // セッションからDTOを取得する
        ReservationDTO reservationDTO = (ReservationDTO)httpSession.getAttribute("reservationDTO");

        if (reservationDTO == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "セッションがタイムアウトしました。もう一度予約内容を入力してください。");

            return "redirect:/main";
        }

        model.addAttribute("reservationDTO", reservationDTO);

        return "reservation/confirmation";
    }
    
    @PostMapping("/reservation/create")
    public String create(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
    		RedirectAttributes redirectAttributes, HttpSession httpSession) {
    	
    	Boolean eligible = userDetailsImpl.getUser().getEligible();
    	
    	if (eligible) {
	        // セッションからDTOを取得する
	        ReservationDTO reservationDTO = (ReservationDTO)httpSession.getAttribute("reservationDTO");
	
	        if (reservationDTO == null) {
	            redirectAttributes.addFlashAttribute("errorMessage", "セッションがタイムアウトしました。もう一度予約内容を入力してください。");
	
	            return "redirect:/houses";
	        }
	
	        User user = userDetailsImpl.getUser();
	        reservationService.createReservation(reservationDTO, user);
	
	        // セッションからDTOを削除する
	        httpSession.removeAttribute("reservationDTO");
	
	        return "redirect:/reservation?reserved";
	        
    	} else {
    		// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
    	}
    	
    }
    
    @PostMapping("/reservation/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id,
    		@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
    		RedirectAttributes redirectAttributes) {
    	
    	Boolean eligible = userDetailsImpl.getUser().getEligible();
    	
    	if (eligible) {
    		reservationService.deleteReservation(id);
    	} else {
    		// 無料ユーザーの場合には、メインページに戻る
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/";
    	}
    	return "redirect:/reservation?canceled";
    }
}
