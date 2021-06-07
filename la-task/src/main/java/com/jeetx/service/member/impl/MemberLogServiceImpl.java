package com.jeetx.service.member.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.MemberLog;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.member.MemberLogService;
@Service
@Transactional
public class MemberLogServiceImpl extends DaoSupport<MemberLog> implements MemberLogService {

}
