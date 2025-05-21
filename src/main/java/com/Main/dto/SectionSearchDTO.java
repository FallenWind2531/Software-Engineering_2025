package com.Main.dto;

import com.Main.entity.Section;
public class SectionSearchDTO {
    private String classroom_location;
    private Section SectionInfo;

    // 构造函数
    public SectionSearchDTO() {
    }

    public SectionSearchDTO(Section sectionInfo, String classroom_location) {
        this.SectionInfo = sectionInfo;
        this.classroom_location = classroom_location;
    }

    //get and set
    public Section getSection() {
        return this.SectionInfo;
    }

    public void setSection(Section sectionInfo) {
        this.SectionInfo = sectionInfo;
    }

    public String getClassroom_location() {
        return classroom_location;
    }

    public void setClassroom_location(String classroom_location) {
        this.classroom_location = classroom_location;
    }
}
