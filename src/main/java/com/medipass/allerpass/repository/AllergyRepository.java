package com.medipass.allerpass.repository;

import com.medipass.allerpass.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<Allergy,Long> {
}
