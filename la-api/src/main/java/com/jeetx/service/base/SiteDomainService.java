package com.jeetx.service.base;

import java.util.List;

import com.jeetx.bean.base.SiteDomain;
import com.jeetx.service.dao.DAO;

public interface SiteDomainService extends DAO<SiteDomain> {
	public List<SiteDomain> getSiteDomainByStationId(Integer stationId) ;
}
