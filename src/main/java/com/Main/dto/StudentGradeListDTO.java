package com.Main.dto;

import com.Main.entity.GradeBase;
import com.Main.entity.GradeComponent;

import java.util.List;

public class StudentGradeListDTO {
    private List<StudentGradeDTO> StudentGradeList;

    public StudentGradeListDTO() {}

    public void SetStudentGradeList(List<StudentGradeDTO> StudentGradeList) {
        this.StudentGradeList = StudentGradeList;
    }

    public List<StudentGradeDTO> getStudentGradeList() {
        return StudentGradeList;
    }
}
