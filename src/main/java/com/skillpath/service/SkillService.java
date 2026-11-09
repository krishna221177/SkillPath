package com.skillpath.service;

import com.skillpath.exception.SkillPathException;
import com.skillpath.model.ProficiencyLevel;
import com.skillpath.model.Skill;
import com.skillpath.model.Student;
import com.skillpath.model.StudentSkill;
import com.skillpath.repository.SkillRepository;
import com.skillpath.repository.StudentRepository;
import com.skillpath.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final StudentRepository studentRepository;

    public SkillService(SkillRepository skillRepository,
                        StudentSkillRepository studentSkillRepository,
                        StudentRepository studentRepository) {
        this.skillRepository = skillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Skill> getAllSkills() {
        return skillRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Skill> getSkillById(Long id) {
        return skillRepository.findById(id);
    }

    public Skill createCustomSkill(String name, String category, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new SkillPathException("Skill name cannot be empty.");
        }
        String trimmedName = name.trim();
        if (skillRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new SkillPathException("Skill '" + trimmedName + "' already exists.");
        }

        Skill skill = new Skill(
                trimmedName,
                category != null && !category.trim().isEmpty() ? category.trim() : "Technical",
                description != null ? description.trim() : "",
                false
        );
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Long id, String name, String category, String description) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillPathException("Skill not found with ID: " + id));

        if (name == null || name.trim().isEmpty()) {
            throw new SkillPathException("Skill name cannot be empty.");
        }

        String trimmedName = name.trim();
        Optional<Skill> existing = skillRepository.findByNameIgnoreCase(trimmedName);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new SkillPathException("Another skill named '" + trimmedName + "' already exists.");
        }

        skill.setName(trimmedName);
        if (category != null && !category.trim().isEmpty()) {
            skill.setCategory(category.trim());
        }
        if (description != null) {
            skill.setDescription(description.trim());
        }

        return skillRepository.save(skill);
    }

    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillPathException("Skill not found with ID: " + id));

        if (skill.isPredefined()) {
            throw new SkillPathException("Predefined system skills cannot be deleted.");
        }

        skillRepository.delete(skill);
    }

    @Transactional(readOnly = true)
    public List<StudentSkill> getStudentSkills(Long studentId) {
        return studentSkillRepository.findByStudentId(studentId);
    }

    public StudentSkill addOrUpdateStudentSkill(Long studentId, Long skillId, ProficiencyLevel proficiency) {
        if (proficiency == null) {
            throw new SkillPathException("Please select a proficiency level.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new SkillPathException("Student not found with ID: " + studentId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillPathException("Skill not found with ID: " + skillId));

        Optional<StudentSkill> existing = studentSkillRepository.findByStudentIdAndSkillId(studentId, skillId);
        StudentSkill studentSkill;
        if (existing.isPresent()) {
            studentSkill = existing.get();
            studentSkill.setProficiency(proficiency);
            studentSkill.setLastUpdated(LocalDate.now());
        } else {
            studentSkill = new StudentSkill(student, skill, proficiency);
        }

        return studentSkillRepository.save(studentSkill);
    }

    public void removeStudentSkill(Long studentId, Long skillId) {
        studentSkillRepository.deleteByStudentIdAndSkillId(studentId, skillId);
    }
}
