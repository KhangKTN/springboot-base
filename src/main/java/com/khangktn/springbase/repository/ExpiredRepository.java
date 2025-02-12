package com.khangktn.springbase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.khangktn.springbase.entity.ExpiredToken;

public interface ExpiredRepository extends JpaRepository<ExpiredToken, String> {

}
