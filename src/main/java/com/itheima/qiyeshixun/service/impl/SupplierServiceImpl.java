package com.itheima.qiyeshixun.service.impl;

import com.github.pagehelper.PageHelper;
import com.itheima.qiyeshixun.common.Result;
import com.itheima.qiyeshixun.mapper.ProductMapper;
import com.itheima.qiyeshixun.mapper.SupplierMapper;
import com.itheima.qiyeshixun.po.ProductExample;
import com.itheima.qiyeshixun.po.Supplier;
import com.itheima.qiyeshixun.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<List<Supplier>> getSupplierPage(Integer pageNum, Integer pageSize, String name) {
        PageHelper.startPage(pageNum, pageSize);
        List<Supplier> list = supplierMapper.searchSuppliers(name);
        return Result.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveSupplier(Supplier supplier) {
        // 1. 业务校验：供应商名称唯一性 (基于需求中的编号/身份唯一性，在缺失code字段下通过名称实现)
        Supplier existing = supplierMapper.selectByName(supplier.getSupplierName());
        if (existing != null) {
            return Result.error("新增失败：已存在名为 [" + supplier.getSupplierName() + "] 的供应商！");
        }

        // 2. 补全基础字段与默认值 (应对数据库 NOT NULL 约束)
        if (supplier.getFax() == null) supplier.setFax("");
        if (supplier.getZipCode() == null) supplier.setZipCode("");
        if (supplier.getLegalPerson() == null) supplier.setLegalPerson("");
        if (supplier.getRemark() == null) supplier.setRemark("");
        
        supplier.setCreateTime(new Date());
        supplier.setUpdateTime(new Date());
        supplier.setDelFlag((byte) 0);

        // 3. 执行插入
        int rows = supplierMapper.insertSelective(supplier);
        return rows > 0 ? Result.success("供应商新增成功") : Result.error("新增失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateSupplier(Supplier supplier) {
        if (supplier.getId() == null) {
            return Result.error("更新失败：ID 不能为空");
        }

        // 1. 唯一性校验：名称冲突检查（排除自己）
        Supplier existing = supplierMapper.selectByName(supplier.getSupplierName());
        if (existing != null && !existing.getId().equals(supplier.getId())) {
            return Result.error("更新失败：供应商名称 [" + supplier.getSupplierName() + "] 已被其他供应商占用！");
        }

        // 2. 补全更新时间
        supplier.setUpdateTime(new Date());

        // 3. 执行更新
        int rows = supplierMapper.updateByPrimaryKeySelective(supplier);
        return rows > 0 ? Result.success("供应商修改成功") : Result.error("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteSupplier(Long id) {
        // 🚨 极其重要业务约束：关联防误删机制
        // 在删除前，必须先去商品表 (product) 中查询是否存在关联了该 supplier_id 的记录
        ProductExample example = new ProductExample();
        example.createCriteria().andSupplierIdEqualTo(id).andDelFlagEqualTo((byte) 0);
        long count = productMapper.countByExample(example);
        
        if (count > 0) {
            return Result.error("该供应商下存在关联商品，禁止删除！");
        }

        // 执行逻辑删除
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setDelFlag((byte) 1);
        supplier.setUpdateTime(new Date());
        
        int rows = supplierMapper.updateByPrimaryKeySelective(supplier);
        return rows > 0 ? Result.success("供应商删除成功") : Result.error("删除失败");
    }

    @Override
    public Result<List<java.util.Map<String, Object>>> getPendingDispatchOrders() {
        return Result.success(supplierMapper.selectPendingDispatchOrders());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> dispatchOrder(Long orderId) {
        int rows = supplierMapper.dispatchOrder(orderId);
        return rows > 0 ? Result.success("发货成功，已通知中心库房准备接车") : Result.error("发货失败");
    }
}
