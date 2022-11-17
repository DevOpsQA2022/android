package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhRoute01 implements Serializable {

	private String hhRoute_customernumber;
	private String hhRoute_customername;
	private int hhRoute_dayofvisit;
	private int hhRoute_monthofvisit;
	private int hhRoute_yearofvisit;
	private int hhRoute_visitstat;
	private String hhRoute_companycode;

	public String getHhRoute_customernumber() {
		return hhRoute_customernumber;
	}

	public void setHhRoute_customernumber(String hhRoute_customernumber) {
		this.hhRoute_customernumber = hhRoute_customernumber;
	}

	public String getHhRoute_customername() {
		return hhRoute_customername;
	}

	public void setHhRoute_customername(String hhRoute_customername) {
		this.hhRoute_customername = hhRoute_customername;
	}

	public int getHhRoute_dayofvisit() {
		return hhRoute_dayofvisit;
	}

	public void setHhRoute_dayofvisit(int hhRoute_dayofvisit) {
		this.hhRoute_dayofvisit = hhRoute_dayofvisit;
	}

	public int getHhRoute_monthofvisit() {
		return hhRoute_monthofvisit;
	}

	public void setHhRoute_monthofvisit(int hhRoute_monthofvisit) {
		this.hhRoute_monthofvisit = hhRoute_monthofvisit;
	}

	public int getHhRoute_yearofvisit() {
		return hhRoute_yearofvisit;
	}

	public void setHhRoute_yearofvisit(int hhRoute_yearofvisit) {
		this.hhRoute_yearofvisit = hhRoute_yearofvisit;
	}

	public int getHhRoute_visitstat() {
		return hhRoute_visitstat;
	}

	public void setHhRoute_visitstat(int hhRoute_visitstat) {
		this.hhRoute_visitstat = hhRoute_visitstat;
	}

	public String getHhRoute_companycode() {
		return hhRoute_companycode;
	}

	public void setHhRoute_companycode(String hhRoute_companycode) {
		this.hhRoute_companycode = hhRoute_companycode;
	}
}
