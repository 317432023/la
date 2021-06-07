package com.jeetx.service.system;

import org.springframework.web.bind.annotation.RequestParam;

import com.jeetx.bean.system.Station;
import com.jeetx.service.dao.DAO;

public interface StationService extends DAO<Station> {
	public Integer getStationId(Integer stationId,String referer);
	public Station getStationByEntryDomain(String entryDomain);
	public Station getStation(String stationName);
	public void createStation(String stationName,String stationDomain,String entryDomain,String imageDomain,String mqDomain) throws Exception;
}
