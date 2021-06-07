package com.jeetx.service.member;

import java.util.List;

import com.jeetx.bean.member.BankCard;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DAO;

public interface BankCardService extends DAO<BankCard> {
	public List<BankCard> getBankCardList(String username,Integer stationId);
	public BankCard findBankCard(String username,Integer bankCardId,Integer stationId);
	public BankCard addBankCard(User user,String ip,String device,String cardJson);
	public void delBankCard(User user,String ip,String device,Integer bankCardId);
}
