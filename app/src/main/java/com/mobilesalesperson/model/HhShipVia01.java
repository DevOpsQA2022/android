package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhShipVia01 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String hhShipVia_code;
	private String hhShipVia_name;
	private String hhShipVia_companycode;

	public String getHhShipVia_code() {
		return hhShipVia_code;
	}

	public void setHhShipVia_code(String hhShipVia_code) {
		this.hhShipVia_code = hhShipVia_code;
	}

	public String getHhShipVia_name() {
		return hhShipVia_name;
	}

	public void setHhShipVia_name(String hhShipVia_name) {
		this.hhShipVia_name = hhShipVia_name;
	}

	public String getHhShipVia_companycode() {
		return hhShipVia_companycode;
	}

	public void setHhShipVia_companycode(String hhShipVia_companycode) {
		this.hhShipVia_companycode = hhShipVia_companycode;
	}

}
