package com.mobilesalesperson.xml;

import com.mobilesalesperson.model.Mspdb;

public class FileMspDb {
	
	public StringBuilder writeXml(Mspdb m, StringBuilder builder) {

		try {
			builder.append("<MSPDB>");

			builder.append("<COMPANYNO>");
			builder.append("" + m.getMspdb_companyNumber());
			builder.append("</COMPANYNO>");

			builder.append("<SALESPERSON>");
			builder.append("" + m.getMspdb_salesPerson());
			builder.append("</SALESPERSON>");

			builder.append("<MAPNO>");
			builder.append("" + m.getMspdb_mapNo());
			builder.append("</MAPNO>");

			builder.append("<CUSTOMERNO>");
			builder.append("" + m.getMspdb_customerNumber());
			builder.append("</CUSTOMERNO>");

			builder.append("<ORDERNO>");
			builder.append("" + m.getMspdb_orderNumber());
			builder.append("</ORDERNO>");

			builder.append("<QUOTENO>");
			builder.append("" + m.getMspdb_quoteNumber());
			builder.append("</QUOTENO>");

			builder.append("<LASTINVOICENO>");
			builder.append("" + m.getMspdb_lastinvno());
			builder.append("</LASTINVOICENO>");

			builder.append("<RECEIPTNO>");
			builder.append("" + m.getMspdb_receiptnumber());
			builder.append("</RECEIPTNO>");

			builder.append("</MSPDB>");
			return builder;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
