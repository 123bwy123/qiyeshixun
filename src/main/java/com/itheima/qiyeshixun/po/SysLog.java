package com.itheima.qiyeshixun.po;

import java.time.LocalDateTime;

public class SysLog {
    private Long id;
    private String operator;
    private String module;
    private String operation;
    private String method;
    private String params;
    private String result;
    private Long time;
    private String ip;
    private Integer type; // 0-正常, 1-异常
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public Long getTime() { return time; }
    public void setTime(Long time) { this.time = time; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
