package com.skillpath.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "role_requirements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"career_role_id", "skill_id"})
})
public class RoleRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_role_id", nullable = false)
    private CareerRole careerRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProficiencyLevel requiredProficiency;

    @Column(nullable = false)
    private boolean optional = false;

    @Column(length = 500)
    private String rationale;

    public RoleRequirement() {
    }

    public RoleRequirement(CareerRole careerRole, Skill skill, ProficiencyLevel requiredProficiency, boolean optional, String rationale) {
        this.careerRole = careerRole;
        this.skill = skill;
        this.requiredProficiency = requiredProficiency;
        this.optional = optional;
        this.rationale = rationale;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CareerRole getCareerRole() {
        return careerRole;
    }

    public void setCareerRole(CareerRole careerRole) {
        this.careerRole = careerRole;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ProficiencyLevel getRequiredProficiency() {
        return requiredProficiency;
    }

    public void setRequiredProficiency(ProficiencyLevel requiredProficiency) {
        this.requiredProficiency = requiredProficiency;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleRequirement that = (RoleRequirement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
