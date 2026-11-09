package com.skillpath.model;

public enum RoadmapPriority {
    HIGH("High Priority"),
    MEDIUM("Medium Priority"),
    LOW("Low Priority / Optional");

    private final String label;

    RoadmapPriority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
