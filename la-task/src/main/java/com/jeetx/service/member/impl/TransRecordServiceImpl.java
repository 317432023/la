package com.jeetx.service.member.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.Letter;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.member.TransRecordService;

@Service
@Transactional
public class TransRecordServiceImpl extends DaoSupport<TransRecord> implements TransRecordService {

	@SuppressWarnings("unchecked")
	public TransRecord findTransRecord(String username,Integer id) {
		List<TransRecord> list = this.getSession().createQuery("from TransRecord o where o.user.username = ? and o.id = ? ")
				.setParameter(0, username).setParameter(1, id).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (TransRecord) list.get(0);
		}
	}
}
