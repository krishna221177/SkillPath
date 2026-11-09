package com.skillpath.service;

import com.skillpath.exception.SkillPathException;
import com.skillpath.model.*;
import com.skillpath.repository.SkillRepository;
import com.skillpath.repository.StudentRepository;
import com.skillpath.repository.StudentSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentSkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @Mock
    private StudentRepository studentRepository;

    private SkillService skillService;

    @BeforeEach
    void setUp() {
        skillService = new SkillService(skillRepository, studentSkillRepository, studentRepository);
    }

    @Test
    @DisplayName("Verify adding a new skill to student portfolio")
    void testAddStudentSkill() {
        Student student = new Student("Alex", "alex@test.com", "Apex", "CSE", 3);
        student.setId(1L);

        Skill skill = new Skill("Java", "Programming", "", true);
        skill.setId(10L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(studentSkillRepository.findByStudentIdAndSkillId(1L, 10L)).thenReturn(Optional.empty());
        when(studentSkillRepository.save(any(StudentSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentSkill result = skillService.addOrUpdateStudentSkill(1L, 10L, ProficiencyLevel.ADVANCED);

        assertNotNull(result);
        assertEquals(ProficiencyLevel.ADVANCED, result.getProficiency());
        assertEquals(skill, result.getSkill());
        assertEquals(student, result.getStudent());
    }

    @Test
    @DisplayName("Verify duplicate prevention: updating existing record instead of creating duplicate")
    void testPreventDuplicateStudentSkill() {
        Student student = new Student("Alex", "alex@test.com", "Apex", "CSE", 3);
        student.setId(1L);

        Skill skill = new Skill("Java", "Programming", "", true);
        skill.setId(10L);

        StudentSkill existing = new StudentSkill(student, skill, ProficiencyLevel.BEGINNER);
        existing.setId(99L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(skillRepository.findById(10L)).thenReturn(Optional.of(skill));
        when(studentSkillRepository.findByStudentIdAndSkillId(1L, 10L)).thenReturn(Optional.of(existing));
        when(studentSkillRepository.save(any(StudentSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentSkill updated = skillService.addOrUpdateStudentSkill(1L, 10L, ProficiencyLevel.EXPERT);

        assertEquals(99L, updated.getId(), "Should update the existing record with same ID");
        assertEquals(ProficiencyLevel.EXPERT, updated.getProficiency());
    }

    @Test
    @DisplayName("Verify predefined system skills cannot be deleted")
    void testPredefinedSkillCannotBeDeleted() {
        Skill predefinedSkill = new Skill("Java", "Programming", "", true);
        predefinedSkill.setId(1L);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(predefinedSkill));

        assertThrows(SkillPathException.class, () -> skillService.deleteSkill(1L));
        verify(skillRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Verify duplicate custom skill name is rejected")
    void testDuplicateCustomSkillNameRejected() {
        when(skillRepository.existsByNameIgnoreCase("Kotlin")).thenReturn(true);

        assertThrows(SkillPathException.class, () -> skillService.createCustomSkill("Kotlin", "Language", ""));
    }
}
