package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.service.dao.DAO;

public interface LotteryDailyOrderTotalService extends DAO<LotteryDailyOrderTotal> {
	public LotteryDailyOrderTotal getLotteryDailyOrderTotal(String totalDate,Integer userId);
}
