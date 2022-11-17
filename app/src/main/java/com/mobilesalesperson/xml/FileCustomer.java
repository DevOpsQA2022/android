package com.mobilesalesperson.xml;

import com.mobilesalesperson.model.HhCustomer01;

public class FileCustomer {
	public StringBuilder writeXml(HhCustomer01 c, StringBuilder builder) {

		try {
			
			builder.append("<NEWCUSTOMER>");

			builder.append("<NUMBER>");
			builder.append("" + c.getHhCustomer_number());
			builder.append("</NUMBER>");

			builder.append("<NAME>");
			builder.append("" + c.getHhCustomer_name());
			builder.append("</NAME>");

			builder.append("<ADDRESS>");
			builder.append("" + c.getHhCustomer_address());
			builder.append("</ADDRESS>");

			builder.append("<CITY>");
			builder.append("" + c.getHhCustomer_city());
			builder.append("</CITY>");

			builder.append("<PHONE1>");
			builder.append("" + c.getHhCustomer_phone1());
			builder.append("</PHONE1>");

			builder.append("<GROUPCODE>");
			builder.append("" + c.getHhCustomer_groupcode());
			builder.append("</GROUPCODE>");

			builder.append("<TYPE>");
			builder.append("" + c.getHhCustomer_type());
			builder.append("</TYPE>");

			builder.append("<TERMS>");
			builder.append("" + c.getHhCustomer_terms());
			builder.append("</TERMS>");

			builder.append("<TAXGROUP>");
			builder.append("" + c.getHhCustomer_taxgroup());
			builder.append("</TAXGROUP>");

			builder.append("<COMMENT>");
			builder.append("" + c.getHhCustomer_comment());
			builder.append("</COMMENT>");

			builder.append("<TAXAUTHORITY1>");
			builder.append("" + c.getHhCustomer_taxauthority1());
			builder.append("</TAXAUTHORITY1>");

			builder.append("<TAXABLE1>");
			builder.append("" + c.getHhCustomer_taxable1());
			builder.append("</TAXABLE1>");

			builder.append("<TAXSTTS1>");
			builder.append("" + c.getHhCustomer_taxstts1());
			builder.append("</TAXSTTS1>");

			builder.append("<TAXAUTHORITY2>");
			builder.append("" + c.getHhCustomer_taxauthority2());
			builder.append("</TAXAUTHORITY2>");

			builder.append("<TAXABLE2>");
			builder.append("" + c.getHhCustomer_taxable2());
			builder.append("</TAXABLE2>");

			builder.append("<TAXSTTS2>");
			builder.append("" + c.getHhCustomer_taxstts2());
			builder.append("</TAXSTTS2>");

			builder.append("<TAXAUTHORITY3>");
			builder.append("" + c.getHhCustomer_taxauthority3());
			builder.append("</TAXAUTHORITY3>");

			builder.append("<TAXABLE3>");
			builder.append("" + c.getHhCustomer_taxable3());
			builder.append("</TAXABLE3>");

			builder.append("<TAXSTTS3>");
			builder.append("" + c.getHhCustomer_taxstts3());
			builder.append("</TAXSTTS3>");

			builder.append("<TAXAUTHORITY4>");
			builder.append("" + c.getHhCustomer_taxauthority4());
			builder.append("</TAXAUTHORITY4>");

			builder.append("<TAXABLE4>");
			builder.append("" + c.getHhCustomer_taxable4());
			builder.append("</TAXABLE4>");

			builder.append("<TAXSTTS4>");
			builder.append("" + c.getHhCustomer_taxstts4());
			builder.append("</TAXSTTS4>");

			builder.append("<TAXAUTHORITY5>");
			builder.append("" + c.getHhCustomer_taxauthority5());
			builder.append("</TAXAUTHORITY5>");

			builder.append("<TAXABLE5>");
			builder.append("" + c.getHhCustomer_taxable5());
			builder.append("</TAXABLE5>");

			builder.append("<TAXSTTS5>");
			builder.append("" + c.getHhCustomer_taxstts5());
			builder.append("</TAXSTTS5>");

			builder.append("<PRICELISTCODE>");
			builder.append("" + c.getHhCustomer_pricelistcode());
			builder.append("</PRICELISTCODE>");

			builder.append("</NEWCUSTOMER>");
			return builder;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
