package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryOrderItem;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryOrderItemService;

@Service
@Transactional
public class LotteryOrderItemServiceImpl extends DaoSupport<LotteryOrderItem> implements LotteryOrderItemService {
	@SuppressWarnings("unchecked")
	public List<LotteryOrderItem> findLotteryOrderItemList(Integer lotteryOrderId) {
		return this.getSession().createQuery("from LotteryOrderItem o where o.lotteryOrder.id = ? ").setParameter(0, lotteryOrderId).list();
	}
}
