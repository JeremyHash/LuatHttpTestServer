package com.openluat.pojo;

import lombok.Data;

@Data
public class GpsInfo {
    private String isFix;
    private String lng;
    private String lat;
    private String altitude;
    private String speed;
    private String azimuth;
    private String usedSateCnt;
    private String viewedSateCnt;
}
