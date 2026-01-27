package com.example.course.constant;

public enum ExamDuration {
    MIN_10(10),
    MIN_15(15),
    MIN_30(30),
    MIN_45(45),
    MIN_60(60),
    MIN_90(90),
    MIN_120(120);

    private final int minutes;

    ExamDuration(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }
}
