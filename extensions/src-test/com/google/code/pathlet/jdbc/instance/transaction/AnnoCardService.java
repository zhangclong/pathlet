package com.google.code.pathlet.jdbc.instance.transaction;

import com.google.code.newpath.jdbc.example.vo.CardVO;

public interface AnnoCardService {
	
	public void saveCard(CardVO card);
	
	public CardVO getCard(String number);
	
}