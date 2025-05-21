package com.Main.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.Main.entity.Course;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class CourseRowMapper implements RowMapper<Course> {
    @Override
    public Course mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("course_id"));
        course.setName(rs.getString("course_name"));
        course.setDescription(rs.getString("course_description"));
        course.setCategory(rs.getString("course_category"));
        course.setCredit(rs.getInt("course_credit"));
        course.setTeacherId(rs.getInt("course_teacher_id"));
        course.setHours_per_week(rs.getInt("course_hours_per_week"));
        return course;
    }
}
