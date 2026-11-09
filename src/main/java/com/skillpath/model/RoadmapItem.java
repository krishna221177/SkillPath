package com.skillpath.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "roadmap_items")
public class RoadmapItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ProficiencyLevel currentProficiency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProficiencyLevel targetProficiency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadmapPriority priority;

    @Column(nullable = false, length = 1000)
    private String learningObjective;

    @Column(nullable = false)
    private int suggestedSequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadmapItemStatus status = RoadmapItemStatus.NOT_STARTED;

    public RoadmapItem() {
    }

    public RoadmapItem(Roadmap roadmap, Skill skill, ProficiencyLevel currentProficiency,
                       ProficiencyLevel targetProficiency, RoadmapPriority priority,
                       String learningObjective, int suggestedSequence, RoadmapItemStatus status) {
        this.roadmap = roadmap;
        this.skill = skill;
        this.currentProficiency = currentProficiency;
        this.targetProficiency = targetProficiency;
        this.priority = priority;
        this.learningObjective = learningObjective;
        this.suggestedSequence = suggestedSequence;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Roadmap getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(Roadmap roadmap) {
        this.roadmap = roadmap;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ProficiencyLevel getCurrentProficiency() {
        return currentProficiency;
    }

    public void setCurrentProficiency(ProficiencyLevel currentProficiency) {
        this.currentProficiency = currentProficiency;
    }

    public ProficiencyLevel getTargetProficiency() {
        return targetProficiency;
    }

    public void setTargetProficiency(ProficiencyLevel targetProficiency) {
        this.targetProficiency = targetProficiency;
    }

    public RoadmapPriority getPriority() {
        return priority;
    }

    public void setPriority(RoadmapPriority priority) {
        this.priority = priority;
    }

    public String getLearningObjective() {
        return learningObjective;
    }

    public void setLearningObjective(String learningObjective) {
        this.learningObjective = learningObjective;
    }

    public int getSuggestedSequence() {
        return suggestedSequence;
    }

    public void setSuggestedSequence(int suggestedSequence) {
        this.suggestedSequence = suggestedSequence;
    }

    public RoadmapItemStatus getStatus() {
        return status;
    }

    public void setStatus(RoadmapItemStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoadmapItem that = (RoadmapItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
