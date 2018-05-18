package com.coder.extend.service.impl;

import com.coder.core.entity.Columns;
import com.coder.core.entity.Table;
import com.coder.core.service.CoreService;
import com.coder.core.service.TemplateCoreService;
import com.coder.core.util.ObjectHelper;
import com.coder.core.util.TableUtil;
import com.coder.main.Config;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * ICH专用后台代码构造器
 * 模板代码定义：
 * #{package} = 包名
 * #{import} = 引入包
 * #{EntityCNName} = 实体中文名称
 * #{EntityName} = 实体类名（首字母大写）
 * #{EntityLName} = 实体类名（首字母小写）
 * #{IDParams} = ID参数（有类型）
 * #{GetIDs} = 实体GETID
 * #{IDs} = ID参数（无类型）
 * #{HeaderMapping} = 主映射
 * #{MethodMapping} = 方法映射
 *
 * #{menu} = 父菜单编码
 * #{view_title}
 * #{view_remark}
 * #{view}
 * #{apply_form_li}
 * #{view_columns}
 * #{view_load_form_data}
 * #{ViewHeaderMapping}
 */
@Service
public class IAdminService implements TemplateCoreService {

    protected final Logger logger = Logger.getLogger(IAdminService.class);

    @Autowired
    private CoreService coreService;

    @Override
    public boolean execute() {
        //取参数
        String schema = Config.configMap.get("schema");
        String tables = Config.configMap.get("tables");
        String template_path = Config.configMap.get("template_path");
        String package_path = Config.configMap.get("package_path");
        String view_path = Config.configMap.get("view_path");
        if(ObjectHelper.isEmpty(schema)){
            logger.error("schema is null!");
            return false;
        }
        if(ObjectHelper.isEmpty(tables)){
            logger.error("tables is null!");
            return false;
        }
        if(ObjectHelper.isEmpty(template_path)){
            logger.error("template_path is null");
            return false;
        }
        if(ObjectHelper.isEmpty(package_path)){
            logger.error("package_path is null");
            return false;
        }
        if(ObjectHelper.isEmpty(view_path)){
            logger.error("view_path is null");
            return false;
        }
        //模板路径
        String service_impl_template_path = template_path + File.separator + "service_impl_template.txt";
        String service_template_path = template_path + File.separator + "service_template.txt";
        String controller_template_path = template_path + File.separator + "controller_template.txt";
        String jsp_template_path = template_path + File.separator + "jsp_template.txt";
        String js_template_path = template_path + File.separator + "js_template.txt";
        List<Table> table_list = new ArrayList<>();
        try {
            String table_array[] = tables.split(",");
            for (String tableName : table_array) {
                //获取表信息
                Table table = this.coreService.findTable(schema, tableName);
                if (ObjectHelper.isEmpty(table)) {
                    logger.error("构建代码失败：" + tableName + "不存在");
                    continue;
                }
                table.setPackageName(package_path);
                table_list.add(table);
            }
        }catch (Exception e){

        }
        String view = "admin";
        String ViewHeaderMapping = "admin";
        String HeaderMapping = "@RequestMapping(\"admin\")";
        String menu = "PID";

        for (Table table : table_list){
            String MethodMapping = TableUtil.lowerCase(table.getClassName());
            String EntityCNName = table.getCnName();
            String EntityName = table.getClassName();
            String EntityLName = TableUtil.lowerCase(table.getClassName());

            String EntityImport = "import " + package_path + ".pojo." + EntityName + ";\n" +
                    "import " + package_path + ".pojo." + EntityName + "Example;\n" ;
            String DaoImport =  "import " + package_path + ".mapper." + EntityName + "Mapper;\n";
            String ServiceImport = "import " + package_path + ".service."+EntityName+"Service;\n";

            String IDParams = "";
            String GetIDs = "";
            String IDs = "";
            for(Columns columns : table.getPrilist()){
                IDParams += columns.getFieldType() + " " + columns.getFieldName() + ",";
                GetIDs += EntityLName + ".get"+ TableUtil.upperCase(columns.getFieldName()) + "(),";
                IDs += columns.getFieldName() + ",";
            }
            if(ObjectHelper.isNotEmpty(IDParams))IDParams = IDParams.substring(0,IDParams.length()-1);
            if(ObjectHelper.isNotEmpty(GetIDs))GetIDs = GetIDs.substring(0,GetIDs.length()-1);
            if(ObjectHelper.isNotEmpty(IDs))IDs = IDs.substring(0,IDs.length()-1);

            String view_title = EntityCNName + "页";
            String view_remark = "备注";

            String apply_form_li = "";
            String view_columns = "";
            String view_load_form_data = "";
            for(Columns columns : table.getColumns()){
                String comment_arr[] = columns.getComment().split("\\|");
                apply_form_li += "\t\t\t\t<li class=\"fm_2l\">\n" +
                        "\t\t\t\t\t<label>"+comment_arr[0]+"：</label>\n" +
                        "\t\t\t\t\t<input name=\""+columns.getFieldName()+"\" class=\"easyui-validatebox textbox vipt\" data-options=\"required:true\">\n" +
                        "\t\t\t\t</li>\n";

                view_columns += "\t\t      {field:'"+columns.getFieldName()+"',title:'"+comment_arr[0]+"',align:\"center\",width:200},\n";

                view_load_form_data += "\t\t" + columns.getFieldName() +":row." + columns.getFieldName() + ",\n";
            }
            if(ObjectHelper.isNotEmpty(view_columns))view_columns = view_columns.substring(0,view_columns.length()-2);
            if(ObjectHelper.isNotEmpty(view_load_form_data))view_load_form_data = view_load_form_data.substring(0,view_load_form_data.length()-2);

            String package_dir = Config.out  + File.separator + package_path.replace(".", File.separator);
            try {
                //输出ServiceImpl
                {
                    //导入文件
                    String service_impl_template  = readFile2String(service_impl_template_path);
                    //创建路径及文件
                    File mkdirFile = new File(package_dir + File.separator +"service");
                    if(!mkdirFile.exists())mkdirFile.mkdirs();
                    File file = new File(package_dir + File.separator +"service" + File.separator + EntityName + "Service.java");
                    if (!file.exists()) file.createNewFile();
                    //写入包名
                    service_impl_template = service_impl_template.replace("#{package}", package_path + ".service;");
                    //写入引入
                    String imports = EntityImport;
                    service_impl_template = service_impl_template.replace("#{import}", imports);
                    //模板替换
                    service_impl_template = service_impl_template.replace("#{EntityName}", EntityName);
                    service_impl_template = service_impl_template.replace("#{EntityLName}", EntityLName);
                    service_impl_template = service_impl_template.replace("#{IDParams}", IDParams);
                    FileWriter entityWriter =new FileWriter(file);
                    entityWriter.write(service_impl_template);
                    entityWriter.close();
                }

                //输出Service
                {
                    //导入文件
                    String service_template  = readFile2String(service_template_path);
                    //创建路径及文件
                    File mkdirFile = new File(package_dir + File.separator +"service"+ File.separator +"impl");
                    if(!mkdirFile.exists())mkdirFile.mkdirs();
                    File file = new File(package_dir + File.separator +"service" + File.separator +"impl" + File.separator + EntityName + "ServiceImpl.java");
                    if (!file.exists()) file.createNewFile();
                    //写入包名
                    service_template = service_template.replace("#{package}", package_path + ".service.impl;");
                    //写入引入
                    String imports = EntityImport + DaoImport + ServiceImport;
                    service_template = service_template.replace("#{import}", imports);
                    //模板替换
                    service_template = service_template.replace("#{EntityName}", EntityName);
                    service_template = service_template.replace("#{EntityLName}", EntityLName);
                    service_template = service_template.replace("#{IDParams}", IDParams);
                    service_template = service_template.replace("#{GetIDs}", GetIDs);
                    service_template = service_template.replace("#{IDs}", IDs);
                    FileWriter entityWriter =new FileWriter(file);
                    entityWriter.write(service_template);
                    entityWriter.close();
                }
                //输出Controller
                {
                    //导入文件
                    String controller_template  = readFile2String(controller_template_path);
                    //创建路径及文件
                    File mkdirFile = new File(package_dir + File.separator +"controller");
                    if(!mkdirFile.exists())mkdirFile.mkdirs();
                    File file = new File(package_dir + File.separator +"controller" + File.separator + EntityName + "Controller.java");
                    if (!file.exists()) file.createNewFile();
                    //写入包名
                    controller_template = controller_template.replace("#{package}", package_path + ".controller;");
                    //写入引入
                    String imports = EntityImport + ServiceImport;
                    controller_template = controller_template.replace("#{import}", imports);
                    //模板替换

                    controller_template = controller_template.replace("#{EntityCNName}", EntityCNName);
                    controller_template = controller_template.replace("#{EntityName}", EntityName);
                    controller_template = controller_template.replace("#{EntityLName}", EntityLName);
                    controller_template = controller_template.replace("#{IDParams}", IDParams);
                    controller_template = controller_template.replace("#{GetIDs}", GetIDs);
                    controller_template = controller_template.replace("#{IDs}", IDs);

                    controller_template = controller_template.replace("#{HeaderMapping}", HeaderMapping);
                    controller_template = controller_template.replace("#{MethodMapping}", MethodMapping);
                    controller_template = controller_template.replace("#{menu}", menu);

                    FileWriter entityWriter =new FileWriter(file);
                    entityWriter.write(controller_template);
                    entityWriter.close();
                }
                //输出JSP
                {
                    //导入文件
                    String jsp_template  = readFile2String(jsp_template_path);
                    //创建路径及文件
                    File file = new File(view_path + File.separator + EntityLName + "Index.jsp");
                    if (!file.exists()) file.createNewFile();

                    jsp_template = jsp_template.replace("#{EntityCNName}", EntityCNName);
                    jsp_template = jsp_template.replace("#{EntityName}", EntityName);
                    jsp_template = jsp_template.replace("#{EntityLName}", EntityLName);
                    jsp_template = jsp_template.replace("#{ViewHeaderMapping}", ViewHeaderMapping);
                    jsp_template = jsp_template.replace("#{MethodMapping}", MethodMapping);
                    jsp_template = jsp_template.replace("#{menu}", menu);

                    jsp_template = jsp_template.replace("#{view_title}", view_title);
                    jsp_template = jsp_template.replace("#{view_remark}", view_remark);
                    jsp_template = jsp_template.replace("#{view}", view + "/" + EntityLName + "Index.js");
                    jsp_template = jsp_template.replace("#{apply_form_li}", apply_form_li);

                    FileWriter entityWriter =new FileWriter(file);
                    entityWriter.write(jsp_template);
                    entityWriter.close();
                }
                //输出JS
                {
                    String js_template  = readFile2String(js_template_path);
                    //创建路径及文件
                    File file = new File(view_path + File.separator + EntityLName + "Index.js");
                    if (!file.exists()) file.createNewFile();

                    js_template = js_template.replace("#{EntityName}", EntityName);
                    js_template = js_template.replace("#{EntityLName}", EntityLName);
                    js_template = js_template.replace("#{view_columns}", view_columns);
                    js_template = js_template.replace("#{view_load_form_data}", view_load_form_data);
                    FileWriter entityWriter =new FileWriter(file);
                    entityWriter.write(js_template);
                    entityWriter.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return true;
    }

    private String readFile2String(String filePath){
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        try {
            File file = new File(filePath);//定义一个file对象，用来初始化FileReader
            FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
            BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
            String s = "";
            while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
            }
            bReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
