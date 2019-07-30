package com.vtest.it.vtestearlywarningplatform.services.FailDieCheck;


import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.vtestearlywarningplatform.pojo.vtptmt.BinWaferInforBean;

public interface RawDataAfterDeal {
    void deal(RawdataInitBean rawdataInitBean, BinWaferInforBean binWaferInforBean);
}
