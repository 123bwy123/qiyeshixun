package com.itheima.qiyeshixun.service.impl;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.CategoryMapper;
import com.itheima.qiyeshixun.mapper.ProductMapper;
import com.itheima.qiyeshixun.po.Category;
import com.itheima.qiyeshixun.po.ProductExample;
import com.itheima.qiyeshixun.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<List<Category>> getList(Integer level, Long parentId) {
        return Result.success(categoryMapper.selectList(level, parentId));
    }

    @Override
    public Result<String> save(Category category) {
        categoryMapper.insert(category);
        return Result.success("保存成功");
    }

    @Override
    public Result<String> update(Category category) {
        categoryMapper.update(category);
        return Result.success("更新成功");
    }

    @Override
    public Result<String> delete(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) return Result.error("分类不存在");

        if (category.getLevel() == 1) {
            // 校验其下是否存在二级分类
            if (categoryMapper.countChildren(id) > 0) {
                return Result.error("该一级分类下存在二级分类，无法删除！");
            }
        } else if (category.getLevel() == 2) {
            // 校验其下是否存在商品
            ProductExample example = new ProductExample();
            example.createCriteria().andCategoryIdEqualTo(id).andDelFlagEqualTo((byte) 0);
            if (productMapper.countByExample(example) > 0) {
                return Result.error("该二级分类下存在商品，无法删除！");
            }
        }

        categoryMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
