package com.library.mapper;

import com.library.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
    Admin selectByUsername(@Param("username") String username);

    Admin selectById(@Param("id") Long id);

    List<Admin> selectAll();

    int insert(Admin admin);

    int updateById(Admin admin);
}