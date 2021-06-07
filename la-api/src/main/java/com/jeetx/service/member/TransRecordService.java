package com.jeetx.service.member;

import com.jeetx.bean.member.TransRecord;
import com.jeetx.service.dao.DAO;

public interface TransRecordService extends DAO<TransRecord> {
	public TransRecord findTransRecord(String username,Integer id) ;
}
