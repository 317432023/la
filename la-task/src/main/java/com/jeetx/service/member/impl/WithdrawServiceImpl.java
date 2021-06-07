package com.jeetx.service.member.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.Withdraw;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.member.LetterService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.member.WithdrawService;

@Service
@Transactional
public class WithdrawServiceImpl extends DaoSupport<Withdraw> implements WithdrawService {

	@Autowired UserService userService;
	@Autowired TransRecordService transRecordService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired LetterService letterService;
	
	@SuppressWarnings("unchecked")
	public Withdraw findWithdraw(String username,Integer id) {
		List<Withdraw> list = this.getSession().createQuery("from Withdraw o where o.user.username = ? and o.id = ? ")
				.setParameter(0, username).setParameter(1, id).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (Withdraw) list.get(0);
		}
	}
}
