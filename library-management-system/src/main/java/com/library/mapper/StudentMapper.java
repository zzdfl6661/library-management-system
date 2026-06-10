package com.library.mapper;

import com.library.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    Student selectBySno(@Param("sno") String sno);

    Student selectById(@Param("id") Long id);

    List<Student> selectAll();

    int insert(Student student);

    int updateById(Student student);

    List<Student> searchByKeyword(@Param("keyword") String keyword);
}