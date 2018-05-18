package com.coder.extend.service.impl;

import com.coder.core.entity.Columns;
import com.coder.core.entity.Table;
import com.coder.core.mapper.TableMapper;
import com.coder.core.service.CoreService;
import com.coder.core.service.TemplateCoreService;
import com.coder.core.service.impl.CoreServiceImpl;
import com.coder.core.util.ObjectHelper;
import com.coder.core.util.TableUtil;
import com.coder.main.Config;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用持久层生成模板
 * 使用：Mybatis,MySQL
 */
@Service
public class Pojo2MService implements TemplateCoreService {

    protected final Logger logger = Logger.getLogger(Pojo2MService.class);

    @Autowired
    private CoreService coreService;

    @Override
    public boolean execute() {
        /** 获取配置 */
        String schema = Config.configMap.get("schema");
        String tables = Config.configMap.get("tables");
        String package_path = Config.configMap.get("package_path");
        if(ObjectHelper.isEmpty(schema)){
            logger.error("schema is null!");
            return false;
        }
        if(ObjectHelper.isEmpty(tables)){
            logger.error("tables is null!");
            return false;
        }
        if(ObjectHelper.isEmpty(package_path)){
            logger.error("package_path is null");
            return false;
        }
        /** 构建路径 */
        String pojo_package = package_path +".pojo";
        String mapper_package = package_path +".mapper";
        String pojo_path = Config.out + File.separator + pojo_package.replace(".",File.separator);
        String mapper_path = Config.out + File.separator + mapper_package.replace(".",File.separator);
        File mkdirFile = new File(pojo_path);
        if(!mkdirFile.exists())mkdirFile.mkdirs();
        mkdirFile = new File(mapper_path);
        if(!mkdirFile.exists())mkdirFile.mkdirs();

        try {
            String table_array[] = tables.split(",");
            for (String tableName : table_array) {
                //获取表信息
                Table table = this.coreService.findTable(schema,tableName);
                if(ObjectHelper.isEmpty(table)){
                    logger.error("构建代码失败："+tableName+"不存在");
                    continue;
                }
                String ClassName = table.getClassName();
                table.setPackageName(package_path);
                //创建文件
                File entityFile = new File(pojo_path + File.separator + ClassName + ".java");
                if (!entityFile.exists()) entityFile.createNewFile();
                File exampleFile = new File(pojo_path + File.separator + ClassName + "Example.java");
                if (!exampleFile.exists()) exampleFile.createNewFile();
                File mapperFile = new File(mapper_path + File.separator + ClassName + "Mapper.java");
                if (!mapperFile.exists()) mapperFile.createNewFile();
                File mapperXMLFile = new File(mapper_path + File.separator + ClassName + "Mapper.xml");
                if (!mapperXMLFile.exists()) mapperXMLFile.createNewFile();
                //写入文件
                FileWriter entityWriter =new FileWriter(entityFile);
                entityWriter.write(writePojo(table));
                entityWriter.close();
                FileWriter exampleWriter =new FileWriter(exampleFile);
                exampleWriter.write(writeExample(table));
                exampleWriter.close();
                FileWriter mapperWriter =new FileWriter(mapperFile);
                mapperWriter.write(writeMapper(table));
                mapperWriter.close();
                FileWriter mapperXMLWriter =new FileWriter(mapperXMLFile);
                mapperXMLWriter.write(writeMapperXML(table));
                mapperXMLWriter.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("构建代码失败："+e.getMessage());
        }

        return true;
    }



    private String writePojo(Table table) {
        String iscomment = Config.configMap.get("iscomment");

        StringBuffer buffer = new StringBuffer();
        buffer.append("package " + table.getPackageName() + ".pojo;\n\n");
        Map<String,String> typeMap = new HashMap<>();
        for(Columns columns : table.getColumns()){
            typeMap.put(columns.getFieldType(),columns.getFieldType());
        }
        for (Map.Entry<String, String> entry : typeMap.entrySet()) {
            if(entry.getKey().equals("BigDecimal")){
                buffer.append("import java.math.BigDecimal;\n");
            }
            if(entry.getKey().equals("Date")){
                buffer.append("import java.util.Date;\n");
            }
        }
        if(Boolean.valueOf(iscomment)){
            buffer.append("/**\n");
            buffer.append(" * "+table.getComment()+"\n");
//            buffer.append(" * @author "+Config.author+"\n");
            buffer.append(" */\n");
        }
        buffer.append("\n");
        buffer.append("public class "+table.getClassName()+" {\n");
        List<String> getters = new ArrayList<>();
        for(Columns columns : table.getColumns()){
            if(Boolean.valueOf(iscomment)) {
                buffer.append("\t//"+ columns.getComment() +"\n");
            }
            buffer.append("\tprivate "+ columns.getFieldType() +" "+ columns.getFieldName() +";\n\n");
            String getter_string = "\tpublic "+ columns.getFieldType() +" get"+ TableUtil.upperCase(columns.getFieldName()) +"(){\n" +
                    "\t\treturn "+columns.getFieldName()+";\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic void set"+ TableUtil.upperCase(columns.getFieldName()) +"("+columns.getFieldType()+" "+columns.getFieldName()+"){\n" +
                    "\t\tthis."+columns.getFieldName()+"="+columns.getFieldName()+";\n" +
                    "\t}\n" +
                    "\n";
            getters.add(getter_string);
        }
        for(String getter : getters){
            buffer.append(getter);
        }
        buffer.append("}");
        return buffer.toString();
    }

    private String writeExample(Table table) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("package " + table.getPackageName() + ".pojo;\n\n");
        buffer.append("import java.util.ArrayList;\n");
        buffer.append("import java.util.List;\n");
        buffer.append("import java.util.Iterator;\n");
        Map<String,String> typeMap = new HashMap<>();
        for(Columns columns : table.getColumns()){
            typeMap.put(columns.getFieldType(),columns.getFieldType());
        }
        for (Map.Entry<String, String> entry : typeMap.entrySet()) {
            if(entry.getKey().equals("BigDecimal")){
                buffer.append("import java.math.BigDecimal;\n");
            }
            if(entry.getKey().equals("Date")){
                buffer.append("import java.util.Date;\n");
            }
        }
        buffer.append("\n");
        buffer.append("public class "+table.getClassName()+"Example{\n");
        buffer.append("    protected String orderByClause;\n\n");
        buffer.append("    protected boolean distinct;\n\n");
        buffer.append("    protected List<Criteria> oredCriteria;\n\n");

        buffer.append("    public "+table.getClassName()+"Example() {\n" +
                "        oredCriteria = new ArrayList<Criteria>();\n" +
                "    }\n\n");
        buffer.append("    public void setOrderByClause(String orderByClause) {\n" +
                "        this.orderByClause = orderByClause;\n" +
                "    }\n\n");
        buffer.append("    public String getOrderByClause() {\n" +
                "        return orderByClause;\n" +
                "    }\n\n");
        buffer.append("    public void setDistinct(boolean distinct) {\n" +
                "        this.distinct = distinct;\n" +
                "    }\n\n");
        buffer.append("    public boolean isDistinct() {\n" +
                "        return distinct;\n" +
                "    }\n\n");
        buffer.append("    public List<Criteria> getOredCriteria() {\n" +
                "        return oredCriteria;\n" +
                "    }\n\n");
        buffer.append("    public void or(Criteria criteria) {\n" +
                "        oredCriteria.add(criteria);\n" +
                "    }\n\n");
        buffer.append("    public Criteria or() {\n" +
                "        Criteria criteria = createCriteriaInternal();\n" +
                "        oredCriteria.add(criteria);\n" +
                "        return criteria;\n" +
                "    }\n\n");
        buffer.append("    public Criteria createCriteria() {\n" +
                "        Criteria criteria = createCriteriaInternal();\n" +
                "        if (oredCriteria.size() == 0) {\n" +
                "            oredCriteria.add(criteria);\n" +
                "        }\n" +
                "        return criteria;\n" +
                "    }\n\n");
        buffer.append("    protected Criteria createCriteriaInternal() {\n" +
                "        Criteria criteria = new Criteria();\n" +
                "        return criteria;\n" +
                "    }\n\n");
        buffer.append("    public void clear() {\n" +
                "        oredCriteria.clear();\n" +
                "        orderByClause = null;\n" +
                "        distinct = false;\n" +
                "    }\n\n");
        buffer.append("    protected abstract static class GeneratedCriteria {\n" +
                "        protected List<Criterion> criteria;\n" +
                "\n" +
                "        protected GeneratedCriteria() {\n" +
                "            super();\n" +
                "            criteria = new ArrayList<Criterion>();\n" +
                "        }\n" +
                "\n" +
                "        public boolean isValid() {\n" +
                "            return criteria.size() > 0;\n" +
                "        }\n" +
                "\n" +
                "        public List<Criterion> getAllCriteria() {\n" +
                "            return criteria;\n" +
                "        }\n" +
                "\n" +
                "        public List<Criterion> getCriteria() {\n" +
                "            return criteria;\n" +
                "        }\n" +
                "\n" +
                "        protected void addCriterion(String condition) {\n" +
                "            if (condition == null) {\n" +
                "                throw new RuntimeException(\"Value for condition cannot be null\");\n" +
                "            }\n" +
                "            criteria.add(new Criterion(condition));\n" +
                "        }\n" +
                "\n" +
                "        protected void addCriterion(String condition, Object value, String property) {\n" +
                "            if (value == null) {\n" +
                "                throw new RuntimeException(\"Value for \" + property + \" cannot be null\");\n" +
                "            }\n" +
                "            criteria.add(new Criterion(condition, value));\n" +
                "        }\n" +
                "\n" +
                "        protected void addCriterion(String condition, Object value1, Object value2, String property) {\n" +
                "            if (value1 == null || value2 == null) {\n" +
                "                throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");\n" +
                "            }\n" +
                "            criteria.add(new Criterion(condition, value1, value2));\n" +
                "        }\n\n");
        if(table.getHavDate()){
            buffer.append("        protected void addCriterionForJDBCDate(String condition, Date value, String property) {\n" +
                    "            if (value == null) {\n" +
                    "                throw new RuntimeException(\"Value for \" + property + \" cannot be null\");\n" +
                    "            }\n" +
                    "            addCriterion(condition, new java.sql.Date(value.getTime()), property);\n" +
                    "        }\n" +
                    "\n" +
                    "        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {\n" +
                    "            if (values == null || values.size() == 0) {\n" +
                    "                throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");\n" +
                    "            }\n" +
                    "            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();\n" +
                    "            Iterator<Date> iter = values.iterator();\n" +
                    "            while (iter.hasNext()) {\n" +
                    "                dateList.add(new java.sql.Date(iter.next().getTime()));\n" +
                    "            }\n" +
                    "            addCriterion(condition, dateList, property);\n" +
                    "        }\n" +
                    "\n" +
                    "        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {\n" +
                    "            if (value1 == null || value2 == null) {\n" +
                    "                throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");\n" +
                    "            }\n" +
                    "            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);\n" +
                    "        }\n" +
                    "\n");
        }
        for(Columns columns : table.getColumns()){
            if(columns.getJdbcType().equals("DATE")){
                String cloumnMethod = writeCloumnDMethod(columns);
                buffer.append(cloumnMethod);
            }else{
                String cloumnMethod = writeCloumnMethod(columns);
                buffer.append(cloumnMethod);
            }
        }
        buffer.append("    }\n");
        buffer.append("    public static class Criteria extends GeneratedCriteria {\n" +
                "\n" +
                "        protected Criteria() {\n" +
                "            super();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static class Criterion {\n" +
                "        private String condition;\n" +
                "\n" +
                "        private Object value;\n" +
                "\n" +
                "        private Object secondValue;\n" +
                "\n" +
                "        private boolean noValue;\n" +
                "\n" +
                "        private boolean singleValue;\n" +
                "\n" +
                "        private boolean betweenValue;\n" +
                "\n" +
                "        private boolean listValue;\n" +
                "\n" +
                "        private String typeHandler;\n" +
                "\n" +
                "        public String getCondition() {\n" +
                "            return condition;\n" +
                "        }\n" +
                "\n" +
                "        public Object getValue() {\n" +
                "            return value;\n" +
                "        }\n" +
                "\n" +
                "        public Object getSecondValue() {\n" +
                "            return secondValue;\n" +
                "        }\n" +
                "\n" +
                "        public boolean isNoValue() {\n" +
                "            return noValue;\n" +
                "        }\n" +
                "\n" +
                "        public boolean isSingleValue() {\n" +
                "            return singleValue;\n" +
                "        }\n" +
                "\n" +
                "        public boolean isBetweenValue() {\n" +
                "            return betweenValue;\n" +
                "        }\n" +
                "\n" +
                "        public boolean isListValue() {\n" +
                "            return listValue;\n" +
                "        }\n" +
                "\n" +
                "        public String getTypeHandler() {\n" +
                "            return typeHandler;\n" +
                "        }\n" +
                "\n" +
                "        protected Criterion(String condition) {\n" +
                "            super();\n" +
                "            this.condition = condition;\n" +
                "            this.typeHandler = null;\n" +
                "            this.noValue = true;\n" +
                "        }\n" +
                "\n" +
                "        protected Criterion(String condition, Object value, String typeHandler) {\n" +
                "            super();\n" +
                "            this.condition = condition;\n" +
                "            this.value = value;\n" +
                "            this.typeHandler = typeHandler;\n" +
                "            if (value instanceof List<?>) {\n" +
                "                this.listValue = true;\n" +
                "            } else {\n" +
                "                this.singleValue = true;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        protected Criterion(String condition, Object value) {\n" +
                "            this(condition, value, null);\n" +
                "        }\n" +
                "\n" +
                "        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {\n" +
                "            super();\n" +
                "            this.condition = condition;\n" +
                "            this.value = value;\n" +
                "            this.secondValue = secondValue;\n" +
                "            this.typeHandler = typeHandler;\n" +
                "            this.betweenValue = true;\n" +
                "        }\n" +
                "\n" +
                "        protected Criterion(String condition, Object value, Object secondValue) {\n" +
                "            this(condition, value, secondValue, null);\n" +
                "        }\n" +
                "    }\n\n");
        buffer.append("}\n");
        buffer.append("\n");
        return buffer.toString();
    }

    private String writeCloumnDMethod(Columns columns) {
        StringBuffer buffer = new StringBuffer();
        String colName = TableUtil.upperCase(columns.getFieldName());
        buffer.append("        public Criteria and"+colName+"IsNull() {\n" +
                "            addCriterion(\""+columns.getFieldName()+" is null\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"IsNotNull() {\n" +
                "            addCriterion(\""+columns.getFieldName()+" is not null\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"EqualTo("+columns.getFieldType()+" value) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" =\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"NotEqualTo("+columns.getFieldType()+" value) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" <>\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"GreaterThan("+columns.getFieldType()+" value) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" >\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"GreaterThanOrEqualTo("+columns.getFieldType()+" value) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" >=\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"LessThan("+columns.getFieldType()+" value) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" <\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"LessThanOrEqualTo("+columns.getFieldType()+" value) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" <=\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"In(List<"+columns.getFieldType()+"> values) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" in\", values, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"NotIn(List<"+columns.getFieldType()+"> values) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" not in\", values, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"Between("+columns.getFieldType()+" value1, "+columns.getFieldType()+" value2) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" between\", value1, value2, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"NotBetween("+columns.getFieldType()+" value1, "+columns.getFieldType()+" value2) {\n" +
                "            addCriterionForJDBCDate(\""+columns.getFieldName()+" not between\", value1, value2, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        return buffer.toString();
    }

    private String writeCloumnMethod(Columns columns) {
        StringBuffer buffer = new StringBuffer();
        String colName = TableUtil.upperCase(columns.getFieldName());
        buffer.append("        public Criteria and"+colName+"IsNull() {\n" +
                "            addCriterion(\""+columns.getFieldName()+" is null\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"IsNotNull() {\n" +
                "            addCriterion(\""+columns.getFieldName()+" is not null\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"EqualTo("+columns.getFieldType()+" value) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" =\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"NotEqualTo("+columns.getFieldType()+" value) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" <>\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"GreaterThan("+columns.getFieldType()+" value) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" >\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"GreaterThanOrEqualTo("+columns.getFieldType()+" value) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" >=\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"LessThan("+columns.getFieldType()+" value) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" <\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"LessThanOrEqualTo("+columns.getFieldType()+" value) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" <=\", value, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        if(columns.getFieldType().equals("String")){
            buffer.append("        public Criteria and"+colName+"Like(String value) {\n" +
                    "            addCriterion(\""+columns.getFieldName()+" like\", value, \""+columns.getFieldName()+"\");\n" +
                    "            return (Criteria) this;\n" +
                    "        }\n\n");
            buffer.append("        public Criteria and"+colName+"NotLike(String value) {\n" +
                    "            addCriterion(\""+columns.getFieldName()+" not like\", value, \""+columns.getFieldName()+"\");\n" +
                    "            return (Criteria) this;\n" +
                    "        }\n\n");
        }
        buffer.append("        public Criteria and"+colName+"In(List<"+columns.getFieldType()+"> values) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" in\", values, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"NotIn(List<"+columns.getFieldType()+"> values) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" not in\", values, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"Between("+columns.getFieldType()+" value1, "+columns.getFieldType()+" value2) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" between\", value1, value2, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        buffer.append("        public Criteria and"+colName+"NotBetween("+columns.getFieldType()+" value1, "+columns.getFieldType()+" value2) {\n" +
                "            addCriterion(\""+columns.getFieldName()+" not between\", value1, value2, \""+columns.getFieldName()+"\");\n" +
                "            return (Criteria) this;\n" +
                "        }\n\n");
        return buffer.toString();
    }

    private String writeMapper(Table table) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("package " + table.getPackageName() + ".mapper;\n\n");
        buffer.append("import "+table.getPackageName()+".pojo."+table.getClassName()+";\n");
        buffer.append("import "+table.getPackageName()+".pojo."+table.getClassName()+"Example;\n");
        buffer.append("import java.util.List;\n");
        buffer.append("import org.apache.ibatis.annotations.Param;\n");
        String idParam = "";
        for(Columns pri : table.getPrilist()){
            idParam += "@Param(\""+pri.getFieldName()+"\")"+pri.getFieldType()+" "+pri.getFieldName()+",";
        }
        idParam = idParam.substring(0,idParam.length()-1);
        buffer.append("public interface "+table.getClassName()+"Mapper {\n");
        buffer.append("    int countByExample("+table.getClassName()+"Example example);\n" +
                "\n" +
                "    int deleteByExample("+table.getClassName()+"Example example);\n" +
                "\n" +
                "    int deleteByPrimaryKey("+idParam+");\n" +
                "\n" +
                "    int insert("+table.getClassName()+" record);\n" +
                "\n" +
                "    int insertSelective("+table.getClassName()+" record);\n" +
                "\n" +
                "    List<"+table.getClassName()+"> selectByExample("+table.getClassName()+"Example example);\n" +
                "\n" +
                "    "+table.getClassName()+" selectByPrimaryKey("+idParam+");\n" +
                "\n" +
                "    int updateByExampleSelective(@Param(\"record\") "+table.getClassName()+" record, @Param(\"example\") "+table.getClassName()+"Example example);\n" +
                "\n" +
                "    int updateByExample(@Param(\"record\") "+table.getClassName()+" record, @Param(\"example\") "+table.getClassName()+"Example example);\n" +
                "\n" +
                "    int updateByPrimaryKeySelective("+table.getClassName()+" record);\n" +
                "\n" +
                "    int updateByPrimaryKey("+table.getClassName()+" record);\n" +
                "\n");

        if(table.getHavText()){
            buffer.append("    List<"+table.getClassName()+"> selectByExampleWithBLOBs("+table.getClassName()+"Example example);\n\n");
            buffer.append("    int updateByExampleWithBLOBs(@Param(\"record\") "+table.getClassName()+" record, @Param(\"example\") "+table.getClassName()+"Example example);\n\n");
            buffer.append("    int updateByPrimaryKeyWithBLOBs("+table.getClassName()+" record);\n");
        }
        buffer.append("}");
        return buffer.toString();
    }

    private String writeMapperXML(Table table) {
        StringBuffer buffer = new StringBuffer();
        //配置需要的信息
        String exampleClass = table.getPackageName() + ".pojo." + table.getClassName() + "Example" ;
        String pojoClass = table.getPackageName() + ".pojo." + table.getClassName() ;
        String BaseResultMap = "";
        String ResultMapWithBLOBs = "";
        String Base_Column_List = "";
        String Blob_Column_List = "";
        String Insert_Column = "";
        String Insert_Column_Value = "";
        String Insert_S_Column = "";
        String Insert_S_Column_Value = "";
        String Update_Column = "";
        String Update_S_Column = "";
        String Update_BLOB_Column = "";
        String ID_Where = "";
        Map<String,String> columnMap = new HashMap<>();
        for(Columns columns : table.getColumns()){
            if(columns.getJdbcType().equals("LONGVARCHAR")){
                Blob_Column_List += columns.getColumnName() + ", ";
                Update_BLOB_Column  += "      " + columns.getColumnName() + " = #{"+columns.getFieldName()+"},\n";
                ResultMapWithBLOBs += "    <result column=\""+columns.getColumnName()+"\" property=\""+columns.getFieldName()+"\" jdbcType=\""+columns.getJdbcType()+"\" />\n";
            }else{
                Base_Column_List += columns.getColumnName() + ", ";
                if(columns.getCkey().equals("PRI")){
                    ID_Where += " "+columns.getColumnName()+" = #{"+columns.getFieldName()+"} and";
                    BaseResultMap += "    <id column=\""+columns.getColumnName()+"\" property=\""+columns.getFieldName()+"\" jdbcType=\""+columns.getJdbcType()+"\" />\n";
                }else{
                    Update_Column += "      "+columns.getColumnName() + " = #{"+columns.getFieldName()+"},\n";
                    Update_S_Column +="      <if test=\""+columns.getFieldName()+" != null\" >\n" +
                            "        "+columns.getColumnName()+" = #{"+columns.getFieldName()+",jdbcType="+columns.getJdbcType()+"},\n" +
                            "      </if>\n";
                    BaseResultMap  += "    <result column=\""+columns.getColumnName()+"\" property=\""+columns.getFieldName()+"\" jdbcType=\""+columns.getJdbcType()+"\" />\n";
                }
            }
            Insert_Column += columns.getColumnName() + ",";
            Insert_Column_Value += "#{"+columns.getFieldName() + "},";
            Insert_S_Column += "      <if test=\""+columns.getFieldName()+" != null\" >\n" +
                    "        "+columns.getColumnName()+",\n" +
                    "      </if>\n";
            Insert_S_Column_Value += "      <if test=\""+columns.getFieldName()+" != null\" >\n" +
                    "        #{"+columns.getFieldName()+",jdbcType="+columns.getJdbcType()+"},\n" +
                    "      </if>\n";

        }
        Base_Column_List = Base_Column_List.substring(0,Base_Column_List.length()-2);
        if(ObjectHelper.isNotEmpty(Blob_Column_List)) Blob_Column_List = Blob_Column_List.substring(0,Blob_Column_List.length()-2);
        ID_Where = ID_Where.substring(0,ID_Where.length()-3);
        Insert_Column = Insert_Column.substring(0,Insert_Column.length()-1);
        Insert_Column_Value = Insert_Column_Value.substring(0,Insert_Column_Value.length()-1);
        if(ObjectHelper.isNotEmpty(Update_BLOB_Column))Update_BLOB_Column = Update_BLOB_Column.substring(0,Update_BLOB_Column.length()-2);
        Update_Column = Update_Column.substring(6,Update_Column.length()-2);
        //构建页面
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        buffer.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n");
        buffer.append("<mapper namespace=\""+ table.getPackageName() + ".mapper."+table.getClassName()+"Mapper\" >\n");
        buffer.append("  <resultMap id=\"BaseResultMap\" type=\""+pojoClass+"\" >\n" + BaseResultMap + "  </resultMap>\n");
        if(table.getHavText()) {
            buffer.append("  <resultMap id=\"ResultMapWithBLOBs\" type=\"" + pojoClass + "\" extends=\"BaseResultMap\" >\n" + ResultMapWithBLOBs + "  </resultMap>\n");
        }
        buffer.append("  <sql id=\"Example_Where_Clause\" >\n" +
                "    <where >\n" +
                "      <foreach collection=\"oredCriteria\" item=\"criteria\" separator=\"or\" >\n" +
                "        <if test=\"criteria.valid\" >\n" +
                "          <trim prefix=\"(\" suffix=\")\" prefixOverrides=\"and\" >\n" +
                "            <foreach collection=\"criteria.criteria\" item=\"criterion\" >\n" +
                "              <choose >\n" +
                "                <when test=\"criterion.noValue\" >\n" +
                "                  and ${criterion.condition}\n" +
                "                </when>\n" +
                "                <when test=\"criterion.singleValue\" >\n" +
                "                  and ${criterion.condition} #{criterion.value}\n" +
                "                </when>\n" +
                "                <when test=\"criterion.betweenValue\" >\n" +
                "                  and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "                </when>\n" +
                "                <when test=\"criterion.listValue\" >\n" +
                "                  and ${criterion.condition}\n" +
                "                  <foreach collection=\"criterion.value\" item=\"listItem\" open=\"(\" close=\")\" separator=\",\" >\n" +
                "                    #{listItem}\n" +
                "                  </foreach>\n" +
                "                </when>\n" +
                "              </choose>\n" +
                "            </foreach>\n" +
                "          </trim>\n" +
                "        </if>\n" +
                "      </foreach>\n" +
                "    </where>\n" +
                "  </sql>\n" +
                "  <sql id=\"Update_By_Example_Where_Clause\" >\n" +
                "    <where >\n" +
                "      <foreach collection=\"example.oredCriteria\" item=\"criteria\" separator=\"or\" >\n" +
                "        <if test=\"criteria.valid\" >\n" +
                "          <trim prefix=\"(\" suffix=\")\" prefixOverrides=\"and\" >\n" +
                "            <foreach collection=\"criteria.criteria\" item=\"criterion\" >\n" +
                "              <choose >\n" +
                "                <when test=\"criterion.noValue\" >\n" +
                "                  and ${criterion.condition}\n" +
                "                </when>\n" +
                "                <when test=\"criterion.singleValue\" >\n" +
                "                  and ${criterion.condition} #{criterion.value}\n" +
                "                </when>\n" +
                "                <when test=\"criterion.betweenValue\" >\n" +
                "                  and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "                </when>\n" +
                "                <when test=\"criterion.listValue\" >\n" +
                "                  and ${criterion.condition}\n" +
                "                  <foreach collection=\"criterion.value\" item=\"listItem\" open=\"(\" close=\")\" separator=\",\" >\n" +
                "                    #{listItem}\n" +
                "                  </foreach>\n" +
                "                </when>\n" +
                "              </choose>\n" +
                "            </foreach>\n" +
                "          </trim>\n" +
                "        </if>\n" +
                "      </foreach>\n" +
                "    </where>\n" +
                "  </sql>\n");
        buffer.append("  <sql id=\"Base_Column_List\" >\n" +
                "    "+Base_Column_List+"\n" +
                "  </sql>\n");
        if(table.getHavText()){
            buffer.append("  <sql id=\"Blob_Column_List\" >\n" +
                    "    "+Blob_Column_List+"\n" +
                    "  </sql>\n");
        }
        buffer.append("<select id=\"selectByExample\" resultMap=\"BaseResultMap\" parameterType=\""+exampleClass+"\" >\n" +
                "    select\n" +
                "    <if test=\"distinct\" >\n" +
                "      distinct\n" +
                "    </if>\n" +
                "    <include refid=\"Base_Column_List\" />\n" +
                "    from "+table.getTableName()+"\n" +
                "    <if test=\"_parameter != null\" >\n" +
                "      <include refid=\"Example_Where_Clause\" />\n" +
                "    </if>\n" +
                "    <if test=\"orderByClause != null\" >\n" +
                "      order by ${orderByClause}\n" +
                "    </if>\n" +
                "  </select>\n");
        if(table.getHavText()){
            buffer.append("  <select id=\"selectByExampleWithBLOBs\" resultMap=\"ResultMapWithBLOBs\" parameterType=\""+exampleClass+"\" >\n" +
                    "    select\n" +
                    "    <if test=\"distinct\" >\n" +
                    "      distinct\n" +
                    "    </if>\n" +
                    "    <include refid=\"Base_Column_List\" />\n" +
                    "    ,\n" +
                    "    <include refid=\"Blob_Column_List\" />\n" +
                    "    from "+table.getTableName()+"\n" +
                    "    <if test=\"_parameter != null\" >\n" +
                    "      <include refid=\"Example_Where_Clause\" />\n" +
                    "    </if>\n" +
                    "    <if test=\"orderByClause != null\" >\n" +
                    "      order by ${orderByClause}\n" +
                    "    </if>\n" +
                    "  </select>\n");
        }
        if(table.getHavText()){
            buffer.append("  <select id=\"selectByPrimaryKey\" resultMap=\"ResultMapWithBLOBs\" >\n" +
                    "    select \n" +
                    "    <include refid=\"Base_Column_List\" />\n" +
                    "    ,\n" +
                    "    <include refid=\"Blob_Column_List\" />\n" +
                    "    from "+table.getTableName()+"\n" +
                    "    where"+ID_Where+"\n" +
                    "  </select>\n");
        }else{
            buffer.append("  <select id=\"selectByPrimaryKey\" resultMap=\"BaseResultMap\" >\n" +
                    "    select \n" +
                    "    <include refid=\"Base_Column_List\" />\n" +
                    "    from "+table.getTableName()+"\n" +
                    "    where "+ID_Where+"\n" +
                    "  </select>\n");
        }


        buffer.append("  <select id=\"countByExample\" parameterType=\""+exampleClass+"\" resultType=\"java.lang.Integer\" >\n" +
                "    select count(*) from "+table.getTableName()+"\n" +
                "    <if test=\"_parameter != null\" >\n" +
                "      <include refid=\"Example_Where_Clause\" />\n" +
                "    </if>\n" +
                "  </select>\n");

        buffer.append("  <delete id=\"deleteByPrimaryKey\" >\n" +
                "    delete from "+table.getTableName()+"\n" +
                "    where "+ID_Where+"\n" +
                "  </delete>\n");

        buffer.append("  <delete id=\"deleteByExample\" parameterType=\""+exampleClass+"\" >\n" +
                "    delete from "+table.getTableName()+"\n" +
                "    <if test=\"_parameter != null\" >\n" +
                "      <include refid=\"Example_Where_Clause\" />\n" +
                "    </if>\n" +
                "  </delete>\n");

        buffer.append("  <insert id=\"insert\" parameterType=\""+pojoClass+"\" >\n" +
                "    insert into "+table.getTableName()+" ("+Insert_Column+")\n" +
                "    values ("+Insert_Column_Value+")\n" +
                "  </insert>\n");

        buffer.append("<insert id=\"insertSelective\" parameterType=\""+pojoClass+"\" >\n" +
                "    insert into "+table.getTableName()+"\n" +
                "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >\n" + Insert_S_Column + "    </trim>\n" +
                "    <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\" >\n" + Insert_S_Column_Value +"    </trim>\n" +
                "  </insert>\n");

        buffer.append("<update id=\"updateByExampleSelective\" parameterType=\"map\" >\n" +
                "    update "+table.getTableName()+"\n" +
                "    <set >\n" + Update_S_Column +"    </set>\n" +
                "    <if test=\"_parameter != null\" >\n" +
                "      <include refid=\"Update_By_Example_Where_Clause\" />\n" +
                "    </if>\n" +
                "  </update>\n");

        if(table.getHavText()) {
            buffer.append("  <update id=\"updateByExampleWithBLOBs\" parameterType=\"map\" >\n" +
                    "    update " + table.getTableName() + "\n" +
                    "    set " + Update_Column + Update_BLOB_Column + "\n" +
                    "    <if test=\"_parameter != null\" >\n" +
                    "      <include refid=\"Update_By_Example_Where_Clause\" />\n" +
                    "    </if>\n" +
                    "  </update>\n");
        }

        buffer.append("  <update id=\"updateByExample\" parameterType=\"map\" >\n" +
                "    update "+table.getTableName()+"\n" +
                "    set " + Update_Column + "\n" +
                "    <if test=\"_parameter != null\" >\n" +
                "      <include refid=\"Update_By_Example_Where_Clause\" />\n" +
                "    </if>\n" +
                "  </update>\n");

        buffer.append("  <update id=\"updateByPrimaryKeySelective\" parameterType=\""+pojoClass+"\" >\n" +
                "    update "+table.getTableName()+"\n" +
                "    <set >\n" + Update_S_Column +"    </set>\n" +
                "    where "+ ID_Where +"\n" +
                "  </update>\n");

        if(table.getHavText()) {
            buffer.append("  <update id=\"updateByPrimaryKeyWithBLOBs\" parameterType=\"" + pojoClass + "\" >\n" +
                    "    update " + table.getTableName() + "\n" +
                    "    set " + Update_Column + Update_BLOB_Column + "\n" +
                    "    where " + ID_Where + "\n" +
                    "  </update>\n");
        }

        buffer.append("  <update id=\"updateByPrimaryKey\" parameterType=\""+pojoClass+"\" >\n" +
                "    update ich_text_copy\n" +
                "    set " + Update_Column + "\n" +
                "    where "+ ID_Where +"\n" +
                "  </update>\n");

        buffer.append("</mapper>");
        return buffer.toString();
    }


}
