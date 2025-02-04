package com.medipass.allerpass.repository;

import com.medipass.allerpass.entity.Hospital;
import com.medipass.allerpass.entity.HospitalAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalAdminRepository extends JpaRepository<HospitalAdmin,Long> {
    Optional<HospitalAdmin> findByEmail(String email);
}
