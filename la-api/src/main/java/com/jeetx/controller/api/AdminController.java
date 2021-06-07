package com.jeetx.controller.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

import com.jeetx.bean.member.User;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.swagger.model.JsonResult;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.lottery.LotteryRobotPlantConfigService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.lottery.LotteryTypeService;
import com.jeetx.service.member.RechargeService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.member.WithdrawService;
import com.jeetx.service.system.StationService;
import com.jeetx.util.IpUtil;
import com.jeetx.util.LogUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin
@Controller
@RequestMapping("/api/admin")
@Api(tags = "后台服务接口(参数URLEncoder后提交，线上环境需绑定IP)") //swagger分类标题注解
public class AdminController {
	
	@Autowired RechargeService rechargeService;
	@Autowired UserService userService;
	@Autowired LotteryOrderService lotteryOrderService;
	@Autowired WithdrawService withdrawService;
	@Autowired StationService stationService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired LotteryTypeService lotteryTypeService;
	@Autowired LotteryRobotPlantConfigService lotteryRobotPlantConfigService;
	
	@Value("${developMode}")
	private Boolean developMode;
	
	@Value("${secretKey}")
	private String secretKey;
	
	@Value("${isBindAdminIp}")
	private Boolean isBindAdminIp;
	
	@Value("${bindAdminIps}")
	private String bindAdminIps;

    /**手动充值(调账)处理*/
    @ResponseBody
    @RequestMapping(value = "/rechargeHandle", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "手动充值(调账)处理")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "充值用户", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "amount", value = "充值金额(负数进行充减)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "type", value = "充值方式(1账户充值、2彩金充值)", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "remark", value = "摘要信息", required = false, dataType = "string")
    })
    public JsonResult rechargeHandle(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id") Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="type") Integer type, 
    		@RequestParam(value="remark",required=false) String remark, 
    		@RequestParam(value="amount") String amount){
   		String methodName = "手动充值(调账)处理";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("station_id", stationId);
			parasMap.put("username", username);
			parasMap.put("amount", amount);
			parasMap.put("type", type);
			parasMap.put("remark", remark);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
			
			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
				throw new BusinessException(ApiUtil.getErrorCode("106"));
			}
			
			if(type == 1) {
				remark = StringUtils.isNotBlank((String)parasMap.get("remark"))?"后台手动充值,".concat((String)parasMap.get("remark")):"后台手动充值";
			}else if(type == 2) {
				remark = StringUtils.isNotBlank((String)parasMap.get("remark"))?"后台充值彩金,".concat((String)parasMap.get("remark")):"后台充值彩金";
			}

			rechargeService.rechargeHandle((String)parasMap.get("username"), new BigDecimal(amount), type,remark,stationId,new Date(),true);
   			String message = "处理成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**后台提现处理*/
    @ResponseBody
    @RequestMapping(value = "/withdrawHandle", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "后台提现处理")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "withdraw_id", value = "提现申请单id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "type", value = "处理结果(1同意提现、2拒绝提现)", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "remark", value = "摘要信息", required = false, dataType = "string")
    })
    public JsonResult withdrawHandle(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id") Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="withdraw_id") Integer withdrawId, 
    		@RequestParam(value="type") Integer type, 
    		@RequestParam(value="remark",required=false) String remark){
   		String methodName = "后台提现处理";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("station_id", stationId);
			parasMap.put("withdraw_id", withdrawId);
			parasMap.put("type", type);
			parasMap.put("remark", remark);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
			
			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
				throw new BusinessException(ApiUtil.getErrorCode("106"));
			}

			withdrawService.withdrawHandle(withdrawId, type, (String)parasMap.get("remark"),stationId,new Date());
   			String message = "处理成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**后台手动派奖处理*/
    @ResponseBody
    @RequestMapping(value = "/lotteryAwardHandle", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "后台手动派奖处理")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "lottery_period", value = "彩票期数", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "lottery_type", value = "彩票类型", required = true, dataType = "int")
    })
    public JsonResult lotteryAwardHandle(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id") Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="lottery_type") Integer lotteryType, 
    		@RequestParam(value="lottery_period") String lotteryPeriod){
   		String methodName = "后台手动派奖处理";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("station_id", stationId);
			parasMap.put("lottery_type", lotteryType);
			parasMap.put("lottery_period", lotteryPeriod);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
			
			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
				throw new BusinessException(ApiUtil.getErrorCode("106"));
			}

			lotteryOrderService.lotteryAwardHandle((String)parasMap.get("lottery_period"),lotteryType,stationId,new Date());
   			String message = "处理成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    
    /**取消订单*/
    @ResponseBody
    @RequestMapping(value = "/cancelOrder", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "取消订单")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "order_ids", value = "订单ID集，多个“,”隔开", required = true, dataType = "string")
    })
    public JsonResult cancelOrder(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id") Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="order_ids") String orderIds){
   		String methodName = "取消订单";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("station_id", stationId);
			parasMap.put("order_ids", orderIds);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
			
			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
				throw new BusinessException(ApiUtil.getErrorCode("106"));
			}

			lotteryOrderService.adminCancelOrder((String)parasMap.get("order_ids"),stationId);
   			String message = "处理成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    
    /**创建站点*/
    @ResponseBody
    @RequestMapping(value = "/createStation", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "创建站点")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "station_name", value = "站点名称", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "station_domain", value = "站点域名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "entry_domain", value = "入口域名", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "image_domain", value = "图片域名", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "mq_domain", value = "MQ域名", required = false, dataType = "string")
    	
    })
    public JsonResult createStation(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="entry_domain",required=false) String entryDomain, 
    		@RequestParam(value="image_domain",required=false) String imageDomain, 
    		@RequestParam(value="mq_domain",required=false) String mqDomain, 
    		@RequestParam(value="station_name") String stationName, 
    		@RequestParam(value="station_domain") String stationDomain){
   		String methodName = "创建站点";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("station_name", stationName);
			parasMap.put("station_domain", stationDomain);
			parasMap.put("entry_domain", entryDomain);
			parasMap.put("image_domain", imageDomain);
			parasMap.put("mq_domain", mqDomain);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
			
			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
				throw new BusinessException(ApiUtil.getErrorCode("106"));
			}

			stationService.createStation((String)parasMap.get("station_name"),
					(String)parasMap.get("station_domain"),
					parasMap.get("entry_domain")!=null?(String)parasMap.get("entry_domain"):null,
					parasMap.get("image_domain")!=null?(String)parasMap.get("image_domain"):null,
					parasMap.get("mq_domain")!=null?(String)parasMap.get("mq_domain"):null);
   			String message = "创建成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    
    /**用户隶属关系转移*/
    @ResponseBody
    @RequestMapping(value = "/subTransfer", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "用户隶属关系转移")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "uid", value = "用户id", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "pid", value = "上级id", required = false, dataType = "string")
    })
    public JsonResult subTransfer(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id") Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="uid") Integer uid, 
    		@RequestParam(value="pid") Integer pid){
   		String methodName = "用户隶属关系转移";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("station_id", stationId);
			parasMap.put("uid", uid);
			parasMap.put("pid", pid);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
			
			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
				throw new BusinessException(ApiUtil.getErrorCode("106"));
			}

			userService.subTransfer(stationId, uid, pid);
   			String message = "成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**后台踢除用户登陆*/
    @ResponseBody
    @RequestMapping(value = "/kickOutUsers", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "后台踢除用户登陆")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "充值用户", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "remark", value = "摘要信息", required = false, dataType = "string")
    })
    public JsonResult kickOutUsers(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id") Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="amount") String amount){
   		String methodName = "后台踢除用户登陆";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("station_id", stationId);
			parasMap.put("username", username);
			parasMap.put("amount", amount);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
			
			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
				throw new BusinessException(ApiUtil.getErrorCode("106"));
			}

			userService.kickOutUsers((String)parasMap.get("username"),stationId);
   			String message = "踢除成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**初始游戏规则*/
    @ResponseBody
    @RequestMapping(value = "/initLotteryRule", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "初始游戏规则")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string")
    })
    public JsonResult initLotteryRule(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="timestamp") String timestamp,
    		@RequestParam(value="version") String version,
    		@RequestParam(value="sign") String sign){
   		String methodName = "初始游戏规则";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
//			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数

//			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
//				throw new BusinessException(ApiUtil.getErrorCode("106"));
//			}

			lotteryRuleService.initLotteryRule();
   			String message = "初始化成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
//    /**初始游戏*/
//    @ResponseBody
//    @RequestMapping(value = "/initLotteryType", method={RequestMethod.POST, RequestMethod.GET})
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
//    @ApiOperation(httpMethod = "GET", value = "初始游戏")//当前接口注解
//    @ApiImplicitParams({
//    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = true, dataType = "int"),
//    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
//    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
//    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string")
//    })
//    public JsonResult initLotteryType(HttpServletRequest request, HttpServletResponse response,
//    		@RequestParam(value="station_id") Integer stationId, 
//    		@RequestParam(value="timestamp") String timestamp, 
//    		@RequestParam(value="version") String version, 
//    		@RequestParam(value="sign") String sign){
//   		String methodName = "初始游戏";
//   		try {
//			Map<String, Object> parasMap = new HashMap<String, Object>();
//			parasMap.put("station_id", stationId);
//			parasMap.put("timestamp", timestamp);
//			parasMap.put("version", version);
//			parasMap.put("sign", sign);
//			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
//			
////			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
////				throw new BusinessException(ApiUtil.getErrorCode("106"));
////			}
//			
//			Integer lotteryType = 6;
//			lotteryTypeService.initLotteryType(lotteryType,stationId);
//   			String message = "初始化成功";
//
//   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
//   		}catch (BusinessException e) {
//   			LogUtil.info(methodName.concat("-处理信息异常"), e);
//   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
//   		}catch (Exception e) {
//   			LogUtil.info(methodName.concat("-系统错误"), e);
//   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
//   		}
//   	}
    
    /**后台新增虚拟号*/
    @ResponseBody
    @RequestMapping(value = "/addVirtualUser", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "后台新增虚拟号")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "推广员用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "add_username", value = "需新增用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "add_password", value = "需新增用户登陆密码，两次MD5加密后的结果，结果小写", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "is_init_order", value = "是否初始虚拟交易记录(1：是、0或空：否)", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "card_json", value = "is_init_order=1时有效，银行卡json,格式：{bankName:\"中国建设银行\",cardholder:\"张三\",cardNo:\"12345678912344\",bankPlace:\"河北石家庄\",bankBranch:\"保定支行\"}", required = false, dataType = "string")
    })
    public JsonResult addVirtualUser(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id") Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
     		@RequestParam(value="is_init_order",required=false) String isInitOrder,
    		@RequestParam(value="card_json",required=false) String cardJson, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="add_username") String addUsername, 
    		@RequestParam(value="add_password") String addPassword){
   		String methodName = "后台新增虚拟号";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			parasMap.put("station_id", stationId);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap.put("username", username);
			parasMap.put("is_init_order", isInitOrder);
			parasMap.put("card_json", cardJson);
			parasMap.put("add_username", addUsername);
			parasMap.put("add_password", addPassword);
			parasMap =  ApiUtil.checkParameter(parasMap,ApiUtil.SECRETKEY);//验证参数
			
			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
				throw new BusinessException(ApiUtil.getErrorCode("106"));
			}

			User user = userService.findUser((String)parasMap.get("username"), stationId);
			userService.addVirtualUser(user,(String)parasMap.get("add_username"),(String)parasMap.get("add_password"),(String)parasMap.get("is_init_order"),(String)parasMap.get("card_json"),
					IpUtil.getIpAddr(request),null,stationId);
   			String message = "新增成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
//	/** 初始化假人 */
//	@ResponseBody
//	@RequestMapping(value = "/initRobotPlantConfig", method = { RequestMethod.POST, RequestMethod.GET })
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误") })
//	@ApiOperation(httpMethod = "GET", value = "初始游戏") // 当前接口注解
//    @ApiImplicitParams({
//		@ApiImplicitParam(paramType = "query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
//		@ApiImplicitParam(paramType = "query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
//		@ApiImplicitParam(paramType = "query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string")
//    })
//	public JsonResult initRobotPlantConfig(HttpServletRequest request, HttpServletResponse response,
//			@RequestParam(value = "timestamp") String timestamp,
//			@RequestParam(value = "version") String version, 
//			@RequestParam(value = "sign") String sign) {
//		String methodName = "初始化假人 ";
//		try {
//			Map<String, Object> parasMap = new HashMap<String, Object>();
//			parasMap.put("timestamp", timestamp);
//			parasMap.put("version", version);
//			parasMap.put("sign", sign);
//			parasMap = ApiUtil.checkParameter(parasMap, ApiUtil.SECRETKEY);// 验证参数
//
////			if(isBindAdminIp && !(Arrays.asList(bindAdminIps.split(",")).contains(IpUtil.getIpAddr(request)))) {
////				throw new BusinessException(ApiUtil.getErrorCode("106"));
////			}
//
//			lotteryRobotPlantConfigService.initRobotPlantConfig();
//			String message = "初始化成功";
//
//			return new JsonResult(developMode, methodName, ApiUtil.getRequestUrl(request), 0, message, null);
//		} catch (BusinessException e) {
//			LogUtil.info(methodName.concat("-处理信息异常"), e);
//			return new JsonResult(developMode, methodName, ApiUtil.getRequestUrl(request),
//					Integer.valueOf(e.getMessage().split("-")[0]), e.getMessage().split("-")[1], null);
//		} catch (Exception e) {
//			LogUtil.info(methodName.concat("-系统错误"), e);
//			return new JsonResult(developMode, methodName, ApiUtil.getRequestUrl(request), 200, "系统错误", null);
//		}
//	}
}
