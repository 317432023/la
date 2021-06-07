package com.jeetx.service.base.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.Advert;
import com.jeetx.service.base.AdvertService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class AdvertServiceImpl extends DaoSupport<Advert> implements AdvertService {

	
}
