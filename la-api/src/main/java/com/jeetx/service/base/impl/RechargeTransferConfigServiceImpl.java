package com.jeetx.service.base.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.RechargeTransferConfig;
import com.jeetx.service.base.RechargeTransferConfigService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class RechargeTransferConfigServiceImpl extends DaoSupport<RechargeTransferConfig> implements RechargeTransferConfigService {

	
}
