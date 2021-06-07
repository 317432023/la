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

import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.LotteryOrderItem;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.bean.member.User;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.model.page.PageView;
import com.jeetx.common.model.page.QueryResult;
import com.jeetx.common.redis.JedisClient;
import com.jeetx.common.swagger.model.JsonResult;
import com.jeetx.common.swagger.model.Member.TransRecordVo;
import com.jeetx.common.swagger.model.Member.UserVo;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemVo;
import com.jeetx.common.swagger.model.lottery.LotteryOrderTotalVo;
import com.jeetx.common.swagger.model.lottery.LotteryOrderVo;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.member.BankCardService;
import com.jeetx.service.member.LetterService;
import com.jeetx.service.member.RechargeService;
import com.jeetx.service.member.TransRecordService;
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

@CrossOrigin
@Controller
@RequestMapping("/api/team")
@Api(tags = "团队信息服务接口(参数URLEncoder后提交)") //swagger分类标题注解
public class TeamController {
	
    @Autowired JedisClient jedisClient;
	@Autowired UserService userService;
	@Autowired BankCardService bankCardService;
	@Autowired LetterService letterService;
	@Autowired TransRecordService transRecordService;
	@Autowired WithdrawService withdrawService;
	@Autowired LotteryOrderService lotteryOrderService;
	@Autowired RechargeService rechargeService;
	@Autowired StationService stationService;
	
	@Value("${developMode}")
	private Boolean developMode;
	
    
  	/**下级推广员列表(仅代理有权限)*/
    @ResponseBody
  	@RequestMapping(value = "/promoterList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = UserVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "下级推广员列表(仅代理有权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "team_username", value = "用户名查询", required = false, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult promoterList(HttpServletRequest request, HttpServletResponse response,
    	@RequestParam(value="station_id",required=false) Integer stationId, 
  		@RequestParam(value="timestamp") String timestamp, 
  		@RequestParam(value="version") String version, 
  		@RequestParam(value="sign") String sign, 
  		@RequestParam(value="device") Integer device,
 		@RequestParam(value="team_username",required=false) String teamUsername,
  		@RequestParam(value="username") String username, 
  		@RequestParam(value="token") String token,
  		@RequestParam(value="page") Integer page,
  		@RequestParam(value="limit") Integer limit){
		String methodName = "下级推广员列表(仅代理有权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("team_username", teamUsername);
			parasMap.put("token", token);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			if(user.getUserType()!=2) {//0游客、1玩家、2代理、3推广员、4虚拟号
				throw new BusinessException(ApiUtil.getErrorCode("120"));
			}

			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("id", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			sb.append(" and o.status = ?");
			params.add(1);

			sb.append(" and o.userType = ?");
			params.add(3);
			
			sb.append(" and o.station.id = ?");
			params.add(stationId);
			
			sb.append(" and o.parent.id = ?");
			params.add(user.getId());
			
			if(StringUtils.isNotBlank(teamUsername)) {
				sb.append(" and o.username like ?");
				params.add("%"+teamUsername+"%");
			}

			QueryResult<User> qr = userService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<UserVo> data = new ArrayList<UserVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (User object : qr.getResultData()) {
			 		UserVo vo = new UserVo();
			 		vo.setId(object.getId());
			 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
			 		vo.setUserType(object.getUserType());
			 		vo.setUsername(object.getUsername());
			 		vo.setExpandUserNum(userService.getExpandUserNum(object.getId()).intValue());
			 		vo.setParent(object.getParent()!=null?object.getParent().getUsername():"");
			 		vo.setNickName(StringUtils.isNotBlank(object.getNickName())?object.getNickName():"");
			 		vo.setBalance(object.getBalance().toString());
			 		vo.setLotteryBalance(object.getLotteryBalance().toString());
			 		
			 		BigDecimal balance = new BigDecimal("0");
			 		if(object.getChildUsers()!=null && object.getChildUsers().size()>0) {
			 			for (User u : object.getChildUsers()) {
							if(u!=null && u.getUserType() == 1) {
								balance = balance.add(u.getBalance());
							}
						}
			 		}
			 		vo.setBalance(balance.toString());
			 		
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
    
    
    /**下级虚拟号列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/virtualUserList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = UserVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "下级虚拟号列表(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "team_username", value = "用户名查询", required = false, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult virtualUserList(HttpServletRequest request, HttpServletResponse response,
    	@RequestParam(value="station_id",required=false) Integer stationId, 
  		@RequestParam(value="timestamp") String timestamp, 
  		@RequestParam(value="version") String version, 
  		@RequestParam(value="sign") String sign, 
  		@RequestParam(value="device") Integer device,
 		@RequestParam(value="team_username",required=false) String teamUsername,
  		@RequestParam(value="username") String username, 
  		@RequestParam(value="token") String token,
  		@RequestParam(value="page") Integer page,
  		@RequestParam(value="limit") Integer limit){
		String methodName = "下级虚拟号列表(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("team_username", teamUsername);
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
			
			sb.append(" and o.status = ?");
			params.add(1);

			sb.append(" and o.userType = ?");
			params.add(4);
			
			sb.append(" and o.station.id = ?");
			params.add(stationId);
			
			if(user.getUserType()==2) {//0游客、1玩家、2代理、3推广员、4虚拟号
				sb.append(" and o.parent.parent.id = ?");
				params.add(user.getId());
			}else if(user.getUserType()==3) {
				sb.append(" and o.parent.id = ?");
				params.add(user.getId());
			}else {
				throw new BusinessException(ApiUtil.getErrorCode("120"));
			}
			
			if(StringUtils.isNotBlank(teamUsername)) {
				sb.append(" and o.username like ?");
				params.add("%"+teamUsername+"%");
			}

			QueryResult<User> qr = userService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<UserVo> data = new ArrayList<UserVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (User object : qr.getResultData()) {
			 		UserVo vo = new UserVo();
			 		vo.setId(object.getId());
			 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
			 		vo.setUserType(object.getUserType());
			 		vo.setUsername(object.getUsername());
			 		vo.setParent(object.getParent()!=null?object.getParent().getUsername():"");
			 		vo.setNickName(StringUtils.isNotBlank(object.getNickName())?object.getNickName():"");
			 		vo.setBalance(object.getBalance().toString());
			 		vo.setLotteryBalance(object.getLotteryBalance().toString());
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
  
    
    /**下级玩家列表(仅推广员有权限)*/
    @ResponseBody
  	@RequestMapping(value = "/playerList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = UserVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "下级玩家列表(仅推广员有权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "team_username", value = "用户名查询", required = false, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult playerList(HttpServletRequest request, HttpServletResponse response,
    	@RequestParam(value="station_id",required=false) Integer stationId, 
  		@RequestParam(value="timestamp") String timestamp, 
  		@RequestParam(value="version") String version, 
  		@RequestParam(value="sign") String sign, 
  		@RequestParam(value="device") Integer device,
 		@RequestParam(value="team_username",required=false) String teamUsername,
  		@RequestParam(value="username") String username, 
  		@RequestParam(value="token") String token,
  		@RequestParam(value="page") Integer page,
  		@RequestParam(value="limit") Integer limit){
		String methodName = "下级玩家列表(仅推广员有权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("team_username", teamUsername);
			parasMap.put("token", token);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			if(user.getUserType()!=3) {//0游客、1玩家、2代理、3推广员、4虚拟号
				throw new BusinessException(ApiUtil.getErrorCode("120"));
			}
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("id", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			sb.append(" and o.status = ?");
			params.add(1);

			sb.append(" and o.userType = ?");
			params.add(1);
			
			sb.append(" and o.station.id = ?");
			params.add(stationId);
			
			sb.append(" and o.parent.id = ?");
			params.add(user.getId());

			if(StringUtils.isNotBlank(teamUsername)) {
				sb.append(" and o.username like ?");
				params.add("%"+teamUsername+"%");
			}

			QueryResult<User> qr = userService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<UserVo> data = new ArrayList<UserVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (User object : qr.getResultData()) {
			 		UserVo vo = new UserVo();
			 		vo.setId(object.getId());
			 		vo.setCreateTime(DateTimeTool.dateFormat(null, object.getCreateTime()));
			 		vo.setUserType(object.getUserType());
			 		vo.setUsername(object.getUsername());
			 		vo.setParent(object.getParent()!=null?object.getParent().getUsername():"");
			 		vo.setNickName(StringUtils.isNotBlank(object.getNickName())?object.getNickName():"");
			 		vo.setBalance(object.getBalance().toString());
			 		vo.setLotteryBalance(object.getLotteryBalance().toString());
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
    
  	/**团队投注列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/lotteryOrderList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryOrderVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "团队投注列表(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "status", value = "1待开奖、2已开奖、3已取消、-1全部", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "lotteryType", value = "游戏类型、-1全部", required = false, dataType = "int"),
		@ApiImplicitParam(paramType="query", name = "team_username", value = "用户名查询", required = false, dataType = "string"),
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
 		@RequestParam(value="team_username",required=false) String teamUsername,
 		@RequestParam(value="lotteryType",required=false) Integer lotteryType,
 		@RequestParam(value="date_begin",required=false) String dateBegin,
 		@RequestParam(value="date_end",required=false) String dateEnd,
  		@RequestParam(value="username") String username, 
  		@RequestParam(value="token") String token,
  		@RequestParam(value="page") Integer page,
  		@RequestParam(value="limit") Integer limit){
		String methodName = "团队投注列表(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("status", status);
			parasMap.put("team_username", teamUsername);
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
			
			//列表玩家
			sb.append(" and o.user.userType = ?");
			params.add(1);
			
			if(status > 0) {
				sb.append(" and o.status = ?");
				params.add(status);
			}
			
			if(lotteryType > 0) {
				sb.append(" and o.lotteryType.id = ?");
				params.add(lotteryType);
			}
			
			sb.append(" and o.user.station.id = ?");
			params.add(stationId);
			
			if(user.getUserType()==2) {//0游客、1玩家、2代理、3推广员、4虚拟号
				sb.append(" and o.user.parent.parent.id = ?");
				params.add(user.getId());
			}else if(user.getUserType()==3) {
				sb.append(" and o.user.parent.id = ?");
				params.add(user.getId());
			}else {
				throw new BusinessException(ApiUtil.getErrorCode("120"));
			}
			
			if(StringUtils.isNotBlank(teamUsername)) {
				sb.append(" and o.user.username like ?");
				params.add("%"+teamUsername+"%");
			}


			QueryResult<LotteryOrder> qr = lotteryOrderService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<LotteryOrderVo> data = new ArrayList<LotteryOrderVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (LotteryOrder object : qr.getResultData()) {
			 		LotteryOrderVo vo = new LotteryOrderVo();
			 		vo.setId(object.getId());
			 		vo.setUserId(object.getUser().getId());
			 		vo.setUsername(object.getUser().getUsername());
			 		vo.setNickName(object.getUser().getNickName());
			 		vo.setOrderCode(object.getOrderCode());
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
    
    /**团队账变列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/transRecordList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = TransRecordVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "团队账变列表(需登录权限)")//当前接口注解
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
		@ApiImplicitParam(paramType="query", name = "team_username", value = "用户名查询", required = false, dataType = "string"),
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
 		@RequestParam(value="team_username",required=false) String teamUsername,
 		@RequestParam(value="date_begin",required=false) String dateBegin,
 		@RequestParam(value="date_end",required=false) String dateEnd,
  		@RequestParam(value="username") String username, 
  		@RequestParam(value="token") String token,
  		@RequestParam(value="page") Integer page,
  		@RequestParam(value="limit") Integer limit){
		String methodName = "团队账变列表(需登录权限)";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("status", status);
			parasMap.put("team_username", teamUsername);
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
			
			//列表玩家
			sb.append(" and o.user.userType = ?");
			params.add(1);
			
			sb.append(" and o.user.station.id = ?");
			params.add(stationId);
		
			if(status > 0) {
				sb.append(" and o.transCategory = ?");
				params.add(status);
			}
			
			if(user.getUserType()==2) {//0游客、1玩家、2代理、3推广员、4虚拟号
				sb.append(" and o.user.parent.parent.id = ?");
				params.add(user.getId());
			}else if(user.getUserType()==3) {
				sb.append(" and o.user.parent.id = ?");
				params.add(user.getId());
			}else {
				throw new BusinessException(ApiUtil.getErrorCode("120"));
			}
			
			if(StringUtils.isNotBlank(teamUsername)) {
				sb.append(" and o.user.username like ?");
				params.add("%"+teamUsername+"%");
			}

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
    
    
    /**团队盈亏列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/profitList", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryOrderTotalVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "团队盈亏列表(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
		@ApiImplicitParam(paramType="query", name = "team_username", value = "用户名查询", required = false, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult profitList(HttpServletRequest request, HttpServletResponse response,
    	@RequestParam(value="station_id",required=false) Integer stationId, 
  		@RequestParam(value="timestamp") String timestamp, 
  		@RequestParam(value="version") String version, 
  		@RequestParam(value="sign") String sign, 
  		@RequestParam(value="device") Integer device,
 		@RequestParam(value="team_username",required=false) String teamUsername,
 		@RequestParam(value="date_begin",required=false) String dateBegin,
 		@RequestParam(value="date_end",required=false) String dateEnd,
  		@RequestParam(value="username") String username, 
  		@RequestParam(value="token") String token,
  		@RequestParam(value="page") Integer page,
  		@RequestParam(value="limit") Integer limit){
		String methodName = "团队盈亏列表(需登录权限)";
		try {
			//System.out.println(ApiUtil.getRequestUrl(request));
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("team_username", teamUsername);
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
			
			Integer expandUserNum = 0;
			BigDecimal betMoney = new BigDecimal(0);
			BigDecimal profitMoney = new BigDecimal(0);
			BigDecimal rechargeMoney = new BigDecimal(0);
			BigDecimal withdrawMoney = new BigDecimal(0);
			BigDecimal waterMoney = new BigDecimal(0);
			BigDecimal winMoney = new BigDecimal(0);
			BigDecimal balance = new BigDecimal(0);
			BigDecimal lotteryBalance = new BigDecimal(0);
			QueryResult<Object[]> qr = null;
			
			if(StringUtils.isBlank(dateBegin))
				dateBegin = DateTimeTool.dateFormat("yyyy-MM-dd", new Date());
			if(StringUtils.isBlank(dateEnd))
				dateEnd = DateTimeTool.dateFormat("yyyy-MM-dd", new Date());
			
			if(user.getUserType()==2) {//0游客、1玩家、2代理、3推广员、4虚拟号
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("SELECT p.id,p.username,p.nick_name,SUM(t.bet_money) as bet_money,e.expand_user_num as expand_user_num,sum(t.profit_money) as profit_money ,sum(t.recharge_money) as recharge_money,sum(t.water_money) as water_money,sum(t.win_money) as win_money,sum(t.withdraw_money) as withdraw_money,sum(b.balance) as balance,sum(b.lottery_balance) as lottery_balance from");
				sqlBuffer.append(" (SELECT p.id,p.parent_id,p.username,p.nick_name,u.id as uid,p.station_id from tb_member_user u,tb_member_user p where u.parent_id = p.id and u.user_type = 1 ) p ");
				
				sqlBuffer.append(" LEFT JOIN (SELECT t1.balance as balance,t1.user_id as user_id,t1.lottery_balance as lottery_balance from tb_lottery_daily_order_total t1 where t1.total_date = str_to_date((SELECT MAX(t2.total_date) from tb_lottery_daily_order_total t2 where t1.user_id = t2.user_id and t2.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY t2.user_id ORDER BY t2.total_date),'%Y-%m-%d %H:%i:%s')) b");
				sqlBuffer.append(" on p.uid = b.user_id");
				
				sqlBuffer.append(" LEFT JOIN  (SELECT sum(o1.expand_user_num) as expand_user_num,o1.user_id from tb_lottery_daily_order_total o1 where  o1.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o1.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s')GROUP BY o1.user_id) e");
				sqlBuffer.append(" on p.id = e.user_id");
				
				sqlBuffer.append(" LEFT JOIN (SELECT o.user_id,SUM(o.bet_money) as bet_money,sum(o.profit_money) as profit_money ,sum(o.recharge_money) as recharge_money,sum(o.water_money) as water_money,sum(o.win_money) as win_money,sum(o.withdraw_money) as withdraw_money from");
				sqlBuffer.append(" tb_lottery_daily_order_total o where o.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY o.user_id ORDER BY SUM(o.profit_money) DESC) t ");
				sqlBuffer.append(" on t.user_id = p.uid");
				
				sqlBuffer.append(" where p.parent_id = ").append(user.getId());
				sqlBuffer.append(" and p.station_id = ").append(stationId);
				if(StringUtils.isNotBlank(teamUsername)) {
					sqlBuffer.append(" and p.username like '%").append(teamUsername).append("%'");
				}
				
				sqlBuffer.append(" GROUP BY p.id");
				
				System.out.println(sqlBuffer.toString());
				qr = lotteryOrderService.getScrollData((page-1)*limit, limit, sqlBuffer.toString());
			}else if(user.getUserType()==3) {
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("SELECT u.id,u.username,u.nick_name,SUM(t.bet_money) as bet_money,sum(t.expand_user_num) as expand_user_num,sum(t.profit_money) as profit_money ,sum(t.recharge_money) as recharge_money,sum(t.water_money) as water_money,sum(t.win_money) as win_money,sum(t.withdraw_money) as withdraw_money,sum(b.balance) as balance,sum(b.lottery_balance) as lottery_balance from");
				sqlBuffer.append(" (SELECT u.id,u.parent_id,u.username,u.nick_name,u.station_id from tb_member_user u where u.user_type = 1 ) u");
				
				sqlBuffer.append(" LEFT JOIN (SELECT t1.balance as balance,t1.user_id as user_id,t1.lottery_balance as lottery_balance from tb_lottery_daily_order_total t1 where t1.total_date = str_to_date((SELECT MAX(t2.total_date) from tb_lottery_daily_order_total t2 where t1.user_id = t2.user_id and t2.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY t2.user_id ORDER BY t2.total_date),'%Y-%m-%d %H:%i:%s')) b");
				sqlBuffer.append(" on u.id = b.user_id ");
				
				sqlBuffer.append(" LEFT JOIN (SELECT o.user_id,SUM(o.bet_money) as bet_money,sum(o.expand_user_num) as expand_user_num,sum(o.profit_money) as profit_money ,sum(o.recharge_money) as recharge_money,sum(o.water_money) as water_money,sum(o.win_money) as win_money,sum(o.withdraw_money) as withdraw_money from");
				sqlBuffer.append(" tb_lottery_daily_order_total o where o.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY o.user_id ORDER BY SUM(o.profit_money) DESC) t");
				sqlBuffer.append(" on t.user_id = u.id ");
				
				sqlBuffer.append(" where u.station_id = ").append(stationId);
				sqlBuffer.append(" and u.parent_id = ").append(user.getId());
				if(StringUtils.isNotBlank(teamUsername)) {
					sqlBuffer.append(" and u.username like '%").append(teamUsername).append("%'");
				}
				
				sqlBuffer.append(" GROUP BY u.id");
				
				//System.out.println(sqlBuffer.toString());
				qr = lotteryOrderService.getScrollData((page-1)*limit, limit, sqlBuffer.toString());
			}else {
				throw new BusinessException(ApiUtil.getErrorCode("120"));
			}
			
			String message = "";
			Map<String, Object> result = null;
			List<LotteryOrderTotalVo> data = new ArrayList<LotteryOrderTotalVo>();
			Map<String, Object> total = new HashMap<String, Object>();
			if(qr!=null && qr.getResultCount()>0) {
				message = "成功";
				for (Object[] objects : qr.getResultData()) {
					LotteryOrderTotalVo vo = new LotteryOrderTotalVo();
			 		vo.setUserId((Integer)objects[0]);
			 		vo.setUsername((String)objects[1]);
			 		vo.setNickName((String)objects[2]);
			 		vo.setBetMoney(objects[3]!=null?((BigDecimal)objects[3]).toString():"0");
			 		vo.setExpandUserNum(objects[4]!=null?((BigDecimal)objects[4]).intValue():0);
			 		//vo.setProfitMoney(objects[5]!=null?((BigDecimal)objects[5]).toString():"0");
			 		vo.setRechargeMoney(objects[6]!=null?((BigDecimal)objects[6]).toString():"0");
			 		vo.setBackWaterMoney(objects[7]!=null?((BigDecimal)objects[7]).toString():"0");
			 		vo.setWinMoney(objects[8]!=null?((BigDecimal)objects[8]).toString():"0");
			 		vo.setWithdrawMoney(objects[9]!=null?((BigDecimal)objects[9]).toString():"0");
			 		vo.setBalance(objects[10]!=null?((BigDecimal)objects[10]).toString():"0");
			 		vo.setLotteryBalance(objects[11]!=null?((BigDecimal)objects[11]).toString():"0");
			 		vo.setProfitMoney(new BigDecimal(vo.getRechargeMoney()).subtract(new BigDecimal(vo.getWithdrawMoney())).toString());
			 		data.add(vo);
			 		
			 		betMoney = betMoney.add(new BigDecimal(vo.getBetMoney()));
			 		expandUserNum = expandUserNum + vo.getExpandUserNum();
			 		profitMoney = profitMoney.add(new BigDecimal(vo.getProfitMoney()));
			 		rechargeMoney = rechargeMoney.add(new BigDecimal(vo.getRechargeMoney()));
			 		withdrawMoney = withdrawMoney.add(new BigDecimal(vo.getWithdrawMoney()));
			 		waterMoney = waterMoney.add(new BigDecimal(vo.getBackWaterMoney()));
			 		winMoney = winMoney.add(new BigDecimal(vo.getWinMoney()));
			 		balance = balance.add(new BigDecimal(vo.getBalance()));
			 		lotteryBalance = lotteryBalance.add(new BigDecimal(vo.getLotteryBalance()));
				}

				total.put("betMoney", betMoney);
				total.put("expandUserNum", expandUserNum);
				total.put("profitMoney", profitMoney);
				total.put("rechargeMoney", rechargeMoney);
				total.put("withdrawMoney", withdrawMoney);
				total.put("waterMoney", waterMoney);
				total.put("winMoney", winMoney);
				total.put("balance", balance);
				total.put("lotteryBalance", lotteryBalance);
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
    
    /**新增推广号(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/addSalesman", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "新增推广号(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "add_username", value = "需新增用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "add_password", value = "需新增用户登陆密码，两次MD5加密后的结果，结果小写", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult addSalesman(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="add_username") String addUsername, 
    		@RequestParam(value="add_password") String addPassword,
    		@RequestParam(value="token") String token){
   		String methodName = "新增推广号(需登录权限)";
   		try {
   			//System.out.println(nickname);
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("add_username", addUsername);
			parasMap.put("add_password", addPassword);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			userService.addSalesman(user,(String)parasMap.get("add_username"),(String)parasMap.get("add_password"),IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
			
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
    
    /**新增虚拟号(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/addVirtualUser", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "新增虚拟号(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "add_username", value = "需新增用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "add_password", value = "需新增用户登陆密码，两次MD5加密后的结果，结果小写", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "is_init_order", value = "是否初始虚拟交易记录(1：是、0或空：否)", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "card_json", value = "is_init_order=1时有效，银行卡json,格式：{bankName:\"中国建设银行\",cardholder:\"张三\",cardNo:\"12345678912344\",bankPlace:\"河北石家庄\",bankBranch:\"保定支行\"}", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult addVirtualUser(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
     		@RequestParam(value="is_init_order",required=false) String isInitOrder,
    		@RequestParam(value="card_json",required=false) String cardJson, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="add_username") String addUsername, 
    		@RequestParam(value="add_password") String addPassword,
    		@RequestParam(value="token") String token){
   		String methodName = "新增虚拟号(需登录权限)";
   		try {
   			//System.out.println(nickname);
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("is_init_order", isInitOrder);
			parasMap.put("card_json", cardJson);
			parasMap.put("add_username", addUsername);
			parasMap.put("add_password", addPassword);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			userService.addVirtualUser(user,(String)parasMap.get("add_username"),(String)parasMap.get("add_password"),(String)parasMap.get("is_init_order"),(String)parasMap.get("card_json"),
					IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
			
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
    
    /**注销下级账号(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/disableUser", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "注销下级账号(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "child_username", value = "下级用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult disableUser(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="child_username") String childUsername, 
    		@RequestParam(value="token") String token){
   		String methodName = "注销下级账号(需登录权限)";
   		try {
   			//System.out.println(nickname);
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("child_username", childUsername);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			userService.disableUser(user,(String)parasMap.get("child_username"),IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
			
   			String message = "操作成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**虚拟号充值(需登录权限)*/
    @ResponseBody
    @RequestMapping(value = "/virtualUserRecharge", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "虚拟号充值(需登录权限)")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
      	@ApiImplicitParam(paramType="query", name = "type", value = "充值方式(1账户充值、2彩金充值)", required = true, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "child_username", value = "下级用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "amount", value = "充值金额(负数进行充减)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult virtualUserRecharge(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device,
    		@RequestParam(value="type") Integer type, 
    		@RequestParam(value="amount") String amount,
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="child_username") String childUsername, 
    		@RequestParam(value="token") String token){
   		String methodName = "虚拟号充值(需登录权限)";
   		try {
   			//System.out.println(nickname);
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("amount", amount);
			parasMap.put("type", type);
			parasMap.put("child_username", childUsername);
			parasMap.put("token", token);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			rechargeService.virtualUserRecharge(user,(String)parasMap.get("child_username"),new BigDecimal(amount),type,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
   			String message = "充值成功";

   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    /**团队信息统计(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/teamTotal", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
  	@ApiOperation(httpMethod = "GET", value = "团队信息统计(需登录权限)")//当前接口注解
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
  	})
    public JsonResult teamTotal(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
	  		@RequestParam(value="timestamp") String timestamp, 
	  		@RequestParam(value="version") String version, 
	  		@RequestParam(value="sign") String sign, 
	  		@RequestParam(value="device") Integer device,
	 		@RequestParam(value="date_begin",required=false) String dateBegin,
	 		@RequestParam(value="date_end",required=false) String dateEnd,
	  		@RequestParam(value="username") String username, 
	  		@RequestParam(value="token") String token){
		String methodName = "团队信息统计(需登录权限)";
		try {
			//System.out.println(ApiUtil.getRequestUrl(request));
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("token", token);
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			User user = userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			
			Integer expandUserNum = 0;
			BigDecimal betMoney = new BigDecimal(0);
			BigDecimal profitMoney = new BigDecimal(0);
			BigDecimal rechargeMoney = new BigDecimal(0);
			BigDecimal withdrawMoney = new BigDecimal(0);
			BigDecimal waterMoney = new BigDecimal(0);
			BigDecimal winMoney = new BigDecimal(0);
			BigDecimal balance = new BigDecimal(0);
			BigDecimal lotteryBalance = new BigDecimal(0);
			QueryResult<Object[]> qr = null;
			
			if(StringUtils.isBlank(dateBegin))
				dateBegin = DateTimeTool.dateFormat("yyyy-MM-dd", new Date());
			if(StringUtils.isBlank(dateEnd))
				dateEnd = DateTimeTool.dateFormat("yyyy-MM-dd", new Date());
			
			if(user.getUserType()==2) {//0游客、1玩家、2代理、3推广员、4虚拟号
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("SELECT p.id,p.username,p.nick_name,SUM(t.bet_money) as bet_money,e.expand_user_num as expand_user_num,sum(t.profit_money) as profit_money ,sum(t.recharge_money) as recharge_money,sum(t.water_money) as water_money,sum(t.win_money) as win_money,sum(t.withdraw_money) as withdraw_money,sum(b.balance) as balance,sum(b.lottery_balance) as lottery_balance from");
				sqlBuffer.append(" (SELECT p.id,p.parent_id,p.username,p.nick_name,u.id as uid,p.station_id from tb_member_user u,tb_member_user p where u.parent_id = p.id and u.`status` = 1 and u.user_type = 1 ) p ");
				
				sqlBuffer.append(" LEFT JOIN (SELECT t1.balance as balance,t1.user_id as user_id,t1.lottery_balance as lottery_balance from tb_lottery_daily_order_total t1 where t1.total_date = str_to_date((SELECT MAX(t2.total_date) from tb_lottery_daily_order_total t2 where t1.user_id = t2.user_id and t2.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY t2.user_id ORDER BY t2.total_date),'%Y-%m-%d %H:%i:%s')) b");
				sqlBuffer.append(" on p.uid = b.user_id");
				
				sqlBuffer.append(" LEFT JOIN  (SELECT sum(o1.expand_user_num) as expand_user_num,o1.user_id from tb_lottery_daily_order_total o1 where  o1.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o1.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s')GROUP BY o1.user_id) e");
				sqlBuffer.append(" on p.id = e.user_id");
				
				sqlBuffer.append(" LEFT JOIN (SELECT o.user_id,SUM(o.bet_money) as bet_money,sum(o.profit_money) as profit_money ,sum(o.recharge_money) as recharge_money,sum(o.water_money) as water_money,sum(o.win_money) as win_money,sum(o.withdraw_money) as withdraw_money from");
				sqlBuffer.append(" tb_lottery_daily_order_total o where o.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY o.user_id ORDER BY SUM(o.profit_money) DESC) t ");
				sqlBuffer.append(" on t.user_id = p.uid");
				
				sqlBuffer.append(" where p.parent_id = ").append(user.getId());
				sqlBuffer.append(" and p.station_id = ").append(stationId);
				sqlBuffer.append(" GROUP BY p.id");
				
				qr = lotteryOrderService.getScrollData(-1, -1, sqlBuffer.toString());
			}else if(user.getUserType()==3) {
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("SELECT u.id,u.username,u.nick_name,SUM(t.bet_money) as bet_money,sum(t.expand_user_num) as expand_user_num,sum(t.profit_money) as profit_money ,sum(t.recharge_money) as recharge_money,sum(t.water_money) as water_money,sum(t.win_money) as win_money,sum(t.withdraw_money) as withdraw_money,sum(b.balance) as balance,sum(b.lottery_balance) as lottery_balance from");
				sqlBuffer.append(" (SELECT u.id,u.parent_id,u.username,u.nick_name,u.station_id from tb_member_user u where u.`status` = 1 and u.user_type = 1 ) u");
				
				sqlBuffer.append(" LEFT JOIN (SELECT t1.balance as balance,t1.user_id as user_id,t1.lottery_balance as lottery_balance from tb_lottery_daily_order_total t1 where t1.total_date = str_to_date((SELECT MAX(t2.total_date) from tb_lottery_daily_order_total t2 where t1.user_id = t2.user_id and t2.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY t2.user_id ORDER BY t2.total_date),'%Y-%m-%d %H:%i:%s')) b");
				sqlBuffer.append(" on u.id = b.user_id ");
				
				sqlBuffer.append(" LEFT JOIN (SELECT o.user_id,SUM(o.bet_money) as bet_money,sum(o.expand_user_num) as expand_user_num,sum(o.profit_money) as profit_money ,sum(o.recharge_money) as recharge_money,sum(o.water_money) as water_money,sum(o.win_money) as win_money,sum(o.withdraw_money) as withdraw_money from");
				sqlBuffer.append(" tb_lottery_daily_order_total o where o.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY o.user_id ORDER BY SUM(o.profit_money) DESC) t");
				sqlBuffer.append(" on t.user_id = u.id ");
				
				sqlBuffer.append(" where u.station_id = ").append(stationId);
				sqlBuffer.append(" and u.parent_id = ").append(user.getId());
				sqlBuffer.append(" GROUP BY u.id");
				
				qr = lotteryOrderService.getScrollData(-1, -1, sqlBuffer.toString());
			}else {
				throw new BusinessException(ApiUtil.getErrorCode("120"));
			}
			
			String message = "";
			Map<String, Object> result = null;
			List<LotteryOrderTotalVo> data = new ArrayList<LotteryOrderTotalVo>();
			Map<String, Object> total = new HashMap<String, Object>();
			if(qr!=null && qr.getResultCount()>0) {
				message = "成功";
				for (Object[] objects : qr.getResultData()) {
					LotteryOrderTotalVo vo = new LotteryOrderTotalVo();
			 		vo.setUserId((Integer)objects[0]);
			 		vo.setUsername((String)objects[1]);
			 		vo.setNickName((String)objects[2]);
			 		vo.setBetMoney(objects[3]!=null?((BigDecimal)objects[3]).toString():"0");
			 		vo.setExpandUserNum(objects[4]!=null?((BigDecimal)objects[4]).intValue():0);
			 		//vo.setProfitMoney(objects[5]!=null?((BigDecimal)objects[5]).toString():"0");
			 		vo.setRechargeMoney(objects[6]!=null?((BigDecimal)objects[6]).toString():"0");
			 		vo.setBackWaterMoney(objects[7]!=null?((BigDecimal)objects[7]).toString():"0");
			 		vo.setWinMoney(objects[8]!=null?((BigDecimal)objects[8]).toString():"0");
			 		vo.setWithdrawMoney(objects[9]!=null?((BigDecimal)objects[9]).toString():"0");
			 		vo.setBalance(objects[10]!=null?((BigDecimal)objects[10]).toString():"0");
			 		vo.setLotteryBalance(objects[11]!=null?((BigDecimal)objects[11]).toString():"0");
			 		vo.setProfitMoney(new BigDecimal(vo.getRechargeMoney()).subtract(new BigDecimal(vo.getWithdrawMoney())).toString());
			 		data.add(vo);
			 		
			 		betMoney = betMoney.add(new BigDecimal(vo.getBetMoney()));
			 		expandUserNum = expandUserNum + vo.getExpandUserNum();
			 		profitMoney = profitMoney.add(new BigDecimal(vo.getProfitMoney()));
			 		rechargeMoney = rechargeMoney.add(new BigDecimal(vo.getRechargeMoney()));
			 		withdrawMoney = withdrawMoney.add(new BigDecimal(vo.getWithdrawMoney()));
			 		waterMoney = waterMoney.add(new BigDecimal(vo.getBackWaterMoney()));
			 		winMoney = winMoney.add(new BigDecimal(vo.getWinMoney()));
			 		balance = balance.add(new BigDecimal(vo.getBalance()));
			 		lotteryBalance = lotteryBalance.add(new BigDecimal(vo.getLotteryBalance()));
				}

				total.put("betMoney", betMoney);
				total.put("expandUserNum", expandUserNum);
				total.put("profitMoney", profitMoney);
				total.put("rechargeMoney", rechargeMoney);
				total.put("withdrawMoney", withdrawMoney);
				total.put("waterMoney", waterMoney);
				total.put("winMoney", winMoney);
				total.put("balance", balance);
				total.put("lotteryBalance", lotteryBalance);
			}
			
		 	result = new HashMap<String, Object>();
			result.put("data", total);
			
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
    
    /**盈亏统计列表(需登录权限)*/
    @ResponseBody
  	@RequestMapping(value = "/profitTotal", method={RequestMethod.POST, RequestMethod.GET})
	@ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryOrderTotalVo.class)})
  	@ApiOperation(httpMethod = "GET", value = "团队盈亏列表(需登录权限)")//当前接口注解
  	@ApiImplicitParams({
  		@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "team_username", value = "用户名查询", required = false, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "token", value = "登陆token", required = true, dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "date_begin", value = "开始时间，时间格式yyyy-MM-dd", required = false,dataType = "string"),
		@ApiImplicitParam(paramType="query", name = "pid", value = "上级玩家ID", required = false, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "date_end", value = "结束时间，时间格式yyyy-MM-dd",required = false,dataType = "string"),
  		@ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
  		@ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int"),
  	})
    public JsonResult profitTotal(HttpServletRequest request, HttpServletResponse response,
    	@RequestParam(value="station_id",required=false) Integer stationId, 
  		@RequestParam(value="timestamp") String timestamp, 
  		@RequestParam(value="version") String version, 
  		@RequestParam(value="sign") String sign, 
  		@RequestParam(value="device") Integer device,
  		@RequestParam(value="team_username",required=false) String teamUsername, 
 		@RequestParam(value="pid") Integer pid,
 		@RequestParam(value="date_begin",required=false) String dateBegin,
 		@RequestParam(value="date_end",required=false) String dateEnd,
  		@RequestParam(value="username") String username, 
  		@RequestParam(value="token") String token,
  		@RequestParam(value="page") Integer page,
  		@RequestParam(value="limit") Integer limit){
		String methodName = "团队盈亏列表(需登录权限)";
		try {
			//System.out.println(ApiUtil.getRequestUrl(request));
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("teamUsername", teamUsername);
			parasMap.put("token", token);
			parasMap.put("pid", pid);
			parasMap.put("date_begin", dateBegin);
			parasMap.put("date_end", dateEnd);
			parasMap.put("page", page);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			String secretKey = device==3?jedisClient.get(token):ApiUtil.SECRETKEY;
			parasMap =  ApiUtil.checkParameter(parasMap,secretKey);//验证参数
			
			userService.checkToken((String)parasMap.get("username"), token,stationId);//登陆验证
			User pUser = userService.find(pid);
			if(pUser == null || pUser.getStatus() == 0) {
				throw new BusinessException(ApiUtil.getErrorCode("119"));
			}
			
			Integer expandUserNum = 0;
			BigDecimal betMoney = new BigDecimal(0);
			BigDecimal profitMoney = new BigDecimal(0);
			BigDecimal rechargeMoney = new BigDecimal(0);
			BigDecimal withdrawMoney = new BigDecimal(0);
			BigDecimal waterMoney = new BigDecimal(0);
			BigDecimal winMoney = new BigDecimal(0);
			BigDecimal balance = new BigDecimal(0);
			BigDecimal lotteryBalance = new BigDecimal(0);
			QueryResult<Object[]> qr = null;
			
			if(StringUtils.isBlank(dateBegin))
				dateBegin = DateTimeTool.dateFormat("yyyy-MM-dd", new Date());
			if(StringUtils.isBlank(dateEnd))
				dateEnd = DateTimeTool.dateFormat("yyyy-MM-dd", new Date());
			
			if(pUser.getUserType()==2) {//0游客、1玩家、2代理、3推广员、4虚拟号
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("SELECT p.id,p.username,p.nick_name,SUM(t.bet_money) as bet_money,e.expand_user_num as expand_user_num,sum(t.profit_money) as profit_money ,sum(t.recharge_money) as recharge_money,sum(t.water_money) as water_money,sum(t.win_money) as win_money,sum(t.withdraw_money) as withdraw_money,sum(b.balance) as balance,sum(b.lottery_balance) as lottery_balance from");
				sqlBuffer.append(" (SELECT p.id,p.parent_id,p.username,p.nick_name,u.id as uid,p.station_id from tb_member_user u,tb_member_user p where u.parent_id = p.id and u.user_type = 1 ) p ");
				
				sqlBuffer.append(" LEFT JOIN (SELECT t1.balance as balance,t1.user_id as user_id from tb_lottery_daily_order_total t1 where t1.total_date = str_to_date((SELECT MAX(t2.total_date) from tb_lottery_daily_order_total t2 where t1.user_id = t2.user_id and t2.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY t2.user_id ORDER BY t2.total_date),'%Y-%m-%d %H:%i:%s')) b");
				sqlBuffer.append(" on p.uid = b.user_id");
				
				sqlBuffer.append(" LEFT JOIN  (SELECT sum(o1.expand_user_num) as expand_user_num,o1.user_id,t1.lottery_balance as lottery_balance from tb_lottery_daily_order_total o1 where  o1.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o1.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s')GROUP BY o1.user_id) e");
				sqlBuffer.append(" on p.id = e.user_id");
				
				sqlBuffer.append(" LEFT JOIN (SELECT o.user_id,SUM(o.bet_money) as bet_money,sum(o.profit_money) as profit_money ,sum(o.recharge_money) as recharge_money,sum(o.water_money) as water_money,sum(o.win_money) as win_money,sum(o.withdraw_money) as withdraw_money from");
				sqlBuffer.append(" tb_lottery_daily_order_total o where o.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY o.user_id ORDER BY SUM(o.profit_money) DESC) t ");
				sqlBuffer.append(" on t.user_id = p.uid");
				
				sqlBuffer.append(" where p.parent_id = ").append(pUser.getId());
				sqlBuffer.append(" and p.station_id = ").append(stationId);
				if(StringUtils.isNotBlank(teamUsername)) {
					sqlBuffer.append(" and p.username like '%").append(teamUsername).append("%'");
				}
				
				sqlBuffer.append(" GROUP BY p.id");
				
				//System.out.println(sqlBuffer.toString());
				qr = lotteryOrderService.getScrollData((page-1)*limit, limit, sqlBuffer.toString());
			}else if(pUser.getUserType()==3) {
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("SELECT u.id,u.username,u.nick_name,SUM(t.bet_money) as bet_money,sum(t.expand_user_num) as expand_user_num,sum(t.profit_money) as profit_money ,sum(t.recharge_money) as recharge_money,sum(t.water_money) as water_money,sum(t.win_money) as win_money,sum(t.withdraw_money) as withdraw_money,sum(b.balance) as balance,sum(b.lottery_balance) as lottery_balance from");
				sqlBuffer.append(" (SELECT u.id,u.parent_id,u.username,u.nick_name,u.station_id from tb_member_user u where u.user_type = 1 ) u");
				
				sqlBuffer.append(" LEFT JOIN (SELECT t1.balance as balance,t1.user_id as user_id,t1.lottery_balance as lottery_balance from tb_lottery_daily_order_total t1 where t1.total_date = str_to_date((SELECT MAX(t2.total_date) from tb_lottery_daily_order_total t2 where t1.user_id = t2.user_id and t2.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY t2.user_id ORDER BY t2.total_date),'%Y-%m-%d %H:%i:%s')) b");
				sqlBuffer.append(" on u.id = b.user_id ");
				
				sqlBuffer.append(" LEFT JOIN (SELECT o.user_id,SUM(o.bet_money) as bet_money,sum(o.expand_user_num) as expand_user_num,sum(o.profit_money) as profit_money ,sum(o.recharge_money) as recharge_money,sum(o.water_money) as water_money,sum(o.win_money) as win_money,sum(o.withdraw_money) as withdraw_money from");
				sqlBuffer.append(" tb_lottery_daily_order_total o where o.total_date >=str_to_date('").append(DateTimeTool.queryStartDate(dateBegin)).append("','%Y-%m-%d %H:%i:%s') and o.total_date <=str_to_date('").append(DateTimeTool.queryEndDate(dateEnd)).append("','%Y-%m-%d %H:%i:%s') GROUP BY o.user_id ORDER BY SUM(o.profit_money) DESC) t");
				sqlBuffer.append(" on t.user_id = u.id ");
				
				sqlBuffer.append(" where u.station_id = ").append(stationId);
				sqlBuffer.append(" and u.parent_id = ").append(pUser.getId());
				if(StringUtils.isNotBlank(teamUsername)) {
					sqlBuffer.append(" and u.username like '%").append(teamUsername).append("%'");
				}
				
				sqlBuffer.append(" GROUP BY u.id");
				
				//System.out.println(sqlBuffer.toString());
				qr = lotteryOrderService.getScrollData((page-1)*limit, limit, sqlBuffer.toString());
			}else {
				throw new BusinessException(ApiUtil.getErrorCode("120"));
			}
			
			String message = "";
			Map<String, Object> result = null;
			List<LotteryOrderTotalVo> data = new ArrayList<LotteryOrderTotalVo>();
			Map<String, Object> total = new HashMap<String, Object>();
			if(qr!=null && qr.getResultCount()>0) {
				message = "成功";
				for (Object[] objects : qr.getResultData()) {
					LotteryOrderTotalVo vo = new LotteryOrderTotalVo();
			 		vo.setUserId((Integer)objects[0]);
			 		vo.setUsername((String)objects[1]);
			 		vo.setNickName((String)objects[2]);
			 		vo.setBetMoney(objects[3]!=null?((BigDecimal)objects[3]).toString():"0");
			 		vo.setExpandUserNum(objects[4]!=null?((BigDecimal)objects[4]).intValue():0);
			 		//vo.setProfitMoney(objects[5]!=null?((BigDecimal)objects[5]).toString():"0");
			 		vo.setRechargeMoney(objects[6]!=null?((BigDecimal)objects[6]).toString():"0");
			 		vo.setBackWaterMoney(objects[7]!=null?((BigDecimal)objects[7]).toString():"0");
			 		vo.setWinMoney(objects[8]!=null?((BigDecimal)objects[8]).toString():"0");
			 		vo.setWithdrawMoney(objects[9]!=null?((BigDecimal)objects[9]).toString():"0");
			 		vo.setBalance(objects[10]!=null?((BigDecimal)objects[10]).toString():"0");
			 		vo.setLotteryBalance(objects[11]!=null?((BigDecimal)objects[11]).toString():"0");
			 		vo.setProfitMoney(new BigDecimal(vo.getRechargeMoney()).subtract(new BigDecimal(vo.getWithdrawMoney())).toString());
			 		data.add(vo);
			 		
			 		betMoney = betMoney.add(new BigDecimal(vo.getBetMoney()));
			 		expandUserNum = expandUserNum + vo.getExpandUserNum();
			 		profitMoney = profitMoney.add(new BigDecimal(vo.getProfitMoney()));
			 		rechargeMoney = rechargeMoney.add(new BigDecimal(vo.getRechargeMoney()));
			 		withdrawMoney = withdrawMoney.add(new BigDecimal(vo.getWithdrawMoney()));
			 		waterMoney = waterMoney.add(new BigDecimal(vo.getBackWaterMoney()));
			 		winMoney = winMoney.add(new BigDecimal(vo.getWinMoney()));
			 		balance = balance.add(new BigDecimal(vo.getBalance()));
			 		lotteryBalance = lotteryBalance.add(new BigDecimal(vo.getLotteryBalance()));
				}

				total.put("betMoney", betMoney);
				total.put("expandUserNum", expandUserNum);
				total.put("profitMoney", profitMoney);
				total.put("rechargeMoney", rechargeMoney);
				total.put("withdrawMoney", withdrawMoney);
				total.put("waterMoney", waterMoney);
				total.put("winMoney", winMoney);
				total.put("balance", balance);
				total.put("lotteryBalance", lotteryBalance);
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
}
