package com.itheima.qiyeshixun.po;

import java.util.Date;

public class SysRole {
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer isBuiltin;
    private Date createTime;
    private Date updateTime;
    private Byte delFlag;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getRoleKey() { return roleKey; }
    public void setRoleKey(String roleKey) { this.roleKey = roleKey; }
    public Integer getIsBuiltin() { return isBuiltin; }
    public void setIsBuiltin(Integer isBuiltin) { this.isBuiltin = isBuiltin; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Byte getDelFlag() { return delFlag; }
    public void setDelFlag(Byte delFlag) { this.delFlag = delFlag; }
}
