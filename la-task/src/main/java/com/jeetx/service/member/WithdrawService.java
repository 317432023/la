package com.jeetx.service.member;

import com.jeetx.bean.member.Withdraw;
import com.jeetx.service.dao.DAO;

public interface WithdrawService extends DAO<Withdraw> {
	public Withdraw findWithdraw(String username,Integer id);
}
