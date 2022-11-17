package com.mobilesalesperson.xml;

import com.mobilesalesperson.model.HhTran01;

public class FileTran {
	public StringBuilder writeXml(HhTran01 t, StringBuilder builder, boolean isReturn) {
		try {

			builder.append("<TRANSTYPE>");
			builder.append(t.getHhTran_transType());
			builder.append("</TRANSTYPE>");

			builder.append("<REFERENCENUMBER>");
			builder.append(t.getHhTran_referenceNumber());
			builder.append("</REFERENCENUMBER>");

			builder.append("<INVOICENUMBER>");
			builder.append(t.getHhTran_invoiceNumber());
			builder.append("</INVOICENUMBER>");
			
			builder.append("<ORDERNUMBER>");
			builder.append(t.getHhTran_orderNumber());
			builder.append("</ORDERNUMBER>");
			
			builder.append("<TRANSDAY>");
			builder.append(t.getHhTran_transDay());
			builder.append("</TRANSDAY>");
			
			builder.append("<TRANSMONTH>");
			builder.append(t.getHhTran_transMonth());
			builder.append("</TRANSMONTH>");	
			
			builder.append("<TRANSYEAR>");
			builder.append(t.getHhTran_transYear());
			builder.append("</TRANSYEAR>");
			
			builder.append("<EXPSHIPDAY>");
			builder.append(t.getHhTran_expShipDay());
			builder.append("</EXPSHIPDAY>");	

			builder.append("<EXPSHIPMONTH>");
			builder.append(t.getHhTran_expShipMonth());
			builder.append("</EXPSHIPMONTH>");

			builder.append("<EXPSHIPYEAR>");
			builder.append(t.getHhTran_expShipYear());
			builder.append("</EXPSHIPYEAR>");

			builder.append("<CUSTOMERNUMBER>");
			builder.append(t.getHhTran_customerNumber());
			builder.append("</CUSTOMERNUMBER>");

			builder.append("<SALESPERSON>");
			builder.append(t.getHhTran_salesPerson());
			builder.append("</SALESPERSON>");
			
			builder.append("<ITEMNUMBER>");
			builder.append(t.getHhTran_itemNumber());
			builder.append("</ITEMNUMBER>");
			
			builder.append("<LOCID>");
			builder.append(t.getHhTran_locId());
			builder.append("</LOCID>");
			
			builder.append("<TERMS>");
			builder.append(t.getHhTran_terms());
			builder.append("</TERMS>");
			
			builder.append("<CURRENCY>");
			builder.append(t.getHhTran_currency());
			builder.append("</CURRENCY>");

			builder.append("<PRICELISTCODE>");
			builder.append(t.getHhTran_priceListCode());
			builder.append("</PRICELISTCODE>");
			
			builder.append("<UOM>");
			builder.append(t.getHhTran_uom());
			builder.append("</UOM>");

			builder.append("<QTY>");
			if(isReturn){
				builder.append("-"+t.getHhTran_qty()); // add minus for return item qty
			}else{
				builder.append(t.getHhTran_qty());
			}
			
			builder.append("</QTY>");

			builder.append("<PRICE>");
			builder.append(t.getHhTran_price());
			builder.append("</PRICE>");

			builder.append("<DISCPRICE>");
			builder.append(t.getHhTran_discPrice());
			builder.append("</DISCPRICE>");

			builder.append("<NETPRICE>");
			builder.append(t.getHhTran_netPrice());
			builder.append("</NETPRICE>");

			builder.append("<EXTPRICE>");
			builder.append(t.getHhTran_extPrice());
			builder.append("</EXTPRICE>");
			
			builder.append("<TAX>");
			builder.append(t.getHhTran_tax());
			builder.append("</TAX>");
			
			builder.append("<ShipToCode>");
			builder.append(t.getHhTran_shipToCode());
			builder.append("</ShipToCode>");
			
			builder.append("<ShipViaCode>");
			builder.append(t.getHhTran_shipViaCode());
			builder.append("</ShipViaCode>");
		
			builder.append("<DISCVALUE>");
			builder.append(t.getHhTran_discValue());
			builder.append("</DISCVALUE>");
			
			builder.append("<DISCTYPE>");
			//</HISTORY 12-OCTOBER-2017> Suresh changed  DISCTYPE to P always because We are Passing Amount only
		//	builder.append(t.getHhTran_discType());
			builder.append("A");
				//</HISTORY 12-OCTOBER-2017> Suresh changed  DISCTYPE to P always because We are Passing Amount only
			builder.append("</DISCTYPE>");
			
			builder.append("<ORDSHIPDAY>");
			builder.append(t.getHhTran_ordShipDay());
			builder.append("</ORDSHIPDAY>");
			
			builder.append("<ORDSHIPMONTH>");
			builder.append(t.getHhTran_ordShipMonth());
			builder.append("</ORDSHIPMONTH>");

			builder.append("<ORDSHIPYEAR>");
			builder.append(t.getHhTran_ordShipYear());
			builder.append("</ORDSHIPYEAR>");
		
			builder.append("<manitemno>");
			builder.append(t.getHhTran_manItemNo());
			builder.append("</manitemno>");
		
			builder.append("<REFNO>");
			builder.append(t.getHhTran_refNo());
			builder.append("</REFNO>");

			/*builder.append("<SALESCOMMISION>");
			builder.append(t.getHhSalescommission());
			builder.append("</SALESCOMMISSION>");*/

			return builder;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
