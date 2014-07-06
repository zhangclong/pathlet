package com.google.code.pathlet.jdbc.instance.transaction.impl;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.code.newpath.jdbc.example.vo.CardVO;
import com.google.code.pathlet.config.anno.InstanceIn;
import com.google.code.pathlet.jdbc.EntityRowMapper;
import com.google.code.pathlet.jdbc.instance.transaction.AnnoCardService;

@Transactional
public class AnnoCardServiceImpl implements AnnoCardService {

	private JdbcTemplate jdbcTemplate;

	@InstanceIn(path="/dataSource")
	public void setDataSource(DataSource dataSource) throws IOException {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void saveCard(CardVO card) {
		throw new RuntimeException();
		//jdbcTemplate.update("insert into T_CARD(card_number, user_name) values(?,?)", new Object[] {card.getCardNumber(), card.getUserName()});
	}
	
	@Transactional(propagation=Propagation.NOT_SUPPORTED, readOnly=true)
	public CardVO getCard(String number) {
		CardVO card = (CardVO) this.jdbcTemplate.queryForObject(
				"select t.* from T_CARD t where CARD_NUMBER=?",
				new Object[]{number}, new EntityRowMapper(CardVO.class, null));
		
		return card;
	}
	
}
