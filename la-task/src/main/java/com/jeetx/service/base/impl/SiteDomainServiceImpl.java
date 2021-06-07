package com.jeetx.service.base.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.SiteDomain;
import com.jeetx.service.base.SiteDomainService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class SiteDomainServiceImpl extends DaoSupport<SiteDomain> implements SiteDomainService {

	
}
