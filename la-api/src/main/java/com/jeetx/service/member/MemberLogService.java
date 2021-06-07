package com.jeetx.service.member;

import com.jeetx.bean.member.MemberLog;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DAO;

public interface MemberLogService extends DAO<MemberLog> {

	public void saveLog(User user,String params,String memo,String ip,String device);
}
