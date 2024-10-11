package com.example.kadai002.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryEditForm {
	@NotBlank(message = "カテゴリ名を入力してください。")
    private String name;
}
