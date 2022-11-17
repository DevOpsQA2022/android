package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhCustomerGroup01 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String hhCustomerGroup_group_code;
	private String hhCustomerGroup_description;
	private String hhCustomerGroup_terms;
	private String hhCustomerGroup_companycode;

	public String getHhCustomerGroup_group_code() {
		return hhCustomerGroup_group_code;
	}

	public void setHhCustomerGroup_group_code(String hhCustomerGroup_group_code) {
		this.hhCustomerGroup_group_code = hhCustomerGroup_group_code;
	}

	public String getHhCustomerGroup_description() {
		return hhCustomerGroup_description;
	}

	public void setHhCustomerGroup_description(
			String hhCustomerGroup_description) {
		this.hhCustomerGroup_description = hhCustomerGroup_description;
	}

	public String getHhCustomerGroup_terms() {
		return hhCustomerGroup_terms;
	}

	public void setHhCustomerGroup_terms(String hhCustomerGroup_terms) {
		this.hhCustomerGroup_terms = hhCustomerGroup_terms;
	}

	public String getHhCustomerGroup_companycode() {
		return hhCustomerGroup_companycode;
	}

	public void setHhCustomerGroup_companycode(
			String hhCustomerGroup_companycode) {
		this.hhCustomerGroup_companycode = hhCustomerGroup_companycode;
	}

}
