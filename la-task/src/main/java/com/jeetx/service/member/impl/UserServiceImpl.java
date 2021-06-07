package com.jeetx.service.member.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.member.MemberLogService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserService;

@Service
@Transactional
public class UserServiceImpl extends DaoSupport<User> implements UserService {

	@Autowired TransRecordService transRecordService;
	@Autowired MemberLogService memberLogService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	
	@SuppressWarnings("unchecked")
	public User findUser(String username) {
		List<User> list = this.getSession().createQuery("from User o where o.username = ? ").setParameter(0, username).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public User checkUser(String username,String password) {
		List<User> list = this.getSession().createQuery("from User o where o.username = ? and o.password = ?")
				.setParameter(0, username).setParameter(1, password).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public User checkUserByToken(String username,String loginToken) {
		List<User> list = this.getSession().createQuery("from User o where o.username = ? and o.loginToken = ?")
				.setParameter(0, username).setParameter(1, loginToken).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
}
