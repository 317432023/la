package com.jeetx.service.base.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.Info;
import com.jeetx.service.base.InfoService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class InfoServiceImpl extends DaoSupport<Info> implements InfoService {

	
}
