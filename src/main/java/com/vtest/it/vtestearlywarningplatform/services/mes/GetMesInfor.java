package com.vtest.it.vtestearlywarningplatform.services.mes;


import com.vtest.it.vtestearlywarningplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.vtestearlywarningplatform.pojo.mes.MesConfigBean;
import com.vtest.it.vtestearlywarningplatform.pojo.mes.SlotAndSequenceConfigBean;

public interface GetMesInfor {
    public String getWaferIdBySlot(String lot, String slot);
    public SlotAndSequenceConfigBean getLotSlotConfig(String lot);
    public MesConfigBean getWaferConfigFromMes(String waferId, String cpProcess);
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByLot(String lot);
    public String getCurrentCpStep(String waferId);
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByWaferAndCpStep(String waferId, String cpStep);
}
