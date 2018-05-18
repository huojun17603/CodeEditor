package com.coder.core.service;

import com.coder.core.entity.Columns;
import com.coder.core.entity.Table;

import java.util.List;

public interface CoreService {

    Table findTable(String schema, String tableName);

    List<Columns> findColumns(String schema, String tableName);

    List<Columns> findPriColumns(String schema, String tableName);
}
