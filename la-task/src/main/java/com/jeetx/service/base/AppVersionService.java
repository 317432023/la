package com.jeetx.service.base;

import com.jeetx.bean.base.AppVersion;
import com.jeetx.service.dao.DAO;

public interface AppVersionService extends DAO<AppVersion> {
	public AppVersion findAppVersion(Integer deviceType);
}
