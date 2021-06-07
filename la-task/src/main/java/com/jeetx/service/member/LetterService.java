package com.jeetx.service.member;

import com.jeetx.bean.member.Letter;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DAO;

public interface LetterService extends DAO<Letter> {
	public Letter findLetter(String username,Integer letterId);
	public void sendLetter(User user,String title,String contents);
}
