package com.jeetx.service.member;

import com.jeetx.bean.member.PointsLevel;
import com.jeetx.service.dao.DAO;

public interface PointsLevelService extends DAO<PointsLevel> {
	public PointsLevel findPointsLevel(Integer points);
}
