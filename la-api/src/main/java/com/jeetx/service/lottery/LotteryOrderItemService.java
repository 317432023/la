package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryOrderItem;
import com.jeetx.service.dao.DAO;

public interface LotteryOrderItemService extends DAO<LotteryOrderItem> {
	public List<LotteryOrderItem> findLotteryOrderItemList(Integer lotteryOrderId);
}
