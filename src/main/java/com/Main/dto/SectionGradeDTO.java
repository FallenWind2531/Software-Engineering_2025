package com.Main.dto;


import com.Main.entity.User;

import java.util.List;

public class SectionGradeDTO {
    public List<User> user;
    public List<GradeDTO> grade;

    public SectionGradeDTO() {
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public List<GradeDTO> getGrade() {
        return grade;
    }

    public void setGrade(List<GradeDTO> grade) {
        this.grade = grade;
    }


}
