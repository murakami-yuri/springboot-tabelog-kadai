package com.example.kadai002.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai002.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	public Role findByName(String name);
}
