package com.medipass.allerpass.repository;

import com.medipass.allerpass.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital,Long> {
    Optional<Hospital> findByTelno(String telno);

}// 이거 어디서 쓰이나 찾아야됨
