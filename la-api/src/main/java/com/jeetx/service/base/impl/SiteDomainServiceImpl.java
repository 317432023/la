package com.jeetx.service.base.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.AppVersion;
import com.jeetx.bean.base.SiteDomain;
import com.jeetx.service.base.SiteDomainService;
import com.jeetx.service.dao.DaoSupport;

@Service
@Transactional
public class SiteDomainServiceImpl extends DaoSupport<SiteDomain> implements SiteDomainService {

	@SuppressWarnings("unchecked")
	public List<SiteDomain> getSiteDomainByStationId(Integer stationId) {
		return this.getSession().createQuery("from SiteDomain o where o.station.id = ? ").setParameter(0, stationId).list();
	}
	
}
