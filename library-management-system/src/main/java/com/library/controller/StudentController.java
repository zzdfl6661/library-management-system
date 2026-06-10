
package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.entity.Student;
import com.library.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    @Autowired
    private StudentMapper studentMapper;

    @GetMapping("/{studentNo}")
    public ApiResponse<Student> getStudentByStudentNo(@PathVariable String studentNo) {
        Student student = studentMapper.selectByStudentNo(studentNo);
        if (student == null) {
            throw new com.library.exception.BusinessException("学生不存在");
        }
        return ApiResponse.success(student);
    }
}
