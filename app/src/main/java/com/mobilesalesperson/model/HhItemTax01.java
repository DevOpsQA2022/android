package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhItemTax01 implements Serializable {

	private String hhItemTax_number;
	private String hhItemTax_authority;
	private int hhItemTax_salestaxclass;
	private String hhItemTax_companycode;

	public String getHhItemTax_number() {
		return hhItemTax_number;
	}

	public void setHhItemTax_number(String hhItemTax_number) {
		this.hhItemTax_number = hhItemTax_number;
	}

	public String getHhItemTax_authority() {
		return hhItemTax_authority;
	}

	public void setHhItemTax_authority(String hhItemTax_authority) {
		this.hhItemTax_authority = hhItemTax_authority;
	}

	public int getHhItemTax_salestaxclass() {
		return hhItemTax_salestaxclass;
	}

	public void setHhItemTax_salestaxclass(int hhItemTax_salestaxclass) {
		this.hhItemTax_salestaxclass = hhItemTax_salestaxclass;
	}

	public String getHhItemTax_companycode() {
		return hhItemTax_companycode;
	}

	public void setHhItemTax_companycode(String hhItemTax_companycode) {
		this.hhItemTax_companycode = hhItemTax_companycode;
	}

}
