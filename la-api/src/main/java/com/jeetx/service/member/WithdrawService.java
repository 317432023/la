package com.jeetx.service.member;

import java.math.BigDecimal;
import java.util.Date;

import com.jeetx.bean.member.User;
import com.jeetx.bean.member.Withdraw;
import com.jeetx.service.dao.DAO;

public interface WithdrawService extends DAO<Withdraw> {
//	public boolean isNewUserWithdraw(User user) throws Exception;
	public Withdraw findWithdraw(String username,Integer id);
	public void withdrawHandle(Integer id,Integer type,String remark,Integer stationId,Date handleTime) throws Exception ;
	public Withdraw applyWithdraw(User user,String ip,String device,String timestamp,String securityCode,
			Integer bankCardId,BigDecimal applyAmount,Date applyTime)throws Exception ;
	public Long getWithdrawCount(String queryDate,Integer userId) ;
	public Withdraw getLastWithdraw(Integer userId);
}
	