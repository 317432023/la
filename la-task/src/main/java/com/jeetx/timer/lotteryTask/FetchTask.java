package com.jeetx.timer.lotteryTask;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class FetchTask implements Callable<LotteryDTO> {
	public static Logger logger = Logger.getLogger(FetchTask.class);
	private static int maxRunCount1 = 80;
	private static int maxRunCount2 = 280;
	
	private String provider;
	private Integer lotteryType;
	private String openPeriods;
	private String sourceUrl;  

	public FetchTask(String provider,Integer lotteryType,String openPeriods,String sourceUrl) {
		this.provider = provider;
		this.lotteryType = lotteryType;
		this.openPeriods = openPeriods;
		this.sourceUrl = sourceUrl;
	}
	
	public LotteryDTO call() throws Exception {
		LotteryDTO lotteryDTO = null;
		int runCount = 1;
		while (true) {
			lotteryDTO = FetchResult.getOpenCur(provider,openPeriods,sourceUrl,lotteryType, runCount);
    		
	    	if(lotteryDTO!=null && lotteryDTO.isBool()){
	    		logger.info("最新采集完成: "+openPeriods+ "-" + provider);   
               	return lotteryDTO;
    		}
	    	
	    	if(lotteryType == 4 || lotteryType == 5 ) {
		    	if(runCount>=maxRunCount2){
		    		return null;
		    		//throw new Exception("超过限定查询次数");
		    	}
	    	}else {
		    	if(runCount>=maxRunCount1){
		    		return null;
		    		//throw new Exception("超过限定查询次数");
		    	}
	    	}

    		runCount++;
    		
    		if("开彩网".equalsIgnoreCase(provider)) {
    			TimeUnit.SECONDS.sleep(3);
    		}else {
    			TimeUnit.SECONDS.sleep(2);
    		}
		}
	}
}
