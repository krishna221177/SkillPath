package com.skillpath.service;

import com.skillpath.dto.SkillGapAnalysisResultDto;
import com.skillpath.dto.SkillGapItemDto;
import com.skillpath.exception.SkillPathException;
import com.skillpath.model.*;
import com.skillpath.repository.CareerRoleRepository;
import com.skillpath.repository.StudentRepository;
import com.skillpath.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class SkillGapAnalysisService {

    private final StudentRepository studentRepository;
    private final CareerRoleRepository careerRoleRepository;
    private final StudentSkillRepository studentSkillRepository;

    public SkillGapAnalysisService(StudentRepository studentRepository,
                                   CareerRoleRepository careerRoleRepository,
                                   StudentSkillRepository studentSkillRepository) {
        this.studentRepository = studentRepository;
        this.careerRoleRepository = careerRoleRepository;
        this.studentSkillRepository = studentSkillRepository;
    }

    public SkillGapAnalysisResultDto analyze(Long studentId, Long careerRoleId) {
        if (studentId == null) {
            throw new SkillPathException("Student ID cannot be null.");
        }
        if (careerRoleId == null) {
            throw new SkillPathException("Career Role ID cannot be null.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new SkillPathException("Student not found with ID: " + studentId));

        CareerRole careerRole = careerRoleRepository.findById(careerRoleId)
                .orElseThrow(() -> new SkillPathException("Career role not found with ID: " + careerRoleId));

        return analyze(student, careerRole);
    }

    public SkillGapAnalysisResultDto analyze(Student student, CareerRole careerRole) {
        if (careerRole == null) {
            return new SkillGapAnalysisResultDto(null, Collections.emptyList());
        }

        // Map student skills by skill id for fast O(1) lookup
        List<StudentSkill> studentSkills = student != null && student.getId() != null
                ? studentSkillRepository.findByStudentId(student.getId())
                : (student != null && student.getSkills() != null ? student.getSkills() : Collections.emptyList());

        Map<Long, ProficiencyLevel> studentSkillMap = new HashMap<>();
        for (StudentSkill ss : studentSkills) {
            if (ss.getSkill() != null && ss.getSkill().getId() != null) {
                studentSkillMap.put(ss.getSkill().getId(), ss.getProficiency());
            }
        }

        List<RoleRequirement> requirements = careerRole.getRequirements() != null
                ? careerRole.getRequirements()
                : Collections.emptyList();

        List<SkillGapItemDto> gapItems = new ArrayList<>();
        for (RoleRequirement req : requirements) {
            Skill skill = req.getSkill();
            ProficiencyLevel studentProficiency = (skill != null && skill.getId() != null)
                    ? studentSkillMap.get(skill.getId())
                    : null;

            SkillGapItemDto item = new SkillGapItemDto(
                    skill,
                    req.getRequiredProficiency(),
                    studentProficiency,
                    req.isOptional(),
                    req.getRationale()
            );
            gapItems.add(item);
        }

        return new SkillGapAnalysisResultDto(careerRole, gapItems);
    }
}
