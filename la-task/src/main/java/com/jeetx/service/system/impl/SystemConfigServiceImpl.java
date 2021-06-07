package com.jeetx.service.system.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.system.SystemConfig;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.system.SystemConfigService;

@Service
@Transactional
public class SystemConfigServiceImpl extends DaoSupport<SystemConfig> implements SystemConfigService {

	@SuppressWarnings("unchecked")
	public List<SystemConfig> findByJpl(String jpl) {
		return this.getSession().createQuery(jpl).list();
	}
	

	public String getValueByName(String name) {
		List<String> list=this.getSession().createQuery("select value from SystemConfig where name = ?").setParameter(0, name).list();
		if(!list.isEmpty()&&list.size()>=0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
}
