package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.entity.Student;
import com.library.service.LibraryCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private LibraryCardService libraryCardService;

    @GetMapping("/{sno}")
    public ApiResponse<Student> getStudent(@PathVariable String sno) {
        Student student = libraryCardService.getStudentBySno(sno);
        if (student == null) {
            return ApiResponse.error(404, "学生不存在");
        }
        return ApiResponse.success(student);
    }
}