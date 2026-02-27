package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminEmail(String email);

    boolean existsByAdminEmail(String email);
}