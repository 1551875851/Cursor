package com.mailcursor.mapper;

import com.mailcursor.mapper.dto.AccessLogRecord;
import com.mailcursor.mapper.dto.MailLogRecord;
import com.mailcursor.mapper.dto.RuankaoScanLogRecord;
import com.mailcursor.mapper.dto.SystemEventLogRecord;

public interface AppLogMapper {

    int insertAccessLog(AccessLogRecord record);

    int insertMailLog(MailLogRecord record);

    int insertRuankaoScanLog(RuankaoScanLogRecord record);

    int insertSystemEventLog(SystemEventLogRecord record);
}
