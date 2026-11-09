package com.skillpath.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "roadmaps")
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "career_role_id", nullable = false)
    private CareerRole careerRole;

    @Column(nullable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("suggestedSequence ASC")
    private List<RoadmapItem> items = new ArrayList<>();

    public Roadmap() {
    }

    public Roadmap(Student student, CareerRole careerRole) {
        this.student = student;
        this.careerRole = careerRole;
        this.generatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CareerRole getCareerRole() {
        return careerRole;
    }

    public void setCareerRole(CareerRole careerRole) {
        this.careerRole = careerRole;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<RoadmapItem> getItems() {
        return items;
    }

    public void setItems(List<RoadmapItem> items) {
        this.items = items;
    }

    public void addItem(RoadmapItem item) {
        items.add(item);
        item.setRoadmap(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roadmap roadmap = (Roadmap) o;
        return Objects.equals(id, roadmap.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
