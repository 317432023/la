package com.jeetx.service.member;

import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DAO;

public interface UserService extends DAO<User> {
	public User findUserByToken(String loginToken) ;
}
