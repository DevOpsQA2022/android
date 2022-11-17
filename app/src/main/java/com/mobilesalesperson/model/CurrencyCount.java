package com.mobilesalesperson.model;

public class CurrencyCount {
	private String currency;
	private String quantity;
	private String result;
	
	
	public CurrencyCount(String currency, String quantity, String result) {
		super();
		this.currency = currency;
		this.quantity = quantity;
		this.result = result;
	}
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

}
