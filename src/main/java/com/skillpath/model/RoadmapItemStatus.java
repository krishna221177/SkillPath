package com.skillpath.model;

public enum RoadmapItemStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String label;

    RoadmapItemStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
