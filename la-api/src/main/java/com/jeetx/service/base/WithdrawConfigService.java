package com.jeetx.service.base;

import com.jeetx.bean.base.WithdrawConfig;
import com.jeetx.service.dao.DAO;

public interface WithdrawConfigService extends DAO<WithdrawConfig> {
	public WithdrawConfig getWithdrawConfigByStationId(Integer stationId);
}
