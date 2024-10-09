package com.example.kadai002.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewPostForm {
    
    private Integer shopId;
    
    private String shopName;
    
    private Integer userId;
    
    @NotBlank(message = "レビューを入力してください。")
    private String description;
}
