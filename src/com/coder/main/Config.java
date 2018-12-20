package com.coder.main;

import com.coder.core.entity.Template;
import com.ich.core.base.JsonUtils;
import com.ich.core.base.TimeUtil;

import java.util.*;

public class Config {

    public static String author = "";
    /************输出路径************/
    public static String out = ".\\src";
    /************配置参数************/
    public static Map<String,String> configMap = new HashMap<>();
    /************模板注册************/
    public static Template currentTemplate = null;//当前使用的模板
    public static List<Template> templates = new ArrayList<>();//可使用模板列表
    static {
        templates.add(new Template("通用持久层生成模板","pojo2MService","Mybatis,MySQL"));
        templates.add(new Template("ICH通用代码生成模板","IAdminService","Mybatis,MySQL"));
    }

    public static void main(String[] args){
        Date date = new Date();
        List<Map<String,Object>> list = new ArrayList<>();
        for(int i=0;i<365;i++){
            Map<String,Object> data = new HashMap<>();
            String day = TimeUtil.format(new Date(date.getTime()-(i*TimeUtil.ONE_DAY)),"yyyy-MM-dd");
            data.put("month",day);
            data.put("A",i);
            data.put("B",i*2);
            data.put("C",i*3);
            list.add(data);
        }
        String json = JsonUtils.objectToJson(list);
        System.out.println(json);
    }
}
