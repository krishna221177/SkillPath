package com.skillpath.exception;

public class SkillPathException extends RuntimeException {
    public SkillPathException(String message) {
        super(message);
    }

    public SkillPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
