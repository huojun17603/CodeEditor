package com.uu.storeb.mapper;

import com.uu.storeb.DemoB;
import com.uu.storeb.DemoBExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
public interface DemoBMapper {
    int countByExample(DemoBExample example);

    int deleteByExample(DemoBExample example);

    int deleteByPrimaryKey(@Param("id")String id);

    int insert(DemoB record);

    int insertSelective(DemoB record);

    List<DemoB> selectByExample(DemoBExample example);

    DemoB selectByPrimaryKey(@Param("id")String id);

    int updateByExampleSelective(@Param("record") DemoB record, @Param("example") DemoBExample example);

    int updateByExample(@Param("record") DemoB record, @Param("example") DemoBExample example);

    int updateByPrimaryKeySelective(DemoB record);

    int updateByPrimaryKey(DemoB record);

}