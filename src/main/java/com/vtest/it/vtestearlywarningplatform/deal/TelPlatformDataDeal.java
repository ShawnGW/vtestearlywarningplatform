package com.vtest.it.vtestearlywarningplatform.deal;


import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.DealWaferIdInformationBean;
import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.vtestearlywarningplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.vtestearlywarningplatform.services.rawdatatools.GenerateRawdataTemp;
import com.vtest.it.vtestearlywarningplatform.services.vtptmt.impl.VtptmtInforImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TelPlatformDataDeal {
    @Autowired
    private VtptmtInforImpl vtptmtInfor;
    @Autowired
    private GenerateRawdataInitInformation generateRawdataInitInformation;
    @Autowired
    private GenerateRawdataTemp generateRawdataTemp;

    public void deal(ArrayList<DealWaferIdInformationBean> source) {
        for (DealWaferIdInformationBean bean : source) {
            try {
                ArrayList<DataParseIssueBean> dataParseIssueBeans = new ArrayList<>();
                generateRawdata(bean,dataParseIssueBeans);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void generateRawdata(DealWaferIdInformationBean bean,ArrayList<DataParseIssueBean> dataParseIssueBeans) throws Exception {
        RawdataInitBean rawdataInitBean = generateRawdataInitInformation.generateRawdata(bean);
        boolean checkFlag = generateRawdataTemp.generateTempRawdata(rawdataInitBean, dataParseIssueBeans);
        if (checkFlag) {
            if (dataParseIssueBeans.size() > 0) {
                vtptmtInfor.dataErrorsRecord(dataParseIssueBeans);
            }
        }
    }
}
