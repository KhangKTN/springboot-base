package com.khangktn.springbase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khangktn.springbase.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
