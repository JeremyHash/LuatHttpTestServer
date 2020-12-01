package com.openluat.service;

import com.openluat.pojo.ScoreAim;
import com.taobao.api.ApiException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class QABPService {

    private final ScoreAim scoreAim;

    private final AttendanceQueryUtils queryUtils;

    public QABPService(ScoreAim scoreAim, AttendanceQueryUtils queryUtils) {
        this.scoreAim = scoreAim;
        this.queryUtils = queryUtils;
    }

    public String query(String phoneNumber) throws ApiException {

        long startTime = System.currentTimeMillis();

        int queryMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        System.out.println("queryMonth = " + queryMonth);

        StringBuilder returnInfo = new StringBuilder();
        returnInfo.append("<h2>正在查询的号码为：").append(phoneNumber).append("</h2>");

        String token = queryUtils.queryToken("dingmwwphiz0vzg6dmp6", "Tp6MfxCeIKHhJC7bQaN0sOPO26lqbXVZAkl6ZiuM6AMEG7rro4Azd1kfus309WEx");

        String id = queryUtils.queryUserIdByPhoneNumber(phoneNumber, token);

        int jigeScoreHours = scoreAim.getJigeScoreHours();
        int fullScoreHours = scoreAim.getFullScoreHours();
        int jigeScoreMinutes = scoreAim.getJigeScoreMinutes();
        int fullScoreMinutes = scoreAim.getFullScoreMinutes();

        returnInfo.append(queryMonth).append("月及格分钟数 = ").append(jigeScoreMinutes).append("<br>");
        returnInfo.append(queryMonth).append("月满分分钟数 = ").append(fullScoreMinutes).append("<br>");
        returnInfo.append(queryMonth).append("月及格小时数 = ").append(jigeScoreHours).append("<br>");
        returnInfo.append(queryMonth).append("月满分小时数 = ").append(fullScoreHours).append("<br><br>");

        HashMap<Integer, HashMap<String, Date>> attendanceDetail = queryUtils.queryAttendanceDetail(id, queryMonth, token);
        returnInfo.append(queryMonth).append("月打卡情况：<br>");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map.Entry<Integer, HashMap<String, Date>> entry : attendanceDetail.entrySet()) {
            Integer day = entry.getKey();
            returnInfo.append(day).append("号：");
            HashMap<String, Date> dutyMap = entry.getValue();
            long OnDutyTime = 0;
            long OffDutyTime;
            for (Map.Entry<String, Date> stringDateEntry : dutyMap.entrySet()) {
                String dutyType = stringDateEntry.getKey();
                Date dutyTime = stringDateEntry.getValue();
                String formatDutyTime = simpleDateFormat.format(dutyTime);
                if (dutyType.equals("OnDuty")) {
                    OnDutyTime = dutyTime.getTime();
                    returnInfo.append("上班时间：").append(formatDutyTime).append("---");
                } else {
                    OffDutyTime = dutyTime.getTime();
                    returnInfo.append("下班时间：").append(formatDutyTime);
                    if (day == 1 || day == 7 || day == 8 || day == 14 || day == 15 || day == 21 || day == 22 || day == 28 || day == 29) {
                        returnInfo.append("（休息日工作）<br>");
                    } else {
                        if ((OffDutyTime - OnDutyTime) / 1000 <= 32400) {
                            returnInfo.append("（当天可能存在早退或请假或出差情况,按照9小时工时计算）<br>");
                        } else {
                            returnInfo.append("<br>");
                        }
                    }
                }
            }
        }
        int minutesCount = queryUtils.queryAttendMinutes(attendanceDetail);
        returnInfo.append("<br><br>").append(queryMonth).append("月工作总分钟数 = ").append(minutesCount).append("<br>");
        float hoursCount = queryUtils.dived(minutesCount, 60);
        returnInfo.append(queryMonth).append("月工作总小时数 = ").append(hoursCount).append("<br>");

        if (minutesCount > jigeScoreMinutes) {
            returnInfo.append(queryMonth).append("月及格了").append("<br>");
        } else {
            returnInfo.append(queryMonth).append("月距离及格还差").append(jigeScoreMinutes - minutesCount).append("分钟，或").append(jigeScoreHours - hoursCount).append("小时").append("<br>");
        }
        if (minutesCount > fullScoreMinutes) {
            returnInfo.append(queryMonth).append("月满分了").append("<br>");
        } else {
            returnInfo.append(queryMonth).append("月距离满分还差").append(fullScoreMinutes - minutesCount).append("分钟，或").append(fullScoreHours - hoursCount).append("小时").append("<br>");
        }

        long endTime = System.currentTimeMillis();

        long time = endTime - startTime;
        returnInfo.append("<br>查询用时：").append(time).append("ms<br>");
        return returnInfo.toString();
    }
}
