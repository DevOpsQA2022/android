package com.mobilesalesperson.model;

import java.io.Serializable;

import android.graphics.Bitmap;

public class HhSignature implements Serializable {

	private int hhSignature_signid;
	private String hhSignature_comapanyname;
	private String hhSignature_referencenumber;
	private Bitmap hhSignature_signature;
	private int hhSignature_status;
	private String hhSignature_date;
	private String hhSignature_companycode;

	public int getHhSignature_signid() {
		return hhSignature_signid;
	}

	public void setHhSignature_signid(int hhSignature_signid) {
		this.hhSignature_signid = hhSignature_signid;
	}

	public String getHhSignature_comapanyname() {
		return hhSignature_comapanyname;
	}

	public void setHhSignature_comapanyname(String hhSignature_comapanyname) {
		this.hhSignature_comapanyname = hhSignature_comapanyname;
	}

	public String getHhSignature_referencenumber() {
		return hhSignature_referencenumber;
	}

	public void setHhSignature_referencenumber(
			String hhSignature_referencenumber) {
		this.hhSignature_referencenumber = hhSignature_referencenumber;
	}

	public int getHhSignature_status() {
		return hhSignature_status;
	}

	public void setHhSignature_status(int hhSignature_status) {
		this.hhSignature_status = hhSignature_status;
	}

	public String getHhSignature_date() {
		return hhSignature_date;
	}

	public void setHhSignature_date(String hhSignature_date) {
		this.hhSignature_date = hhSignature_date;
	}

	public Bitmap getHhSignature_signature() {
		return hhSignature_signature;
	}

	public void setHhSignature_signature(Bitmap hhSignature_signature) {
		this.hhSignature_signature = hhSignature_signature;
	}

	public String getHhSignature_companycode() {
		return hhSignature_companycode;
	}

	public void setHhSignature_companycode(String hhSignature_companycode) {
		this.hhSignature_companycode = hhSignature_companycode;
	}

}
