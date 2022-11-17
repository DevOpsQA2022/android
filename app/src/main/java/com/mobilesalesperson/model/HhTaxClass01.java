package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhTaxClass01 implements Serializable {

	private String hhTaxClass_taxauthority;
	private String hhTaxClass_taxstts;
	private String hhTaxClass_description;
	private String hhTaxClass_companycode;

	public String getHhTaxClass_taxauthority() {
		return hhTaxClass_taxauthority;
	}

	public void setHhTaxClass_taxauthority(String hhTaxClass_taxauthority) {
		this.hhTaxClass_taxauthority = hhTaxClass_taxauthority;
	}

	public String getHhTaxClass_taxstts() {
		return hhTaxClass_taxstts;
	}

	public void setHhTaxClass_taxstts(String hhTaxClass_taxstts) {
		this.hhTaxClass_taxstts = hhTaxClass_taxstts;
	}

	public String getHhTaxClass_description() {
		return hhTaxClass_description;
	}

	public void setHhTaxClass_description(String hhTaxClass_description) {
		this.hhTaxClass_description = hhTaxClass_description;
	}

	public String getHhTaxClass_companycode() {
		return hhTaxClass_companycode;
	}

	public void setHhTaxClass_companycode(String hhTaxClass_companycode) {
		this.hhTaxClass_companycode = hhTaxClass_companycode;
	}
}
