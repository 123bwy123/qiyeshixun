package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.Category;
import java.util.List;

public interface CategoryService {
    Result<List<Category>> getList(Integer level, Long parentId);
    Result<String> save(Category category);
    Result<String> update(Category category);
    Result<String> delete(Long id);
}
