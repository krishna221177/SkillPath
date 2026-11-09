package com.skillpath.model;

public enum ProficiencyLevel {
    BEGINNER(1, "Beginner"),
    BASIC(2, "Basic"),
    INTERMEDIATE(3, "Intermediate"),
    ADVANCED(4, "Advanced"),
    EXPERT(5, "Expert");

    private final int value;
    private final String label;

    ProficiencyLevel(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static ProficiencyLevel fromValue(int value) {
        for (ProficiencyLevel level : values()) {
            if (level.value == value) {
                return level;
            }
        }
        return BEGINNER;
    }

    @Override
    public String toString() {
        return label + " (" + value + "/5)";
    }
}
