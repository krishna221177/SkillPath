package com.skillpath.repository;

import com.skillpath.model.CareerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareerRoleRepository extends JpaRepository<CareerRole, Long> {
    Optional<CareerRole> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
