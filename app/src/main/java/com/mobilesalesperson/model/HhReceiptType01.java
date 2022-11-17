package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhReceiptType01 implements Serializable {

	private String hhReceiptType_receipttype;
	private String hhReceiptType_receiptname;
	private String hhReceiptType_companycode;

	public String getHhReceiptType_receipttype() {
		return hhReceiptType_receipttype;
	}

	public void setHhReceiptType_receipttype(String hhReceiptType_receipttype) {
		this.hhReceiptType_receipttype = hhReceiptType_receipttype;
	}

	public String getHhReceiptType_receiptname() {
		return hhReceiptType_receiptname;
	}

	public void setHhReceiptType_receiptname(String hhReceiptType_receiptname) {
		this.hhReceiptType_receiptname = hhReceiptType_receiptname;
	}

	public String getHhReceiptType_companycode() {
		return hhReceiptType_companycode;
	}

	public void setHhReceiptType_companycode(String hhReceiptType_companycode) {
		this.hhReceiptType_companycode = hhReceiptType_companycode;
	}

}
