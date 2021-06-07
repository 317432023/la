package com.jeetx.controller.notify;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jeetx.common.pay.alipays.AlipaysToolKit;
import com.jeetx.common.pay.jdzf.JdzfToolKit;
import com.jeetx.common.pay.lyf.LyfToolKit;
import com.jeetx.common.pay.xPay.XpayToolKit;
import com.jeetx.common.pay.xl.XlToolKit;
import com.jeetx.common.pay.ytb.YtbToolKit;
import com.jeetx.service.member.RechargeService;
import com.jeetx.util.HttpUtil;
import com.jeetx.util.LogUtil;
import com.jeetx.util.MD5Util;

@Controller
@RequestMapping("/pay/return")
public class PayReturnController {
	@Autowired RechargeService rechargeService;
	
    /**龙易付支付通知*/
	@RequestMapping(value = "/lyf")
	public String lyf(Model model, HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String order_no,
			@RequestParam(required=true) String pay_status,@RequestParam(required=true) String sign) {
		String result = "支付中";
		try {
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("order_no", order_no);
			metaSignMap.put("pay_status", pay_status);
			
			String localSign = LyfToolKit.generateSign(metaSignMap);
			if(sign.equals(localSign)) {
				if("1".equalsIgnoreCase(pay_status)) {
					rechargeService.onlineRechargeSuccess(order_no,10,null);
					result = "交易成功";
				}else{
					result = "交易失败";
				}
			}else {
				result = "签名错误";
				LogUtil.info("龙易付同步返回签名错误");
			}
		} catch (Exception e) {
			result = "处理异常:"+e;
			LogUtil.info("龙易付同步通知处理异常", e);
		}
		
		model.addAttribute("result", result);
		return "pay/result";
	}
	
    /**易通宝支付通知*/
	@RequestMapping(value = "/ytb")
	public String ytb(Model model, HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String status,
			@RequestParam(required=true) String customerid,@RequestParam(required=true) String sdpayno,@RequestParam(required=true) String sdorderno,
			@RequestParam(required=true) String total_fee,@RequestParam(required=true) String paytype,
			@RequestParam(required=true) String remark,@RequestParam(required=true) String sign) {
		String result = "支付中";
		try {
			String signStr = "customerid="+customerid+"&status="+status+"&sdpayno="+sdpayno+"&sdorderno="+sdorderno+"&total_fee="+total_fee+"&paytype="+paytype+"&"+YtbToolKit.APP_SECECT;
			if(sign.equals( MD5Util.MD5Encode(signStr, "utf-8"))) {
				if("1".equalsIgnoreCase(status)) {
					rechargeService.onlineRechargeSuccess(sdorderno,11,new BigDecimal(total_fee));
					result = "交易成功";
				}else {
					result = "交易失败";
				}
			}else {
				result = "签名错误";
				LogUtil.info("易通宝异步返回签名错误");
			}
		} catch (Exception e) {
			result = "处理异常:"+e;
			LogUtil.info("易通宝同步通知处理异常", e);
		}
		
		model.addAttribute("result", result);
		return "pay/result";
	}

    /**喜力支付通知*/
	@RequestMapping(value = "/xl")
	public String xl(Model model,HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String merOrderNo,
			@RequestParam(required=true) String orderPrice,@RequestParam(required=true) String retCode,@RequestParam(required=true) String status,
			@RequestParam(required=true) String sign) {
		String result = "支付中";
		try {
			Map<String, String> parms = new TreeMap<String, String>();
			parms.put("merOrderNo", merOrderNo);
			parms.put("orderPrice", orderPrice);
			parms.put("status", status);
			parms.put("retCode", retCode);
			String signStr = XlToolKit.generateSign(parms);
			
			if(sign.equals(signStr)) {
				if("SUCCESS".equalsIgnoreCase(status)) {
					//rechargeService.onlineRechargeSuccess(merOrderNo,12,new BigDecimal(orderPrice));
					result = "交易成功";
				}else {
					result = "交易失败";
				}
			}else {
				result = "签名错误";
				LogUtil.info("喜力异步返回签名错误");
			}
		} catch (Exception e) {
			result = "处理异常:"+e;
			LogUtil.info("喜力同步通知处理异常", e);
		}
		
		model.addAttribute("result", result);
		return "pay/result";
	}
	
	/**阿里付通知*/
	@RequestMapping(value = "/alipays")
	public String alipays(Model model,HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String memberid,
			@RequestParam(required=true) String orderid,@RequestParam(required=true) String amount,@RequestParam(required=true) String transaction_id,
			@RequestParam(required=true) String datetime,@RequestParam(required=true) String returncode,@RequestParam(required=false) String attach,@RequestParam(required=true) String sign) {
		String result = "支付中";
		try {
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("memberid", AlipaysToolKit.PAY_MEMBERID);
			metaSignMap.put("orderid", orderid);
			metaSignMap.put("amount", amount);
			metaSignMap.put("transaction_id", transaction_id);
			metaSignMap.put("datetime", datetime);
			metaSignMap.put("returncode", returncode);
			String signStr = AlipaysToolKit.generateSign(metaSignMap);
			
			if(sign.equals(signStr)) {
				if("00".equalsIgnoreCase(returncode)) {
					result = "交易成功";
				}else {
					result = "交易失败";
				}
			}else {
				result = "签名错误";
				LogUtil.info("阿里付异步返回签名错误");
			}
		} catch (Exception e) {
			result = "处理异常:"+e;
			LogUtil.info("阿里付同步通知处理异常", e);
		}
		
		model.addAttribute("result", result);
		return "pay/result";
	}
	
    /**金樽支付通知*/
	@RequestMapping(value = "/jdzf")
	public String jdzf(Model model,HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String businessId,
			@RequestParam(required=true) String amount,@RequestParam(required=true) String outTradeNo,@RequestParam(required=true) String tradeNo,
			@RequestParam(required=true) String orderState,@RequestParam(required=true) String random,@RequestParam(required=true) String sign) {
		String result = "等待支付";
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
					result = "交易成功";
				}else if("1".equalsIgnoreCase(orderState)){
					result = "超时失败";
				}
			}else {
				result = "签名错误";
				LogUtil.info("金樽支付同步返回签名错误");
			}
		} catch (Exception e) {
			result = "处理异常:"+e;
			LogUtil.info("金樽支付同步通知处理异常", e);
		}
		
		model.addAttribute("result", result);
		return "pay/result";
	}
	
    /**星支付通知*/
	@RequestMapping(value = "/xpay")
	public String xpay(Model model,HttpServletRequest request, HttpServletResponse response,@RequestParam(required=true) String shid,
			@RequestParam(required=true) String orderid,@RequestParam(required=true) String amount,@RequestParam(required=true) String trueamount,
			@RequestParam(required=true) String status,@RequestParam(required=true) String sign) {
		String result = "等待支付";
		try {
			Map<String, String> metaSignMap = new TreeMap<String, String>();
			metaSignMap.put("shid", XpayToolKit.SHID);
			metaSignMap.put("orderid", orderid);
			metaSignMap.put("amount", amount);
			metaSignMap.put("trueamount", trueamount);
			String localSign = XpayToolKit.generateSign(metaSignMap);
			
			if(sign.equals(localSign)) {
				if("3".equalsIgnoreCase(status)) {
					result = "交易成功";
				}else {
					result = "交易失败";
				}
			}else {
				result = "签名错误";
				LogUtil.info("星支付同步返回签名错误");
			}
		} catch (Exception e) {
			result = "处理异常:"+e;
			LogUtil.info("星支付同步通知处理异常", e);
		}
		
		model.addAttribute("result", result);
		return "pay/result";
	}

    public static void main(String[] args) {
		String url = "http://localhost:8080/la-api/pay/return/lyf?order_no=2&pay_status=2&sign=3";
		String resultJsonStr = new HttpUtil().getMethod(url, "UTF-8");
		System.out.println(resultJsonStr);
    	
	}
}
