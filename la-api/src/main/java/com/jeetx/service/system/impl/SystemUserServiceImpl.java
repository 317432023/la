package com.jeetx.service.system.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.system.SystemUser;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.system.SystemUserService;

@Service
@Transactional
public class SystemUserServiceImpl extends DaoSupport<SystemUser> implements SystemUserService {


}
