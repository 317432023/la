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

	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void saveLog(User user,String params,String memo,String ip,String device) {
		MemberLog memberLog = new MemberLog();
		memberLog.setUser(user);
		memberLog.setArgument(params);
		memberLog.setMemo(memo);
		memberLog.setIp(ip);
		memberLog.setDevice(device);
		memberLog.setCreateTime(new Date());
		this.save(memberLog);
	}

}
