package com.google.code.pathlet.jdbc.instance.transaction.impl;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.code.newpath.jdbc.example.vo.CardVO;
import com.google.code.newpath.jdbc.example.vo.UserVO;
import com.google.code.pathlet.config.anno.InstanceIn;
import com.google.code.pathlet.jdbc.EntityRowMapper;
import com.google.code.pathlet.jdbc.instance.transaction.AnnoCardService;
import com.google.code.pathlet.jdbc.instance.transaction.AnnoUserService;

@Transactional
public class AnnoUserServiceImpl implements AnnoUserService {

	private JdbcTemplate jdbcTemplate;
	
	@InstanceIn(path="/transaction/AnnoCardService")
	private AnnoCardService cardService;
	
	@InstanceIn(path="/dataSource")
	public void setDataSource(DataSource dataSource) throws IOException {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/*@InstanceIn(path="/dataSource2")
	public void setDataSource2(DataSource dataSource2) throws IOException {
		this.jdbcTemplate2 = new JdbcTemplate(dataSource2);
	}*/
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void deleteUsers(String[] usernames) {
		throw new RuntimeException();
		
		/*for(String username : usernames) {
			jdbcTemplate.update("DELETE from T_USER where USERNAME= ?", new Object[] { username });
		}*/
	}
	
	@Transactional(propagation=Propagation.NOT_SUPPORTED, readOnly=true)
	public UserVO getUser(String username) {
		UserVO user = (UserVO) this.jdbcTemplate.queryForObject(
				"select t.* from T_USER t where USERNAME=?",
				new Object[]{username}, new EntityRowMapper(UserVO.class, null));
		return user;
	}
	
	public void saveUser(UserVO user) {
		
		this.jdbcTemplate.execute("insert into T_USER(username,password) values('hello world','123456')");
		
		try {
			this.cardService.saveCard(new CardVO("1234567890","hello world"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*jdbcTemplate.execute("insert into T_USER(username,password) values('hello world','123456')");
		
		try {
			cardService.getCard("123456789");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
	
	public void updateUser(UserVO user) {
		//jdbcTemplate.updateEntity("T_USER", user);
	}

}
