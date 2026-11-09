package com.skillpath.service;

import com.skillpath.dto.SkillGapAnalysisResultDto;
import com.skillpath.dto.SkillGapItemDto;
import com.skillpath.model.*;
import com.skillpath.repository.CareerRoleRepository;
import com.skillpath.repository.StudentRepository;
import com.skillpath.repository.StudentSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillGapAnalysisServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CareerRoleRepository careerRoleRepository;

    @Mock
    private StudentSkillRepository studentSkillRepository;

    private SkillGapAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new SkillGapAnalysisService(studentRepository, careerRoleRepository, studentSkillRepository);
    }

    @Test
    @DisplayName("Verify exact prompt specification calculation: (3+1+0)/(3+2+4)*100 = 44.44%")
    void testPromptExampleReadinessCalculation() {
        // Setup Career Role with requirements:
        // Java: Intermediate (3)
        // SQL: Basic (2)
        // DSA: Advanced (4)
        CareerRole role = new CareerRole("Backend Dev", "Role description");
        role.setId(10L);

        Skill javaSkill = new Skill("Java", "Programming", "", true);
        javaSkill.setId(1L);

        Skill sqlSkill = new Skill("SQL", "Database", "", true);
        sqlSkill.setId(2L);

        Skill dsaSkill = new Skill("DSA", "Core CS", "", true);
        dsaSkill.setId(3L);

        role.addRequirement(new RoleRequirement(role, javaSkill, ProficiencyLevel.INTERMEDIATE, false, ""));
        role.addRequirement(new RoleRequirement(role, sqlSkill, ProficiencyLevel.BASIC, false, ""));
        role.addRequirement(new RoleRequirement(role, dsaSkill, ProficiencyLevel.ADVANCED, false, ""));

        // Student Skills:
        // Java: Intermediate (3)
        // SQL: Beginner (1)
        // DSA: Missing (0)
        Student student = new Student("Test Student", "student@test.edu", "Test College", "CSE", 4);
        student.setId(1L);

        List<StudentSkill> studentSkills = new ArrayList<>();
        studentSkills.add(new StudentSkill(student, javaSkill, ProficiencyLevel.INTERMEDIATE));
        studentSkills.add(new StudentSkill(student, sqlSkill, ProficiencyLevel.BEGINNER));

        when(studentSkillRepository.findByStudentId(1L)).thenReturn(studentSkills);

        SkillGapAnalysisResultDto result = analysisService.analyze(student, role);

        // Verification
        // Total Required Points = 3 + 2 + 4 = 9
        // Achieved Points = min(3,3) + min(1,2) + 0 = 3 + 1 + 0 = 4
        // Readiness = (4 / 9) * 100 = 44.44%
        assertEquals(9, result.getTotalRequiredPoints(), "Total required points should be 9");
        assertEquals(4, result.getTotalAchievedPoints(), "Total achieved points should be 4");
        assertEquals(44.44, result.getReadinessPercentage(), 0.01, "Expected readiness to be approximately 44.44%");

        // Status counts verification
        assertEquals(1, result.getSatisfiedCount(), "Java should be SATISFIED");
        assertEquals(1, result.getNeedsImprovementCount(), "SQL should be NEEDS_IMPROVEMENT");
        assertEquals(1, result.getMissingCount(), "DSA should be MISSING");
    }

    @Test
    @DisplayName("Verify that meeting all requirements yields exactly 100%")
    void testPerfectReadiness100Percent() {
        CareerRole role = new CareerRole("Role", "Desc");
        Skill s1 = new Skill("Java", "Programming", "", true);
        s1.setId(1L);
        role.addRequirement(new RoleRequirement(role, s1, ProficiencyLevel.ADVANCED, false, ""));

        Student student = new Student("Alex", "alex@test.com", "College", "CSE", 2);
        student.setId(2L);

        List<StudentSkill> skills = List.of(new StudentSkill(student, s1, ProficiencyLevel.ADVANCED));
        when(studentSkillRepository.findByStudentId(2L)).thenReturn(skills);

        SkillGapAnalysisResultDto result = analysisService.analyze(student, role);

        assertEquals(100.0, result.getReadinessPercentage());
        assertEquals(1, result.getSatisfiedCount());
        assertEquals(0, result.getMissingCount());
        assertEquals(0, result.getNeedsImprovementCount());
    }

    @Test
    @DisplayName("Verify that having no matching skills yields exactly 0%")
    void testZeroReadiness() {
        CareerRole role = new CareerRole("Role", "Desc");
        Skill s1 = new Skill("Java", "Programming", "", true);
        s1.setId(1L);
        role.addRequirement(new RoleRequirement(role, s1, ProficiencyLevel.INTERMEDIATE, false, ""));

        Student student = new Student("Bob", "bob@test.com", "College", "CSE", 1);
        student.setId(3L);

        when(studentSkillRepository.findByStudentId(3L)).thenReturn(List.of());

        SkillGapAnalysisResultDto result = analysisService.analyze(student, role);

        assertEquals(0.0, result.getReadinessPercentage());
        assertEquals(1, result.getMissingCount());
        assertEquals(0, result.getSatisfiedCount());
    }

    @Test
    @DisplayName("Verify exceeding requirements does not exceed required points or 100%")
    void testExceedingRequirementsCapped() {
        CareerRole role = new CareerRole("Role", "Desc");
        Skill s1 = new Skill("Java", "Programming", "", true);
        s1.setId(1L);
        // Required: Basic (2 points)
        role.addRequirement(new RoleRequirement(role, s1, ProficiencyLevel.BASIC, false, ""));

        Student student = new Student("Charlie", "charlie@test.com", "College", "CSE", 3);
        student.setId(4L);

        // Student has Expert (5 points)
        List<StudentSkill> skills = List.of(new StudentSkill(student, s1, ProficiencyLevel.EXPERT));
        when(studentSkillRepository.findByStudentId(4L)).thenReturn(skills);

        SkillGapAnalysisResultDto result = analysisService.analyze(student, role);

        // Required points = 2, Achieved points = min(5, 2) = 2 -> 100.0%
        assertEquals(2, result.getTotalRequiredPoints());
        assertEquals(2, result.getTotalAchievedPoints());
        assertEquals(100.0, result.getReadinessPercentage());
        assertTrue(result.getReadinessPercentage() <= 100.0);
    }

    @Test
    @DisplayName("Verify optional skills do not affect mandatory readiness denominator")
    void testOptionalSkillsExcludedFromDenominator() {
        CareerRole role = new CareerRole("Role", "Desc");

        Skill sMandatory = new Skill("Java", "Programming", "", true);
        sMandatory.setId(1L);

        Skill sOptional = new Skill("Docker", "DevOps", "", true);
        sOptional.setId(2L);

        // Mandatory requirement: Java (3 points)
        role.addRequirement(new RoleRequirement(role, sMandatory, ProficiencyLevel.INTERMEDIATE, false, ""));
        // Optional requirement: Docker (2 points)
        role.addRequirement(new RoleRequirement(role, sOptional, ProficiencyLevel.BASIC, true, ""));

        Student student = new Student("Dana", "dana@test.com", "College", "CSE", 3);
        student.setId(5L);

        // Student has Java Intermediate (3) and Docker Expert (5)
        List<StudentSkill> skills = List.of(
                new StudentSkill(student, sMandatory, ProficiencyLevel.INTERMEDIATE),
                new StudentSkill(student, sOptional, ProficiencyLevel.EXPERT)
        );
        when(studentSkillRepository.findByStudentId(5L)).thenReturn(skills);

        SkillGapAnalysisResultDto result = analysisService.analyze(student, role);

        // Total mandatory points should be only Java (3 points)
        assertEquals(3, result.getTotalRequiredPoints(), "Denominator must exclude optional skills");
        assertEquals(3, result.getTotalAchievedPoints(), "Numerator must exclude optional points");
        assertEquals(100.0, result.getReadinessPercentage());
        assertEquals(1, result.getOptionalCount(), "Optional skills count should be 1");
    }
}
