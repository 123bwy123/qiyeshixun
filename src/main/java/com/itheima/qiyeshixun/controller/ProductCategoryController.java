package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.po.Category;
import com.itheima.qiyeshixun.po.Product;
import com.itheima.qiyeshixun.service.CategoryService;
import com.itheima.qiyeshixun.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dc")
@CrossOrigin
public class ProductCategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    // --- 分类管理 ---
    @GetMapping("/category/list")
    public Result<List<Category>> getCategoryList(
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Long parentId) {
        return categoryService.getList(level, parentId);
    }

    @PostMapping("/category/save")
    public Result<String> saveCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @PutMapping("/category/update")
    public Result<String> updateCategory(@RequestBody Category category) {
        return categoryService.update(category);
    }

    @DeleteMapping("/category/delete/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        return categoryService.delete(id);
    }

    // --- 商品管理 ---
    @GetMapping("/product/list")
    public Result<List<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId) {
        return productService.getPage(pageNum, pageSize, name, categoryId);
    }

    @PostMapping("/product/save")
    public Result<String> saveProduct(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/product/update")
    public Result<String> updateProduct(@RequestBody Product product) {
        return productService.update(product);
    }

    @DeleteMapping("/product/delete/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        return productService.delete(id);
    }
}
