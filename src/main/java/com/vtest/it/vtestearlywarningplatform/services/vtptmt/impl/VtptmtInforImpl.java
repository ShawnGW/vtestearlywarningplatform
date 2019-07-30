package com.vtest.it.vtestearlywarningplatform.services.vtptmt.impl;


import com.vtest.it.vtestearlywarningplatform.dao.vtptmt.VtptmtDao;
import com.vtest.it.vtestearlywarningplatform.pojo.mes.MesProperties;
import com.vtest.it.vtestearlywarningplatform.pojo.vtptmt.BinWaferInforBean;
import com.vtest.it.vtestearlywarningplatform.pojo.vtptmt.CheckItemBean;
import com.vtest.it.vtestearlywarningplatform.pojo.vtptmt.DataInforToMesBean;
import com.vtest.it.vtestearlywarningplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.vtestearlywarningplatform.services.vtptmt.VtptmtInfor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(transactionManager = "vtptmtTransactionManager", isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
public class VtptmtInforImpl implements VtptmtInfor {
    @Autowired
    private VtptmtDao vtptmtDao;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int dataErrorsRecord(ArrayList<DataParseIssueBean> list) {
        for (DataParseIssueBean bean : list) {
            if (null == bean.getCustomCode()) {
                bean.setCustomCode("NA");
            }
            if (null == bean.getDevice()) {
                bean.setDevice("NA");
            }
        }
        return vtptmtDao.dataErrorsRecord(list);
    }

    @Override
    public ArrayList<CheckItemBean> getCheckItemList() {
        return vtptmtDao.getCheckItemList();
    }

    @Override
    public ArrayList<DataInforToMesBean> getList() {
        return vtptmtDao.getList();
    }

    @Override
    public ArrayList<BinWaferInforBean> getTesterStatus() {
        return vtptmtDao.getTesterStatus();
    }

    @Override
    public BinWaferInforBean getTesterStatusSingle(String tester) {
        return vtptmtDao.getTesterStatusSingle(tester);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int insertWaferInforToBinWaferSummary(BinWaferInforBean binWaferInforBean) {
        return vtptmtDao.insertWaferInforToBinWaferSummary(binWaferInforBean);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void waferFailTypeCheckOthers(String waferId, String cpProcess, String tester) {
        vtptmtDao.waferFailTypeCheckOthers(waferId, cpProcess, tester);
    }

    @Override
    public MesProperties getProperties() {
        return vtptmtDao.getProperties();
    }

    @Override
    public int updateProperties(MesProperties mesProperties) {
        return vtptmtDao.updateProperties(mesProperties);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void singleWaferDeal(BinWaferInforBean binWaferInforBean, String waferId, String cpProcess, String tester) {
        vtptmtDao.insertWaferInforToBinWaferSummary(binWaferInforBean);
        vtptmtDao.waferFailTypeCheckOthers(waferId, cpProcess, tester);
    }
}
