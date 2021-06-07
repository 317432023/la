package com.jeetx.service.member;

import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DAO;

public interface UserService extends DAO<User> {
	public User findUser(String username);
	public User checkUser(String username,String password);
	public User checkUserByToken(String username,String loginToken);
}
