package com.itheima.qiyeshixun.service.impl;

import com.github.pagehelper.PageHelper;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.OrderItemMapper;
import com.itheima.qiyeshixun.mapper.ProductMapper;
import com.itheima.qiyeshixun.po.OrderItemExample;
import com.itheima.qiyeshixun.po.Product;
import com.itheima.qiyeshixun.po.ProductExample;
import com.itheima.qiyeshixun.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Result<List<Product>> getPage(Integer pageNum, Integer pageSize, String name, Long categoryId) {
        PageHelper.startPage(pageNum, pageSize);
        ProductExample example = new ProductExample();
        ProductExample.Criteria criteria = example.createCriteria().andDelFlagEqualTo((byte) 0);
        if (name != null && !name.isEmpty()) {
            criteria.andProductNameLike("%" + name + "%");
        }
        if (categoryId != null) {
            criteria.andCategoryIdEqualTo(categoryId);
        }
        example.setOrderByClause("create_time DESC");
        return Result.success(productMapper.selectByExample(example));
    }

    @Override
    public Result<String> save(Product product) {
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        product.setDelFlag((byte) 0);
        productMapper.insertSelective(product);
        return Result.success("保存成功");
    }

    @Override
    public Result<String> update(Product product) {
        product.setUpdateTime(new Date());
        productMapper.updateByPrimaryKeySelective(product);
        return Result.success("更新成功");
    }

    @Override
    public Result<String> delete(Long id) {
        // 校验该商品是否已经存在于任何订单中
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andProductIdEqualTo(id).andDelFlagEqualTo((byte) 0);
        if (orderItemMapper.countByExample(example) > 0) {
            return Result.error("该商品已关联订单，禁止删除！");
        }

        Product product = new Product();
        product.setId(id);
        product.setDelFlag((byte) 1);
        productMapper.updateByPrimaryKeySelective(product);
        return Result.success("删除成功");
    }
}
