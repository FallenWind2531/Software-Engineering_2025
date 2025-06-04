package com.Main.util.course_selection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间工具类
 */
public class TimeUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeUtils.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 检查当前时间是否在指定的时间段内
     * 
     * @param timeList 时间段列表，应包含起止时间点，如 ["2023-01-01 00:00:00", "2023-01-10 00:00:00"]
     * @return 如果当前时间在任意一个时间段内则返回true，否则返回false
     */
    public static boolean isTimeInRanges(List<String> timeList) {
        if (timeList == null || timeList.size() < 2 || timeList.size() % 2 != 0) {
            logger.warn("Invalid time list format: {}", timeList);
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        logger.info("Current time: {}", now.format(FORMATTER));
        
        for (int i = 0; i < timeList.size(); i += 2) {
            try {
                String startTimeStr = timeList.get(i);
                String endTimeStr = timeList.get(i + 1);
                
                LocalDateTime startTime = LocalDateTime.parse(startTimeStr, FORMATTER);
                LocalDateTime endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
                
                logger.info("Checking time range: {} to {}", startTimeStr, endTimeStr);
                
                if (now.isAfter(startTime) && now.isBefore(endTime)) {
                    logger.info("Current time is within range");
                    return true;
                }
            } catch (DateTimeParseException e) {
                logger.error("Error parsing time: {}", e.getMessage());
            }
        }
        
        logger.info("Current time is not within any range");
        return false;
    }
} 