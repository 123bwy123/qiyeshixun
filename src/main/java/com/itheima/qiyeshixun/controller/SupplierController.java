package com.itheima.qiyeshixun.controller;

import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.common.annotation.Log;
import com.itheima.qiyeshixun.po.Supplier;
import com.itheima.qiyeshixun.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/supplier")
@CrossOrigin
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * 综合查询/分页查询供应商
     */
    @GetMapping("/list")
    public Result<List<Supplier>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        return supplierService.getSupplierPage(pageNum, pageSize, name);
    }

    /**
     * 新增供应商
     */
    @PostMapping("/add")
    @Log(module = "配送中心", operation = "新增供应商")
    public Result<String> add(@RequestBody Supplier supplier) {
        return supplierService.saveSupplier(supplier);
    }

    /**
     * 修改供应商
     */
    @PutMapping("/update")
    @Log(module = "配送中心", operation = "修改供应商信息")
    public Result<String> update(@RequestBody Supplier supplier) {
        return supplierService.updateSupplier(supplier);
    }

    /**
     * 删除供应商 (带关联检查)
     */
    @DeleteMapping("/delete/{id}")
    @Log(module = "配送中心", operation = "删除供应商")
    public Result<String> delete(@PathVariable Long id) {
        return supplierService.deleteSupplier(id);
    }

    /**
     * 【供应商专属】拉取待发货采购单
     */
    @GetMapping("/pending")
    public Result<List<java.util.Map<String, Object>>> getPending() {
        return supplierService.getPendingDispatchOrders();
    }

    /**
     * 【供应商专属】执行发货装车
     */
    @PostMapping("/dispatch")
    public Result<String> dispatch(@RequestParam Long orderId) {
        return supplierService.dispatchOrder(orderId);
    }
}