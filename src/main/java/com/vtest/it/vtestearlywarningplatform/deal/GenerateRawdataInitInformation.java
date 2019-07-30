package com.vtest.it.vtestearlywarningplatform.deal;



import com.vtest.it.vtestearlywarningplatform.pojo.mes.MesConfigBean;
import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.DealWaferIdInformationBean;
import com.vtest.it.vtestearlywarningplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.vtestearlywarningplatform.services.mes.impl.GetMesInforImpl;
import com.vtest.it.vtestearlywarningplatform.services.probermappingparsetools.TelProberMappingNormalParse;
import com.vtest.it.vtestearlywarningplatform.services.probermappingparsetools.TelProberMappingSmallDieParse;
import com.vtest.it.vtestearlywarningplatform.services.rawdatatools.InitMesConfigToRawdataProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenerateRawdataInitInformation {

    @Autowired
    private InitMesConfigToRawdataProperties initMesConfigToRawdataProperties;
    @Autowired
    private GetMesInforImpl getMesInfor;
    @Autowired
    private TelProberMappingNormalParse telProberMappingNormalParse;
    @Autowired
    private TelProberMappingSmallDieParse telProberMappingSmallDieParse;

    public RawdataInitBean generateRawdata(DealWaferIdInformationBean bean) throws Exception {
        RawdataInitBean rawdataInitBean = bean.getRawdataInitBean();
        try {
            if (bean.isNormalDieFlag()){
                telProberMappingNormalParse.Get(bean.getFile(),rawdataInitBean);
            }else {
                telProberMappingSmallDieParse.Get(bean.getFile(),rawdataInitBean);
            }
        } catch (Exception e) {
            throw new Exception("there are error in file coding");
        }
        MesConfigBean mesConfigBean = getMesInfor.getWaferConfigFromMes(rawdataInitBean.getDataProperties().get("Wafer ID"), rawdataInitBean.getDataProperties().get("CP Process"));
        if (null == mesConfigBean.getInnerLot()) {
            throw new Exception("can't find this wafer in mes system : no such wafer or cpProcess");
        }
        initMesConfigToRawdataProperties.initMesConfig(rawdataInitBean, mesConfigBean);
        return rawdataInitBean;
    }
}
