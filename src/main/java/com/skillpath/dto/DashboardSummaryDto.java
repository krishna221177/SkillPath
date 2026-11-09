package com.skillpath.dto;

import com.skillpath.model.CareerRole;
import com.skillpath.model.Student;

public class DashboardSummaryDto {
    private Student student;
    private CareerRole targetCareer;
    private int totalSkillsAdded;
    private double readinessPercentage;
    private int missingSkillsCount;
    private int skillsNeedingImprovementCount;
    private int satisfiedSkillsCount;
    private long totalRoadmapItems;
    private long completedRoadmapItems;
    private long inProgressRoadmapItems;
    private double roadmapProgressPercentage;

    public DashboardSummaryDto() {
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CareerRole getTargetCareer() {
        return targetCareer;
    }

    public void setTargetCareer(CareerRole targetCareer) {
        this.targetCareer = targetCareer;
    }

    public int getTotalSkillsAdded() {
        return totalSkillsAdded;
    }

    public void setTotalSkillsAdded(int totalSkillsAdded) {
        this.totalSkillsAdded = totalSkillsAdded;
    }

    public double getReadinessPercentage() {
        return readinessPercentage;
    }

    public void setReadinessPercentage(double readinessPercentage) {
        this.readinessPercentage = readinessPercentage;
    }

    public int getMissingSkillsCount() {
        return missingSkillsCount;
    }

    public void setMissingSkillsCount(int missingSkillsCount) {
        this.missingSkillsCount = missingSkillsCount;
    }

    public int getSkillsNeedingImprovementCount() {
        return skillsNeedingImprovementCount;
    }

    public void setSkillsNeedingImprovementCount(int skillsNeedingImprovementCount) {
        this.skillsNeedingImprovementCount = skillsNeedingImprovementCount;
    }

    public int getSatisfiedSkillsCount() {
        return satisfiedSkillsCount;
    }

    public void setSatisfiedSkillsCount(int satisfiedSkillsCount) {
        this.satisfiedSkillsCount = satisfiedSkillsCount;
    }

    public long getTotalRoadmapItems() {
        return totalRoadmapItems;
    }

    public void setTotalRoadmapItems(long totalRoadmapItems) {
        this.totalRoadmapItems = totalRoadmapItems;
    }

    public long getCompletedRoadmapItems() {
        return completedRoadmapItems;
    }

    public void setCompletedRoadmapItems(long completedRoadmapItems) {
        this.completedRoadmapItems = completedRoadmapItems;
    }

    public long getInProgressRoadmapItems() {
        return inProgressRoadmapItems;
    }

    public void setInProgressRoadmapItems(long inProgressRoadmapItems) {
        this.inProgressRoadmapItems = inProgressRoadmapItems;
    }

    public double getRoadmapProgressPercentage() {
        return roadmapProgressPercentage;
    }

    public void setRoadmapProgressPercentage(double roadmapProgressPercentage) {
        this.roadmapProgressPercentage = roadmapProgressPercentage;
    }
}
