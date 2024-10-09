package com.example.kadai002.form;

import java.util.List;

import lombok.Data;

@Data
//@AllArgsConstructor
public class SearchConditionForm {
	private String keyword;
	private List<Integer> categoryIds;
	//private Integer page;
}
