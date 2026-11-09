package com.skillpath.repository;

import com.skillpath.model.RoleRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRequirementRepository extends JpaRepository<RoleRequirement, Long> {
    List<RoleRequirement> findByCareerRoleId(Long careerRoleId);
}
