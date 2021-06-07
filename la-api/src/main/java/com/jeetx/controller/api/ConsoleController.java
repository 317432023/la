package com.jeetx.controller.api;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.jeetx.bean.base.RechargeOnLineConfig;
import com.jeetx.bean.base.RechargeTransferConfig;
import com.jeetx.bean.base.RechargeUnderLineConfig;
import com.jeetx.bean.base.WithdrawConfig;
import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.LotteryOrderItem;
import com.jeetx.bean.lottery.LotteryWaterRecord;
import com.jeetx.bean.member.BankCard;
import com.jeetx.bean.member.Letter;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.bean.member.User;
import com.jeetx.bean.member.UserOnline;
import com.jeetx.bean.member.Withdraw;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.model.page.PageView;
import com.jeetx.common.model.page.QueryResult;
import com.jeetx.common.redis.JedisClient;
import com.jeetx.common.swagger.model.JsonResult;
import com.jeetx.common.swagger.model.Member.BankCardVo;
import com.jeetx.common.swagger.model.Member.LetterVo;
import com.jeetx.common.swagger.model.Member.TransRecordVo;
import com.jeetx.common.swagger.model.Member.WithdrawVo;
import com.jeetx.common.swagger.model.base.RechargeOnLineConfigVo;
import com.jeetx.common.swagger.model.base.RechargeTransferConfigVo;
import com.jeetx.common.swagger.model.base.RechargeUnderLineConfigVo;
import com.jeetx.common.swagger.model.base.WithdrawConfigVo;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemVo;
import com.jeetx.common.swagger.model.lottery.LotteryOrderTotalVo;
import com.jeetx.common.swagger.model.lottery.LotteryOrderVo;
import com.jeetx.common.swagger.model.lottery.LotteryWaterRecordVo;
import com.jeetx.service.base.RechargeOnLineConfigService;
import com.jeetx.service.base.RechargeTransferConfigService;
import com.jeetx.service.base.RechargeUnderLineConfigService;
import com.jeetx.service.base.WithdrawConfigService;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.lottery.LotteryWaterRecordService;
import com.jeetx.service.member.BankCardService;
import com.jeetx.service.member.LetterService;
import com.jeetx.service.member.RechargeService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserOnlineService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.member.WithdrawService;
import com.jeetx.service.system.StationService;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.IpUtil;
import com.jeetx.util.LogUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@CrossOrigin
@Controller
@RequestMapping("/api/console")
@Api(tags = "用户信息服务接口(参数URLEncoder后提交)") //swagger分类标题注解
public class ConsoleController {
	
    @Autowired JedisClient jedisClient;
	@Autowired UserService userService;
	@Autowired BankCardService bankCardService;
	@Autowired LetterService letterService;
	@Autowired TransRecordService transRecordService;
	@Autowired WithdrawService withdrawService;
	@Autowired LotteryOrderService lotteryOrderService;
	@Autowired WithdrawConfigService withdrawConfigService;
	@Autowired RechargeService rechargeService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired RechargeUnderLineConfigService rechargeUnderLineConfigService;
	@Autowired RechargeOnLineConfigService rechargeOnLineConfigService;
	@Autowired RechargeTransferConfigService rechargeTransferConfigService;
	@Autowired LotteryWaterRecordService lotteryWaterRecordService;
	@Autowired StationService stationService;
	@Autowired UserOnlineService userOnlineService;
	
	@Value("${developMode}")
	private Boolean developMode;
	
	@Value("${resServerLink}")
	private String resServerLink; 
	
	/**玩家退出(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/logout", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "玩家退出(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult logout(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "玩家退出(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			userService.logout((String)parasMap.get("username"), token,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
   			String message = "退出成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
	
    /**获取玩家信息(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/userInfo", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "获取玩家信息(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult userInfo(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "获取玩家信息(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数

			User user = userService.getUserInfo((String)parasMap.get("username"), token,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
			
			BigDecimal profitToday = new BigDecimal(0);//今日流水
   			LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", new Date()), user.getId());
   			if(lotteryDailyOrderTotal != null ) {
   				profitToday = lotteryDailyOrderTotal.getBetMoney();
   			}

   			Map<String, Object> userInfoMap = new HashMap<String, Object>();
   			userInfoMap = ApiUtil.toUserMap(user,ApiUtil.getResServerLink(resServerLink, user.getStation().getImageDomain()));
   			userInfoMap.put("profitToday", profitToday);
   			
   			Map<String, Object> result = new HashMap<String, Object>();
			result.put("userInfo", userInfoMap);
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
    
    /**修改昵称(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/updateNickName", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "修改昵称(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "nickname", value = "新的玩家昵称", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult updateNickName(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="nickname") String nickname, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "修改昵称(需登录权限)";
   		try {
   			//System.out.println(nickname);
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("nickname", nickname);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			userService.updateNickName(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),(String)parasMap.get("nickname"),stationId);
			
   			String message = "修改成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**修改头像(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/updateHeadImg", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "修改头像(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "head_link", value = "头像存放地址", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult updateHeadImg(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="head_link") String headImg, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "修改头像(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("head_link", headImg);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			userService.updateHeadImg(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),(String)parasMap.get("head_link"),stationId);
			
   			String message = "修改成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**添加银行卡(需登录权限)*/
    //{bankName:"中国建设银行",cardholder:"张三",cardNo:"12345678912344",bankPlace:"河北石家庄",bankBranch:"保定支行"}
    @ResponseBody
    @RequestMapping(value = "/addBankCard", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "添加银行卡(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "card_json", value = "银行卡json,格式：{bankName:\"中国建设银行\",cardholder:\"张三\",cardNo:\"12345678912344\",bankPlace:\"河北石家庄\",bankBranch:\"保定支行\"}", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult addBankCard(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="card_json") String cardJson, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "添加银行卡(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("card_json", cardJson);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			bankCardService.addBankCard(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),(String)parasMap.get("card_json"));
			
   			String message = "添加成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**删除银行卡(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/delBankCard", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "删除银行卡(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "card_id", value = "银行卡id", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult delBankCard(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="card_id") Integer cardId, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "删除银行卡(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("card_id", cardId);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			bankCardService.delBankCard(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),Integer.valueOf((String)parasMap.get("card_id")));
			
   			String message = "删除成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
	/**获取银行卡列表(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/bankCardList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = BankCardVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取银行卡列表(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
    })
    public JsonResult bankCardList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token,
    		@RequestParam(value="page") Integer page,
    		@RequestParam(value="limit") Integer limit){
		String methodName = "获取银行卡列表(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("id", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			sb.append(" and o.user.id = ?");
			params.add(user.getId());

			QueryResult<BankCard> qr = bankCardService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<BankCardVo> data = new ArrayList<BankCardVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (BankCard object : qr.getResultData()) {
			 		BankCardVo vo = new BankCardVo();
			 		vo.setId(object.getId());
			 		vo.setBankName(StringUtils.isNotBlank(object.getBankName())?object.getBankName():"");
			 		vo.setBankBranch(StringUtils.isNotBlank(object.getOpenBankBranch())?object.getOpenBankBranch():"");
			 		vo.setBankPlace(StringUtils.isNotBlank(object.getOpenBankPlace())?object.getOpenBankPlace():"");
			 		vo.setCardNo(StringUtils.isNotBlank(object.getBankCard())?object.getBankCard():"");
			 		vo.setCardholder(StringUtils.isNotBlank(object.getCardholder())?object.getCardholder():"");
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
    
	/**修改密码(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/updatePwd", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = BankCardVo.class)})
    @ApiOperation(httpMethod = "GET", value = "修改密码(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "password", value = "登陆密码，两次MD5加密后的结果，结果小写", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "new_password", value = "登陆密码，两次MD5加密后的结果，结果小写", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "type", value = "密码类型(1登陆密码、2安全码)", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
    })
    public JsonResult updatePwd(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="password") String password, 
    		@RequestParam(value="new_password") String newPassword, 
    		@RequestParam(value="type") Integer type, 
    		@RequestParam(value="token") String token){
		String methodName = "修改密码(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("password", password);
			parasMap.put("new_password", newPassword);
			parasMap.put("type", type);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			String message = "修改成功";
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			userService.updatePwd(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device), type, password, newPassword,stationId);
			
			//return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,RandomUtil.generateString(32),null);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }

  	/**站内信列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/letterList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LetterVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "站内信列表(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "status", value = "1未读、2已读、3已删除、-1全部", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult letterList(HttpServletRequest request, HttpServletResponse response,
    	@RequestParam(value="station_id",required=false) Integer stationId, 
  		@RequestParam(value="timestamp") String timestamp, 
  		@RequestParam(value="version") String version, 
  		@RequestParam(value="sign") String sign, 
  		@RequestParam(value="device") Integer device,
  		@RequestParam(value="username") String username, 
  		@RequestParam(value="token") String token,
  		@RequestParam(value="status") Integer status,
  		@RequestParam(value="page") Integer page,
  		@RequestParam(value="limit") Integer limit){
		String methodName = "站内信列表(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("status", status);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("id", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			if(status > 0) {
				sb.append(" and o.status = ?");
				params.add(status);
			}
			
			sb.append(" and o.user.id = ?");
			params.add(user.getId());

			QueryResult<Letter> qr = letterService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<LetterVo> data = new ArrayList<LetterVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (Letter object : qr.getResultData()) {
			 		LetterVo vo = new LetterVo();
			 		vo.setId(object.getId());
			 		vo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
			 		vo.setContents(StringUtils.isNotBlank(object.getContents())?object.getContents():"");
			 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
			 		vo.setStatus(object.getStatus());
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
    
    /**站内信详情(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/letterDetail", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LetterVo.class)})
    @ApiOperation(httpMethod = "GET", value = "站内信详情(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "letter_id", value = "资讯Id", required = true, dataType = "int")
    })
    public JsonResult letterDetail(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device") Integer device,
      		@RequestParam(value="username") String username, 
      		@RequestParam(value="token") String token,
    		@RequestParam(value="letter_id") Integer letterId){
   		String methodName = "站内信详情(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("letter_id", letterId);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
   			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			Letter object = letterService.findLetter(user.getUsername(),letterId);
   			String message = "";
   			LetterVo vo = null;
   			if(object != null) {
   				if(object.getStatus()==1) {
   					object.setStatus(2);
   					object.setReadTime(new Date());
   					letterService.update(object);
   				}
   				
		 		vo = new LetterVo();
		 		vo.setId(object.getId());
		 		vo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
		 		vo.setContents(StringUtils.isNotBlank(object.getContents())?object.getContents():"");
		 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
		 		vo.setStatus(object.getStatus());
		 		message = "成功";
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> result = new HashMap<String, Object>();
			result.put("data", vo);

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    
  	/**资金明细列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/transRecordList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = TransRecordVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "资金明细列表(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "status", value = "交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、-1全部）",required = true,dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult transRecordList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
	  		@RequestParam(value="timestamp") String timestamp, 
	  		@RequestParam(value="version") String version, 
	  		@RequestParam(value="sign") String sign, 
	  		@RequestParam(value="device") Integer device,
	 		@RequestParam(value="status") Integer status,
	 		@RequestParam(value="date_begin",required=false) String dateBegin,
	 		@RequestParam(value="date_end",required=false) String dateEnd,
	  		@RequestParam(value="username") String username, 
	  		@RequestParam(value="token") String token,
	  		@RequestParam(value="page") Integer page,
	  		@RequestParam(value="limit") Integer limit){
			String methodName = "资金明细列表(需登录权限)";
		try {
			//System.out.println(ApiUtil.getRequestUrl(request));
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("status", status);
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("id", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			/** 开始时间*/
			if(StringUtils.isNotBlank(dateBegin)){
				sb.append(" and o.createTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryStartDate(dateBegin));
			}
	
			/** 截止时间*/
			if(StringUtils.isNotBlank(dateEnd)) {
				sb.append(" and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryEndDate(dateEnd));
			}
		
			if(status > 0) {
				sb.append(" and o.transCategory = ?");
				params.add(status);
			}
			
			sb.append(" and o.user.id = ?");
			params.add(user.getId());
			
			
			sb.append(" and o.user.station.id = ?");
			params.add(stationId);

			QueryResult<TransRecord> qr = transRecordService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<TransRecordVo> data = new ArrayList<TransRecordVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (TransRecord object : qr.getResultData()) {
			 		TransRecordVo vo = new TransRecordVo();
			 		vo.setId(object.getId());
			 		vo.setUserId(object.getUser().getId());
			 		vo.setUsername(object.getUser().getUsername());
			 		vo.setNickName(object.getUser().getNickName());
			 		vo.setTransAmount(object.getTransAmount().toString());
			 		vo.setEndBalance(object.getEndBalance().toString());
					vo.setTransLotteryAmount(object.getTransLotteryAmount().toString());
			 		vo.setEndLotteryBalance(object.getEndLotteryBalance().toString());
			 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
			 		vo.setTransCategory(object.getTransCategory());
			 		vo.setRemark(StringUtils.isNotBlank(object.getRemark())?object.getRemark():"");
			 		vo.setFlag(object.getFlag());
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
    
    /**资金明细详情(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/transRecordDetail", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = TransRecordVo.class)})
    @ApiOperation(httpMethod = "GET", value = "资金明细详情(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "record_id", value = "明细Id", required = true, dataType = "int")
    })
    public JsonResult transRecordDetail(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device") Integer device,
      		@RequestParam(value="username") String username, 
      		@RequestParam(value="token") String token,
    		@RequestParam(value="record_id") Integer recordId){
   		String methodName = "资金明细详情(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("record_id", recordId);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
   			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			TransRecord object = transRecordService.findTransRecord(user.getUsername(),recordId);
   			String message = "";
   			TransRecordVo vo = null;
   			if(object != null) {
		 		vo = new TransRecordVo();
		 		vo.setId(object.getId());
		 		vo.setUserId(object.getUser().getId());
		 		vo.setUsername(object.getUser().getUsername());
		 		vo.setNickName(object.getUser().getNickName());
		 		vo.setTransAmount(object.getTransAmount().toString());
		 		vo.setEndBalance(object.getEndBalance().toString());
				vo.setTransLotteryAmount(object.getTransLotteryAmount().toString());
		 		vo.setEndLotteryBalance(object.getEndLotteryBalance().toString());
		 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
		 		vo.setTransCategory(object.getTransCategory());
		 		vo.setRemark(StringUtils.isNotBlank(object.getRemark())?object.getRemark():"");
		 		vo.setFlag(object.getFlag());
		 		message = "成功";
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> result = new HashMap<String, Object>();
			result.put("data", vo);

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    
  	/**提现记录列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/withdrawList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = WithdrawVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "提现记录列表(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "status", value = "1申请中、2提现成功、3打款失败、-1全部", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult withdrawList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
	  		@RequestParam(value="timestamp") String timestamp, 
	  		@RequestParam(value="version") String version, 
	  		@RequestParam(value="sign") String sign, 
	  		@RequestParam(value="device") Integer device,
	 		@RequestParam(value="status") Integer status,
	 		@RequestParam(value="date_begin",required=false) String dateBegin,
	 		@RequestParam(value="date_end",required=false) String dateEnd,
	  		@RequestParam(value="username") String username, 
	  		@RequestParam(value="token") String token,
	  		@RequestParam(value="page") Integer page,
	  		@RequestParam(value="limit") Integer limit){
			String methodName = "提现记录列表(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("status", status);
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("token", token);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("id", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			/** 开始时间*/
			if(StringUtils.isNotBlank(dateBegin)){
				sb.append(" and o.createTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryStartDate(dateBegin));
			}
	
			/** 截止时间*/
			if(StringUtils.isNotBlank(dateEnd)) {
				sb.append(" and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryEndDate(dateEnd));
			}
			
			if(status > 0) {
				sb.append(" and o.status = ?");
				params.add(status);
			}
			
			sb.append(" and o.user.id = ?");
			params.add(user.getId());

			QueryResult<Withdraw> qr = withdrawService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<WithdrawVo> data = new ArrayList<WithdrawVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (Withdraw object : qr.getResultData()) {
			 		WithdrawVo vo = new WithdrawVo();
			 		vo.setId(object.getId());
			 		vo.setTradeCode(object.getTradeCode());
			 		vo.setApplyAmount(object.getApplyAmount().toString());
			 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
			 		vo.setApplyRemark(object.getApplyRemark());
			 		vo.setStatus(object.getStatus());
			 		vo.setBankCardInfo(StringUtils.isNotBlank(object.getBankCardInfo())?object.getBankCardInfo():"");
			 		try {
			 			if(StringUtils.isNotBlank(object.getBankCardInfo())) {
			 				StringBuffer tempBankCardInfo = new StringBuffer("");
			 				String[] bankCardInfoArr= object.getBankCardInfo().split(" ");
			 				if(bankCardInfoArr != null && bankCardInfoArr.length == 3) {
			 					tempBankCardInfo.append(bankCardInfoArr[0]).append(" ");
			 					tempBankCardInfo.append(bankCardInfoArr[1].substring(0, 4))
			 					.append("************")
			 					.append(bankCardInfoArr[1].substring(bankCardInfoArr[1].length()-4, bankCardInfoArr[1].length()))
			 					.append(" ");
			 					tempBankCardInfo.append(bankCardInfoArr[2].substring(0, 2)).append("****");
			 				}
			 				
			 				if(StringUtils.isNotBlank(bankCardInfoArr.toString())) {
			 					vo.setBankCardInfo(tempBankCardInfo.toString());	
			 				}
			 			}
			 		}catch (Exception e) {}
			 		vo.setConfirmTime(DateTimeTool.dateFormat(null, object.getConfirmTime()));
			 		vo.setConfirmRemark(object.getConfirmRemark());
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
    
    /**提现记录详情(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/withdrawDetail", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = WithdrawVo.class)})
    @ApiOperation(httpMethod = "GET", value = "提现记录详情(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "record_id", value = "明细Id", required = true, dataType = "int")
    })
    public JsonResult withdrawDetail(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device") Integer device,
      		@RequestParam(value="username") String username, 
      		@RequestParam(value="token") String token,
    		@RequestParam(value="record_id") Integer recordId){
   		String methodName = "提现记录详情(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("record_id", recordId);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
   			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			Withdraw object = withdrawService.findWithdraw(user.getUsername(),recordId);
   			String message = "";
   			WithdrawVo vo = null;
   			if(object != null) {
		 		vo = new WithdrawVo();
		 		vo.setId(object.getId());
		 		vo.setTradeCode(object.getTradeCode());
		 		vo.setApplyAmount(object.getApplyAmount().toString());
		 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
		 		vo.setApplyRemark(object.getApplyRemark());
		 		vo.setStatus(object.getStatus());
		 		vo.setBankCardInfo(StringUtils.isNotBlank(object.getBankCardInfo())?object.getBankCardInfo():"");
		 		try {
		 			if(StringUtils.isNotBlank(object.getBankCardInfo())) {
		 				StringBuffer tempBankCardInfo = new StringBuffer("");
		 				String[] bankCardInfoArr= object.getBankCardInfo().split(" ");
		 				if(bankCardInfoArr != null && bankCardInfoArr.length == 3) {
		 					tempBankCardInfo.append(bankCardInfoArr[0]).append(" ");
		 					tempBankCardInfo.append(bankCardInfoArr[1].substring(0, 4))
		 					.append("************")
		 					.append(bankCardInfoArr[1].substring(bankCardInfoArr[1].length()-4, bankCardInfoArr[1].length()))
		 					.append(" ");
		 					tempBankCardInfo.append(bankCardInfoArr[2].substring(0, 2)).append("****");
		 				}
		 				
		 				if(StringUtils.isNotBlank(bankCardInfoArr.toString())) {
		 					vo.setBankCardInfo(tempBankCardInfo.toString());	
		 				}
		 			}
		 		}catch (Exception e) {}
		 		vo.setConfirmTime(DateTimeTool.dateFormat(null, object.getConfirmTime()));
		 		vo.setConfirmRemark(object.getConfirmRemark());
		 		message = "成功";
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> result = new HashMap<String, Object>();
			result.put("data", vo);

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**获取提现设置信息(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/withdrawConfig", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = WithdrawConfigVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取提现设置信息(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string")
    })
    public JsonResult withdrawConfig(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="token") String token){
   		String methodName = "获取提现设置信息(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("device", device);
			parasMap.put("sign", sign);
			String secretKey = device!=null&&device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数

			String message = "成功";
			Map<String, Object> result = null;
			WithdrawConfig withdrawConfig = withdrawConfigService.getWithdrawConfigByStationId(stationId);
			if(withdrawConfig != null) {
				WithdrawConfigVo vo = new WithdrawConfigVo();
		 		vo.setBeginTime(withdrawConfig.getBeginTime());
		 		vo.setEndTime(withdrawConfig.getEndTime());
		 		vo.setMaxApplyAmount(withdrawConfig.getMaxApplyAmount());
		 		vo.setMinApplyAmount(withdrawConfig.getMinApplyAmount());
		 		vo.setApplyDailyTimes(withdrawConfig.getApplyDailyTimes());
		 		vo.setReminder(withdrawConfig.getReminder());
		 		
			 	result = new HashMap<String, Object>();
				result.put("config", vo);
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
    
    
	/**提现申请(需登录权限)*/
    @ResponseBody
	@RequestMapping(value = "/withdrawApply", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = BankCardVo.class)})
    @ApiOperation(httpMethod = "GET", value = "提现申请(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "security_code", value = "MD5(安全码(两次MD5加密后的结果，结果小写)+timestamp),结果小写", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "bankcard_id", value = "银行卡ID", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "apply_amount", value = "提现金额", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
    })
    public JsonResult withdrawApply(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="security_code",required=false) String securityCode, 
    		@RequestParam(value="bankcard_id") Integer bankCardId, 
    		@RequestParam(value="apply_amount") String applyAmount, 
    		@RequestParam(value="token") String token){
		String methodName = "提现申请(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("security_code", securityCode);
			parasMap.put("bankcard_id", bankCardId);
			parasMap.put("apply_amount", applyAmount);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			withdrawService.applyWithdraw(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),timestamp,
					securityCode, bankCardId, new BigDecimal(applyAmount),new Date());
			
   			String message = "申请成功";
			
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }

    
  	/**投注记录列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/lotteryOrderList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryOrderVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "投注记录列表(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "status", value = "1待开奖、2已中奖、3已取消、4未中奖、-1全部", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "lotteryType", value = "游戏类型、-1全部", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult lotteryOrderList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
	  		@RequestParam(value="timestamp") String timestamp, 
	  		@RequestParam(value="version") String version, 
	  		@RequestParam(value="sign") String sign, 
	  		@RequestParam(value="device") Integer device,
	 		@RequestParam(value="status") Integer status,
	 		@RequestParam(value="lotteryType",required=false) Integer lotteryType,
	 		@RequestParam(value="date_begin",required=false) String dateBegin,
	 		@RequestParam(value="date_end",required=false) String dateEnd,
	  		@RequestParam(value="username") String username, 
	  		@RequestParam(value="token") String token,
	  		@RequestParam(value="page") Integer page,
	  		@RequestParam(value="limit") Integer limit){
		String methodName = "投注记录列表(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("status", status);
			parasMap.put("lotteryType", lotteryType);
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("token", token);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("id", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			/** 开始时间*/
			if(StringUtils.isNotBlank(dateBegin)){
				sb.append(" and o.createTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryStartDate(dateBegin));
			}
	
			/** 截止时间*/
			if(StringUtils.isNotBlank(dateEnd)) {
				sb.append(" and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryEndDate(dateEnd));
			}
			
			if(status > 0) {
				sb.append(" and o.status = ?");
				params.add(status);
			}
			
			if(lotteryType > 0) {
				sb.append(" and o.lotteryType.id = ?");
				params.add(lotteryType);
			}
			
			sb.append(" and o.user.id = ?");
			params.add(user.getId());

			QueryResult<LotteryOrder> qr = lotteryOrderService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "成功";
			Map<String, Object> result = null;
			List<LotteryOrderVo> data = new ArrayList<LotteryOrderVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (LotteryOrder object : qr.getResultData()) {
			 		LotteryOrderVo vo = new LotteryOrderVo();
			 		vo.setId(object.getId());
			 		vo.setOrderCode(object.getOrderCode());
			 		vo.setNickName(object.getUser().getNickName());
			 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
			 		vo.setLotteryType(object.getLotteryType()!=null?object.getLotteryType().getId():null);
			 		vo.setLotteryName(object.getLotteryType()!=null?object.getLotteryType().getLotteryName():"");
			 		vo.setHallId(object.getLotteryRoom()!=null?object.getLotteryRoom().getLotteryHall().getId():null);
			 		vo.setHallName(object.getLotteryRoom()!=null?object.getLotteryRoom().getLotteryHall().getTitle():null);
			 		vo.setRoomId(object.getLotteryRoom()!=null?object.getLotteryRoom().getId():null);
			 		vo.setRoomName(object.getLotteryRoom()!=null?object.getLotteryRoom().getTitle():"");
			 		vo.setLotteryPeriod(object.getLotteryPeriod());
			 		vo.setBetMoney(object.getBetMoney().toString());
			 		vo.setWinMoney(object.getWinMoney()!=null?object.getWinMoney().toString():"");
			 		vo.setProfitMoney(object.getProfitMoney()!=null?object.getProfitMoney().toString():"");
			 		vo.setStatus(object.getStatus());
			 		vo.setLotteryOpenContent(StringUtils.isNotBlank(object.getLotteryOpenContent())?object.getLotteryOpenContent():"");
			 	
			 		List<LotteryOrderItemVo> items = new ArrayList<LotteryOrderItemVo>();
			 		for (LotteryOrderItem lotteryOrderItem : object.getLotteryOrderItems()) {
			 			LotteryOrderItemVo itemVo = new LotteryOrderItemVo();
			 			itemVo.setId(lotteryOrderItem.getId());
			 			itemVo.setBetMoney(lotteryOrderItem.getBetMoney().toString());
			 			itemVo.setRuleName(lotteryOrderItem.getRuleName());
			 			itemVo.setWinMoney(lotteryOrderItem.getWinMoney()!=null?lotteryOrderItem.getWinMoney().toString():null);
			 			items.add(itemVo);
					}
			 		vo.setItems(items);

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
    
    /**投注记录详情(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/lotteryOrderDetail", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryOrderVo.class)})
    @ApiOperation(httpMethod = "GET", value = "投注记录详情(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "order_id", value = "订单Id", required = true, dataType = "int")
    })
    public JsonResult lotteryOrderDetail(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
      		@RequestParam(value="device") Integer device,
      		@RequestParam(value="username") String username, 
      		@RequestParam(value="token") String token,
    		@RequestParam(value="order_id") Integer orderId){
   		String methodName = "投注记录详情(需登录权限)";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("order_id", orderId);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
   			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			LotteryOrder object = lotteryOrderService.findLotteryOrder(user.getUsername(),orderId);
   			String message = "";
   			LotteryOrderVo vo = null;
   			if(object != null) {
		 		vo = new LotteryOrderVo();
		 		vo.setId(object.getId());
		 		vo.setOrderCode(object.getOrderCode());
		 		vo.setNickName(object.getUser().getNickName());
		 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
		 		vo.setLotteryType(object.getLotteryType()!=null?object.getLotteryType().getId():null);
		 		vo.setLotteryName(object.getLotteryType()!=null?object.getLotteryType().getLotteryName():"");
		 		vo.setHallId(object.getLotteryRoom()!=null?object.getLotteryRoom().getLotteryHall().getId():null);
		 		vo.setHallName(object.getLotteryRoom()!=null?object.getLotteryRoom().getLotteryHall().getTitle():null);
		 		vo.setRoomId(object.getLotteryRoom()!=null?object.getLotteryRoom().getId():null);
		 		vo.setRoomName(object.getLotteryRoom()!=null?object.getLotteryRoom().getTitle():"");
		 		vo.setLotteryPeriod(object.getLotteryPeriod());
		 		vo.setBetMoney(object.getBetMoney().toString());
		 		vo.setWinMoney(object.getWinMoney()!=null?object.getWinMoney().toString():"");
		 		vo.setProfitMoney(object.getProfitMoney()!=null?object.getProfitMoney().toString():"");
		 		vo.setStatus(object.getStatus());
		 		vo.setLotteryOpenContent(StringUtils.isNotBlank(object.getLotteryOpenContent())?object.getLotteryOpenContent():"");
		 	
		 		List<LotteryOrderItemVo> items = new ArrayList<LotteryOrderItemVo>();
		 		for (LotteryOrderItem lotteryOrderItem : object.getLotteryOrderItems()) {
		 			LotteryOrderItemVo itemVo = new LotteryOrderItemVo();
		 			itemVo.setId(lotteryOrderItem.getId());
		 			itemVo.setBetMoney(lotteryOrderItem.getBetMoney().toString());
		 			itemVo.setRuleName(lotteryOrderItem.getRuleName());
		 			itemVo.setWinMoney(lotteryOrderItem.getWinMoney()!=null?lotteryOrderItem.getWinMoney().toString():null);
		 			items.add(itemVo);
				}
		 		vo.setItems(items);
		 		message = "成功";
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> result = new HashMap<String, Object>();
			result.put("data", vo);

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    
   /**充值(线下扫码)*/
   @ResponseBody
	@RequestMapping(value = "/scanCodeRechargeConfig", method={RequestMethod.POST, RequestMethod.GET})
   @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = RechargeUnderLineConfigVo.class)})
   @ApiOperation(httpMethod = "GET", value = "充值(线下扫码)")//当前接口注解
   @ApiImplicitParams({
	   	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
   		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
   })
   public JsonResult scanCodeRechargeConfig(HttpServletRequest request, HttpServletResponse response,
		    @RequestParam(value="station_id",required=false) Integer stationId, 
	   		@RequestParam(value="timestamp") String timestamp, 
	  		@RequestParam(value="username") String username, 
	  		@RequestParam(value="token") String token,
	   		@RequestParam(value="version") String version, 
	   		@RequestParam(value="sign") String sign, 
	   		@RequestParam(value="device") Integer device){
		String methodName = "充值(线下扫码)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
  			String message = "";
  			List<RechargeUnderLineConfig> rechargeUnderLineConfigList = null;
  			switch (device) {
			case 0:
				rechargeUnderLineConfigList = rechargeUnderLineConfigService.findByHql("from RechargeUnderLineConfig o where o.station.id = "+stationId+" and o.isShowPC = 1 order by o.sortNum desc");
				break;
			case 1:
			case 2:
			case 3:
				rechargeUnderLineConfigList = rechargeUnderLineConfigService.findByHql("from RechargeUnderLineConfig o where o.station.id = "+stationId+" and o.isShowApp = 1 order by o.sortNum desc");
				break;
			}

  			List<RechargeUnderLineConfigVo> data = new ArrayList<RechargeUnderLineConfigVo>();
  			if(rechargeUnderLineConfigList != null && rechargeUnderLineConfigList.size() >0) {
  				message = "成功";
			 	for (RechargeUnderLineConfig object : rechargeUnderLineConfigList) {
			 		RechargeUnderLineConfigVo vo = new RechargeUnderLineConfigVo();
			 		vo.setTitle(object.getTitle());
			 		vo.setIconImg(StringUtils.isNotBlank(object.getIconImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getIconImg():"");
			 		vo.setQrCodeLink(StringUtils.isNotBlank(object.getQrCodeLink())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getQrCodeLink():"");
			 		vo.setMinAmount(object.getMinAmount().toString());
			 		vo.setMaxAmount(object.getMaxAmount().toString());
			 		vo.setReminder(StringUtils.isNotBlank(object.getReminder())?object.getReminder():"");
			 		data.add(vo);
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
   
   
   /**充值(网银转账)*/
   @ResponseBody
	@RequestMapping(value = "/transferRechargeConfig", method={RequestMethod.POST, RequestMethod.GET})
   @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = RechargeTransferConfigVo.class)})
   @ApiOperation(httpMethod = "GET", value = "充值(网银转账)")//当前接口注解
   @ApiImplicitParams({
	   	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
   		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
   })
   public JsonResult transferRechargeConfig(HttpServletRequest request, HttpServletResponse response,
		   	@RequestParam(value="station_id",required=false) Integer stationId, 
   			@RequestParam(value="timestamp") String timestamp,
   			@RequestParam(value="username") String username, 
   			@RequestParam(value="token") String token,
   			@RequestParam(value="version") String version, 
   			@RequestParam(value="sign") String sign, 
   			@RequestParam(value="device") Integer device){
		String methodName = "充值(网银转账)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
  			String message = "";
  			List<RechargeTransferConfig> rechargeTransferConfigList = null;
  			switch (device) {
			case 0:
				rechargeTransferConfigList = rechargeTransferConfigService.findByHql("from RechargeTransferConfig o where o.station.id = "+stationId+" and  o.isShowPC = 1 order by o.sortNum desc");
				break;
			case 1:
			case 2:
			case 3:
				rechargeTransferConfigList = rechargeTransferConfigService.findByHql("from RechargeTransferConfig o where o.station.id = "+stationId+" and  o.isShowApp = 1 order by o.sortNum desc");
				break;
			}

  			List<RechargeTransferConfigVo> data = new ArrayList<RechargeTransferConfigVo>();
  			if(rechargeTransferConfigList != null && rechargeTransferConfigList.size() >0) {
  				message = "成功";
			 	for (RechargeTransferConfig object : rechargeTransferConfigList) {
			 		RechargeTransferConfigVo vo = new RechargeTransferConfigVo();
			 		vo.setTitle(object.getTitle());
			 		vo.setIconImg(StringUtils.isNotBlank(object.getIconImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getIconImg():"");
			 		vo.setMinAmount(object.getMinAmount().toString());
			 		vo.setMaxAmount(object.getMaxAmount().toString());
			 		vo.setReminder(StringUtils.isNotBlank(object.getReminder())?object.getReminder():"");
			 		vo.setBankCard(object.getBankCard());
			 		vo.setBankName(object.getBankName());
			 		vo.setCardholder(object.getCardholder());
			 		vo.setOpenBankBranch(StringUtils.isNotBlank(object.getOpenBankBranch())?object.getOpenBankBranch():"");
			 		data.add(vo);
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
   
   /**充值(网上充值)*/
   @ResponseBody
	@RequestMapping(value = "/onlineRechargeConfig", method={RequestMethod.POST, RequestMethod.GET})
   @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = RechargeOnLineConfigVo.class)})
   @ApiOperation(httpMethod = "GET", value = "充值(网上充值)")//当前接口注解
   @ApiImplicitParams({
	   	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
   		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
   })
   public JsonResult onlineRechargeConfig(HttpServletRequest request, HttpServletResponse response,
		   	@RequestParam(value="station_id",required=false) Integer stationId, 
   			@RequestParam(value="timestamp") String timestamp,
   			@RequestParam(value="username") String username, 
   			@RequestParam(value="token") String token,
   			@RequestParam(value="version") String version, 
   			@RequestParam(value="sign") String sign, 
   			@RequestParam(value="device") Integer device){
		String methodName = "充值(网上充值)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
  			String message = "";
  			List<RechargeOnLineConfig> rechargeOnLineConfigList = null;
  			switch (device) {
			case 0:
				rechargeOnLineConfigList = rechargeOnLineConfigService.findByHql("from RechargeOnLineConfig o where o.station.id = "+stationId+" and o.isShowPC = 1 order by o.sortNum desc");
				break;
			case 1:
			case 2:
			case 3:
				rechargeOnLineConfigList = rechargeOnLineConfigService.findByHql("from RechargeOnLineConfig o where o.station.id = "+stationId+" and  o.isShowApp = 1 order by o.sortNum desc");
				break;
			}

  			List<RechargeOnLineConfigVo> data = new ArrayList<RechargeOnLineConfigVo>();
  			if(rechargeOnLineConfigList != null && rechargeOnLineConfigList.size() >0) {
  				message = "成功";
			 	for (RechargeOnLineConfig object : rechargeOnLineConfigList) {
			 		RechargeOnLineConfigVo vo = new RechargeOnLineConfigVo();
			 		vo.setTitle(object.getTitle());
			 		vo.setIconImg(StringUtils.isNotBlank(object.getIconImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getIconImg():"");
			 		vo.setMinAmount(object.getMinAmount().toString());
			 		vo.setMaxAmount(object.getMaxAmount().toString());
			 		vo.setReminder(StringUtils.isNotBlank(object.getReminder())?object.getReminder():"");
			 		vo.setProviderId(object.getId());
			 		vo.setIsAmountRequire(object.getIsAmountRequire());
			 		//vo.setProviderType(object.getProviderType());
			 		vo.setPayTypeJson(StringUtils.isNotBlank(object.getPayTypeJson())?JSONArray.fromObject(object.getPayTypeJson()):null);
			 		data.add(vo);
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

   /**提交网上充值*/
   @ResponseBody
   @RequestMapping(value = "/submitOnlineRecharge", method={RequestMethod.POST, RequestMethod.GET})
   @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
   @ApiOperation(httpMethod = "GET", value = "提交网上充值")//当前接口注解
   @ApiImplicitParams({
	   	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
   		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "provider_id", value = "提供商id,可在‘充值(网上充值)’接口返回中获取", required = true, dataType = "int"),
   		@ApiImplicitParam(paramType="query", name = "netway_code", value = "支付网关代码,可在‘充值(网上充值)’接口返回的‘支付方式json’中获取", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "amount", value = "充值金额，（单位：分）", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
   })
   public JsonResult submitOnlineRecharge(HttpServletRequest request, HttpServletResponse response,
		   	@RequestParam(value="station_id",required=false) Integer stationId, 
   			@RequestParam(value="timestamp") String timestamp, 
   			@RequestParam(value="version") String version, 
   			@RequestParam(value="sign") String sign, 
   			@RequestParam(value="device") Integer device,
   			@RequestParam(value="provider_id") Integer providerId, 
   			@RequestParam(value="netway_code") String netwayCode, 
   			@RequestParam(value="amount") String amount, 
   			@RequestParam(value="username") String username, 
   			@RequestParam(value="token") String token){
  		String methodName = "提交网上充值";
  		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("provider_id", providerId);
			parasMap.put("netway_code", netwayCode);
			parasMap.put("amount", amount);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			JSONObject jsonObject = rechargeService.submitOnlineRecharge(user,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device), providerId, netwayCode, amount);
  			String message = "提交成功";

  		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,jsonObject);
  		}catch (BusinessException e) {
  			LogUtil.info(methodName.concat("-处理信息异常"), e);
  			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
  		}catch (Exception e) {
  			LogUtil.info(methodName.concat("-系统错误"), e);
  			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
  		}
  	}
   
 	/**按游戏厅回水记录(需登录权限)*/
   @ResponseBody
 	@RequestMapping(value = "/waterRecordList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryWaterRecordVo.class)})
 	@ApiOperation(httpMethod = "GET", value = "按游戏厅回水记录(需登录权限)")//当前接口注解
 	@ApiImplicitParams({
 		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
 		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "lotteryType", value = "游戏类型、-1全部", required = false, dataType = "int"),
 		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
 		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
 		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
 	})
   public JsonResult waterRecordList(HttpServletRequest request, HttpServletResponse response,
		   @RequestParam(value="station_id",required=false) Integer stationId, 
		   @RequestParam(value="timestamp") String timestamp, 
		   @RequestParam(value="version") String version, 
		   @RequestParam(value="sign") String sign, 
		   @RequestParam(value="device") Integer device,
		   @RequestParam(value="lotteryType",required=false) Integer lotteryType,
		   @RequestParam(value="date_begin",required=false) String dateBegin,
		   @RequestParam(value="date_end",required=false) String dateEnd,
		   @RequestParam(value="username") String username, 
		   @RequestParam(value="token") String token,
		   @RequestParam(value="page") Integer page,
		   @RequestParam(value="limit") Integer limit){
		String methodName = "按游戏厅回水记录(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("lotteryType", lotteryType);
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("token", token);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("id", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			/** 开始时间*/
			if(StringUtils.isNotBlank(dateBegin)){
				sb.append(" and o.totalDate >=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryStartDate(dateBegin));
			}
	
			/** 截止时间*/
			if(StringUtils.isNotBlank(dateEnd)) {
				sb.append(" and o.totalDate <=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.queryEndDate(dateEnd));
			}
			
			if(lotteryType > 0) {
				sb.append(" and o.lotteryHall.lotteryType.id = ?");
				params.add(lotteryType);
			}
			
			sb.append(" and o.user.id = ?");
			params.add(user.getId());

			QueryResult<LotteryWaterRecord> qr = lotteryWaterRecordService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<LotteryWaterRecordVo> data = new ArrayList<LotteryWaterRecordVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (LotteryWaterRecord object : qr.getResultData()) {
			 		LotteryWaterRecordVo vo = new LotteryWaterRecordVo();
			 		vo.setId(object.getId());
			 		vo.setBackWaterMoney(object.getBackWaterMoney()!=null?object.getBackWaterMoney().toString():"");
			 		vo.setUserId(object.getUser().getId());
			 		vo.setUsername(object.getUser().getUsername());
			 		vo.setNickName(object.getUser().getNickName());
			 		vo.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", object.getTotalDate()));
			 		vo.setLotteryType(object.getLotteryHall().getLotteryType().getId());
			 		vo.setLotteryHallTitle(object.getLotteryHall().getLotteryType().getLotteryName());
			 		vo.setLotteryHallId(object.getLotteryHall().getId());
			 		vo.setLotteryHallTitle(object.getLotteryHall().getTitle());
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
   
   
   /**充值配置(三合一)*/
   @ResponseBody
	@RequestMapping(value = "/rechargeConfig", method={RequestMethod.POST, RequestMethod.GET})
   @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = RechargeOnLineConfigVo.class)})
   @ApiOperation(httpMethod = "GET", value = "充值配置(三合一)")//当前接口注解
   @ApiImplicitParams({
	   	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
   		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
   		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
   })
   public JsonResult rechargeConfig(HttpServletRequest request, HttpServletResponse response,
		   	@RequestParam(value="station_id",required=false) Integer stationId, 
   			@RequestParam(value="timestamp") String timestamp,
   			@RequestParam(value="username") String username, 
   			@RequestParam(value="token") String token,
   			@RequestParam(value="version") String version, 
   			@RequestParam(value="sign") String sign, 
   			@RequestParam(value="device") Integer device){
		String methodName = "充值配置(三合一)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
  			String message = "";

  			//网上充值
  			List<RechargeOnLineConfig> rechargeOnLineConfigList = null;
  			switch (device) {
			case 0:
				rechargeOnLineConfigList = rechargeOnLineConfigService.findByHql("from RechargeOnLineConfig o where o.station.id = "+stationId+" and o.isShowPC = 1 order by o.sortNum desc");
				break;
			case 1:
			case 2:
			case 3:
				rechargeOnLineConfigList = rechargeOnLineConfigService.findByHql("from RechargeOnLineConfig o where o.station.id = "+stationId+" and o.isShowApp = 1 order by o.sortNum desc");
				break;
			}

  			List<RechargeOnLineConfigVo> data1 = null;
  			if(rechargeOnLineConfigList != null && rechargeOnLineConfigList.size() >0) {
  				data1 = new ArrayList<RechargeOnLineConfigVo>();
			 	for (RechargeOnLineConfig object : rechargeOnLineConfigList) {
			 		RechargeOnLineConfigVo vo = new RechargeOnLineConfigVo();
			 		vo.setTitle(object.getTitle());
			 		vo.setIconImg(StringUtils.isNotBlank(object.getIconImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getIconImg():"");
			 		vo.setMinAmount(object.getMinAmount().toString());
			 		vo.setMaxAmount(object.getMaxAmount().toString());
			 		vo.setReminder(StringUtils.isNotBlank(object.getReminder())?object.getReminder():"");
			 		vo.setProviderId(object.getId());
			 		vo.setIsAmountRequire(object.getIsAmountRequire()); 
			 		//vo.setProviderType(object.getProviderType());
			 		vo.setPayTypeJson(StringUtils.isNotBlank(object.getPayTypeJson())?JSONArray.fromObject(object.getPayTypeJson()):null);
			 		data1.add(vo);
				}
  			}
  			
  			//2线下扫码
  			List<RechargeUnderLineConfig> rechargeUnderLineConfigList = null;
  			switch (device) {
			case 0:
				rechargeUnderLineConfigList = rechargeUnderLineConfigService.findByHql("from RechargeUnderLineConfig o where o.station.id = "+stationId+" and o.isShowPC = 1 order by o.sortNum desc");
				break;
			case 1:
			case 2:
			case 3:
				rechargeUnderLineConfigList = rechargeUnderLineConfigService.findByHql("from RechargeUnderLineConfig o where o.station.id = "+stationId+" and o.isShowApp = 1 order by o.sortNum desc");
				break;
			}

  			List<RechargeUnderLineConfigVo> data2 = null;
  			if(rechargeUnderLineConfigList != null && rechargeUnderLineConfigList.size() >0) {
  				data2 = new ArrayList<RechargeUnderLineConfigVo>();
			 	for (RechargeUnderLineConfig object : rechargeUnderLineConfigList) {
			 		RechargeUnderLineConfigVo vo = new RechargeUnderLineConfigVo();
			 		vo.setTitle(object.getTitle());
			 		vo.setIconImg(StringUtils.isNotBlank(object.getIconImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getIconImg():"");
			 		vo.setQrCodeLink(StringUtils.isNotBlank(object.getQrCodeLink())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getQrCodeLink():"");
			 		vo.setMinAmount(object.getMinAmount().toString());
			 		vo.setMaxAmount(object.getMaxAmount().toString());
			 		vo.setReminder(StringUtils.isNotBlank(object.getReminder())?object.getReminder():"");
			 		data2.add(vo);
				}
  			}
  			
  			//3网银转账
  			List<RechargeTransferConfig> rechargeTransferConfigList = null;
  			switch (device) {
			case 0:
				rechargeTransferConfigList = rechargeTransferConfigService.findByHql("from RechargeTransferConfig o where o.station.id = "+stationId+" and o.isShowPC = 1 order by o.sortNum desc");
				break;
			case 1:
			case 2:
			case 3:
				rechargeTransferConfigList = rechargeTransferConfigService.findByHql("from RechargeTransferConfig o where o.station.id = "+stationId+" and o.isShowApp = 1 order by o.sortNum desc");
				break;
			}

  			List<RechargeTransferConfigVo> data3 = null;
  			if(rechargeTransferConfigList != null && rechargeTransferConfigList.size() >0) {
  				data3 = new ArrayList<RechargeTransferConfigVo>();
			 	for (RechargeTransferConfig object : rechargeTransferConfigList) {
			 		RechargeTransferConfigVo vo = new RechargeTransferConfigVo();
			 		vo.setTitle(object.getTitle());
			 		vo.setIconImg(StringUtils.isNotBlank(object.getIconImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getIconImg():"");
			 		vo.setMinAmount(object.getMinAmount().toString());
			 		vo.setMaxAmount(object.getMaxAmount().toString());
			 		vo.setReminder(StringUtils.isNotBlank(object.getReminder())?object.getReminder():"");
			 		vo.setBankCard(object.getBankCard());
			 		vo.setBankName(object.getBankName());
			 		vo.setCardholder(object.getCardholder());
			 		vo.setOpenBankBranch(StringUtils.isNotBlank(object.getOpenBankBranch())?object.getOpenBankBranch():"");
			 		data3.add(vo);
				}
  			}
  			
  			Map<String, Object> result = new HashMap<String, Object>();
			result.put("data1", data1!=null?data1:"");
			result.put("data2", data2!=null?data2:"");
			result.put("data3", data3!=null?data3:"");
			
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
   }
   
   
   /**每日盈亏列表*/
   @ResponseBody
 	@RequestMapping(value = "/profitDailyList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryOrderTotalVo.class)})
 	@ApiOperation(httpMethod = "GET", value = "每日盈亏列表(需登录权限)")//当前接口注解
 	@ApiImplicitParams({
 		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
 		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
 		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
 		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
 		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
 	})
   public JsonResult profitDailyList(HttpServletRequest request, HttpServletResponse response,
   	@RequestParam(value="station_id",required=false) Integer stationId, 
 		@RequestParam(value="timestamp") String timestamp, 
 		@RequestParam(value="version") String version, 
 		@RequestParam(value="sign") String sign, 
 		@RequestParam(value="device") Integer device,
		@RequestParam(value="date_begin",required=false) String dateBegin,
		@RequestParam(value="date_end",required=false) String dateEnd,
 		@RequestParam(value="username") String username, 
 		@RequestParam(value="token") String token,
 		@RequestParam(value="page") Integer page,
 		@RequestParam(value="limit") Integer limit){
		String methodName = "每日盈亏列表(需登录权限)";
		try {
			//System.out.println(ApiUtil.getRequestUrl(request));
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			QueryResult<Object[]> qr = null;
			List<Object[]> totalList = null;
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("SELECT u.id,u.username,u.nick_name,SUM(o.bet_money) as bet_money,sum(o.expand_user_num) as expand_user_num ,sum(o.profit_money) as profit_money ,sum(o.recharge_money) as recharge_money ,sum(o.water_money) as water_money,sum(o.win_money) as win_money  ,sum(o.withdraw_money) as withdraw_money,date_format(o.total_date,'%Y-%m-%d') as total_date ");
			sqlBuffer.append("from tb_lottery_daily_order_total o,tb_member_user u where o.user_id = u.id and u.`status` = 1 ");
			
			StringBuffer totalSql = new StringBuffer();
			totalSql.append("SELECT SUM(o.bet_money) as bet_money,sum(o.expand_user_num) as expand_user_num ,sum(o.profit_money) as profit_money ,sum(o.recharge_money) as recharge_money ,sum(o.water_money) as water_money,sum(o.win_money) as win_money,sum(o.withdraw_money) as withdraw_money ");
			totalSql.append("from tb_lottery_daily_order_total o,tb_member_user u where o.user_id = u.id and u.`status` = 1 ");
			
			sqlBuffer.append(" and u.id = ").append(user.getId());
			totalSql.append(" and u.id = ").append(user.getId());
			
			//查询时间 开始时间
			if(StringUtils.isNotBlank(dateBegin)){
				sqlBuffer.append(" and o.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s')");
				totalSql.append(" and o.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s')");
			}
			
			//查询时间  结束时间
			if(StringUtils.isNotBlank(dateEnd)){
				sqlBuffer.append(" and o.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s')");
				totalSql.append(" and o.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s')");	
			}
			
			sqlBuffer.append(" GROUP BY date_format(o.total_date,'%Y-%m-%d'),u.id,u.username,u.nick_name ORDER BY date_format(o.total_date,'%Y-%m-%d') DESC");
			totalSql.append(" ");
			
			qr = lotteryOrderService.getScrollData((page-1)*limit, limit, sqlBuffer.toString());
			totalList = lotteryOrderService.findBySql(totalSql.toString());

			
			String message = "";
			Map<String, Object> result = null;
			List<LotteryOrderTotalVo> data = new ArrayList<LotteryOrderTotalVo>();
			Map<String, Object> total = new HashMap<String, Object>();
			if(qr!=null && qr.getResultCount()>0 && totalList!=null && totalList.size()>0) {
				message = "成功";
				for (Object[] objects : qr.getResultData()) {
					LotteryOrderTotalVo vo = new LotteryOrderTotalVo();
			 		vo.setUserId((Integer)objects[0]);
			 		vo.setUsername((String)objects[1]);
			 		vo.setNickName((String)objects[2]);
			 		vo.setBetMoney(((BigDecimal)objects[3]).toString());
			 		vo.setExpandUserNum(((BigDecimal)objects[4]).intValue());
			 		vo.setProfitMoney(((BigDecimal)objects[5]).toString());
			 		vo.setRechargeMoney(((BigDecimal)objects[6]).toString());
			 		vo.setBackWaterMoney(((BigDecimal)objects[7]).toString());
			 		vo.setWinMoney(((BigDecimal)objects[8]).toString());
			 		vo.setWithdrawMoney(((BigDecimal)objects[9]).toString());
			 		vo.setTotalDate((String)objects[10]);
			 		data.add(vo);
				}

				total.put("betMoney", totalList.get(0)[0]);
				total.put("expandUserNum", totalList.get(0)[1]);
				total.put("profitMoney", totalList.get(0)[2]);
				total.put("rechargeMoney", totalList.get(0)[3]);
				total.put("waterMoney", totalList.get(0)[4]);
				total.put("winMoney", totalList.get(0)[5]);
				total.put("withdrawMoney", totalList.get(0)[6]);
			}
			
		 	result = new HashMap<String, Object>();
			result.put("page", page);
			result.put("limit", limit);
			result.put("count", qr.getResultCount());
			result.put("total", total);
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
   
   /**获取在线客服信息(需登录权限)*/
   @ResponseBody
   @RequestMapping(value = "/getCustomerLinks", method={RequestMethod.POST, RequestMethod.GET})
   @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
   @ApiOperation(httpMethod = "GET", value = "获取在线客服信息(需登录权限)")//当前接口注解
   @ApiImplicitParams({
   	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
   	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
   	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
   	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
   	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
   	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
   	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
   })
   public JsonResult getCustomerLinks(HttpServletRequest request, HttpServletResponse response,
   		@RequestParam(value="station_id",required=false) Integer stationId, 
   		@RequestParam(value="timestamp") String timestamp, 
   		@RequestParam(value="version") String version, 
   		@RequestParam(value="sign") String sign, 
   		@RequestParam(value="device") Integer device, 
   		@RequestParam(value="username") String username, 
   		@RequestParam(value="token") String token){
  		String methodName = "获取在线客服信息(需登录权限)";
  		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.getUserInfo((String)parasMap.get("username"), token,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
			
			//玩家类型(0游客、1玩家、2代理、3推广员、4虚拟号)
			User parent = null;
			if(user!=null &&(user.getUserType()==0 || user.getUserType()==1 || user.getUserType()==4) 
					&& user.getParent() != null && user.getParent().getParent() != null) {
				parent = user.getParent().getParent();
			}else if(user!=null && user.getUserType()==2) {
				parent = user;
			}else if(user!=null && user.getUserType()==3 && user.getParent() != null) {
				parent = user.getParent();
			}

			//System.out.println(parent.getId() + "..." + parent.getUsername());
			if(parent != null) {
				UserOnline userOnline = userOnlineService.findUserOnline(parent.getId());
				if(userOnline !=null && userOnline.getIsOpen() != null) {
		  			Map<String, Object> result = new HashMap<String, Object>();
		  			result.put("isOpen", userOnline.getIsOpen());
		  			result.put("customerLinks", userOnline.getCustomerLinks());
		  			result.put("onlineInfo", userOnline.getOnlineInfo());
		  			result.put("agentUsername", parent.getUsername());
		  			result.put("agentNickName", parent.getNickName());
		  			result.put("rechargeTotal", rechargeService.sumAllRechargeAmount(user, user.getCreateTime(), new Date()));
		  			
					String message = "成功";
		  		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
				}
			}
			
			throw new BusinessException(ApiUtil.getErrorCode("105"));
  		}catch (BusinessException e) {
  			LogUtil.info(methodName.concat("-处理信息异常"), e);
  			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
  		}catch (Exception e) {
  			LogUtil.info(methodName.concat("-系统错误"), e);
  			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
  		}
  	}
}
