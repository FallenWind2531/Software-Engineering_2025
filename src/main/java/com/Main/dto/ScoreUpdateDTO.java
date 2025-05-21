package com.Main.dto;

import java.util.List;

public class ScoreUpdateDTO {

    private List<StudentScoreDTO> studentScores;

    public ScoreUpdateDTO() {
    }

    public ScoreUpdateDTO(List<StudentScoreDTO> studentScores) {
        this.studentScores = studentScores;
    }

    public List<StudentScoreDTO> getStudentScores() {
        return studentScores;
    }

    public void setStudentScores(List<StudentScoreDTO> studentScores) {
        this.studentScores = studentScores;
    }

    public static class StudentScoreDTO {
        private int student_id;
        private int score;
        private float gpa;

        public StudentScoreDTO() {
        }

        public int getStudent_id() {
            return student_id;
        }

        public void setStudent_id(int student_id) {
            this.student_id = student_id;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public float getGpa() {
            return gpa;
        }

        public void setGpa(float gpa) {
            this.gpa = gpa;
        }
    }
}
