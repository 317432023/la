package com.jeetx.controller.notify;

import com.jeetx.common.model.dto.FbdNotifyModel;
import com.jeetx.common.model.json.AjaxJson;
import com.jeetx.common.pay.alipays.AlipaysToolKit;
import com.jeetx.common.pay.bf.BfToolKit;
import com.jeetx.common.pay.fbd.FbdToolKit;
import com.jeetx.common.pay.ifuniu.IfuniuToolKit;
import com.jeetx.common.pay.jdzf.JdzfToolKit;
import com.jeetx.common.pay.lyf.LyfToolKit;
import com.jeetx.common.pay.mjf.MfjToolKit;
import com.jeetx.common.pay.msf.MsfToolKit;
import com.jeetx.common.pay.xPay.XpayToolKit;
import com.jeetx.common.pay.xl.XlToolKit;
import com.jeetx.common.pay.yfb.YfbToolKit;
import com.jeetx.common.pay.ytb.YtbToolKit;
import com.jeetx.controller.api.ApiUtil;
import com.jeetx.service.member.RechargeService;
import com.jeetx.util.HttpUtil;
import com.jeetx.util.LogUtil;
import com.jeetx.util.MD5Util;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pay/notify")
public class PayNotifyController {
	
	@Autowired RechargeService rechargeService;
	
    /**明捷支付通知*/
	@RequestMapping(value = "/mjf")
	public void mjf(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String data) {
		try {
			LogUtil.info("明捷支付解密前报文=" + data);
			byte[] result = MfjToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(data), MfjToolKit.PRIVATE_KEY);
			String resultData = new String(result, MfjToolKit.CHARSET);// 解密数据

			JSONObject jsonObj = JSONObject.fromObject(resultData);
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("merchNo", jsonObj.getString("merchNo"));
			metaSignMap.put("netwayCode", jsonObj.getString("netwayCode"));
			metaSignMap.put("orderNum", jsonObj.getString("orderNum"));
			metaSignMap.put("amount", jsonObj.getString("amount"));
			metaSignMap.put("goodsName", jsonObj.getString("goodsName"));
			metaSignMap.put("payStateCode", jsonObj.getString("payStateCode"));// 支付状态
			metaSignMap.put("payDate", jsonObj.getString("payDate"));// yyyyMMddHHmmss
			String jsonStr = MfjToolKit.mapToJson(metaSignMap);
			LogUtil.info("明捷支付解密后报文=" + jsonStr);
			
			String sign = MfjToolKit.MD5(jsonStr.toString() + MfjToolKit.KEY, MfjToolKit.CHARSET);
			if (sign.equals(jsonObj.getString("sign"))) {//签名校验成功
				rechargeService.onlineRechargeSuccess(jsonObj.getString("orderNum"),6,null);
				response.getOutputStream().write("SUCCESS".getBytes());
			}
		} catch (Exception e) {
			LogUtil.info("明捷支付异步通知处理异常", e);
		}
	}
	
    /**佰富支付通知*/
	@RequestMapping(value = "/bf")
	public void bf(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String paramData) {
		try {
			LogUtil.info("佰富支付解密前报文=" + paramData);
			JSONObject jsonObj = JSONObject.fromObject(paramData);
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("merchantNo", jsonObj.getString("merchantNo"));
			metaSignMap.put("netwayCode", jsonObj.getString("netwayCode"));
			metaSignMap.put("orderNum", jsonObj.getString("orderNum"));
			metaSignMap.put("payAmount", jsonObj.getString("payAmount"));
			metaSignMap.put("goodsName", jsonObj.getString("goodsName"));
			metaSignMap.put("resultCode", jsonObj.getString("resultCode"));// 支付状态
			metaSignMap.put("payDate", jsonObj.getString("payDate"));// yyyy-MM-dd HH:mm:ss
			
			String jsonStr = BfToolKit.mapToJson(metaSignMap);
			String sign = BfToolKit.MD5(jsonStr.toString() + BfToolKit.KEY, "UTF-8");
			if(sign.equals(jsonObj.getString("sign"))){
				rechargeService.onlineRechargeSuccess(jsonObj.getString("orderNum"),8,null);
				response.getOutputStream().write("000000".getBytes());//强制要求返回000000
			}
		} catch (Exception e) {
			LogUtil.info("佰富支付异步通知处理异常", e);
		}
	}
	
	
    /**码上付支付通知*/
	@RequestMapping(value = "/msf")
	public void msf(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String aoid,
			@RequestParam(required=true) String order_id,@RequestParam(required=true) String extend,@RequestParam(required=true) String price,
			@RequestParam(required=true) String pay_price,@RequestParam(required=true) String sign) {
		try {
			if(sign.equals(MsfToolKit.getMD5(aoid+order_id+extend+price+pay_price).toLowerCase())) {
				rechargeService.onlineRechargeSuccess(order_id,9,null);
				response.getOutputStream().write("SUCCESS".getBytes());
			}else {
				LogUtil.info("码上付异步返回签名错误");
			}
		} catch (Exception e) {
			LogUtil.info("码上付异步通知处理异常", e);
		}
	}

	/**=========================================================================================**/
	
    /**龙易付支付通知*/
	@RequestMapping(value = "/lyf")
	public void lyf(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String appid,
			@RequestParam(required=true) String order_no,@RequestParam(required=true) String amount,@RequestParam(required=true) String platform_order_no,
			@RequestParam(required=true) String attach,@RequestParam(required=true) String pay_time,
			@RequestParam(required=true) String pay_status,@RequestParam(required=true) String actual_amount,@RequestParam(required=true) String sign) {
		try {
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("appid", LyfToolKit.APPID);
			metaSignMap.put("order_no", order_no);
			metaSignMap.put("amount", amount);
			metaSignMap.put("platform_order_no", platform_order_no);
			metaSignMap.put("attach", attach);
			metaSignMap.put("pay_time", pay_time);
			metaSignMap.put("pay_status", pay_status);
			metaSignMap.put("actual_amount", actual_amount);
			
			String localSign = LyfToolKit.generateSign(metaSignMap);
			if(sign.equals(localSign)) {
				if("1".equalsIgnoreCase(pay_status)) {
					rechargeService.onlineRechargeSuccess(order_no,10,null);
				}
				response.getOutputStream().write("SUCCESS".getBytes());
			}else {
				LogUtil.info("龙易付异步返回签名错误");
			}
		} catch (Exception e) {
			LogUtil.info("龙易付异步通知处理异常", e);
		}
	}
	
    /**易通宝支付通知*/
	@RequestMapping(value = "/ytb")
	public void ytb(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String status,
			@RequestParam(required=true) String customerid,@RequestParam(required=true) String sdpayno,@RequestParam(required=true) String sdorderno,
			@RequestParam(required=true) String total_fee,@RequestParam(required=true) String paytype,
			@RequestParam(required=true) String remark,@RequestParam(required=true) String sign) {
		try {
			String signStr = "customerid="+customerid+"&status="+status+"&sdpayno="+sdpayno+"&sdorderno="+sdorderno+"&total_fee="+total_fee+"&paytype="+paytype+"&"+YtbToolKit.APP_SECECT;
			LogUtil.info("易通宝异步回执=" + signStr);
			if(sign.equals( MD5Util.MD5Encode(signStr, "utf-8"))) {
				if("1".equalsIgnoreCase(status)) {
					rechargeService.onlineRechargeSuccess(sdorderno,11,new BigDecimal(total_fee));
				}
				response.getOutputStream().write("success".getBytes());
			}else {
				LogUtil.info("易通宝异步返回签名错误");
			}
		} catch (Exception e) {
			LogUtil.info("易通宝异步通知处理异常", e);
		}
	}
	
    /**喜力通知*/
	@RequestMapping(value = "/xl")
	public void xl(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String merOrderNo,
			@RequestParam(required=true) String orderPrice,@RequestParam(required=true) String retCode,@RequestParam(required=true) String status,
			@RequestParam(required=true) String sign) {
		try {
			Map<String, String> parms = new TreeMap<String, String>();
			parms.put("merOrderNo", merOrderNo);
			parms.put("orderPrice", orderPrice);
			parms.put("status", status);
			parms.put("retCode", retCode);
			String signStr = XlToolKit.generateSign(parms);
			
			LogUtil.info("喜力异步回执=" + new HttpUtil().mapToString(parms));
			if(sign.equals(signStr)) {
				if("SUCCESS".equalsIgnoreCase(status)) {
					rechargeService.onlineRechargeSuccess(merOrderNo,12,new BigDecimal(orderPrice));
					response.getOutputStream().write("success".getBytes());
				}
			}else {
				LogUtil.info("喜力异步返回签名错误");
			}
		} catch (Exception e) {
			LogUtil.info("喜力异步通知处理异常", e);
		}
	}
	
    /**阿里付通知*/
	@RequestMapping(value = "/alipays")
	public void alipays(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String memberid,
			@RequestParam(required=true) String orderid,@RequestParam(required=true) String amount,@RequestParam(required=true) String transaction_id,
			@RequestParam(required=true) String datetime,@RequestParam(required=true) String returncode,@RequestParam(required=false) String attach,@RequestParam(required=true) String sign) {
		try {
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("memberid", AlipaysToolKit.PAY_MEMBERID);
			metaSignMap.put("orderid", orderid);
			metaSignMap.put("amount", amount);
			metaSignMap.put("transaction_id", transaction_id);
			metaSignMap.put("datetime", datetime);
			metaSignMap.put("returncode", returncode);
			String signStr = AlipaysToolKit.generateSign(metaSignMap);
			
			LogUtil.info("阿里付异步回执=" + new HttpUtil().mapToString(metaSignMap));
			if(sign.equals(signStr)) {
				if("00".equalsIgnoreCase(returncode)) {
					rechargeService.onlineRechargeSuccess(orderid,13,new BigDecimal(amount));
					response.getOutputStream().write("OK".getBytes());
				}
			}else {
				LogUtil.info("喜力异步返回签名错误");
			}
		} catch (Exception e) {
			LogUtil.info("喜力异步通知处理异常", e);
		}
	}
	
    /**丽狮通知*/
	@RequestMapping(value = "/ifuniu")
	public void ifuniu(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String paramData) {
		try {
			LogUtil.info("丽狮支付通知JOSN报文=" + paramData);
			JSONObject resultJsonObj = JSONObject.fromObject(paramData);
			String isSuccess = resultJsonObj.getString("success");
			if("true".equalsIgnoreCase(isSuccess)) {
				JSONObject dataObject = resultJsonObj.getJSONObject("data");
				
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("merchant_no", IfuniuToolKit.PAY_MEMBERID);
				metaSignMap.put("status", dataObject.getString("status"));
				metaSignMap.put("request_no", dataObject.getString("request_no"));
				metaSignMap.put("request_time", dataObject.getString("request_time"));
				metaSignMap.put("order_no", dataObject.getString("order_no"));
				metaSignMap.put("pay_time", dataObject.getString("pay_time"));
				metaSignMap.put("amount", dataObject.getString("amount"));
				metaSignMap.put("order_amount", dataObject.getString("order_amount"));
				metaSignMap.put("pay_channel", dataObject.getString("pay_channel"));
				metaSignMap.put("nonce_str", dataObject.getString("nonce_str"));
				metaSignMap.put("call_nums", dataObject.getString("call_nums"));

				String signStr = metaSignMap.put("sign", IfuniuToolKit.generateSign(metaSignMap));
				
				LogUtil.info("丽狮异步回执=" + new HttpUtil().mapToString(metaSignMap));
				if(dataObject.getString("sign").equals(signStr)) {
					if("3".equalsIgnoreCase(dataObject.getString("status"))) {
						rechargeService.onlineRechargeSuccess(dataObject.getString("request_no"),14,new BigDecimal(dataObject.getString("order_amount")));
						response.getOutputStream().write("SUCCESS".getBytes());
					}
				}else {
					LogUtil.info("丽狮返回签名错误");
				}
				
				
			}
		} catch (Exception e) {
			LogUtil.info("丽狮通知处理异常", e);
		}
	}
	
    /**金樽支付通知*/
	@RequestMapping(value = "/jdzf")
	public void jdzf(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String businessId,
			@RequestParam(required=true) String amount,@RequestParam(required=true) String outTradeNo,@RequestParam(required=true) String tradeNo,
			@RequestParam(required=true) String orderState,@RequestParam(required=true) String random,@RequestParam(required=true) String sign) {
		try {
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("businessId", JdzfToolKit.MERCHANT_ID);
			metaSignMap.put("amount", amount);
			metaSignMap.put("outTradeNo", outTradeNo);
			metaSignMap.put("tradeNo", tradeNo);
			metaSignMap.put("orderState", orderState );
			metaSignMap.put("random", random);
			metaSignMap.put("secret", JdzfToolKit.MERCHANT_SECRET);
			
			String localSign = JdzfToolKit.generateSign(metaSignMap);
			if(sign.equals(localSign)) {
				if("2".equalsIgnoreCase(orderState)) {
					rechargeService.onlineRechargeSuccess(outTradeNo,15,new BigDecimal(amount));
				}
				response.getOutputStream().write("SUCCESS".getBytes());
			}else {
				LogUtil.info("金樽支付异步返回签名错误:"+new HttpUtil().mapToString(metaSignMap) + "" + sign + "" + localSign);
				response.getOutputStream().write("FAIL".getBytes());
			}
		} catch (Exception e) {
			LogUtil.info("金樽支付异步通知处理异常", e);
		}
	}
	
    /**星支付通知*/
	@RequestMapping(value = "/xpay")
	public void xpay(HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String shid,
			@RequestParam(required=true) String orderid,@RequestParam(required=true) String amount,@RequestParam(required=true) String trueamount,
			@RequestParam(required=true) String status,@RequestParam(required=true) String sign) {
		try {
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("shid", XpayToolKit.SHID);
			metaSignMap.put("orderid", orderid);
			metaSignMap.put("amount", amount);
			metaSignMap.put("trueamount", trueamount);
			String localSign = XpayToolKit.generateSign(metaSignMap);
			
			if(sign.equals(localSign)) {
				if("3".equalsIgnoreCase(status)) {
					rechargeService.onlineRechargeSuccess(orderid,16,new BigDecimal(amount));
				}
				response.getOutputStream().write("ok".getBytes());
			}else {
				LogUtil.info("星支付异步返回签名错误:"+new HttpUtil().mapToString(metaSignMap) + "" + sign + "" + localSign);
				response.getOutputStream().write("fail".getBytes());
			}
		} catch (Exception e) {
			LogUtil.info("星支付异步通知处理异常", e);
		}
	}

	/**付必达通知*/
	@RequestMapping(value="/fbd2", method= RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public void fbd2(HttpServletRequest request, HttpServletResponse response,  @RequestBody FbdNotifyModel fbdNotifyModel) {
		try {
			System.out.println( fbdNotifyModel.getOrderId());
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("partnerId", FbdToolKit.PARTNERID);
			metaSignMap.put("orderId", fbdNotifyModel.getOrderId());
			metaSignMap.put("orderCode", fbdNotifyModel.getOrderCode());
			metaSignMap.put("userId", fbdNotifyModel.getUserId());
			metaSignMap.put("gameCode", fbdNotifyModel.getGameCode());
			metaSignMap.put("createDate", fbdNotifyModel.getCreateDate());
			metaSignMap.put("updateDate", fbdNotifyModel.getUpdateDate());
			metaSignMap.put("amounts", fbdNotifyModel.getAmounts());
			metaSignMap.put("status", fbdNotifyModel.getStatus());
			metaSignMap.put("remark", fbdNotifyModel.getRemark());
			metaSignMap.put("exp", fbdNotifyModel.getExp());
			metaSignMap.put("tokenId", fbdNotifyModel.getTokenId());
			metaSignMap.put("flag", fbdNotifyModel.getFlag());

			LogUtil.info("付必达回执Url"+ new HttpUtil().mapToString(metaSignMap));
			String localSign = FbdToolKit.generateSign(metaSignMap);

			if(fbdNotifyModel.getSign().equals(localSign)) {
				if("2".equalsIgnoreCase(fbdNotifyModel.getStatus())) {//订单状态（-2：错误，-1：取消，0：待⽀付，1：待收款，2：已收款） 详情可参阅附注-订单状态
					rechargeService.onlineRechargeSuccess(fbdNotifyModel.getOrderId(),17,new BigDecimal(fbdNotifyModel.getAmounts()));
					response.getOutputStream().write("{'status': '1','msg': 'success'} ".getBytes());
				}else{
					response.getOutputStream().write("{'status': '0','msg': '未支付'} ".getBytes());
				}
			}else {
				LogUtil.info("付必达异步返回签名错误:"+new HttpUtil().mapToString(metaSignMap) + "" + fbdNotifyModel.getSign() + "" + localSign);
				response.getOutputStream().write("{'status': '0','msg': '签名错误'} ".getBytes());
			}
		} catch (Exception e) {
			LogUtil.info("付必达异步通知处理异常", e);
		}
	}

	/**付必达通知*/
	@RequestMapping(value="/fbd1", method= RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public void fbd1(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject jsonObject) {
		try {
			System.out.println( jsonObject.getString("orderId"));
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("partnerId", FbdToolKit.PARTNERID);
			metaSignMap.put("orderId", jsonObject.getString("orderId"));
			metaSignMap.put("orderCode", jsonObject.getString("orderCode"));
			metaSignMap.put("userId", jsonObject.getString("userId"));
			metaSignMap.put("gameCode", jsonObject.getString("gameCode"));
			metaSignMap.put("createDate", jsonObject.getString("createDate"));
			metaSignMap.put("updateDate", jsonObject.getString("updateDate"));
			metaSignMap.put("amounts", jsonObject.getString("amounts"));
			metaSignMap.put("status", jsonObject.getString("status"));
			metaSignMap.put("remark", jsonObject.getString("remark"));
			metaSignMap.put("exp", jsonObject.getString("exp"));
			metaSignMap.put("tokenId", jsonObject.getString("tokenId"));
			metaSignMap.put("flag", jsonObject.getString("flag"));

			LogUtil.info("付必达回执Url"+ new HttpUtil().mapToString(metaSignMap));
			String localSign = FbdToolKit.generateSign(metaSignMap);

			if(jsonObject.getString("sign").equals(localSign)) {
				if("2".equalsIgnoreCase(jsonObject.getString("status"))) {//订单状态（-2：错误，-1：取消，0：待⽀付，1：待收款，2：已收款） 详情可参阅附注-订单状态
					rechargeService.onlineRechargeSuccess(jsonObject.getString("orderCode"),17,new BigDecimal(jsonObject.getString("amounts")));
					response.getOutputStream().write("{'status': '1','msg': 'success'} ".getBytes());
				}else{
					response.getOutputStream().write("{'status': '0','msg': '未支付'} ".getBytes());
				}
			}else {
				LogUtil.info("付必达异步返回签名错误:"+new HttpUtil().mapToString(metaSignMap) + "" + jsonObject.getString("sign")+ "" + localSign);
				response.getOutputStream().write("{'status': '0','msg': '签名错误'} ".getBytes());
			}
		} catch (Exception e) {
			LogUtil.info("付必达异步通知处理异常", e);
		}
	}

	/**付必达通知*/
	@RequestMapping(value="/fbd", method= RequestMethod.POST)
	@ResponseBody
	public void fbd(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject jsonObject = new JSONObject();
			Iterator<String> it=request.getParameterMap().keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = ((Object[]) (request.getParameterMap().get(key)))[0].toString();
				jsonObject.put(key,value);
				System.out.println(key + "=" + value);
			}

			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("partnerId", FbdToolKit.PARTNERID);
			metaSignMap.put("orderId", jsonObject.getString("orderId"));
			metaSignMap.put("orderCode", jsonObject.getString("orderCode"));
			metaSignMap.put("userId", jsonObject.getString("userId"));
			metaSignMap.put("gameCode", jsonObject.getString("gameCode"));
			metaSignMap.put("createDate", jsonObject.getString("createDate"));
			metaSignMap.put("updateDate", jsonObject.getString("updateDate"));
			metaSignMap.put("amounts", jsonObject.getString("amounts"));
			metaSignMap.put("status", jsonObject.getString("status"));
			metaSignMap.put("remark", jsonObject.getString("remark"));
			metaSignMap.put("exp", jsonObject.getString("exp"));
			metaSignMap.put("tokenId", jsonObject.getString("tokenId"));
			metaSignMap.put("flag", jsonObject.getString("flag"));

			LogUtil.info("付必达回执Url"+ new HttpUtil().mapToString(metaSignMap));
			String localSign = FbdToolKit.generateSign(metaSignMap);

			if(jsonObject.getString("sign").equals(localSign)) {
				if("2".equalsIgnoreCase(jsonObject.getString("status"))) {//订单状态（-2：错误，-1：取消，0：待⽀付，1：待收款，2：已收款） 详情可参阅附注-订单状态
					rechargeService.onlineRechargeSuccess(jsonObject.getString("orderCode"),17,new BigDecimal(jsonObject.getString("amounts")));
					response.getOutputStream().write("{'status': '1','msg': 'success'} ".getBytes());
				}else{
					response.getOutputStream().write("{'status': '0','msg': '未支付'} ".getBytes());
				}
			}else {
				LogUtil.info("付必达异步返回签名错误:"+new HttpUtil().mapToString(metaSignMap) + "" + jsonObject.getString("sign")+ "" + localSign);
				response.getOutputStream().write("{'status': '0','msg': '签名错误'} ".getBytes());
			}
		} catch (Exception e) {
			LogUtil.info("付必达异步通知处理异常", e);
		}
	}

	/**优付宝通知*/
	@RequestMapping(value = "/yfb")
	public void yfb(HttpServletRequest request, HttpServletResponse response) {
		try {
			LogUtil.info("优付宝回执Url"+ApiUtil.getRequestUrl(request));
			Map<String, String> metaSignMap = ApiUtil.getParameterMap(request);
			metaSignMap.remove("sign");

			String amount = request.getParameter("amount");
			String orderNo = request.getParameter("orderNo");

			String status = request.getParameter("status");
			String sign = request.getParameter("sign");
			String localSign = YfbToolKit.generateSign(metaSignMap);

			if(sign.equals(localSign)) {
				if("PAID".equalsIgnoreCase(status)) {
					rechargeService.onlineRechargeSuccess(orderNo,18,new BigDecimal(amount));
					response.getOutputStream().write("success".getBytes());
				}else{
					response.getOutputStream().write("fail".getBytes());
				}
			}else {
				LogUtil.info("优付宝异步返回签名错误:"+new HttpUtil().mapToString(metaSignMap) + "" + sign + "" + localSign);
				response.getOutputStream().write("fail".getBytes());
			}
		} catch (Exception e) {
			LogUtil.info("优付宝异步通知处理异常", e);
		}
	}
	
    public static void main(String[] args) {
		String url = "http://localhost:8080/la-api/pay/notify/fbd?order_no=2&pay_status=2&sign=3";
		String resultJsonStr = new HttpUtil().getMethod(url, "UTF-8");
		System.out.println(resultJsonStr);

			//			for (Iterator iter = metaSignMap.entrySet().iterator(); iter.hasNext(); ) {
//				Map.Entry element = (Map.Entry) iter.next();
//				Object strKey = element.getKey();
//				System.out.println(strKey+"-"+element.getValue());
//			}

//		Map<String, String> metaSignMap = new TreeMap<String, String>();
//		metaSignMap.put("businessId", "10155");
//		metaSignMap.put("amount", "2000.00");
//		metaSignMap.put("outTradeNo", "R20200228182517537606");
//		metaSignMap.put("tradeNo", "JZ1015520200228062525299877998");
//		metaSignMap.put("orderState","2");
//		metaSignMap.put("random", "1582885986016");
//		metaSignMap.put("secret", JdzfToolKit.MERCHANT_SECRET);
//		
//		String localSign = JdzfToolKit.generateSign(metaSignMap);
//		System.out.println(localSign);
		
		//391649e08a2b47c53c7bd456755ef1fb
//
//		Map<String, String> metaSignMap = new TreeMap<String, String>();
//		metaSignMap.put("businessId", "10155");
//		metaSignMap.put("amount", "5000.00");
//		metaSignMap.put("outTradeNo", "R20200229132151724108");
//		metaSignMap.put("tradeNo", "JZ1015520200229012200096744632");
//		metaSignMap.put("orderState","2");
//		metaSignMap.put("random", "1582955115015");
//		metaSignMap.put("secret", JdzfToolKit.MERCHANT_SECRET);
//
//		String localSign = JdzfToolKit.generateSign(metaSignMap);
//		System.out.println(localSign);
//		//645c211f0eb64ccec3eaa401acbfa0fd
    	
	}
}
