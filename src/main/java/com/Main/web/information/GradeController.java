package com.Main.web.information;

import com.Main.dto.*;
import com.Main.entity.Course;
import com.Main.entity.User;
import com.Main.service.UserService;
import com.Main.service.information.CourseService;
import com.Main.service.information.GradeService;
import com.Main.service.information.SectionService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/information/api/v1")
public class GradeController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GradeService gradeService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private UserService userService;


    /**
     * 获取学生成绩
     * @param grade_id 成绩ID
     * @param request 请求对象
     * @return 成绩
     */
    @GetMapping("/student/grades/{grade_id}")
    public ResponseEntity<ApiResponseDTO<StudentGradeDTO>> getGrades(
            @PathVariable("grade_id") int grade_id,
            HttpServletRequest request
    ) {
        logger.info("获取学生成绩列表, grade_id: {}", grade_id);

        // 获取学生成绩
        try {
            int student_id = (int) request.getAttribute("userId");
            StudentGradeDTO gradeDTO = gradeService.getStudentGradeDetail(student_id, grade_id);

            SectionSearchDTO sectionDTO = sectionService.getSectionById(gradeDTO.getSection_id());
            Course course = courseService.getCourseById(gradeDTO.getCourse_id());
            User user = userService.getUserById(course.getTeacherId());

            String semester = sectionDTO.getSection().getSemester();
            int sec_year = sectionDTO.getSection().getSecYear();
            String course_name = course.getName();
            String teacher_name = user.getName();

            // 设置成绩信息
            gradeDTO.setSemester(semester);
            gradeDTO.setSec_year(sec_year);
            gradeDTO.setCourse_name(course_name);
            gradeDTO.setTeacher_name(teacher_name);

            return ResponseEntity.ok(ApiResponseDTO.success("获取成功",gradeDTO));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.ok(ApiResponseDTO.error(500,"获取失败"));
        }
    }

    /**
     * 获取学生成绩列表
     * @param request 请求对象
     * @param semester 学期
     * @param sec_year 学年
     * @param course_id 课程ID
     * @return 成绩列表
     */
    @GetMapping("/student/grades")
    public ResponseEntity<ApiResponseDTO<StudentGradeListDTO>> getGradesList(
            HttpServletRequest request,
            @RequestParam(value = "semester", required = false) String semester,
            @RequestParam(value = "sec_year", required = false) Integer sec_year,
            @RequestParam(value = "course_id", required = false) Integer course_id
    ) {
        logger.info("获取学生成绩列表, semester: {}, sec_year: {}, course_id: {}", semester, sec_year, course_id);

        // 获取学生成绩列表
        try {
            int student_id = (int) request.getAttribute("userId");
            StudentGradeListDTO gradeListDTO = gradeService.getStudentGradeList(student_id, semester, sec_year, course_id);

            // 补充每个成绩的详细信息
            for (StudentGradeDTO gradeDTO : gradeListDTO.getStudentGradeList()) {
                SectionSearchDTO sectionDTO = sectionService.getSectionById(gradeDTO.getSection_id());
                Course course = courseService.getCourseById(gradeDTO.getCourse_id());
                User user = userService.getUserById(course.getTeacherId());

                String course_name = course.getName();
                String teacher_name = user.getName();
                String semesterValue = sectionDTO.getSection().getSemester();
                int sec_yearValue = sectionDTO.getSection().getSecYear();

                // 设置成绩信息
                gradeDTO.setCourse_name(course_name);
                gradeDTO.setTeacher_name(teacher_name);
                gradeDTO.setSemester(semesterValue);
                gradeDTO.setSec_year(sec_yearValue);
            }

            return ResponseEntity.ok(ApiResponseDTO.success("获取成功", gradeListDTO));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.ok(ApiResponseDTO.error(500, "获取失败"));
        }
    }

    /**
     * 教师提交成绩
     * @param request 请求对象
     * @param scoreUpdateDTO 成绩更新对象
     * @param section_id 开课ID
     * @return 成绩列表
     */
    @PostMapping("/teacher/sections/{section_id}/grades")
    public ResponseEntity<ApiResponseDTO<Map<String,Integer>>> submitGrades(
            HttpServletRequest request,
            @RequestBody ScoreUpdateDTO scoreUpdateDTO,
            @PathVariable("section_id") int section_id
    ) {
        Map<String, Integer> result = new HashMap<>();

        // 获取教师ID
        int teacher_id = (int) request.getAttribute("userId");
        logger.info("教师提交成绩, teacher_id: {}, section_id: {}", teacher_id, section_id);

        boolean status = false;
        for (ScoreUpdateDTO.StudentScoreDTO scoreDTO : scoreUpdateDTO.getStudentScores()) {
            int student_id = scoreDTO.getStudent_id();
            int score = scoreDTO.getScore();
            float gpa = scoreDTO.getGpa();
            int course_id = sectionService.getSectionById(section_id).getSection().getCourseId();
            // 提交成绩
            try {
                status = gradeService.submitStudentGrades(section_id, student_id, course_id, score, gpa);
            } catch (Exception e) {
                logger.error("提交成绩失败: {}", e.getMessage());
                result.put("success_updates", 0);
                result.put("failed_updates", 1);
                return ResponseEntity.ok(ApiResponseDTO.error(500, "提交成绩失败"));
            }
        }
        if (status) {
            result.put("success_updates", 1);
            result.put("failed_updates", 0);
        } else {
            result.put("success_updates", 0);
            result.put("failed_updates", 1);
        }
        return ResponseEntity.ok(ApiResponseDTO.success("成绩提交/更新成功", result));
    }

    /**
     * 教师获取班级成绩
     * @param section_id 开课ID
     * @param student_id 学生ID
     * @param student_name 学生姓名
     * @return 成绩列表
     */
    @GetMapping("/teacher/sections/{section_id}/grades")
    public ResponseEntity<ApiResponseDTO<SectionGradeDTO>> getSectionGrades(
            @PathVariable("section_id") int section_id,
            @RequestParam(value = "student_id", required = false) Integer student_id,
            @RequestParam(value = "student_name", required = false) String student_name
    ) {
        logger.info("教师获取班级成绩, section_id: {}, student_id: {}, student_name: {}", section_id, student_id, student_name);

        // 获取班级成绩
        try {
            SectionGradeDTO sectionGradeDTO = gradeService.getSectionStudentGrades(section_id,  student_name,student_id);
            return ResponseEntity.ok(ApiResponseDTO.success("获取成功", sectionGradeDTO));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.ok(ApiResponseDTO.error(500, "获取失败"));
        }
    }


}
