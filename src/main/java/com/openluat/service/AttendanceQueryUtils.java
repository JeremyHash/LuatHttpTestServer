package com.openluat.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.openluat.pojo.User;
import com.taobao.api.ApiException;

import java.text.DecimalFormat;
import java.util.*;

public class AttendanceQueryUtils {

    //除法
    public Float dived(float a, float b) {

        DecimalFormat df = new DecimalFormat("0.00");//设置保留位数

        return Float.valueOf(df.format(a / b));

    }

    //通过手机号查询用户ID
    public String queryUserIdByPhoneNumber(String phoneNumber, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile");
        OapiUserGetByMobileRequest request = new OapiUserGetByMobileRequest();
        request.setMobile(phoneNumber);

        OapiUserGetByMobileResponse res = client.execute(request, accessToken);
        return res.getUserid();
    }

    //查询Token
    public String queryToken(String appKey, String appSecret) {
        String accessToken = "";
        DefaultDingTalkClient getTokenClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest getTokenReq = new OapiGettokenRequest();
        getTokenReq.setAppkey(appKey);
        getTokenReq.setAppsecret(appSecret);
        getTokenReq.setHttpMethod("GET");
        OapiGettokenResponse getTokenRes = null;
        try {
            getTokenRes = getTokenClient.execute(getTokenReq);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (getTokenRes != null) {
            accessToken = getTokenRes.getAccessToken();
        }
        return accessToken;
    }

    //查询考勤分数线
    public HashMap<String, Float> queryScoreAim(int queryMonth) {
        int days;
        if (queryMonth == 2) {
            days = 29;
        } else if (queryMonth == 1 || queryMonth == 3 || queryMonth == 5 || queryMonth == 7 || queryMonth == 8 || queryMonth == 10 || queryMonth == 12) {
            days = 31;
        } else {
            days = 30;
        }

        float fullScoreMinutes = (days - 3) * 12 * 60;
        float jigeScoreMinutes = (days - 3) * 10 * 60;

        float fullScoreHours = dived(fullScoreMinutes, 60);
        float jigeScoreHours = dived(jigeScoreMinutes, 60);

        HashMap<String, Float> scoreMap = new HashMap<>();
        scoreMap.put("jigeScoreMinutes", jigeScoreMinutes);
        scoreMap.put("fullScoreMinutes", fullScoreMinutes);
        scoreMap.put("jigeScoreHours", jigeScoreHours);
        scoreMap.put("fullScoreHours", fullScoreHours);
        return scoreMap;
    }

    //        查询部门
    public void queryDepartment(String accessToken) throws ApiException {
        DingTalkClient getDepartmentClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
        OapiDepartmentListRequest getDepartmentReq = new OapiDepartmentListRequest();
        getDepartmentReq.setHttpMethod("GET");
        OapiDepartmentListResponse getDepartmentRes = getDepartmentClient.execute(getDepartmentReq, accessToken);
        List<OapiDepartmentListResponse.Department> departments = getDepartmentRes.getDepartment();
        for (OapiDepartmentListResponse.Department department : departments) {
            Long departmentId = department.getId();
            System.out.println(departmentId + department.getName());
        }
    }

    //查询部门成员
    private void queryDepartmentUser(long departmentId, String accessToken) throws ApiException {
        DingTalkClient getUserClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/simplelist");
        OapiUserSimplelistRequest getUserReq = new OapiUserSimplelistRequest();
        getUserReq.setDepartmentId(departmentId);
        getUserReq.setHttpMethod("GET");
        OapiUserSimplelistResponse getUserRes = getUserClient.execute(getUserReq, accessToken);
        for (OapiUserSimplelistResponse.Userlist userlist : getUserRes.getUserlist()) {
            System.out.println(userlist.getUserid() + userlist.getName());
        }
    }

    //查询考勤记录
    public HashMap<Integer, HashMap<String, Date>> queryAttendanceDetail(User user, int queryMonth, String accessToken) throws ApiException {
        DingTalkClient getAttendanceClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/attendance/list");
        OapiAttendanceListRequest getAttendanceReq = new OapiAttendanceListRequest();
        getAttendanceReq.setOffset(0L);
        getAttendanceReq.setLimit(50L);
        //设置用户ID
        getAttendanceReq.setUserIdList(Collections.singletonList(user.getId()));
        //查询每天的考勤情况
        HashMap<Integer, HashMap<String, Date>> AttendanceMap = new HashMap<Integer, HashMap<String, Date>>();

        for (int i = 1; i <= 31; i++) {
            getAttendanceReq.setWorkDateFrom("2020-" + queryMonth + "-" + i + " 00:00:00");
            getAttendanceReq.setWorkDateTo("2020-" + queryMonth + "-" + i + " 00:00:00");
            OapiAttendanceListResponse getAttendanceRes = getAttendanceClient.execute(getAttendanceReq, accessToken);
            //一天中的考勤次数情况
            List<OapiAttendanceListResponse.Recordresult> list = getAttendanceRes.getRecordresult();
            for (OapiAttendanceListResponse.Recordresult recordResult : list) {
                Date userCheckTime = recordResult.getUserCheckTime();
                HashMap<String, Date> dayMap = new HashMap<String, Date>();
                String checkType = recordResult.getCheckType();
                dayMap.put(checkType, userCheckTime);
                HashMap<String, Date> mapTmp = AttendanceMap.get(i);
                if (mapTmp == null) {
                    AttendanceMap.put(i, dayMap);
                } else {
                    if (mapTmp.get(checkType) != null) {
                        if (checkType.equals("OnDuty")) {
                            if (mapTmp.get(checkType).getTime() > userCheckTime.getTime()) {
                                mapTmp.put(checkType, userCheckTime);
                            }
                        } else {
                            if (mapTmp.get(checkType).getTime() < userCheckTime.getTime()) {
                                mapTmp.put(checkType, userCheckTime);
                            }
                        }


                    } else {
                        mapTmp.put(checkType, userCheckTime);
                    }
                }
            }
        }

        return AttendanceMap;
    }

    //根据考勤记录计算时间
    public HashMap<String, Float> queryAttendInfo(HashMap<Integer, HashMap<String, Date>> AttendanceMap) throws ApiException {

        int minutsCount = 0;
        float hoursCount = 0;
        for (Map.Entry<Integer, HashMap<String, Date>> entry : AttendanceMap.entrySet()) {
            Date onDuty = entry.getValue().get("OnDuty");
            Date offDuty = entry.getValue().get("OffDuty");
            if (onDuty == null || offDuty == null) {
                continue;
            }
            long onDutyTime = offDuty.getTime();
            long OffDutyTime = onDuty.getTime();
            long seconds = (onDutyTime - OffDutyTime) / 1000;
            float minutes = dived(seconds, 60);
            float hours = dived(minutes, 60);
            int roundMinutes = Math.round(minutes);
            minutsCount += roundMinutes;
            hoursCount += hours;
        }
        HashMap<String, Float> timeCountMap = new HashMap<>();
        timeCountMap.put("minutesCount", (float) minutsCount);
        timeCountMap.put("hoursCount", hoursCount);
        return timeCountMap;

    }
}
