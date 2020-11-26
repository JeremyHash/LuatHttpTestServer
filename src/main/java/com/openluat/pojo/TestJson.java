package com.openluat.pojo;

import lombok.Data;

@Data
public class TestJson {
    /*
        {
            "imei": "123456789012345",
            "mcc": "460",
            "mnc": "0",
            "lac": "21133",
            "ci": "52365",
            "hex": "10"
        }
     */
    private String imei;
    private String mcc;
    private String mnc;
    private String lac;
    private String ci;
    private String hex;
}
