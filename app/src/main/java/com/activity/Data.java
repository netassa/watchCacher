package com.activity;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name = "watchData")
public class Data {
    public Data(String device, String num, String watchId, String IMEI) {
        this.device = device;
        this.num = num;
        this.watchId = watchId;
        this.IMEI = IMEI;
    }

    @SmartColumn(id = 0, name = "机型", autoMerge = true)
    private String device;
    @SmartColumn(id = 1, name = "编号")
    private String num;
    @SmartColumn(id = 2, name = "绑定号")
    private String watchId;
    @SmartColumn(id = 3, name = "IMEI")
    private String IMEI;
}
