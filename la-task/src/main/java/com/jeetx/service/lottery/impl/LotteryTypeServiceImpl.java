package com.jeetx.service.lottery.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryType;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryTypeService;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

@Service
@Transactional
public class LotteryTypeServiceImpl extends DaoSupport<LotteryType> implements LotteryTypeService {

	//自动清理数据
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void autoClearData() throws Exception {
		String queryDate = null;
		
		//删除消息记录(保留最近三天的数据)
		queryDate = DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.getDaysByDate2Days(1,new Date()));
		LogUtil.info("删除消息记录，删除时间截止："+DateTimeTool.queryStartDate(queryDate));
		try {
			getSession().createSQLQuery("delete from tb_lottery_room_message where create_time <= str_to_date('"+DateTimeTool.queryEndDate(queryDate)+"','%Y-%m-%d %H:%i:%s');").executeUpdate();
			//LogUtil.info("delete from tb_lottery_room_message where create_time <= str_to_date('"+DateTimeTool.queryEndDate(queryDate)+"','%Y-%m-%d %H:%i:%s');");
		}catch (Exception e) {
			LogUtil.info("删除消息记录异常");
			e.printStackTrace();
		}
		
//		//删除统计记录(保留最近35天的数据)
//		queryDate = DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.getDaysByDate2Days(35,new Date()));
//		System.out.println("删除统计记录，删除时间截止："+DateTimeTool.queryStartDate(queryDate));
//		try {
//			System.out.println("删除统计记录1");
//			em.createNativeQuery("DELETE from TB_LOTTERY_ORDERROOM_TOTAL where TOTAL_DATE <= str_to_date('"+DateTimeTool.queryEndDate(queryDate)+"','%Y-%m-%d %H:%i:%s');").executeUpdate();		
//			System.out.println("DELETE from TB_LOTTERY_ORDERROOM_TOTAL where TOTAL_DATE <= str_to_date('"+DateTimeTool.queryEndDate(queryDate)+"','%Y-%m-%d %H:%i:%s');");
//		}catch (Exception e) {
//			System.out.println("删除统计记录异常1");
//			e.printStackTrace();
//		}
		
//		//删除下注记录(保留最近三天的数据)
//		queryDate = DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.getDaysByDate2Days(3,new Date()));
//		System.out.println("删除下注记录，删除时间截止："+DateTimeTool.queryStartDate(queryDate));
//		try {
//			System.out.println("删除下注记录");
//			em.createNativeQuery("DELETE from TB_LOTTERY_ORDER where UPDATE_TIME <= str_to_date('"+DateTimeTool.queryEndDate(queryDate)+"','%Y-%m-%d %H:%i:%s');").executeUpdate();
//			System.out.println("DELETE from TB_LOTTERY_ORDER where UPDATE_TIME <= str_to_date('"+DateTimeTool.queryEndDate(queryDate)+"','%Y-%m-%d %H:%i:%s');");
//		}catch (Exception e) {
//			System.out.println("删除下注记录异常");
//			e.printStackTrace();
//		}
	}
}
