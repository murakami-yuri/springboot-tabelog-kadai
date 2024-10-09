package com.example.kadai002.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryPickForm {
	private List<Integer> categoryIds;
}
