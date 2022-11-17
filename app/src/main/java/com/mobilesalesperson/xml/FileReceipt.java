package com.mobilesalesperson.xml;

import com.mobilesalesperson.model.HhReceipt01;

public class FileReceipt {
	public StringBuilder writeXml(HhReceipt01 r, StringBuilder builder) {

		try {
			builder.append("<RECEIPT>");
			
			builder.append("<SNO>");
			builder.append("" + r.getHhReceipt_sno());
			builder.append("</SNO>");			
			
			builder.append("<CUSTOMERNUMBER>");
			builder.append("" + r.getHhReceipt_customernumber());
			builder.append("</CUSTOMERNUMBER>");
			
			builder.append("<CUSTOMERNAME>");
			builder.append("" + r.getHhReceipt_customername());
			builder.append("</CUSTOMERNAME>");
			
			builder.append("<DOCNUMBER>");
			builder.append("" + r.getHhReceipt_docnumber());
			builder.append("</DOCNUMBER>");
			
			builder.append("<PENDINGBAL>");
			builder.append("" + r.getHhReceipt_pendingbal());
			builder.append("</PENDINGBAL>");
			
			builder.append("<CURRENCY>");
			builder.append("" + r.getHhReceipt_currency());
			builder.append("</CURRENCY>");
			
			builder.append("<APPLY1>");
			builder.append("" + r.getHhReceipt_apply1());
			builder.append("</APPLY1>");
			
			builder.append("<APPLIEDAMOUNT>");
			builder.append("" + r.getHhReceipt_appliedamount());
			builder.append("</APPLIEDAMOUNT>");
			
			builder.append("<RECEIPTTYPE>");
			builder.append("" + r.getHhReceipt_receipttype());
			builder.append("</RECEIPTTYPE>");
						
			builder.append("<AMOUNT>");
			builder.append("" + r.getHhReceipt_amount());
			builder.append("</AMOUNT>");
			
			builder.append("<RECEIPTNUMBER>");
			builder.append("" + r.getHhReceipt_receiptnumber());
			builder.append("</RECEIPTNUMBER>");
			
			builder.append("<RECEIPTDAY>");
			builder.append("" + r.getHhReceipt_receiptday());
			builder.append("</RECEIPTDAY>");
			
			builder.append("<RECEIPTMONTH>");
			builder.append("" + r.getHhReceipt_receiptmonth());
			builder.append("</RECEIPTMONTH>");
			
			builder.append("<RECEIPTYEAR>");
			builder.append("" + r.getHhReceipt_receiptyear());
			builder.append("</RECEIPTYEAR>");
			
			builder.append("<STATUS>");
			builder.append("" + r.getHhReceipt_status());
			builder.append("</STATUS>");
			
			builder.append("<ReceiptUnapplied>");
			builder.append("" + r.getHhReceipt_receiptunapplied());
			builder.append("</ReceiptUnapplied>");
			
			builder.append("<customerAmount>");
			builder.append("" + r.getHhReceipt_customeramount());
			builder.append("</customerAmount>");
			
			builder.append("<REFNO>");
			builder.append("" + r.getHhReceipt_refno());
			builder.append("</REFNO>");
			
			builder.append("</RECEIPT>");
			return builder;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
