package com.example.kadai002.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kadai002.entity.Role;
import com.example.kadai002.repository.RoleRepository;

@Service
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	// すべてのロールを取得する
    public List<Role> findAllRole() {
        return roleRepository.findAll();
    }
}
