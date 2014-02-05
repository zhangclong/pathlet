package com.google.code.pathlet.jdbc.instance.transaction.impl;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import com.google.code.newpath.jdbc.example.vo.UserQueryVO;
import com.google.code.newpath.jdbc.example.vo.UserVO;
import com.google.code.pathlet.jdbc.EntityInsertDef;
import com.google.code.pathlet.jdbc.EntityRowMapper;
import com.google.code.pathlet.jdbc.EntityUpdateDef;
import com.google.code.pathlet.jdbc.ExtJdbcTemplate;
import com.google.code.pathlet.jdbc.instance.transaction.ConfUserService;
import com.google.code.pathlet.util.ValueUtils;
import com.google.code.pathlet.vo.QueryResultVo;

public class ConfUserServiceImpl implements ConfUserService {

	private ExtJdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) throws IOException {

		this.jdbcTemplate = new ExtJdbcTemplate(dataSource);
		
		this.jdbcTemplate.registerInsertEntity(
				new EntityInsertDef("T_USER", UserVO.class, "T_USER"));
		
		this.jdbcTemplate.registerUpdateEntity(
				new EntityUpdateDef("T_USER", UserVO.class, "T_USER", new String[] {"username"}));

	}

	public void deleteUsers(String[] usernames) {
		for(String username : usernames) {
			jdbcTemplate.update("DELETE from T_USER where USERNAME= ?", new Object[] { username });
		}
	}

	public UserVO getUser(String username, String password) {
		return (UserVO)this.jdbcTemplate.queryForObject(
				"select * from T_USER where USERNAME=? and PASSWORD=?", 
				new Object[]{username, password}, new EntityRowMapper(UserVO.class, null));
	}


	public UserVO getUser(String username) {
		UserVO user = (UserVO)this.jdbcTemplate.queryForObject(
				"select t.* from T_USER t where USERNAME=?",
				new Object[]{username}, new EntityRowMapper(UserVO.class, null));
		return user;
	}


	public QueryResultVo<UserVO> queryUserList(UserQueryVO parameters) {
		
		StringBuffer sqlBuff = new StringBuffer(" from T_USER u where 1=1 ");

		if(ValueUtils.notEmpty(parameters.getUsername())) {
			sqlBuff.append(" and u.username like '%" + ValueUtils.escapeSQLParam(parameters.getUsername()) + "%'");
		}
		
		if(ValueUtils.notEmpty(parameters.getEmail())) {
			sqlBuff.append(" and u.email = '" + ValueUtils.escapeSQLParam(parameters.getEmail()) + "'");
		}
		
		StringBuilder countSql = new StringBuilder();
		countSql.append("select count(u.username)").append(sqlBuff);
		
		StringBuilder listSql = new StringBuilder();
		listSql.append("select u.username, u.password, u.name, u.email").append(sqlBuff);
		
		//order by clause
		if("username".equals(parameters.getSortName())) {
			listSql.append(" order by u.username ").append(parameters.getSortOrder());
		}
		
		//Get total records count
		Long rowCount = jdbcTemplate.queryForLong(countSql.toString());
	
		//Get rows data
//		List<Map<String, Object>> listResult = jdbcTemplate
//				.query(parameters.getStartIndex(), parameters.getFetchSize(), listSql.toString(), null);
//		
//		
//		//Query the row data in current page		
//		List<User> dataList = new ArrayList<User>(listResult.size());
//		for(Map<String, Object> rowdata : listResult) {
//			User user = new User();
//			
//			user.setUsername((String)rowdata.get("username"));
//			user.setPassword((String)rowdata.get("password"));
//			user.setName((String)rowdata.get("name"));
//			user.setEmail((String)rowdata.get("email"));
//			
//			dataList.add(user);
//		}
		
		List<UserVO> listResult = jdbcTemplate
				.query(parameters.getStartIndex(), parameters.getFetchSize(), listSql.toString(), null, new EntityRowMapper(UserVO.class, null));
		
		return new QueryResultVo<UserVO>(rowCount, listResult);
	}

	public void saveUser(UserVO user) {
		jdbcTemplate.insertEntity("T_USER", user);
	}
	
	public void updateUser(UserVO user) {
		jdbcTemplate.updateEntity("T_USER", user);
	}

}
