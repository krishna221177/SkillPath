package com.skillpath.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "career_roles")
public class CareerRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role name is required")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "careerRole", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RoleRequirement> requirements = new ArrayList<>();

    public CareerRole() {
    }

    public CareerRole(String name, String description) {
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RoleRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RoleRequirement> requirements) {
        this.requirements = requirements;
    }

    public void addRequirement(RoleRequirement requirement) {
        requirements.add(requirement);
        requirement.setCareerRole(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CareerRole that = (CareerRole) o;
        return Objects.equals(id, that.id) || (name != null && name.equalsIgnoreCase(that.name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name != null ? name.toLowerCase() : null);
    }

    @Override
    public String toString() {
        return name;
    }
}
