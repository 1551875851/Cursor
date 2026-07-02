package com.mailcursor.mapper;

import com.mailcursor.mapper.dto.OperLogQuery;
import com.mailcursor.operlog.model.SysOperLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysOperLogMapper {

    int insert(SysOperLog log);

    int countByQuery(OperLogQuery query);

    List<SysOperLog> selectByQuery(OperLogQuery query);
}
