package com.jeetx.service.member.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.PointsLevel;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.member.PointsLevelService;

@Service
@Transactional
public class PointsLevelServiceImpl extends DaoSupport<PointsLevel> implements PointsLevelService {

	@SuppressWarnings("unchecked")
	public PointsLevel findPointsLevel(Integer points) {
		List<PointsLevel> list = this.getSession().createQuery("from PointsLevel o where o.minPoints <= ? and o.maxPoints >= ?").setParameter(0, points).setParameter(1, points).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (PointsLevel) list.get(0);
		}
	}
}
