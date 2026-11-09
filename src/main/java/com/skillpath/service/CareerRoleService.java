package com.skillpath.service;

import com.skillpath.exception.SkillPathException;
import com.skillpath.model.CareerRole;
import com.skillpath.model.RoleRequirement;
import com.skillpath.repository.CareerRoleRepository;
import com.skillpath.repository.RoleRequirementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CareerRoleService {

    private final CareerRoleRepository careerRoleRepository;
    private final RoleRequirementRepository roleRequirementRepository;

    public CareerRoleService(CareerRoleRepository careerRoleRepository,
                             RoleRequirementRepository roleRequirementRepository) {
        this.careerRoleRepository = careerRoleRepository;
        this.roleRequirementRepository = roleRequirementRepository;
    }

    public List<CareerRole> getAllRoles() {
        return careerRoleRepository.findAll();
    }

    public CareerRole getRoleById(Long id) {
        return careerRoleRepository.findById(id)
                .orElseThrow(() -> new SkillPathException("Career role not found with ID: " + id));
    }

    public List<RoleRequirement> getRequirementsForRole(Long roleId) {
        return roleRequirementRepository.findByCareerRoleId(roleId);
    }
}
