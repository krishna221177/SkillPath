package com.skillpath.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, length = 150)
    private String email;

    @NotBlank(message = "College name cannot be empty")
    @Column(nullable = false, length = 150)
    private String collegeName;

    @NotBlank(message = "Department cannot be empty")
    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false)
    private Integer semester = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_career_id")
    private CareerRole targetCareer;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudentSkill> skills = new ArrayList<>();

    public Student() {
    }

    public Student(String name, String email, String collegeName, String department, Integer semester) {
        this.name = name;
        this.email = email;
        this.collegeName = collegeName;
        this.department = department;
        this.semester = semester;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public CareerRole getTargetCareer() {
        return targetCareer;
    }

    public void setTargetCareer(CareerRole targetCareer) {
        this.targetCareer = targetCareer;
    }

    public List<StudentSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<StudentSkill> skills) {
        this.skills = skills;
    }

    public void addSkill(StudentSkill studentSkill) {
        skills.add(studentSkill);
        studentSkill.setStudent(this);
    }

    public void removeSkill(StudentSkill studentSkill) {
        skills.remove(studentSkill);
        studentSkill.setStudent(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
