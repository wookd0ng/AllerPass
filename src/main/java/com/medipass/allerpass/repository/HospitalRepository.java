package com.medipass.allerpass.repository;

import com.medipass.allerpass.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital,Long> {

}
