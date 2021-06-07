package com.jeetx.service.base.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.AppVersion;
import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.service.base.AppVersionService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class AppVersionServiceImpl extends DaoSupport<AppVersion> implements AppVersionService {

	public AppVersion findAppVersion(Integer deviceType,Integer stationId) {
		List<AppVersion> list = this.getSession().createQuery("from AppVersion o where o.deviceType = ? and o.station.id = ? ").setParameter(0, deviceType).setParameter(1, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (AppVersion) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AppVersion> getAppVersionByStationId(Integer stationId) {
		return this.getSession().createQuery("from AppVersion o where o.station.id = ? ").setParameter(0, stationId).list();
	}
}
