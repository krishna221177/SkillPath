package com.skillpath.dto;

import com.skillpath.model.CareerRole;
import com.skillpath.model.SkillGapStatus;

import java.util.ArrayList;
import java.util.List;

public class SkillGapAnalysisResultDto {
    private CareerRole careerRole;
    private List<SkillGapItemDto> items = new ArrayList<>();
    private int totalRequiredPoints = 0;
    private int totalAchievedPoints = 0;
    private double readinessPercentage = 0.0;
    private int satisfiedCount = 0;
    private int needsImprovementCount = 0;
    private int missingCount = 0;
    private int optionalCount = 0;

    public SkillGapAnalysisResultDto() {
    }

    public SkillGapAnalysisResultDto(CareerRole careerRole, List<SkillGapItemDto> items) {
        this.careerRole = careerRole;
        this.items = items != null ? items : new ArrayList<>();
        calculateMetrics();
    }

    private void calculateMetrics() {
        int reqSum = 0;
        int achSum = 0;
        int sat = 0;
        int imp = 0;
        int mis = 0;
        int opt = 0;

        for (SkillGapItemDto item : items) {
            if (item.isOptional()) {
                opt++;
                // Optional skills are NOT included in mandatory readiness denominator
                continue;
            }

            reqSum += item.getRequiredPoints();
            achSum += item.getAchievedPoints();

            if (item.getStatus() == SkillGapStatus.SATISFIED) {
                sat++;
            } else if (item.getStatus() == SkillGapStatus.NEEDS_IMPROVEMENT) {
                imp++;
            } else if (item.getStatus() == SkillGapStatus.MISSING) {
                mis++;
            }
        }

        this.totalRequiredPoints = reqSum;
        this.totalAchievedPoints = achSum;
        this.satisfiedCount = sat;
        this.needsImprovementCount = imp;
        this.missingCount = mis;
        this.optionalCount = opt;

        if (totalRequiredPoints > 0) {
            double raw = ((double) totalAchievedPoints / (double) totalRequiredPoints) * 100.0;
            // Ensure within [0.0, 100.0] and rounded to 2 decimal places
            double capped = Math.min(100.0, Math.max(0.0, raw));
            this.readinessPercentage = Math.round(capped * 100.0) / 100.0;
        } else {
            this.readinessPercentage = 0.0;
        }
    }

    public CareerRole getCareerRole() {
        return careerRole;
    }

    public List<SkillGapItemDto> getItems() {
        return items;
    }

    public int getTotalRequiredPoints() {
        return totalRequiredPoints;
    }

    public int getTotalAchievedPoints() {
        return totalAchievedPoints;
    }

    public double getReadinessPercentage() {
        return readinessPercentage;
    }

    public int getSatisfiedCount() {
        return satisfiedCount;
    }

    public int getNeedsImprovementCount() {
        return needsImprovementCount;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public int getOptionalCount() {
        return optionalCount;
    }

    public int getTotalMandatorySkills() {
        return satisfiedCount + needsImprovementCount + missingCount;
    }
}
