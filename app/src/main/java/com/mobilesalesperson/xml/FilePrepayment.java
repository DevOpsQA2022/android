package com.mobilesalesperson.xml;


import com.mobilesalesperson.model.HhPrepayment01;

public class FilePrepayment {
	public StringBuilder writeXml(HhPrepayment01 p, StringBuilder builder) {
		try {
			builder.append("<PREPAYMENT>");
			
			builder.append("<TRANSTYPE>");
			builder.append("" + p.getHhPrePayment_transType() );
			builder.append("</TRANSTYPE>");				
			
			builder.append("<REFERENCENUMBER>");
			builder.append("" + p.getHhPrePayment_referenceNumber() );
			builder.append("</REFERENCENUMBER>");
			
			builder.append("<INVOICENUMBER>");
			builder.append("" + p.getHhPrePayment_invoiceNumber() );
			builder.append("</INVOICENUMBER>");
			
			builder.append("<CUSTOMERNUMBER>");
			builder.append("" + p.getHhPrePayment_customerNumber() );
			builder.append("</CUSTOMERNUMBER>");
						
			builder.append("<CURRENCY>");
			builder.append("" + p.getHhPrePayment_currency() );
			builder.append("</CURRENCY>");
						
			builder.append("<ORDERTOTAL>");
			builder.append("" + p.getHhPrePayment_orderTotal() );
			builder.append("</ORDERTOTAL>");
			
			builder.append("<AMTDUE>");
			builder.append("" + p.getHhPrePayment_amtDue() );
			builder.append("</AMTDUE>");
			
			builder.append("<RECEIPTTYPE>");
			builder.append("" + p.getHhPrePayment_receiptType() );
			builder.append("</RECEIPTTYPE>");
			
			builder.append("<CHECKRECEIPTNO>");
			builder.append("" + p.getHhPrePayment_checkReceiptNo());
			builder.append("</CHECKRECEIPTNO>");
			
			builder.append("<RECEIPTDAY>");
			builder.append("" + p.getHhPrePayment_receiptDay());
			builder.append("</RECEIPTDAY>");
						
			builder.append("<RECEIPTMONTH>");
			builder.append("" + p.getHhPrePayment_receiptMonth());
			builder.append("</RECEIPTMONTH>");
			
			builder.append("<RECEIPTYEAR>");
			builder.append("" + p.getHhPrePayment_receiptYear());
			builder.append("</RECEIPTYEAR>");

			builder.append("<RECEIPTAMOUNT>");
			builder.append("" + p.getHhPrePayment_receiptAmount());
			builder.append("</RECEIPTAMOUNT>");
			
			builder.append("<ACCPACORDNUMBER>");
			builder.append("" + p.getHhPrePayment_accpacOrdNumber());
			builder.append("</ACCPACORDNUMBER>");
			
			builder.append("<Flag>");
			builder.append("" + p.getHhPrePayment_flag());
			builder.append("</Flag>");
			
			builder.append("<REFNO>");
			builder.append("" + p.getHhPrePayment_refNo());
			builder.append("</REFNO>");
			
			builder.append("</PREPAYMENT>");
			return builder;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
