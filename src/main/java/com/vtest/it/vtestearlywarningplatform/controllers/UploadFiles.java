package com.vtest.it.vtestearlywarningplatform.controllers;

import com.alibaba.fastjson.JSON;
import com.vtest.it.vtestearlywarningplatform.deal.TelPlatformDataDeal;
import com.vtest.it.vtestearlywarningplatform.pojo.mes.MesConfigBean;
import com.vtest.it.vtestearlywarningplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.DealWaferIdInformationBean;
import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.vtestearlywarningplatform.services.mes.impl.GetMesInforImpl;
import com.vtest.it.vtestearlywarningplatform.services.probermappingparsetools.TelOpusProberMappingDaParse;
import com.vtest.it.vtestearlywarningplatform.services.tools.PerfectCopy;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadFiles {
    @Value("${system.properties.tel.mapdown}")
    private String mapdown;
    @Autowired
    private TelOpusProberMappingDaParse telOpusProberMappingDaParse;
    @Autowired
    private GetMesInforImpl getMesInfor;
    @Autowired
    private TelPlatformDataDeal telPlatformDataDeal;
    @Autowired
    private PerfectCopy perfectCopy;
    private SimpleDateFormat simpleDateFormatOthers = new SimpleDateFormat("yyMMddHHmm");
    @PostMapping("/tel")
    public Map<Boolean, String> upload(Part[] files, String cpProcess, Boolean ifMapDown, String type, String lot) {
        lot = lot.trim();
        ArrayList<File> sourceFiles=new ArrayList<>();
        Map<Boolean,String> result=new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        try {
            ArrayList<DealWaferIdInformationBean> dealWaferIdInformationBeanArrayList =new ArrayList<>();
            ArrayList<File> datFileList=new ArrayList<>();
            ArrayList<File> daFileList=new ArrayList<>();
            ArrayList<File> rmpFileList=new ArrayList<>();
            try {
                for (Part file : files) {
                    file.getSubmittedFileName();
                    file.write("/upload/" + file.getSubmittedFileName());
                    File uploadFile = new File("/upload/" + file.getSubmittedFileName());
                    if (ifMapDown) {
                        perfectCopy.copy(uploadFile, new File(mapdown + lot + "/" + getFileNameAfterModify(uploadFile.getName())));
                    }
                    sourceFiles.add(uploadFile);
                    if (uploadFile.getName().endsWith(".DAT")){
                        datFileList.add(uploadFile);
                    }else if(uploadFile.getName().endsWith(".DA")){
                        daFileList.add(uploadFile);
                    }else if(uploadFile.getName().endsWith(".RMP")){
                        rmpFileList.add(uploadFile);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                result.put(false,"upload failed");
                return  result;
            }
            if (null == getMesInfor.getCustomerAndDeviceByLot(lot).getCustomerCode()) {
                result.put(false, "this lot is not exist ,please check it again!");
                return result;
            }
            if (type.equals("normal")){
                dealDatUploadFile(dealWaferIdInformationBeanArrayList, datFileList, cpProcess, lot, errors);
            }else {
                dealRpmUploadFile(dealWaferIdInformationBeanArrayList, daFileList, rmpFileList, cpProcess, lot, errors);
            }
            telPlatformDataDeal.deal(dealWaferIdInformationBeanArrayList);
            if (errors.size() > 0) {
                result.put(false, JSON.toJSONString(errors));
                return result;
            }
            result.put(true,"generate success");
        }catch (Exception e){
            result.put(false,"generate failed :"+e.getMessage());
        }finally {
            for (File file : sourceFiles) {
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void dealDatUploadFile(ArrayList<DealWaferIdInformationBean> dealWaferIdInformationBeanArrayList, ArrayList<File> datFileList, String cpProcess, String lot, Map<String, String> errors) {
        SlotAndSequenceConfigBean slotAndSequenceConfigBean = getMesInfor.getLotSlotConfig(lot);
        boolean slotFlag = false;
        if (slotAndSequenceConfigBean.getReadType().toUpperCase().equals("SLOT")) {
            slotFlag = true;
        }
        for (File datFile : datFileList) {
            DealWaferIdInformationBean dealWaferIdInformationBean = new DealWaferIdInformationBean();
            dealWaferIdInformationBean.setNormalDieFlag(true);
            Map<String, String> information = getFileInformaton(datFile.getName());
            String waferId = information.get("waferId");
            String rightWaferId = waferId;
            if (slotFlag) {
                try {
                    String tempWaferId = getMesInfor.getWaferIdBySlot(lot, waferId.substring(waferId.length() - 2));
                    rightWaferId = "NA".equals(tempWaferId) ? rightWaferId : tempWaferId;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MesConfigBean mesConfigBean = getMesInfor.getWaferConfigFromMes(rightWaferId, cpProcess.trim());
            if (null == mesConfigBean.getInnerLot()) {
                errors.put(waferId + " : " + rightWaferId, "can't find this wafer in mes system : no such wafer or cpProcess");
            } else {
                RawdataInitBean rawdataInitBean = new RawdataInitBean();
                LinkedHashMap<String, String> dataProperties = new LinkedHashMap<>();
                dataProperties.put("Wafer ID", rightWaferId);
                dataProperties.put("Operator", "V888");
                dataProperties.put("CP Process", cpProcess.trim());
                dataProperties.put("Lot ID", lot);
                rawdataInitBean.setDataProperties(dataProperties);
                dealWaferIdInformationBean.setRawdataInitBean(rawdataInitBean);
                dealWaferIdInformationBean.setFile(datFile);
                dealWaferIdInformationBeanArrayList.add(dealWaferIdInformationBean);
            }
        }
    }

    public void dealRpmUploadFile(ArrayList<DealWaferIdInformationBean> dealWaferIdInformationBeanArrayList, ArrayList<File> daFileList, ArrayList<File> rmpFileList, String cpProcess, String lot, Map<String, String> errors) {
        Map<String, Map<String, String>> waferIdTestTimeInformation = new HashMap<>();
        for (File daFile : daFileList) {
            try {
                HashMap<String, String> resultMap = telOpusProberMappingDaParse.GetResult(daFile);
                Map<String, String> information = getFileInformaton(daFile.getName());
                String waferId = information.get("waferId");
                waferIdTestTimeInformation.put(waferId, resultMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File rmpFile : rmpFileList) {
            DealWaferIdInformationBean dealWaferIdInformationBean = new DealWaferIdInformationBean();
            dealWaferIdInformationBean.setNormalDieFlag(false);
            RawdataInitBean rawdataInitBean = new RawdataInitBean();
            Map<String, String> information = getFileInformaton(rmpFile.getName());
            String waferId = information.get("waferId");

            String testStartTime = waferIdTestTimeInformation.get(waferId).get("testStartTime");
            String testEndTime = waferIdTestTimeInformation.get(waferId).get("testEndTime");
            testStartTime = null == testStartTime ? simpleDateFormatOthers.format(new Date()) : testStartTime;
            testEndTime = null == testEndTime ? simpleDateFormatOthers.format(new Date()) : testEndTime;

            MesConfigBean mesConfigBean = getMesInfor.getWaferConfigFromMes(waferId, cpProcess.trim());
            if (null == mesConfigBean.getInnerLot()) {
                errors.put(waferId, "can't find this wafer in mes system : no such wafer or cpProcess");
            } else {
                LinkedHashMap<String, String> dataProperties = new LinkedHashMap<>();
                dataProperties.put("Wafer ID", waferId);
                dataProperties.put("Operator", "V888");
                dataProperties.put("CP Process", cpProcess);
                dataProperties.put("Test Start Time", testStartTime);
                dataProperties.put("Test End Time", testEndTime);
                dataProperties.put("Lot ID", lot);
                rawdataInitBean.setDataProperties(dataProperties);
                dealWaferIdInformationBean.setRawdataInitBean(rawdataInitBean);
                dealWaferIdInformationBean.setFile(rmpFile);
                dealWaferIdInformationBeanArrayList.add(dealWaferIdInformationBean);
            }
        }
    }
    public String getFileNameAfterModify(String fileName) {
        Map<String, String> information = getFileInformaton(fileName);
        return information.get("waferId") + "1" + information.get("suffix");
    }
    public Map<String, String> getFileInformaton(String mappingName) {
        Map<String, String> information = new HashMap<>();
        String waferId = mappingName.substring(0, mappingName.lastIndexOf(".") - 1);
        Integer time = Integer.valueOf(mappingName.substring(mappingName.lastIndexOf(".") - 1, mappingName.lastIndexOf(".")));
        String suffix = mappingName.substring(mappingName.lastIndexOf("."));
        information.put("waferId", waferId);
        information.put("time", String.valueOf(time));
        information.put("suffix", suffix);
        return information;
    }
}
