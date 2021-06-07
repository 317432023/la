package com.jeetx.controller.api;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.member.User;
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
import com.jeetx.service.lottery.LotteryHallService;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.lottery.LotteryPeriodsService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.member.UserService;
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
@RequestMapping("/api/cloud")
@Api(tags = "外部公共服务接口(参数URLEncoder后提交)") //swagger分类标题注解
public class CloudController {
	
	private static final String SECRETKEY = "AF241F8CA6E2AC7138F50D713851432F";
	@Autowired LotteryPeriodsService lotteryPeriodsService;
	
	@Value("${developMode}")
	private Boolean developMode;

    /**获取彩票历史开奖列表*/
    @ResponseBody
	@RequestMapping(value = "/periodsHistoryList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryPeriodsVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取彩票历史开奖列表")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "lottery_type", value = "彩票类型", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int")
    })
    public JsonResult periodsHistoryList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="lottery_type") Integer lotteryType, 
     		@RequestParam(value="date_begin",required=false) String dateBegin,
     		@RequestParam(value="date_end",required=false) String dateEnd,
    		@RequestParam(value="page") Integer page,
    		@RequestParam(value="limit") Integer limit){
		String methodName = "获取彩票历史开奖列表";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("lotteryType", lotteryType);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("sign", sign);
			parasMap = ApiUtil.checkParameter(parasMap,SECRETKEY);//验证参数
			
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
    
    
	/**获取当前彩票期数*/
    @ResponseBody
	@RequestMapping(value = "/currentPeriods", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryPeriodsVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取当前彩票期数")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "lottery_type", value = "彩票类型", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "periods", value = "彩票期数", required = false, dataType = "string")
    })
    public JsonResult currentPeriods(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="periods",required=false) String periods, 
    		@RequestParam(value="lottery_type") Integer lotteryType){
		String methodName = "获取当前彩票期数";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("lottery_type", lotteryType);
			parasMap.put("timestamp", timestamp);
			parasMap.put("sign", sign);
			parasMap = ApiUtil.checkParameter(parasMap,SECRETKEY);//验证参数
			
			String message = "成功";
			LotteryPeriods lotteryPeriods = null;
			if(StringUtils.isNotBlank(periods)) {
				lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryType, periods);
			}else {
				lotteryPeriods = lotteryPeriodsService.findTopLotteryPeriods(lotteryType);
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
  
}
