package com.mobilesalesperson.model;

import java.io.Serializable;

public class Manf_Number01 implements Serializable {

	private String manuf_manitemno;
	private String manuf_itemno;
	private String manuf_uom;
	private String company_code;

	public String getManuf_manitemno() {
		return manuf_manitemno;
	}

	public void setManuf_manitemno(String manuf_manitemno) {
		this.manuf_manitemno = manuf_manitemno;
	}

	public String getManuf_itemno() {
		return manuf_itemno;
	}

	public void setManuf_itemno(String manuf_itemno) {
		this.manuf_itemno = manuf_itemno;
	}

	public String getManuf_uom() {
		return manuf_uom;
	}

	public void setManuf_uom(String manuf_uom) {
		this.manuf_uom = manuf_uom;
	}

	public String getCompany_code() {
		return company_code;
	}

	public void setCompany_code(String company_code) {
		this.company_code = company_code;
	}

}
