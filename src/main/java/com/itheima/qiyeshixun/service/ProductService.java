package com.itheima.qiyeshixun.service;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.Product;
import java.util.List;

public interface ProductService {
    Result<List<Product>> getPage(Integer pageNum, Integer pageSize, String name, Long categoryId);
    Result<String> save(Product product);
    Result<String> update(Product product);
    Result<String> delete(Long id);
}
