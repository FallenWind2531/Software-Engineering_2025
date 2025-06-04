package com.Main.web;

import com.Main.service.QuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.Main.entity.QuestionBank;

@RestController
@RequestMapping("/test/questions")
public class QuestionBankController {

    @Autowired
    private QuestionBankService questionBankService;

    @PostMapping("/addQuestion")
    public void addBank(@RequestBody QuestionBank questionBank) {
        questionBankService.add_question(questionBank);
    }

    @DeleteMapping("/delQuestion")
    public void deleteBank(@RequestParam int question_id) {
        questionBankService.del_question(question_id);
    }

    @GetMapping("/course/{teacherId}")
    public List<Map<String, Object>> getCourses(@PathVariable int teacherId) {
        return questionBankService.get_course(teacherId);
    }

    @GetMapping("/getQuestionByCourse")
    public List<Map<String, Object>> getQuestionByCourse(
            @RequestParam int courseId,
            @RequestParam(required = false, defaultValue = "0") int chapter_id) {
        if (chapter_id == 0) {
            return questionBankService.get_question_by_course(courseId, 0);
        }
        return questionBankService.get_question_by_course(courseId, chapter_id);
    }

    @GetMapping("/searchQuestions")
    public List<Map<String, Object>> searchQuestions(
            @RequestParam int courseId,
            @RequestParam int bankId,
            @RequestParam(required = false) String queName,
            @RequestParam(required = false) String queType) {
        return questionBankService.search_que(courseId, bankId, queName, queType);
    }
}
