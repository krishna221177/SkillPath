package com.skillpath.service;

import com.skillpath.exception.SkillPathException;
import com.skillpath.model.CareerRole;
import com.skillpath.model.Student;
import com.skillpath.repository.CareerRoleRepository;
import com.skillpath.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final CareerRoleRepository careerRoleRepository;

    public StudentService(StudentRepository studentRepository, CareerRoleRepository careerRoleRepository) {
        this.studentRepository = studentRepository;
        this.careerRoleRepository = careerRoleRepository;
    }

    public Student getOrCreateDefaultStudent() {
        Optional<Student> existing = studentRepository.findFirstByOrderByIdAsc();
        if (existing.isPresent()) {
            return existing.get();
        }

        // Initialize default student
        Student student = new Student();
        student.setName("Alex Morgan");
        student.setEmail("alex.morgan@university.edu");
        student.setCollegeName("Apex Institute of Technology");
        student.setDepartment("Computer Science & Engineering");
        student.setSemester(6);

        // Associate with first career role if available
        List<CareerRole> roles = careerRoleRepository.findAll();
        if (!roles.isEmpty()) {
            student.setTargetCareer(roles.get(0));
        }

        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new SkillPathException("Student not found with ID: " + id));
    }

    public Student updateStudentProfile(Student updated) {
        if (updated.getName() == null || updated.getName().trim().isEmpty()) {
            throw new SkillPathException("Student name cannot be empty.");
        }
        if (updated.getEmail() == null || !updated.getEmail().contains("@")) {
            throw new SkillPathException("Please provide a valid email address.");
        }
        if (updated.getCollegeName() == null || updated.getCollegeName().trim().isEmpty()) {
            throw new SkillPathException("College name cannot be empty.");
        }
        if (updated.getDepartment() == null || updated.getDepartment().trim().isEmpty()) {
            throw new SkillPathException("Department cannot be empty.");
        }
        if (updated.getSemester() == null || updated.getSemester() < 1 || updated.getSemester() > 12) {
            throw new SkillPathException("Semester must be between 1 and 12.");
        }

        return studentRepository.save(updated);
    }

    public Student setTargetCareer(Long studentId, Long careerRoleId) {
        Student student = getStudentById(studentId);
        CareerRole careerRole = careerRoleRepository.findById(careerRoleId)
                .orElseThrow(() -> new SkillPathException("Career role not found with ID: " + careerRoleId));

        student.setTargetCareer(careerRole);
        return studentRepository.save(student);
    }
}
