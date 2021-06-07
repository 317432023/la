package com.jeetx.service.lottery.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRoomService;

@Service
@Transactional
public class LotteryRoomServiceImpl extends DaoSupport<LotteryRoom> implements LotteryRoomService {

}
