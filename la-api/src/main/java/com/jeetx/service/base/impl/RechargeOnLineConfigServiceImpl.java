package com.jeetx.service.base.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.RechargeOnLineConfig;
import com.jeetx.service.base.RechargeOnLineConfigService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class RechargeOnLineConfigServiceImpl extends DaoSupport<RechargeOnLineConfig> implements RechargeOnLineConfigService {

	
}
