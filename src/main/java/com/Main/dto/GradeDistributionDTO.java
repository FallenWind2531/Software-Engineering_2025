package com.Main.dto;

public class GradeDistributionDTO {
    private String courseName; // 课程名称
    private double score; // 分数
    private double gpa; // GPA
    private float credit; // 学分

    public GradeDistributionDTO() {}

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }
}