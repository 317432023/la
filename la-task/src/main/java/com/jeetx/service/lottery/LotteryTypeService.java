package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryType;
import com.jeetx.service.dao.DAO;

public interface LotteryTypeService extends DAO<LotteryType> {
	public void autoClearData() throws Exception;
}
