package com.jeetx.service.base.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.WithdrawConfig;
import com.jeetx.service.base.WithdrawConfigService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class WithdrawConfigServiceImpl extends DaoSupport<WithdrawConfig> implements WithdrawConfigService {

	@SuppressWarnings("unchecked")
	public WithdrawConfig getWithdrawConfigByStationId(Integer stationId) {
		List<WithdrawConfig> list = this.getSession().createQuery("from WithdrawConfig o where o.station.id = ? ").setParameter(0, stationId).list();
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}
}
