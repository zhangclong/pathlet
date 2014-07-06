package com.google.code.newpath.jdbc.example.vo;

import java.io.Serializable;

public class CardVO implements Serializable {

	private static final long serialVersionUID = 2374948373267360431L;
	
	private String cardNumber;
	private String userName;

	public CardVO() {
	}
	
	public CardVO(String cardNumber, String userName) {
		this.cardNumber = cardNumber;
		this.userName = userName;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
