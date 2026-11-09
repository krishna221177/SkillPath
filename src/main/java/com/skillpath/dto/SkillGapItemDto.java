package com.skillpath.dto;

import com.skillpath.model.ProficiencyLevel;
import com.skillpath.model.Skill;
import com.skillpath.model.SkillGapStatus;

public class SkillGapItemDto {
    private Skill skill;
    private String skillName;
    private ProficiencyLevel requiredProficiency;
    private ProficiencyLevel currentProficiency;
    private int requiredPoints;
    private int currentPoints;
    private int achievedPoints;
    private SkillGapStatus status;
    private boolean optional;
    private String improvementNeeded;
    private String rationale;

    public SkillGapItemDto() {
    }

    public SkillGapItemDto(Skill skill, ProficiencyLevel requiredProficiency, ProficiencyLevel currentProficiency,
                           boolean optional, String rationale) {
        this.skill = skill;
        this.skillName = skill != null ? skill.getName() : "";
        this.requiredProficiency = requiredProficiency;
        this.currentProficiency = currentProficiency;
        this.optional = optional;
        this.rationale = rationale;

        this.requiredPoints = requiredProficiency != null ? requiredProficiency.getValue() : 0;
        this.currentPoints = currentProficiency != null ? currentProficiency.getValue() : 0;

        // Achieved points capped at required points
        this.achievedPoints = Math.min(this.currentPoints, this.requiredPoints);

        // Determine status
        if (currentProficiency == null || this.currentPoints == 0) {
            this.status = SkillGapStatus.MISSING;
            this.improvementNeeded = "Missing: +" + this.requiredPoints + " level" + (this.requiredPoints > 1 ? "s" : "") + " needed";
        } else if (this.currentPoints >= this.requiredPoints) {
            this.status = SkillGapStatus.SATISFIED;
            int excess = this.currentPoints - this.requiredPoints;
            this.improvementNeeded = excess > 0 ? "Satisfied (Exceeds requirement by " + excess + ")" : "Satisfied (Meets requirement)";
        } else {
            this.status = SkillGapStatus.NEEDS_IMPROVEMENT;
            int gap = this.requiredPoints - this.currentPoints;
            this.improvementNeeded = "Needs Improvement: +" + gap + " level" + (gap > 1 ? "s" : "") + " (" + currentProficiency.getLabel() + " -> " + requiredProficiency.getLabel() + ")";
        }
    }

    public Skill getSkill() {
        return skill;
    }

    public String getSkillName() {
        return skillName;
    }

    public ProficiencyLevel getRequiredProficiency() {
        return requiredProficiency;
    }

    public ProficiencyLevel getCurrentProficiency() {
        return currentProficiency;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public int getAchievedPoints() {
        return achievedPoints;
    }

    public SkillGapStatus getStatus() {
        return status;
    }

    public boolean isOptional() {
        return optional;
    }

    public String getImprovementNeeded() {
        return improvementNeeded;
    }

    public String getRationale() {
        return rationale;
    }
}
