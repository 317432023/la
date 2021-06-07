package com.jeetx.service.member.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.BankCard;
import com.jeetx.bean.member.User;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.controller.api.ApiUtil;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.member.BankCardService;
import com.jeetx.service.member.MemberLogService;
import com.jeetx.service.member.UserService;

import net.sf.json.JSONObject;
@Service
@Transactional
public class BankCardServiceImpl extends DaoSupport<BankCard> implements BankCardService {
	
	@Autowired MemberLogService memberLogService;
	@Autowired UserService userService;
	
	@SuppressWarnings("unchecked")
	public List<BankCard> getBankCardList(String username,Integer stationId) {
		return this.getSession().createQuery("from BankCard o where o.user.username = ? and o.user.station.id = ?")
				.setParameter(0, username).setParameter(1, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public BankCard findBankCard(String username,Integer bankCardId,Integer stationId) {
		List<BankCard> list = this.getSession().createQuery("from BankCard o where o.user.username = ? and o.id = ? and o.user.station.id = ?")
				.setParameter(0, username).setParameter(1, bankCardId).setParameter(2, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (BankCard) list.get(0);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public BankCard addBankCard(User user,String ip,String device,String cardJson) {
		String cardholder = null; //持卡人姓名
		String bankCardNo = null;//银行卡号
		String bankName = null; // 银行名称
		String openBankPlace = null; // 开户地点
		String openBankBranch = null;//开户支行
		try {
			userService.validateUser(user, true, true);//验证用户
			
			JSONObject bankCardJson= JSONObject.fromObject(cardJson);
			cardholder = bankCardJson.getString("cardholder");
			bankCardNo = bankCardJson.getString("cardNo");
			bankName = bankCardJson.getString("bankName");
			openBankPlace = bankCardJson.get("bankPlace")!=null?bankCardJson.getString("bankPlace"):"";
			openBankBranch = bankCardJson.get("bankBranch")!=null?bankCardJson.getString("bankBranch"):"";
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ApiUtil.getErrorCode("115"));
		}
		
		List<BankCard> bankCardList = this.getBankCardList(user.getUsername(), user.getStation().getId());
		if(bankCardList !=null && bankCardList.size() >0) {
			if(!cardholder.equalsIgnoreCase(bankCardList.get(0).getCardholder())) {
				throw new BusinessException(ApiUtil.getErrorCode("165").replace("姓名", bankCardList.get(0).getCardholder()));
			}
		}
		
		BankCard bankCard = new BankCard();
		bankCard.setUser(user);
		bankCard.setBankCard(bankCardNo);
		bankCard.setBankName(bankName);
		bankCard.setCardholder(cardholder);
		bankCard.setOpenBankBranch(openBankBranch);
		bankCard.setOpenBankPlace(openBankPlace);
		this.save(bankCard);

		memberLogService.saveLog(user, cardJson, "新增银行卡", ip, device);
		
		return bankCard;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void delBankCard(User user,String ip,String device,Integer bankCardId) {
		userService.validateUser(user, true, true);//验证用户
		
		BankCard bankCard = this.findBankCard(user.getUsername(),bankCardId,user.getStation().getId());
		if(bankCard == null) {
			throw new BusinessException(ApiUtil.getErrorCode("114"));
		}
		this.delete(bankCard);
		
		memberLogService.saveLog(user, "id:".concat(bankCardId+""), "删除银行卡", ip, device);
	}
}
