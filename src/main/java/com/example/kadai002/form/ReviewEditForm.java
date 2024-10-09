package com.example.kadai002.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewEditForm {
	
	private Integer id;
	
	private Integer shopId;
    
    private String shopName;
    
    private Integer userId;
    
    @NotBlank(message = "レビューを入力してください。")
    private String description;

}
