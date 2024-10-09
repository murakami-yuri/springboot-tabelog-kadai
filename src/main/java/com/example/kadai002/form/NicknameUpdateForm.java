package com.example.kadai002.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NicknameUpdateForm {

	private String email;
	
    @NotBlank(message = "新しいニックネームを入力してください。")
    private String nickname;

}
