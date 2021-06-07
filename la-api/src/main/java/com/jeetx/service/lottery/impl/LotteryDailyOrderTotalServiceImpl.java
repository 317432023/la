package com.jeetx.service.lottery.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;

@Service
@Transactional
public class LotteryDailyOrderTotalServiceImpl extends DaoSupport<LotteryDailyOrderTotal> implements LotteryDailyOrderTotalService {

	
	@SuppressWarnings("unchecked")
	public LotteryDailyOrderTotal getLotteryDailyOrderTotal(String totalDate,Integer userId) {
		List<LotteryDailyOrderTotal> list = this.getSession().createQuery("from LotteryDailyOrderTotal o where o.user.id = ? and o.totalDate <=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.totalDate >=str_to_date(?,'%Y-%m-%d %H:%i:%s')")
				.setParameter(0, userId).setParameter(1, totalDate).setParameter(2, totalDate).list();
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}
}
