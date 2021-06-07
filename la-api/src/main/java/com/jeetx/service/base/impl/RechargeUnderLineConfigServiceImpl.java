package com.jeetx.service.base.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.RechargeUnderLineConfig;
import com.jeetx.service.base.RechargeUnderLineConfigService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class RechargeUnderLineConfigServiceImpl extends DaoSupport<RechargeUnderLineConfig> implements RechargeUnderLineConfigService {

	
}
