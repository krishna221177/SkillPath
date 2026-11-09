package com.skillpath.model;

public enum SkillGapStatus {
    SATISFIED("Satisfied"),
    NEEDS_IMPROVEMENT("Needs Improvement"),
    MISSING("Missing");

    private final String displayName;

    SkillGapStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
