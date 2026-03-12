package com.itheima.qiyeshixun.mapper;

import com.itheima.qiyeshixun.po.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("<script>" +
            "SELECT * FROM category WHERE del_flag = 0 " +
            "<if test='level != null'>AND level = #{level}</if> " +
            "<if test='parentId != null'>AND parent_id = #{parentId}</if> " +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Category> selectList(@Param("level") Integer level, @Param("parentId") Long parentId);

    @Select("SELECT * FROM category WHERE id = #{id} AND del_flag = 0")
    Category selectById(Long id);

    @Insert("INSERT INTO category(name, parent_id, level, create_time, update_time, del_flag) " +
            "VALUES(#{name}, #{parentId}, #{level}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("UPDATE category SET name = #{name}, parent_id = #{parentId}, level = #{level}, update_time = NOW() " +
            "WHERE id = #{id}")
    int update(Category category);

    @Update("UPDATE category SET del_flag = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM category WHERE parent_id = #{id} AND del_flag = 0")
    long countChildren(Long id);
}
