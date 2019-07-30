package com.vtest.it.vtestearlywarningplatform.services.rawdataCheck;

import com.vtest.it.vtestearlywarningplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.vtestearlywarningplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.vtestearlywarningplatform.services.mes.GetMesInfor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Service
public class GetIssueBean {
    @Value("${system.properties.tel.error-path}")
    private String errorPath;
    @Autowired
    private GetMesInfor getMesInfor;
    public DataParseIssueBean getDataBean(HashMap<String, String> waferInfor, int level, String descripth) {
        DataParseIssueBean dataParseIssueBean = new DataParseIssueBean();
        dataParseIssueBean.setCustomCode(waferInfor.get("customCode"));
        dataParseIssueBean.setDevice(waferInfor.get("device"));
        dataParseIssueBean.setLotId(waferInfor.get("lot"));
        dataParseIssueBean.setCpStep(waferInfor.get("cpStep"));
        dataParseIssueBean.setWaferNo(waferInfor.get("waferNo"));
        dataParseIssueBean.setResource(waferInfor.get("resource"));
        dataParseIssueBean.setIssueType("data Check");
        dataParseIssueBean.setIssuLevel(level);
        dataParseIssueBean.setIssuePath("na");
        dataParseIssueBean.setIssueDescription(descripth);
        dataParseIssueBean.setDealFlag(0);
        return dataParseIssueBean;
    }

    public DataParseIssueBean getDataBeanForException(int level, String description, File file, String waferId, String lot) throws IOException {
        String cpProcess = getMesInfor.getCurrentCpStep(waferId);
        CustomerCodeAndDeviceBean customerCodeAndDeviceBean = getMesInfor.getCustomerAndDeviceByWaferAndCpStep(waferId, cpProcess);
        HashMap<String, String> waferInfor = new HashMap<>();
        waferInfor.put("customCode", customerCodeAndDeviceBean.getCustomerCode());
        waferInfor.put("device", customerCodeAndDeviceBean.getDevice());
        waferInfor.put("lot", lot);
        waferInfor.put("cpStep", cpProcess);
        waferInfor.put("waferNo", waferId);
        waferInfor.put("resource", "TSK");
        DataParseIssueBean dataParseIssueBean = getDataBean(waferInfor, level, description);
        if (description.equals("there are error in file coding")) {
            dataParseIssueBean.setIssueType("mapping parse");
        } else {
            dataParseIssueBean.setIssueType("mes information");
        }
        dataParseIssueBean.setIssuePath(errorPath + "mappingParseError/" + lot + "/" + file.getName());
        FileUtils.copyFile(file, new File(errorPath + "mappingParseError/" + lot + "/" + file.getName()));
        return dataParseIssueBean;
    }
}
