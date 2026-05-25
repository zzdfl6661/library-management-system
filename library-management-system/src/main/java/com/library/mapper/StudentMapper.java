
package com.library.mapper;

import com.library.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    Student selectById(@Param("id") Long id);
    Student selectByStudentNo(@Param("studentNo") String studentNo);
    List<Student> selectAll();
    int insert(Student student);
    int update(Student student);
    int deleteById(@Param("id") Long id);
}
