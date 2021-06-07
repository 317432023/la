package com.jeetx.timer.lotteryTask;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jeetx.timer.lotteryTask.FetchCollector.AscxwCollector;
import com.jeetx.timer.lotteryTask.FetchCollector.CxwCollector;
import com.jeetx.timer.lotteryTask.FetchCollector.DpcCollector;
import com.jeetx.timer.lotteryTask.FetchCollector.KcwCollector;
import com.jeetx.timer.lotteryTask.FetchCollector.Pc268Collector;
import com.jeetx.timer.lotteryTask.FetchCollector.Pc28amCollector;
import com.jeetx.timer.lotteryTask.FetchCollector.T28Collector;
import com.jeetx.util.LogUtil;

public class FetchResult {
	public static Logger logger = Logger.getLogger(FetchResult.class);
	
	/**
	 * @param lotteryType 游戏类型 
	 * @param dataSource 开奖源名称
	 * @param section 期数
	 * @param openTime 开奖时间
	 * @param openCode 开奖号码 
	 * @return 
	 */
	public static LotteryDTO createLotteryDTO(Integer lotteryType,String dataSource,String section,String openTime,String openCode) throws Exception{
		LotteryDTO lotteryDTO = new LotteryDTO();
		lotteryDTO.setBool(false);
		
		String openContent = null;
		Integer openTotal = null;
		String groupName = null;
		if(StringUtils.isNotBlank(openCode) && openCode.contains(",")) {
			String[] number = openCode.split(",");
			switch (lotteryType) {
			case 1:
			case 2:
			case 6:
				int num1 = Integer.valueOf(number[0]);
				int num2 = Integer.valueOf(number[1]);
				int num3 = Integer.valueOf(number[2]);
				openContent =  String.format("%02d", num1) + "+" + String.format("%02d", num2) + "+" + String.format("%02d", num3);
				openTotal = Integer.valueOf(num1) + Integer.valueOf(num2) + Integer.valueOf(num3);
				groupName = FetchResult.getGroupName(num1, num2, num3);
				break;
			case 3:
			case 4:
				num1 = Integer.valueOf(number[0]);
				num2 = Integer.valueOf(number[1]);
				num3 = Integer.valueOf(number[2]);
				int num4 = Integer.valueOf(number[3]);
				int num5 = Integer.valueOf(number[4]);
				int num6 = Integer.valueOf(number[5]);
				int num7 = Integer.valueOf(number[6]);
				int num8 = Integer.valueOf(number[7]);
				int num9 = Integer.valueOf(number[8]);
				int num10 = Integer.valueOf(number[9]);
				openContent = num1  + "+" + num2  + "+" + num3  + "+" + num4  + "+" + num5  + "+" + num6  + "+" + num7  + "+" + num8  + "+" + num9  + "+" + num10;
				openTotal = num1 + num2 + num3 + num4 + num5 + num6 + num7 + num8 + num9 + num10;
				break;
			case 5:
				num1 = Integer.valueOf(number[0]);
				num2 = Integer.valueOf(number[1]);
				num3 = Integer.valueOf(number[2]);
				num4 = Integer.valueOf(number[3]);
				num5 = Integer.valueOf(number[4]);
				
				openContent =  String.format("%01d", num1) + "+" + String.format("%01d", num2) + "+" + String.format("%01d", num3) + "+" + String.format("%01d", num4) + "+" + String.format("%01d", num5);
				openTotal = Integer.valueOf(num1) + Integer.valueOf(num2) + Integer.valueOf(num3) + Integer.valueOf(num4) + Integer.valueOf(num5);
				groupName = null;
				break;
			}
		}
		
		lotteryDTO.setDataSource(dataSource);
		lotteryDTO.setLotteryType(lotteryType);
		lotteryDTO.setOpenContent(openContent);
		lotteryDTO.setOpenTime(openTime);
		lotteryDTO.setPeriod(section);
		lotteryDTO.setLotteryNumber(openTotal);
		lotteryDTO.setGroupName(groupName);
		
		return lotteryDTO;
	}
	
	/**
	 * 获取开奖结果
	 * @param dataSource 开奖源名称
	 * @param sourceUrl 开奖源主域名
	 * @param openPeriods 开奖期数
	 * @param lotteryType 游戏类型
	 * @param runCount 当前期采集次数
	 * @return 
	 */
	public static LotteryDTO getOpenCur(String dataSource,String openPeriods,String sourceUrl,Integer lotteryType,int runCount) throws Exception{
		LotteryDTO lotteryDTO = new LotteryDTO();
		lotteryDTO.setBool(false);
		
		String gameName = null;
		Integer type = null;//源上游戏类型
		long startTime = System.currentTimeMillis();  
		try {
			if("PC28AM".equalsIgnoreCase(dataSource)) {
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					type = 1;
					break;
				case 2:
					gameName = "加拿大28";
					type = 2;
					break;
				case 3:
					gameName = "幸运飞艇";
					type = 6;
					break;
				case 4:
					gameName = "北京赛车";
					type = 5;
					break;
				case 5:
					gameName = "重庆时时彩";
					type = 4;
					break;
				}
				lotteryDTO = Pc28amCollector.getOpenCur(type,lotteryType,sourceUrl,dataSource);
				
			}else if("开彩网".equalsIgnoreCase(dataSource)){
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					sourceUrl = "http://ho.apiplus.net/newly.do?token=tc5b5259f3f127cf0k&code=bjkl8&format=json";
					break;
				case 2:
					gameName = "加拿大28";
					sourceUrl = "http://ho.apiplus.net/newly.do?token=tc5b5259f3f127cf0k&code=cakeno&format=json";
					break;
				case 3:
					gameName = "幸运飞艇";
					sourceUrl = "http://ho.apiplus.net/newly.do?token=tc5b5259f3f127cf0k&code=mlaft&format=json";
					break;
				case 4:
					gameName = "北京赛车";
					sourceUrl = "http://ho.apiplus.net/newly.do?token=tc5b5259f3f127cf0k&code=bjpk10&format=json";
					break;
				case 5:
					gameName = "重庆时时彩";
					sourceUrl = "http://ho.apiplus.net/newly.do?token=tc5b5259f3f127cf0k&code=cqssc&format=json";
					break;
				}
				
				type = lotteryType;
				lotteryDTO = KcwCollector.getOpenCur(type,lotteryType,sourceUrl,dataSource);
				
			}else if("PC268".equalsIgnoreCase(dataSource)){
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					sourceUrl = "http://www.pc268.com/api/data!openHistory?token=web1275259f3fcf0k5259f3f&type=2&rows=5&sort=desc";
					break;
				case 2:
					gameName = "加拿大28";
					sourceUrl = "http://www.pc268.com/api/data!openHistory?token=web1275259f3fcf0k5259f3f&type=3&rows=5&sort=desc";
					break;
				case 3:
					gameName = "幸运飞艇";
					break;
				case 4:
					gameName = "北京赛车";
					break;
				case 5:
					gameName = "重庆时时彩";
					sourceUrl = "http://www.pc268.com/api/data!openHistory?token=web1275259f3fcf0k5259f3f&type=1&rows=5&sort=desc";
					break;
				}
				
				type = lotteryType;
				lotteryDTO = Pc268Collector.getOpenCur(type,lotteryType,sourceUrl,dataSource);
				
			}else if("彩讯网".equalsIgnoreCase(dataSource)){
				sourceUrl = StringUtils.isNotBlank(sourceUrl)?sourceUrl:"http://www.pc69999.com";
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					sourceUrl = sourceUrl+"/pc/index/getLottoDetail?lottoName=pcbj&page=1&showpage=10";
					break;
				case 2:
					gameName = "加拿大28";
					sourceUrl = sourceUrl+"/pc/index/getLottoDetail?lottoName=pcjnd&page=1&showpage=10";
					break;
				case 3:
					gameName = "幸运飞艇";
					sourceUrl = sourceUrl+"/pc/index/getLottoDetail?lottoName=xyft&page=1&showpage=10";
					break;
				case 4:
					gameName = "北京赛车";
					sourceUrl = sourceUrl+"/pc/index/getLottoDetail?lottoName=bjpk&page=1&showpage=10";
					break;
				case 5:
					gameName = "重庆时时彩";
					sourceUrl = sourceUrl+"//pc/index/getLottoDetail?lottoName=cqssc&page=1&showpage=10";
					break;
				}
				
				type = lotteryType;
				lotteryDTO = CxwCollector.getOpenCur(type,lotteryType,sourceUrl,dataSource);
			}else if("28T".equalsIgnoreCase(dataSource)){
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					type = 1;
					break;
				case 2:
					gameName = "加拿大28";
					type = 2;
					break;
				case 3:
					gameName = "幸运飞艇";
					type = 5;
					break;
				case 4:
					gameName = "北京赛车";
					type = 3;
					break;
				case 5:
					gameName = "重庆时时彩";
					type = 4;
					break;
				}
				lotteryDTO = T28Collector.getOpenCur(type,lotteryType,sourceUrl,dataSource);
			}else if("ASCXW".equalsIgnoreCase(dataSource)){
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					type = 1;
					break;
				case 2:
					gameName = "加拿大28";
					type = 2;
					break;
				case 3:
					gameName = "幸运飞艇";
					type = 6;
					break;
				case 4:
					gameName = "北京赛车";
					type = 5;
					break;
				case 5:
					gameName = "重庆时时彩";
					type = 4;
					break;
				}
				lotteryDTO = AscxwCollector.getOpenCur(type,lotteryType,sourceUrl,dataSource);
			}else if("处理中心".equalsIgnoreCase(dataSource)){
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					type = 1;
					break;
				case 2:
					gameName = "加拿大28";
					type = 2;
					break;
				case 3:
					gameName = "幸运飞艇";
					type = 3;
					break;
				case 4:
					gameName = "北京赛车";
					type = 4;
					break;
				case 5:
					gameName = "重庆时时彩";
					type = 5;
					break;
				case 6:
					gameName = "新西兰28";
					type = 8;
					break;
				}
				lotteryDTO = DpcCollector.getOpenCur(type,lotteryType,sourceUrl,dataSource);
			}
			
			long endTime =System.currentTimeMillis()-startTime; 
			//LogUtil.info(gameName+"-执行["+dataSource+"]服务获取数据, 耗时：" + endTime + ", 第 "+runCount+" 次抓取结果：第" + openPeriods+"/"+lotteryDTO.getPeriod() + "期 , 开奖时间：" +lotteryDTO.getOpenTime()+ ", 开奖内容：" + lotteryDTO.getOpenContent());
	
			if(StringUtils.isNotBlank(lotteryDTO.getOpenContent()) && StringUtils.isBlank(openPeriods)){
				LogUtil.info(gameName+"-执行["+dataSource+"]服务获取数据, 耗时：" + endTime + ", 第 "+runCount+" 次抓取结果：第" + openPeriods+"/"+lotteryDTO.getPeriod() + "期 , 开奖时间：" +lotteryDTO.getOpenTime()+ ", 开奖内容：" + lotteryDTO.getOpenContent());
				lotteryDTO.setBool(true);
			}else if(StringUtils.isNotBlank(lotteryDTO.getOpenContent()) && openPeriods.equalsIgnoreCase(lotteryDTO.getPeriod())){
				LogUtil.info(gameName+"-执行["+dataSource+"]服务获取数据, 耗时：" + endTime + ", 第 "+runCount+" 次抓取结果：第" + openPeriods+"/"+lotteryDTO.getPeriod() + "期 , 开奖时间：" +lotteryDTO.getOpenTime()+ ", 开奖内容：" + lotteryDTO.getOpenContent());
				lotteryDTO.setBool(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.info(gameName+"执行"+dataSource+"时异常抛出："+e.getMessage());
		}	
		return lotteryDTO;
	}
	
	/**
	 * 获取开奖结果
	 * @param dataSource 开奖源名称
	 * @param sourceUrl 开奖源主域名
	 * @param lotteryType 游戏类型
	 * @return 
	 */
	public static List<LotteryDTO> getOpenHis(Integer lotteryType,String dataSource,String sourceUrl) throws Exception{
		List<LotteryDTO> historyList = null;
		
		String gameName = null;
		Integer type = null;
		try {
			if("PC28AM".equalsIgnoreCase(dataSource)) {
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					type = 1;
					break;
				case 2:
					gameName = "加拿大28";
					type = 2;
					break;
				case 3:
					gameName = "幸运飞艇";
					type = 6;
					break;
				case 4:
					gameName = "北京赛车";
					type = 5;
					break;
				case 5:
					gameName = "重庆时时彩";
					type = 4;
					break;
				}
				historyList = Pc28amCollector.getOpenHis(type,lotteryType,sourceUrl,dataSource);
			}else if("ASCXW".equalsIgnoreCase(dataSource)){
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					type = 1;
					break;
				case 2:
					gameName = "加拿大28";
					type = 2;
					break;
				case 3:
					gameName = "幸运飞艇";
					type = 6;
					break;
				case 4:
					gameName = "北京赛车";
					type = 5;
					break;
				case 5:
					gameName = "重庆时时彩";
					type = 4;
					break;
				}
				historyList = AscxwCollector.getOpenHis(type,lotteryType,sourceUrl,dataSource);
			}else if("PC268".equalsIgnoreCase(dataSource)){
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					sourceUrl = "http://www.pc268.com/api/data!openHistory?token=web1275259f3fcf0k5259f3f&type=2&rows=10&sort=desc";
					break;
				case 2:
					gameName = "加拿大28";
					sourceUrl = "http://www.pc268.com/api/data!openHistory?token=web1275259f3fcf0k5259f3f&type=3&rows=10&sort=desc";
					break;
				case 3:
					gameName = "幸运飞艇";
					break;
				case 4:
					gameName = "北京赛车";
					break;
				case 5:
					gameName = "重庆时时彩";
					sourceUrl = "http://www.pc268.com/api/data!openHistory?token=web1275259f3fcf0k5259f3f&type=1&rows=10&sort=desc";
					break;
				}
				type = lotteryType;
				historyList = Pc268Collector.getOpenHis(type,lotteryType,sourceUrl,dataSource);
			}else if("处理中心".equalsIgnoreCase(dataSource)){
				switch (lotteryType) {
				case 1:
					gameName = "幸运28";
					type = 1;
					break;
				case 2:
					gameName = "加拿大28";
					type = 2;
					break;
				case 3:
					gameName = "幸运飞艇";
					type = 3;
					break;
				case 4:
					gameName = "北京赛车";
					type = 4;
					break;
				case 5:
					gameName = "重庆时时彩";
					type = 5;
					break;
				case 6:
					gameName = "新西兰28";
					type = 8;
					break;
				}
				historyList = DpcCollector.getOpenHis(type,lotteryType,sourceUrl,dataSource);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.info(gameName+"执行"+dataSource+"-获取历史记录时异常抛出："+e.getMessage());
		}	
		return historyList;
	}
	
	public static String getGroupName(Integer a,Integer b,Integer c) throws Exception{
		String groupName = null;
		if (a < b) {
			int t = a;
			a = b;
			b = t;
		}
		if (a < c) {
			int t = a;
			a = c;
			c = t;
		}
		if (b < c) {
			int t = b;
			b = c;
			c = t;
		}
		//System.out.println(a+""+b+""+c);
	    if(a==b && b==c && a==c) {
	    	groupName = "豹子";
	    }else if(a==b || b==c || a==c) {
	    	groupName = "对子";
	    }else if(a>0 && b> 0 && c >0 && a-b==1 && b-c==1) {
	    	groupName = "顺子";
	    }
		
		return groupName;
	}
	
	public static String sortNumber(Integer a,Integer b,Integer c) throws Exception{
		if (a < b) {
			int t = a;
			a = b;
			b = t;
		}
		if (a < c) {
			int t = a;
			a = c;
			c = t;
		}
		if (b < c) {
			int t = b;
			b = c;
			c = t;
		}
		
		return a+""+b+""+c;
	}

	public static void main(String[] args) throws Exception {
//		FetchResult.getOpenCur("PC28AM",null,"https://28pc.cc",1, 1);
//		FetchResult.getOpenCur("PC28AM",null,"https://28pc.cc",2, 1);
//		FetchResult.getOpenCur("PC28AM",null,"https://28pc.cc",3, 1);
//		FetchResult.getOpenCur("PC28AM",null,"https://28pc.cc",4, 1);
//		FetchResult.getOpenCur("PC28AM",null,"https://28pc.cc",5, 1);
//		System.out.println("");
		
//		FetchResult.getOpenCur("28T",null,"https://www28yl.com",1, 1);
//		FetchResult.getOpenCur("28T",null,"https://www28yl.com",2, 1);
//		FetchResult.getOpenCur("28T",null,"https://www28yl.com",3, 1);
//		FetchResult.getOpenCur("28T",null,"https://www28yl.com",4, 1);
//		FetchResult.getOpenCur("28T",null,"https://www28yl.com",5, 1);
//		System.out.println("");
//		
//		FetchResult.getOpenCur("开彩网",null,"",1, 1);
//		FetchResult.getOpenCur("开彩网",null,"",2, 1);
//		FetchResult.getOpenCur("开彩网",null,"",3, 1);
//		FetchResult.getOpenCur("开彩网",null,"",4, 1);
//		FetchResult.getOpenCur("开彩网",null,"",5, 1);
//		System.out.println("");
//	
//		FetchResult.getOpenCur("PC268",null,"",1, 1);
//		FetchResult.getOpenCur("PC268",null,"",2, 1);
//		FetchResult.getOpenCur("PC268",null,"",3, 1);
//		FetchResult.getOpenCur("PC268",null,"",4, 1);
//		FetchResult.getOpenCur("PC268",null,"",5, 1);
//		System.out.println("");
//		
//		FetchResult.getOpenCur("彩讯网",null,"http://www.pc69999.com/",1, 1);
//		FetchResult.getOpenCur("彩讯网",null,"http://www.pc69999.com/",2, 1);
//		FetchResult.getOpenCur("彩讯网",null,"http://www.pc69999.com/",3, 1);
//		FetchResult.getOpenCur("彩讯网",null,"http://www.pc69999.com/",4, 1);
//		FetchResult.getOpenCur("彩讯网",null,"http://www.pc69999.com/",5, 1);
//		System.out.println("");
//		
//		FetchResult.getOpenCur("ASCXW",null,"https://ascxw.com",1, 1);
//		FetchResult.getOpenCur("ASCXW",null,"https://ascxw.com",2, 1);
//		FetchResult.getOpenCur("ASCXW",null,"https://ascxw.com",3, 1);
//		FetchResult.getOpenCur("ASCXW",null,"https://ascxw.com",4, 1);
//		FetchResult.getOpenCur("ASCXW",null,"https://ascxw.com",5, 1);
		
//		FetchResult.getOpenCur("处理中心",null,null,2, 1);
		
		List<LotteryDTO> list = null;
//		list = FetchResult.getOpenHis(5,"PC28AM","https://28pc.cc");
//		for (LotteryDTO lotteryDTO : list) {
//			System.out.println(lotteryDTO.getPeriod() +"-"+lotteryDTO.getOpenContent());
//		}
		System.out.println("----------------------");
		list = FetchResult.getOpenHis(2,"ASCXW","https://ascxw.com");
		for (LotteryDTO lotteryDTO : list) {
			System.out.println(lotteryDTO.getPeriod() +"-"+lotteryDTO.getOpenContent());
		}
//		System.out.println("----------------------");
//		list = FetchResult.getOpenHis(2,"PC268","");
//		for (LotteryDTO lotteryDTO : list) {
//			System.out.println(lotteryDTO.getPeriod() +"-"+lotteryDTO.getOpenContent());
//		}
////		System.out.println("----------------------");
//		list = FetchResult.getOpenHis(5,"处理中心","");
//		for (LotteryDTO lotteryDTO : list) {
//			System.out.println(lotteryDTO.getPeriod() +"-"+lotteryDTO.getOpenContent());
//		}
//		System.out.println(getGroupName(3,1,2));
//		System.out.println(getGroupName(5,6,4));
//		System.out.println(getGroupName(0,1,2));
//		System.out.println(getGroupName(0,8,9));
//		System.out.println(getGroupName(0,1,9));
	}
}
