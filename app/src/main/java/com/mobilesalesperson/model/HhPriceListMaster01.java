package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhPriceListMaster01 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String priceList;
	private String priceList_companycode;

	public String getPriceList() {
		return priceList;
	}

	public void setPriceList(String priceList) {
		this.priceList = priceList;
	}

	public String getPriceList_companycode() {
		return priceList_companycode;
	}

	public void setPriceList_companycode(String priceList_companycode) {
		this.priceList_companycode = priceList_companycode;
	}

}
