package com.jeetx.service.lottery.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryWaterRecord;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryWaterRecordService;

@Service
@Transactional
public class LotteryWaterRecordServiceImpl extends DaoSupport<LotteryWaterRecord> implements LotteryWaterRecordService {

}
