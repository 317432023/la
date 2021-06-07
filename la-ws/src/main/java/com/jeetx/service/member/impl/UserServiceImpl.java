package com.jeetx.service.member.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.member.UserService;

@Service
@Transactional
public class UserServiceImpl extends DaoSupport<User> implements UserService {
	@SuppressWarnings("unchecked")
	public User findUserByToken(String loginToken) {
		List<User> list = this.getSession().createQuery("from User o where o.loginToken = ?").setParameter(0, loginToken).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
}
