package com.coder.main;

import com.coder.core.entity.Schema;
import com.coder.core.entity.Table;
import com.coder.core.mapper.TableMapper;
import com.coder.core.service.TemplateCoreService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class Main {

    public static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext(){
        if(applicationContext==null){
            applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
        }
        return applicationContext;
    }

    public static void main(String[] args) {
        Config.configMap.put("iscomment","false");
        Config.configMap.put("schema","xxl");
        Config.configMap.put("tables","u_proxy");
        Config.configMap.put("package_path","com.xxl.shop");
        Config.author = "霍俊";
        Config.currentTemplate = Config.templates.get(0);
        //TemplateCoreService TemplateCoreService = (TemplateCoreService)getApplicationContext().getBean(Config.currentTemplate.getService());
        //TemplateCoreService.execute();

        Config.configMap.put("PID","xxl-shop-dl-index");
        Config.configMap.put("js_path","xxl/shop");//JS引入路径
        Config.configMap.put("template_path",".\\resources\\template\\最新版本\\标准版本");
        Config.configMap.put("view_out",".\\webapp\\WEB-INF");
        Config.currentTemplate = Config.templates.get(1);
        TemplateCoreService TemplateCoreService2 = (TemplateCoreService)getApplicationContext().getBean(Config.currentTemplate.getService());
        TemplateCoreService2.execute();
    }
}
