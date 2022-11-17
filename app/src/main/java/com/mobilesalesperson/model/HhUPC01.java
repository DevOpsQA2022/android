package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhUPC01 implements Serializable {

	private String hhUPC_itemnumber;
	private String hhUPC_upcnumber;
	private String hhUPC_itemdesc;
	private String hhUPC_companycode;

	public String getHhUPC_itemnumber() {
		return hhUPC_itemnumber;
	}

	public void setHhUPC_itemnumber(String hhUPC_itemnumber) {
		this.hhUPC_itemnumber = hhUPC_itemnumber;
	}

	public String getHhUPC_upcnumber() {
		return hhUPC_upcnumber;
	}

	public void setHhUPC_upcnumber(String hhUPC_upcnumber) {
		this.hhUPC_upcnumber = hhUPC_upcnumber;
	}

	public String getHhUPC_itemdesc() {
		return hhUPC_itemdesc;
	}

	public void setHhUPC_itemdesc(String hhUPC_itemdesc) {
		this.hhUPC_itemdesc = hhUPC_itemdesc;
	}

	public String getHhUPC_companycode() {
		return hhUPC_companycode;
	}

	public void setHhUPC_companycode(String hhUPC_companycode) {
		this.hhUPC_companycode = hhUPC_companycode;
	}

}
