package com.Main.service.exam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Statement;
import java.util.Map;

@Service
public class StudentAnswerResultService {

   @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Setter used for injecting JdbcTemplate in tests.
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Submit an answer for the given test question. If the student has already
     * answered this question for the test, the existing record will be updated.
     *
     * @param testId        test identifier
     * @param studentId     id of the student
     * @param questionId    id of the question
     * @param studentAnswer answer provided by the student
     * @return generated result_id when inserting or number of affected rows when updating
     */
    public int submitAnswer(int testId, int studentId, int questionId, String studentAnswer) {
        String queryQuestion = "SELECT answer, score FROM QuestionBank WHERE question_id = ?";
        Map<String, Object> question = jdbcTemplate.queryForMap(queryQuestion, questionId);
        String correctAnswer = (String) question.get("answer");
        Integer score = ((Number) question.get("score")).intValue();

        boolean isCorrect = studentAnswer != null && studentAnswer.equals(correctAnswer);
        int scoreObtained = isCorrect ? score : 0;

        String checkSql = "SELECT result_id FROM StudentAnswerResult " +
                          "WHERE test_id = ? AND student_id = ? AND question_id = ?";
        var existing = jdbcTemplate.query(checkSql,
                (rs, rowNum) -> rs.getInt("result_id"),
                testId, studentId, questionId);

        if (!existing.isEmpty()) {
            String updateSql =
                "UPDATE StudentAnswerResult SET student_answer = ?, is_correct = ?, " +
                "score_obtained = ?, answer_time = NOW() " +
                "WHERE test_id = ? AND student_id = ? AND question_id = ?";
            return jdbcTemplate.update(updateSql,
                    studentAnswer, isCorrect, scoreObtained,
                    testId, studentId, questionId);
        } else {
            String insertSql = "INSERT INTO StudentAnswerResult " +
                               "(test_id, student_id, question_id, student_answer, " +
                               "is_correct, score_obtained, answer_time) " +
                               "VALUES (?,?,?,?,?,?,NOW())";
            KeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                var ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, testId);
                ps.setObject(2, studentId);
                ps.setObject(3, questionId);
                ps.setString(4, studentAnswer);
                ps.setObject(5, isCorrect);
                ps.setObject(6, scoreObtained);
                return ps;
            }, holder);
            return holder.getKey().intValue();
        }
    }
}
