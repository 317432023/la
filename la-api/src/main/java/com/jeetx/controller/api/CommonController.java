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

import com.jeetx.bean.base.Advert;
import com.jeetx.bean.base.AppVersion;
import com.jeetx.bean.base.Info;
import com.jeetx.bean.base.SiteDomain;
import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.LotteryRobotPlant;
import com.jeetx.bean.lottery.StationLotteryType;
import com.jeetx.bean.member.User;
import com.jeetx.bean.system.Station;
import com.jeetx.bean.system.StationConfig;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.model.page.PageView;
import com.jeetx.common.model.page.QueryResult;
import com.jeetx.common.redis.JedisClient;
import com.jeetx.common.swagger.model.JsonResult;
import com.jeetx.common.swagger.model.base.AdvertVo;
import com.jeetx.common.swagger.model.base.AppVersionVo;
import com.jeetx.common.swagger.model.base.InfoVo;
import com.jeetx.common.swagger.model.base.SiteDomainVo;
import com.jeetx.common.swagger.model.lottery.LotteryTypeVo;
import com.jeetx.common.swagger.model.lottery.WinVo;
import com.jeetx.common.swagger.model.system.StationVo;
import com.jeetx.common.swagger.model.system.SystemConfigVo;
import com.jeetx.service.base.AdvertService;
import com.jeetx.service.base.AppVersionService;
import com.jeetx.service.base.InfoService;
import com.jeetx.service.base.SiteDomainService;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.lottery.LotteryPeriodsService;
import com.jeetx.service.lottery.LotteryRobotPlantService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryTypeService;
import com.jeetx.service.lottery.StationLotteryTypeService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.system.StationConfigService;
import com.jeetx.service.system.StationService;
import com.jeetx.service.system.SystemConfigService;
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
@RequestMapping("/api/common")
@Api(tags = "公共服务接口(参数URLEncoder后提交)") //swagger分类标题注解
public class CommonController {
	
    @Autowired JedisClient jedisClient;
	@Autowired AdvertService advertService;
	@Autowired InfoService infoService;
	@Autowired LotteryTypeService lotteryTypeService;
	@Autowired SiteDomainService siteDomainService;
	@Autowired LotteryPeriodsService lotteryPeriodsService;
	@Autowired AppVersionService appVersionService;
	@Autowired UserService userService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired SystemConfigService systemConfigService;
	@Autowired StationConfigService stationConfigService;
	@Autowired StationLotteryTypeService stationLotteryTypeService;
	@Autowired LotteryOrderService lotteryOrderService;
	@Autowired LotteryRobotPlantService lotteryRobotPlantService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired StationService stationService;
	
	@Value("${developMode}")
	private Boolean developMode;
	
	@Value("${secretKey}")
	private String secretKey;
	
	@Value("${resServerLink}")
	private String resServerLink;
	
	/**获取服务器时间*/
    @ResponseBody
	@RequestMapping(value = "/serverTime", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "获取服务器时间")//当前接口注解
    public JsonResult serverTime(HttpServletRequest request, HttpServletResponse response){
		String methodName = "获取服务器时间";
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("serverTime", DateTimeTool.dateFormat("yyyyMMddHHmmss", new Date()));
			
		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,"成功",result);
		}catch (BusinessException e) {
			LogUtil.info(methodName.concat("-处理信息异常"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
		}catch (Exception e) {
			LogUtil.info(methodName.concat("-系统错误"), e);
			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
		}
    }
	
	/**获取轮播图列表*/
    @ResponseBody
	@RequestMapping(value = "/advertList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = AdvertVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取轮播图列表")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "advert_type", value = "轮播图类型(1:PC、2:APP)", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int")
    })
    public JsonResult advertList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="advert_type") Integer advertType, 
    		@RequestParam(value="page") Integer page,
    		@RequestParam(value="limit") Integer limit){
		String methodName = "获取轮播图列表";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("advertType", advertType);
			parasMap.put("page", page);
			parasMap.put("device", device);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("sortNum", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			sb.append(" and o.advertType = ?");
			params.add(advertType);
			
			sb.append(" and o.station.id = ?");
			params.add(stationId);

			QueryResult<Advert> qr = advertService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<AdvertVo> data = new ArrayList<AdvertVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (Advert object : qr.getResultData()) {
			 		AdvertVo vo = new AdvertVo();
			 		vo.setId(object.getId());
			 		vo.setTitle(StringUtils.isNotBlank(object.getAdTitle())?object.getAdTitle():"");
			 		vo.setHttpLink(StringUtils.isNotBlank(object.getAdHttp())?object.getAdHttp():"");
			 		vo.setPicLink(StringUtils.isNotBlank(object.getAdImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getAdImg():"");
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
    
    /**获取资讯列表*/
    @ResponseBody
	@RequestMapping(value = "/infoList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = InfoVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取资讯列表")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "category_id", value = "类型Id", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int")
    })
    public JsonResult infoList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="category_id") Integer categoryId,
    		@RequestParam(value="page") Integer page,
    		@RequestParam(value="limit") Integer limit){
		String methodName = "获取资讯列表";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("categoryId", categoryId);
			parasMap.put("page", page);
			parasMap.put("device", device);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
			
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("sortNum", "desc");
			orderby.put("createtime", "asc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			sb.append(" and o.infoCategory.id = ?");
			params.add(categoryId);

			sb.append(" and o.station.id = ?");
			params.add(stationId);
			
			sb.append(" and o.status = ?");
			params.add(1);

			QueryResult<Info> qr = infoService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			
			String message = "";
			Map<String, Object> result = null;
			List<InfoVo> data = new ArrayList<InfoVo>();
			if(qr.getResultCount()>0) {
				message = "成功";
			 	for (Info object : qr.getResultData()) {
			 		InfoVo vo = new InfoVo();
			 		vo.setId(object.getId());
			 		vo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
			 		vo.setContent(StringUtils.isNotBlank(object.getContent())?object.getContent():"");
			 		vo.setCreatetime(DateTimeTool.dateFormat(null, object.getCreatetime()));
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

    /**获取资讯详情*/
    @ResponseBody
    @RequestMapping(value = "/infoDetail", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = InfoVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取资讯详情")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "info_id", value = "资讯Id", required = true, dataType = "int")
    })
    public JsonResult infoDetail(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="info_id") Integer infoId){
   		String methodName = "获取资讯详情";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("infoId", infoId);
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
   			
   			Info object = infoService.find(infoId);
   			if(object != null && object.getStation().getId() != stationId) {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			String message = "";
   			InfoVo vo = null;
   			if(object != null) {
		 		vo = new InfoVo();
		 		vo.setId(object.getId());
		 		vo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
		 		vo.setContent(StringUtils.isNotBlank(object.getContent())?object.getContent():"");
		 		vo.setCreatetime(DateTimeTool.dateFormat(null, object.getCreatetime()));
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
    
    /**获取彩票列表*/
    @ResponseBody
    @RequestMapping(value = "/lotteryTypeList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = LotteryTypeVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取彩票列表")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult lotteryTypeList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device){
   		String methodName = "获取彩票列表";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
   			
   			String message = "";
   			List<StationLotteryType> lotteryTypeList = stationLotteryTypeService.getStationLotteryTypeByStationId(stationId);

   			List<LotteryTypeVo> data = new ArrayList<LotteryTypeVo>();
   			if(lotteryTypeList != null && lotteryTypeList.size() >0) {
   				message = "成功";
			 	for (StationLotteryType stationLotteryType : lotteryTypeList) {
			 		LotteryTypeVo vo = new LotteryTypeVo();
			 		vo.setId(stationLotteryType.getLotteryType().getId());
			 		vo.setTitle(StringUtils.isNotBlank(stationLotteryType.getLotteryName())?stationLotteryType.getLotteryName():"");
			 		vo.setRemarks(StringUtils.isNotBlank(stationLotteryType.getLotteryType().getRemarks())?stationLotteryType.getLotteryType().getRemarks():"");
			 		vo.setStatus(stationLotteryType.getStatus());
			 		vo.setPicLink(StringUtils.isNotBlank(stationLotteryType.getPicLink())?ApiUtil.getResServerLink(resServerLink, stationLotteryType.getStation().getImageDomain())+stationLotteryType.getPicLink():"");
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
    
    /**获取站点线路列表*/
    @ResponseBody
    @RequestMapping(value = "/siteDomainList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = SiteDomainVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取站点线路列表")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult siteDomainList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device){
   		String methodName = "获取站点线路列表";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
   			
   			String message = "";
   			List<SiteDomain> siteDomainList = null;
   			switch (device) {
			case 0:
				siteDomainList = siteDomainService.findByHql("from SiteDomain where isOpenPC = 1 and station.id = "+stationId+" order by sortNum desc");
				break;
			case 1:
			case 2:
			case 3:
				siteDomainList = siteDomainService.findByHql("from SiteDomain where isOpenApp = 1 and station.id = "+stationId+" order by sortNum desc");
				break;
			}

   			List<SiteDomainVo> data = new ArrayList<SiteDomainVo>();
   			if(siteDomainList != null && siteDomainList.size() >0) {
   				message = "成功";
			 	for (SiteDomain object : siteDomainList) {
			 		SiteDomainVo vo = new SiteDomainVo();
			 		vo.setId(object.getId());
			 		vo.setSiteName(StringUtils.isNotBlank(object.getSiteName())?object.getSiteName():"");
			 		vo.setSiteDomain(StringUtils.isNotBlank(object.getSiteDomain())?object.getSiteDomain():"");
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
    
    /**获取APP版本信息*/
    @ResponseBody
    @RequestMapping(value = "/checkAppVersion", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = AppVersionVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取APP版本信息")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult checkAppVersion(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device){
   		String methodName = "获取APP版本信息";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
			
			AppVersion appVersion = appVersionService.findAppVersion(device,stationId);
   			String message = "";
   			AppVersionVo vo = null;
   			if(appVersion != null) {
   				message = "成功";
		 		vo = new AppVersionVo();
		 		vo.setDeviceType(appVersion.getDeviceType());
		 		vo.setDownloadLink(appVersion.getDownloadLink());
		 		vo.setIsForceupdate(appVersion.getIsForceupdate());
		 		vo.setUpdateLog(StringUtils.isNotBlank(appVersion.getUpdateLog())?appVersion.getUpdateLog():"");
		 		vo.setVersionCode(appVersion.getVersionCode());
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
    
    /**玩家注册*/
    @ResponseBody
    @RequestMapping(value = "/register", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "玩家注册")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "password", value = "登陆密码，两次MD5加密后的结果，结果小写", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "pid", value = "上级推广员Id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "phone", value = "手机号码", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "wechat", value = "微信号", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "referee", value = "推荐人", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "pid", value = "上级推广员Id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "p_username", value = "虚拟号", required = false, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult register(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device, 
    		@RequestParam(value="phone" ,required=false) String phone, 
    		@RequestParam(value="wechat" ,required=false) String wechat, 
    		@RequestParam(value="referee" ,required=false) String referee, 
    		@RequestParam(value="pid" ,required=false) Integer pid, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="p_username",required=false) String pUsername, 
    		@RequestParam(value="password") String password){
   		String methodName = "玩家注册";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("pid", pid);
			parasMap.put("referee", referee);
			parasMap.put("wechat", wechat);
			parasMap.put("phone", phone);
			parasMap.put("username", username);
			parasMap.put("p_username", pUsername);
			parasMap.put("password", password);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
			
			userService.register(pid,(String)parasMap.get("username"),
					parasMap.get("phone")!=null?(String)parasMap.get("phone"):null,
					parasMap.get("referee")!=null?(String)parasMap.get("referee"):null,
					parasMap.get("wechat")!=null?(String)parasMap.get("wechat"):null,
					1, password,ApiUtil.getDeviceName(device),stationId,
					parasMap.get("p_username")!=null?(String)parasMap.get("p_username"):null);
   			String message = "注册成功";
   			
   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,null);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}
    
    
    /**玩家登陆*/
    @ResponseBody
    @RequestMapping(value = "/login", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "玩家登陆")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "username", value = "用户名", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "password", value = "登陆密码，两次MD5加密后的结果，结果小写", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult login(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device, 
    		@RequestParam(value="username") String username, 
    		@RequestParam(value="password") String password){
   		String methodName = "玩家登陆";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("username", username);
			parasMap.put("password", password);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数

			User user = userService.login((String)parasMap.get("username"), password,IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
			
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
			result.put("secretKey", jedisClient.get(user.getLoginToken()));
			
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
    
    /**游客登陆*/
    @ResponseBody
    @RequestMapping(value = "/touristLogin", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "游客登陆")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult touristLogin(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device){
   		String methodName = "游客登陆";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
			
			User user = userService.touristLogin(ApiUtil.getDeviceName(device),stationId);
			
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
			result.put("secretKey", jedisClient.get(user.getLoginToken()));
			
   			String message = "登陆成功";
   			
   		    return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),0,message,result);
   		}catch (BusinessException e) {
   			LogUtil.info(methodName.concat("-处理信息异常"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),Integer.valueOf(e.getMessage().split("-")[0]),e.getMessage().split("-")[1],null);
   		}catch (Exception e) {
   			LogUtil.info(methodName.concat("-系统错误"), e);
   			return new JsonResult(developMode,methodName,ApiUtil.getRequestUrl(request),200,"系统错误",null);
   		}
   	}

    /**系统配置*/
    @ResponseBody
    @RequestMapping(value = "/systemConfig", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = SystemConfigVo.class)})
    @ApiOperation(httpMethod = "GET", value = "游客登陆")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string")
    })
    public JsonResult systemConfig(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="sign") String sign){
   		String methodName = "系统配置";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("device", device);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
			
			String message = "";
			List<StationConfig> configList = stationConfigService.getListByStationId(stationId);
   			List<SystemConfigVo> data = new ArrayList<SystemConfigVo>();
   			if(configList != null && configList.size() >0) {
   				message = "成功";
			 	for (StationConfig object : configList) {
			 		SystemConfigVo vo = new SystemConfigVo();
			 		vo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
			 		vo.setName(StringUtils.isNotBlank(object.getName())?object.getName():"");
			 		vo.setValue(StringUtils.isNotBlank(object.getValue())?object.getValue():"");
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
    
	/**获取最近中奖名单*/
    @ResponseBody
	@RequestMapping(value = "/getWinList", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = AdvertVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取最近中奖名单")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string")
    })
    public JsonResult getWinList(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="sign") String sign){
		String methodName = "获取最近中奖名单";
		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("device", device);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
			
			String message = "";
			Map<String, Object> result = null;
			List<WinVo> data = null;
			
			String key = "WINLIST:" + stationId;
			String dataStr = jedisClient.get(key);
			if(StringUtils.isBlank(dataStr)) {
				PageView pageView = new PageView(18, 1);
				LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
				orderby.put("id", "desc");
				
				StringBuilder sb = new StringBuilder();
				List<Object> params = new ArrayList<Object>();

				sb.append(" and o.status = ?");
				params.add(2);
				
				sb.append(" and o.createTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.addTime(new Date(), -300));
		
				sb.append(" and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", new Date()));
				
				sb.append(" and o.user.station.id = ?");
				params.add(stationId);

				QueryResult<LotteryOrder> qr = lotteryOrderService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);

				data = new ArrayList<WinVo>();
				if(qr.getResultCount()>0) {
					message = "成功";
				 	for (LotteryOrder object : qr.getResultData()) {
				 		WinVo vo = new WinVo();
				 		vo.setNickName(object.getUser().getNickName());
				 		vo.setHillTitle(object.getLotteryType().getLotteryName());
				 		vo.setBonus(object.getWinMoney().setScale(0, BigDecimal.ROUND_DOWN).toString());
				 		data.add(vo);
					}
				}
				
				//少用18条信息时，随机生成相差记录
				if(data.size()<18) {
					int k = 18 - data.size();
					WinVo vo = null;
					
					String lotteryTypeStr = "";
					String curTime = DateTimeTool.dateFormat("HH:mm", new Date());
					
					//1北京28
					if(DateTimeTool.isInTime("09:5-23:56", curTime)){
						if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
						lotteryTypeStr = lotteryTypeStr.concat("1");
					}
					
					//2加拿大28
					String jndTimeInterval = systemConfigService.getValueByName("lottery_jnd_time_interval");
					if(StringUtils.isNotBlank(jndTimeInterval)&&"1".equals(jndTimeInterval)) {//夏令时
						if(!DateTimeTool.isInTime("19:00-21:00", curTime)){
							if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
							lotteryTypeStr = lotteryTypeStr.concat("2");
						}
					}else if(StringUtils.isNotBlank(jndTimeInterval)&&"2".equals(jndTimeInterval)){//冬令时
						if(!DateTimeTool.isInTime("20:00-22:00", curTime)){
							if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
							lotteryTypeStr = lotteryTypeStr.concat("2");
						}
					}
					
					//3幸运飞艇 13:00~04:00
					if(!DateTimeTool.isInTime("04:0-13:00", curTime)){
						if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
						lotteryTypeStr = lotteryTypeStr.concat("3");
					}
					
					//4北京赛车09:10~23:50
					if(DateTimeTool.isInTime("09:10-23:50", curTime)){
						if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
						lotteryTypeStr = lotteryTypeStr.concat("4");
					}
					
					//5重庆时时彩
					if(DateTimeTool.isInTime("07:10-23:49", curTime) || DateTimeTool.isInTime("00:30-03:00", curTime) ){
						if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
						lotteryTypeStr = lotteryTypeStr.concat("5");
					}
					//System.out.println(lotteryTypeStr);
					if(StringUtils.isNotBlank(lotteryTypeStr)) {
						String hql = "from StationLotteryType o where o.station.id = "+stationId+" and o.status = 1 and o.lotteryType.id in ("+lotteryTypeStr+") order by sortNum desc";
						List<StationLotteryType> stationLotteryTypeList = stationLotteryTypeService.findByHql(hql);
						for (int i = 0; i < k; i++) {
							LotteryRobotPlant lotteryRobotPlant = lotteryRobotPlantService.find(RandomUtil.getRangeRandom(1,110));
							if(lotteryRobotPlant != null && stationLotteryTypeList != null) {
								vo = new WinVo();
						 		vo.setNickName(lotteryRobotPlant.getNickName());
						 		vo.setHillTitle(stationLotteryTypeList.get(RandomUtil.getRangeRandom(0,stationLotteryTypeList.size())).getLotteryName());
						 		
						 		BigDecimal betMoney = new BigDecimal(RandomUtil.getRangeRandom(50,5000));//中奖
								betMoney = betMoney.compareTo(new BigDecimal(10))<0?new BigDecimal(10):betMoney;
								if(betMoney.compareTo(new BigDecimal(100))<0) {
									betMoney = betMoney.divide(new BigDecimal(10)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(10));
								}else {
									betMoney = betMoney.divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
								}
						 		vo.setBonus(betMoney.toString());
						 		data.add(vo);
							}
						}
					}
				}
				//System.out.println("1:"+JsonUtil.toJSONString(data));
				jedisClient.set(key, JsonUtil.toJSONString(data));
				jedisClient.expire(key, 60*1); //设置会话过期时间180秒=3分钟
			}else {
				//System.out.println("2:"+dataStr);
				data = JsonUtil.toList(dataStr,WinVo.class);
			}
			
		 	result = new HashMap<String, Object>();
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
    
    /**微信授权登陆*/
    @ResponseBody
    @RequestMapping(value = "/weChatLogin", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误")})
    @ApiOperation(httpMethod = "GET", value = "微信授权登陆")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "openid", value = "微信openId", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "nickname", value = "微信昵称", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "head_img", value = "微信头像", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult weChatLogin(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device, 
    		@RequestParam(value="nickname") String nickname,
    		@RequestParam(value="head_img") String headImg,
    		@RequestParam(value="openid") String openId){
   		String methodName = "微信授权登陆";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("openid", openId);
			parasMap.put("nickname", nickname);
			parasMap.put("head_img", headImg);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数

			User user = userService.weChatLogin((String)parasMap.get("openid"),(String)parasMap.get("nickname"),(String)parasMap.get("head_img"),
					IpUtil.getIpAddr(request),ApiUtil.getDeviceName(device),stationId);
			
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
			result.put("secretKey", jedisClient.get(user.getLoginToken()));
			
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
    
    
    /**获取站点信息*/
    @ResponseBody
    @RequestMapping(value = "/stationInfo", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = SiteDomainVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取站点信息")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = true, dataType = "int")
    })
    public JsonResult stationInfo(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device") Integer device){
   		String methodName = "获取站点信息";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("device", device);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
   			
			if(stationId == null) {
				throw new BusinessException(ApiUtil.getErrorCode("108"));
			}

			Station station = stationService.find(stationId);
			if(station == null) {
				throw new BusinessException(ApiUtil.getErrorCode("108"));
			}
   			
			StationVo vo = new StationVo();
	 		vo.setId(station.getId());
	 		vo.setStationName(StringUtils.isNotBlank(station.getStationName())?station.getStationName():"");
	 		vo.setEntryDomain(StringUtils.isNotBlank(station.getEntryDomain())?station.getEntryDomain():"");
	 		vo.setImageDomain(StringUtils.isNotBlank(station.getImageDomain())?station.getImageDomain():resServerLink);
	 		vo.setMqDomain(StringUtils.isNotBlank(station.getMqDomain())?station.getMqDomain():"");
	 		vo.setEffectiveTime(station.getEffectiveTime()!=null?DateTimeTool.dateFormat(null, station.getEffectiveTime()):"");
   			
   			Map<String, Object> result = new HashMap<String, Object>();
			result.put("station", vo);

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
    
    
    /**获取站点首页信息*/
    @ResponseBody
    @RequestMapping(value = "/index", method={RequestMethod.POST, RequestMethod.GET})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "0成功,result才有返回信息,其它为错误", response = SiteDomainVo.class)})
    @ApiOperation(httpMethod = "GET", value = "获取站点首页信息")//当前接口注解
    @ApiImplicitParams({
    	@ApiImplicitParam(paramType="query", name = "station_id", value = "站点id", required = false, dataType = "int"),
    	@ApiImplicitParam(paramType="query", name = "timestamp", value = "发送请求的时间，格式:yyyyMMddHHmmss", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "version", value = "调用的接口版本，固定为：1.0", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "sign", value = "参数的签名串，签名说明:各参数按首字母顺序排列，对应的参数值拼接结果+secretKey，后再md5加密，结果小写，如:a=1&b=2&c=3,secretKey=abc,sign=md5(123abc)", required = true, dataType = "string"),
    	@ApiImplicitParam(paramType="query", name = "category_id", value = "资讯类型Id", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "page", value = "页数", required = true, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "device", value = "设备(1安卓、2IOS、3H5)", required = false, dataType = "int"),
        @ApiImplicitParam(paramType="query", name = "limit", value = "每页记录数", required = true, dataType = "int")
    })
    public JsonResult index(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value="station_id",required=false) Integer stationId, 
    		@RequestParam(value="timestamp") String timestamp, 
    		@RequestParam(value="version") String version, 
    		@RequestParam(value="sign") String sign, 
    		@RequestParam(value="device",required=false) Integer device,
    		@RequestParam(value="category_id") Integer categoryId,
    		@RequestParam(value="page") Integer page,
    		@RequestParam(value="limit") Integer limit){
   		String methodName = "获取站点首页信息";
   		try {
			Map<String, Object> parasMap = new HashMap<String, Object>();
			if(stationId != null) parasMap.put("station_id", stationId); else stationId = stationService.getStationId(stationId, request.getHeader("Referer"));
			parasMap.put("categoryId", categoryId);
			parasMap.put("page", page);
			parasMap.put("device", device);
			parasMap.put("limit", limit);
			parasMap.put("timestamp", timestamp);
			parasMap.put("version", version);
			parasMap.put("sign", sign);
			parasMap =  ApiUtil.checkParameter(parasMap,device!=null&&device==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY);//验证参数
   			
   			Map<String, Object> result = new HashMap<String, Object>();

   			
			//==========================1聚合stationInfo==========================
			if(stationId == null) {
				throw new BusinessException(ApiUtil.getErrorCode("108"));
			}

			Station station = stationService.find(stationId);
			if(station == null) {
				throw new BusinessException(ApiUtil.getErrorCode("108"));
			}
   			
			StationVo vo = new StationVo();
	 		vo.setId(station.getId());
	 		vo.setStationName(StringUtils.isNotBlank(station.getStationName())?station.getStationName():"");
	 		vo.setEntryDomain(StringUtils.isNotBlank(station.getEntryDomain())?station.getEntryDomain():"");
	 		vo.setImageDomain(StringUtils.isNotBlank(station.getImageDomain())?station.getImageDomain():resServerLink);
	 		vo.setMqDomain(StringUtils.isNotBlank(station.getMqDomain())?station.getMqDomain():"");
	 		vo.setEffectiveTime(station.getEffectiveTime()!=null?DateTimeTool.dateFormat(null, station.getEffectiveTime()):"");
   			
			result.put("station", vo);
			
			
	 		//==========================2聚合advertList==========================
			PageView pageView = new PageView(limit, page);
			LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("sortNum", "desc");
			
			StringBuilder sb = new StringBuilder();
			List<Object> params = new ArrayList<Object>();
			
			sb.append(" and o.advertType = ?");
			params.add(2);
			
			sb.append(" and o.station.id = ?");
			params.add(stationId);

			QueryResult<Advert> qr = advertService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			List<AdvertVo> advertVoData = new ArrayList<AdvertVo>();
			if(qr.getResultCount()>0) {
			 	for (Advert object : qr.getResultData()) {
			 		AdvertVo advertVo = new AdvertVo();
			 		advertVo.setId(object.getId());
			 		advertVo.setTitle(StringUtils.isNotBlank(object.getAdTitle())?object.getAdTitle():"");
			 		advertVo.setHttpLink(StringUtils.isNotBlank(object.getAdHttp())?object.getAdHttp():"");
			 		advertVo.setPicLink(StringUtils.isNotBlank(object.getAdImg())?ApiUtil.getResServerLink(resServerLink, object.getStation().getImageDomain())+object.getAdImg():"");
			 		advertVoData.add(advertVo);
				}
			}
			
			HashMap<String, Object> advert = new HashMap<String, Object>();
			advert.put("page", page);
			advert.put("limit", limit);
			advert.put("count", qr.getResultCount());
			advert.put("data", advertVoData);
	 		
			result.put("advert", advert);
			
			
			//==========================3聚合infoList==========================
			pageView = new PageView(limit, page);
			orderby = new LinkedHashMap<String, String>();
			orderby.put("sortNum", "desc");
			orderby.put("createtime", "asc");
			
			sb = new StringBuilder();
			params = new ArrayList<Object>();
			
			sb.append(" and o.infoCategory.id = ?");
			params.add(categoryId);

			sb.append(" and o.station.id = ?");
			params.add(stationId);
			
			sb.append(" and o.status = ?");
			params.add(1);

			QueryResult<Info> qr1 = infoService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);
			List<InfoVo> infoData = new ArrayList<InfoVo>();
			if(qr1.getResultCount()>0) {
			 	for (Info object : qr1.getResultData()) {
			 		InfoVo infoVo = new InfoVo();
			 		infoVo.setId(object.getId());
			 		infoVo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
			 		infoVo.setContent(StringUtils.isNotBlank(object.getContent())?object.getContent():"");
			 		infoVo.setCreatetime(DateTimeTool.dateFormat(null, object.getCreatetime()));
			 		infoData.add(infoVo);
				}
			}
			
			HashMap<String, Object> info = new HashMap<String, Object>();
			info.put("page", page);
			info.put("limit", limit);
			info.put("count", qr.getResultCount());
			info.put("data", infoData);
	 		
			result.put("info", info);
	 		
	 		
	 		//==========================4聚合siteDomainList==========================
   			List<SiteDomain> siteDomainList = null;
   			switch (device) {
			case 0:
				siteDomainList = siteDomainService.findByHql("from SiteDomain where isOpenPC = 1 and station.id = "+stationId+" order by sortNum desc");
				break;
			case 1:
			case 2:
			case 3:
				siteDomainList = siteDomainService.findByHql("from SiteDomain where isOpenApp = 1 and station.id = "+stationId+" order by sortNum desc");
				break;
			}

   			List<SiteDomainVo> siteDomainVoDate = new ArrayList<SiteDomainVo>();
   			if(siteDomainList != null && siteDomainList.size() >0) {
			 	for (SiteDomain object : siteDomainList) {
			 		SiteDomainVo siteDomainVo = new SiteDomainVo();
			 		siteDomainVo.setId(object.getId());
			 		siteDomainVo.setSiteName(StringUtils.isNotBlank(object.getSiteName())?object.getSiteName():"");
			 		siteDomainVo.setSiteDomain(StringUtils.isNotBlank(object.getSiteDomain())?object.getSiteDomain():"");
			 		siteDomainVoDate.add(siteDomainVo);
				}
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> siteDomain = new HashMap<String, Object>();
   			siteDomain.put("data", siteDomainVoDate);
			
			result.put("siteDomain", siteDomain);
			
			
	 		//==========================5聚合getWinList==========================
			List<WinVo> winVoData = null;
			String key = "WINLIST:" + stationId;
			String dataStr = jedisClient.get(key);
			if(StringUtils.isBlank(dataStr)) {
				pageView = new PageView(18, 1);
				orderby = new LinkedHashMap<String, String>();
				orderby.put("id", "desc");
				
				sb = new StringBuilder();
				params = new ArrayList<Object>();

				sb.append(" and o.status = ?");
				params.add(2);
				
				sb.append(" and o.createTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.addTime(new Date(), -300));
		
				sb.append(" and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s')");
				params.add(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", new Date()));
				
				sb.append(" and o.user.station.id = ?");
				params.add(stationId);

				QueryResult<LotteryOrder> qr2 = lotteryOrderService.getScrollData(pageView.getFirstResult(), pageView.getLimit(), sb.toString(), params.toArray(), orderby);

				winVoData = new ArrayList<WinVo>();
				if(qr.getResultCount()>0) {
				 	for (LotteryOrder object : qr2.getResultData()) {
				 		WinVo winVo = new WinVo();
				 		winVo.setNickName(object.getUser().getNickName());
				 		winVo.setHillTitle(object.getLotteryType().getLotteryName());
				 		winVo.setBonus(object.getWinMoney().setScale(0, BigDecimal.ROUND_DOWN).toString());
				 		winVoData.add(winVo);
					}
				}
				
				//少用18条信息时，随机生成相差记录
				if(winVoData.size()<18) {
					int k = 18 - winVoData.size();
					WinVo winVo = null;
					
					String lotteryTypeStr = "";
					String curTime = DateTimeTool.dateFormat("HH:mm", new Date());
					
					//1北京28
					if(DateTimeTool.isInTime("09:5-23:56", curTime)){
						if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
						lotteryTypeStr = lotteryTypeStr.concat("1");
					}
					
					//2加拿大28
					String jndTimeInterval = systemConfigService.getValueByName("lottery_jnd_time_interval");
					if(StringUtils.isNotBlank(jndTimeInterval)&&"1".equals(jndTimeInterval)) {//夏令时
						if(!DateTimeTool.isInTime("19:00-21:00", curTime)){
							if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
							lotteryTypeStr = lotteryTypeStr.concat("2");
						}
					}else if(StringUtils.isNotBlank(jndTimeInterval)&&"2".equals(jndTimeInterval)){//冬令时
						if(!DateTimeTool.isInTime("20:00-22:00", curTime)){
							if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
							lotteryTypeStr = lotteryTypeStr.concat("2");
						}
					}
					
					//3幸运飞艇 13:00~04:00
					if(!DateTimeTool.isInTime("04:0-13:00", curTime)){
						if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
						lotteryTypeStr = lotteryTypeStr.concat("3");
					}
					
					//4北京赛车09:10~23:50
					if(DateTimeTool.isInTime("09:10-23:50", curTime)){
						if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
						lotteryTypeStr = lotteryTypeStr.concat("4");
					}
					
					//5重庆时时彩
					if(DateTimeTool.isInTime("07:10-23:49", curTime) || DateTimeTool.isInTime("00:30-03:00", curTime) ){
						if(StringUtils.isNotBlank(lotteryTypeStr)) lotteryTypeStr = lotteryTypeStr.concat(",");
						lotteryTypeStr = lotteryTypeStr.concat("5");
					}
					//System.out.println(lotteryTypeStr);
					if(StringUtils.isNotBlank(lotteryTypeStr)) {
						String hql = "from StationLotteryType o where o.station.id = "+stationId+" and o.status = 1 and o.lotteryType.id in ("+lotteryTypeStr+") order by sortNum desc";
						List<StationLotteryType> stationLotteryTypeList = stationLotteryTypeService.findByHql(hql);
						for (int i = 0; i < k; i++) {
							LotteryRobotPlant lotteryRobotPlant = lotteryRobotPlantService.find(RandomUtil.getRangeRandom(1,110));
							if(lotteryRobotPlant != null && stationLotteryTypeList != null) {
								winVo = new WinVo();
								winVo.setNickName(lotteryRobotPlant.getNickName());
								winVo.setHillTitle(stationLotteryTypeList.get(RandomUtil.getRangeRandom(0,stationLotteryTypeList.size())).getLotteryName());
						 		
						 		BigDecimal betMoney = new BigDecimal(RandomUtil.getRangeRandom(50,5000));//中奖
								betMoney = betMoney.compareTo(new BigDecimal(10))<0?new BigDecimal(10):betMoney;
								if(betMoney.compareTo(new BigDecimal(100))<0) {
									betMoney = betMoney.divide(new BigDecimal(10)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(10));
								}else {
									betMoney = betMoney.divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
								}
								winVo.setBonus(betMoney.toString());
								winVoData.add(winVo);
							}
						}
					}
				}
				jedisClient.set(key, JsonUtil.toJSONString(winVoData));
				jedisClient.expire(key, 60*1); //设置会话过期时间180秒=3分钟
			}else {
				winVoData = JsonUtil.toList(dataStr,WinVo.class);
			}
			
			HashMap<String, Object> win = new HashMap<String, Object>();
			win.put("data", winVoData);
			
			result.put("winList", win);
			
			
			//==========================5聚合systemConfig==========================
			List<StationConfig> configList = stationConfigService.getListByStationId(stationId);
   			List<SystemConfigVo> systemConfigVoData = new ArrayList<SystemConfigVo>();
   			if(configList != null && configList.size() >0) {
			 	for (StationConfig object : configList) {
			 		SystemConfigVo systemConfigVo = new SystemConfigVo();
			 		systemConfigVo.setTitle(StringUtils.isNotBlank(object.getTitle())?object.getTitle():"");
			 		systemConfigVo.setName(StringUtils.isNotBlank(object.getName())?object.getName():"");
			 		systemConfigVo.setValue(StringUtils.isNotBlank(object.getValue())?object.getValue():"");
			 		systemConfigVoData.add(systemConfigVo);
				}
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> stationConfig = new HashMap<String, Object>();
   			stationConfig.put("data", systemConfigVoData);
			
			result.put("stationConfig", stationConfig);
			
			
			//==========================6聚合lotteryTypeList==========================
   			List<StationLotteryType> lotteryTypeList = stationLotteryTypeService.getStationLotteryTypeByStationId(stationId);

   			List<LotteryTypeVo> lotteryTypeVoData = new ArrayList<LotteryTypeVo>();
   			if(lotteryTypeList != null && lotteryTypeList.size() >0) {
			 	for (StationLotteryType stationLotteryType : lotteryTypeList) {
			 		LotteryTypeVo lotteryTypeVo = new LotteryTypeVo();
			 		lotteryTypeVo.setId(stationLotteryType.getLotteryType().getId());
			 		lotteryTypeVo.setTitle(StringUtils.isNotBlank(stationLotteryType.getLotteryName())?stationLotteryType.getLotteryName():"");
			 		lotteryTypeVo.setRemarks(StringUtils.isNotBlank(stationLotteryType.getLotteryType().getRemarks())?stationLotteryType.getLotteryType().getRemarks():"");
			 		lotteryTypeVo.setStatus(stationLotteryType.getStatus());
			 		lotteryTypeVo.setPicLink(StringUtils.isNotBlank(stationLotteryType.getPicLink())?ApiUtil.getResServerLink(resServerLink, stationLotteryType.getStation().getImageDomain())+stationLotteryType.getPicLink():"");
			 		lotteryTypeVoData.add(lotteryTypeVo);
				}
   			}else {
   				throw new BusinessException(ApiUtil.getErrorCode("105"));
   			}
   			
   			Map<String, Object> lotteryType = new HashMap<String, Object>();
   			lotteryType.put("data", lotteryTypeVoData);
			
			result.put("lotteryType", lotteryType);

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
