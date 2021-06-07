package com.jeetx.service.member.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.member.Letter;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.member.LetterService;

@Service
@Transactional
public class LetterServiceImpl extends DaoSupport<Letter> implements LetterService {

	@SuppressWarnings("unchecked")
	public Letter findLetter(String username,Integer letterId) {
		List<Letter> list = this.getSession().createQuery("from Letter o where o.user.username = ? and o.id = ? ")
				.setParameter(0, username).setParameter(1, letterId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (Letter) list.get(0);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void sendLetter(User user,String title,String contents) {
		Letter letter = new Letter();
		letter.setUser(user);
		letter.setTitle(title);
		letter.setContents(contents);
		letter.setStatus(1);
		letter.setCreateTime(new Date());
		this.save(letter);
	}
	
}
