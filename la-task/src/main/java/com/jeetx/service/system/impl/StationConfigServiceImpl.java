package com.jeetx.service.system.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.system.StationConfig;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.system.StationConfigService;

@Service
@Transactional
public class StationConfigServiceImpl extends DaoSupport<StationConfig> implements StationConfigService {

	@SuppressWarnings("unchecked")
	public List<StationConfig> findByJpl(String jpl) {
		return this.getSession().createQuery(jpl).list();
	}
	

	public String getValueByName(String name,Integer stationId) {
		List<String> list=this.getSession().createQuery("select value from StationConfig where name = ? and station.id = ?").setParameter(0, name).setParameter(1, stationId).list();
		if(!list.isEmpty()&&list.size()>=0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public List<StationConfig> getListByStationId(Integer stationId) {
		return this.getSession().createQuery("from StationConfig where station.id = ?").setParameter(0, stationId).list();
	}
}
