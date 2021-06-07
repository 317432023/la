package com.jeetx.service.member.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.User;
import com.jeetx.bean.member.UserOnline;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.member.UserOnlineService;

@Service
@Transactional
public class UserOnlineServiceImpl extends DaoSupport<UserOnline> implements UserOnlineService {
	
	@SuppressWarnings("unchecked")
	public UserOnline findUserOnline(Integer userId) {
		List<UserOnline> list = this.getSession().createQuery("from UserOnline o where o.user.id = ?").setParameter(0, userId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (UserOnline) list.get(0);
		}
	}
}
