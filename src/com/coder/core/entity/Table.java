package com.coder.core.entity;

import java.util.List;

public class Table {

    //包名称
    private String packageName;
    //类名称
    private String className;
    //表名称
    private String tableName;
    //中文名称
    private String cnName;
    //表注解
    private String comment;
    //是否存在长文本类型
    private Boolean havText;
    //
    private Boolean havDate;
    //长文本类型列表
    private List<Columns> longlist;
    //主键列表
    private List<Columns> prilist;
    //所有字段列表
    private List<Columns> columns;

    private String PID;

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getHavText() {
        return havText;
    }

    public void setHavText(Boolean havText) {
        this.havText = havText;
    }

    public List<Columns> getPrilist() {
        return prilist;
    }

    public void setPrilist(List<Columns> prilist) {
        this.prilist = prilist;
    }

    public List<Columns> getColumns() {
        return columns;
    }

    public void setColumns(List<Columns> columns) {
        this.columns = columns;
    }

    public Boolean getHavDate() {
        return havDate;
    }

    public void setHavDate(Boolean havDate) {
        this.havDate = havDate;
    }

    public List<Columns> getLonglist() {
        return longlist;
    }

    public void setLonglist(List<Columns> longlist) {
        this.longlist = longlist;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }
}
