package com.jeetx.controller.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.lottery.LotteryRulePlanItem;
import com.jeetx.bean.lottery.StationLotteryType;
import com.jeetx.bean.member.User;
import com.jeetx.bean.system.StationConfig;
import com.jeetx.common.constant.Globals;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.model.page.PageView;
import com.jeetx.common.model.page.QueryResult;
import com.jeetx.common.rabbitmq.RabbitMQClientUtil;
import com.jeetx.common.redis.JedisClient;
import com.jeetx.common.swagger.model.JsonResult;
import com.jeetx.common.swagger.model.lottery.LotteryHallVo;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemDTO;
import com.jeetx.common.swagger.model.lottery.LotteryPeriodsVo;
import com.jeetx.common.swagger.model.lottery.LotteryRoomVo;
import com.jeetx.common.swagger.model.lottery.LotteryRuleTypeVo;
import com.jeetx.common.swagger.model.lottery.LotteryRuleVo;
import com.jeetx.common.swagger.model.lottery.LotteryWaterRecordVo;
import com.jeetx.common.swagger.model.system.SystemConfigVo;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.lottery.LotteryHallService;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.lottery.LotteryPeriodsService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRulePlanItemService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.lottery.StationLotteryTypeService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.system.StationConfigService;
import com.jeetx.service.system.StationService;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.IpUtil;
import com.jeetx.util.JsonUtil;
import com.jeetx.util.LogUtil;
import com.jeetx.util.RandomUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin
@Controller
@RequestMapping("/api/lottery")
@Api(tags = "彩票信息服务接口(参数URLEncoder后提交)") //swagger分类标题注解
public class LotteryController {
	
    @Autowired JedisClient jedisClient;
	@Autowired UserService userService;
	@Autowired LotteryHallService lotteryHallService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired LotteryPeriodsService lotteryPeriodsService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired LotteryOrderService lotteryOrderService;
	@Autowired StationLotteryTypeService stationLotteryTypeService;
	@Autowired StationConfigService stationConfigService;
	@Autowired LotteryRulePlanItemService lotteryRulePlanItemService;
	@Autowired StationService stationService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	
	@Value("${developMode}")
	private Boolean developMode;
	
	@Value("${resServerLink}")
	private String resServerLink;
	
	@Value("${closeLotteryMessage}")
	private String closeLotteryMessage;
	
	@Value("${intoLotteryMessage}")
	private String intoLotteryMessage;
	
	@Value("${rabbitHostname}")
	private String rabbitHostname;
	
	@Value("${rabbitUsername}")
	private String rabbitUsername;
	
	@Value("${rabbitPassword}")
	private String rabbitPassword;
    
	/**获取房厅列表(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/getRoom2HallList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryHallVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取房厅列表(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "lottery_type", value = "彩票类型", required = true, dataType = "int")
    })
    public JsonResult getRoom2HallList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="lottery_type") Integer lotteryType,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="token") String token){
		String methodName = "获取房厅列表(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("lottery_type", lotteryType);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			List<LotteryHall> lotteryHallList = lotteryHallService.getLotteryHallList(lotteryType,stationId);
			
			String message = "成功";
			Map<String, Object> result = null;
			if(lotteryHallList!=null && lotteryHallList.size()>0) {
				List<LotteryHallVo> data = new ArrayList<LotteryHallVo>();
			 	for (LotteryHall object : lotteryHallList) {
			 		LotteryHallVo vo = new LotteryHallVo();
			 		vo.setId(object.getId());
			 		vo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
			 		vo.setIconImg(StringUtils.isNotBlank(object.getIconImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getIconImg():"");
			 		vo.setMinimum(object.getMinimum().toString());
			 		vo.setRuleRemarks(StringUtils.isNotBlank(object.getRuleRemarks())?object.getRuleRemarks():"");

			 		List<LotteryRoom> lotteryRoomList = lotteryRoomService.getLotteryRoomList(lotteryType, object.getId(),stationId);
			 		if(lotteryRoomList!=null && lotteryRoomList.size()>0) {
			 			List<LotteryRoomVo> rooms = new ArrayList<LotteryRoomVo>();
			 			for (LotteryRoom lotteryRoom : lotteryRoomList) {
			 				LotteryRoomVo vo1 = new LotteryRoomVo();
			 				
			 				vo1.setId(lotteryRoom.getId());
			 				vo1.setTitle(StringUtils.isNotBlank(lotteryRoom.getTitle())?lotteryRoom.getTitle():"");
			 				vo1.setHallTitle(lotteryRoom.getLotteryHall()!=null?lotteryRoom.getLotteryHall().getTitle():"");
			 				vo1.setIconImg(StringUtils.isNotBlank(lotteryRoom.getIconImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+lotteryRoom.getIconImg():"");
			 				vo1.setStatus(lotteryRoom.getStatus());
			 				vo1.setOnLineCount(RandomUtil.getRangeRandom(
			 						lotteryRoom.getOnLineCount()>50?lotteryRoom.getOnLineCount()-50:lotteryRoom.getOnLineCount(), lotteryRoom.getOnLineCount()+200));
			 				rooms.add(vo1);
						}
			 			vo.setRooms(rooms);
			 		}
			 		data.add(vo);
				}
			 	
			 	result = new HashMap<String, Object>();
				result.put("data", data);

			}else {
				throw new BusinessException(ApiUtil.getErrorCode("105"));
			}
			
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
    
    
    /**获取最近已开奖期数*/
    @ResponseBody
    @RequestMapping(value = "/currentFinishPeriods", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryPeriodsVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取最近已开奖期数")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    })
    public JsonResult currentFinishPeriods(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="sign") String sign){
   		String methodName = "获取最近已开奖期数";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("device", device);
			parasMap.put("sign", sign);
			
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
   			
  			String message = "";
  			
  			List<LotteryPeriods> lotteryPeriodsList = new ArrayList<LotteryPeriods>();
  			for (int i = 1; i <= 6; i++) {
  				LotteryPeriods lotteryPeriods = lotteryPeriodsService.currentFinishLotteryPeriods(i);
  				if(lotteryPeriods != null) {
  					lotteryPeriodsList.add(lotteryPeriods);
  				}
			}
  			
  			List<LotteryPeriodsVo> data = new ArrayList<LotteryPeriodsVo>();
   			if(lotteryPeriodsList != null && lotteryPeriodsList.size() >0) {
   				message = "成功";
				String pcStraightCombine = stationConfigService.getValueByName("pc_straight_combine", stationId);
			 	for (LotteryPeriods object : lotteryPeriodsList) {
			 		StationLotteryType stationLotteryType = stationLotteryTypeService.getStationLotteryTypeByLotteryType(object.getLotteryType().getId(),stationId);
			 		if(stationLotteryType !=null && stationLotteryType.getStatus() == 1) {
				 		LotteryPeriodsVo vo = new LotteryPeriodsVo();
				 		vo.setLotteryType(object.getLotteryType().getId());
				 		vo.setLotteryName(object.getLotteryType().getLotteryName());
				 		vo.setLotteryPeriods(object.getLotteryPeriods());
				 		vo.setStatus(object.getStatus());
				 		vo.setLotteryBeginTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", object.getLotteryBeginTime()));
				 		vo.setLotteryOpenTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", object.getLotteryOpenTime()));
				 		vo.setLotteryOpenContent(StringUtils.isNotBlank(object.getLotteryOpenContent())?object.getLotteryOpenContent():"");
				 		vo.setLotteryShowContent(StringUtils.isNotBlank(object.getLotteryShowContent())?object.getLotteryShowContent():"");
				 		vo.setChartData(ApiUtil.getChartData(object.getLotteryType().getId(),object));
				 		
						if((object.getLotteryType().getId() == 1 || object.getLotteryType().getId() == 2 || object.getLotteryType().getId() == 6) && StringUtils.isNotBlank(pcStraightCombine)
								&& StringUtils.isNotBlank(vo.getLotteryOpenContent())) {
							String[] numberArr = vo.getLotteryOpenContent().split("\\+");
							if(numberArr != null && numberArr.length == 3) {
								String numbers = ApiUtil.sortNumber(Integer.valueOf(numberArr[0]), Integer.valueOf(numberArr[1]), Integer.valueOf(numberArr[2]));
								if(Arrays.asList(pcStraightCombine.split(",")).contains(numbers) && !vo.getLotteryShowContent().contains("顺子")) {
									vo.setLotteryShowContent(vo.getLotteryShowContent().replaceAll("\\)", "、顺子)"));
								}
							}
						}
				 		data.add(vo);
			 		}
				}
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> result = new HashMap<String, Object>();
			result.put("data", data);

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}

    /**获取彩票历史开奖列表*/
    @ResponseBody
	@RequestMapping(value = "/periodsHistoryList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryPeriodsVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取彩票历史开奖列表")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "lottery_type", value = "彩票类型", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int")
    })
    public JsonResult periodsHistoryList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="lottery_type") Integer lotteryType, 
     		@RequestParam(value="date_begin",required=false) String dateBegin,
     		@RequestParam(value="date_end",required=false) String dateEnd,
    		@RequestParam(value="page") Integer page,
    		@RequestParam(value="limit") Integer limit){
		String methodName = "获取彩票历史开奖列表";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("lotteryType", lotteryType);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("device", device);
			parasMap.put("sign", sign);
			
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("lotteryOpenTime", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			/** 开始时间*/
			if(StringUtils.isNotBlank(dateBegin)){
				sb.append(" and o.lotteryOpenTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryStartDate(dateBegin));
			}
	
			/** 截止时间*/
			if(StringUtils.isNotBlank(dateEnd)) {
				sb.append(" and o.lotteryOpenTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryEndDate(dateEnd));
			}
			
			sb.append(" and o.status = ?");
			params.add(3);

			sb.append(" and o.lotteryType.id = ?");
			params.add(lotteryType);

			QueryResult<LotteryPeriods> qr = lotteryPeriodsService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<LotteryPeriodsVo> data = new ArrayList<LotteryPeriodsVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
				String pcStraightCombine = stationConfigService.getValueByName("pc_straight_combine", stationId);
			 	for (LotteryPeriods object : qr.getResultData()) {
			 		LotteryPeriodsVo vo = new LotteryPeriodsVo();
			 		vo.setLotteryType(object.getLotteryType().getId());
			 		vo.setLotteryName(object.getLotteryType().getLotteryName());
			 		vo.setLotteryPeriods(object.getLotteryPeriods());
			 		vo.setStatus(object.getStatus());
			 		vo.setLotteryBeginTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", object.getLotteryBeginTime()));
			 		vo.setLotteryOpenTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", object.getLotteryOpenTime()));
			 		vo.setLotteryOpenContent(StringUtils.isNotBlank(object.getLotteryOpenContent())?object.getLotteryOpenContent():"");
			 		vo.setLotteryShowContent(StringUtils.isNotBlank(object.getLotteryShowContent())?object.getLotteryShowContent():"");
			 		vo.setChartData(ApiUtil.getChartData(object.getLotteryType().getId(),object));
			 		
					if((object.getLotteryType().getId() == 1 || object.getLotteryType().getId() == 2 || object.getLotteryType().getId() == 6) 
							&& StringUtils.isNotBlank(pcStraightCombine)
							&& StringUtils.isNotBlank(vo.getLotteryOpenContent())) {
						String[] numberArr = vo.getLotteryOpenContent().split("\\+");
						if(numberArr != null && numberArr.length == 3) {
							String numbers = ApiUtil.sortNumber(Integer.valueOf(numberArr[0]), Integer.valueOf(numberArr[1]), Integer.valueOf(numberArr[2]));
							if(Arrays.asList(pcStraightCombine.split(",")).contains(numbers) && !vo.getLotteryShowContent().contains("顺子")) {
								vo.setLotteryShowContent(vo.getLotteryShowContent().replaceAll("\\)", "、顺子)"));
							}
						}
					}
					
			 		data.add(vo);
				}
			}
			
		 	result = new HashMap<String, Object>();
			result.put("page", page);
			result.put("limit", limit);
			result.put("count", qr.getResultCount());
			result.put("data", data);
			
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
    
    
	/**获取当前彩票期数(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/currentPeriods", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryPeriodsVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取当前彩票期数(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "room_id", value = "房间Id，remarks字段返回房间开启状态显示信息", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "lottery_type", value = "彩票类型", required = true, dataType = "int")
    })
    public JsonResult currentPeriods(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="lottery_type") Integer lotteryType,
    		@RequestParam(value="room_id") Integer roomId,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
		String methodName = "获取当前彩票期数(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("lottery_type", lotteryType);
			parasMap.put("room_id", roomId);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			String message = "成功";
			userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			LotteryPeriods lotteryPeriods = lotteryPeriodsService.findTopLotteryPeriods(lotteryType);
			
			LotteryRoom lotteryRoom = lotteryRoomService.find(roomId);
			if(lotteryRoom == null) {
				throw new BusinessException(ApiUtil.getErrorCode("139"));
			}
			
			if(lotteryRoom.getLotteryType().getId()!=lotteryType) {
				throw new BusinessException(ApiUtil.getErrorCode("140"));
			}
			
			Map<String, Object> result = null;
	 		LotteryPeriodsVo vo = new LotteryPeriodsVo();
   			if(lotteryPeriods != null ) {
		 		vo.setLotteryType(lotteryPeriods.getLotteryType().getId());
		 		vo.setLotteryName(lotteryPeriods.getLotteryType().getLotteryName());
		 		vo.setLotteryPeriods(lotteryPeriods.getLotteryPeriods());
		 		vo.setStatus(lotteryPeriods.getStatus());
		 		vo.setLotteryBeginTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryPeriods.getLotteryBeginTime()));
		 		vo.setLotteryOpenTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryPeriods.getLotteryOpenTime()));
		 		vo.setLotteryOpenContent(StringUtils.isNotBlank(lotteryPeriods.getLotteryOpenContent())?lotteryPeriods.getLotteryOpenContent():"");
		 		vo.setLotteryShowContent(StringUtils.isNotBlank(lotteryPeriods.getLotteryShowContent())?lotteryPeriods.getLotteryShowContent():"");
		 		
		 		String pcStraightCombine = stationConfigService.getValueByName("pc_straight_combine", stationId);
				if((lotteryPeriods.getLotteryType().getId() == 1 || lotteryPeriods.getLotteryType().getId() == 2 || lotteryPeriods.getLotteryType().getId() == 6) 
						&& StringUtils.isNotBlank(pcStraightCombine)
						&& StringUtils.isNotBlank(lotteryPeriods.getLotteryOpenContent()) ) {
					String[] numberArr = lotteryPeriods.getLotteryOpenContent().split("\\+");
					if(numberArr != null && numberArr.length == 3) {
						String numbers = ApiUtil.sortNumber(Integer.valueOf(numberArr[0]), Integer.valueOf(numberArr[1]), Integer.valueOf(numberArr[2]));
						if(Arrays.asList(pcStraightCombine.split(",")).contains(numbers) && !lotteryPeriods.getLotteryShowContent().contains("顺子")) {
							vo.setLotteryShowContent(vo.getLotteryShowContent().replaceAll("\\)", "、顺子)"));
						}
					}
				}
		 		
		 		String remarks = "彩票维护，非投注时段";
				if(lotteryRoom.getStatus()==1) {
					if(lotteryPeriods.getStatus()==1) {
						String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "总注限制",stationId);
						if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
							remarks = intoLotteryMessage.replace("[开奖期数]", lotteryPeriods.getLotteryPeriods()).replace("[最大值]", betRange.split("-")[1]).replace("[最小值]", betRange.split("-")[0]);
						}else {
							remarks = "【第[开奖期数]期】现在可以开始下注".replace("[开奖期数]", lotteryPeriods.getLotteryPeriods());
						}
					}else {
						remarks =  closeLotteryMessage.replace("[开奖期数]", lotteryPeriods.getLotteryPeriods());
					}
				}
				
			 	result = new HashMap<String, Object>();
				result.put("data", vo);
				result.put("roomId", lotteryRoom.getId());
				result.put("roomTitle", lotteryRoom.getTitle());
				result.put("lotteryType", lotteryRoom.getLotteryType().getId());
				result.put("lotteryName", lotteryRoom.getLotteryType().getLotteryName());
				result.put("hallId", lotteryRoom.getLotteryHall().getId());
				result.put("hallTitle", lotteryRoom.getLotteryHall().getTitle());
				result.put("ruleRemarks", StringUtils.isNotBlank(lotteryRoom.getLotteryHall().getRuleRemarks())?lotteryRoom.getLotteryHall().getRuleRemarks():"");
				result.put("remarks", remarks);
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
			
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
    
	/**获取彩票玩法赔率(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/lotteryRule", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryRuleTypeVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取彩票玩法赔率(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "hall_id", value = "彩票大厅Id", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "rule_types", value = "彩票玩法类型，同时查询多个用“;”隔开,值说明：1、北京28及加拿大28(1猜双面、3猜数字、2特殊玩法); 2、幸运飞艇及北京赛车(1猜双面、2猜号码、3龙虎斗、4猜庄闲、5猜冠亚、6冠亚和<猜双面>、7冠亚和<猜数字>、8冠亚和<猜区段>; 3、重庆时时彩(1猜双面、2猜数字、3猜和值、4龙虎斗; ", required = true, dataType = "string")
    })
    public JsonResult lotteryRule(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
      		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="hall_id") Integer hallId,
    		@RequestParam(value="rule_types") String ruleTypes,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
		String methodName = "获取彩票玩法赔率(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("rule_types", ruleTypes);
			parasMap.put("hall_id", hallId);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数

			userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			LotteryHall lotteryHall = lotteryHallService.find(hallId);
			if(lotteryHall == null || lotteryHall.getStatus()!=1 || lotteryHall.getStation().getId()!= stationId) {
				throw new BusinessException(ApiUtil.getErrorCode("124"));
			}
			
			String message = "成功";
			Map<String, Object> result = null;
			List<LotteryRuleTypeVo> ruleTypeVoList = new ArrayList<LotteryRuleTypeVo>();
			String[] ruleTypesArr = ((String)parasMap.get("rule_types")).split(";");
			for (int i = 0; i < ruleTypesArr.length; i++) {
				List<LotteryRuleVo> ruleVoList = new ArrayList<LotteryRuleVo>();
				
				List<LotteryRule> lotteryRulelist = lotteryRuleService.getLotteryRuleList(hallId, Integer.valueOf(ruleTypesArr[i]),stationId);
	   			if(lotteryRulelist != null && lotteryRulelist.size()>0) {
	   				for (LotteryRule lotteryRule : lotteryRulelist) {
	   					if(lotteryRule.getStatus()>=0) {
		   					LotteryRuleVo vo = new LotteryRuleVo();
		   					vo.setRuleId(lotteryRule.getId());
		   					vo.setRuleName(lotteryRule.getParamName());
		   					
		   					vo.setRuleOdds(lotteryRule.getParamValues());
		   					LotteryRulePlanItem lotteryRulePlanItem = lotteryRulePlanItemService.findLotteryRulePlanItem(stationId, lotteryRule.getLotteryHall().getId(), lotteryRule.getId(),new Date());
		   					if(lotteryRulePlanItem !=null && StringUtils.isNotBlank(lotteryRulePlanItem.getParamValues())) {
		   						vo.setRuleOdds(lotteryRulePlanItem.getParamValues());
		   					}
		   					
		   					vo.setRuleRegular(StringUtils.isNotBlank(lotteryRule.getRuleRegular())?lotteryRule.getRuleRegular():"");
		   					vo.setRuleType(lotteryRule.getRuleType());
		   					vo.setRemarks(lotteryRule.getRemarks());
		   					vo.setStatus(lotteryRule.getStatus());
		   					ruleVoList.add(vo);
	   					}
	   				}
	   				
	   				LotteryRuleTypeVo ruleTypeVo = new LotteryRuleTypeVo();
	   				ruleTypeVo.setRuleType(Integer.valueOf(ruleTypesArr[i]));
	   				ruleTypeVo.setRuleTypeName(ApiUtil.getRuleTypeName(lotteryHall.getLotteryType().getId(), Integer.valueOf(ruleTypesArr[i])));
	   				ruleTypeVo.setRules(ruleVoList);
	   				ruleTypeVoList.add(ruleTypeVo);
	   			}
			}
			
		 	result = new HashMap<String, Object>();
			result.put("data", ruleTypeVoList);
			result.put("hallTitle", lotteryHall.getTitle());
			result.put("ruleRemarks", StringUtils.isNotBlank(lotteryHall.getRuleRemarks())?lotteryHall.getRuleRemarks():"");

		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
    
	/**通过期号获取期数信息(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/getLotteryPeriods", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryPeriodsVo.class)})
    @ApiOperation(httpMethod = "GET", value = "通过期号获取期数信息(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "lottery_period", value = "彩票期数", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "lottery_type", value = "彩票类型", required = true, dataType = "int")
    })
    public JsonResult getLotteryPeriods(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
      		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="lottery_period") String lotteryPeriod, 
    		@RequestParam(value="lottery_type") Integer lotteryType,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
		String methodName = "通过期号获取期数信息(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("lottery_period", lotteryPeriod);
			parasMap.put("lottery_type", lotteryType);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			String message = "成功";
			userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryType, lotteryPeriod);
			
			Map<String, Object> result = null;
	 		LotteryPeriodsVo vo = new LotteryPeriodsVo();
   			if(lotteryPeriods != null ) {
		 		vo.setLotteryType(lotteryPeriods.getLotteryType().getId());
		 		vo.setLotteryName(lotteryPeriods.getLotteryType().getLotteryName());
		 		vo.setLotteryPeriods(lotteryPeriods.getLotteryPeriods());
		 		vo.setStatus(lotteryPeriods.getStatus());
		 		vo.setLotteryBeginTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryPeriods.getLotteryBeginTime()));
		 		vo.setLotteryOpenTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryPeriods.getLotteryOpenTime()));
		 		vo.setLotteryOpenContent(StringUtils.isNotBlank(lotteryPeriods.getLotteryOpenContent())?lotteryPeriods.getLotteryOpenContent():"");
		 		vo.setLotteryShowContent(StringUtils.isNotBlank(lotteryPeriods.getLotteryShowContent())?lotteryPeriods.getLotteryShowContent():"");
		 		
		 		String pcStraightCombine = stationConfigService.getValueByName("pc_straight_combine", stationId);
				if((lotteryPeriods.getLotteryType().getId() == 1 || lotteryPeriods.getLotteryType().getId() == 2  || lotteryPeriods.getLotteryType().getId() == 6) 
						&& StringUtils.isNotBlank(pcStraightCombine)
						&& StringUtils.isNotBlank(lotteryPeriods.getLotteryOpenContent())) {
					String[] numberArr = lotteryPeriods.getLotteryOpenContent().split("\\+");
					if(numberArr != null && numberArr.length == 3) {
						String numbers = ApiUtil.sortNumber(Integer.valueOf(numberArr[0]), Integer.valueOf(numberArr[1]), Integer.valueOf(numberArr[2]));
						if(Arrays.asList(pcStraightCombine.split(",")).contains(numbers) && !lotteryPeriods.getLotteryShowContent().contains("顺子")) {
							vo.setLotteryShowContent(vo.getLotteryShowContent().replaceAll("\\)", "、顺子)"));
						}
					}
				}
				
			 	result = new HashMap<String, Object>();
				result.put("data", vo);
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
			
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
    
    
    /**提交订单|玩家下注(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/submitOrder", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "提交订单|玩家下注(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "room_id", value = "房间ID", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "lottery_period", value = "彩票期数", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "order_code", value = "订单编号,格式：CP+yyyyMMddHHmmss+3位随机数字，如：", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "drop_money", value = "下注总额", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "orders_json", value = "订单json,格式： [{ruleId:1,betContent:\"大\",betMoney:\"100\"},{ruleId:5,betContent:\"大单\",betMoney:\"100\"}]", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult submitOrder(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="room_id") Integer roomId, 
    		@RequestParam(value="lottery_period") String lotteryPeriod, 
    		@RequestParam(value="order_code") String orderCode,
    		@RequestParam(value="drop_money") String dropMoney, 
    		@RequestParam(value="orders_json") String ordersJson, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "提交订单|玩家下注(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("room_id", roomId);
			parasMap.put("lottery_period", lotteryPeriod);
			parasMap.put("order_code", orderCode);
			parasMap.put("drop_money", dropMoney);
			parasMap.put("orders_json", ordersJson);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			BigDecimal allDropMoney = null;
			try {
				allDropMoney = new BigDecimal((String)parasMap.get("drop_money"));
			}catch (Exception e) {
				throw new BusinessException(ApiUtil.getErrorCode("125"));
			}
			
			List<LotteryOrderItemDTO> orderItemList = null;
			try {
				orderItemList = JsonUtil.toList((String)parasMap.get("orders_json"),LotteryOrderItemDTO.class);
			}catch (Exception e) {
				throw new BusinessException(ApiUtil.getErrorCode("126"));
			}

			lotteryOrderService.submitOrder(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),(String)parasMap.get("order_code"), roomId, lotteryPeriod, 
					allDropMoney, orderItemList,stationId,true,new Date(),true);
   			String message = "下注成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**创建队列(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/createMQQueue", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryPeriodsVo.class)})
    @ApiOperation(httpMethod = "GET", value = "创建队列(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "room_id", value = "房间Id，remarks字段返回房间开启状态显示信息", required = true, dataType = "int")
    })
    public JsonResult createMQQueue(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="room_id") Integer roomId,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
		String methodName = "创建队列(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("room_id", roomId);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			String message = "成功";
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			LotteryRoom lotteryRoom = lotteryRoomService.find(roomId);
			if(lotteryRoom == null) {
				throw new BusinessException(ApiUtil.getErrorCode("139"));
			}
			
			Map<String, Object> result = null;
			try {
				String exchangeName = Globals.RABBIT_EXCHANGE_ROOM.concat(lotteryRoom.getLotteryType().getId().toString()).concat("-").concat(lotteryRoom.getId().toString());
				String queueName = Globals.RABBIT_QUEUE_USER.concat(lotteryRoom.getId().toString()).concat("-").concat(user.getId().toString());
				
				RabbitMQClientUtil rabbitUtil = new RabbitMQClientUtil(rabbitHostname,rabbitUsername,rabbitPassword);
				rabbitUtil.bindQueue(exchangeName, queueName);
				
				result = new HashMap<String, Object>();
				result.put("userId", user.getId());
				result.put("username", user.getUsername());
				result.put("exchangeName", exchangeName);
				result.put("queueName", queueName);
				result.put("mqDomain", StringUtils.isNotBlank(user.getStation().getMqDomain())?user.getStation().getMqDomain():"");
			}catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(ApiUtil.getErrorCode("107"));
			}

		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
    
    /**取消订单(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/cancelOrder", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "取消订单(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "order_code", value = "订单编号", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult cancelOrder(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="order_code") String orderCode,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "取消订单(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("order_code", orderCode);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			lotteryOrderService.cancelOrder(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),(String)parasMap.get("order_code"),stationId);
   			String message = "取消成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    
    /**清空队列(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/clearMQQueue", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryPeriodsVo.class)})
    @ApiOperation(httpMethod = "GET", value = "清空队列(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "room_id", value = "房间Id，remarks字段返回房间开启状态显示信息", required = true, dataType = "int")
    })
    public JsonResult clearMQQueue(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="room_id") Integer roomId,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
		String methodName = "清空队列(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("room_id", roomId);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			LotteryRoom lotteryRoom = lotteryRoomService.find(roomId);
			if(lotteryRoom == null) {
				throw new BusinessException(ApiUtil.getErrorCode("139"));
			}
			
			String message = "成功";
			try {
				//String exchangeName = Globals.RABBIT_EXCHANGE_ROOM.concat(lotteryRoom.getLotteryType().getId().toString()).concat("-").concat(lotteryRoom.getId().toString());
				String queueName = Globals.RABBIT_QUEUE_USER.concat(lotteryRoom.getId().toString()).concat("-").concat(user.getId().toString());
				
				RabbitMQClientUtil rabbitUtil = new RabbitMQClientUtil(rabbitHostname,rabbitUsername,rabbitPassword);
				rabbitUtil.deleteQueue(queueName);
				
				message = "清空成功";
			}catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(ApiUtil.getErrorCode("107"));
			}

		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
    
    
    /**获取中奖信息(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/getWin", method={RequestMethod.POST, RequestMethod.GET})
 	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryWaterRecordVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "获取中奖信息(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  	})
    public JsonResult getWin(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
	  		@RequestParam(value="timestamp") String timestamp, 
	  		@RequestParam(value="version") String version, 
      		@RequestParam(value="device",required=false) Integer device,
	  		@RequestParam(value="sign") String sign, 
	  		@RequestParam(value="username") String username, 
	  		@RequestParam(value="token") String token){
 		String methodName = "获取中奖信息(需登录权限)";
 		try {
 			Map<String, Object> parasMap = new HashMap<String, Object>();
 			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
 			parasMap.put("username", username);
 			parasMap.put("token", token);
 			parasMap.put("device", device);
 			parasMap.put("timestamp", timestamp);
 			parasMap.put("version", version);
 			parasMap.put("sign", sign);
 			
 			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
 			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
 			
 			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
 			
 			String message = "成功";
 			String key = "win_".concat(user.getId().toString());
 			BigDecimal winMoney = new BigDecimal(0); 
 			
 			String winMoneyStr = jedisClient.get(key);
 			if(StringUtils.isNotBlank(winMoneyStr)) {
 				//System.out.println(winMoneyStr);
 				jedisClient.del(key);
 				winMoney = new BigDecimal(winMoneyStr);
 			}
 			
 			Map<String, Object> result = new HashMap<String, Object>();
	 		result.put("winMoney", winMoney);

 		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
 		}catch (BusinessException e) {
 			LogUtil.info(methodName.concat("-处理信息异常"), e);
 			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
 		}catch (Exception e) {
 			LogUtil.info(methodName.concat("-系统错误"), e);
 			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
 		}
    }
    
    /**获取房间聚合信息(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/lottery", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryRuleTypeVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取彩票玩法赔率(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "hall_id", value = "彩票大厅Id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "room_id", value = "房间Id，remarks字段返回房间开启状态显示信息", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "rule_types", value = "彩票玩法类型，同时查询多个用“;”隔开,值说明：1、北京28及加拿大28(1猜双面、3猜数字、2特殊玩法); 2、幸运飞艇及北京赛车(1猜双面、2猜号码、3龙虎斗、4猜庄闲、5猜冠亚、6冠亚和<猜双面>、7冠亚和<猜数字>、8冠亚和<猜区段>; 3、重庆时时彩(1猜双面、2猜数字、3猜和值、4龙虎斗; ", required = true, dataType = "string")
    })
    public JsonResult lottery(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
      		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="hall_id") Integer hallId,
    		@RequestParam(value="room_id") Integer roomId,
    		@RequestParam(value="rule_types") String ruleTypes,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token,
    		@RequestParam(value="page") Integer page,
    		@RequestParam(value="limit") Integer limit){
		String methodName = "获取房间聚合信息(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("rule_types", ruleTypes);
			parasMap.put("room_id", roomId);
			parasMap.put("hall_id", hallId);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数

			userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			LotteryHall lotteryHall = lotteryHallService.find(hallId);
			if(lotteryHall == null || lotteryHall.getStatus()!=1 || lotteryHall.getStation().getId()!= stationId) {
				throw new BusinessException(ApiUtil.getErrorCode("124"));
			}
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			//==========================1聚合currentPeriods==========================
			LotteryPeriods lotteryPeriods = lotteryPeriodsService.findTopLotteryPeriods(lotteryHall.getLotteryType().getId());
			if(lotteryPeriods == null ) {
				throw new BusinessException(ApiUtil.getErrorCode("129"));
			}
			
			LotteryRoom lotteryRoom = lotteryRoomService.find(roomId);
			if(lotteryRoom == null) {
				throw new BusinessException(ApiUtil.getErrorCode("139"));
			}
			
			if(lotteryRoom.getLotteryType().getId()!=lotteryHall.getLotteryType().getId()) {
				throw new BusinessException(ApiUtil.getErrorCode("140"));
			}
			
	 		LotteryPeriodsVo lotteryPeriodsVo = new LotteryPeriodsVo();
			lotteryPeriodsVo.setLotteryType(lotteryPeriods.getLotteryType().getId());
			lotteryPeriodsVo.setLotteryName(lotteryPeriods.getLotteryType().getLotteryName());
			lotteryPeriodsVo.setLotteryPeriods(lotteryPeriods.getLotteryPeriods());
			lotteryPeriodsVo.setStatus(lotteryPeriods.getStatus());
			lotteryPeriodsVo.setLotteryBeginTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryPeriods.getLotteryBeginTime()));
			lotteryPeriodsVo.setLotteryOpenTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryPeriods.getLotteryOpenTime()));
			lotteryPeriodsVo.setLotteryOpenContent(StringUtils.isNotBlank(lotteryPeriods.getLotteryOpenContent())?lotteryPeriods.getLotteryOpenContent():"");
			lotteryPeriodsVo.setLotteryShowContent(StringUtils.isNotBlank(lotteryPeriods.getLotteryShowContent())?lotteryPeriods.getLotteryShowContent():"");
	 		
	 		String pcStraightCombine = stationConfigService.getValueByName("pc_straight_combine", stationId);
			if((lotteryPeriods.getLotteryType().getId() == 1 || lotteryPeriods.getLotteryType().getId() == 2 || lotteryPeriods.getLotteryType().getId() == 6) 
					&& StringUtils.isNotBlank(pcStraightCombine)
					&& StringUtils.isNotBlank(lotteryPeriods.getLotteryOpenContent()) ) {
				String[] numberArr = lotteryPeriods.getLotteryOpenContent().split("\\+");
				if(numberArr != null && numberArr.length == 3) {
					String numbers = ApiUtil.sortNumber(Integer.valueOf(numberArr[0]), Integer.valueOf(numberArr[1]), Integer.valueOf(numberArr[2]));
					if(Arrays.asList(pcStraightCombine.split(",")).contains(numbers) && !lotteryPeriods.getLotteryShowContent().contains("顺子")) {
						lotteryPeriodsVo.setLotteryShowContent(lotteryPeriodsVo.getLotteryShowContent().replaceAll("\\)", "、顺子)"));
					}
				}
			}
	 		
	 		String remarks = "彩票维护，非投注时段";
			if(lotteryRoom.getStatus()==1) {
				if(lotteryPeriods.getStatus()==1) {
					String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "总注限制",stationId);
					if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
						remarks = intoLotteryMessage.replace("[开奖期数]", lotteryPeriods.getLotteryPeriods()).replace("[最大值]", betRange.split("-")[1]).replace("[最小值]", betRange.split("-")[0]);
					}else {
						remarks = "【第[开奖期数]期】现在可以开始下注".replace("[开奖期数]", lotteryPeriods.getLotteryPeriods());
					}
				}else {
					remarks =  closeLotteryMessage.replace("[开奖期数]", lotteryPeriods.getLotteryPeriods());
				}
			}
			
			HashMap<String, Object> currentPeriods = new HashMap<String, Object>();
			currentPeriods.put("data", lotteryPeriodsVo);
			currentPeriods.put("roomId", lotteryRoom.getId());
			currentPeriods.put("roomTitle", lotteryRoom.getTitle());
			currentPeriods.put("lotteryType", lotteryRoom.getLotteryType().getId());
			currentPeriods.put("lotteryName", lotteryRoom.getLotteryType().getLotteryName());
			currentPeriods.put("hallId", lotteryRoom.getLotteryHall().getId());
			currentPeriods.put("hallTitle", lotteryRoom.getLotteryHall().getTitle());
			currentPeriods.put("ruleRemarks", StringUtils.isNotBlank(lotteryRoom.getLotteryHall().getRuleRemarks())?lotteryRoom.getLotteryHall().getRuleRemarks():"");
			currentPeriods.put("remarks", remarks);
			
			result.put("currentPeriods", currentPeriods);

			//==========================2聚合periodsHistoryList==========================
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("lotteryOpenTime", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			sb.append(" and o.status = ?");
			params.add(3);

			sb.append(" and o.lotteryType.id = ?");
			params.add(lotteryHall.getLotteryType().getId());

			QueryResult<LotteryPeriods> qr = lotteryPeriodsService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			List<LotteryPeriodsVo> lotteryPeriodsVoData = new ArrayList<LotteryPeriodsVo>();
			if(qr.getResultCount()>0) {
				String pcStraightCombineTemp = stationConfigService.getValueByName("pc_straight_combine", stationId);
			 	for (LotteryPeriods object : qr.getResultData()) {
			 		lotteryPeriodsVo = new LotteryPeriodsVo();
			 		lotteryPeriodsVo.setLotteryType(object.getLotteryType().getId());
			 		lotteryPeriodsVo.setLotteryName(object.getLotteryType().getLotteryName());
			 		lotteryPeriodsVo.setLotteryPeriods(object.getLotteryPeriods());
			 		lotteryPeriodsVo.setStatus(object.getStatus());
			 		lotteryPeriodsVo.setLotteryBeginTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", object.getLotteryBeginTime()));
			 		lotteryPeriodsVo.setLotteryOpenTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", object.getLotteryOpenTime()));
			 		lotteryPeriodsVo.setLotteryOpenContent(StringUtils.isNotBlank(object.getLotteryOpenContent())?object.getLotteryOpenContent():"");
			 		lotteryPeriodsVo.setLotteryShowContent(StringUtils.isNotBlank(object.getLotteryShowContent())?object.getLotteryShowContent():"");
			 		lotteryPeriodsVo.setChartData(ApiUtil.getChartData(object.getLotteryType().getId(),object));
			 		
					if((object.getLotteryType().getId() == 1 || object.getLotteryType().getId() == 2 || object.getLotteryType().getId() == 6) 
							&& StringUtils.isNotBlank(pcStraightCombineTemp)
							&& StringUtils.isNotBlank(lotteryPeriodsVo.getLotteryOpenContent())) {
						String[] numberArr = lotteryPeriodsVo.getLotteryOpenContent().split("\\+");
						if(numberArr != null && numberArr.length == 3) {
							String numbers = ApiUtil.sortNumber(Integer.valueOf(numberArr[0]), Integer.valueOf(numberArr[1]), Integer.valueOf(numberArr[2]));
							if(Arrays.asList(pcStraightCombineTemp.split(",")).contains(numbers) && !lotteryPeriodsVo.getLotteryShowContent().contains("顺子")) {
								lotteryPeriodsVo.setLotteryShowContent(lotteryPeriodsVo.getLotteryShowContent().replaceAll("\\)", "、顺子)"));
							}
						}
					}
					
					lotteryPeriodsVoData.add(lotteryPeriodsVo);
				}
			}
			
			HashMap<String, Object> periodsHistoryList = new HashMap<String, Object>();
			periodsHistoryList.put("page", page);
			periodsHistoryList.put("limit", limit);
			periodsHistoryList.put("count", qr.getResultCount());
			periodsHistoryList.put("data", lotteryPeriodsVoData);
			
			result.put("periodsHistoryList", periodsHistoryList);
			
			
			//==========================3聚合systemConfig==========================
			List<StationConfig> configList = stationConfigService.getListByStationId(stationId);
   			List<SystemConfigVo> systemConfigVoData = new ArrayList<SystemConfigVo>();
   			if(configList != null && configList.size() >0) {
			 	for (StationConfig object : configList) {
			 		SystemConfigVo vo = new SystemConfigVo();
			 		vo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
			 		vo.setName(StringUtils.isNotBlank(object.getName())?object.getName():"");
			 		vo.setValue(StringUtils.isNotBlank(object.getValue())?object.getValue():"");
			 		systemConfigVoData.add(vo);
				}
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> systemConfig = new HashMap<String, Object>();
   			systemConfig.put("data", systemConfigVoData);
			
   			result.put("systemConfig", systemConfig);
			
			
   			//==========================4聚合lotteryRule==========================
			List<LotteryRuleTypeVo> ruleTypeVoList = new ArrayList<LotteryRuleTypeVo>();
			String[] ruleTypesArr = ((String)parasMap.get("rule_types")).split(";");
			for (int i = 0; i < ruleTypesArr.length; i++) {
				List<LotteryRuleVo> ruleVoList = new ArrayList<LotteryRuleVo>();
				
				List<LotteryRule> lotteryRulelist = lotteryRuleService.getLotteryRuleList(hallId, Integer.valueOf(ruleTypesArr[i]),stationId);
	   			if(lotteryRulelist != null && lotteryRulelist.size()>0) {
	   				for (LotteryRule lotteryRule : lotteryRulelist) {
	   					if(lotteryRule.getStatus()>=0) {
		   					LotteryRuleVo vo = new LotteryRuleVo();
		   					vo.setRuleId(lotteryRule.getId());
		   					vo.setRuleName(lotteryRule.getParamName());
		   					
		   					vo.setRuleOdds(lotteryRule.getParamValues());
		   					LotteryRulePlanItem lotteryRulePlanItem = lotteryRulePlanItemService.findLotteryRulePlanItem(stationId, lotteryRule.getLotteryHall().getId(), lotteryRule.getId(),new Date());
		   					if(lotteryRulePlanItem !=null && StringUtils.isNotBlank(lotteryRulePlanItem.getParamValues())) {
		   						vo.setRuleOdds(lotteryRulePlanItem.getParamValues());
		   					}
		   					
		   					vo.setRuleRegular(StringUtils.isNotBlank(lotteryRule.getRuleRegular())?lotteryRule.getRuleRegular():"");
		   					vo.setRuleType(lotteryRule.getRuleType());
		   					vo.setRemarks(lotteryRule.getRemarks());
		   					vo.setStatus(lotteryRule.getStatus());
		   					ruleVoList.add(vo);
	   					}
	   				}
	   				
	   				LotteryRuleTypeVo ruleTypeVo = new LotteryRuleTypeVo();
	   				ruleTypeVo.setRuleType(Integer.valueOf(ruleTypesArr[i]));
	   				ruleTypeVo.setRuleTypeName(ApiUtil.getRuleTypeName(lotteryHall.getLotteryType().getId(), Integer.valueOf(ruleTypesArr[i])));
	   				ruleTypeVo.setRules(ruleVoList);
	   				ruleTypeVoList.add(ruleTypeVo);
	   			}
			}
			
			HashMap<String, Object> lotteryRule = new HashMap<String, Object>();
			lotteryRule.put("data", ruleTypeVoList);
			lotteryRule.put("hallTitle", lotteryHall.getTitle());
			lotteryRule.put("ruleRemarks", StringUtils.isNotBlank(lotteryHall.getRuleRemarks())?lotteryHall.getRuleRemarks():"");
			
 			result.put("lotteryRule", lotteryRule);
 			
 			//==========================5聚合userInfo==========================
			User user = userService.getUserInfo((String)parasMap.get("username"), token,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
			
			BigDecimal profitToday = new BigDecimal(0);//今日流水
   			LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", new Date()), user.getId());
   			if(lotteryDailyOrderTotal != null ) {
   				profitToday = lotteryDailyOrderTotal.getBetMoney();
   			}

   			Map<String, Object> userInfoMap = new HashMap<String, Object>();
   			userInfoMap = ApiUtil.toUserMap(user,ApiUtil.getResServerLink(resServerLink, user.getStation().getImageDomain()));
   			userInfoMap.put("profitToday", profitToday);
			
   			result.put("userInfo", userInfoMap);
   			
   			
   			//==========================6聚合createMQQueue==========================
			String exchangeName = Globals.RABBIT_EXCHANGE_ROOM.concat(lotteryRoom.getLotteryType().getId().toString()).concat("-").concat(lotteryRoom.getId().toString());
			String queueName = Globals.RABBIT_QUEUE_USER.concat(lotteryRoom.getId().toString()).concat("-").concat(user.getId().toString());
			
			RabbitMQClientUtil rabbitUtil = new RabbitMQClientUtil(rabbitHostname,rabbitUsername,rabbitPassword);
			rabbitUtil.bindQueue(exchangeName, queueName);
			
			HashMap<String, Object> createMQQueue = new HashMap<String, Object>();
			createMQQueue.put("userId", user.getId());
			createMQQueue.put("username", user.getUsername());
			createMQQueue.put("exchangeName", exchangeName);
			createMQQueue.put("queueName", queueName);
			createMQQueue.put("mqDomain", StringUtils.isNotBlank(user.getStation().getMqDomain())?user.getStation().getMqDomain():"");
			
			result.put("createMQQueue", createMQQueue);

   			String message = "成功";
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
}
