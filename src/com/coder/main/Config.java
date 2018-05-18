package com.coder.main;

import com.coder.core.entity.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public static String author = "";
    /************输出路径************/
    public static String out = "";
    /************配置参数************/
    public static Map<String,String> configMap = new HashMap<>();
    /************模板注册************/
    public static Template currentTemplate = null;//当前使用的模板
    public static List<Template> templates = new ArrayList<>();//可使用模板列表
    static {
        templates.add(new Template("通用持久层生成模板","pojo2MService","Mybatis,MySQL"));
        templates.add(new Template("ssss","IAdminService","Mybatis,MySQL"));
    }


}
