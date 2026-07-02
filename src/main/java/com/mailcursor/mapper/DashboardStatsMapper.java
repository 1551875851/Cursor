package com.mailcursor.mapper;

import com.mailcursor.mapper.dto.DayCount;
import com.mailcursor.mapper.dto.NameCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DashboardStatsMapper {

    Integer countTodayAccess(@Param("todayPrefix") String todayPrefix);

    Integer countTodayActiveUsers(@Param("todayPrefix") String todayPrefix);

    Integer countActiveUsers();

    Integer countTodayApiSuccess(@Param("todayPrefix") String todayPrefix);

    Integer countTodayApiTotal(@Param("todayPrefix") String todayPrefix);

    List<DayCount> countAccessByDay(@Param("startDay") String startDay);

    List<DayCount> countActiveUsersByDay(@Param("startDay") String startDay);

    List<NameCount> countFeatureByUri();

    Integer countMailSendLog();

    Integer countRuankaoScanLog();

    List<NameCount> countApiRanking(@Param("limit") int limit);

    Integer countApiSuccess();

    Integer countApiClientError();

    Integer countApiServerError();

    Integer countApiUnknown();
}
