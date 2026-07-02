package com.mailcursor.mapper;

import com.mailcursor.system.model.SysOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysOrgMapper {

    List<SysOrg> selectAll();

    SysOrg selectById(@Param("id") Long id);

    int insert(SysOrg org);

    int update(SysOrg org);

    int countChildren(@Param("id") Long id);

    int countUsersByOrgId(@Param("orgId") Long orgId);

    int deleteById(@Param("id") Long id);
}
