package com.Main.web;

import com.Main.RowMapper.StudentAnswerResultRowMapper;
import com.Main.entity.StudentAnswerResult;
import com.Main.service.StudentAnswerResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test/answer")
public class StudentAnswerResultController {

    @Autowired
    private StudentAnswerResultService service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** Setter for injection in tests */
    public void setService(StudentAnswerResultService service) {
        this.service = service;
    }

    /** Setter for injection in tests */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 单题提交 */
    @PostMapping("/submit")
    public StudentAnswerResult submit(@RequestBody StudentAnswerResult answer) {
        service.submitAnswer(
                answer.getTestId(),
                answer.getStudentId(),
                answer.getQuestionId(),
                answer.getStudentAnswer());

        String sql = "SELECT * FROM student_answer_result " +
                     "WHERE test_id = ? AND student_id = ? AND question_id = ?";
        return jdbcTemplate.queryForObject(
                sql,
                new StudentAnswerResultRowMapper(),
                answer.getTestId(),
                answer.getStudentId(),
                answer.getQuestionId());
    }

    /** 批量提交 */
    @PostMapping("/submitBatch")
    public List<StudentAnswerResult> submitBatch(@RequestBody List<StudentAnswerResult> answers) {
        List<StudentAnswerResult> saved = new ArrayList<>();

        String sql = "SELECT * FROM student_answer_result " +
                     "WHERE test_id = ? AND student_id = ? AND question_id = ?";

        for (StudentAnswerResult ans : answers) {
            service.submitAnswer(
                    ans.getTestId(),
                    ans.getStudentId(),
                    ans.getQuestionId(),
                    ans.getStudentAnswer());

            StudentAnswerResult persisted = jdbcTemplate.queryForObject(
                    sql,
                    new StudentAnswerResultRowMapper(),
                    ans.getTestId(),
                    ans.getStudentId(),
                    ans.getQuestionId());

            saved.add(persisted);
        }
        return saved;
    }
}
