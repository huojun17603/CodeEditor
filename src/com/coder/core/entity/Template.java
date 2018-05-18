package com.coder.core.entity;

public class Template {

    //模板名称
    private String name;
    //模板业务层名称
    private String service;
    //模板说明
    private String remark;

    public Template() {}

    public Template(String name,String service, String remark) {
        this.name = name;
        this.service = service;
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
