package com.Main.dto;

import java.util.List;

public class StudentAnalyseDTO {
    private double overallGpa; // 总体GPA
    private double averageScore; // 平均分
    private int totalCreditsEarned; // 已获得学分
    private int totalCreditsTaken; // 已修学分
    private List<GradeDistributionDTO> gradeDistributionByCourseDTO; // 按课程的成绩分布
    private List<PerformanceTrendDTO> performanceTrendDTO; // 表现趋势

    public StudentAnalyseDTO() {}

    public double getOverallGpa() {
        return overallGpa;
    }

    public void setOverallGpa(double overallGpa) {
        this.overallGpa = overallGpa;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getTotalCreditsEarned() {
        return totalCreditsEarned;
    }

    public void setTotalCreditsEarned(int totalCreditsEarned) {
        this.totalCreditsEarned = totalCreditsEarned;
    }

    public int getTotalCreditsTaken() {
        return totalCreditsTaken;
    }

    public void setTotalCreditsTaken(int totalCreditsTaken) {
        this.totalCreditsTaken = totalCreditsTaken;
    }

    public List<GradeDistributionDTO> getGradeDistributionByCourseDTO() {
        return gradeDistributionByCourseDTO;
    }

    public void setGradeDistributionByCourse(List<GradeDistributionDTO> gradeDistributionByCourseDTO) {
        this.gradeDistributionByCourseDTO = gradeDistributionByCourseDTO;
    }

    public List<PerformanceTrendDTO> getPerformanceTrendDTO() {
        return performanceTrendDTO;
    }

    public void setPerformanceTrend(List<PerformanceTrendDTO> performanceTrendDTO) {
        this.performanceTrendDTO = performanceTrendDTO;
    }



}
