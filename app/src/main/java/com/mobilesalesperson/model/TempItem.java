package com.mobilesalesperson.model;

import java.io.Serializable;

public class TempItem implements Serializable{

	private String temp_itemNum;
	private String temp_qty;
	private String temp_extPrice;
	private String temp_location;
	private String temp_uom;
	private String temp_date;
	private String temp_entryType;
	private String temp_discount;
	private String temp_discType;
	
	public String getTemp_itemNum() {
		return temp_itemNum;
	}
	public void setTemp_itemNum(String temp_itemNum) {
		this.temp_itemNum = temp_itemNum;
	}
	public String getTemp_qty() {
		return temp_qty;
	}
	public void setTemp_qty(String temp_qty) {
		this.temp_qty = temp_qty;
	}
	public String getTemp_extPrice() {
		return temp_extPrice;
	}
	public void setTemp_extPrice(String temp_extPrice) {
		this.temp_extPrice = temp_extPrice;
	}
	public String getTemp_location() {
		return temp_location;
	}
	public void setTemp_location(String temp_location) {
		this.temp_location = temp_location;
	}
	public String getTemp_uom() {
		return temp_uom;
	}
	public void setTemp_uom(String temp_uom) {
		this.temp_uom = temp_uom;
	}
	public String getTemp_date() {
		return temp_date;
	}
	public void setTemp_date(String temp_date) {
		this.temp_date = temp_date;
	}
	public String getTemp_entryType() {
		return temp_entryType;
	}
	public void setTemp_entryType(String temp_entryType) {
		this.temp_entryType = temp_entryType;
	}
	public String getTemp_discount() {
		return temp_discount;
	}
	public void setTemp_discount(String temp_discount) {
		this.temp_discount = temp_discount;
	}
	public String getTemp_discType() {
		return temp_discType;
	}
	public void setTemp_discType(String temp_discType) {
		this.temp_discType = temp_discType;
	}
	
}
