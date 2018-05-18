package com.coder.core.service.impl;

import com.coder.core.entity.Columns;
import com.coder.core.entity.Schema;
import com.coder.core.entity.Table;
import com.coder.core.mapper.TableMapper;
import com.coder.core.service.CoreService;
import com.coder.core.util.ObjectHelper;
import com.coder.core.util.TableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreServiceImpl implements CoreService {
    @Autowired
    private TableMapper tableMapper;


    @Override
    public Table findTable(String schema, String tableName) {
        Table table = tableMapper.selectTable(schema,tableName);
        String ClassName = TableUtil.getClassName(tableName);
        table.setClassName(ClassName);
        table.setCnName(table.getComment().split("\\|")[0]);
        table.setColumns(this.findColumns(schema,tableName));
        table.setPrilist(this.findPriColumns(schema,tableName));
        table.setHavText(false);
        table.setHavDate(false);
        for(Columns columns : table.getColumns()){
            if(columns.getJdbcType().equals("DATE")){
                table.setHavDate(true);
            }
            if(ObjectHelper.isNotEmpty(columns.getLength())&&Long.valueOf(columns.getLength())>=65535l){
                table.setHavText(true);
            }
        }
        return table;
    }


    @Override
    public List<Columns> findPriColumns(String schema, String tableName) {
        List<Columns> list = tableMapper.selectPriColumns(schema,tableName);
        for (Columns columns : list){
            columns.setFieldName(Mysql2JavaName(columns.getColumnName()));
            columns.setFieldType(Mysql2JavaType(columns.getColumnType()));
            columns.setJdbcType(Mysql2JdbcType(columns.getColumnType()));
        }
        return list;
    }

    @Override
    public List<Columns> findColumns(String schema, String tableName) {
        List<Columns> list = tableMapper.selectColumns(schema,tableName);
        for (Columns columns : list){
            columns.setFieldName(Mysql2JavaName(columns.getColumnName()));
            columns.setFieldType(Mysql2JavaType(columns.getColumnType()));
            columns.setJdbcType(Mysql2JdbcType(columns.getColumnType()));
        }
        return list;
    }



    //全小写
    private String  Mysql2JavaName(String columnName){
        return columnName.replace("_","").toLowerCase();
    }


    private String Mysql2JavaType(String columnType){
        if(columnType.equals("char"))return "String";
        if(columnType.equals("varchar"))return "String";
        if(columnType.equals("varbinary"))return "String";

        if(columnType.equals("blob"))return "String";
        if(columnType.equals("longblob"))return "String";

        if(columnType.equals("longtext"))return "String";
        if(columnType.equals("mediumtext"))return "String";
        if(columnType.equals("text"))return "String";

        if(columnType.equals("enum"))return "String";
        if(columnType.equals("set"))return "String";

        if(columnType.equals("tinyint"))return "Byte";
        if(columnType.equals("bit"))return "Boolean";
        if(columnType.equals("smallint"))return "Integer";
        if(columnType.equals("int"))return "Integer";

        if(columnType.equals("bigint"))return "Long";

        if(columnType.equals("double"))return "BigDecimal";
        if(columnType.equals("decimal"))return "BigDecimal";

        if(columnType.equals("date"))return "Date";
        if(columnType.equals("time"))return "Date";
        if(columnType.equals("year"))return "Date";
        if(columnType.equals("datetime"))return "Date";
        if(columnType.equals("timestamp"))return "Date";

        return "";
    }

    private String Mysql2JdbcType(String columnType) {

        if(columnType.equals("bigint"))return "BIGINT";
        if(columnType.equals("tinyint"))return "TINYINT";
        if(columnType.equals("smallint"))return "SMALLINT";
        if(columnType.equals("mediumint"))return "INTEGER";
        if(columnType.equals("integer"))return "INTEGER";
        if(columnType.equals("int"))return "INTEGER";

        if(columnType.equals("bit"))return "BIT";

        if(columnType.equals("float"))return "REAL";
        if(columnType.equals("double"))return "DOUBLE";
        if(columnType.equals("decimal"))return "DECIMAL";
        if(columnType.equals("numeric"))return "DECIMAL";
        if(columnType.equals("char"))return "CHAR";
        if(columnType.equals("varchar"))return "VARCHAR";

        if(columnType.equals("tinyblob"))return "BINARY";
        if(columnType.equals("tinytext"))return "VARCHAR";
        if(columnType.equals("blob"))return "BINARY";
        if(columnType.equals("text"))return "LONGVARCHAR";
        if(columnType.equals("mediumblob"))return "LONGVARBINARY";
        if(columnType.equals("mediumtext"))return "LONGVARCHAR";
        if(columnType.equals("longblob"))return "LONGVARBINARY";
        if(columnType.equals("longtext"))return "LONGVARCHAR";

        if(columnType.equals("date"))return "DATE";
        if(columnType.equals("time"))return "TIME";
        if(columnType.equals("year"))return "DATE";
        if(columnType.equals("datetime"))return "TIMESTAMP";
        if(columnType.equals("timestamp"))return "TIMESTAMP";

        return "";
    }


//    MySQL数据类型	JAVA数据类型	JDBC TYPE	普通变量类型	主键类型
//    BIGINT	Long	BIGINT	支持	支持
//    TINYINT	Byte	TINYINT	支持	不支持
//    SMALLINT	Short	SMALLINT	支持	不支持
//    MEDIUMINT	Integer	INTEGER	支持	支持
//    INTEGER	Integer	INTEGER	支持	支持
//    INT	Integer	INTEGER	支持	支持
//    FLOAT	Float	REAL	支持	不支持
//    DOUBLE	Double	DOUBLE	支持	不支持
//    DECIMAL	BigDecimal	DECIMAL	支持	不支持
//    NUMERIC	BigDecimal	DECIMAL	支持	不支持
//    CHAR	String	CHAR	支持	不支持
//    VARCHAR	String	VARCHAR	支持	不支持
//    TINYBLOB	DataTypeWithBLOBs.byte[]	BINARY	不支持	不支持
//    TINYTEXT	String	VARCHAR	支持	不支持
//    BLOB	DataTypeWithBLOBs.byte[]	BINARY	不支持	不支持
//    TEXT	DataTypeWithBLOBs.String	LONGVARCHAR	不支持	不支持
//    MEDIUMBLOB	DataTypeWithBLOBs.byte[]	LONGVARBINARY	不支持	不支持
//    MEDIUMTEXT	DataTypeWithBLOBs.String	LONGVARCHAR	不支持	不支持
//    LONGBLOB	DataTypeWithBLOBs.byte[]	LONGVARBINARY	不支持	不支持
//    LONGTEXT	DataTypeWithBLOBs.String	LONGVARCHAR	不支持	不支持
//    DATE	Date	DATE	支持	不支持
//    TIME	Date	TIME	支持	不支持
//    YEAR	Date	DATE	不支持	不支持
//    DATETIME	Date	TIMESTAMP	支持	不支持
//    TIMESTAMP	Date	TIMESTAMP
}
