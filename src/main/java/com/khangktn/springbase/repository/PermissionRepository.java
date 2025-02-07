package com.khangktn.springbase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.khangktn.springbase.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
}
