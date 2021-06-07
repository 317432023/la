package com.jeetx.service.member;

import java.math.BigDecimal;
import java.util.Date;

import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DAO;

public interface UserService extends DAO<User> {
	public void validateUser(User user,Boolean validateDisable,Boolean validateDreeze);
	public User findUserByToken(String loginToken);
	public Long getExpandUserNum(Integer parentId) ;
	public User findUserByNickName(String nickName,Integer stationId);
	public User findUser(String username,Integer stationId);
	public User checkUser(String username,String password,Integer stationId);
	public User register(Integer pid,String username,String phone,String referee,String wechat,
			Integer userType,String password,String device,Integer stationId,String pUsername) throws Exception;
	public User login(String username,String password,String ip,String device,Integer stationId);
	public User checkUserByToken(String username,String loginToken,Integer stationId);
	public User checkToken(String username,String token,Integer stationId);
	public String resetToken(User user);
	public User touristLogin(String device,Integer stationId);
	public User getUserInfo(String username,String token,String ip,String device,Integer stationId) ;
	public void updateNickName(User user,String ip,String device,String nickName,Integer stationId);
	public void updateHeadImg(User user,String ip,String device,String headImg,Integer stationId);
	public void logout(String username,String token,String ip,String device,Integer stationId);
	public void updatePwd(User user,String ip,String device,Integer type,String password,String newPassword,Integer stationId);
	public User addSalesman(User user,String username,String password,String ip,String device,Integer stationId) throws Exception ;
	public User addVirtualUser(User user,String username,String password,String isInitOrder,String cardJson,
			String ip,String device,Integer stationId) throws Exception ;
	public User disableUser(User user,String username,String ip,String device,Integer stationId) throws Exception;
	public void subTransfer(Integer stationId,Integer uid,Integer pid) throws Exception;
	public User weChatLogin(String openId,String nickname,String headImg,String ip,String device,Integer stationId);
	public User checkUser(String wechatOpenId,Integer stationId);
	public void initVirtualData(User user,Date beginDate,Boolean isRecharge,BigDecimal rechargeAmount,Boolean isWithdraw,
			String ip,String device,Integer stationId,Boolean joinActivity) throws Exception;
	public void kickOutUsers(String username,Integer stationId);
	public void initVirtualDataV2(User user,Date beginDate,Boolean isRecharge,BigDecimal rechargeAmount,Boolean isGiveLotteryAmount,BigDecimal giveLotteryAmount,Boolean isWithdraw,
			String ip,String device,Integer stationId,Boolean joinActivity) throws Exception ;
  public void initVirtualDataV3(User user,Date beginDate,Boolean isRecharge,BigDecimal rechargeAmount,Boolean isWithdraw,
                                String ip,String device,Integer stationId,Boolean joinActivity) throws Exception ;
	public void initVirtualDataV4(User user,Date beginDate,Boolean isRecharge,BigDecimal rechargeAmount,Boolean isGiveLotteryAmount,BigDecimal giveLotteryAmount,Boolean isWithdraw,
																String ip,String device,Integer stationId,Boolean joinActivity) throws Exception ;
}
