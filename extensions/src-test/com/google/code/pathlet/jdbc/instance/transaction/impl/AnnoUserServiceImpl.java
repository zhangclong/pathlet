package com.google.code.pathlet.jdbc.instance.transaction.impl;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.google.code.newpath.jdbc.example.vo.UserQueryVO;
import com.google.code.newpath.jdbc.example.vo.UserVO;
import com.google.code.pathlet.config.anno.InstanceIn;
import com.google.code.pathlet.jdbc.EntityInsertDef;
import com.google.code.pathlet.jdbc.EntityRowMapper;
import com.google.code.pathlet.jdbc.EntityUpdateDef;
import com.google.code.pathlet.jdbc.ExtJdbcTemplate;
import com.google.code.pathlet.jdbc.instance.transaction.AnnoUserService;
import com.google.code.pathlet.util.ValueUtils;
import com.google.code.pathlet.vo.QueryResultVo;

@Transactional(readOnly=false)
public class AnnoUserServiceImpl implements AnnoUserService {

	private JdbcTemplate jdbcTemplate;
	
	private JdbcTemplate jdbcTemplate2;

	@InstanceIn(path="/dataSource")
	public void setDataSource(DataSource dataSource) throws IOException {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@InstanceIn(path="/dataSource2")
	public void setDataSource2(DataSource dataSource2) throws IOException {
		this.jdbcTemplate2 = new JdbcTemplate(dataSource2);
	}

	public void deleteUsers(String[] usernames) {
		for(String username : usernames) {
			jdbcTemplate.update("DELETE from T_USER where USERNAME= ?", new Object[] { username });
		}
	}
	
	@Transactional(readOnly=true)
	public UserVO getUser(String username) {
		UserVO user = (UserVO)this.jdbcTemplate.queryForObject(
				"select t.* from T_USER t where USERNAME=?",
				new Object[]{username}, new EntityRowMapper(UserVO.class, null));
		return user;
	}

	public void saveUser(UserVO user) {
		//jdbcTemplate.insertEntity("T_USER", user);
	}
	
	public void updateUser(UserVO user) {
		//jdbcTemplate.updateEntity("T_USER", user);
	}

}
