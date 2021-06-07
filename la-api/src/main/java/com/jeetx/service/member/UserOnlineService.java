package com.jeetx.service.member;

import com.jeetx.bean.member.UserOnline;
import com.jeetx.service.dao.DAO;

public interface UserOnlineService extends DAO<UserOnline> {
	public UserOnline findUserOnline(Integer userId);
	
}
