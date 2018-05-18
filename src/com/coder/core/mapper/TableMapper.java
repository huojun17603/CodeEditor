package com.coder.core.mapper;

import com.coder.core.entity.Columns;
import com.coder.core.entity.Schema;
import com.coder.core.entity.Table;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableMapper {

    public List<Schema> selectSchemas();

    public List<Table> selectTables(String schema);

    public List<Columns> selectColumns(@Param("schema") String schema, @Param("table") String table);

    Table selectTable(@Param("schema") String schema, @Param("table") String table);

    List<Columns> selectPriColumns(@Param("schema") String schema, @Param("table") String table);
}
