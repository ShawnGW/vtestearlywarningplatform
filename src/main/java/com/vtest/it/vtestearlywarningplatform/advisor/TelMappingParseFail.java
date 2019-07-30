package com.vtest.it.vtestearlywarningplatform.advisor;


import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.DealWaferIdInformationBean;
import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.vtestearlywarningplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.vtestearlywarningplatform.services.FailDieCheck.AdjacentFailDieCheck;
import com.vtest.it.vtestearlywarningplatform.services.rawdataCheck.GetIssueBean;
import com.vtest.it.vtestearlywarningplatform.services.vtptmt.impl.VtptmtInforImpl;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Aspect
@Component
@Order(1)
public class TelMappingParseFail {
    @Autowired
    private GetIssueBean getIssueBean;
    @Autowired
    private VtptmtInforImpl vtptmtInfor;
    @Autowired
    private AdjacentFailDieCheck adjacentFailDieCheck;

    @AfterThrowing(value = "execution(* generateRawdata(..)) && target(com.vtest.it.vtestearlywarningplatform.deal.GenerateRawdataInitInformation)&& args(bean)", throwing = "exception")
    private void mappingParseDealException(DealWaferIdInformationBean bean, Exception exception) {
        try {
            DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBeanForException(5, exception.getMessage(), bean.getFile(), bean.getRawdataInitBean().getDataProperties().get("Wafer ID"), bean.getRawdataInitBean().getDataProperties().get("Lot ID"));
            ArrayList<DataParseIssueBean> list = new ArrayList<>();
            list.add(dataParseIssueBean);
            vtptmtInfor.dataErrorsRecord(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterReturning(value = "execution(* generateRawdata(..)) && target(com.vtest.it.vtestearlywarningplatform.deal.GenerateRawdataInitInformation)", returning = "rawdataInitBean")
    public void optimizeRawdataBean(RawdataInitBean rawdataInitBean) {
        adjacentFailDieCheck.perfectDeal(rawdataInitBean);
    }
}
