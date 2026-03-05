package org.example.unipath2.application.exception;

public class DuplicateCourseException extends RuntimeException {
    public DuplicateCourseException(String message) {
        super(message);
    }
}
