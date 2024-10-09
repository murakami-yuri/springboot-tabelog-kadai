package com.example.kadai002.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai002.entity.Review;
import com.example.kadai002.entity.Shop;
import com.example.kadai002.entity.User;
import com.example.kadai002.form.ReviewEditForm;
import com.example.kadai002.form.ReviewPostForm;
import com.example.kadai002.repository.ReviewRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	 
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

	// 指定したshopのレビューを取得する
    public List<Review> findReviewByShop(Shop shop) {
        return reviewRepository.findByShop(shop);
    }
    
    // 指定したshopのレビューを投稿日時が遅い順に取得する
    public List<Review> findReviewByShopOrderByCreatedAtDesc(Shop shop) {
        return reviewRepository.findByShopOrderByCreatedAtDesc(shop);
    }
    
    // 指定したidを持つレビューを取得する
    public Optional<Review> findReviewById(Integer id) {
        return reviewRepository.findById(id);
    }
    
    // 検索条件とページ情報を付加したリダイレクト先のURLを作成
    public String createReviewUrl(Integer shopId, String keyword, List<Integer> categoryIds, Integer page) {
    	String redirectUrl = "redirect:/review/";
     	redirectUrl = redirectUrl + shopId;
     	redirectUrl = redirectUrl + "/show";
     	redirectUrl = redirectUrl + "?keyword=" + keyword;
     	if (categoryIds != null) {
     		for (Integer categoryId: categoryIds) {
	     		redirectUrl = redirectUrl + "&categoryIds=" + categoryId;
	     	}
     	}
     	redirectUrl = redirectUrl + "&page=" + page;
     	
     	return redirectUrl;
    }
    
    @Transactional
    public void reviewRegister(ReviewPostForm reviewPostForm, Shop shop, User user) {
        Review review = new Review();
        
        review.setShop(shop);
        review.setUser(user);
        review.setReview(reviewPostForm.getDescription());

        reviewRepository.save(review);
    }
    
    @Transactional
    public void updateReview(ReviewEditForm reviewEditForm, Shop shop, User user) {
    	Review review = new Review();
    	
    	review.setId(reviewEditForm.getId());
    	review.setShop(shop);
        review.setUser(user);
        review.setReview(reviewEditForm.getDescription());

        reviewRepository.save(review);
    }
    
    @Transactional
    public void deleteReview(Review review) {
        reviewRepository.delete(review);
    }
}

