package com.medipass.allerpass.repository;

import com.medipass.allerpass.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient,Long> {
}
