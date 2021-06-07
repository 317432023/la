package com.jeetx.service.system;

import java.util.List;

import com.jeetx.bean.system.SystemConfig;
import com.jeetx.service.dao.DAO;

public interface SystemConfigService extends DAO<SystemConfig> {
	public List<SystemConfig> findByJpl(String jpl);
	public String getValueByName(String name);
}
