package com.vtest.it.vtestearlywarningplatform.pojo.vtptmt;

import java.io.Serializable;

public class DataInforToMesBean implements Serializable {
    private static final long serialVersionUID = 1l;
    private String customCode;
    private String device;

    public String getCustomCode() {
        return customCode;
    }

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
