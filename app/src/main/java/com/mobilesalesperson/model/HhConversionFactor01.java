package com.mobilesalesperson.model;

import java.io.Serializable;

public class HhConversionFactor01 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String hhConversionFactor_number;
	private double hhConversionFactor_uom_conv_factor;
	private String hhConversionFactor_name;
	private String hhConversionFactor_companycode;

	public String getHhConversionFactor_number() {
		return hhConversionFactor_number;
	}

	public void setHhConversionFactor_number(String hhConversionFactor_number) {
		this.hhConversionFactor_number = hhConversionFactor_number;
	}

	public double getHhConversionFactor_uom_conv_factor() {
		return hhConversionFactor_uom_conv_factor;
	}

	public void setHhConversionFactor_uom_conv_factor(
			double hhConversionFactor_uom_conv_factor) {
		this.hhConversionFactor_uom_conv_factor = hhConversionFactor_uom_conv_factor;
	}

	public String getHhConversionFactor_name() {
		return hhConversionFactor_name;
	}

	public void setHhConversionFactor_name(String hhConversionFactor_name) {
		this.hhConversionFactor_name = hhConversionFactor_name;
	}

	public String getHhConversionFactor_companycode() {
		return hhConversionFactor_companycode;
	}

	public void setHhConversionFactor_companycode(
			String hhConversionFactor_companycode) {
		this.hhConversionFactor_companycode = hhConversionFactor_companycode;
	}

}
