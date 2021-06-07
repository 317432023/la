package com.jeetx.service.system;

import java.util.List;

import com.jeetx.bean.system.StationConfig;
import com.jeetx.service.dao.DAO;

public interface StationConfigService extends DAO<StationConfig> {
	public List<StationConfig> findByJpl(String jpl);
	public String getValueByName(String Name,Integer stationId);
	public List<StationConfig> getListByStationId(Integer stationId);
}
