package com.example.kadai002.form;

import lombok.Data;

@Data
public class UserEditForm {

    private String email;
	
    private String nickname;

    private String password;
    
    //private List<Integer> categoryIds;
}
