package com.jeetx.service.member.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.BankCard;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.member.BankCardService;
@Service
@Transactional
public class BankCardServiceImpl extends DaoSupport<BankCard> implements BankCardService {
	
}
