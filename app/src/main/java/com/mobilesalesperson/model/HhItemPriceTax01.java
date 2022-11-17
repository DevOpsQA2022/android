package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhItemPriceTax01 implements Serializable {

	private String hhItemPriceTax_number;
	private String hhItemPriceTax_pricelist;
	private String hhItemPriceTax_authority;
	private int hhItemPriceTax_salestaxclass;
	private String hhItemPriceTax_companycode;

	public String getHhItemPriceTax_number() {
		return hhItemPriceTax_number;
	}

	public void setHhItemPriceTax_number(String hhItemPriceTax_number) {
		this.hhItemPriceTax_number = hhItemPriceTax_number;
	}

	public String getHhItemPriceTax_pricelist() {
		return hhItemPriceTax_pricelist;
	}

	public void setHhItemPriceTax_pricelist(String hhItemPriceTax_pricelist) {
		this.hhItemPriceTax_pricelist = hhItemPriceTax_pricelist;
	}

	public String getHhItemPriceTax_authority() {
		return hhItemPriceTax_authority;
	}

	public void setHhItemPriceTax_authority(String hhItemPriceTax_authority) {
		this.hhItemPriceTax_authority = hhItemPriceTax_authority;
	}

	public int getHhItemPriceTax_salestaxclass() {
		return hhItemPriceTax_salestaxclass;
	}

	public void setHhItemPriceTax_salestaxclass(int hhItemPriceTax_salestaxclass) {
		this.hhItemPriceTax_salestaxclass = hhItemPriceTax_salestaxclass;
	}

	public String getHhItemPriceTax_companycode() {
		return hhItemPriceTax_companycode;
	}

	public void setHhItemPriceTax_companycode(String hhItemPriceTax_companycode) {
		this.hhItemPriceTax_companycode = hhItemPriceTax_companycode;
	}

}
