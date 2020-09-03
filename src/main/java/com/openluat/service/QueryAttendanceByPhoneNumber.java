package com.openluat.service;

import com.openluat.pojo.User;
import com.taobao.api.ApiException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class QueryAttendanceByPhoneNumber {
    public static String query(String phoneNumber) throws ApiException {

        int queryMonth = 9;

        StringBuilder returnInfo = new StringBuilder();
        returnInfo.append("<html lang=\"en\">");
        returnInfo.append("<h2>正在查询的号码为：").append(phoneNumber).append("</h2>");

        AttendanceQueryUtils queryUtils = new AttendanceQueryUtils();

        String token = queryUtils.queryToken("dingmwwphiz0vzg6dmp6", "Tp6MfxCeIKHhJC7bQaN0sOPO26lqbXVZAkl6ZiuM6AMEG7rro4Azd1kfus309WEx");

        String id = queryUtils.queryUserIdByPhoneNumber(phoneNumber, token);

        HashMap<String, Float> scoreAimMap = queryUtils.queryScoreAim(queryMonth);
        Float jigeScoreMinutes = scoreAimMap.get("jigeScoreMinutes");
        Float fullScoreMinutes = scoreAimMap.get("fullScoreMinutes");
        Float fullScoreHours = scoreAimMap.get("fullScoreHours");
        Float jigeScoreHours = scoreAimMap.get("jigeScoreHours");
        returnInfo.append(queryMonth).append("月及格分钟数 = ").append(jigeScoreMinutes.intValue()).append("<br>");
        returnInfo.append(queryMonth).append("月满分分钟数 = ").append(fullScoreMinutes.intValue()).append("<br>");
        returnInfo.append(queryMonth).append("月及格小时数 = ").append(jigeScoreHours.intValue()).append("<br>");
        returnInfo.append(queryMonth).append("月满分小时数 = ").append(fullScoreHours.intValue()).append("<br><br>");

        User yanjunjie = new User("testman", id);
        HashMap<Integer, HashMap<String, Date>> attendanceDetail = queryUtils.queryAttendanceDetail(yanjunjie, queryMonth, token);
        returnInfo.append(queryMonth).append("月打卡情况：<br>");
        for (Map.Entry<Integer, HashMap<String, Date>> entry : attendanceDetail.entrySet()) {
            returnInfo.append(entry.getKey()).append(entry.getValue()).append("<br>");
        }
        HashMap<String, Float> timeCountMap = queryUtils.queryAttendInfo(attendanceDetail);
        Float minutesCount = timeCountMap.get("minutesCount");
        returnInfo.append("<br>").append(queryMonth).append("月工作总分钟数 = ").append(minutesCount.intValue()).append("<br>");
        Float hoursCount = timeCountMap.get("hoursCount");
        returnInfo.append(queryMonth).append("月工作总小时数 = ").append(hoursCount).append("<br>");
        Float jigepercent = queryUtils.dived(minutesCount, jigeScoreMinutes);
        Float perfectpercent = queryUtils.dived(minutesCount, fullScoreMinutes);
        returnInfo.append(queryMonth).append("月及格时间完成百分比 = ").append(jigepercent * 100).append("%").append("<br>");
        returnInfo.append(queryMonth).append("月满分时间完成百分比 = ").append(perfectpercent * 100).append("%").append("<br>");
        if (minutesCount > jigeScoreMinutes) {
            returnInfo.append(queryMonth).append("月及格了").append("<br>");
        } else {
            returnInfo.append(queryMonth).append("月距离及格还差").append(jigeScoreMinutes.intValue() - minutesCount.intValue()).append("分钟,或").append(jigeScoreHours - hoursCount).append("小时").append("<br>");
        }
        if (minutesCount > fullScoreMinutes) {
            returnInfo.append(queryMonth).append("月满分了").append("<br>");
        } else {
            returnInfo.append(queryMonth).append("月距离满分还差").append(fullScoreMinutes.intValue() - minutesCount.intValue()).append("分钟,或").append(fullScoreHours - hoursCount).append("小时").append("<br>");
        }

        returnInfo.append("</html>");

        return returnInfo.toString();
    }
}
