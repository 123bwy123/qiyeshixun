package com.itheima.qiyeshixun.po;

import java.util.Date;
import java.util.List;

public class SysPermission {
    private Long id;
    private String name;
    private Long parentId;
    private Byte type; // 1:菜单, 2:按钮
    private String permKey;
    private String routePath;
    private String componentPath;
    private String icon;
    private Integer orderNum;
    private Date createTime;
    private Date updateTime;
    private Byte delFlag;
    
    // 子权限列表 (用于前端树展示)
    private List<SysPermission> children;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Byte getType() { return type; }
    public void setType(Byte type) { this.type = type; }
    public String getPermKey() { return permKey; }
    public void setPermKey(String permKey) { this.permKey = permKey; }
    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }
    public String getComponentPath() { return componentPath; }
    public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Byte getDelFlag() { return delFlag; }
    public void setDelFlag(Byte delFlag) { this.delFlag = delFlag; }
    public List<SysPermission> getChildren() { return children; }
    public void setChildren(List<SysPermission> children) { this.children = children; }
}
