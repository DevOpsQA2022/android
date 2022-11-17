package com.mobilesalesperson.database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhContractPrice01;
import com.mobilesalesperson.model.HhConversionFactor01;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhCustomerGroup01;
import com.mobilesalesperson.model.HhEmailSetting;
import com.mobilesalesperson.model.HhHistory01;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.HhItemPriceTax01;
import com.mobilesalesperson.model.HhItemTax01;
import com.mobilesalesperson.model.HhLocMast01;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.HhMultiUOM01;
import com.mobilesalesperson.model.HhPayment01;
import com.mobilesalesperson.model.HhPrepayment01;
import com.mobilesalesperson.model.HhPrice01;
import com.mobilesalesperson.model.HhPriceListMaster01;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.model.HhReceiptType01;
import com.mobilesalesperson.model.HhRoute01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhShipTo01;
import com.mobilesalesperson.model.HhShipVia01;
import com.mobilesalesperson.model.HhSignature;
import com.mobilesalesperson.model.HhTaxClass01;
import com.mobilesalesperson.model.HhTaxGroup01;
import com.mobilesalesperson.model.HhTaxRate01;
import com.mobilesalesperson.model.HhTerms01;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.model.HhUPC01;
import com.mobilesalesperson.model.Manf_Number01;
import com.mobilesalesperson.model.Mspdb;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.LogfileCreator;

import static android.content.ContentValues.TAG;

/**
 * @author TIVM class to help database process
 */
public class MspDBHelper{

    private Cursor cursor;
    private SQLiteDatabase db;
    private File DATABASE_FILE_PATH = null;
    private File fpath = null;
    private String errCode;
    private String msg;
    private String errMsg;

    // database name definitions
    private static final String DATABASE_NAME = "msp.db";

    // table name definitions
    public static final String CUSTOMER_TABLE = "hhCustomer01";
    public static final String ITEM_TABLE = "hhItem01";
    public static final String ITEM_PRICE_LIST_MASTER_TABLE = "hhPriceListMaster01";
    public static final String PRICE_TABLE = "hhPrice01";
    public static final String CUSTOMER_GROUPCODE_TABLE = "hhCustomerGroup01";
    public static final String SHIPTO_TABLE = "hhShipTo01";
    public static final String SHIPVIA_TABLE = "hhShipVia01";
    public static final String CONVFACTOR_TABLE = "hhConversionFactor01";
    public static final String TRANS_TABLE = "hhTran01";
    public static final String HISTORY_TABLE = "hhHistory01";
    public static final String MSPDB_TABLE = "mspdb";
    public static final String PREPAYMENT_TABLE = "hhPrepayment01";
    public static final String COMPANY_TABLE = "hhCompany";
    public static final String CONT_PRICE_TABLE = "hhContractPrice01";
    public static final String ITMPRCTAX_TABLE = "hhItemPriceTax01";
    public static final String ITEMTAX_TABLE = "hhItemTax01";
    public static final String LOCMAST_TABLE = "hhLocMast01";
    public static final String MANAGER_TABLE = "hhManager";
    public static final String RECEIPT_TABLE = "hhReceipt01"; /* Created by TISN */
    public static final String RECEIPTTYPE_TABLE = "hhReceiptType01";
    public static final String MANUF_TABLE = "manf_number01";
    public static final String PAYMENT_TABLE = "hhPayment01";
    public static final String MULTIUOM_TABLE = "hhMultiUOM01";
    public static final String ROUTE_TABLE = "hhRoute01";
    public static final String TAXCLASS_TABLE = "hhTaxClass01";
    public static final String TAXGROUP_TABLE = "hhTaxGroup01";
    public static final String TAXRATE_TABLE = "hhTaxRate01";
    public static final String TERMS_TABLE = "hhTerms01";
    public static final String UPC_TABLE = "hhUPC01";
    public static final String KILOMETER_TABLE = "hhKiloMeter01";
    public static final String VAN_TABLE = "hhVan01";

    public static final String SETTING_TABLE = "hhSetting";
    public static final String SIGNATURE_TABLE = "hhSignature";
    // temporary tables
    public static final String TEMPITEM_TABLE = "tempitem";
    public static final String EMAIL_SETTING_TABLE = "hhEmailSetting";
    // customer field declaration
    public static final String CUSTOMER_NUMBER = "number";
    private static final String CUSTOMER_ALT_NO = "customer_alt_no";

    public static final String CUSTOMER_NAME = "name";
    public static final String CUSTOMER_ADDRESS = "address";
    public static final String CUSTOMER_CITY = "city";
    public static final String CUSTOMER_STATE = "state";
    public static final String CUSTOMER_ZIP = "zip";
    public static final String CUSTOMER_COUNTRY = "country";
    public static final String CUSTOMER_PHONE1 = "phone1";
    public static final String CUSTOMER_EMAIL1 = "email1";
    public static final String CUSTOMER_WEBSITE = "website";
    public static final String CUSTOMER_CONTACT = "contact";
    public static final String CUSTOMER_EMAIL2 = "email2";
    public static final String CUSTOMER_GROUPCODE = "groupcode";
    public static final String CUSTOMER_TYPE = "type";
    public static final String CUSTOMER_TERMS = "terms";
    public static final String CUSTOMER_LASTBALANCE = "lastbalance";
    public static final String CUSTOMER_CREDITLIMIT = "creditlimit";
    public static final String CUSTOMER_LASTSALE = "lastsale";
    public static final String CUSTOMER_TAXGROUP = "taxgroup";
    public static final String CUSTOMER_COMMENT = "comment";
    public static final String CUSTOMER_TAXAUTHORITY1 = "taxauthority1";
    public static final String CUSTOMER_TAXABLE1 = "taxable1";
    public static final String CUSTOMER_TAXSTTS1 = "taxstts1";
    public static final String CUSTOMER_TAXAUTHORITY2 = "taxauthority2";
    public static final String CUSTOMER_TAXABLE2 = "taxable2";
    public static final String CUSTOMER_TAXSTTS2 = "taxstts2";
    public static final String CUSTOMER_TAXAUTHORITY3 = "taxauthority3";
    public static final String CUSTOMER_TAXABLE3 = "taxable3";
    public static final String CUSTOMER_TAXSTTS3 = "taxstts3";
    public static final String CUSTOMER_TAXAUTHORITY4 = "taxauthority4";
    public static final String CUSTOMER_TAXABLE4 = "taxable4";
    public static final String CUSTOMER_TAXSTTS4 = "taxstts4";
    public static final String CUSTOMER_TAXAUTHORITY5 = "taxauthority5";
    public static final String CUSTOMER_TAXABLE5 = "taxable5";
    public static final String CUSTOMER_TAXSTTS5 = "taxstts5";
    public static final String CUSTOMER_STATUS = "status";
    public static final String CUSTOMER_PRICELISTCODE = "pricelistcode";
    public static final String CUSTOMER_IDTAXREGI1 = "idtaxregi1";
    public static final String CUSTOMER_CREDITSTATUS = "creditstatus";
    public static final String CUSTOMER_SHIPVIA = "shipvia";
    public static final String CUSTOMER_COMPANYCODE = "companycode";

    // item field declaration
    public static final String ITEM_NUMBER = "number";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_CATEGORY = "category";
    public static final String ITEM_LOCID = "locid";
    public static final String ITEM_QTYONHAND = "qtyonhand";
    public static final String ITEM_CURRENCY = "currency";
    public static final String ITEM_PRICELISTCODE = "pricelistcode";
    public static final String ITEM_ALTITEM = "altitem";
    public static final String ITEM_ALTITEMDESC = "altitemdesc";
    public static final String ITEM_COSTINGMETHOD = "costingmethod";
    public static final String ITEM_STANDARDCOST = "standardcost";
    public static final String ITEM_MOSTRECENTCOST = "mostrecentcost";
    public static final String ITEM_AVERAGECOST = "averagecost";
    public static final String ITEM_LASTUNITCOST = "lastunitcost";
    public static final String ITEM_COST1 = "cost1";
    public static final String ITEM_COST2 = "cost2";
    public static final String ITEM_STOCKINGUOM = "stockinguom";
    public static final String ITEM_STOCKINGCONVFACTOR = "stockingconvfactor";
    public static final String ITEM_LOCUOM = "locuom";
    public static final String ITEM_LOCCONVFACTOR = "locconvfactor";
    public static final String ITEM_CARTONDISC = "cartondisc";
    public static final String ITEM_UNITWGT = "unitwgt";
    public static final String ITEM_STOCKITEM = "stockitem";
    public static final String ITEM_COMPANYCODE = "companycode";

    // pricelistmaster field declaration
    public static final String ITEM_PRICE_LIST = "pricelist";
    public static final String ITEM_PRICE_LIST_COMPANYCODE = "companycode";

    // price field declaration
    public static final String PRICE_NUMBER = "number";
    public static final String PRICE_DISCTYPE = "disctype";
    public static final String PRICE_PRICE1 = "price1";
    public static final String PRICE_PRICE2 = "price2";
    public static final String PRICE_PRICE3 = "price3";
    public static final String PRICE_PRICE4 = "price4";
    public static final String PRICE_PRICE5 = "price5";
    public static final String PRICE_PRICE6 = "price6";
    public static final String PRICE_QTYLEVEL1 = "qtylevel1";
    public static final String PRICE_QTYLEVEL2 = "qtylevel2";
    public static final String PRICE_QTYLEVEL3 = "qtylevel3";
    public static final String PRICE_QTYLEVEL4 = "qtylevel4";
    public static final String PRICE_QTYLEVEL5 = "qtylevel5";
    public static final String PRICE_MARKUPCOST = "markupcost";
    public static final String PRICE_PRICELISTCODE = "pricelistcode";
    public static final String PRICE_LOCATION = "location";
    public static final String PRICE_PRICINGUOM = "pricinguom";
    public static final String PRICE_PRICINGCONVFACTOR = "pricingconvfactor";
    public static final String PRICE_MARKUPUOM = "markupuom";
    public static final String PRICE_MARKUPCONVFACTOR = "markupconvfactor";
    public static final String PRICE_PRICETYPE = "pricetype";
    public static final String PRICE_SALESPRICE = "salesprice";
    public static final String PRICE_SALESUOM = "salesuom";
    public static final String PRICE_SALESCONVFACTOR = "salesconvfactor";
    public static final String PRICE_SALESSTARTDATE = "salesstartdate";
    public static final String PRICE_SALESENDDATE = "salesenddate";
    public static final String PRICE_COMPANYCODE = "companycode";

    // customer group field declaration
    public static final String CUSGCODE_GROUPCODE = "groupcode";
    public static final String CUSGCODE_DESCRIPTION = "description";
    public static final String CUSGCODE_TERMS = "terms";
    public static final String CUSGCODE_COMPANYCODE = "companycode";

    // shipto field declaration
    public static final String SHIPTO_CUSTOMERNUMBER = "customernumber";
    public static final String SHIPTO_PRIMARYSHIPTO = "primaryshipto";
    public static final String SHIPTO_LOCCODE = "loccode";
    public static final String SHIPTO_DESCRIPTION = "description";
    public static final String SHIPTO_SHIPVIA = "shipvia";
    public static final String SHIPTO_VIADESCRIPTION = "viadescription";
    public static final String SHIPTO_ADDRESS = "address";
    public static final String SHIPTO_CITY = "city";
    public static final String SHIPTO_STATE = "state";
    public static final String SHIPTO_ZIP = "zip";
    public static final String SHIPTO_COUNTRY = "country";
    public static final String SHIPTO_CONTACT = "contact";
    public static final String SHIPTO_TAXGROUP = "taxgroup";
    public static final String SHIPTO_TAXAUTHORITY1 = "taxauthority1";
    public static final String SHIPTO_TAXABLE1 = "taxable1";
    public static final String SHIPTO_TAXSTTS1 = "taxstts1";
    public static final String SHIPTO_TAXAUTHORITY2 = "taxauthority2";
    public static final String SHIPTO_TAXABLE2 = "taxable2";
    public static final String SHIPTO_TAXSTTS2 = "taxstts2";
    public static final String SHIPTO_TAXAUTHORITY3 = "taxauthority3";
    public static final String SHIPTO_TAXABLE3 = "taxable3";
    public static final String SHIPTO_TAXSTTS3 = "taxstts3";
    public static final String SHIPTO_TAXAUTHORITY4 = "taxauthority4";
    public static final String SHIPTO_TAXABLE4 = "taxable4";
    public static final String SHIPTO_TAXSTTS4 = "taxstts4";
    public static final String SHIPTO_TAXAUTHORITY5 = "taxauthority5";
    public static final String SHIPTO_TAXABLE5 = "taxable5";
    public static final String SHIPTO_TAXSTTS5 = "taxstts5";
    public static final String SHIPTO_COMPANYCODE = "companycode";

    // shipvia field declaration
    public static final String SHIPVIA_CODE = "code";
    public static final String SHIPVIA_NAME = "name";
    public static final String SHIPVIA_COMPANYCODE = "companycode";

    // conversion factor declaration
    public static final String CONVFACTOR_NUMBER = "number";
    public static final String CONVFACTOR_UOMCONVFACTOR = "uomconvfactor";
    public static final String CONVFACTOR_UOMNAME = "uomname";
    public static final String CONVFACTOR_COMPANYCODE = "companycode";

    // transaction table field declaration
    public static final String TRANS_TRANSTYPE = "transtype";
    public static final String TRANS_REFERENCENUMBER = "referencenumber";
    public static final String TRANS_INVOICENUMBER = "invoicenumber";
    public static final String TRANS_ORDERNUMBER = "ordernumber";
    public static final String TRANS_TRANSDAY = "transday";
    public static final String TRANS_TRANSMONTH = "transmonth";
    public static final String TRANS_TRANSYEAR = "transyear";
    public static final String TRANS_EXPSHIPDAY = "expshipday";
    public static final String TRANS_EXPSHIPMONTH = "expshipmonth";
    public static final String TRANS_EXPSHIPYEAR = "expshipyear";
    public static final String TRANS_CUSTOMERNUMBER = "customernumber";
    public static final String TRANS_SALESPERSON = "salesperson";
    private static final String SALES_COMMISSION = "sales_commission";
    private static final String COMMENTS = "comments";
    public static final String TRANS_ITEMNUMBER = "itemnumber";
    public static final String TRANS_LOCID = "locid";
    public static final String TRANS_TERMS = "terms";
    public static final String TRANS_CURRENCY = "currency";
    public static final String TRANS_PRICELISTCODE = "pricelistcode";
    public static final String TRANS_UOM = "uom";
    public static final String TRANS_QTY = "qty";
    public static final String TRANS_PRICE = "price";
    public static final String TRANS_DISCPRICE = "discprice";
    public static final String TRANS_NETPRICE = "netprice";
    public static final String TRANS_EXTPRICE = "extprice";
    public static final String TRANS_TAX = "tax";
    public static final String TRANS_SHIPTOCODE = "shiptocode";
    public static final String TRANS_SHIPVIACODE = "shipviacode";
    public static final String TRANS_STATUS = "status";
    public static final String TRANS_LINEITEM = "lineitem";
    public static final String TRANS_DISCVALUE = "discvalue";
    public static final String TRANS_DISCTYPE = "disctype";
    public static final String TRANS_ORDSHIPDAY = "ordshipday";
    public static final String TRANS_ORDSHIPMONTY = "ordshipmonth";
    public static final String TRANS_ORDSHIPYEAR = "ordshipyear";
    public static final String TRANS_EDITABLE = "editable";
    public static final String TRANS_MANITEMNO = "manitemno";
    public static final String TRANS_PRETAX = "pretax";
    public static final String TRANS_REFNO = "refno";
    public static final String TRANS_ORDERDISCOUNT = "orderdiscount";
    public static final String TRANS_TAXAMOUNT = "taxamount";
    public static final String TRANS_TOTALCARTON = "totalcarton";
    public static final String TRANS_EDITED_CUSTOMERNAME = "editedcustomername";
    public static final String TRANS_ITEMUNITWEIGHT = "itemunitweight";
    public static final String TRANS_COMPANYCODE = "companycode";
    public static final String TRANS_LATITUDE = "latitude";
    public static final String TRANS_LONGITUDE = "longitude";


    // transaction table field declaration
    public static final String HISTORY_TRANS_TRANSTYPE = "transtype";
    public static final String HISTORY_TRANS_REFERENCENUMBER = "referencenumber";
    public static final String HISTORY_TRANS_INVOICENUMBER = "invoicenumber";
    public static final String HISTORY_TRANS_ORDERNUMBER = "ordernumber";
    public static final String HISTORY_TRANS_TRANSDAY = "transday";
    public static final String HISTORY_TRANS_TRANSMONTH = "transmonth";
    public static final String HISTORY_TRANS_TRANSYEAR = "transyear";
    public static final String HISTORY_TRANS_EXPSHIPDAY = "expshipday";
    public static final String HISTORY_TRANS_EXPSHIPMONTH = "expshipmonth";
    public static final String HISTORY_TRANS_EXPSHIPYEAR = "expshipyear";
    public static final String HISTORY_TRANS_CUSTOMERNUMBER = "customernumber";
    public static final String HISTORY_TRANS_SALESPERSON = "salesperson";
    public static final String HISTORY_TRANS_ITEMNUMBER = "itemnumber";
    public static final String HISTORY_TRANS_LOCID = "locid";
    public static final String HISTORY_TRANS_TERMS = "terms";
    public static final String HISTORY_TRANS_CURRENCY = "currency";
    public static final String HISTORY_TRANS_PRICELISTCODE = "pricelistcode";
    public static final String HISTORY_TRANS_UOM = "uom";
    public static final String HISTORY_TRANS_QTY = "qty";
    public static final String HISTORY_TRANS_PRICE = "price";
    public static final String HISTORY_TRANS_DISCPRICE = "discprice";
    public static final String HISTORY_TRANS_NETPRICE = "netprice";
    public static final String HISTORY_TRANS_EXTPRICE = "extprice";
    public static final String HISTORY_TRANS_TAX = "tax";
    public static final String HISTORY_TRANS_SHIPTOCODE = "shiptocode";
    public static final String HISTORY_TRANS_SHIPVIACODE = "shipviacode";
    public static final String HISTORY_TRANS_STATUS = "status";
    public static final String HISTORY_TRANS_LINEITEM = "lineitem";
    public static final String HISTORY_TRANS_DISCVALUE = "discvalue";
    public static final String HISTORY_TRANS_DISCTYPE = "disctype";
    public static final String HISTORY_TRANS_ORDSHIPDAY = "ordshipday";
    public static final String HISTORY_TRANS_ORDSHIPMONTY = "ordshipmonth";
    public static final String HISTORY_TRANS_ORDSHIPYEAR = "ordshipyear";
    public static final String HISTORY_TRANS_EDITABLE = "editable";
    public static final String HISTORY_TRANS_MANITEMNO = "manitemno";
    public static final String HISTORY_TRANS_PRETAX = "pretax";
    public static final String HISTORY_TRANS_REFNO = "refno";
    public static final String HISTORY_TRANS_ORDERDISCOUNT = "orderdiscount";
    public static final String HISTORY_TRANS_TAXAMOUNT = "taxamount";
    public static final String HISTORY_TRANS_TOTALCARTON = "totalcarton";
    public static final String HISTORY_TRANS_EDITED_CUSTOMERNAME = "editedcustomername";
    public static final String HISTORY_TRANS_ITEMUNITWEIGHT = "itemunitweight";
    public static final String HISTORY_TRANS_COMPANYCODE = "companycode";
    public static final String HISTORY_TRANS_LATITUDE = "latitude";
    public static final String HISTORY_TRANS_LONGITUDE = "longitude";
    // msbdb table field declaration
    public static final String MSPDB_COMPANYNUMBER = "companynumber";
    public static final String MSPDB_SALESPERSON = "salesperson";
    public static final String MSPDB_MAPNO = "mapno";
    public static final String MSPDB_CUSTOMERNUMBER = "customernumber";
    public static final String MSPDB_ORDERNUMBER = "ordernumber";
    public static final String MSPDB_QUOTENUMBER = "quotenumber";
    public static final String MSPDB_LASTINVNO = "lastinvno";
    public static final String MSPDB_RECEIPTNUMBER = "receiptnumber";

    // Prepayment table field declaration
    public static final String PREPAYMENT_TRANSTYPE = "transtype";
    public static final String PREPAYMENT_REFERENCENUMBER = "referencenumber";
    public static final String PREPAYMENT_INVOICENUMBER = "invoicenumber";
    public static final String PREPAYMENT_CUSTOMERNUMBER = "customernumber";
    public static final String PREPAYMENT_CURRENCY = "currency";
    public static final String PREPAYMENT_ORDERTOTAL = "ordertotal";
    public static final String PREPAYMENT_AMTDUE = "amtdue";
    public static final String PREPAYMENT_RECEIPTTYPE = "receipttype";
    public static final String PREPAYMENT_CHECKRECEIPTNO = "checkreceiptno";
    public static final String PREPAYMENT_RECEIPTDAY = "receiptday";
    public static final String PREPAYMENT_RECEIPTMONTH = "receiptmonth";
    public static final String PREPAYMENT_RECEIPTYEAR = "receiptyear";
    public static final String PREPAYMENT_RECEIPTAMOUNT = "receiptamount";
    public static final String PREPAYMENT_ACCPACORDNUMBER = "accpacordnumber";
    public static final String PREPAYMENT_FLAG = "flag";
    public static final String PREPAYMENT_REFNO = "refno";
    public static final String PREPAYMENT_COMPANYCODE = "companycode";
    public static final String PREPAYMENT_RECEIPTAMOUNT1 = "receiptamount2";
    /*public static final String PREPAYMENT_RECEIPTAMOUNT2= "receiptamount2";*/
    public static final String PREPAYMENT_RECEIPTTYPE2 = "receipttype2";

    // company field declaration
    public static final String COMPANY_NUMBER = "number";
    public static final String COMPANY_NAME = "name";
    public static final String COMPANY_ADDRESS = "address";
    public static final String COMPANY_CITY = "city";
    public static final String COMPANY_STATE = "state";
    public static final String COMPANY_ZIP = "zip";
    public static final String COMPANY_COUNTRY = "country";
    public static final String COMPANY_PHONE = "phone";
    public static final String COMPANY_CONTACT = "contact";
    public static final String COMPANY_ISSTOCKINGUOM = "isstockinguom";
    public static final String COMPANY_TINNO = "tinno";

    // contract price field declaration
    public static final String CONT_PRICE_NUMBER = "customerumber";
    public static final String CONT_PRICE_PRICEBY = "priceby";
    public static final String CONT_PRICE_CATEGORY = "category";
    public static final String CONT_PRICE_ITEMNUMBER = "itemnumber";
    public static final String CONT_PRICE_PRICELISTCODE = "pricelistcode";
    public static final String CONT_PRICE_PRICETYPE = "pricetype";
    public static final String CONT_PRICE_CUSTOMERTYPE = "customertype";
    public static final String CONT_PRICE_DISCPERCENT = "discpercent";
    public static final String CONT_PRICE_DISCAMOUNT = "discamount";
    public static final String CONT_PRICE_COSTMETHOD = "costmethod";
    public static final String CONT_PRICE_PLUSAMOUNT = "plusamount";
    public static final String CONT_PRICE_PLUSPERCENT = "pluspercent";
    public static final String CONT_PRICE_FIXEDPRICE = "fixedprice";
    public static final String CONT_PRICE_CALCUNITPRICE = "calcunitprice";
    public static final String CONT_PRICE_USELOWEST = "uselowest";
    public static final String CONT_PRICE_COMPANYCODE = "companycode";

    // Item price tax field declaration
    public static final String ITMPRCTAX_NUMBER = "number";
    public static final String ITMPRCTAX_PRICELIST = "pricelist";
    public static final String ITMPRCTAX_AUTHORITY = "authority";
    public static final String ITMPRCTAX_SALESTAXCLASS = "salestaxclass";
    public static final String ITMPRCTAX_COMPANYCODE = "companycode";

    // Item tax field declaration
    public static final String ITEMTAX_ITEM_NUMBER = "itemnumber";
    public static final String ITEMTAX_AUTHORITY = "authority";
    public static final String ITEMTAX_SALESTAXCLASS = "salestaxclass";
    public static final String ITEMTAX_COMPANYCODE = "companycode";

    // bank field declaration
    public static final String BANK_BANKCODE = "bankcode";
    public static final String BANK_BANKNAME = "bankname";
    public static final String BANK_COMPANYCODE = "companycode";

    // Location master field declaration
    public static final String LOCMAST_LOCATION = "location";
    public static final String LOCMAST_COMPANYCODE = "companycode";

    // Manager table filed declaration
    public static final String MANAGER_COMPANYID = "companyid";
    public static final String MANAGER_USERID = "userid";
    public static final String MANAGER_USERPASS = "userpass";
    public static final String MANAGER_ACTIVEKEY = "activekey";
    public static final String MANAGER_USERNAME = "username";
    public static final String MANAGER_LOCID = "locid";
    public static final String MANAGER_EMAIL = "email";
    public static final String MANAGER_CURRENCY = "currency";
    public static final String MANAGER_PRICELISTCODE = "pricelistcode";
    public static final String MANAGER_COMPANYNUMBER = "companynumber";
    public static final String MANAGER_COMPANYNAME = "companyname";
    public static final String MANAGER_COMPANYMAILID = "companymailid";
    public static final String MANAGER_UNITPRICEEDITABLE = "unitpriceeditable";
    public static final String MANAGER_ACCPACVERSION = "accpacversion";
    public static final String MANAGER_ADDCUSTOMERSTATE = "addcustomerstate";
    public static final String MANAGER_CUSTOMERBASEDPL = "customerbasedpl";
    public static final String MANAGER_ADMINPASS = "adminpass";
    public static final String MANAGER_NEG_INV = "NegativeInventory";
    public static final String MANAGER_CREATE_ORDER = "CreateOrder";
    public static final String MANAGER_CREDIT_NOTE= "CreidtNote";
    public static final String MANAGER_SUMMARY_REPORT = "SummaryReport";
    public static final String MANAGER_INVENTORY_REPORT = "InventoryReport";
    public static final String MANAGER_RECEIPT_SUMMARY_REPORT = "ReceiptSummaryReport";
    public static final String MANAGER_SALES_SUMMARY_REPORT = "SalesSummaryReport";
    public static final String MANAGER_EDIT_DELETE = "EditDelete";
    public static final String MANAGER_DEF_PAYMENTTYPE = "DefPaymentType";

    /* Created by TISN */
    // receipt field declaration
    public static final String RECEIPT_SNO = "sno";
    public static final String RECEIPT_CUSTOMERNUMBER = "customernumber";
    public static final String RECEIPT_CUSTOMERNAME = "customername";
    public static final String RECEIPT_DOCNUMBER = "docnumber";
    public static final String RECEIPT_PENDINGBAL = "pendingbal";
    public static final String RECEIPT_CURRENCY = "currency";
    public static final String RECEIPT_APPLY1 = "apply1";
    public static final String RECEIPT_APPLIEDAMOUNT = "appliedamount";
    public static final String RECEIPT_RECEIPTTYPE = "receipttype";
    public static final String RECEIPT_AMOUNT = "amount";
    public static final String RECEIPT_RECEIPTNUMBER = "receiptnumber";
    public static final String RECEIPT_RECEIPTDAY = "receiptday";
    public static final String RECEIPT_RECEIPTMONTH = "receiptmonth";
    public static final String RECEIPT_RECEIPTYEAR = "receiptyear";
    public static final String RECEIPT_STATUS = "status";
    public static final String RECEIPT_RECEIPTUNAPPLIED = "receiptunapplied";
    public static final String RECEIPT_CUSTOMERAMOUNT = "customeramount";
    public static final String RECEIPT_REFNO = "refno";
    public static final String RECEIPT_COMPANYCODE = "companycode";
    public static final String RECEIPT_RECEIPTTYPE2 = "receipttype2";
    public static final String RECEIPT_AMOUNT2 = "amount2";

    // receipt type field declaration
    public static final String RECEIPTTYPE_RECEIPTTYPE = "receipttype";
    public static final String RECEIPTTYPE_RECEIPTNAME = "receiptname";
    public static final String RECEIPTTYPE_COMPANYCODE = "companycode";

    // Manufacture number table field declaration
    public static final String MANUF_MANITEMNO = "manitemno";
    public static final String MANUF_ITEMNO = "itemno";
    public static final String MANUF_UOM = "uom";
    public static final String MANUF_COMPNAYCODE = "companycode";

    // Payment table field declaration
    public static final String PAYMENT_APPLY1 = "apply1";
    public static final String PAYMENT_CUSNUMBER = "cusnumber";
    public static final String PAYMENT_CUSNAME = "cusname";
    public static final String PAYMENT_DOCUMENTNUMBER = "documentnumber";
    public static final String PAYMENT_PENDINGBALANCE = "pendingbalance";
    public static final String PAYMENT_APPLIEDAMOUNT = "appliedamount";
    public static final String PAYMENT_NETAMOUNT = "netamount";
    public static final String PAYMENT_STATUS = "status";
    public static final String PAYMENT_INVOICEDATE = "invoicedate";
    public static final String PAYMENT_DUEDATE = "duedate";
    public static final String PAYMENT_COMPANYCODE = "companycode";

    // MultiUOM table field declaration
    public static final String MULTIUOM_NUMBER = "number";
    public static final String MULTIUOM_QTYUOM = "qtyuom";
    public static final String MULTIUOM_DISCTYPE = "disctype";
    public static final String MULTIUOM_PRICE1 = "price1";
    public static final String MULTIUOM_PRICE2 = "price2";
    public static final String MULTIUOM_PRICE3 = "price3";
    public static final String MULTIUOM_PRICE4 = "price4";
    public static final String MULTIUOM_PRICE5 = "price5";
    public static final String MULTIUOM_PRICE6 = "price6";
    public static final String MULTIUOM_QTYLEVEL1 = "qtylevel1";
    public static final String MULTIUOM_QTYLEVEL2 = "qtylevel2";
    public static final String MULTIUOM_QTYLEVEL3 = "qtylevel3";
    public static final String MULTIUOM_QTYLEVEL4 = "qtylevel4";
    public static final String MULTIUOM_QTYLEVEL5 = "qtylevel5";
    public static final String MULTIUOM_MARKUPCOST = "markupcost";
    public static final String MULTIUOM_PRICELISTCODE = "pricelistcode";
    public static final String MULTIUOM_LOCATION = "location";
    public static final String MULTIUOM_PRICINGUOM = "pricinguom";
    public static final String MULTIUOM_PRICINGCONVFACTOR = "pricingconvfactor";
    public static final String MULTIUOM_MARKUPUOM = "markupuom";
    public static final String MULTIUOM_MARKUPCONVFACTOR = "markupconvfactor";
    public static final String MULTIUOM_COMPANYCODE = "companycode";

    // Route field declaration
    public static final String ROUTE_CUSTOMERNUMBER = "customernumber";
    public static final String ROUTE_CUSTOMERNAME = "customername";
    public static final String ROUTE_DAYOFVISIT = "dayofvisit";
    public static final String ROUTE_MONTHOFVISIT = "monthofvisit";
    public static final String ROUTE_YEAROFVISIT = "yearofvisit";
    public static final String ROUTE_VISITSTAT = "visitstat";
    public static final String ROUTE_COMPANYCODE = "companycode";

    // Tax class field declaration
    public static final String TAXCLASS_TAXAUTHORITY = "taxauthority";
    public static final String TAXCLASS_TAXSTTS = "taxstts";
    public static final String TAXCLASS_DESCRIPTION = "description";
    public static final String TAXCLASS_COMPANYCODE = "companycode";

    // Taxgroup field declaration
    public static final String TAXGROUP_TAXGROUP = "taxgroup";
    public static final String TAXGROUP_TAXAUTHORITY1 = "taxauthority1";
    public static final String TAXGROUP_TAXSTTS1 = "taxstts1";
    public static final String TAXGROUP_TAXABLE1 = "taxable1";
    public static final String TAXGROUP_TAXAUTHORITY2 = "taxauthority2";
    public static final String TAXGROUP_TAXSTTS2 = "taxstts2";
    public static final String TAXGROUP_TAXABLE2 = "taxable2";
    public static final String TAXGROUP_TAXAUTHORITY3 = "taxauthority3";
    public static final String TAXGROUP_TAXSTTS3 = "taxstts3";
    public static final String TAXGROUP_TAXABLE3 = "taxable3";
    public static final String TAXGROUP_TAXAUTHORITY4 = "taxauthority4";
    public static final String TAXGROUP_TAXSTTS4 = "taxstts4";
    public static final String TAXGROUP_TAXABLE4 = "taxable4";
    public static final String TAXGROUP_TAXAUTHORITY5 = "taxauthority5";
    public static final String TAXGROUP_TAXSTTS5 = "taxstts5";
    public static final String TAXGROUP_TAXABLE5 = "taxable5";
    public static final String TAXGROUP_COMPANYCODE = "companycode";

    // Taxrate field declaration
    public static final String TAXRATE_AUTHORITY = "authority";
    public static final String TAXRATE_BUYERCLASS = "buyerclass";
    public static final String TAXRATE_ITEMRATE1 = "itemrate1";
    public static final String TAXRATE_ITEMRATE2 = "itemrate2";
    public static final String TAXRATE_ITEMRATE3 = "itemrate3";
    public static final String TAXRATE_ITEMRATE4 = "itemrate4";
    public static final String TAXRATE_ITEMRATE5 = "itemrate5";
    public static final String TAXRATE_ITEMRATE6 = "itemrate6";
    public static final String TAXRATE_ITEMRATE7 = "itemrate7";
    public static final String TAXRATE_ITEMRATE8 = "itemrate8";
    public static final String TAXRATE_ITEMRATE9 = "itemrate9";
    public static final String TAXRATE_ITEMRATE10 = "itemrate10";
    public static final String TAXRATE_COMPANYCODE = "companycode";

    // Terms table field declaration
    public static final String TERMS_CODETERM = "codeterm";
    public static final String TERMS_COUNTPAYMENT = "countpayment";
    public static final String TERMS_PERCENTDUE = "percentdue";
    public static final String TERMS_PERCENTDISC = "percentdisc";
    public static final String TERMS_NUMBERDAYS = "numberdays";
    public static final String TERMS_DUEDAYS = "duedays";
    public static final String TERMS_DISCWITHTAX = "discwithtax";
    public static final String TERMS_COMPANYCODE = "companycode";

    // UPC field declaration
    public static final String UPC_ITEMNUMBER = "itemnumber";
    public static final String UPC_UPCNUMBER = "upcnumber";
    public static final String UPC_ITEMDESC = "itemdesc";
    public static final String UPC_COMPANYCODE = "companycode";

    // Kilometer field declaration
    public static final String KILOMETER_MVSP = "mvsp";
    public static final String KILOMETER_DOT = "dot";
    public static final String KILOMETER_SKM = "skm";
    public static final String KILOMETER_EKM = "ekm";
    public static final String KILOMETER_TOTALKM = "totalkm";

    // Van field declaration
    public static final String VAN_DOT = "dot";
    public static final String VAN_VNO = "vno";

    // setting field creation
    public static final String SETTING_NUMCOPIES_PRINT = "numcopiesprint";
    public static final String SETTING_ITEM_AUTOSAVE = "itemautosave";
    public static final String SETTING_NUMOF_PIECEDISC = "numofpiecedisc";
    public static final String SETTING_DISCPRICE = "discprice";
    public static final String SETTING_DATE_FORMAT = "dateformat";
    public static final String SETTING_DECIMAL_FORMAT = "decimalformat";
    public static final String SETTING_SYNC_SERVICE = "datasyncservice";
    public static final String SETTING_MSP_SERVERPATH = "mspserverpath";
    public static final String SETTING_PRINTER_NAME = "printername";
    public static final String SETTING_PRINTER_MODEL = "printermodel";
    public static final String SETTING_SHOW_SHIP = "showship";
    public static final String SETTING_SHOW_PREPAY = "showprepay";
    public static final String SETTING_AUTOREPORT_GEN = "autoreportgen";
    public static final String SETTING_SPCODE = "spcode";
    // public static final String SETTING_COMPANY_NUMBER = "companynumber";
    public static final String SETTING_CUSTOMER_NAME_EDIT = "customernameeditable";
    public static final String SETTING_NONSTOCK_ITEM = "nonstockitem";

    // signature field creation
    public static final String SIGNATURE_ID = "signid";
    public static final String SIGNATURE_REF_NUMBER = "refernumber";
    public static final String SIGNATURE_COMPANY_NAME = "companyname";
    public static final String SIGNATURE_SIGN = "signature";
    public static final String SIGNATURE_STATUS = "status";
    public static final String SIGNATURE_DATE = "date";
    public static final String SIGNATURE_COMPANYCODE = "companycode";

    // temporary item field declaration
    public static final String TEMPITEM_NUMBER = "number";
    public static final String TEMPITEM_QTY = "qty";
    public static final String TEMPITEM_EXTPRICE = "extprice";
    public static final String TEMPITEM_LOCATION = "location";
    public static final String TEMPITEM_UOM = "uom";
    public static final String TEMPITEM_DATE = "date";
    public static final String TEMPITEM_ENTRYTYPE = "entrytype";
    public static final String TEMPITEM_DISCOUNT = "discount";
    public static final String TEMPITEM_DISCTYPE = "disctype";

    // Email Setting table field declaration
    public static final String EMAILSETTING_SALESPERSON_EMAIL = "salespersonemail";
    public static final String EMAILSETTING_SALESPERSON_PWD = "salespersonpwd";
    public static final String EMAILSETTING_COMPANY_EMAIL = "companyemail";
    public static final String EMAILSETTING_COMPANY_PWD = "companypwd";
    public static final String EMAILSETTING_SALESPERSON_HOSTNAME = "salespersonhostname";
    public static final String EMAILSETTING_SALESPERSON_PORTNO = "salespersonportno";
    public static final String EMAILSETTING_COMPANY_HOSTNAME = "companyhostname";
    public static final String EMAILSETTING_COMPANY_PORTNO = "companyportno";




    // customer table create query string
    private static final String CUSTOMER_CREATE_QUERY = "create table "
            + CUSTOMER_TABLE + "(" + CUSTOMER_NUMBER + " text, "
            + CUSTOMER_NAME + " text, " + CUSTOMER_ADDRESS + " text, "
            + CUSTOMER_CITY + " text, " + CUSTOMER_STATE + " text, "
            + CUSTOMER_ZIP + " text, " + CUSTOMER_COUNTRY + " text, "
            + CUSTOMER_PHONE1 + " text, " + CUSTOMER_EMAIL1 + " text, "
            + CUSTOMER_WEBSITE + " text, " + CUSTOMER_CONTACT + " text, "
            + CUSTOMER_EMAIL2 + " text, " + CUSTOMER_GROUPCODE + " text, "
            + CUSTOMER_TYPE + " text, " + CUSTOMER_TERMS + " text, "
            + CUSTOMER_LASTBALANCE + " numeric, " + CUSTOMER_CREDITLIMIT
            + " numeric, " + CUSTOMER_LASTSALE + " numeric, "
            + CUSTOMER_TAXGROUP + " text, " + CUSTOMER_COMMENT + " text, "
            + CUSTOMER_TAXAUTHORITY1 + " text, " + CUSTOMER_TAXABLE1
            + " integer, " + CUSTOMER_TAXSTTS1 + " text, "
            + CUSTOMER_TAXAUTHORITY2 + " text, " + CUSTOMER_TAXABLE2
            + " integer, " + CUSTOMER_TAXSTTS2 + " text, "
            + CUSTOMER_TAXAUTHORITY3 + " text, " + CUSTOMER_TAXABLE3
            + " integer, " + CUSTOMER_TAXSTTS3 + " text, "
            + CUSTOMER_TAXAUTHORITY4 + " text, " + CUSTOMER_TAXABLE4
            + " integer, " + CUSTOMER_TAXSTTS4 + " text, "
            + CUSTOMER_TAXAUTHORITY5 + " text, " + CUSTOMER_TAXABLE5
            + " integer, " + CUSTOMER_TAXSTTS5 + " text, " + CUSTOMER_STATUS
            + " integer, " + CUSTOMER_PRICELISTCODE + " text, "
            + CUSTOMER_IDTAXREGI1 + " text," + CUSTOMER_CREDITSTATUS
            + " text, " + CUSTOMER_SHIPVIA + " text," + CUSTOMER_COMPANYCODE
            + " text);";


    // Item table create query string
    private static final String ITEM_CREATE_QUERY = "create table "
            + ITEM_TABLE + "(" + ITEM_NUMBER + " text, " + ITEM_DESCRIPTION
            + " text, " + ITEM_CATEGORY + " text, " + ITEM_LOCID + " text, "
            + ITEM_QTYONHAND + " numeric, " + ITEM_CURRENCY + " text, "
            + ITEM_PRICELISTCODE + " text, " + ITEM_ALTITEM + " text, "
            + ITEM_ALTITEMDESC + " text, " + ITEM_COSTINGMETHOD + " text, "
            + ITEM_STANDARDCOST + " numeric, " + ITEM_MOSTRECENTCOST
            + " numeric, " + ITEM_AVERAGECOST + " numeric, "
            + ITEM_LASTUNITCOST + " numeric, " + ITEM_COST1 + " numeric, "
            + ITEM_COST2 + " numeric, " + ITEM_STOCKINGUOM + " text, "
            + ITEM_STOCKINGCONVFACTOR + " numeric, " + ITEM_LOCUOM + " text, "
            + ITEM_LOCCONVFACTOR + " numeric, " + ITEM_CARTONDISC + " text,"
            + ITEM_UNITWGT + " numeric," + ITEM_COMPANYCODE + " text);";

    // Item price list table create query string
    private static final String ITEM_PRICE_LIST_CREATE_QUERY = "create table "
            + ITEM_PRICE_LIST_MASTER_TABLE + "(" + ITEM_PRICE_LIST + " text, "
            + ITEM_PRICE_LIST_COMPANYCODE + " text);";

    // Price table create query string
    private static final String PRICE_CREATE_QUERY = "create table "
            + PRICE_TABLE + "(" + PRICE_NUMBER + " text, " + PRICE_DISCTYPE
            + " integer, " + PRICE_PRICE1 + " numeric, " + PRICE_PRICE2
            + " numeric, " + PRICE_PRICE3 + " numeric, " + PRICE_PRICE4
            + " numeric, " + PRICE_PRICE5 + " numeric, " + PRICE_PRICE6
            + " numeric, " + PRICE_QTYLEVEL1 + " numeric, " + PRICE_QTYLEVEL2
            + " numeric, " + PRICE_QTYLEVEL3 + " numeric, " + PRICE_QTYLEVEL4
            + " numeric, " + PRICE_QTYLEVEL5 + " numeric, " + PRICE_MARKUPCOST
            + " numeric, " + PRICE_PRICELISTCODE + " text, " + PRICE_LOCATION
            + " text, " + PRICE_PRICINGUOM + " text, "
            + PRICE_PRICINGCONVFACTOR + " numeric, " + PRICE_MARKUPUOM
            + " text, " + PRICE_MARKUPCONVFACTOR + " numeric, "
            + PRICE_PRICETYPE + " numeric, " + PRICE_SALESPRICE + " text, "
            + PRICE_SALESUOM + " text, " + PRICE_SALESCONVFACTOR + " text, "
            + PRICE_SALESSTARTDATE + " text, " + PRICE_SALESENDDATE + " text, "
            + PRICE_COMPANYCODE + " text);";

    // Group code table create query string
    private static final String CUSTOMER_GROUPCODE_CREATE_QUERY = "create table "
            + CUSTOMER_GROUPCODE_TABLE
            + "("
            + CUSGCODE_GROUPCODE
            + " text, "
            + CUSGCODE_DESCRIPTION
            + " text, "
            + CUSGCODE_TERMS
            + " text, "
            + CUSGCODE_COMPANYCODE + " text);";

    // Shipto table create query string
    private static final String SHIPTO_CREATE_QUERY = "create table "
            + SHIPTO_TABLE + "(" + SHIPTO_CUSTOMERNUMBER + " text, "
            + SHIPTO_PRIMARYSHIPTO + " integer, " + SHIPTO_LOCCODE + " text, "
            + SHIPTO_DESCRIPTION + " text, " + SHIPTO_SHIPVIA + " text, "
            + SHIPTO_VIADESCRIPTION + " text, " + SHIPTO_ADDRESS + " text, "
            + SHIPTO_CITY + " text, " + SHIPTO_STATE + " text, " + SHIPTO_ZIP
            + " text, " + SHIPTO_COUNTRY + " text, " + SHIPTO_CONTACT
            + " text, " + SHIPTO_TAXGROUP + " text, " + SHIPTO_TAXAUTHORITY1
            + " text, " + SHIPTO_TAXABLE1 + " integer, " + SHIPTO_TAXSTTS1
            + " text, " + SHIPTO_TAXAUTHORITY2 + " text, " + SHIPTO_TAXABLE2
            + " integer, " + SHIPTO_TAXSTTS2 + " text, " + SHIPTO_TAXAUTHORITY3
            + " text, " + SHIPTO_TAXABLE3 + " integer, " + SHIPTO_TAXSTTS3
            + " text, " + SHIPTO_TAXAUTHORITY4 + " text, " + SHIPTO_TAXABLE4
            + " integer, " + SHIPTO_TAXSTTS4 + " text, " + SHIPTO_TAXAUTHORITY5
            + " text, " + SHIPTO_TAXABLE5 + " integer, " + SHIPTO_TAXSTTS5
            + " text, " + SHIPTO_COMPANYCODE + " text);";

    // Group code table create query string
    private static final String SHIPVIA_CREATE_QUERY = "create table "
            + SHIPVIA_TABLE + "(" + SHIPVIA_CODE + " text, " + SHIPVIA_NAME
            + " text, " + SHIPVIA_COMPANYCODE + " text);";

    // Conversion factor table create query string
    private static final String CONVFACTOR_CREATE_QUERY = "create table "
            + CONVFACTOR_TABLE + "(" + CONVFACTOR_NUMBER + " text, "
            + CONVFACTOR_UOMCONVFACTOR + " numeric, " + CONVFACTOR_UOMNAME
            + " text, " + CONVFACTOR_COMPANYCODE + " text);";

    // Transaction table create query string
    private static final String TRANS_CREATE_QUERY = "create table "
            + TRANS_TABLE + "(" + TRANS_TRANSTYPE + " text, "
            + TRANS_REFERENCENUMBER + " text, " + TRANS_INVOICENUMBER
            + " text, " + TRANS_ORDERNUMBER + " text, " + TRANS_TRANSDAY
            + " integer, " + TRANS_TRANSMONTH + " integer, " + TRANS_TRANSYEAR
            + " integer, " + TRANS_EXPSHIPDAY + " integer, "
            + TRANS_EXPSHIPMONTH + " integer, " + TRANS_EXPSHIPYEAR
            + " integer, " + TRANS_CUSTOMERNUMBER + " text, "
            + TRANS_SALESPERSON + " text, " + TRANS_ITEMNUMBER + " text, "
            + TRANS_LOCID + " text, " + TRANS_TERMS + " text, "
            + TRANS_CURRENCY + " text, " + TRANS_PRICELISTCODE + " text, "
            + TRANS_UOM + " text, " + TRANS_QTY + " float, " + TRANS_PRICE
            + " numeric, " + TRANS_DISCPRICE + " numeric, " + TRANS_NETPRICE
            + " numeric, " + TRANS_EXTPRICE + " numeric, " + TRANS_TAX
            + " numeric, " + TRANS_SHIPTOCODE + " text, " + TRANS_SHIPVIACODE
            + " text, " + TRANS_STATUS + " integer, " + TRANS_LINEITEM
            + " integer, " + TRANS_DISCVALUE + " numeric, " + TRANS_DISCTYPE
            + " text, " + TRANS_ORDSHIPDAY + " integer, " + TRANS_ORDSHIPMONTY
            + " integer, " + TRANS_ORDSHIPYEAR + " integer, " + TRANS_EDITABLE
            + " integer, " + TRANS_MANITEMNO + " text, " + TRANS_PRETAX
            + " numeric, " + TRANS_REFNO + " text, " + TRANS_ORDERDISCOUNT
            + " numeric," + TRANS_TAXAMOUNT + " numeric, " + TRANS_TOTALCARTON
            + " numeric," + TRANS_EDITED_CUSTOMERNAME + " text, "
            + TRANS_ITEMUNITWEIGHT + " numeric, " + TRANS_COMPANYCODE
            + " text," + TRANS_LATITUDE + " real, " + TRANS_LONGITUDE
            + " real);";

    // History table create query string
    private static final String HISTORY_TABLE_QUERY = "create table "
            + HISTORY_TABLE + "(" + HISTORY_TRANS_TRANSTYPE + " text, "
            + HISTORY_TRANS_REFERENCENUMBER + " text primary key, " + HISTORY_TRANS_INVOICENUMBER
            + " text, " + HISTORY_TRANS_ORDERNUMBER + " text, " + HISTORY_TRANS_TRANSDAY
            + " integer, " + HISTORY_TRANS_TRANSMONTH + " integer, " + HISTORY_TRANS_TRANSYEAR
            + " integer, " + HISTORY_TRANS_EXPSHIPDAY + " integer, "
            + HISTORY_TRANS_EXPSHIPMONTH + " integer, " + HISTORY_TRANS_EXPSHIPYEAR
            + " integer, " + HISTORY_TRANS_CUSTOMERNUMBER + " text, "
            + HISTORY_TRANS_SALESPERSON + " text, " + HISTORY_TRANS_ITEMNUMBER + " text, "
            + HISTORY_TRANS_LOCID + " text, " + HISTORY_TRANS_TERMS + " text, "
            + HISTORY_TRANS_CURRENCY + " text, " + HISTORY_TRANS_PRICELISTCODE + " text, "
            + HISTORY_TRANS_UOM + " text, " + HISTORY_TRANS_QTY + " float, " + HISTORY_TRANS_PRICE
            + " numeric, " + HISTORY_TRANS_DISCPRICE + " numeric, " + HISTORY_TRANS_NETPRICE
            + " numeric, " + HISTORY_TRANS_EXTPRICE + " numeric, " + HISTORY_TRANS_TAX
            + " numeric, " + HISTORY_TRANS_SHIPTOCODE + " text, " + HISTORY_TRANS_SHIPVIACODE
            + " text, " + HISTORY_TRANS_STATUS + " integer, " + HISTORY_TRANS_LINEITEM
            + " integer, " + HISTORY_TRANS_DISCVALUE + " numeric, " + HISTORY_TRANS_DISCTYPE
            + " text, " + HISTORY_TRANS_ORDSHIPDAY + " integer, " + HISTORY_TRANS_ORDSHIPMONTY
            + " integer, " + HISTORY_TRANS_ORDSHIPYEAR + " integer, " + HISTORY_TRANS_EDITABLE
            + " integer, " + HISTORY_TRANS_MANITEMNO + " text, " + HISTORY_TRANS_PRETAX
            + " numeric, " + HISTORY_TRANS_REFNO + " text, " + HISTORY_TRANS_ORDERDISCOUNT
            + " numeric," + HISTORY_TRANS_TAXAMOUNT + " numeric, " + HISTORY_TRANS_TOTALCARTON
            + " numeric," + HISTORY_TRANS_EDITED_CUSTOMERNAME + " text, "
            + HISTORY_TRANS_ITEMUNITWEIGHT + " numeric, " + HISTORY_TRANS_COMPANYCODE
            + " text," + HISTORY_TRANS_LATITUDE + " real, " + HISTORY_TRANS_LONGITUDE
            + " real);";

    public static final String  MYNAME = "myname";
    public static final String  MYDB = "mydb1";
    public static final String  MYMOBILE = "mymobile";
    private static final String new_Db = "create table "+ MYDB + "(" + MYNAME + " text, "+ MYMOBILE +" integer);";

    // mspda table create query string modified by TISN for Najeeb (12-Oct-2013)
    private static final String MSPDB_CREATE_QUERY = "create table "
            + MSPDB_TABLE + "(" + MSPDB_COMPANYNUMBER + " text, "
            + MSPDB_SALESPERSON + " text, " + MSPDB_MAPNO + " integer, "
            + MSPDB_CUSTOMERNUMBER + " integer, " + MSPDB_ORDERNUMBER
            + " integer, " + MSPDB_QUOTENUMBER + " integer," + MSPDB_LASTINVNO
            + " integer, " + MSPDB_RECEIPTNUMBER + " integer);";

    private static final String PREPAYMENT_CREATE_QUERY = "create table "
            + PREPAYMENT_TABLE + "(" + PREPAYMENT_TRANSTYPE + " text, "
            + PREPAYMENT_REFERENCENUMBER + " text, " + PREPAYMENT_INVOICENUMBER
            + " text, " + PREPAYMENT_CUSTOMERNUMBER + " text, "
            + PREPAYMENT_CURRENCY + " text, " + PREPAYMENT_ORDERTOTAL
            + " numeric, " + PREPAYMENT_AMTDUE + " numeric, "
            + PREPAYMENT_RECEIPTTYPE + " text, " + PREPAYMENT_CHECKRECEIPTNO
            + " text, " + PREPAYMENT_RECEIPTDAY + " integer, "
            + PREPAYMENT_RECEIPTMONTH + " integer, " + PREPAYMENT_RECEIPTYEAR
            + " integer, " + PREPAYMENT_RECEIPTAMOUNT + " numeric, "
            + PREPAYMENT_ACCPACORDNUMBER + " text, " + PREPAYMENT_FLAG
            + " text, " + PREPAYMENT_REFNO + " text, " + PREPAYMENT_COMPANYCODE
            + " text, " + PREPAYMENT_RECEIPTTYPE2 + " text, "
            + PREPAYMENT_RECEIPTAMOUNT1 + " numeric);";

    // company table create query string
    private static final String COMPANY_CREATE_QUERY = "create table "
            + COMPANY_TABLE + "(" + COMPANY_NUMBER + " text, " + COMPANY_NAME
            + " text, " + COMPANY_ADDRESS + " text, " + COMPANY_CITY
            + " text, " + COMPANY_STATE + " text, " + COMPANY_ZIP + " text, "
            + COMPANY_COUNTRY + " text, " + COMPANY_PHONE + " text, "
            + COMPANY_CONTACT + " text, " + COMPANY_ISSTOCKINGUOM + " integer,"
            + COMPANY_TINNO + " text);";

    // Contract price table create query string
    private static final String CONT_PRICE_CREATE_QUERY = "create table "
            + CONT_PRICE_TABLE + "(" + CONT_PRICE_NUMBER + " text, "
            + CONT_PRICE_PRICEBY + " integer, " + CONT_PRICE_CATEGORY
            + " text, " + CONT_PRICE_ITEMNUMBER + " text, "
            + CONT_PRICE_PRICELISTCODE + " text, " + CONT_PRICE_PRICETYPE
            + " integer, " + CONT_PRICE_CUSTOMERTYPE + " integer, "
            + CONT_PRICE_DISCPERCENT + " numeric, " + CONT_PRICE_DISCAMOUNT
            + " numeric, " + CONT_PRICE_COSTMETHOD + " integer, "
            + CONT_PRICE_PLUSAMOUNT + " numeric, " + CONT_PRICE_PLUSPERCENT
            + " numeric, " + CONT_PRICE_FIXEDPRICE + " numeric, "
            + CONT_PRICE_CALCUNITPRICE + " numeric, " + CONT_PRICE_USELOWEST
            + " numeric, " + CONT_PRICE_COMPANYCODE + " text);";

    // Item price tax table create query string
    private static final String ITMPRCTAX_CREATE_QUERY = "create table "
            + ITMPRCTAX_TABLE + "(" + ITMPRCTAX_NUMBER + " text, "
            + ITMPRCTAX_PRICELIST + " text, " + ITMPRCTAX_AUTHORITY + " text, "
            + ITMPRCTAX_SALESTAXCLASS + " integer, " + ITMPRCTAX_COMPANYCODE
            + " text);";

    // Item tax table create query string
    private static final String ITEMTAX_CREATE_QUERY = "create table "
            + ITEMTAX_TABLE + "(" + ITEMTAX_ITEM_NUMBER + " text, "
            + ITEMTAX_AUTHORITY + " text, " + ITEMTAX_SALESTAXCLASS
            + " integer, " + ITEMTAX_COMPANYCODE + " text);";

    // Location master table create query string
    private static final String LOCMAST_CREATE_QUERY = "create table "
            + LOCMAST_TABLE + "(" + LOCMAST_LOCATION + " text, "
            + LOCMAST_COMPANYCODE + " text);";

    // Manager table create query string
    private static final String MANAGER_CREATE_QUERY = "create table "
            + MANAGER_TABLE + "(" + MANAGER_COMPANYID + " text, "
            + MANAGER_USERID + " text, " + MANAGER_USERPASS + " text, "
            + MANAGER_ACTIVEKEY + " text, " + MANAGER_USERNAME + " text, "
            + MANAGER_LOCID + " text, " + MANAGER_EMAIL + " text, "
            + MANAGER_CURRENCY + " text, " + MANAGER_PRICELISTCODE + " text, "
            + MANAGER_COMPANYNUMBER + " text, " + MANAGER_COMPANYNAME
            + " text, " + MANAGER_COMPANYMAILID + " text, "
            + MANAGER_UNITPRICEEDITABLE + " integer, " + MANAGER_ACCPACVERSION
            + " text, " + MANAGER_ADDCUSTOMERSTATE + " integer, "
            + MANAGER_CUSTOMERBASEDPL + " integer, " + MANAGER_ADMINPASS
            + " text);";


    /*// Manager table create query string
    private static final String MANAGER_CREATE_QUERY = "create table "
            + MANAGER_TABLE + "(" + MANAGER_COMPANYID + " text, "
            + MANAGER_USERID + " text, " + MANAGER_USERPASS + " text, "
            + MANAGER_ACTIVEKEY + " text, " + MANAGER_USERNAME + " text, "
            + MANAGER_LOCID + " text, " + MANAGER_EMAIL + " text, "
            + MANAGER_CURRENCY + " text, " + MANAGER_PRICELISTCODE + " text, "
            + MANAGER_COMPANYNUMBER + " text, " + MANAGER_COMPANYNAME
            + " text, " + MANAGER_COMPANYMAILID + " text, "
            + MANAGER_UNITPRICEEDITABLE + " integer, " + MANAGER_ACCPACVERSION
            + " text, " + MANAGER_ADDCUSTOMERSTATE + " integer, "
            + MANAGER_CUSTOMERBASEDPL + " integer, " + MANAGER_ADMINPASS
            + " text," + MANAGER_NEG_INV + " integer," + MANAGER_CREATE_ORDER+ " integer ,"
            + MANAGER_CREDIT_NOTE + " integer,"+ MANAGER_SUMMARY_REPORT + " integer,"
            + MANAGER_INVENTORY_REPORT +" integer,"+ MANAGER_RECEIPT_SUMMARY_REPORT
            + "integer,"+ MANAGER_SALES_SUMMARY_REPORT +" integer, "
            + MANAGER_EDIT_DELETE +"integer," + MANAGER_DEF_PAYMENTTYPE +" text);";*/


    /* Created by TISN */
    // Receipt table create query string
    private static final String RECEIPT_CREATE_QUERY = "create table "
            + RECEIPT_TABLE + "(" + RECEIPT_SNO
            + " integer primary key autoincrement, " + RECEIPT_CUSTOMERNUMBER
            + " text, " + RECEIPT_CUSTOMERNAME + " text," + RECEIPT_DOCNUMBER
            + " text," + RECEIPT_PENDINGBAL + " numeric," + RECEIPT_CURRENCY
            + " text," + RECEIPT_APPLY1 + " text," + RECEIPT_APPLIEDAMOUNT
            + " numeric," + RECEIPT_RECEIPTTYPE + " text," + RECEIPT_AMOUNT
            + " numeric," + RECEIPT_RECEIPTNUMBER + " text,"
            + RECEIPT_RECEIPTDAY + " integer," + RECEIPT_RECEIPTMONTH
            + " integer," + RECEIPT_RECEIPTYEAR + " integer," + RECEIPT_STATUS
            + " integer," + RECEIPT_RECEIPTUNAPPLIED + " numeric,"
            + RECEIPT_CUSTOMERAMOUNT + " numeric," + RECEIPT_REFNO + " text,"
            + RECEIPT_COMPANYCODE + " text," + RECEIPT_RECEIPTTYPE2 + " text, " + RECEIPT_AMOUNT2 + " numeric);";

    // Receipt type table create query string
    private static final String RECEIPTTYPE_CREATE_QUERY = "create table "
            + RECEIPTTYPE_TABLE + "(" + RECEIPTTYPE_RECEIPTTYPE + " text, "
            + RECEIPTTYPE_RECEIPTNAME + " text, " + RECEIPTTYPE_COMPANYCODE
            + " text);";

    // Manufacture item table create query string
    private static final String MANUF_CREATE_QUERY = "create table "
            + MANUF_TABLE + "(" + MANUF_MANITEMNO + " text, " + MANUF_ITEMNO
            + " text," + MANUF_UOM + " text, " + MANUF_COMPNAYCODE + " text);";

    // Manager table create query string
    private static final String MULTIUOM_CREATE_QUERY = "create table "
            + MULTIUOM_TABLE + "(" + MULTIUOM_NUMBER + " text, "
            + MULTIUOM_QTYUOM + " text, " + MULTIUOM_DISCTYPE + " integer, "
            + MULTIUOM_PRICE1 + " numeric, " + MULTIUOM_PRICE2 + " numeric, "
            + MULTIUOM_PRICE3 + " numeric, " + MULTIUOM_PRICE4 + " numeric, "
            + MULTIUOM_PRICE5 + " numeric, " + MULTIUOM_PRICE6 + " numeric, "
            + MULTIUOM_QTYLEVEL1 + " numeric, " + MULTIUOM_QTYLEVEL2
            + " numeric, " + MULTIUOM_QTYLEVEL3 + " numeric, "
            + MULTIUOM_QTYLEVEL4 + " numeric, " + MULTIUOM_QTYLEVEL5
            + " numeric, " + MULTIUOM_MARKUPCOST + " numeric, "
            + MULTIUOM_PRICELISTCODE + " text, " + MULTIUOM_LOCATION
            + " text, " + MULTIUOM_PRICINGUOM + " text, "
            + MULTIUOM_PRICINGCONVFACTOR + " numeric, " + MULTIUOM_MARKUPUOM
            + " text, " + MULTIUOM_MARKUPCONVFACTOR + " numeric, "
            + MULTIUOM_COMPANYCODE + " text);";

    // Route create query string
    private static final String ROUTE_CREATE_QUERY = "create table "
            + ROUTE_TABLE + "(" + ROUTE_CUSTOMERNUMBER + " text, "
            + ROUTE_CUSTOMERNAME + " text, " + ROUTE_DAYOFVISIT + " integer, "
            + ROUTE_MONTHOFVISIT + " integer, " + ROUTE_YEAROFVISIT
            + " integer, " + ROUTE_VISITSTAT + " integer, " + ROUTE_COMPANYCODE
            + " text);";

    // Manager table create query string
    private static final String PAYMENT_CREATE_QUERY = "create table "
            + PAYMENT_TABLE + "(" + PAYMENT_APPLY1 + " text, "
            + PAYMENT_CUSNUMBER + " text, " + PAYMENT_CUSNAME + " text, "
            + PAYMENT_DOCUMENTNUMBER + " text, " + PAYMENT_PENDINGBALANCE
            + " float, " + PAYMENT_APPLIEDAMOUNT + " text, "
            + PAYMENT_NETAMOUNT + " float, " + PAYMENT_STATUS + " integer, "
            + PAYMENT_COMPANYCODE + " text);";

    // Taxclass table create query string
    private static final String TAXCLASS_CREATE_QUERY = "create table "
            + TAXCLASS_TABLE + "(" + TAXCLASS_TAXAUTHORITY + " text, "
            + TAXCLASS_TAXSTTS + " text, " + TAXCLASS_DESCRIPTION + " text, "
            + TAXCLASS_COMPANYCODE + " text);";

    // Taxgroup table create query string
    private static final String TAXGROUP_CREATE_QUERY = "create table "
            + TAXGROUP_TABLE + "(" + TAXGROUP_TAXGROUP + " text, "
            + TAXGROUP_TAXAUTHORITY1 + " text, " + TAXGROUP_TAXSTTS1
            + " text, " + TAXGROUP_TAXABLE1 + " integer, "
            + TAXGROUP_TAXAUTHORITY2 + " text, " + TAXGROUP_TAXSTTS2
            + " text, " + TAXGROUP_TAXABLE2 + " integer, "
            + TAXGROUP_TAXAUTHORITY3 + " text, " + TAXGROUP_TAXSTTS3
            + " text, " + TAXGROUP_TAXABLE3 + " integer, "
            + TAXGROUP_TAXAUTHORITY4 + " text, " + TAXGROUP_TAXSTTS4
            + " text, " + TAXGROUP_TAXABLE4 + " integer, "
            + TAXGROUP_TAXAUTHORITY5 + " text, " + TAXGROUP_TAXSTTS5
            + " text, " + TAXGROUP_TAXABLE5 + " integer, "
            + TAXGROUP_COMPANYCODE + " text);";

    // Taxrate table create query string
    private static final String TAXRATE_CREATE_QUERY = "create table "
            + TAXRATE_TABLE + "(" + TAXRATE_AUTHORITY + " text, "
            + TAXRATE_BUYERCLASS + " integer, " + TAXRATE_ITEMRATE1
            + " numeric, " + TAXRATE_ITEMRATE2 + " numeric, "
            + TAXRATE_ITEMRATE3 + " numeric, " + TAXRATE_ITEMRATE4
            + " numeric, " + TAXRATE_ITEMRATE5 + " numeric, "
            + TAXRATE_ITEMRATE6 + " numeric, " + TAXRATE_ITEMRATE7
            + " numeric, " + TAXRATE_ITEMRATE8 + " numeric, "
            + TAXRATE_ITEMRATE9 + " numeric, " + TAXRATE_ITEMRATE10
            + " numeric, " + TAXRATE_COMPANYCODE + " text);";

    // Terms table create query string
    private static final String TERMS_CREATE_QUERY = "create table "
            + TERMS_TABLE + "(" + TERMS_CODETERM + " text, "
            + TERMS_COUNTPAYMENT + " integer, " + TERMS_PERCENTDUE
            + " numeric, " + TERMS_PERCENTDISC + " numeric, "
            + TERMS_NUMBERDAYS + " integer, " + TERMS_DUEDAYS + " integer, "
            + TERMS_DISCWITHTAX + " integer, " + TERMS_COMPANYCODE + " text);";

    // Taxrate table create query string
    private static final String UPC_CREATE_QUERY = "create table " + UPC_TABLE
            + "(" + UPC_ITEMNUMBER + " text, " + UPC_UPCNUMBER + " text, "
            + UPC_ITEMDESC + " text," + UPC_COMPANYCODE + " text);";

    // Kilometer table create query string
    private static final String KILOMETER_CREATE_QUERY = "create table "
            + KILOMETER_TABLE + "(" + KILOMETER_MVSP + " text, "
            + KILOMETER_DOT + " text, " + KILOMETER_SKM + " text, "
            + KILOMETER_EKM + " text, " + KILOMETER_TOTALKM + " text);";

    // Kilometer table create query string
    private static final String VAN_CREATE_QUERY = "create table " + VAN_TABLE
            + "(" + VAN_DOT + " long, " + VAN_VNO + " text);";

    // Setting table create query string
    private static final String SETTING_CREATE_QUERY = "create table "
            + SETTING_TABLE + "(" + SETTING_NUMCOPIES_PRINT + " integer, "
            + SETTING_ITEM_AUTOSAVE + " integer, " + SETTING_NUMOF_PIECEDISC
            + " integer, " + SETTING_DISCPRICE + " numeric, "
            + SETTING_DATE_FORMAT + " text," + SETTING_DECIMAL_FORMAT
            + " text," + SETTING_SYNC_SERVICE + " text, "
            + SETTING_MSP_SERVERPATH + " text, " + SETTING_PRINTER_NAME
            + " text, " + SETTING_PRINTER_MODEL + " text, " + SETTING_SHOW_SHIP
            + " integer, " + SETTING_SHOW_PREPAY + " integer, "
            + SETTING_AUTOREPORT_GEN + " integer, " + SETTING_SPCODE + " text,"
            + SETTING_CUSTOMER_NAME_EDIT + " integer," + SETTING_NONSTOCK_ITEM
            + " integer);";

    // signature table create query string
    private static final String SIGNATURE_CREATE_QUERY = "create table "
            + SIGNATURE_TABLE + "(" + SIGNATURE_ID
            + " INTEGER PRIMARY KEY autoincrement," + SIGNATURE_COMPANY_NAME
            + " TEXT," + SIGNATURE_REF_NUMBER + " TEXT," + SIGNATURE_SIGN
            + " BLOB," + SIGNATURE_STATUS + " INTEGER," + SIGNATURE_DATE
            + " TEXT," + SIGNATURE_COMPANYCODE + " TEXT);";

    // Temporary item table create query string
    private static final String TEMPITEM_CREATE_QUERY = "create table "
            + TEMPITEM_TABLE + "(" + TEMPITEM_NUMBER + " text, " + TEMPITEM_QTY
            + " text, " + TEMPITEM_EXTPRICE + " text, " + TEMPITEM_LOCATION
            + " text, " + TEMPITEM_UOM + " text, " + TEMPITEM_DATE + " text, "
            + TEMPITEM_ENTRYTYPE + " text, " + TEMPITEM_DISCOUNT + " text, "
            + TEMPITEM_DISCTYPE + " text);";

    // customized email setting table create query
    private static final String EMAIL_SETTING_CREATE_QUERY = "create table "
            + EMAIL_SETTING_TABLE + "(" + EMAILSETTING_SALESPERSON_EMAIL
            + " text, " + EMAILSETTING_SALESPERSON_PWD + " text, "
            + EMAILSETTING_COMPANY_EMAIL + " text, "
            + EMAILSETTING_SALESPERSON_HOSTNAME + " text, "
            + EMAILSETTING_SALESPERSON_PORTNO + " integer, "
            + EMAILSETTING_COMPANY_PORTNO + " text);";

    private static final String CUS_ALT_NO = "ALTER TABLE "+CUSTOMER_TABLE+" ADD COLUMN "+CUSTOMER_ALT_NO+"";
    private  static final String Trans_Alt_SalesCommissiom = "ALTER TABLE "+TRANS_TABLE+" ADD COLUMN "+SALES_COMMISSION+"";
    // constructor to create db
    public MspDBHelper(Context context) {

        File root_path = Environment.getExternalStorageDirectory();

        DATABASE_FILE_PATH = new File(root_path.getAbsoluteFile() + "/"
                + "Android/AMSP");

        fpath = new File(DATABASE_FILE_PATH, DATABASE_NAME);

        if (!fpath.exists()) {
            // db.setVersion(1);
            db = SQLiteDatabase.openOrCreateDatabase(DATABASE_FILE_PATH
                    + File.separator + DATABASE_NAME, null);
            try {

                db.beginTransaction();

                db.execSQL(CUSTOMER_CREATE_QUERY);
                db.execSQL(ITEM_CREATE_QUERY);
                db.execSQL(ITEM_PRICE_LIST_CREATE_QUERY);
                db.execSQL(PRICE_CREATE_QUERY);
                db.execSQL(CUSTOMER_GROUPCODE_CREATE_QUERY);
                db.execSQL(SHIPTO_CREATE_QUERY);
                db.execSQL(SHIPVIA_CREATE_QUERY);
                db.execSQL(CONVFACTOR_CREATE_QUERY);
                db.execSQL(TRANS_CREATE_QUERY);

                db.execSQL(MSPDB_CREATE_QUERY);
                db.execSQL(PREPAYMENT_CREATE_QUERY);
                db.execSQL(COMPANY_CREATE_QUERY);
                db.execSQL(CONT_PRICE_CREATE_QUERY);
                db.execSQL(ITMPRCTAX_CREATE_QUERY);
                db.execSQL(ITEMTAX_CREATE_QUERY);
                db.execSQL(LOCMAST_CREATE_QUERY);
                db.execSQL(MANAGER_CREATE_QUERY);
                db.execSQL(RECEIPT_CREATE_QUERY); /* Created by TISN */
                db.execSQL(RECEIPTTYPE_CREATE_QUERY);
                db.execSQL(MANUF_CREATE_QUERY);
                db.execSQL(PAYMENT_CREATE_QUERY);
                db.execSQL(MULTIUOM_CREATE_QUERY);
                db.execSQL(ROUTE_CREATE_QUERY);
                db.execSQL(TAXCLASS_CREATE_QUERY);
                db.execSQL(TAXGROUP_CREATE_QUERY);
                db.execSQL(TAXRATE_CREATE_QUERY);
                db.execSQL(TERMS_CREATE_QUERY);
                db.execSQL(UPC_CREATE_QUERY);
                db.execSQL(KILOMETER_CREATE_QUERY);
                db.execSQL(VAN_CREATE_QUERY);
                db.execSQL(new_Db);
                db.execSQL(HISTORY_TABLE_QUERY);
//                db.execSQL(Trans_Alt_SalesCommissiom);



                db.execSQL(SIGNATURE_CREATE_QUERY);
                db.execSQL(SETTING_CREATE_QUERY);
                db.execSQL(TEMPITEM_CREATE_QUERY); // temporary table
                db.execSQL(EMAIL_SETTING_CREATE_QUERY);

                db.setTransactionSuccessful();

                Log.i("Success", "All Table Created successfully...");
            } catch (Exception e) {
                db.endTransaction();
                errCode = "Error 501";
                msg = "Table Creation failed.";
                errMsg = errCode + " : " + msg;
                LogfileCreator.appendLog(errMsg);
            } finally {
                db.endTransaction();
            }

            db.close();
        }
    }


    // to get readable database
    public SQLiteDatabase getReadableDatabase() {

        if (fpath.exists()) {

            db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

                            + File.separator + DATABASE_NAME, null,

                    SQLiteDatabase.OPEN_READONLY);
        }

        return db;
    }


    // to get writable database
    public SQLiteDatabase getWritableDatabase() {
        if (fpath.exists()) {

            db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

                            + File.separator + DATABASE_NAME, null,

                    SQLiteDatabase.OPEN_READWRITE);
        }

        return db;
    }

    /* to insert customer data to db */
    public void addCustomer(HhCustomer01 customer) {


        try {

            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.CUSTOMER_NUMBER, customer.getHhCustomer_number());
            cv.put(MspDBHelper.CUSTOMER_NAME, customer.getHhCustomer_name());
            cv.put(MspDBHelper.CUSTOMER_ADDRESS,
                    customer.getHhCustomer_address());
            cv.put(MspDBHelper.CUSTOMER_CITY, customer.getHhCustomer_city());
            cv.put(MspDBHelper.CUSTOMER_STATE, customer.getHhCustomer_state());
            cv.put(MspDBHelper.CUSTOMER_ZIP, customer.getHhCustomer_zip());
            cv.put(MspDBHelper.CUSTOMER_COUNTRY,
                    customer.getHhCustomer_country());
            cv.put(MspDBHelper.CUSTOMER_PHONE1, customer.getHhCustomer_phone1());
            cv.put(MspDBHelper.CUSTOMER_EMAIL1, customer.getHhCustomer_email1());
            cv.put(MspDBHelper.CUSTOMER_WEBSITE,
                    customer.getHhCustomer_website());
            cv.put(MspDBHelper.CUSTOMER_CONTACT,
                    customer.getHhCustomer_contact());
            cv.put(MspDBHelper.CUSTOMER_EMAIL2, customer.getHhCustomer_email2());
            cv.put(MspDBHelper.CUSTOMER_GROUPCODE,
                    customer.getHhCustomer_groupcode());
            cv.put(MspDBHelper.CUSTOMER_TYPE, customer.getHhCustomer_type());
            cv.put(MspDBHelper.CUSTOMER_TERMS, customer.getHhCustomer_terms());
            cv.put(MspDBHelper.CUSTOMER_LASTBALANCE,
                    customer.getHhCustomer_lastbalance());
            cv.put(MspDBHelper.CUSTOMER_CREDITLIMIT,
                    customer.getHhCustomer_creditlimit());
            cv.put(MspDBHelper.CUSTOMER_LASTSALE,
                    customer.getHhCustomer_lastsale());
            cv.put(MspDBHelper.CUSTOMER_TAXGROUP,
                    customer.getHhCustomer_taxgroup());
            cv.put(MspDBHelper.CUSTOMER_COMMENT,
                    customer.getHhCustomer_comment());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY1,
                    customer.getHhCustomer_taxauthority1());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE1,
                    customer.getHhCustomer_taxable1());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS1,
                    customer.getHhCustomer_taxstts1());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY2,
                    customer.getHhCustomer_taxauthority2());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE2,
                    customer.getHhCustomer_taxable2());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS2,
                    customer.getHhCustomer_taxstts2());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY3,
                    customer.getHhCustomer_taxauthority3());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE3,
                    customer.getHhCustomer_taxable3());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS3,
                    customer.getHhCustomer_taxstts3());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY4,
                    customer.getHhCustomer_taxauthority4());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE4,
                    customer.getHhCustomer_taxable4());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS4,
                    customer.getHhCustomer_taxstts4());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY5,
                    customer.getHhCustomer_taxauthority5());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE5,
                    customer.getHhCustomer_taxable5());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS5,
                    customer.getHhCustomer_taxstts5());
            cv.put(MspDBHelper.CUSTOMER_STATUS, customer.getHhCustomer_status());
            cv.put(MspDBHelper.CUSTOMER_PRICELISTCODE,
                    customer.getHhCustomer_pricelistcode());
            cv.put(MspDBHelper.CUSTOMER_IDTAXREGI1,
                    customer.getHhCustomer_idtaxregi1());
            cv.put(MspDBHelper.CUSTOMER_CREDITSTATUS,
                    customer.getHhCustomer_creditstatus());
            cv.put(MspDBHelper.CUSTOMER_SHIPVIA,
                    customer.getHhCustomer_shipvia());
            cv.put(MspDBHelper.CUSTOMER_COMPANYCODE,
                    customer.getHhCustomer_companycode());
           /* cv.put(MspDBHelper.CUSTOMER_ALT_NO,
                           customer.getHhCustomer_customer_alt_no());*/
//                db.execSQL(CUS_ALT_NO );



            db.insert(MspDBHelper.CUSTOMER_TABLE, null, cv);



            Log.i("Customer Insert", "Customer Record Inserted ");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting customer.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            errCode = "Error 528";
            msg = "New Customer Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // to transaction details
    public void addTransactionDetail(HhTran01 transaction) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TRANS_TRANSTYPE,
                    transaction.getHhTran_transType());
            cv.put(MspDBHelper.TRANS_REFERENCENUMBER,
                    transaction.getHhTran_referenceNumber());
            cv.put(MspDBHelper.TRANS_INVOICENUMBER,
                    transaction.getHhTran_invoiceNumber());
            cv.put(MspDBHelper.TRANS_ORDERNUMBER,
                    transaction.getHhTran_orderNumber());
            cv.put(MspDBHelper.TRANS_TRANSDAY, transaction.getHhTran_transDay());
            cv.put(MspDBHelper.TRANS_TRANSMONTH,
                    transaction.getHhTran_transMonth());
            cv.put(MspDBHelper.TRANS_TRANSYEAR,
                    transaction.getHhTran_transYear());
            cv.put(MspDBHelper.TRANS_EXPSHIPDAY,
                    transaction.getHhTran_expShipDay());
            cv.put(MspDBHelper.TRANS_EXPSHIPMONTH,
                    transaction.getHhTran_expShipMonth());
            cv.put(MspDBHelper.TRANS_EXPSHIPYEAR,
                    transaction.getHhTran_expShipYear());
            cv.put(MspDBHelper.TRANS_CUSTOMERNUMBER,
                    transaction.getHhTran_customerNumber());
            cv.put(MspDBHelper.TRANS_SALESPERSON,
                    transaction.getHhTran_salesPerson());
            cv.put(MspDBHelper.TRANS_ITEMNUMBER,
                    transaction.getHhTran_itemNumber());
            cv.put(MspDBHelper.TRANS_LOCID, transaction.getHhTran_locId());
            cv.put(MspDBHelper.TRANS_TERMS, transaction.getHhTran_terms());
            cv.put(MspDBHelper.TRANS_CURRENCY, transaction.getHhTran_currency());
            cv.put(MspDBHelper.TRANS_PRICELISTCODE,
                    transaction.getHhTran_priceListCode());
            cv.put(MspDBHelper.TRANS_UOM, transaction.getHhTran_uom());
            cv.put(MspDBHelper.TRANS_QTY, transaction.getHhTran_qty());
            cv.put(MspDBHelper.TRANS_PRICE, transaction.getHhTran_price());
            cv.put(MspDBHelper.TRANS_DISCPRICE,
                    transaction.getHhTran_discPrice());
            cv.put(MspDBHelper.TRANS_NETPRICE, transaction.getHhTran_netPrice());
            cv.put(MspDBHelper.TRANS_EXTPRICE, transaction.getHhTran_extPrice());
            cv.put(MspDBHelper.TRANS_TAX, transaction.getHhTran_tax());
            cv.put(MspDBHelper.TRANS_SHIPTOCODE,
                    transaction.getHhTran_shipToCode());
            cv.put(MspDBHelper.TRANS_SHIPVIACODE,
                    transaction.getHhTran_shipViaCode());
            cv.put(MspDBHelper.TRANS_STATUS, transaction.getHhTran_status());
            cv.put(MspDBHelper.TRANS_LINEITEM, transaction.getHhTran_lineItem());
            cv.put(MspDBHelper.TRANS_DISCVALUE,
                    transaction.getHhTran_discValue());
            cv.put(MspDBHelper.TRANS_DISCTYPE, transaction.getHhTran_discType());
            cv.put(MspDBHelper.TRANS_ORDSHIPDAY,
                    transaction.getHhTran_ordShipDay());
            cv.put(MspDBHelper.TRANS_ORDSHIPMONTY,
                    transaction.getHhTran_ordShipMonth());
            cv.put(MspDBHelper.TRANS_ORDSHIPYEAR,
                    transaction.getHhTran_ordShipYear());
            cv.put(MspDBHelper.TRANS_EDITABLE, transaction.getHhTran_editable());
            cv.put(MspDBHelper.TRANS_MANITEMNO,
                    transaction.getHhTran_manItemNo());
            cv.put(MspDBHelper.TRANS_PRETAX, transaction.getHhTran_preTax());
            cv.put(MspDBHelper.TRANS_REFNO, transaction.getHhTran_refNo());
            cv.put(MspDBHelper.TRANS_ORDERDISCOUNT,
                    transaction.getHhTran_orderdiscount());
            cv.put(MspDBHelper.TRANS_TAXAMOUNT,
                    transaction.getHhTran_taxamount());
            cv.put(MspDBHelper.TRANS_TOTALCARTON,
                    transaction.getHhTran_totalcarton());
            cv.put(MspDBHelper.TRANS_EDITED_CUSTOMERNAME,
                    transaction.getHhTran_editedcustomername());
            cv.put(MspDBHelper.TRANS_ITEMUNITWEIGHT,
                    transaction.getHhTran_itemunitweight());
            cv.put(MspDBHelper.TRANS_COMPANYCODE,
                    transaction.getHhTran_companycode());
            cv.put(MspDBHelper.TRANS_LATITUDE, transaction.getHhTran_lat());
            cv.put(MspDBHelper.TRANS_LONGITUDE, transaction.getHhTran_lon());
            //cv.put(MspDBHelper.SALES_COMMISSION,transaction.getHhSalescommission());
            //cv.put(MspDBHelper.COMMENTS,transaction.getHhComments());

//            db.execSQL(Trans_Alt_SalesCommissiom);
            db.insert(MspDBHelper.TRANS_TABLE, null, cv);


            Log.i("Transaction Insert",
                    "Transaction Record Inserted successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting trans table record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            errCode = "Error 530";
            msg = "Transaction Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }
    // to add History data
    public  void  addHistory(HhHistory01 history01){


        try {

            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.HISTORY_TRANS_TRANSTYPE,
                    history01.getHhTran_transType_new());
            cv.put(MspDBHelper.HISTORY_TRANS_REFERENCENUMBER,
                    history01.getHhTran_referenceNumber_new());
            cv.put(MspDBHelper.HISTORY_TRANS_INVOICENUMBER,
                    history01.getHhTran_invoiceNumber_new());
            cv.put(MspDBHelper.HISTORY_TRANS_ORDERNUMBER,
                    history01.getHhTran_orderNumber_new());
            cv.put(MspDBHelper.HISTORY_TRANS_TRANSDAY,
                    history01.getHhTran_transDay_new());
            cv.put(MspDBHelper.HISTORY_TRANS_TRANSMONTH,
                    history01.getHhTran_transMonth_new());
            cv.put(MspDBHelper.HISTORY_TRANS_TRANSYEAR,
                    history01.getHhTran_transYear_new());
            cv.put(MspDBHelper.HISTORY_TRANS_EXPSHIPDAY,
                    history01.getHhTran_expShipDay_new());
            cv.put(MspDBHelper.HISTORY_TRANS_EXPSHIPMONTH,
                    history01.getHhTran_expShipMonth_new());
            cv.put(MspDBHelper.HISTORY_TRANS_EXPSHIPYEAR,
                    history01.getHhTran_expShipYear_new());
            cv.put(MspDBHelper.HISTORY_TRANS_CUSTOMERNUMBER,
                    history01.getHhTran_customerNumber_new());
            cv.put(MspDBHelper.HISTORY_TRANS_SALESPERSON,
                    history01.getHhTran_salesPerson_new());
            cv.put(MspDBHelper.HISTORY_TRANS_ITEMNUMBER,
                    history01.getHhTran_itemNumber_new());
            cv.put(MspDBHelper.HISTORY_TRANS_LOCID,
                    history01.getHhTran_locId_new());
            cv.put(MspDBHelper.HISTORY_TRANS_TERMS,
                    history01.getHhTran_terms_new());
            cv.put(MspDBHelper.HISTORY_TRANS_CURRENCY,
                    history01.getHhTran_currency_new());
            cv.put(MspDBHelper.HISTORY_TRANS_PRICELISTCODE,
                    history01.getHhTran_priceListCode_new());
            cv.put(MspDBHelper.HISTORY_TRANS_UOM,
                    history01.getHhTran_uom_new());
            cv.put(MspDBHelper.HISTORY_TRANS_QTY,
                    history01.getHhTran_qty_new());
            cv.put(MspDBHelper.HISTORY_TRANS_PRICE,
                    history01.getHhTran_price_new());
            cv.put(MspDBHelper.HISTORY_TRANS_DISCPRICE,
                    history01.getHhTran_discPrice_new());
            cv.put(MspDBHelper.HISTORY_TRANS_NETPRICE,
                    history01.getHhTran_netPrice_new());
            cv.put(MspDBHelper.HISTORY_TRANS_EXTPRICE,
                    history01.getHhTran_extPrice_new());
            cv.put(MspDBHelper.HISTORY_TRANS_TAX,
                    history01.getHhTran_tax_new());
            cv.put(MspDBHelper.HISTORY_TRANS_SHIPTOCODE,
                    history01.getHhTran_shipToCode_new());
            cv.put(MspDBHelper.HISTORY_TRANS_SHIPVIACODE,
                    history01.getHhTran_shipViaCode_new());
            cv.put(MspDBHelper.HISTORY_TRANS_STATUS,
                    history01.getHhTran_status_new());
            cv.put(MspDBHelper.HISTORY_TRANS_LINEITEM,
                    history01.getHhTran_lineItem_new());
            cv.put(MspDBHelper.HISTORY_TRANS_DISCVALUE,
                    history01.getHhTran_discValue_new());
            cv.put(MspDBHelper.HISTORY_TRANS_DISCTYPE,
                    history01.getHhTran_discType_new());
            cv.put(MspDBHelper.HISTORY_TRANS_ORDSHIPDAY,
                    history01.getHhTran_ordShipDay_new());
            cv.put(MspDBHelper.HISTORY_TRANS_ORDSHIPMONTY,
                    history01.getHhTran_ordShipMonth_new());
            cv.put(MspDBHelper.HISTORY_TRANS_ORDSHIPYEAR,
                    history01.getHhTran_ordShipYear_new());
            cv.put(MspDBHelper.HISTORY_TRANS_EDITABLE
                    , history01.getHhTran_editable_new());
            cv.put(MspDBHelper.HISTORY_TRANS_MANITEMNO,
                    history01.getHhTran_manItemNo_new());
            cv.put(MspDBHelper.HISTORY_TRANS_PRETAX,
                    history01.getHhTran_preTax_new());
            cv.put(MspDBHelper.HISTORY_TRANS_REFNO,
                    history01.getHhTran_refNo_new());
            cv.put(MspDBHelper.HISTORY_TRANS_ORDERDISCOUNT,
                    history01.getHhTran_orderdiscount_new());
            cv.put(MspDBHelper.HISTORY_TRANS_TAXAMOUNT,
                    history01.getHhTran_taxamount_new());
            cv.put(MspDBHelper.HISTORY_TRANS_TOTALCARTON,
                    history01.getHhTran_totalcarton_new());
            cv.put(MspDBHelper.HISTORY_TRANS_EDITED_CUSTOMERNAME,
                    history01.getHhTran_editedcustomername_new());
            cv.put(MspDBHelper.HISTORY_TRANS_ITEMUNITWEIGHT,
                    history01.getHhTran_itemunitweight_new());
            cv.put(MspDBHelper.HISTORY_TRANS_COMPANYCODE,
                    history01.getHhTran_companycode_new());
            cv.put(MspDBHelper.HISTORY_TRANS_LATITUDE,
                    history01.getHhTran_lat_new());
            cv.put(MspDBHelper.HISTORY_TRANS_LONGITUDE,
                    history01.getHhTran_lon_new());




            db.insert(MspDBHelper.HISTORY_TABLE, null, cv);

            Log.i("Transaction Insert",
                    "Transaction Record Inserted successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting trans table record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            errCode = "Error 530";
            msg = "Transaction Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

public void INSERT_DATA_NEW(String name,String number){
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MYNAME,name);
        cv.put(MYMOBILE,number);
        db.insert(MYDB,null,cv);
}
    // to add prepayment data
    public void addPrepayment(HhPrepayment01 prepayment) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.PREPAYMENT_TRANSTYPE,
                    prepayment.getHhPrePayment_transType());
            cv.put(MspDBHelper.PREPAYMENT_REFERENCENUMBER,
                    prepayment.getHhPrePayment_referenceNumber());
            cv.put(MspDBHelper.PREPAYMENT_INVOICENUMBER,
                    prepayment.getHhPrePayment_invoiceNumber());
            cv.put(MspDBHelper.PREPAYMENT_CUSTOMERNUMBER,
                    prepayment.getHhPrePayment_customerNumber());
            cv.put(MspDBHelper.PREPAYMENT_CURRENCY,
                    prepayment.getHhPrePayment_currency());
            cv.put(MspDBHelper.PREPAYMENT_ORDERTOTAL,
                    prepayment.getHhPrePayment_orderTotal());
            cv.put(MspDBHelper.PREPAYMENT_AMTDUE,
                    prepayment.getHhPrePayment_amtDue());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTTYPE,
                    prepayment.getHhPrePayment_receiptType());
            cv.put(MspDBHelper.PREPAYMENT_CHECKRECEIPTNO,
                    prepayment.getHhPrePayment_checkReceiptNo());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTDAY,
                    prepayment.getHhPrePayment_receiptDay());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTMONTH,
                    prepayment.getHhPrePayment_receiptMonth());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTYEAR,
                    prepayment.getHhPrePayment_receiptYear());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTAMOUNT,
                    prepayment.getHhPrePayment_receiptAmount());
            cv.put(MspDBHelper.PREPAYMENT_ACCPACORDNUMBER,
                    prepayment.getHhPrePayment_accpacOrdNumber());
            cv.put(MspDBHelper.PREPAYMENT_FLAG,
                    prepayment.getHhPrePayment_flag());
            cv.put(MspDBHelper.PREPAYMENT_REFNO,
                    prepayment.getHhPrePayment_refNo());
            cv.put(MspDBHelper.PREPAYMENT_COMPANYCODE,
                    prepayment.getHhPrePayment_companycode());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTTYPE2,
                    prepayment.getHhPrePayment_receiptType2());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTAMOUNT1,
                    prepayment.getHhPrePayment_receiptAmount1());

            db.insert(MspDBHelper.PREPAYMENT_TABLE, null, cv);

            Log.i("Prepayment Insert",
                    "Prepayment record inserted successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting prepayment.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            errCode = "Error 529";
            msg = "Prepayment Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    /* Created by TISN */
    // to add receipt data
    public void addReceipt(HhReceipt01 receipt) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.RECEIPT_CUSTOMERNUMBER,
                    receipt.getHhReceipt_customernumber());
            cv.put(MspDBHelper.RECEIPT_CUSTOMERNAME,
                    receipt.getHhReceipt_customername());
            cv.put(MspDBHelper.RECEIPT_DOCNUMBER,
                    receipt.getHhReceipt_docnumber());
            cv.put(MspDBHelper.RECEIPT_PENDINGBAL,
                    receipt.getHhReceipt_pendingbal());
            cv.put(MspDBHelper.RECEIPT_CURRENCY,
                    receipt.getHhReceipt_currency());
            cv.put(MspDBHelper.RECEIPT_APPLY1, receipt.getHhReceipt_apply1());
            cv.put(MspDBHelper.RECEIPT_APPLIEDAMOUNT,
                    receipt.getHhReceipt_appliedamount());
            cv.put(MspDBHelper.RECEIPT_RECEIPTTYPE,
                    receipt.getHhReceipt_receipttype());
            cv.put(MspDBHelper.RECEIPT_AMOUNT, receipt.getHhReceipt_amount());
            cv.put(MspDBHelper.RECEIPT_RECEIPTNUMBER,
                    receipt.getHhReceipt_receiptnumber());
            cv.put(MspDBHelper.RECEIPT_RECEIPTDAY,
                    receipt.getHhReceipt_receiptday());
            cv.put(MspDBHelper.RECEIPT_RECEIPTMONTH,
                    receipt.getHhReceipt_receiptmonth());
            cv.put(MspDBHelper.RECEIPT_RECEIPTYEAR,
                    receipt.getHhReceipt_receiptyear());
            cv.put(MspDBHelper.RECEIPT_STATUS, receipt.getHhReceipt_status());
            cv.put(MspDBHelper.RECEIPT_RECEIPTUNAPPLIED,
                    receipt.getHhReceipt_receiptunapplied());
            cv.put(MspDBHelper.RECEIPT_CUSTOMERAMOUNT,
                    receipt.getHhReceipt_customeramount());
            cv.put(MspDBHelper.RECEIPT_REFNO, receipt.getHhReceipt_refno());
            cv.put(MspDBHelper.RECEIPT_COMPANYCODE,
                    receipt.getHhReceipt_companycode());
            cv.put(MspDBHelper.RECEIPT_RECEIPTTYPE2,
                    receipt.getHhReceipt_receipttype2());
            cv.put(MspDBHelper.RECEIPT_AMOUNT2,
                    receipt.getHhReceipt_amount2());

            db.insert(MspDBHelper.RECEIPT_TABLE, null, cv);

            Log.i("Receipt Insert", "Receipt record inserted successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting receipt.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            errCode = "Error 531";
            msg = "Receipt Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // to add payment data
    public void addPayment(HhPayment01 payment) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.PAYMENT_APPLY1, payment.getHhPayment_apply1());
            cv.put(MspDBHelper.PAYMENT_CUSNUMBER,
                    payment.getHhPayment_cusnumber());
            cv.put(MspDBHelper.PAYMENT_CUSNAME, payment.getHhPayment_cusname());
            cv.put(MspDBHelper.PAYMENT_DOCUMENTNUMBER,
                    payment.getHhPayment_documentnumber());
            cv.put(MspDBHelper.PAYMENT_PENDINGBALANCE,
                    payment.getHhPayment_pendingbalance());
            cv.put(MspDBHelper.PAYMENT_APPLIEDAMOUNT,
                    payment.getHhPayment_appliedamount());
            cv.put(MspDBHelper.PAYMENT_NETAMOUNT,
                    payment.getHhPayment_netamount());
            cv.put(MspDBHelper.PAYMENT_STATUS, payment.getHhPayment_status());
            cv.put(MspDBHelper.PAYMENT_COMPANYCODE,
                    payment.getHhPayment_companycode());

            db.insert(MspDBHelper.PAYMENT_TABLE, null, cv);

            Log.i("Payment Insert", "Payment record inserted successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting payment.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            errCode = "Error 513";
            msg = "Payment Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // to add settings data
    public void addSetting(HhSetting setting) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.SETTING_NUMCOPIES_PRINT,
                    setting.getHhSetting_numcopiesprint());
            cv.put(MspDBHelper.SETTING_ITEM_AUTOSAVE,
                    setting.getHhSetting_itemautosave());
            cv.put(MspDBHelper.SETTING_NUMOF_PIECEDISC,
                    setting.getHhSetting_numofpiecedic());
            cv.put(MspDBHelper.SETTING_DISCPRICE,
                    setting.getHhSetting_discprice());
            cv.put(MspDBHelper.SETTING_DATE_FORMAT,
                    setting.getHhSetting_dateformat());
            cv.put(MspDBHelper.SETTING_DECIMAL_FORMAT,
                    setting.getHhSetting_decimalformat());
            cv.put(MspDBHelper.SETTING_SYNC_SERVICE,
                    setting.getHhSetting_datasyncservice());
            cv.put(MspDBHelper.SETTING_MSP_SERVERPATH,
                    setting.getHhSetting_mspserverpath());
            cv.put(MspDBHelper.SETTING_PRINTER_NAME,
                    setting.getHhSetting_printerName());
            cv.put(MspDBHelper.SETTING_PRINTER_MODEL,
                    setting.getHhSetting_printerModel());
            cv.put(MspDBHelper.SETTING_SHOW_SHIP,
                    setting.getHhSetting_showship());
            cv.put(MspDBHelper.SETTING_SHOW_PREPAY,
                    setting.getHhSetting_showprepay());
            cv.put(MspDBHelper.SETTING_AUTOREPORT_GEN,
                    setting.getHhSetting_autoreportgen());
            cv.put(MspDBHelper.SETTING_SPCODE, setting.getHhSetting_spCode());
            cv.put(MspDBHelper.SETTING_CUSTOMER_NAME_EDIT,
                    setting.getHhSetting_cusnameeditable());
            cv.put(MspDBHelper.SETTING_NONSTOCK_ITEM,
                    setting.getHhSetting_nonstockitem());

            db.insert(MspDBHelper.SETTING_TABLE, null, cv);

            Log.i("Setting Insert", "Setting record inserted successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting setting.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 527";
            msg = "Setting Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    /* to insert signature data to db */
    public void addSignature(HhSignature sign) {

        try {
            ContentValues cv = new ContentValues();
            cv.put(SIGNATURE_COMPANY_NAME, sign.getHhSignature_comapanyname());
            cv.put(SIGNATURE_REF_NUMBER, sign.getHhSignature_referencenumber());
            Bitmap mbitmap = sign.getHhSignature_signature();
            byte[] bMapArray = convertBitmapToByte(mbitmap);
            cv.put(SIGNATURE_SIGN, bMapArray);
            cv.put(SIGNATURE_STATUS, sign.getHhSignature_status());
            cv.put(SIGNATURE_DATE, sign.getHhSignature_date());
            cv.put(SIGNATURE_COMPANYCODE, sign.getHhSignature_companycode());

            db.insert(MspDBHelper.SIGNATURE_TABLE, null, cv);

            Log.i("Insert", "Signature Record Inserted ");
        } catch (Exception e) {
            // db.endTransaction();
            db.close();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 701";
            msg = "Signature Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    // to add temp item
    public void addTempItem(TempItem tempItem) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TEMPITEM_NUMBER, tempItem.getTemp_itemNum());
            cv.put(MspDBHelper.TEMPITEM_QTY, tempItem.getTemp_qty());
            cv.put(MspDBHelper.TEMPITEM_EXTPRICE, tempItem.getTemp_extPrice());
            cv.put(MspDBHelper.TEMPITEM_LOCATION, tempItem.getTemp_location());
            cv.put(MspDBHelper.TEMPITEM_UOM, tempItem.getTemp_uom());
            cv.put(MspDBHelper.TEMPITEM_DATE, tempItem.getTemp_date());
            cv.put(MspDBHelper.TEMPITEM_ENTRYTYPE, tempItem.getTemp_entryType());
            cv.put(MspDBHelper.TEMPITEM_DISCOUNT, tempItem.getTemp_discount());
            cv.put(MspDBHelper.TEMPITEM_DISCTYPE, tempItem.getTemp_discType());

            db.insert(MspDBHelper.TEMPITEM_TABLE, null, cv);

            Log.i("TempItem Insert",
                    "Temp Item record inserted successfully...");
        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when inserting tempitem table record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 532";
            msg = "TempItem Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // update temp item
    public void updateTempItem(String itmNo, String uom, TempItem tempItem,
                               boolean isEdit) {
        uom = uom.replace("'", "''");
        itmNo = itmNo.replace("'", "''");

        cursor = db.rawQuery(
                "select * from tempitem where number = '" + itmNo
                        + "' and uom = '" + uom + "'and entrytype = '"
                        + tempItem.getTemp_entryType() + "' and location = '"
                        + tempItem.getTemp_location() + "'", null);

        int count = cursor.getCount();

        if (count == 0) { // add if item not exist
            cursor.close();
            this.addTempItem(tempItem);
        } else if (count == 1) { // update if item exist
            cursor.moveToNext();
            // to update qty
            String strQty1 = tempItem.getTemp_qty();

            int qty = Integer.parseInt(strQty1);

            // to update prc
            String strPrc1 = tempItem.getTemp_extPrice();
            double tPrc = Double.parseDouble(strPrc1);
            double tDiscount = Double.parseDouble(tempItem.getTemp_discount());
            if (!isEdit) { // if not edit, quantity and discount will be
                // updated.
                String strQty2 = cursor.getString(1);
                double pPrc = tPrc / qty;
                qty = qty + Integer.parseInt(strQty2);
                tPrc = pPrc * qty;

                String strDiscount = cursor.getString(7);
                tDiscount = tDiscount + Double.parseDouble(strDiscount);
            }

            cursor.close();

            try {
                ContentValues cv = new ContentValues();

                cv.put(MspDBHelper.TEMPITEM_QTY, qty);
                cv.put(MspDBHelper.TEMPITEM_EXTPRICE, tPrc);
                cv.put(MspDBHelper.TEMPITEM_LOCATION,
                        tempItem.getTemp_location());
                cv.put(MspDBHelper.TEMPITEM_UOM, tempItem.getTemp_uom());
                cv.put(MspDBHelper.TEMPITEM_DATE, tempItem.getTemp_date());
                cv.put(MspDBHelper.TEMPITEM_ENTRYTYPE,
                        tempItem.getTemp_entryType());
                cv.put(MspDBHelper.TEMPITEM_DISCOUNT, tDiscount);
                cv.put(MspDBHelper.TEMPITEM_DISCTYPE,
                        tempItem.getTemp_discType());

                db.update(MspDBHelper.TEMPITEM_TABLE, cv, "number = '" + itmNo
                        + "' and uom = '" + uom + "'and entrytype = '"
                        + tempItem.getTemp_entryType() + "' and location = '"
                        + tempItem.getTemp_location() + "'", null);

                Log.i("TempItem Updated",
                        "Temp Item record updated successfully...");
            } catch (Exception e) {
                // db.endTransaction();
                // Log.e("Transaction Failure",
                // "Transaction failure when updating tempitem.");
                this.closeDatabase();
                Log.i("DB closed", "Database closed successfully.");

                errCode = "Error 551";
                msg = "TempItem Record Updation failed.";
                errMsg = errCode + " : " + msg;
                LogfileCreator.appendLog(errMsg);
            }
        }

    }

    // For Invoice Edit
    public void updateOrAddTranTable(String refNo, HhTran01 tranItem,
                                     double qty, String cmpnyNo) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        refNo = refNo.replace("'", "''");

        String tranType = tranItem.getHhTran_transType();

        // use this query for quote or order
        String query = "select * from hhTran01 where referencenumber = '"
                + refNo + "' and itemnumber = '"
                + tranItem.getHhTran_itemNumber() + "' and uom = '"
                + tranItem.getHhTran_uom() + "' and locid = '"
                + tranItem.getHhTran_locId() + "'";

        if (tranType.equals("CN") || tranType.equals("I")) {
            // use this query for invoice
            query = "select * from hhTran01 where referencenumber = '" + refNo
                    + "' and itemnumber = '" + tranItem.getHhTran_itemNumber()
                    + "' and uom = '" + tranItem.getHhTran_uom()
                    + "' and locid = '" + tranItem.getHhTran_locId()
                    + "' and transtype = '" + tranType + "'";
        }

        cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        if (count == 0) { // add if item not exist
            cursor.close();

            double qanty = 0.0;
            String uom = "";
            double conFact = 0.0;
            double rQty = 0.0;

            if (tranType.equals("CN") || tranType.equals("I")) {
                qanty = (double) tranItem.getHhTran_qty();
                uom = tranItem.getHhTran_uom();
                conFact = this.getUOMConvFactor(
                        tranItem.getHhTran_itemNumber(), uom, cmpnyNo);
                rQty = qanty * conFact; // to get actual quantity using uom

                if (tranType.equals("I")) {
                    this.updateItemOnHand(tranItem.getHhTran_itemNumber(),
                            "remove", rQty, tranItem.getHhTran_locId() + "",
                            cmpnyNo);
                } else {
                    this.updateItemOnHand(tranItem.getHhTran_itemNumber(),
                            "add", rQty, tranItem.getHhTran_locId() + "",
                            cmpnyNo);
                }

            }
            this.addTransactionDetail(tranItem);

        } else if (count == 1) { // update if item exist
            cursor.close();
            this.updateTransaction(refNo, tranItem, cmpnyNo);
            cursor.close();

            // double qanty = 0.0;
            String uom = "";
            double conFact = 0.0;
            double rQty = 0.0;
            if (tranType.equals("CN") || tranType.equals("I")) {
                // qanty = (double) tranItem.getHhTran_qty();
                uom = tranItem.getHhTran_uom();
                conFact = this.getUOMConvFactor(
                        tranItem.getHhTran_itemNumber(), uom, cmpnyNo);
                rQty = qty * conFact; // to get actual quantity using uom

                if (tranType.equals("I")) {
                    this.updateItemOnHand(tranItem.getHhTran_itemNumber(),
                            "remove", rQty, tranItem.getHhTran_locId() + "",
                            cmpnyNo);
                } else {
                    this.updateItemOnHand(tranItem.getHhTran_itemNumber(),
                            "add", rQty, tranItem.getHhTran_locId() + "",
                            cmpnyNo);
                }

            }
        }

    }

    public void updateOrAddTranTable(String refNo, HhTran01 tranItem,
                                     String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        refNo = refNo.replace("'", "''");
        String tranType = tranItem.getHhTran_transType();

        // use this query for quote or order
        String query = "select * from hhTran01 where referencenumber = '"
                + refNo + "' and itemnumber = '"
                + tranItem.getHhTran_itemNumber() + "' and uom = '"
                + tranItem.getHhTran_uom() + "' and locid = '"
                + tranItem.getHhTran_locId() + "' and companycode = '"
                + cmpnyNo + "'";

        if (tranType.equals("CN") || tranType.equals("I")) {
            // use this query for invoice
            query = "select * from hhTran01 where referencenumber = '" + refNo
                    + "' and itemnumber = '" + tranItem.getHhTran_itemNumber()
                    + "' and uom = '" + tranItem.getHhTran_uom()
                    + "' and locid = '" + tranItem.getHhTran_locId()
                    + "' and transtype = '" + tranType
                    + "' and companycode = '" + cmpnyNo + "'";
        }

        cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        if (count == 0) { // add if item not exist
            cursor.close();

            double qanty = 0.0;
            String uom = "";
            double conFact = 0.0;
            double rQty = 0.0;

            if (tranType.equals("CN") || tranType.equals("I")) {
                qanty = (double) tranItem.getHhTran_qty();
                uom = tranItem.getHhTran_uom();
                conFact = this.getUOMConvFactor(
                        tranItem.getHhTran_itemNumber(), uom, cmpnyNo);
                rQty = qanty * conFact; // to get actual quantity using uom

                if (tranType.equals("I")) {
                    this.updateItemOnHand(tranItem.getHhTran_itemNumber(),
                            "remove", rQty, tranItem.getHhTran_locId() + "",
                            cmpnyNo);
                } else {
                    this.updateItemOnHand(tranItem.getHhTran_itemNumber(),
                            "add", rQty, tranItem.getHhTran_locId() + "",
                            cmpnyNo);
                }

            }
            this.addTransactionDetail(tranItem);
//            this.addHistory(tranItem);
        } else if (count == 1) { // update if item exist
            cursor.close();
            this.updateTransaction(refNo, tranItem, cmpnyNo);
        }
    }

    // to update transaction record
    public void updateTransaction(String refNo, HhTran01 transaction,
                                  String cmpnyNo) {

        String newRefNo = transaction.getHhTran_referenceNumber();
        String type = transaction.getHhTran_transType();
        /*
         * if (type.equals("Q") && !refNo.startsWith("QT")) { newRefNo = "QT-" +
		 * refNo; } else if (!type.equals("Q") && refNo.startsWith("QT")) {
		 * String[] splits = refNo.split("QT-"); newRefNo = splits[1]; }
		 */

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TRANS_TRANSTYPE, type);
            cv.put(MspDBHelper.TRANS_REFERENCENUMBER, newRefNo);
            cv.put(MspDBHelper.TRANS_INVOICENUMBER,
                    transaction.getHhTran_invoiceNumber());
            cv.put(MspDBHelper.TRANS_ORDERNUMBER,
                    transaction.getHhTran_orderNumber());
            cv.put(MspDBHelper.TRANS_TRANSDAY, transaction.getHhTran_transDay());
            cv.put(MspDBHelper.TRANS_TRANSMONTH,
                    transaction.getHhTran_transMonth());
            cv.put(MspDBHelper.TRANS_TRANSYEAR,
                    transaction.getHhTran_transYear());
            cv.put(MspDBHelper.TRANS_EXPSHIPDAY,
                    transaction.getHhTran_expShipDay());
            cv.put(MspDBHelper.TRANS_EXPSHIPMONTH,
                    transaction.getHhTran_expShipMonth());
            cv.put(MspDBHelper.TRANS_EXPSHIPYEAR,
                    transaction.getHhTran_expShipYear());
            cv.put(MspDBHelper.TRANS_CUSTOMERNUMBER,
                    transaction.getHhTran_customerNumber());
            cv.put(MspDBHelper.TRANS_SALESPERSON,
                    transaction.getHhTran_salesPerson());
            cv.put(MspDBHelper.TRANS_ITEMNUMBER,
                    transaction.getHhTran_itemNumber());
            cv.put(MspDBHelper.TRANS_LOCID, transaction.getHhTran_locId());
            cv.put(MspDBHelper.TRANS_TERMS, transaction.getHhTran_terms());
            cv.put(MspDBHelper.TRANS_CURRENCY, transaction.getHhTran_currency());
            cv.put(MspDBHelper.TRANS_PRICELISTCODE,
                    transaction.getHhTran_priceListCode());
            cv.put(MspDBHelper.TRANS_UOM, transaction.getHhTran_uom());
            cv.put(MspDBHelper.TRANS_QTY, transaction.getHhTran_qty());
            cv.put(MspDBHelper.TRANS_PRICE, transaction.getHhTran_price());
            cv.put(MspDBHelper.TRANS_DISCPRICE,
                    transaction.getHhTran_discPrice());
            cv.put(MspDBHelper.TRANS_NETPRICE, transaction.getHhTran_netPrice());
            cv.put(MspDBHelper.TRANS_EXTPRICE, transaction.getHhTran_extPrice());
            cv.put(MspDBHelper.TRANS_TAX, transaction.getHhTran_tax());
            cv.put(MspDBHelper.TRANS_SHIPTOCODE,
                    transaction.getHhTran_shipToCode());
            cv.put(MspDBHelper.TRANS_SHIPVIACODE,
                    transaction.getHhTran_shipViaCode());
            cv.put(MspDBHelper.TRANS_STATUS, transaction.getHhTran_status());
            cv.put(MspDBHelper.TRANS_LINEITEM, transaction.getHhTran_lineItem());
            cv.put(MspDBHelper.TRANS_DISCVALUE,
                    transaction.getHhTran_discValue());
            cv.put(MspDBHelper.TRANS_DISCTYPE, transaction.getHhTran_discType());
            cv.put(MspDBHelper.TRANS_ORDSHIPDAY,
                    transaction.getHhTran_ordShipDay());
            cv.put(MspDBHelper.TRANS_ORDSHIPMONTY,
                    transaction.getHhTran_ordShipMonth());
            cv.put(MspDBHelper.TRANS_ORDSHIPYEAR,
                    transaction.getHhTran_ordShipYear());
            cv.put(MspDBHelper.TRANS_EDITABLE, transaction.getHhTran_editable());
            cv.put(MspDBHelper.TRANS_MANITEMNO,
                    transaction.getHhTran_manItemNo());
            cv.put(MspDBHelper.TRANS_PRETAX, transaction.getHhTran_preTax());
            cv.put(MspDBHelper.TRANS_REFNO, transaction.getHhTran_refNo());
            cv.put(MspDBHelper.TRANS_ORDERDISCOUNT,
                    transaction.getHhTran_orderdiscount());
            cv.put(MspDBHelper.TRANS_TAXAMOUNT,
                    transaction.getHhTran_taxamount());
            cv.put(MspDBHelper.TRANS_TOTALCARTON,
                    transaction.getHhTran_totalcarton());
            cv.put(MspDBHelper.TRANS_EDITED_CUSTOMERNAME,
                    transaction.getHhTran_editedcustomername());
            cv.put(MspDBHelper.TRANS_ITEMUNITWEIGHT,
                    transaction.getHhTran_itemunitweight());
            cv.put(MspDBHelper.TRANS_COMPANYCODE,
                    transaction.getHhTran_companycode());
            cv.put(MspDBHelper.TRANS_LATITUDE, transaction.getHhTran_lat());
            cv.put(MspDBHelper.TRANS_LONGITUDE, transaction.getHhTran_lon());

            if (type.equals("CN") || type.equals("I")) {
                db.update(
                        MspDBHelper.TRANS_TABLE,
                        cv,
                        "referencenumber = '" + refNo + "' and itemnumber = '"
                                + transaction.getHhTran_itemNumber()
                                + "' and uom = '" + transaction.getHhTran_uom()
                                + "' and locid = '"
                                + transaction.getHhTran_locId()
                                + "'and transtype = '" + type
                                + "' and companycode = '" + cmpnyNo + "'", null);
            } else {
                db.update(
                        MspDBHelper.TRANS_TABLE,
                        cv,
                        "referencenumber = '" + refNo + "' and itemnumber = '"
                                + transaction.getHhTran_itemNumber()
                                + "' and uom = '" + transaction.getHhTran_uom()
                                + "' and locid = '"
                                + transaction.getHhTran_locId()
                                + "' and companycode = '" + cmpnyNo + "'", null);
            }

            Log.i("Transaction Update,",
                    "Transaction Record Updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating tran table record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 552";
            msg = "Transaction Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    // to update prepayment
    public void updatePrepayment(HhPrepayment01 prepayment, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.PREPAYMENT_TRANSTYPE,
                    prepayment.getHhPrePayment_transType());
            cv.put(MspDBHelper.PREPAYMENT_REFERENCENUMBER,
                    prepayment.getHhPrePayment_referenceNumber());
            cv.put(MspDBHelper.PREPAYMENT_INVOICENUMBER,
                    prepayment.getHhPrePayment_invoiceNumber());
            cv.put(MspDBHelper.PREPAYMENT_CUSTOMERNUMBER,
                    prepayment.getHhPrePayment_customerNumber());
            cv.put(MspDBHelper.PREPAYMENT_CURRENCY,
                    prepayment.getHhPrePayment_currency());
            cv.put(MspDBHelper.PREPAYMENT_ORDERTOTAL,
                    prepayment.getHhPrePayment_orderTotal());
            cv.put(MspDBHelper.PREPAYMENT_AMTDUE,
                    prepayment.getHhPrePayment_amtDue());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTTYPE,
                    prepayment.getHhPrePayment_receiptType());
            cv.put(MspDBHelper.PREPAYMENT_CHECKRECEIPTNO,
                    prepayment.getHhPrePayment_checkReceiptNo());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTDAY,
                    prepayment.getHhPrePayment_receiptDay());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTMONTH,
                    prepayment.getHhPrePayment_receiptMonth());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTYEAR,
                    prepayment.getHhPrePayment_receiptYear());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTAMOUNT,
                    prepayment.getHhPrePayment_receiptAmount());
            cv.put(MspDBHelper.PREPAYMENT_ACCPACORDNUMBER,
                    prepayment.getHhPrePayment_accpacOrdNumber());
            cv.put(MspDBHelper.PREPAYMENT_FLAG,
                    prepayment.getHhPrePayment_flag());
            cv.put(MspDBHelper.PREPAYMENT_REFNO,
                    prepayment.getHhPrePayment_refNo());
            cv.put(MspDBHelper.PREPAYMENT_COMPANYCODE,
                    prepayment.getHhPrePayment_companycode());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTTYPE2,
                    prepayment.getHhPrePayment_receiptType2());
            cv.put(MspDBHelper.PREPAYMENT_RECEIPTAMOUNT1,
                    prepayment.getHhPrePayment_receiptAmount1());

            db.update(MspDBHelper.PREPAYMENT_TABLE, cv, "referencenumber = '"
                    + prepayment.getHhPrePayment_referenceNumber()
                    + "' and companycode = '" + cmpnyNo + "'", null);

            Log.i("Prepayment Update",
                    "Prepayment record updated successfully...");

        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating prepayment.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 553";
            msg = "Prepayment Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void updatePrepaymentThrTran(String refNo, double tot,
                                        double recpAmt, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.PREPAYMENT_ORDERTOTAL, tot);
            cv.put(MspDBHelper.PREPAYMENT_AMTDUE, (tot - recpAmt));

            db.update(MspDBHelper.PREPAYMENT_TABLE, cv, "referencenumber = '"
                    + refNo + "' and companycode = '" + cmpnyNo + "'", null);

            Log.i("Prepayment Update",
                    "Prepayment record updated successfully...");

        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating prepayment.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 554";
            msg = "Prepayment Record Updation failed through Transaction.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // update payment
    public void updatePayment(String docNo, String apply1,
                              String appliedAmount, String netAmount, String custNo,
                              String cmpnyNo) {

        try {
            float netAmt = Float.parseFloat(netAmount);

            db.execSQL("update '" + MspDBHelper.PAYMENT_TABLE
                    + "' set apply1='" + apply1
                    + "',appliedamount=appliedamount+'" + appliedAmount
                    + "',netamount='" + netAmt + "' where documentnumber = '"
                    + docNo + "' and cusnumber='" + custNo
                    + "' and companycode = '" + cmpnyNo + "'");

            Log.i("Payment Updated", "Payment record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating payment.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 555";
            msg = "Payment Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // update mspdb table
    public void updateMspDb(Mspdb mspdb, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.MSPDB_COMPANYNUMBER,
                    mspdb.getMspdb_companyNumber());
            cv.put(MspDBHelper.MSPDB_SALESPERSON, mspdb.getMspdb_salesPerson());
            cv.put(MspDBHelper.MSPDB_MAPNO, mspdb.getMspdb_mapNo());
            cv.put(MspDBHelper.MSPDB_CUSTOMERNUMBER,
                    mspdb.getMspdb_customerNumber());
            cv.put(MspDBHelper.MSPDB_ORDERNUMBER, mspdb.getMspdb_orderNumber());
            cv.put(MspDBHelper.MSPDB_QUOTENUMBER, mspdb.getMspdb_quoteNumber());
            cv.put(MspDBHelper.MSPDB_LASTINVNO, mspdb.getMspdb_lastinvno());
            cv.put(MspDBHelper.MSPDB_RECEIPTNUMBER,
                    mspdb.getMspdb_receiptnumber());

            db.update(MspDBHelper.MSPDB_TABLE, cv, "companynumber = '"
                    + cmpnyNo + "'", null);

            Log.i("MspDB Updated", "Mspdb record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 556";
            msg = "MspDB Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void updateItemOnHand(String number, String decision, double dQty,
                                 String locId, String cmpnyNo) {

        HhItem01 item = this.getItemData(number, locId, cmpnyNo);

        double availQty = item.getHhItem_qty_on_hand();

        if (decision.equals("remove")) {
            availQty = availQty - dQty;
        } else if (decision.equals("add")) {
            availQty = availQty + dQty;
        }

        try {
            ContentValues cv = new ContentValues();
            cv.put(MspDBHelper.ITEM_QTYONHAND, availQty);
            if (availQty > 0) {
                db.update(MspDBHelper.ITEM_TABLE, cv, "number = '" + number
                        + "' and locid = '" + locId + "' and companycode = '"
                        + cmpnyNo + "'", null);

                Log.i("Item Updated", "Item record updated successfully...");
            }
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating item.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 557";
            msg = "ItemOnHand Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    // update setting table
    public void updateSetting(HhSetting setting) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.SETTING_NUMCOPIES_PRINT,
                    setting.getHhSetting_numcopiesprint());
            cv.put(MspDBHelper.SETTING_ITEM_AUTOSAVE,
                    setting.getHhSetting_itemautosave());
            cv.put(MspDBHelper.SETTING_NUMOF_PIECEDISC,
                    setting.getHhSetting_numofpiecedic());
            cv.put(MspDBHelper.SETTING_DISCPRICE,
                    setting.getHhSetting_discprice());
            cv.put(MspDBHelper.SETTING_DATE_FORMAT,
                    setting.getHhSetting_dateformat());
            cv.put(MspDBHelper.SETTING_DECIMAL_FORMAT,
                    setting.getHhSetting_decimalformat());
            cv.put(MspDBHelper.SETTING_SYNC_SERVICE,
                    setting.getHhSetting_datasyncservice());
            cv.put(MspDBHelper.SETTING_MSP_SERVERPATH,
                    setting.getHhSetting_mspserverpath());
            cv.put(MspDBHelper.SETTING_PRINTER_NAME,
                    setting.getHhSetting_printerName());
            cv.put(MspDBHelper.SETTING_PRINTER_MODEL,
                    setting.getHhSetting_printerModel());
            cv.put(MspDBHelper.SETTING_SHOW_SHIP,
                    setting.getHhSetting_showship());
            cv.put(MspDBHelper.SETTING_SHOW_PREPAY,
                    setting.getHhSetting_showprepay());
            cv.put(MspDBHelper.SETTING_AUTOREPORT_GEN,
                    setting.getHhSetting_autoreportgen());
            cv.put(MspDBHelper.SETTING_CUSTOMER_NAME_EDIT,
                    setting.getHhSetting_cusnameeditable());
            cv.put(MspDBHelper.SETTING_NONSTOCK_ITEM,
                    setting.getHhSetting_nonstockitem());

            db.update(MspDBHelper.SETTING_TABLE, cv, null, null);

            Log.i("Setting Updated", "Setting record updated successfully...");
        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when updating setting.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            Log.e("Error 558", "Setting Record Updation failed.");
            errCode = "Error 558";
            msg = "Setting Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // update setting table
    public void updateSettingInImport(HhSetting setting) {

        try {
            ContentValues cv = new ContentValues();
            cv.put(MspDBHelper.SETTING_SPCODE, setting.getHhSetting_spCode());

            db.update(MspDBHelper.SETTING_TABLE, cv, null, null);

            Log.i("Setting Updated", "Setting record updated successfully...");
        } catch (Exception e) {
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            Log.e("Error 801", "Setting Record Updation in import failed.");
            errCode = "Error 801";
            msg = "Setting Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // update signature table
    public void updateSignature(HhSignature sign, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();
            cv.put(SIGNATURE_COMPANY_NAME, sign.getHhSignature_comapanyname());
            cv.put(SIGNATURE_REF_NUMBER, sign.getHhSignature_referencenumber());
            Bitmap mbitmap = sign.getHhSignature_signature();
            byte[] bMapArray = convertBitmapToByte(mbitmap);
            cv.put(SIGNATURE_SIGN, bMapArray);
            cv.put(SIGNATURE_STATUS, sign.getHhSignature_status());
            cv.put(SIGNATURE_DATE, sign.getHhSignature_date());
            cv.put(SIGNATURE_COMPANYCODE, sign.getHhSignature_companycode());

            db.update(MspDBHelper.SIGNATURE_TABLE, cv,
                    "refernumber= '" + sign.getHhSignature_referencenumber()
                            + "' and companycode = '" + cmpnyNo + "'", null);

            Log.i("Signature Updated",
                    "Signature record updated successfully...");
        } catch (Exception e) {
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
            Log.e("Error 702", "Signature Record Updation failed.");
            errCode = "Error 702";
            msg = "Signature Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    /* to insert customer data to db */
    public void updateCustomer(HhCustomer01 customer, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.CUSTOMER_NUMBER, customer.getHhCustomer_number());
            cv.put(MspDBHelper.CUSTOMER_NAME, customer.getHhCustomer_name());
            cv.put(MspDBHelper.CUSTOMER_ADDRESS,
                    customer.getHhCustomer_address());
            cv.put(MspDBHelper.CUSTOMER_CITY, customer.getHhCustomer_city());
            cv.put(MspDBHelper.CUSTOMER_STATE, customer.getHhCustomer_state());
            cv.put(MspDBHelper.CUSTOMER_ZIP, customer.getHhCustomer_zip());
            cv.put(MspDBHelper.CUSTOMER_COUNTRY,
                    customer.getHhCustomer_country());
            cv.put(MspDBHelper.CUSTOMER_PHONE1, customer.getHhCustomer_phone1());
            cv.put(MspDBHelper.CUSTOMER_EMAIL1, customer.getHhCustomer_email1());
            cv.put(MspDBHelper.CUSTOMER_WEBSITE,
                    customer.getHhCustomer_website());
            cv.put(MspDBHelper.CUSTOMER_CONTACT,
                    customer.getHhCustomer_contact());
            cv.put(MspDBHelper.CUSTOMER_EMAIL2, customer.getHhCustomer_email2());
            cv.put(MspDBHelper.CUSTOMER_GROUPCODE,
                    customer.getHhCustomer_groupcode());
            cv.put(MspDBHelper.CUSTOMER_TYPE, customer.getHhCustomer_type());
            cv.put(MspDBHelper.CUSTOMER_TERMS, customer.getHhCustomer_terms());
            cv.put(MspDBHelper.CUSTOMER_LASTBALANCE,
                    customer.getHhCustomer_lastbalance());
            cv.put(MspDBHelper.CUSTOMER_CREDITLIMIT,
                    customer.getHhCustomer_creditlimit());
            cv.put(MspDBHelper.CUSTOMER_LASTSALE,
                    customer.getHhCustomer_lastsale());
            cv.put(MspDBHelper.CUSTOMER_TAXGROUP,
                    customer.getHhCustomer_taxgroup());
            cv.put(MspDBHelper.CUSTOMER_COMMENT,
                    customer.getHhCustomer_comment());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY1,
                    customer.getHhCustomer_taxauthority1());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE1,
                    customer.getHhCustomer_taxable1());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS1,
                    customer.getHhCustomer_taxstts1());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY2,
                    customer.getHhCustomer_taxauthority2());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE2,
                    customer.getHhCustomer_taxable2());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS2,
                    customer.getHhCustomer_taxstts2());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY3,
                    customer.getHhCustomer_taxauthority3());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE3,
                    customer.getHhCustomer_taxable3());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS3,
                    customer.getHhCustomer_taxstts3());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY4,
                    customer.getHhCustomer_taxauthority4());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE4,
                    customer.getHhCustomer_taxable4());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS4,
                    customer.getHhCustomer_taxstts4());
            cv.put(MspDBHelper.CUSTOMER_TAXAUTHORITY5,
                    customer.getHhCustomer_taxauthority5());
            cv.put(MspDBHelper.CUSTOMER_TAXABLE5,
                    customer.getHhCustomer_taxable5());
            cv.put(MspDBHelper.CUSTOMER_TAXSTTS5,
                    customer.getHhCustomer_taxstts5());
            cv.put(MspDBHelper.CUSTOMER_STATUS, customer.getHhCustomer_status());
            cv.put(MspDBHelper.CUSTOMER_PRICELISTCODE,
                    customer.getHhCustomer_pricelistcode());
            cv.put(MspDBHelper.CUSTOMER_IDTAXREGI1,
                    customer.getHhCustomer_idtaxregi1());
            cv.put(MspDBHelper.CUSTOMER_CREDITSTATUS,
                    customer.getHhCustomer_creditstatus());
            cv.put(MspDBHelper.CUSTOMER_SHIPVIA,
                    customer.getHhCustomer_shipvia());
            cv.put(MspDBHelper.CUSTOMER_COMPANYCODE,
                    customer.getHhCustomer_companycode());
            cv.put(MspDBHelper.CUSTOMER_ALT_NO,customer.getHhCustomer_customer_alt_no());

            db.update(MspDBHelper.CUSTOMER_TABLE, cv,
                    "number= '" + customer.getHhCustomer_number()
                            + "' and companycode = '" + cmpnyNo + "' ", null);

            Log.i("Customer Update", "Customer Record Updated successfully... ");

        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating customer.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 559";
            msg = "New Customer Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // to delete temp item
    public void deleteTempItem(TempItem obj) {

        try {
            db.delete(
                    MspDBHelper.TEMPITEM_TABLE,
                    "number = '" + obj.getTemp_itemNum() + "' and uom = '"
                            + obj.getTemp_uom() + "' and entrytype = '"
                            + obj.getTemp_entryType() + "'", null);

            Log.i("TempItem Deleted",
                    "Temp Item record deleted successfully...");
        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when deleting tempitem.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 562";
            msg = "Particular TempItem Record Deletion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // to delete all record in temp item table
    public void deleteTempTableRecords() {

        try {
            db.delete(MspDBHelper.TEMPITEM_TABLE, null, null);

            Log.i("All TempItem Deleted",
                    "All temp item records deleted successfully...");
        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when deleting tempitem.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 563";
            msg = "Particular TempItem Record Deletion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void deleteTranItem(String refNo, TempItem obj) {

        String entryType = obj.getTemp_entryType();
        String tranType = "";
        String query = "referencenumber = '" + refNo + "' and itemnumber = '"
                + obj.getTemp_itemNum() + "' and uom = '" + obj.getTemp_uom()
                + "' and locid = '" + obj.getTemp_location() + "'";

        if (entryType.equals("tr")) {
            tranType = "CN";
            query = "referencenumber = '" + refNo + "' and itemnumber = '"
                    + obj.getTemp_itemNum() + "' and uom = '"
                    + obj.getTemp_uom() + "' and locid = '"
                    + obj.getTemp_location() + "' and transtype = '" + tranType
                    + "'";

        } else if (entryType.equals("ts")) {
            tranType = "I";
            query = "referencenumber = '" + refNo + "' and itemnumber = '"
                    + obj.getTemp_itemNum() + "' and uom = '"
                    + obj.getTemp_uom() + "' and locid = '"
                    + obj.getTemp_location() + "' and transtype = '" + tranType
                    + "'";
        }

        try {

            db.delete(MspDBHelper.TRANS_TABLE, query, null);

            Log.i("Transaction Deleted",
                    "Transaction records deleted successfully...");
        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when deleting tran item.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 565";
            msg = "Particular Transaction Item Record Deletion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    // to delete customer data
    public void deleteNewCustomer(String num, String cmpnyNo) {

        try {

            db.delete(MspDBHelper.CUSTOMER_TABLE, "number = '" + num
                    + "' and companycode = '" + cmpnyNo + "' ", null);

            Log.i("Customer Deleted", "Customer record deleted successfully...");
        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when deleting new customer.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 567";
            msg = "New Customer Record Deletion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        } // db.close();
    }

    // to delete customer data
    public void deletePrepayment(String refNum, String cmpnyNo) {

        try {

            db.delete(MspDBHelper.PREPAYMENT_TABLE, "referencenumber = '"
                    + refNum + "' and companycode = '" + cmpnyNo + "'", null);

            Log.i("Prepayment Deleted",
                    "Prepayment record deleted successfully...");
        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when deleting prepayment.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 566";
            msg = "Prepayment Record Deletion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // to delete transaction datas
    public void deleteTransaction(String refNo, String cmpnyNo) {

        try {

            db.delete(MspDBHelper.TRANS_TABLE, "referencenumber = '" + refNo
                    + "' and companycode = '" + cmpnyNo + "'", null);

            Log.i("All Transaction Deleted", "Transaction record deleted ");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when deleting tran item.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 564";
            msg = "Transaction Record Deletion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // to delete signature for this transaction
    public void deleteSignature(String refNo, String cmpnyNo) {

        try {
            db.delete(MspDBHelper.SIGNATURE_TABLE, "refernumber = '" + refNo
                    + "' and companycode = '" + cmpnyNo + "'", null);

            Log.i("Signature Deleted",
                    "Signature record deleted successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when deleting signature.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 704";
            msg = "Particular Signature Record Deletion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public List<HhCustomer01> getCustomerDataList(String cmpnyNo) {

        List<HhCustomer01> cusList = new ArrayList<HhCustomer01>();
        cmpnyNo = cmpnyNo.replace("'", "''");

        HhCustomer01 customer = null;

        cursor = db.rawQuery(
                "select number,name from hhCustomer01 where companycode = '"
                        + cmpnyNo + "' ", null);

        try {
            while (cursor.moveToNext()) {
                customer = new HhCustomer01();
                customer.setHhCustomer_number(cursor.getString(0));
                customer.setHhCustomer_name(cursor.getString(1));

                cusList.add(customer);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 601";
            msg = "All Customer Data Selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
        cursor.close();

        return cusList;
    }

    // get new customer datas
    public List<HhCustomer01> getNewCustomerDataList(String cmpnyNo) {

        List<HhCustomer01> cusList = new ArrayList<HhCustomer01>();
        cmpnyNo = cmpnyNo.replace("'", "''");

        HhCustomer01 customer = null;

        cursor = db.rawQuery(
                "select number,name from hhCustomer01 where companycode = '"
                        + cmpnyNo + "' and status='" + 0 + "'", null);

        try {
            while (cursor.moveToNext()) {
                customer = new HhCustomer01();

                customer.setHhCustomer_number(cursor.getString(0));
                customer.setHhCustomer_name(cursor.getString(1));

                cusList.add(customer);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 602";
            msg = "New Customer Data Selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return cusList;
    }

    public List<HhReceipt01> getReceiptSelectedData(int frday, int frmonth, int fryear, int tday, int tmonth, int tyear,String cmpnyno) {

        Cursor cursor_Receiptdetails = null;
        List<HhReceipt01> hhReceipt01s = null;
        HhReceipt01 hhReceipt01 = null;

        DecimalFormat formatter = new DecimalFormat("00");

        String fromDay = String.valueOf(frday);
        String fromMonth = String.valueOf(frmonth);
        String fromYear = String.valueOf(fryear);
        String toDay = String.valueOf(tday);
        String toMonth = String.valueOf(tmonth);
        String toYear = String.valueOf(tyear);

        if(frday<10){
            fromDay = formatter.format(frday);
        }
        if(frmonth<10){
            fromMonth = formatter.format(frmonth);
        }
        if(tday<10){
            toDay = formatter.format(tday);
        }
        if(tmonth<10){
            toMonth = formatter.format(tmonth);
        }


        cursor_Receiptdetails = db.rawQuery(
                "select distinct customernumber,customername,receiptnumber from hhReceipt01 where (10000*receiptyear +100*receiptmonth+receiptday >= "
                        + fromYear + fromMonth + fromDay +
                        " and 10000*receiptyear+100*receiptmonth+receiptday <= " + toYear + toMonth + toDay + ") and companycode = '" + cmpnyno +  "' ", null);


        if (cursor_Receiptdetails.getCount() > 0) {

            hhReceipt01s = new ArrayList<HhReceipt01>();
            while (cursor_Receiptdetails.moveToNext()) {

                hhReceipt01 = new HhReceipt01();

                hhReceipt01.setHhReceipt_customernumber(cursor_Receiptdetails.getString(0));
                hhReceipt01.setHhReceipt_customername(cursor_Receiptdetails.getString(1));
                hhReceipt01.setHhReceipt_receiptnumber(cursor_Receiptdetails.getString(2));


                hhReceipt01s.add(hhReceipt01);
            }
        }
        cursor_Receiptdetails.close();

        return hhReceipt01s;
    };

    public HhCustomer01 getCustomerData(String number, String cmpnyNo) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        number = number.replace("'", "''");

        HhCustomer01 cust = null;

        cursor = db.rawQuery("select * from hhCustomer01 where number = '"
                + number + "' and companycode = '" + cmpnyNo + "' ", null);

        try {
            while (cursor.moveToNext()) {

                cust = new HhCustomer01();

                cust.setHhCustomer_number(cursor.getString(0));
                cust.setHhCustomer_name(cursor.getString(1));
                cust.setHhCustomer_address(cursor.getString(2));
                cust.setHhCustomer_city(cursor.getString(3));
                cust.setHhCustomer_state(cursor.getString(4));
                cust.setHhCustomer_zip(cursor.getString(5));
                cust.setHhCustomer_country(cursor.getString(6));
                cust.setHhCustomer_phone1(cursor.getString(7));
                cust.setHhCustomer_email1(cursor.getString(8));
                cust.setHhCustomer_website(cursor.getString(9));
                cust.setHhCustomer_contact(cursor.getString(10));
                cust.setHhCustomer_email2(cursor.getString(11));
                cust.setHhCustomer_groupcode(cursor.getString(12));
                cust.setHhCustomer_type(cursor.getString(13));
                cust.setHhCustomer_terms(cursor.getString(14));
                cust.setHhCustomer_lastbalance(cursor.getDouble(15));
                cust.setHhCustomer_creditlimit(cursor.getDouble(16));
                cust.setHhCustomer_lastsale(cursor.getDouble(17));
                cust.setHhCustomer_taxgroup(cursor.getString(18));
                cust.setHhCustomer_comment(cursor.getString(19));

                cust.setHhCustomer_taxauthority1(cursor.getString(20));
                cust.setHhCustomer_taxable1(cursor.getInt(21));
                cust.setHhCustomer_taxstts1(cursor.getString(22));

                cust.setHhCustomer_taxauthority2(cursor.getString(23));
                cust.setHhCustomer_taxable2(cursor.getInt(24));
                cust.setHhCustomer_taxstts2(cursor.getString(25));

                cust.setHhCustomer_taxauthority3(cursor.getString(26));
                cust.setHhCustomer_taxable3(cursor.getInt(27));
                cust.setHhCustomer_taxstts3(cursor.getString(28));

                cust.setHhCustomer_taxauthority4(cursor.getString(29));
                cust.setHhCustomer_taxable4(cursor.getInt(30));
                cust.setHhCustomer_taxstts4(cursor.getString(31));

                cust.setHhCustomer_taxauthority5(cursor.getString(32));
                cust.setHhCustomer_taxable5(cursor.getInt(33));
                cust.setHhCustomer_taxstts5(cursor.getString(34));

                cust.setHhCustomer_status(cursor.getInt(35));
                cust.setHhCustomer_pricelistcode(cursor.getString(36));
                cust.setHhCustomer_idtaxregi1(cursor.getString(37));
                cust.setHhCustomer_creditstatus(cursor.getString(38));
                cust.setHhCustomer_shipvia(cursor.getString(39));
                cust.setHhCustomer_companycode(cursor.getString(40));
//                cust.setHhCustomer_customer_alt_no(cursor.getString(41));
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 603";
            msg = "Particular Customer Data Selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return cust;
    }

    public List<HhTran01> getOrdInvData(int frday, int frmonth, int fryear, int tday, int tmonth, int tyear,String cmpnyno) {

        Cursor cursor_transdetails = null;
        List<HhTran01> hisTranmodel = null;
        HhTran01 hhTran01 = null;

        DecimalFormat formatter = new DecimalFormat("00");

        String fromDay = String.valueOf(frday);
        String fromMonth = String.valueOf(frmonth);
        String fromYear = String.valueOf(fryear);
        String toDay = String.valueOf(tday);
        String toMonth = String.valueOf(tmonth);
        String toYear = String.valueOf(tyear);

        if(frday<10){
            fromDay = formatter.format(frday);
        }
        if(frmonth<10){
            fromMonth = formatter.format(frmonth);
        }
        if(tday<10){
            toDay = formatter.format(tday);
        }
        if(tmonth<10){
            toMonth = formatter.format(tmonth);
        }


        cursor_transdetails = db.rawQuery(
                "select distinct referencenumber,customernumber,ordernumber,invoicenumber,refno from hhTran01 where (10000*expshipyear +100*expshipmonth+expshipday >= "
                        + fromYear + fromMonth + fromDay +
                        " and 10000*expshipyear+100*expshipmonth+expshipday <= " + toYear + toMonth + toDay + ") and companycode = '" + cmpnyno +  "' ", null);


        if (cursor_transdetails.getCount() > 0) {

            hisTranmodel = new ArrayList<HhTran01>();
            while (cursor_transdetails.moveToNext()) {

                hhTran01 = new HhTran01();

                String referenceNo = cursor_transdetails.getString(0);

                String cusNum = cursor_transdetails.getString(1);
                String docNo = cursor_transdetails.getString(2);
                if(docNo.equals("0")){
                    docNo = cursor_transdetails.getString(3);
                }

                String refno = cursor_transdetails.getString(4);

                hhTran01.setHhTran_referenceNumber(referenceNo);
                hhTran01.setHhTran_orderNumber(docNo);
                hhTran01.setHhTran_customerNumber(cusNum);
                hhTran01.setHhTran_refNo(refno);

                hisTranmodel.add(hhTran01);

			/*	hhTran01 = new HhTran01();

				hhTran01.setHhTran_orderNumber(cursor_transdetails.getString(0));
				hhTran01.setHhTran_referenceNumber(cursor_transdetails.getString(1));


				*//*	hhTran01.setHhTran_editedcustomername(cursor_transdetails.getString(2));
				hhTran01.setHhTran_transDay(cursor_transdetails.getInt(3));
				hhTran01.setHhTran_transMonth(cursor_transdetails.getInt(4));
				hhTran01.setHhTran_transYear(cursor_transdetails.getInt(5));
				hhTran01.setHhTran_status(cursor_transdetails.getInt(6));*//*

				hisTranmodel.add(hhTran01);*/

            }
        }
        cursor_transdetails.close();

        return hisTranmodel;
    };

    // to get customer name
    public String getCustomerName(String number, String cmpnyNo) {

        String custName = "";
        cmpnyNo = cmpnyNo.replace("'", "''");
        number = number.replace("'", "''");

        cursor = db.rawQuery("select name from hhCustomer01 where number = '"
                + number + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {

                custName = cursor.getString(0);

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 604";
            msg = "Customer Name Selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return custName;
    }

    // to get customer name
    public String getCustomerPricelist(String number, String cmpnyNo) {

        String custName = "";
        cmpnyNo = cmpnyNo.replace("'", "''");
        number = number.replace("'", "''");
        cursor = db.rawQuery(
                "select pricelistcode from hhCustomer01 where number = '"
                        + number + "' and companycode = '" + cmpnyNo + "'",
                null);

        try {
            while (cursor.moveToNext()) {

                custName = cursor.getString(0);

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 604";
            msg = "Customer Name Selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return custName;
    }

    // to get customer credit limit
    public double getCustomerCreditLimit(String number, String cmpnyNo) {

        double creditLimit = 0.0;
        cmpnyNo = cmpnyNo.replace("'", "''");
        number = number.replace("'", "''");
        cursor = db.rawQuery(
                "select creditlimit from hhCustomer01 where number = '"
                        + number + "' and companycode = '" + cmpnyNo + "'",
                null);

        try {
            while (cursor.moveToNext()) {

                creditLimit = cursor.getDouble(0);

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 605";
            msg = "Particular Customer credit limit Selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return creditLimit;
    }

    public List<HhTran01> getHistoryDateTimeData(int frday, int frmonth, int fryear, int tday, int tmonth, int tyear,String strType) {

        Cursor cursor_transdetails = null;
        List<HhTran01> hisTranmodel = null;
        HhTran01 hhTran01 = null;

        DecimalFormat formatter = new DecimalFormat("00");

        String fromDay = String.valueOf(frday);
        String fromMonth = String.valueOf(frmonth);
        String fromYear = String.valueOf(fryear);
        String toDay = String.valueOf(tday);
        String toMonth = String.valueOf(tmonth);
        String toYear = String.valueOf(tyear);

        if(frday<10){
            fromDay = formatter.format(frday);
        }
        if(frmonth<10){
            fromMonth = formatter.format(frmonth);
        }
        if(tday<10){
            toDay = formatter.format(tday);
        }
        if(tmonth<10){
            toMonth = formatter.format(tmonth);
        }

        String cn = "CN";
        String i = "I";


        if(strType.equals("Orders")){
            cursor_transdetails = db.rawQuery(
                    "select distinct referencenumber,customernumber,refno,transday,transmonth,transyear,status from hhTran01 where (10000*expshipyear +100*expshipmonth+expshipday >= "
                            + fromYear + fromMonth + fromDay +
                            " and 10000*expshipyear+100*expshipmonth+expshipday <= " + toYear + toMonth + toDay + ") and status = 1 and transtype = 'S' ", null);
        }else if(strType.equals("Invoices")){
            cursor_transdetails = db.rawQuery(
                    "select distinct referencenumber,customernumber,refno,transday,transmonth,transyear,status from hhTran01 where (10000*expshipyear +100*expshipmonth+expshipday >= "
                            + fromYear + fromMonth + fromDay +
                            " and 10000*expshipyear+100*expshipmonth+expshipday <= " + toYear + toMonth + toDay + ") and status = 1 and (transtype = 'CN' or transtype = 'I') ", null);
        }


        if (cursor_transdetails.getCount() > 0) {

            hisTranmodel = new ArrayList<HhTran01>();
            while (cursor_transdetails.moveToNext()) {
                hhTran01 = new HhTran01();


                hhTran01.setHhTran_referenceNumber(cursor_transdetails.getString(0));
                hhTran01.setHhTran_customerNumber(cursor_transdetails.getString(1));
                hhTran01.setHhTran_refNo(cursor_transdetails.getString(2));
                hhTran01.setHhTran_transDay(cursor_transdetails.getInt(3));
                hhTran01.setHhTran_transMonth(cursor_transdetails.getInt(4));
                hhTran01.setHhTran_transYear(cursor_transdetails.getInt(5));
                hhTran01.setHhTran_status(cursor_transdetails.getInt(6));

                hisTranmodel.add(hhTran01);
            }
        }
        cursor_transdetails.close();

        return hisTranmodel;
    };
    public List<HhReceipt01> getHistoryDateTimeDataReceipts(int frday, int frmonth, int fryear, int tday, int tmonth, int tyear,String strType) {

        Cursor cursor_transdetails = null;
        List<HhReceipt01> hisTranmodel = null;
        HhReceipt01 HhReceipt01 = null;

        DecimalFormat formatter = new DecimalFormat("00");

        String fromDay = String.valueOf(frday);
        String fromMonth = String.valueOf(frmonth);
        String fromYear = String.valueOf(fryear);
        String toDay = String.valueOf(tday);
        String toMonth = String.valueOf(tmonth);
        String toYear = String.valueOf(tyear);

        if(frday<10){
            fromDay = formatter.format(frday);
        }
        if(frmonth<10){
            fromMonth = formatter.format(frmonth);
        }
        if(tday<10){
            toDay = formatter.format(tday);
        }
        if(tmonth<10){
            toMonth = formatter.format(tmonth);
        }

        String cn = "CN";
        String i = "I";


        if(strType.equals("Receipts")) {
            cursor_transdetails = db.rawQuery(
                    //select distinct receiptnumber,customernumber,refno,receiptday,receiptmonth,receiptyear,status
                    "select distinct receiptnumber,customernumber,refno,receiptday,receiptmonth,receiptyear,status from hhReceipt01 where (10000*receiptyear +100*receiptmonth+receiptday >= "
                            + fromYear + fromMonth + fromDay +
                            " and 10000*receiptyear+100*receiptmonth+receiptday <= " + toYear + toMonth + toDay + ") and status = 1 ", null);

        }

        if (cursor_transdetails.getCount() > 0) {

            hisTranmodel = new ArrayList<HhReceipt01>();
            while (cursor_transdetails.moveToNext()) {
                HhReceipt01 = new HhReceipt01();

                HhReceipt01.setHhReceipt_receiptnumber(cursor_transdetails.getString(0));
                HhReceipt01.setHhReceipt_customernumber(cursor_transdetails.getString(1));
                HhReceipt01.setHhReceipt_refno(cursor_transdetails.getString(2));
                HhReceipt01.setHhReceipt_receiptday(cursor_transdetails.getInt(3));
                HhReceipt01.setHhReceipt_receiptmonth(cursor_transdetails.getInt(4));
                HhReceipt01.setHhReceipt_receiptyear(cursor_transdetails.getInt(5));
                HhReceipt01.setHhReceipt_status(cursor_transdetails.getInt(6));

                hisTranmodel.add(HhReceipt01);
            }
        }
        cursor_transdetails.close();

        return hisTranmodel;
    };

    // for item table
    public List<String> getItemDatas(List<String> itmList, String locId) {

        itmList = new ArrayList<String>();

        String itm = "";

        locId = locId.replace("'", "''");

        cursor = db.rawQuery(
                "select number,description from hhitem01 where locid = '"
                        + locId + "'", null);

        try {
            while (cursor.moveToNext()) {

                itm = cursor.getString(0) + "   " + cursor.getString(1);

                itmList.add(itm);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 606";
            msg = "Item Data Selection failed(Based on Location Id ).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return itmList;
    }

    // for item table
    public List<HhItem01> getItemDataList(String locId, String cmpnyNo) {

        List<HhItem01> itmList = new ArrayList<HhItem01>();

        locId = locId.replace("'", "''");
        cmpnyNo = cmpnyNo.replace("'", "''");
        HhItem01 item = null;

        cursor = db.rawQuery(
                "select number,description from hhitem01 where locid = '"
                        + locId + "' and companycode = '" + cmpnyNo + "' ",
                null);

        try {
            while (cursor.moveToNext()) {
                item = new HhItem01();
                item.setHhItem_number(cursor.getString(0));
                item.setHhItem_description(cursor.getString(1));

                itmList.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 606";
            msg = "Item Data Selection failed(Based on Location Id ).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return itmList;
    }

    // for item table
    public List<HhItem01> getItemDataList(String locId, String cmpnyNo,
                                          int isStockItem) {

        List<HhItem01> itmList = new ArrayList<HhItem01>();

        locId = locId.replace("'", "''");
        cmpnyNo = cmpnyNo.replace("'", "''");

        HhItem01 item = null;

        cursor = db.rawQuery(
                "select number,description from hhitem01 where locid = '"
                        + locId + "' and stockitem = '" + isStockItem
                        + "' and companycode = '" + cmpnyNo + "' ", null);

        try {
            while (cursor.moveToNext()) {
                item = new HhItem01();
                item.setHhItem_number(cursor.getString(0));
                item.setHhItem_description(cursor.getString(1));

                itmList.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 802";
            msg = "Item Data Selection failed(Based on Location Id ).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return itmList;
    }

    public HhItem01 getItemData(String number, String locId, String cmpnyNo) {

        HhItem01 item = null;

        locId = locId.replace("'", "''");
        cmpnyNo = cmpnyNo.replace("'", "''");

        cursor = db
                .rawQuery(
                        "select number,description,lastunitcost,currency,locuom,qtyonhand,costingmethod,altitem,altitemdesc,locid,stockinguom from hhItem01 where number = '"
                                + number
                                + "' and locid = '"
                                + locId
                                + "' and companycode = '" + cmpnyNo + "' ",
                        null);

        try {
            while (cursor.moveToNext()) {
                item = new HhItem01();

                item.setHhItem_number(cursor.getString(0));
                item.setHhItem_description(cursor.getString(1));
                item.setHhItem_last_unit_cost(Double.parseDouble(cursor
                        .getString(2)));
                item.setHhItem_currency(cursor.getString(3));
                item.setHhItem_loc_uom(cursor.getString(4));
                item.setHhItem_qty_on_hand(Double.parseDouble(cursor
                        .getString(5)));
                item.setHhItem_costing_method(cursor.getString(6));
                item.setHhItem_alt_itm(cursor.getString(7));
                item.setHhItem_alt_itm_desc(cursor.getString(8));
                item.setHhItem_loc_id(cursor.getString(9));
                item.setHhItem_stocking_uom(cursor.getString(10));

                // stock item
                /* item.setHhItem_stockitem(cursor.getInt(10)); */// added for
                // nonstock item
                // 19July2013
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 607";
            msg = "Item Data Selection failed(Based on Location Id and Number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return item;
    }

    public Double getItemQty(String itmNum, String itmLoc, String cmpnyNo) {

        itmNum = itmNum.replace("'", "''");
        cmpnyNo = cmpnyNo.replace("'", "''");
        itmLoc = itmLoc.replace("'", "''");

        double itmQty = 0;
        cursor = db.rawQuery("select qtyonhand from hhItem01 where number = '"
                + itmNum + "' and locid = '" + itmLoc + "' and companycode = '"
                + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                itmQty = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 901";
            msg = "Quantity Getting failed(Based on item number and location).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return itmQty;
    }

    // For Invoice Edit
    public Double getItemQty(String itmNum, String itmLoc) {

        itmNum = itmNum.replace("'", "''");
        itmLoc = itmLoc.replace("'", "''");

        double itmQty = 0;
        cursor = db.rawQuery("select qtyonhand from hhItem01 where number = '"
                + itmNum + "' and locid = '" + itmLoc + "'", null);

        try {
            while (cursor.moveToNext()) {
                itmQty = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 901";
            msg = "Quantity Getting failed(Based on item number and location).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return itmQty;
    }

    public void updateInventory(String itmNum, String itmLoc, String cmpnyNo,
                                Double mUpdateQty) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.ITEM_QTYONHAND, mUpdateQty);

            db.update(MspDBHelper.ITEM_TABLE, cv, "number = '" + itmNum
                    + "' and locid = '" + itmLoc + "' and companycode = '"
                    + cmpnyNo + "'", null);

            Log.i("ItemQuantity Updated",
                    "ItemQuantity record updated successfully...");
        } catch (Exception e) {
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 902";
            msg = "ItemQuantity Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // to get item by item number
    public HhItem01 getItemDataByNum(String number, String cmpnyNo) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        number = number.replace("'", "''");

        HhItem01 item = null;

        cursor = db
                .rawQuery(
                        "select number,description,lastunitcost,currency,locuom,qtyonhand,costingmethod,altitem,altitemdesc,locid from hhItem01 where number = '"
                                + number + "' and companycode = '" + cmpnyNo
                                /*
                                 * + "' and locid = '" + locId
								 */
                                + "'or altno = " +number +" and companycode = '" +cmpnyNo+ "' ", null);

        try {
            while (cursor.moveToNext()) {
                item = new HhItem01();
                item.setHhItem_number(cursor.getString(0));
                item.setHhItem_description(cursor.getString(1));
                item.setHhItem_last_unit_cost(Double.parseDouble(cursor
                        .getString(2)));
                item.setHhItem_currency(cursor.getString(3));
                item.setHhItem_loc_uom(cursor.getString(4));
                item.setHhItem_qty_on_hand(Double.parseDouble(cursor
                        .getString(5)));
                item.setHhItem_costing_method(cursor.getString(6));
                item.setHhItem_alt_itm(cursor.getString(7));
                item.setHhItem_alt_itm_desc(cursor.getString(8));
                item.setHhItem_loc_id(cursor.getString(9));
                // stock item
                /* item.setHhItem_stockitem(cursor.getInt(10)); */
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 608";
            msg = "Item Data Selection failed(Based on Item Number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return item;
    }

    // method to get item number from item, jun 28 for barcode integration
    public HhItem01 getItemDataByNum(String number, String uom, String cmpnyNo) {

        HhItem01 item = null;

        cmpnyNo = cmpnyNo.replace("'", "''");
        number = number.replace("'", "''");
        uom = uom.replace("'", "''");

        cursor = db
                .rawQuery(
                        "select number,description,lastunitcost,currency,locuom,qtyonhand,costingmethod,altitem,altitemdesc,locid,stockinguom from hhItem01 where number = '"
                                + number
                                + "' and locuom = '"
                                + uom
                                + "' and companycode = '" + cmpnyNo +
                                /* "' and locid = '" + locId + */
                                "'", null);

        try {
            while (cursor.moveToNext()) {
                item = new HhItem01();
                item.setHhItem_number(cursor.getString(0));
                item.setHhItem_description(cursor.getString(1));
                item.setHhItem_last_unit_cost(Double.parseDouble(cursor
                        .getString(2)));
                item.setHhItem_currency(cursor.getString(3));
                item.setHhItem_loc_uom(cursor.getString(4));
                item.setHhItem_qty_on_hand(Double.parseDouble(cursor
                        .getString(5)));
                item.setHhItem_costing_method(cursor.getString(6));
                item.setHhItem_alt_itm(cursor.getString(7));
                item.setHhItem_alt_itm_desc(cursor.getString(8));
                item.setHhItem_loc_id(cursor.getString(9));
                item.setHhItem_stocking_uom(cursor.getString(10));
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 803";
            msg = "Item Data Selection failed(Based on Item Number & UOM).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return item;
    }

    // to get price list code in itemInfo class
    public List<String> getPriceListFromPriceTable(String itemNo, String cmpnyNo) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        itemNo = itemNo.replace("'", "''");

        List<String> prcListCode = new ArrayList<String>();
        String priceListCode = "";
        cursor = db.rawQuery(
                "select pricelistcode from hhPrice01 where number = '" + itemNo
                        + "' and companycode = '" + cmpnyNo + "'", null);
        try {
            while (cursor.moveToNext()) {
                priceListCode = cursor.getString(0);

                prcListCode.add(priceListCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 609";
            msg = "Price List Code Selection failed(Based on Item Number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return prcListCode;
    }

    // to get price using pricelistcode
    public double getCurrentPrice(String locId, String currentPriceListCode,
                                  String itemNo, String cmpnyNo) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        itemNo = itemNo.replace("'", "''");
        locId = locId.replace("'", "''");
        currentPriceListCode = currentPriceListCode.replace("'", "''");

        double price = 0.0;

        cursor = db.rawQuery("select price1 from hhPrice01 where location ='"
                + locId + "' and pricelistcode = '" + currentPriceListCode
                + "' and number = '" + itemNo + "' and companycode = '"
                + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                price = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 610";
            msg = "Price Getting failed(Based on currentpricelistcode and item number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return price;
    }

    // to get customer group code
    public List<String> getCustomerGroupCode(String cmpnyNo) {

        List<String> groupCode = new ArrayList<String>();
        String strGroupCode = "";
        cmpnyNo = cmpnyNo.replace("'", "''");


        cursor = db.rawQuery(
                "select groupcode from hhCustomerGroup01 where companycode = '" + cmpnyNo + "'", null);
        try {
            while (cursor.moveToNext()) {
                strGroupCode = cursor.getString(0);

                groupCode.add(strGroupCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 611";
            msg = "Customer Groupcode selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return groupCode;
    }

    // to get customer terms code
    public String getCustomerTerms(String cusGroupCode, String cmpnyNo) {

        String term = "";

        cmpnyNo = cmpnyNo.replace("'", "''");
        cusGroupCode = cusGroupCode.replace("'", "''");

        cursor = db.rawQuery(
                "select terms from hhCustomerGroup01 where groupcode = '"
                        + cusGroupCode + "' and companycode = '" + cmpnyNo
                        + "' ", null);
        try {
            while (cursor.moveToNext()) {
                term = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 612";
            msg = "Terms selection failed(Based on customer groupcode).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return term;
    }

    // to get list of pricelist
    public List<String> getPriceList(String cmpnyNo) {

        List<String> prcListCode = new ArrayList<String>();
        String priceListCode = "";
        cmpnyNo = cmpnyNo.replace("'", "''");

        cursor = db.rawQuery(
                "select pricelist from HhPriceListMaster01 where companycode = '"
                        + cmpnyNo + "' ", null);

        try {
            while (cursor.moveToNext()) {
                priceListCode = cursor.getString(0);
                prcListCode.add(priceListCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 613";
            msg = "Price list selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return prcListCode;
    }

    // to get list of receiptType
    public List<String> getPayTypeList(String cmpnyNo) {

        List<String> paymentType = new ArrayList<String>();
        String paymentTypeCode = "";

        cmpnyNo = cmpnyNo.replace("'", "''");

        cursor = db.rawQuery(
                "select receipttype from HhReceiptType01 where companycode = '"
                        + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                paymentTypeCode = cursor.getString(0);
                paymentType.add(paymentTypeCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 804";
            msg = "Payment Type selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        paymentType.add("CHARGE");

        return paymentType;
    }

    // to get ship to location details
    public List<String> getShipToLocDet(String custNo, String cmpnyNo) {

        List<String> locDetailList = new ArrayList<String>();

        cmpnyNo = cmpnyNo.replace("'", "''");
        custNo = custNo.replace("'", "''");
        locDetailList.add("DEFAULT"); // To set default in first position

        String locDetail = "";

        cursor = db.rawQuery(
                "select primaryshipto,locCode from hhShipTo01 where customernumber='"
                        + custNo + "' and companycode = '" + cmpnyNo + "' ",
                null);
        try {
            while (cursor.moveToNext()) {
                int swapInt = cursor.getInt(0);

                locDetail = cursor.getString(1);
                locDetailList.add(locDetail);

                // to swap default location to first item in list.
                if (swapInt == 1) {
                    int cIndex = locDetailList.indexOf(locDetail);
                    Collections.swap(locDetailList, cIndex, 1);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 614";
            msg = "ShipTo and Location getting failed(Based on customer number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return locDetailList;
    }

    // to get ship via details
    public List<String> getShipViaList(String cmpnyNo, String uomcode) {

        List<String> shipViaList = new ArrayList<String>();
        cursor = db.rawQuery(
                "select code,name from hhShipVia01 where companycode = '"
                        + cmpnyNo + "'", null);

        String shipViaStr = "";

            cmpnyNo = cmpnyNo.replace("'", "''");
        uomcode = uomcode.replace("'", "''");

        try {
            while (cursor.moveToNext()) {

                shipViaStr = cursor.getString(0) + "  " + cursor.getString(1);

                if (shipViaStr.equals("  ")) {
                    if (!shipViaList.contains("DEFAULT")) {
                        shipViaList.add("DEFAULT"); // To set default in first
                        // position
                    }
                } else {
                    shipViaList.add(shipViaStr.trim());
                }
            }
            if (shipViaList.size() == 0) {
                shipViaList.add("DEFAULT"); // To set default in first
                // position
            } else {
                if (!uomcode.equals("")) {
                    for (int i = 0; i < shipViaList.size(); i++) {

                        if (shipViaList.get(i).contains(uomcode)) {
                            int cIndex = shipViaList
                                    .indexOf(shipViaList.get(i));
                            Collections.swap(shipViaList, cIndex, 0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 615";
            msg = "ShipVia selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return shipViaList;
    }

    // to get uom
    public List<String> getUOM(String itmNo, String cmpnyNo, String itemUOM) {

        List<String> uomList = new ArrayList<String>();
        cursor = db.rawQuery(
                "select distinct uomname from hhConversionFactor01 where number = '"
                        + itmNo + "' and companycode = '" + cmpnyNo + "' ",
                null);
        String uomStr = "";
           cmpnyNo = cmpnyNo.replace("'", "''");
        itemUOM = itemUOM.replace("'", "''");


        try {
            while (cursor.moveToNext()) {
                uomStr = cursor.getString(0);
                uomList.add(uomStr);

                // to swap ea uom to first position in list
                if (uomStr.equals(itemUOM)) {
                    int cIndex = uomList.indexOf(uomStr);
                    Collections.swap(uomList, cIndex, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 616";
            msg = "UOM getting failed(Based on Item number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return uomList;
    }

    // to mspdb order or quote number
    public Mspdb getMspDbData(String cmpnyNo, String spCode) {

        Mspdb mspdb = null;

           cmpnyNo = cmpnyNo.replace("'", "''");
        spCode = spCode.replace("'", "''");

        cursor = db.rawQuery("select * from mspdb where companynumber = '"
                + cmpnyNo + "' and salesperson = '" + spCode + "' ", null);

        try {
            while (cursor.moveToNext()) {
                mspdb = new Mspdb();
                mspdb.setMspdb_companyNumber(cursor.getString(0));
                mspdb.setMspdb_salesPerson(cursor.getString(1));
                mspdb.setMspdb_mapNo(cursor.getInt(2));
                mspdb.setMspdb_customerNumber(cursor.getInt(3));
                mspdb.setMspdb_orderNumber(cursor.getInt(4));
                mspdb.setMspdb_quoteNumber(cursor.getInt(5));
                mspdb.setMspdb_lastinvno(cursor.getInt(6));
                mspdb.setMspdb_receiptnumber(cursor.getInt(7));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 617";
            msg = "MspDB data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return mspdb;
    }

    // to mspdb order or quote number
    public Mspdb getAdminMspDbData(String cmpnyNo) {

        Mspdb mspdb = null;

        cursor = db.rawQuery("select * from mspdb where companynumber = '"
                + cmpnyNo + "'", null);

         cmpnyNo = cmpnyNo.replace("'", "''");

        try {
            while (cursor.moveToNext()) {
                mspdb = new Mspdb();
                mspdb.setMspdb_companyNumber(cursor.getString(0));
                mspdb.setMspdb_salesPerson(cursor.getString(1));
                mspdb.setMspdb_mapNo(cursor.getInt(2));
                mspdb.setMspdb_customerNumber(cursor.getInt(3));
                mspdb.setMspdb_orderNumber(cursor.getInt(4));
                mspdb.setMspdb_quoteNumber(cursor.getInt(5));
                mspdb.setMspdb_lastinvno(cursor.getInt(6));
                mspdb.setMspdb_receiptnumber(cursor.getInt(7));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 653";
            msg = "MspDB data selection failed using administrator.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return mspdb;
    }

    public List<HhCompany> getCompanyDatas() {

        List<HhCompany> cmpnyList = new ArrayList<HhCompany>();

        HhCompany compny = null;

        cursor = db.rawQuery("select number,name from hhCompany", null);

        try {
            while (cursor.moveToNext()) {
                compny = new HhCompany();

                compny.setCompany_number(cursor.getString(0));
                compny.setCompany_name(cursor.getString(1));

                cmpnyList.add(compny);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 618";
            msg = "Company data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return cmpnyList;
    }

    public HhCompany getCompanyData(String cmpnyNo) {

        HhCompany compny = null;
 cmpnyNo = cmpnyNo.replace("'", "''");
        cursor = db
                .rawQuery(
                        "select number,name,address,city,state,zip,country,phone,contact,isstockinguom from hhCompany where number = '"
                                + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                compny = new HhCompany();
                compny.setCompany_number(cursor.getString(0));
                compny.setCompany_name(cursor.getString(1));
                compny.setCompany_address(cursor.getString(2));
                compny.setCompany_city(cursor.getString(3));
                compny.setCompany_state(cursor.getString(4));
                compny.setCompany_zip(cursor.getString(5));
                compny.setCompany_country(cursor.getString(6));
                compny.setCompany_phone(cursor.getString(7));
                compny.setCompany_contact(cursor.getString(8));
                compny.setCompany_isstockinguom(cursor.getInt(9));
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 619";
            msg = "Company data selection failed(Based on company number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return compny;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+TRANS_STATUS+" FROM "+TRANS_TABLE+" WHERE STATUS = 1";


        Cursor data = db.rawQuery(query,null);
        return data;

    }
    public void getTransData(String result) {
        if (result.equals("success")) {
           HhHistory01 history01 = null;


//          cursor =  db.rawQuery("INSERT INTO " + HISTORY_TABLE + "(transtype,referencenumber,invoicenumber,ordernumber,transday,transmonth,transyear,expshipday,expshipmonth,expshipyear,customernumber,salesperson,itemnumber,locid,terms,currency,pricelistcode,uom,qty,price,discprice,netprice,extprice,tax,shiptocode,status,lineitem,discvalue,disctype,ordshipday,ordshipmonth,ordshipyear,editable,manitemno,pretax,refno,orderdiscount,taxamount,totalcarton,editedcustomername,itemunitweight,companycode,latitude,longitude) SELECT transtype,referencenumber,invoicenumber,ordernumber,transday,transmonth,transyear,expshipday,expshipmonth,expshipyear,customernumber,salesperson,itemnumber,locid,terms,currency,pricelistcode,uom,qty,price,discprice,netprice,extprice,tax,shiptocode,status,lineitem,discvalue,disctype,ordshipday,ordshipmonth,ordshipyear,editable,manitemno,pretax,refno,orderdiscount,taxamount,totalcarton,editedcustomername,itemunitweight,companycode,latitude,longitude FROM " + TRANS_TABLE + " WHERE "+TRANS_STATUS+" = 1", null);

               cursor =  db.rawQuery("SELECT * FROM "+TRANS_TABLE+" WHERE "+TRANS_STATUS+"= 1",null);
            try {
                while (cursor.moveToNext()) {
                    history01 = new HhHistory01();



                    history01.setHhTran_transType_new(cursor.getString(0));
                    history01.setHhTran_referenceNumber_new(cursor.getString(1));
                    history01.setHhTran_invoiceNumber_new(cursor.getString(2));
                    history01.setHhTran_orderNumber_new(cursor.getString(3));
                    history01.setHhTran_transDay_new(cursor.getInt(4));
                    history01.setHhTran_transMonth_new(cursor.getInt(5));
                    history01.setHhTran_transYear_new(cursor.getInt(6));
                    history01.setHhTran_expShipDay_new(cursor.getInt(7));
                    history01.setHhTran_expShipMonth_new(cursor.getInt(8));
                    history01.setHhTran_expShipYear_new(cursor.getInt(9));
                    history01.setHhTran_customerNumber_new(cursor.getString(10));
                    history01.setHhTran_salesPerson_new(cursor.getString(11));
                    history01.setHhTran_itemNumber_new(cursor.getString(12));
                    history01.setHhTran_locId_new(cursor.getString(13));
                    history01.setHhTran_terms_new(cursor.getString(14));
                    history01.setHhTran_currency_new(cursor.getString(15));
                    history01.setHhTran_priceListCode_new(cursor.getString(16));
                    history01.setHhTran_uom_new(cursor.getString(17));
                    history01.setHhTran_qty_new(cursor.getInt(19)); // put float
                    history01.setHhTran_price_new(cursor.getDouble(20));
                    history01.setHhTran_discPrice_new(cursor.getInt(21));
                    history01.setHhTran_netPrice_new(cursor.getDouble(22));
                    history01.setHhTran_extPrice_new(cursor.getDouble(23));
                    history01.setHhTran_tax_new(cursor.getDouble(24));
                    history01.setHhTran_shipToCode_new(cursor.getString(24));
                    history01.setHhTran_shipViaCode_new(cursor.getString(25));
                    history01.setHhTran_status_new(cursor.getInt(26));
                    history01.setHhTran_lineItem_new(cursor.getInt(27));
                    history01.setHhTran_discValue_new(cursor.getInt(28));
                    history01.setHhTran_discType_new(cursor.getString(29));
                    history01.setHhTran_ordShipDay_new(cursor.getInt(30));
                    history01.setHhTran_ordShipMonth_new(cursor.getInt(31));
                    history01.setHhTran_ordShipYear_new(cursor.getInt(32));
                    history01.setHhTran_editable_new(cursor.getInt(33));
                    history01.setHhTran_manItemNo_new(cursor.getString(34));
                    history01.setHhTran_preTax_new(cursor.getInt(35));
                    history01.setHhTran_refNo_new(cursor.getString(36));
                    history01.setHhTran_orderdiscount_new(cursor.getInt(37));
                    history01.setHhTran_taxamount_new(cursor.getInt(38));
                    history01.setHhTran_totalcarton_new(cursor.getInt(39));
                    history01.setHhTran_editedcustomername_new(cursor.getString(40));
                    history01.setHhTran_itemunitweight_new(cursor.getInt(41));
                    history01.setHhTran_companycode_new(cursor.getString(42));
                    history01.setHhTran_lat_new(cursor.getInt(43));
                    history01.setHhTran_lon_new(cursor.getInt(44));

                    this.addHistory(history01);

                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        }

            cursor.close();


        }






    public HhManager getManagerData(String uName, String cmpnyNo) {

        HhManager manager = null;

         cmpnyNo = cmpnyNo.replace("'", "''");
        uName = uName.replace("'", "''");


        cursor = db.rawQuery("select * from hhManager where userid = '" + uName
                + "' and companynumber = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {

                manager = new HhManager();

                manager.setHhManager_companyid(cursor.getString(0));
                manager.setHhManager_userid(cursor.getString(1));
                manager.setHhManager_userpass(cursor.getString(2));
                manager.setHhManager_activekey(cursor.getString(3));
                manager.setHhManager_username(cursor.getString(4));
                manager.setHhManager_locid(cursor.getString(5));
                manager.setHhManager_email(cursor.getString(6));
                manager.setHhManager_currency(cursor.getString(7));
                manager.setHhManager_pricelistcode(cursor.getString(8));
                manager.setHhManager_companynumber(cursor.getString(9));
                manager.setHhManager_companyname(cursor.getString(10));
                manager.setHhManager_companymailid(cursor.getString(11));
                manager.setHhManager_unitpriceeditable(cursor.getInt(12));
                manager.setHhManager_accpacversion(cursor.getString(13));
                manager.setHhManager_addcustomerstate(cursor.getInt(14));
                manager.setHhManager_customerbasedpl(cursor.getInt(15));
                manager.setHhManager_adminpass(cursor.getString(16));
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 620";
            msg = "Manager data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return manager;
    }

    public HhManager getAdminData(String aPwd, String cmpnyNo) {

        HhManager manager = null;

        cmpnyNo = cmpnyNo.replace("'", "''");
        aPwd = aPwd.replace("'", "''");

        cursor = db.rawQuery("select * from hhManager where adminpass = '"
                + aPwd + "' and companynumber = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {

                manager = new HhManager();

                manager.setHhManager_companyid(cursor.getString(0));
                manager.setHhManager_userid(cursor.getString(1));
                manager.setHhManager_userpass(cursor.getString(2));
                manager.setHhManager_activekey(cursor.getString(3));
                manager.setHhManager_username(cursor.getString(4));
                manager.setHhManager_locid(cursor.getString(5));
                manager.setHhManager_email(cursor.getString(6));
                manager.setHhManager_currency(cursor.getString(7));
                manager.setHhManager_pricelistcode(cursor.getString(8));
                manager.setHhManager_companynumber(cursor.getString(9));
                manager.setHhManager_companyname(cursor.getString(10));
                manager.setHhManager_companymailid(cursor.getString(11));
                manager.setHhManager_unitpriceeditable(cursor.getInt(12));
                manager.setHhManager_accpacversion(cursor.getString(13));
                manager.setHhManager_addcustomerstate(cursor.getInt(14));
                manager.setHhManager_customerbasedpl(cursor.getInt(15));
                manager.setHhManager_adminpass(cursor.getString(16));
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 652";
            msg = "Getting Admin Data failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return manager;
    }

    // to get uom conv factor
    public double getUOMConvFactor(String itemNum, String uom, String cmpnyNo) {

        double convFact = 0.0;

         cmpnyNo = cmpnyNo.replace("'", "''");
        itemNum = itemNum.replace("'", "''");
        uom = uom.replace("'", "''");

        cursor = db.rawQuery(
                "select uomconvfactor from hhConversionFactor01 where number = '"
                        + itemNum + "' and uomname = '" + uom
                        + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {

                convFact = cursor.getDouble(0);

            }

        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when getting uom convention factor data.");
            e.printStackTrace();
            errCode = "Error 621";
            msg = "ConversionFactor selection failed(Based on item number and uom).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return convFact;
    }

    // to get pending payments
    public List<HhPayment01> getPendingPayments(String cusNo, String cmpnyNo) {

        List<HhPayment01> tempPaymentList = new ArrayList<HhPayment01>();

        cmpnyNo = cmpnyNo.replace("'", "''");
        cusNo = cusNo.replace("'", "''");

        cursor = db
                .rawQuery(
                        "select * from hhPayment01 where cusnumber='"
                                + cusNo
                                + "' and pendingbalance > 0 and (pendingbalance-appliedamount !=0) and companycode = '"
                                + cmpnyNo + "'", null);
        HhPayment01 payment = null;

        try {
            while (cursor.moveToNext()) {
                payment = new HhPayment01();
                String status = cursor.getString(0);
                if (status.equals("Yes")) {
                    payment.setHhPayment_apply1("Pending"); // change yes to
                    // pending
                    payment.setHhPayment_pendingbalance(cursor.getFloat(6)); // assign
                    // netamout
                    // value
                    // if
                    // pending
                    // status
                    // for
                    // logical
                    // need.
                } else {
                    payment.setHhPayment_apply1(status);
                    payment.setHhPayment_pendingbalance(cursor.getFloat(4));
                }

                payment.setHhPayment_cusnumber(cursor.getString(1));
                payment.setHhPayment_cusname(cursor.getString(2));
                payment.setHhPayment_documentnumber(cursor.getString(3));
                payment.setHhPayment_appliedamount("0.00"); // anytime this
                // field is
                // 0
                payment.setHhPayment_netamount(cursor.getFloat(6));
                payment.setHhPayment_status(cursor.getInt(7));
                payment.setHhPayment_companycode(cursor.getString(8));

                tempPaymentList.add(payment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 622";
            msg = "Pendingpayments selection failed(Based on customer number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempPaymentList;
    }

    // to get pending balance
    public double getPendingBalance(String cusNo, String cmpnyNo) {

        double pendingBalance = 0.0;
        cmpnyNo = cmpnyNo.replace("'", "''");
        cusNo = cusNo.replace("'", "''");


        cursor = db
                .rawQuery(
                        "select pendingbalance from hhPayment01 where cusnumber='"
                                + cusNo + "' and companycode = '" + cmpnyNo
                                + "'", null);

        try {
            while (cursor.moveToNext()) {
                pendingBalance = pendingBalance + (double) cursor.getFloat(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 623";
            msg = "Pending balance selection failed(Based on customer number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return pendingBalance;
    }

    // to get payment mode
    public List<String> getReceiptType(String cmpnyNo) {

           cmpnyNo = cmpnyNo.replace("'", "''");

        List<String> paymentModeList = new ArrayList<String>();

        cursor = db.rawQuery(
                "select * from hhReceiptType01 where companycode = '" + cmpnyNo
                        + "'", null);
        String paymentmode = null;

        try {
            while (cursor.moveToNext()) {
                paymentmode = cursor.getString(0);
                paymentModeList.add(paymentmode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 624";
            msg = "Receipt Type selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return paymentModeList;
    }

    public String getReciptName(String receiptType, String cmpnyNo) {
        String receiptName = "";
           cmpnyNo = cmpnyNo.replace("'", "''");
        receiptType = receiptType.replace("'", "''");

        cursor = db
                .rawQuery(
                        "select * from hhReceiptType01 where receipttype = '"
                                + receiptType + "' and companycode = '"
                                + cmpnyNo + "'", null);
        try {
            while (cursor.moveToNext()) {
                receiptName = cursor.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 625";
            msg = "Receipt name selection failed(Based on receipt type).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return receiptName;
    }

    // to get tax group
    public List<String> getTaxGroup(String cmpnyNo) {
        List<String> taxGroupList = new ArrayList<String>();

                 cmpnyNo = cmpnyNo.replace("'", "''");

        cursor = db.rawQuery(
                "select taxgroup from hhTaxGroup01 where companycode = '"
                        + cmpnyNo + "'", null);
        String taxGroup = null;

        try {
            while (cursor.moveToNext()) {
                taxGroup = cursor.getString(0);
                taxGroupList.add(taxGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 626";
            msg = "Tax Group selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return taxGroupList;
    }

    public HhTaxGroup01 getTaxGroupAuthority(String strTaxGrp, String cmpnyNo) {

        HhTaxGroup01 taxGroupObj = null;

                 cmpnyNo = cmpnyNo.replace("'", "''");
        strTaxGrp = strTaxGrp.replace("'", "''");

        cursor = db.rawQuery("select * from hhTaxGroup01 where taxgroup = '"
                + strTaxGrp + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                taxGroupObj = new HhTaxGroup01();
                taxGroupObj.setHhTaxGroup_taxgroup(cursor.getString(0));
                taxGroupObj.setHhTaxGroup_taxauthority1(cursor.getString(1));
                taxGroupObj.setHhTaxGroup_taxstts1(cursor.getString(2));
                taxGroupObj.setHhTaxGroup_taxable1(cursor.getInt(3));
                taxGroupObj.setHhTaxGroup_taxauthority2(cursor.getString(4));
                taxGroupObj.setHhTaxGroup_taxstts2(cursor.getString(5));
                taxGroupObj.setHhTaxGroup_taxable2(cursor.getInt(6));
                taxGroupObj.setHhTaxGroup_taxauthority3(cursor.getString(7));
                taxGroupObj.setHhTaxGroup_taxstts3(cursor.getString(8));
                taxGroupObj.setHhTaxGroup_taxable3(cursor.getInt(9));
                taxGroupObj.setHhTaxGroup_taxauthority4(cursor.getString(10));
                taxGroupObj.setHhTaxGroup_taxstts4(cursor.getString(11));
                taxGroupObj.setHhTaxGroup_taxable4(cursor.getInt(12));
                taxGroupObj.setHhTaxGroup_taxauthority5(cursor.getString(13));
                taxGroupObj.setHhTaxGroup_taxstts5(cursor.getString(14));
                taxGroupObj.setHhTaxGroup_taxable5(cursor.getInt(15));
                taxGroupObj.setHhTaxGroup_companycode(cursor.getString(16));
            }
        } catch (Exception e) {
            db.endTransaction();
            Log.e("TaxGroup selection Failure", "TaxGroup selection failure.");
            e.printStackTrace();
            errCode = "Error 639";
            msg = "TaxGroup selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return taxGroupObj;
    }

    // to get tax state and county list
    public List<String> getTaxAuthority(String authority, String cmpnyNo) {

           cmpnyNo = cmpnyNo.replace("'", "''");
        authority = authority.replace("'", "''");

        List<String> taxAuthority = new ArrayList<String>();

        cursor = db.rawQuery(
                "select * from hhTaxClass01 where taxauthority = '" + authority
                        + "' and companycode = '" + cmpnyNo + "'", null);
        String taxState = null;

        try {
            while (cursor.moveToNext()) {
                taxState = cursor.getString(1) + " " + cursor.getString(2);
                taxAuthority.add(taxState);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 627";
            msg = "Tax Authority selection failed(Based on authority).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return taxAuthority;
    }

    // to get all temporary items
    /*
	 * public List<TempItem> getTempItems(String expDate) {
	 *
	 * List<TempItem> tempItemList = new ArrayList<TempItem>();
	 *
	 * cursor = db.rawQuery("select * from tempitem", null); TempItem tempitem =
	 * null;
	 *
	 * try { while (cursor.moveToNext()) { tempitem = new TempItem();
	 * tempitem.setTemp_itemNum(cursor.getString(0));
	 * tempitem.setTemp_qty(cursor.getString(1));
	 * tempitem.setTemp_extPrice(cursor.getString(2));
	 * tempitem.setTemp_location(cursor.getString(3));
	 * tempitem.setTemp_uom(cursor.getString(4));
	 * tempitem.setTemp_date(expDate);
	 * tempitem.setTemp_entryType(cursor.getString(6));
	 * tempitem.setTemp_discount(cursor.getString(7));
	 * tempitem.setTemp_discType(cursor.getString(8));
	 *
	 * tempItemList.add(tempitem); } } catch (Exception e) {
	 * e.printStackTrace(); errCode = "Error 628"; msg =
	 * "Temp Items selection failed."; errMsg = errCode + " : " + msg;
	 * LogfileCreator.appendLog(errMsg); db.close(); }
	 *
	 * cursor.close();
	 *
	 * return tempItemList; }
	 */

    public List<TempItem> getTempItems() {

        List<TempItem> tempItemList = new ArrayList<TempItem>();

        cursor = db.rawQuery("select * from tempitem", null);
        TempItem tempitem = null;

        try {
            while (cursor.moveToNext()) {
                tempitem = new TempItem();
                tempitem.setTemp_itemNum(cursor.getString(0));
                tempitem.setTemp_qty(cursor.getString(1));
                tempitem.setTemp_extPrice(cursor.getString(2));
                tempitem.setTemp_location(cursor.getString(3));
                tempitem.setTemp_uom(cursor.getString(4));
                tempitem.setTemp_date(cursor.getString(5));
                tempitem.setTemp_entryType(cursor.getString(6));
                tempitem.setTemp_discount(cursor.getString(7));
                tempitem.setTemp_discType(cursor.getString(8));

                tempItemList.add(tempitem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 628";
            msg = "Temp Items selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempItemList;
    }

    public Manf_Number01 getManufItemDetail(String itemNumber, String cmpnyNo) {

         cmpnyNo = cmpnyNo.replace("'", "''");
        itemNumber = itemNumber.replace("'", "''");

        Manf_Number01 manufDet = null;

        cursor = db.rawQuery("select * from manf_number01 where itemno = '"
                + itemNumber + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                manufDet = new Manf_Number01();
                manufDet.setManuf_manitemno(cursor.getString(0));
                manufDet.setManuf_itemno(cursor.getString(1));
                manufDet.setManuf_uom(cursor.getString(2));
                manufDet.setCompany_code(cursor.getString(3));
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 629";
            msg = "Manf_Item Detail selection failed(Based on item number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return manufDet;
    }

    // to get all available list from location master table
    public List<String> getAvailableLocation(String cmpnyNo) {

        List<String> locList = new ArrayList<String>();
        cmpnyNo = cmpnyNo.replace("'", "''");


        cursor = db.rawQuery("select * from hhLocMast01 where companycode = '"
                + cmpnyNo + "' ", null);
        String loc = null;

        try {
            while (cursor.moveToNext()) {
                loc = cursor.getString(0);
                locList.add(loc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 630";
            msg = "Location selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return locList;
    }

    public List<HhItem01> getInventoryItemDatas(List<HhItem01> itemList,
                                                String locSpnData, String cmpnyNo) {

        List<HhItem01> tempItemList = new ArrayList<HhItem01>();

        cmpnyNo = cmpnyNo.replace("'", "''");
        locSpnData = locSpnData.replace("'", "''");

        cursor = db.rawQuery(
                "select number,description,qtyonhand from hhitem01 where locid = '"
                        + locSpnData + "' and companycode = '" + cmpnyNo + "'",
                null);

        HhItem01 item = null;
        try {
            while (cursor.moveToNext()) {
                item = new HhItem01();

                item.setHhItem_number(cursor.getString(0));
                item.setHhItem_description(cursor.getString(1));
                item.setHhItem_qty_on_hand(Double.parseDouble(cursor
                        .getString(2)));

                tempItemList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 631";
            msg = "InventoryItem Datas selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempItemList;
    }

    public List<List<String>> getItemTaxstts(String itemNumber,
                                             List<String> auth, String cmpnyNo) {


        cmpnyNo = cmpnyNo.replace("'", "''");
        itemNumber = itemNumber.replace("'", "''");

        List<List<String>> itmTxblLst = new ArrayList<List<String>>();

        List<String> authItm = new ArrayList<String>();
        List<String> txbleSttsItm = new ArrayList<String>();

        cursor = db.rawQuery("select * from hhItemTax01 where itemnumber = '"
                + itemNumber + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                String authority = cursor.getString(1);

                for (int i = 0; i < auth.size(); i++) {
                    String fAuth = auth.get(i);
                    if (fAuth.equals(authority)) {
                        authItm.add(fAuth);
                        txbleSttsItm.add(cursor.getString(2));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 632";
            msg = "Item Taxstts data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        if (authItm.size() != 0) {
            itmTxblLst.add(authItm);
            itmTxblLst.add(txbleSttsItm);
        }

        cursor.close();

        return itmTxblLst;

    }

    // to get tax rate
    public double getTaxRate(String authority, String buyerValue,
                             String itmRateValue, String cmpnyNo) {


        cmpnyNo = cmpnyNo.replace("'", "''");
        itmRateValue = itmRateValue.replace("'", "''");
        authority = authority.replace("'", "''");

        double result = 0.0;

        cursor = db.rawQuery("select itemrate" + itmRateValue
                + " from hhTaxRate01 where authority = '" + authority
                + "' and buyerclass = '" + buyerValue + "' and companycode = '"
                + cmpnyNo + "'", null);
        try {
            while (cursor.moveToNext()) {

                result = Double.parseDouble(cursor.getString(0));

            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 633";
            msg = "Tax Rate selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return result;

    }

    // to get prepayment
    public double getPrepayAmount(String refno, String cmpnyNo) {

          cmpnyNo = cmpnyNo.replace("'", "''");
        refno = refno.replace("'", "''");


        double result = 0.00;

        cursor = db
                .rawQuery(
                        "select receiptamount from hhPrepayment01 where referencenumber='"
                                + refno + "' and companycode = '" + cmpnyNo
                                + "'", null);

        try {
            while (cursor.moveToNext()) {

                result = Double.parseDouble(cursor.getString(0));

            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 634";
            msg = "Prepayment selection failed(Based on reference number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return result;

    }

    // to get prepaymentReceiptNo
    public String getPrepayReceiptNo(String refno, String cmpnyNo) {


                cmpnyNo = cmpnyNo.replace("'", "''");
        refno = refno.replace("'", "''");

        String receiptNum = "";

        cursor = db
                .rawQuery(
                        "select checkreceiptno from hhPrepayment01 where referencenumber='"
                                + refno + "' and companycode = '" + cmpnyNo
                                + "'", null);

        try {
            while (cursor.moveToNext()) {

                receiptNum = cursor.getString(0);

            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 805";
            msg = "Prepayment Check/Receiptno selection failed(Based on reference number).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return receiptNum;

    }

    // to get setting data
    public HhSetting getSettingData() {

        HhSetting setting = null;

        cursor = db.rawQuery("select * from hhSetting ", null);

        try {
            while (cursor.moveToNext()) {
                setting = new HhSetting();

                setting.setHhSetting_numcopiesprint(cursor.getInt(0));
                setting.setHhSetting_itemautosave(cursor.getInt(1));
                setting.setHhSetting_numofpiecedic(cursor.getInt(2));
                setting.setHhSetting_discprice(cursor.getDouble(3));
                setting.setHhSetting_dateformat(cursor.getString(4));
                setting.setHhSetting_decimalformat(cursor.getString(5));
                setting.setHhSetting_datasyncservice(cursor.getString(6));
                setting.setHhSetting_mspserverpath(cursor.getString(7));
                setting.setHhSetting_printerName(cursor.getString(8));
                setting.setHhSetting_printerModel(cursor.getString(9));
                setting.setHhSetting_showship(cursor.getInt(10));
                setting.setHhSetting_showprepay(cursor.getInt(11));
                setting.setHhSetting_autoreportgen(cursor.getInt(12));
                setting.setHhSetting_spCode(cursor.getString(13));
                setting.setHhSetting_cusnameeditable(cursor.getInt(14));
                setting.setHhSetting_nonstockitem(cursor.getInt(15));
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 635";
            msg = "Setting data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return setting;
    }

    // to get signature
    public byte[] getSignature(String number, String cmpnyNo) {

                cmpnyNo = cmpnyNo.replace("'", "''");
        number = number.replace("'", "''");

        cursor = db.rawQuery(
                "select signature from hhSignature where refernumber = '"
                        + number + "' and companycode = '" + cmpnyNo + "'",
                null);
        byte[] imgbyte = null;
        try {
            while (cursor.moveToNext()) {
                imgbyte = cursor.getBlob(0);
            }
        } catch (Exception e) {
            errCode = "Error 703";
            msg = "Signature data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return imgbyte;
    }

    // get temp item by item number
    public List<TempItem> getTempItems(String itmNo, String locId) {

           itmNo = itmNo.replace("'", "''");
        locId = locId.replace("'", "''");



        List<TempItem> tempItemList = new ArrayList<TempItem>();

        cursor = db.rawQuery("select * from tempitem where number ='" + itmNo
                + "' and location = '" + locId + "'", null);
        TempItem tempitem = null;
        try {
            while (cursor.moveToNext()) {
                tempitem = new TempItem();
                tempitem.setTemp_itemNum(cursor.getString(0));
                tempitem.setTemp_qty(cursor.getString(1));
                tempitem.setTemp_extPrice(cursor.getString(2));
                tempitem.setTemp_location(cursor.getString(3));
                tempitem.setTemp_uom(cursor.getString(4));
                tempitem.setTemp_date(cursor.getString(5));
                tempitem.setTemp_entryType(cursor.getString(6));

                tempItemList.add(tempitem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 636";
            msg = "Temp Items selection failed(Based on Item No. and Location id).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempItemList;
    }

    // to get transaction summary datas
    public List<HhTran01> getTransactionSummaryDatas(int fromDay,
                                                     int fromMonth, int fromYear, int toDay, int toMonth, int toYear,
                                                     String cmpnyNo) {

           cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhTran01> tempTransList = new ArrayList<HhTran01>();

        DecimalFormat formatter = new DecimalFormat("00");

        String strFDay = String.valueOf(fromDay);
        String strFMonth = String.valueOf(fromMonth);
        String strTDay = String.valueOf(toDay);
        String strTMonth = String.valueOf(toMonth);

        if (fromDay < 10) {
            strFDay = formatter.format(fromDay);
        }

        if (fromMonth < 10) {
            strFMonth = formatter.format(fromMonth);
        }

        if (toDay < 10) {
            strTDay = formatter.format(toDay);
        }

        if (toMonth < 10) {
            strTMonth = formatter.format(toMonth);
        }

        String query = "select distinct referencenumber,transday,transmonth,transyear,customernumber,status from hhTran01 where (10000*transyear+100*transmonth+transday >= "
                + fromYear
                + strFMonth
                + strFDay
                + " and 10000*transyear+100*transmonth+transday <= "
                + toYear
                + strTMonth + strTDay + ") and companycode = '" + cmpnyNo + "'";
        Log.i("Query: ", query);

        cursor = db.rawQuery(query, null);

        HhTran01 tranSummary = null;

        try {
            while (cursor.moveToNext()) {
                tranSummary = new HhTran01();
                tranSummary.setHhTran_referenceNumber(cursor.getString(0));
                tranSummary.setHhTran_transDay(cursor.getInt(1));
                tranSummary.setHhTran_transMonth(cursor.getInt(2));
                tranSummary.setHhTran_transYear(cursor.getInt(3));
                tranSummary.setHhTran_customerNumber(cursor.getString(4));
                tranSummary.setHhTran_status(cursor.getInt(5));
               // tranSummary.setHhComments(cursor.getString(6));

                tempTransList.add(tranSummary);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 637";
            msg = "Transaction Summary Data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempTransList;
    }

    // to get transaction summary datas
    public List<HhReceipt01> getReceiptSummaryDatas(int fromDay, int fromMonth,
                                                    int fromYear, int toDay, int toMonth, int toYear, String cmpnyNo) {

         cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhReceipt01> tempReceiptList = new ArrayList<HhReceipt01>();

        DecimalFormat formatter = new DecimalFormat("00");

        String strFDay = String.valueOf(fromDay);
        String strFMonth = String.valueOf(fromMonth);
        String strTDay = String.valueOf(toDay);
        String strTMonth = String.valueOf(toMonth);

        if (fromDay < 10) {
            strFDay = formatter.format(fromDay);
        }

        if (fromMonth < 10) {
            strFMonth = formatter.format(fromMonth);
        }

        if (toDay < 10) {
            strTDay = formatter.format(toDay);
        }

        if (toMonth < 10) {
            strTMonth = formatter.format(toMonth);
        }

        String query = "select distinct customernumber,customername,receipttype,amount,receiptnumber,receiptday,receiptmonth,receiptyear,receiptunapplied from hhReceipt01 where (10000*receiptyear+100*receiptmonth+receiptday >= "
                + fromYear
                + strFMonth
                + strFDay
                + " and 10000*receiptyear+100*receiptmonth+receiptday <= "
                + toYear
                + strTMonth
                + strTDay
                + ") and companycode = '"
                + cmpnyNo + "'";

        Log.i("Query: ", query);

        cursor = db.rawQuery(query, null);

        HhReceipt01 receiptSummary = null;

        try {
            while (cursor.moveToNext()) {
                receiptSummary = new HhReceipt01();
                receiptSummary.setHhReceipt_customernumber(cursor.getString(0));
                receiptSummary.setHhReceipt_customername(cursor.getString(1));
                receiptSummary.setHhReceipt_receipttype(cursor.getString(2));
                receiptSummary.setHhReceipt_amount(cursor.getDouble(3));
                receiptSummary.setHhReceipt_receiptnumber(cursor.getString(4));
                receiptSummary.setHhReceipt_receiptday(cursor.getInt(5));
                receiptSummary.setHhReceipt_receiptmonth(cursor.getInt(6));
                receiptSummary.setHhReceipt_receiptyear(cursor.getInt(7));
                receiptSummary.setHhReceipt_receiptunapplied(cursor
                        .getDouble(8));

                tempReceiptList.add(receiptSummary);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 657";
            msg = "Receipt Summary Data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempReceiptList;
    }

    // to get transaction summary datas
    public List<HhReceipt01> getReceiptListData(String cmpnyNo) {

         cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhReceipt01> tempReceiptList = new ArrayList<HhReceipt01>();

        String query = "select distinct customernumber,customername,receiptnumber from hhReceipt01 where companycode = '"
                + cmpnyNo + "'";

        Log.i("Query: ", query);

        cursor = db.rawQuery(query, null);

        HhReceipt01 receiptLists = null;

        try {
            while (cursor.moveToNext()) {
                receiptLists = new HhReceipt01();
                receiptLists.setHhReceipt_customernumber(cursor.getString(0));
                receiptLists.setHhReceipt_customername(cursor.getString(1));
                receiptLists.setHhReceipt_receiptnumber(cursor.getString(2));

                tempReceiptList.add(receiptLists);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 658";
            msg = "Receipt List Data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempReceiptList;
    }

    // to get preapayment summary datas
    public List<HhPrepayment01> getPrepaymentSalesSummaryDatas(int fromDay,
                                                               int fromMonth, int fromYear, int toDay, int toMonth, int toYear,
                                                               String payType, String cmpnyNo) {

         cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhPrepayment01> tempPrepayList = new ArrayList<HhPrepayment01>();

        DecimalFormat formatter = new DecimalFormat("00");

        String strFDay = String.valueOf(fromDay);
        String strFMonth = String.valueOf(fromMonth);
        String strTDay = String.valueOf(toDay);
        String strTMonth = String.valueOf(toMonth);

        if (fromDay < 10) {
            strFDay = formatter.format(fromDay);
        }

        if (fromMonth < 10) {
            strFMonth = formatter.format(fromMonth);
        }

        if (toDay < 10) {
            strTDay = formatter.format(toDay);
        }

        if (toMonth < 10) {
            strTMonth = formatter.format(toMonth);
        }

        String query = "select referencenumber,customernumber,receipttype,checkreceiptno,receiptamount from hhPrepayment01 where (10000*receiptyear+100*receiptmonth+receiptday >= "
                + fromYear
                + strFMonth
                + strFDay
                + " and 10000*receiptyear+100*receiptmonth+receiptday <= "
                + toYear
                + strTMonth
                + strTDay
                + ") and receipttype='"
                + payType + "' and companycode = '" + cmpnyNo + "'";

        Log.i("Query: ", query);

        cursor = db.rawQuery(query, null);

        HhPrepayment01 prepaySummary = null;

        try {
            while (cursor.moveToNext()) {
                prepaySummary = new HhPrepayment01();

                prepaySummary.setHhPrePayment_referenceNumber(cursor
                        .getString(0));
                prepaySummary.setHhPrePayment_customerNumber(cursor
                        .getString(1));
                prepaySummary.setHhPrePayment_receiptType(cursor.getString(2));
                prepaySummary.setHhPrePayment_checkReceiptNo(cursor
                        .getString(3));
                prepaySummary
                        .setHhPrePayment_receiptAmount(cursor.getDouble(4));

                tempPrepayList.add(prepaySummary);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 659";
            msg = "Prepayment Sales Summary Data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempPrepayList;
    }

    // to get transaction summary datas
    public List<HhTran01> getTranSalesSummaryDatas(int fromDay, int fromMonth,
                                                   int fromYear, int toDay, int toMonth, int toYear, String cmpnyNo) {

         cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhTran01> tempTransList = new ArrayList<HhTran01>();

        DecimalFormat formatter = new DecimalFormat("00");

        String strFDay = String.valueOf(fromDay);
        String strFMonth = String.valueOf(fromMonth);
        String strTDay = String.valueOf(toDay);
        String strTMonth = String.valueOf(toMonth);

        if (fromDay < 10) {
            strFDay = formatter.format(fromDay);
        }

        if (fromMonth < 10) {
            strFMonth = formatter.format(fromMonth);
        }

        if (toDay < 10) {
            strTDay = formatter.format(toDay);
        }

        if (toMonth < 10) {
            strTMonth = formatter.format(toMonth);
        }

        String query = "select distinct referencenumber,customernumber,(select sum(extprice) from hhTran01 where referencenumber = tran.referencenumber and transtype !='CN') as amount from hhTran01 as tran where (select count(referencenumber) from hhPrepayment01 where referencenumber = tran.referencenumber) = 0 and transtype != 'CN' "
                + "and (10000*transyear+100*transmonth+transday >= "
                + fromYear
                + strFMonth
                + strFDay
                + " and 10000*transyear+100*transmonth+transday <= "
                + toYear
                + strTMonth + strTDay + ") and companycode = '" + cmpnyNo + "'";

        Log.i("Query: ", query);

        cursor = db.rawQuery(query, null);

        HhTran01 tranSummary = null;

        try {
            while (cursor.moveToNext()) {
                tranSummary = new HhTran01();
                tranSummary.setHhTran_referenceNumber(cursor.getString(0));
                tranSummary.setHhTran_customerNumber(cursor.getString(1));
                tranSummary.setHhTran_extPrice(cursor.getDouble(2)); // here
                // total
                // of
                // the
                // extPrice
                // is
                // added.

                tempTransList.add(tranSummary);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 660";
            msg = "Transaction Sales Summary Data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempTransList;
    }

    // to get transaction summary datas
    public List<HhTran01> getTranLocationDatas(int fromDay, int fromMonth,
                                               int fromYear, String cmpnyNo) {

         cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhTran01> tempTransList = new ArrayList<HhTran01>();

        DecimalFormat formatter = new DecimalFormat("00");

        String strFDay = String.valueOf(fromDay);
        String strFMonth = String.valueOf(fromMonth);

        if (fromDay < 10) {
            strFDay = formatter.format(fromDay);
        }

        if (fromMonth < 10) {
            strFMonth = formatter.format(fromMonth);
        }

        String query = "select distinct referencenumber,latitude,longitude from hhTran01 "
                + "where transyear='" + fromYear + "' and transmonth='" + strFMonth + "' and transday='" + strFDay + "' and companycode = '"

                + cmpnyNo + "'";

        Log.i("Query: ", query);

        cursor = db.rawQuery(query, null);

        HhTran01 tranloc = null;

        try {
            while (cursor.moveToNext()) {
                tranloc = new HhTran01();
                tranloc.setHhTran_lat(cursor.getDouble(1));
                tranloc.setHhTran_lon(cursor.getDouble(2));
                tranloc.setHhTran_referenceNumber(cursor.getString(0));

                tempTransList.add(tranloc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 660";
            msg = "Transaction Location Summary Data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempTransList;
    }

    // to get transaction document records
    public List<HhTran01> getTransactionList(String refno, String cmpnyNo) {

         cmpnyNo = cmpnyNo.replace("'", "''");
         refno = refno.replace("'", "''");
        List<HhTran01> transactionList = new ArrayList<HhTran01>();

        cursor = db.rawQuery("select * from hhTran01 where referencenumber = '"
                + refno + "' and companycode = '" + cmpnyNo + "'", null);

        HhTran01 transData = null;

        try {
            while (cursor.moveToNext()) {
                transData = new HhTran01();
                transData.setHhTran_transType(cursor.getString(0));
                transData.setHhTran_referenceNumber(cursor.getString(1));
                transData.setHhTran_invoiceNumber(cursor.getString(2));
                transData.setHhTran_orderNumber(cursor.getString(3));
                transData.setHhTran_transDay(cursor.getInt(4));
                transData.setHhTran_transMonth(cursor.getInt(5));
                transData.setHhTran_transYear(cursor.getInt(6));
                transData.setHhTran_expShipDay(cursor.getInt(7));
                transData.setHhTran_expShipMonth(cursor.getInt(8));
                transData.setHhTran_expShipYear(cursor.getInt(9));
                transData.setHhTran_customerNumber(cursor.getString(10));
                transData.setHhTran_salesPerson(cursor.getString(11));
                transData.setHhTran_itemNumber(cursor.getString(12));
                transData.setHhTran_locId(cursor.getString(13));
                transData.setHhTran_terms(cursor.getString(14));
                transData.setHhTran_currency(cursor.getString(15));
                transData.setHhTran_priceListCode(cursor.getString(16));
                transData.setHhTran_uom(cursor.getString(17));
                transData.setHhTran_qty(cursor.getInt(18)); // need to change
                // float
                transData.setHhTran_price(cursor.getDouble(19));
                transData.setHhTran_discPrice(cursor.getDouble(20));
                transData.setHhTran_netPrice(cursor.getDouble(21));
                transData.setHhTran_extPrice(cursor.getDouble(22));
                transData.setHhTran_tax(cursor.getDouble(23));
                transData.setHhTran_shipToCode(cursor.getString(24));
                transData.setHhTran_shipViaCode(cursor.getString(25));
                transData.setHhTran_status(cursor.getInt(26));
                transData.setHhTran_lineItem(cursor.getInt(27));
                transData.setHhTran_discValue(cursor.getDouble(28));
                transData.setHhTran_discType(cursor.getString(29));
                transData.setHhTran_ordShipDay(cursor.getInt(30));
                transData.setHhTran_ordShipMonth(cursor.getInt(31));
                transData.setHhTran_ordShipYear(cursor.getInt(32));
                transData.setHhTran_editable(cursor.getInt(33));
                transData.setHhTran_manItemNo(cursor.getString(34));
                transData.setHhTran_preTax(cursor.getDouble(35));
                transData.setHhTran_refNo(cursor.getString(36));
                transData.setHhTran_orderdiscount(cursor.getDouble(37));
                transData.setHhTran_taxamount(cursor.getDouble(38));
                transData.setHhTran_totalcarton(cursor.getDouble(39));
                transData.setHhTran_editedcustomername(cursor.getString(40));
                transData.setHhTran_itemunitweight(cursor.getDouble(41));
                transData.setHhTran_companycode(cursor.getString(42));

                transactionList.add(transData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 638";
            msg = "Get All Transactions failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return transactionList;
    }

    // For Invoice Edit
    public HhTran01 getTransactionList_(String refno, String itemNum,
                                        String locId, String uom, String type) {

              itemNum = itemNum.replace("'", "''");
         refno = refno.replace("'", "''");
              locId = locId.replace("'", "''");
         uom = uom.replace("'", "''");
         type = type.replace("'", "''");

        String transType = "";
        if (type.equals("tr")) {
            transType = "CN";
        } else {
            transType = "I";
        }
        cursor = db.rawQuery("select * from hhTran01 where referencenumber = '"
                + refno + "' and locid='" + locId + "' and itemnumber='"
                + itemNum + "' and uom='" + uom + "' and transType='"
                + transType + "'", null);

        HhTran01 transData = null;

        try {
            while (cursor.moveToNext()) {
                transData = new HhTran01();
                transData.setHhTran_transType(cursor.getString(0));
                transData.setHhTran_referenceNumber(cursor.getString(1));
                transData.setHhTran_invoiceNumber(cursor.getString(2));
                transData.setHhTran_orderNumber(cursor.getString(3));
                transData.setHhTran_transDay(cursor.getInt(4));
                transData.setHhTran_transMonth(cursor.getInt(5));
                transData.setHhTran_transYear(cursor.getInt(6));
                transData.setHhTran_expShipDay(cursor.getInt(7));
                transData.setHhTran_expShipMonth(cursor.getInt(8));
                transData.setHhTran_expShipYear(cursor.getInt(9));
                transData.setHhTran_customerNumber(cursor.getString(10));
                transData.setHhTran_salesPerson(cursor.getString(11));
                transData.setHhTran_itemNumber(cursor.getString(12));
                transData.setHhTran_locId(cursor.getString(13));
                transData.setHhTran_terms(cursor.getString(14));
                transData.setHhTran_currency(cursor.getString(15));
                transData.setHhTran_priceListCode(cursor.getString(16));
                transData.setHhTran_uom(cursor.getString(17));
                transData.setHhTran_qty(cursor.getInt(18)); // need to change
                // float
                transData.setHhTran_price(cursor.getDouble(19));
                transData.setHhTran_discPrice(cursor.getDouble(20));
                transData.setHhTran_netPrice(cursor.getDouble(21));
                transData.setHhTran_extPrice(cursor.getDouble(22));
                transData.setHhTran_tax(cursor.getDouble(23));
                transData.setHhTran_shipToCode(cursor.getString(24));
                transData.setHhTran_shipViaCode(cursor.getString(25));
                transData.setHhTran_status(cursor.getInt(26));
                transData.setHhTran_lineItem(cursor.getInt(27));
                transData.setHhTran_discValue(cursor.getDouble(28));
                transData.setHhTran_discType(cursor.getString(29));
                transData.setHhTran_ordShipDay(cursor.getInt(30));
                transData.setHhTran_ordShipMonth(cursor.getInt(31));
                transData.setHhTran_ordShipYear(cursor.getInt(32));
                transData.setHhTran_editable(cursor.getInt(33));
                transData.setHhTran_manItemNo(cursor.getString(34));
                transData.setHhTran_preTax(cursor.getDouble(35));
                transData.setHhTran_refNo(cursor.getString(36));
				/*
				 * transData.setHhTran_createDateTime(cursor.getString(37));
				 * transData.setIsSchemePrice(cursor.getString(38));
				 */

            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 638_1";
            msg = "Get particular Transactions failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return transData;
    }

    // to get transaction document records
    public List<HhReceipt01> getReceiptListofData(String reciptNo,
                                                  String cmpnyNo) {

         reciptNo = reciptNo.replace("'", "''");
         cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhReceipt01> receiptList = new ArrayList<HhReceipt01>();

        cursor = db.rawQuery(
                "select * from hhReceipt01 where receiptnumber = '" + reciptNo
                        + "' and companycode = '" + cmpnyNo + "'", null);

        HhReceipt01 receiptData = null;

        try {
            while (cursor.moveToNext()) {
                receiptData = new HhReceipt01();

                receiptData.setHhReceipt_sno(cursor.getInt(0));
                receiptData.setHhReceipt_customernumber(cursor.getString(1));
                receiptData.setHhReceipt_customername(cursor.getString(2));
                receiptData.setHhReceipt_docnumber(cursor.getString(3));
                receiptData.setHhReceipt_pendingbal(cursor.getString(4));
                receiptData.setHhReceipt_currency(cursor.getString(5));
                receiptData.setHhReceipt_apply1(cursor.getString(6));
                receiptData.setHhReceipt_appliedamount(cursor.getDouble(7));
                receiptData.setHhReceipt_receipttype(cursor.getString(8));
                receiptData.setHhReceipt_amount(cursor.getDouble(9));
                receiptData.setHhReceipt_receiptnumber(cursor.getString(10));
                receiptData.setHhReceipt_receiptday(cursor.getInt(11));
                receiptData.setHhReceipt_receiptmonth(cursor.getInt(12));
                receiptData.setHhReceipt_receiptyear(cursor.getInt(13));
                receiptData.setHhReceipt_status(cursor.getInt(14));
                receiptData.setHhReceipt_receiptunapplied(cursor.getDouble(15));
                receiptData.setHhReceipt_customeramount(cursor.getDouble(16));
                receiptData.setHhReceipt_refno(cursor.getString(17));
                receiptData.setHhReceipt_companycode(cursor.getString(18));

                receiptList.add(receiptData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 658";
            msg = "Get All Receipts failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return receiptList;
    }

    public List<HhTran01> getTranDetForItemUpdate(String refno, String cmpnyNo) {

              refno = refno.replace("'", "''");
         cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhTran01> transactionList = new ArrayList<HhTran01>();

        cursor = db
                .rawQuery(
                        "select transtype,referencenumber,itemnumber,locid,uom,qty from hhTran01 where referencenumber = '"
                                + refno
                                + "' and companycode = '"
                                + cmpnyNo
                                + "'", null);

        HhTran01 transData = null;

        try {
            while (cursor.moveToNext()) {
                transData = new HhTran01();
                transData.setHhTran_transType(cursor.getString(0));/////////
                transData.setHhTran_referenceNumber(cursor.getString(1));//////////
                transData.setHhTran_itemNumber(cursor.getString(2));///////////
                transData.setHhTran_locId(cursor.getString(3));
                transData.setHhTran_uom(cursor.getString(4));
                transData.setHhTran_qty(cursor.getInt(5));
                transactionList.add(transData);
            }
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when getting tran item.");
            e.printStackTrace();
            errCode = "Error 639";
            msg = "Transaction for Item Update selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return transactionList;
    }

    public HhContractPrice01 getContractPrc(String custNum, String itemNum,
                                            String custPriceList, String companyCurrency, String cmpnyNo) {
        HhContractPrice01 contPrc = null;

            custNum = custNum.replace("'", "''");
            itemNum = itemNum.replace("'", "''");
            custPriceList = custPriceList.replace("'", "''");
         companyCurrency = companyCurrency.replace("'", "''");
         cmpnyNo = cmpnyNo.replace("'", "''");

        cursor = db.rawQuery(
                "select * from hhContractPrice01 where customerumber = '"
                        + custNum + "' and itemnumber = '" + itemNum
                        + "' and pricelistcode = '" + custPriceList
                        + "' and companycode = '" + cmpnyNo + "' ", null);

        try {
            while (cursor.moveToNext()) {
                contPrc = new HhContractPrice01();

                contPrc.setHhContPrc_customernumber(cursor.getString(0));
                contPrc.setHhContPrc_prictby(cursor.getInt(1));
                contPrc.setHhContPrc_category(cursor.getString(2));
                contPrc.setHhContPrc_itemnumber(cursor.getString(3));
                contPrc.setHhContPrc_pricelistcode(cursor.getString(4));
                contPrc.setHhContPrc_pricetype(cursor.getInt(5));
                contPrc.setHhContPrc_customertype(cursor.getInt(6));
                contPrc.setHhContPrc_discpercent(cursor.getDouble(7));
                contPrc.setHhContPrc_discamount(cursor.getDouble(8));
                contPrc.setHhContPrc_costmethod(cursor.getInt(9));
                contPrc.setHhContPrc_plusamount(cursor.getInt(10));
                contPrc.setHhContPrc_pluspercent(cursor.getDouble(11));
                contPrc.setHhContPrc_fixedprice(cursor.getDouble(12));
                contPrc.setHhContPrc_calcunitprice(cursor.getDouble(13));
                contPrc.setHhContPrc_uselowest(cursor.getDouble(14));
                contPrc.setHhContPrc_companycode(cursor.getString(15));

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 640";
            msg = "Getting Contract price failed(based on cus no. item no, company currency).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return contPrc;
    }

    public HhContractPrice01 getContractPrc(String custNum, String itemCategory) {
        custNum = custNum.replace("'", "''");
            itemCategory = itemCategory.replace("'", "''");

        HhContractPrice01 contPrc = null;

        cursor = db.rawQuery(
                "select * from hhContractPrice01 where customerumber = '"
                        + custNum + "' and category = '" + itemCategory + "' ",
                null);

        try {
            while (cursor.moveToNext()) {
                contPrc = new HhContractPrice01();

                contPrc.setHhContPrc_customernumber(cursor.getString(0));
                contPrc.setHhContPrc_prictby(cursor.getInt(1));
                contPrc.setHhContPrc_category(cursor.getString(2));
                contPrc.setHhContPrc_pricetype(cursor.getInt(5));
                contPrc.setHhContPrc_customertype(cursor.getInt(6));
                contPrc.setHhContPrc_discpercent(cursor.getDouble(7));
                contPrc.setHhContPrc_discamount(cursor.getDouble(8));
                contPrc.setHhContPrc_costmethod(cursor.getInt(9));
                contPrc.setHhContPrc_plusamount(cursor.getInt(10));
                contPrc.setHhContPrc_pluspercent(cursor.getDouble(11));
                contPrc.setHhContPrc_fixedprice(cursor.getDouble(12));
                contPrc.setHhContPrc_calcunitprice(cursor.getDouble(13));
                contPrc.setHhContPrc_uselowest(cursor.getDouble(14));
                contPrc.setHhContPrc_companycode(cursor.getString(15));

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 640";
            msg = "Getting Contract price failed(based on cus no. item no, company currency).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return contPrc;
    }

    // to get transaction document number
    public List<HhTran01> getTransDocNoList(int day, int month, int year,
                                            String mode, String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");

        List<HhTran01> tempTransDocNoList = new ArrayList<HhTran01>();
        String[] arrEntTyps = new String[2];
        if (mode.equals("orderEdit") || mode.equals("orderDelete")) {
            arrEntTyps[0] = "S";
            arrEntTyps[1] = "Q";

        } else if (mode.equals("invoiceEdit") || mode.equals("invoiceDelete")) {
            arrEntTyps[0] = "CN";
            arrEntTyps[1] = "I";
        }

        String query = "select distinct referencenumber,customernumber,refno from hhTran01 where companycode = '"
                + cmpnyNo + "' ";

        if (mode.equals("orderDelete") || mode.equals("invoiceDelete")) {

            query = "select distinct referencenumber,customernumber,refno from hhTran01 where (transtype = '"
                    + arrEntTyps[0]
                    + "' or transtype = '"
                    + arrEntTyps[1]
                    + "') and (companycode = '" + cmpnyNo + "')";

        } else if (day != 0) {
            query = "select distinct referencenumber,customernumber,refno from hhTran01 where (transday = '"
                    + day
                    + "' and transmonth = '"
                    + month
                    + "' and transyear = '"
                    + year
                    + "') and (transtype = '"
                    + arrEntTyps[0]
                    + "' or transtype = '"
                    + arrEntTyps[1]
                    + "') and status = 0 and (companycode = '" + cmpnyNo + "')";
        }

        cursor = db.rawQuery(query, null);

        HhTran01 tranData = null;

        try {
            while (cursor.moveToNext()) {
                tranData = new HhTran01();

                String referenceNo = cursor.getString(0);
                String docNo = cursor.getString(2);
                String cusNum = cursor.getString(1);

                tranData.setHhTran_referenceNumber(referenceNo);
                tranData.setHhTran_refNo(docNo);
                tranData.setHhTran_customerNumber(cusNum);

                tempTransDocNoList.add(tranData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 641";
            msg = "Get Transaction Document No list failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempTransDocNoList;
    }

    // to get export orders or invoices distinct reference number list...
    public List<HhTran01> getExportOrderList(String strCheckPosted,
                                             String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        List<HhTran01> tempExportTranList = new ArrayList<HhTran01>();

        String query = "select distinct referencenumber,customernumber,refno,transday,transmonth,transyear,status from hhTran01 where (transtype!='CN' and transtype!='I') and status='"
                + 0 + "' and companycode = '" + cmpnyNo + "' ";
        if (strCheckPosted.equals("Exported")) {
            query = "select distinct referencenumber,customernumber,refno,transday,transmonth,transyear,status from hhTran01 where (transtype!='CN' and transtype!='I') and status='"
                    + 1 + "' and companycode = '" + cmpnyNo + "' ";
        }
        cursor = db.rawQuery(query, null);

        HhTran01 tranData = null;

        try {
            while (cursor.moveToNext()) {
                tranData = new HhTran01();

                String referenceNo = cursor.getString(0);
                String docNo = cursor.getString(2);
                String cusNum = cursor.getString(1);
                int day = cursor.getInt(3);
                int month = cursor.getInt(4);
                int year = cursor.getInt(5);
                int status = cursor.getInt(6);

                tranData.setHhTran_referenceNumber(referenceNo);
                tranData.setHhTran_refNo(docNo);
                tranData.setHhTran_customerNumber(cusNum);
                tranData.setHhTran_transDay(day);
                tranData.setHhTran_transMonth(month);
                tranData.setHhTran_transYear(year);
                tranData.setHhTran_status(status);

                tempExportTranList.add(tranData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 641";
            msg = "Get Transaction Document No list failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempExportTranList;
    }

    // to get export orders or invoices distinct reference number list...
    public List<HhTran01> getExportCNList(String strCheckPosted, String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        List<HhTran01> tempExportTranList = new ArrayList<HhTran01>();

        String query = "select distinct referencenumber,customernumber,refno,transday,transmonth,transyear,status from hhTran01 where (transtype='CN' or transtype='I') and status='"
                + 0 + "' and companycode = '" + cmpnyNo + "' ";
        if (strCheckPosted.equals("Exported")) {
            query = "select distinct referencenumber,customernumber,refno,transday,transmonth,transyear,status from hhTran01 where (transtype='CN' or transtype='I') and status='"
                    + 1 + "' and companycode = '" + cmpnyNo + "' ";
        }
        cursor = db.rawQuery(query, null);

        HhTran01 tranData = null;

        try {
            while (cursor.moveToNext()) {
                tranData = new HhTran01();

                String referenceNo = cursor.getString(0);
                String docNo = cursor.getString(2);
                String cusNum = cursor.getString(1);
                int day = cursor.getInt(3);
                int month = cursor.getInt(4);
                int year = cursor.getInt(5);
                int status = cursor.getInt(6);

                tranData.setHhTran_referenceNumber(referenceNo);
                tranData.setHhTran_refNo(docNo);
                tranData.setHhTran_customerNumber(cusNum);
                tranData.setHhTran_transDay(day);
                tranData.setHhTran_transMonth(month);
                tranData.setHhTran_transYear(year);
                tranData.setHhTran_status(status);

                tempExportTranList.add(tranData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 641";
            msg = "Get Transaction Document No list failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return tempExportTranList;
    }

    // method to get item number from upc, apr 26 for barcode integration
    public String getItemFromUpc(String upcCode, String cmpnyNo) {
        String itemNumber = "";
        cmpnyNo = cmpnyNo.replace("'", "''");
        upcCode = upcCode.replace("'", "''");
        cursor = db.rawQuery(
                "select itemnumber from hhUPC01 where upcnumber = '" + upcCode
                        + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {

                itemNumber = cursor.getString(0);

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Scanning Error";
            msg = "Error in barcode scanning";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return itemNumber;
    }

    // method to get item number from manf, jun 28 for barcode integration
    public Manf_Number01 getItemFromManfNum(String manfCode, String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        manfCode = manfCode.replace("'", "''");
        Manf_Number01 manf = null;

        cursor = db.rawQuery("select * from manf_number01 where manitemno = '"
                + manfCode + "' or itemno = '" + manfCode
                + "' and companycode = '" + cmpnyNo + "' or alt_no = '"+ manfCode +"' and companycode = '"+cmpnyNo+"' ", null);

        try {
            while (cursor.moveToNext()) {
                manf = new Manf_Number01();

                manf.setManuf_manitemno(cursor.getString(0));
                manf.setManuf_itemno(cursor.getString(1));
                manf.setManuf_uom(cursor.getString(2));
                manf.setCompany_code(cursor.getString(3));
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Scanning Error";
            msg = "Error in barcode scanning";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return manf;
    }

    public boolean checkItemExist(String itemNum) {
        itemNum = itemNum.replace("'", "''");
        boolean result = false;
        int count = 0;
        cursor = db.rawQuery("select * from tempitem where number = '"
                + itemNum + "'", null);
        count = cursor.getCount();

        if (count != 0) {
            result = true;
        }

        cursor.close();

        return result;
    }

    public boolean checkTempDataExist() {

        boolean result = false;
        int count = 0;
        cursor = db.rawQuery("select * from tempitem", null);
        count = cursor.getCount();

        if (count != 0) {
            result = true;
        }

        cursor.close();

        return result;
    }

    // to check the customer is new or not
    public boolean isNewCustomer(String number, String cmpnyNo) {
        boolean isNewCust = false;
        int status = 1;
        cmpnyNo = cmpnyNo.replace("'", "''");
        number = number.replace("'", "''");
        cursor = db.rawQuery("select status from hhCustomer01 where number = '"
                + number + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {

                status = cursor.getInt(0);

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 651";
            msg = "Customer status checking error";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        if (status == 0) {
            isNewCust = true;
        }

        return isNewCust;
    }

    // to check whether the new customer is having transaction or not
    public boolean checkTranForNewCust(String custNum, String cmpnyNo) {
        boolean isTranExist = false;
        cmpnyNo = cmpnyNo.replace("'", "''");
        custNum = custNum.replace("'", "''");
        int count = 0;
        cursor = db.rawQuery(
                "select customernumber from hhTran01 where customernumber = '"
                        + custNum + "' and companycode = '" + cmpnyNo + "' ",
                null);
        count = cursor.getCount();
        if (count != 0) {
            isTranExist = true;
        }

        cursor.close();

        return isTranExist;
    }

    public boolean checkDB() {

        boolean result = false;

        int count = 0;
        cursor = db.rawQuery("select * from hhCompany", null);
        count = cursor.getCount();

        if (count != 0) {
            result = true;
        }

        cursor.close();

        return result;

    }

    public boolean isReceiptExist(String receiptNum, String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        receiptNum = receiptNum.replace("'", "''");
        boolean isExist = false;

        int count = 0;
        cursor = db.rawQuery(
                "select receiptnumber from hhReceipt01 where receiptnumber = '"
                        + receiptNum + "' and companycode = '" + cmpnyNo + "'",
                null);
        count = cursor.getCount();

        if (count != 0) {
            isExist = true;
        }

        cursor.close();

        return isExist;
    }

    public boolean isCheckReceiptExistForPrepayment(String chkReceiptNum,
                                                    String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        chkReceiptNum = chkReceiptNum.replace("'", "''");
        boolean isExist = false;

        int count = 0;
        cursor = db.rawQuery(
                "select checkreceiptno from hhPrepayment01 where checkreceiptno = '"
                        + chkReceiptNum + "' and companycode = '" + cmpnyNo
                        + "'", null);
        count = cursor.getCount();

        if (count != 0) {
            isExist = true;
        }

        cursor.close();

        return isExist;
    }

    public boolean isCheckReceiptExistForPrepaymentForEdit(
            String chkReceiptNum, String refNo, String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        chkReceiptNum = chkReceiptNum.replace("'", "''");
        refNo = refNo.replace("'", "''");

        boolean isExist = false;

        int count = 0;
        cursor = db.rawQuery(
                "select checkreceiptno from hhPrepayment01 where checkreceiptno = '"
                        + chkReceiptNum + "' and referencenumber != '" + refNo
                        + "' and companycode = '" + cmpnyNo + "'", null);
        count = cursor.getCount();

        if (count != 0) {
            isExist = true;
        }

        cursor.close();

        return isExist;
    }

    public HhPrice01 getPrcData(String currentPriceListCode, String itemNo,
                                String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        itemNo = itemNo.replace("'", "''");
        currentPriceListCode = currentPriceListCode.replace("'", "''");
        HhPrice01 price = null;

        cursor = db.rawQuery("select * from hhPrice01 where pricelistcode = '"
                + currentPriceListCode + "' and number = '" + itemNo
                + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                price = new HhPrice01();

                price.setHhPrice_number(cursor.getString(0));
                price.setHhPrice_disctype(cursor.getInt(1));
                price.setHhPrice_price1(cursor.getDouble(2));
                price.setHhPrice_price2(cursor.getDouble(3));
                price.setHhPrice_price3(cursor.getDouble(4));
                price.setHhPrice_price4(cursor.getDouble(5));
                price.setHhPrice_price5(cursor.getDouble(6));
                price.setHhPrice_price6(cursor.getDouble(7));
                price.setHhPrice_qtylevel1(cursor.getDouble(8));
                price.setHhPrice_qtylevel2(cursor.getDouble(9));
                price.setHhPrice_qtylevel3(cursor.getDouble(10));
                price.setHhPrice_qtylevel4(cursor.getDouble(11));
                price.setHhPrice_qtylevel5(cursor.getDouble(12));
                price.setHhPrice_markup_cost(cursor.getDouble(13));
                price.setHhPrice_price_list_code(cursor.getString(14));
                price.setHhPrice_location(cursor.getString(15));
                price.setHhPrice_pricing_uom(cursor.getString(16));
                price.setHhPrice_pricing_conv_factor(cursor.getDouble(17));
                price.setHhPrice_markup_uom(cursor.getString(18));
                price.setHhPrice_markup_conv_factor(cursor.getDouble(19));
                price.setHhPrice_price_type(cursor.getDouble(20));
                price.setHhPrice_sales_price(cursor.getString(21));
                price.setHhPrice_sales_uom(cursor.getString(22));
                price.setHhPrice_sales_conv_factor(cursor.getString(23));
                price.setHhPrice_sales_startdate(cursor.getString(24));
                price.setHhPrice_sales_enddate(cursor.getString(25));
                price.setHhPrice_companycode(cursor.getString(26));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 644";
            msg = "Get price data failed(Based on price list code, item no).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return price;

    }

    public double getSummaryTotal(String refNumber, String cmpnyNo) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        refNumber = refNumber.replace("'", "''");
        double summaryTotal = 0.00;

        cursor = db.rawQuery(
                "select transtype,qty,extprice from hhTran01 where referencenumber = '"
                        + refNumber + "' and companycode = '" + cmpnyNo + "'",
                null);

        try {
            while (cursor.moveToNext()) {
                String tranType = cursor.getString(0);
                int qty = cursor.getInt(1);
                double tempPrc = cursor.getDouble(2);

                if (tranType.equals("CN")) {
                    summaryTotal = summaryTotal - tempPrc;
                } else {
                    summaryTotal = summaryTotal + tempPrc;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 645";
            msg = "Get Summary Total failed(Based on ref.no.).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return summaryTotal;
    }

    // for export purpose

    public List<HhCustomer01> getNewCustomerDatasObj(String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        List<HhCustomer01> custArray = new ArrayList<HhCustomer01>();

        cursor = db.rawQuery("select * from hhCustomer01 where status='" + 0
                + "' and companycode = '" + cmpnyNo + "'", null);
        HhCustomer01 cust = null;
        try {
            while (cursor.moveToNext()) {
                cust = new HhCustomer01();

                cust.setHhCustomer_number(cursor.getString(0));
                cust.setHhCustomer_name(cursor.getString(1));
                cust.setHhCustomer_address(cursor.getString(2));
                cust.setHhCustomer_city(cursor.getString(3));
                cust.setHhCustomer_state(cursor.getString(4));
                cust.setHhCustomer_zip(cursor.getString(5));
                cust.setHhCustomer_country(cursor.getString(6));
                cust.setHhCustomer_phone1(cursor.getString(7));
                cust.setHhCustomer_email1(cursor.getString(8));
                cust.setHhCustomer_website(cursor.getString(9));
                cust.setHhCustomer_contact(cursor.getString(10));
                cust.setHhCustomer_email2(cursor.getString(11));
                cust.setHhCustomer_groupcode(cursor.getString(12));
                cust.setHhCustomer_type(cursor.getString(13));
                cust.setHhCustomer_terms(cursor.getString(14));
                cust.setHhCustomer_lastbalance(cursor.getDouble(15));
                cust.setHhCustomer_creditlimit(cursor.getDouble(16));
                cust.setHhCustomer_lastsale(cursor.getDouble(17));
                cust.setHhCustomer_taxgroup(cursor.getString(18));
                cust.setHhCustomer_comment(cursor.getString(19));

                cust.setHhCustomer_taxauthority1(cursor.getString(20));
                cust.setHhCustomer_taxable1(cursor.getInt(21));
                cust.setHhCustomer_taxstts1(cursor.getString(22));

                cust.setHhCustomer_taxauthority2(cursor.getString(23));
                cust.setHhCustomer_taxable2(cursor.getInt(24));
                cust.setHhCustomer_taxstts2(cursor.getString(25));

                cust.setHhCustomer_taxauthority3(cursor.getString(26));
                cust.setHhCustomer_taxable3(cursor.getInt(27));
                cust.setHhCustomer_taxstts3(cursor.getString(28));

                cust.setHhCustomer_taxauthority4(cursor.getString(29));
                cust.setHhCustomer_taxable4(cursor.getInt(30));
                cust.setHhCustomer_taxstts4(cursor.getString(31));

                cust.setHhCustomer_taxauthority5(cursor.getString(32));
                cust.setHhCustomer_taxable5(cursor.getInt(33));
                cust.setHhCustomer_taxstts5(cursor.getString(34));

                cust.setHhCustomer_status(cursor.getInt(35));
                cust.setHhCustomer_pricelistcode(cursor.getString(36));
                cust.setHhCustomer_idtaxregi1(cursor.getString(37));
                cust.setHhCustomer_creditstatus(cursor.getString(38));
                cust.setHhCustomer_shipvia(cursor.getString(39));
                cust.setHhCustomer_companycode(cursor.getString(40));

                custArray.add(cust);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 646";
            msg = "Get New Customer Data obj failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        cursor.close();

        return custArray;
    }

    // to get transaction for export
    public List<HhTran01> getTransactionExportOrderList(String refno,
                                                        List<HhTran01> transArray, String cmpnyNo, boolean isExported) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        refno = refno.replace("'", "''");
        if (isExported) {
            cursor = db
                    .rawQuery(
                            "select * from hhTran01 where status='"
                                    + 1
                                    + "' and (transtype!='CN' and transtype!='I') and referencenumber = '"
                                    + refno + "' and companycode = '" + cmpnyNo
                                    + "'", null);
        } else {
            cursor = db
                    .rawQuery(
                            "select * from hhTran01 where status='"
                                    + 0
                                    + "' and (transtype!='CN' and transtype!='I') and referencenumber = '"
                                    + refno + "' and companycode = '" + cmpnyNo
                                    + "'", null);
        }

        HhTran01 transData = null;
        try {
            while (cursor.moveToNext()) {
                transData = new HhTran01();
                transData.setHhTran_transType(cursor.getString(0));
                transData.setHhTran_referenceNumber(cursor.getString(1));
                transData.setHhTran_invoiceNumber(cursor.getString(2));
                transData.setHhTran_orderNumber(cursor.getString(3));
                transData.setHhTran_transDay(cursor.getInt(4));
                transData.setHhTran_transMonth(cursor.getInt(5));
                transData.setHhTran_transYear(cursor.getInt(6));
                transData.setHhTran_expShipDay(cursor.getInt(7));
                transData.setHhTran_expShipMonth(cursor.getInt(8));
                transData.setHhTran_expShipYear(cursor.getInt(9));
                transData.setHhTran_customerNumber(cursor.getString(10));
                transData.setHhTran_salesPerson(cursor.getString(11));
                transData.setHhTran_itemNumber(cursor.getString(12));
                transData.setHhTran_locId(cursor.getString(13));
                transData.setHhTran_terms(cursor.getString(14));
                transData.setHhTran_currency(cursor.getString(15));
                transData.setHhTran_priceListCode(cursor.getString(16));
                transData.setHhTran_uom(cursor.getString(17));
                transData.setHhTran_qty(cursor.getInt(18)); // need to change
                // float
                transData.setHhTran_price(cursor.getDouble(19));
                transData.setHhTran_discPrice(cursor.getDouble(20));
                transData.setHhTran_netPrice(cursor.getDouble(21));
                transData.setHhTran_extPrice(cursor.getDouble(22));
                transData.setHhTran_tax(cursor.getDouble(23));
                transData.setHhTran_shipToCode(cursor.getString(24));
                transData.setHhTran_shipViaCode(cursor.getString(25));
                transData.setHhTran_status(cursor.getInt(26));
                transData.setHhTran_lineItem(cursor.getInt(27));
                transData.setHhTran_discValue(cursor.getDouble(28));// change
                // double
                transData.setHhTran_discType(cursor.getString(29));
                transData.setHhTran_ordShipDay(cursor.getInt(30));
                transData.setHhTran_ordShipMonth(cursor.getInt(31));
                transData.setHhTran_ordShipYear(cursor.getInt(32));
                transData.setHhTran_editable(cursor.getInt(33));
                transData.setHhTran_manItemNo(cursor.getString(34));
                transData.setHhTran_preTax(cursor.getDouble(35));
                transData.setHhTran_refNo(cursor.getString(36));
                transData.setHhTran_orderdiscount(cursor.getDouble(37));
                transData.setHhTran_taxamount(cursor.getDouble(38));
                transData.setHhTran_totalcarton(cursor.getDouble(39));
                transData.setHhTran_editedcustomername(cursor.getString(40));
                transData.setHhTran_itemunitweight(cursor.getDouble(41));
                transData.setHhTran_companycode(cursor.getString(42));
               // transData.setHhSalescommission(cursor.getString(45));

                transArray.add(transData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 654";
            msg = "Get Transaction Export list details failed for only orders and particular reference number.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        cursor.close();

        return transArray;

    }

    // to get return transaction for export
    public List<HhTran01> getTransactionExportCreditNoteList(String refNo,
                                                             List<HhTran01> transArray, String cmpnyNo, boolean isExported) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        refNo = refNo.replace("'", "''");
        if (isExported) {
            cursor = db
                    .rawQuery(
                            "select * from hhTran01 where status='"
                                    + 1
                                    + "' and (transtype='CN' or transtype='I') and referencenumber = '"
                                    + refNo + "' and companycode = '" + cmpnyNo
                                    + "'", null);
        } else {
            cursor = db
                    .rawQuery(
                            "select * from hhTran01 where status='"
                                    + 0
                                    + "' and (transtype='CN' or transtype='I') and referencenumber = '"
                                    + refNo + "' and companycode = '" + cmpnyNo
                                    + "'", null);
        }

        HhTran01 transData = null;
        try {
            while (cursor.moveToNext()) {
                transData = new HhTran01();
                transData.setHhTran_transType(cursor.getString(0));
                transData.setHhTran_referenceNumber(cursor.getString(1));
                transData.setHhTran_invoiceNumber(cursor.getString(2));
                transData.setHhTran_orderNumber(cursor.getString(3));
                transData.setHhTran_transDay(cursor.getInt(4));
                transData.setHhTran_transMonth(cursor.getInt(5));
                transData.setHhTran_transYear(cursor.getInt(6));
                transData.setHhTran_expShipDay(cursor.getInt(7));
                transData.setHhTran_expShipMonth(cursor.getInt(8));
                transData.setHhTran_expShipYear(cursor.getInt(9));
                transData.setHhTran_customerNumber(cursor.getString(10));
                transData.setHhTran_salesPerson(cursor.getString(11));
                transData.setHhTran_itemNumber(cursor.getString(12));
                transData.setHhTran_locId(cursor.getString(13));
                transData.setHhTran_terms(cursor.getString(14));
                transData.setHhTran_currency(cursor.getString(15));
                transData.setHhTran_priceListCode(cursor.getString(16));
                transData.setHhTran_uom(cursor.getString(17));
                transData.setHhTran_qty(cursor.getInt(18)); // need to change
                // float
                transData.setHhTran_price(cursor.getDouble(19));
                transData.setHhTran_discPrice(cursor.getDouble(20));
                transData.setHhTran_netPrice(cursor.getDouble(21));
                transData.setHhTran_extPrice(cursor.getDouble(22));
                transData.setHhTran_tax(cursor.getDouble(23));
                transData.setHhTran_shipToCode(cursor.getString(24));
                transData.setHhTran_shipViaCode(cursor.getString(25));
                transData.setHhTran_status(cursor.getInt(26));
                transData.setHhTran_lineItem(cursor.getInt(27));
                transData.setHhTran_discValue(cursor.getDouble(28));// change
                // double
                transData.setHhTran_discType(cursor.getString(29));
                transData.setHhTran_ordShipDay(cursor.getInt(30));
                transData.setHhTran_ordShipMonth(cursor.getInt(31));
                transData.setHhTran_ordShipYear(cursor.getInt(32));
                transData.setHhTran_editable(cursor.getInt(33));
                transData.setHhTran_manItemNo(cursor.getString(34));
                transData.setHhTran_preTax(cursor.getDouble(35));
                transData.setHhTran_refNo(cursor.getString(36));
                transData.setHhTran_orderdiscount(cursor.getDouble(37));
                transData.setHhTran_taxamount(cursor.getDouble(38));
                transData.setHhTran_totalcarton(cursor.getDouble(39));
                transData.setHhTran_editedcustomername(cursor.getString(40));
                transData.setHhTran_itemunitweight(cursor.getDouble(41));
                transData.setHhTran_companycode(cursor.getString(42));

                transArray.add(transData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 655";
            msg = "Get Credit Note Transaction Export list details failed for only orders and particular reference number.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        cursor.close();

        return transArray;

    }

    // to get receipts for export
    public List<HhReceipt01> getReceiptsList() {

        List<HhReceipt01> reciptArray = new ArrayList<HhReceipt01>();

        cursor = db.rawQuery("select * from hhReceipt01 where status='" + 0
                + "'", null);

        HhReceipt01 reciptData = null;
        try {
            while (cursor.moveToNext()) {
                reciptData = new HhReceipt01();

                reciptData.setHhReceipt_sno(cursor.getInt(0));
                reciptData.setHhReceipt_customernumber(cursor.getString(1));
                reciptData.setHhReceipt_customername(cursor.getString(2));
                reciptData.setHhReceipt_docnumber(cursor.getString(3));
                reciptData.setHhReceipt_pendingbal(cursor.getString(4));
                reciptData.setHhReceipt_currency(cursor.getString(5));
                reciptData.setHhReceipt_apply1(cursor.getString(6));
                reciptData.setHhReceipt_appliedamount(cursor.getDouble(7));
                reciptData.setHhReceipt_receipttype(cursor.getString(8));
                reciptData.setHhReceipt_amount(cursor.getDouble(9));
                reciptData.setHhReceipt_receiptnumber(cursor.getString(10));
                reciptData.setHhReceipt_receiptday(cursor.getInt(11));
                reciptData.setHhReceipt_receiptmonth(cursor.getInt(12));
                reciptData.setHhReceipt_receiptyear(cursor.getInt(13));
                reciptData.setHhReceipt_status(cursor.getInt(14));
                reciptData.setHhReceipt_receiptunapplied(cursor.getDouble(15));
                reciptData.setHhReceipt_customeramount(cursor.getDouble(16));
                reciptData.setHhReceipt_refno(cursor.getString(17));
                reciptData.setHhReceipt_companycode(cursor.getString(18));

                reciptArray.add(reciptData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 649";
            msg = "Get Applied Receipts failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        cursor.close();

        return reciptArray;

    }

    // to get receipts for export
    public List<HhReceipt01> getReceiptsList(String strCheckPosted,
                                             String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        List<HhReceipt01> reciptArray = new ArrayList<HhReceipt01>();

        String query = "select distinct receiptnumber,customernumber,refno,receiptday,receiptmonth,receiptyear,status from hhReceipt01 where status='"
                + 0 + "' and companycode = '" + cmpnyNo + "' ";

        if (strCheckPosted.equals("Exported")) {
            query = "select distinct receiptnumber,customernumber,refno,receiptday,receiptmonth,receiptyear,status from hhReceipt01 where status='"
                    + 1 + "' and companycode = '" + cmpnyNo + "' ";
        }
        cursor = db.rawQuery(query, null);

        HhReceipt01 reciptData = null;
        try {
            while (cursor.moveToNext()) {
                reciptData = new HhReceipt01();

                String reciptNum = cursor.getString(0);
                String cusNum = cursor.getString(1);
                String refNo = cursor.getString(2);
                int day = cursor.getInt(3);
                int month = cursor.getInt(4);
                int year = cursor.getInt(5);
                int status = cursor.getInt(6);

                reciptData.setHhReceipt_receiptnumber(reciptNum);
                reciptData.setHhReceipt_customernumber(cusNum);
                reciptData.setHhReceipt_refno(refNo);
                reciptData.setHhReceipt_receiptday(day);
                reciptData.setHhReceipt_receiptmonth(month);
                reciptData.setHhReceipt_receiptyear(year);
                reciptData.setHhReceipt_status(status);

                reciptArray.add(reciptData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 649";
            msg = "Get Applied Receipts failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        cursor.close();

        return reciptArray;

    }

    // to get receipts for export
    public List<HhReceipt01> getExportReceiptsList(String receiptNum,
                                                   List<HhReceipt01> reciptArray, String cmpnyNo, boolean isExported) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        receiptNum = receiptNum.replace("'", "''");
        if (isExported) {
            cursor = db.rawQuery("select * from hhReceipt01 where status='" + 1
                    + "' and receiptnumber='" + receiptNum
                    + "' and companycode = '" + cmpnyNo + "'", null);
        } else {
            cursor = db.rawQuery("select * from hhReceipt01 where status='" + 0
                    + "' and receiptnumber='" + receiptNum
                    + "' and companycode = '" + cmpnyNo + "'", null);
        }

        HhReceipt01 reciptData = null;
        try {
            while (cursor.moveToNext()) {
                reciptData = new HhReceipt01();

                reciptData.setHhReceipt_sno(cursor.getInt(0));
                reciptData.setHhReceipt_customernumber(cursor.getString(1));
                reciptData.setHhReceipt_customername(cursor.getString(2));
                reciptData.setHhReceipt_docnumber(cursor.getString(3));
                reciptData.setHhReceipt_pendingbal(cursor.getString(4));
                reciptData.setHhReceipt_currency(cursor.getString(5));
                reciptData.setHhReceipt_apply1(cursor.getString(6));
                reciptData.setHhReceipt_appliedamount(cursor.getDouble(7));
                reciptData.setHhReceipt_receipttype(cursor.getString(8));
                reciptData.setHhReceipt_amount(cursor.getDouble(9));
                reciptData.setHhReceipt_receiptnumber(cursor.getString(10));
                reciptData.setHhReceipt_receiptday(cursor.getInt(11));
                reciptData.setHhReceipt_receiptmonth(cursor.getInt(12));
                reciptData.setHhReceipt_receiptyear(cursor.getInt(13));
                reciptData.setHhReceipt_status(cursor.getInt(14));
                reciptData.setHhReceipt_receiptunapplied(cursor.getDouble(15));
                reciptData.setHhReceipt_customeramount(cursor.getDouble(16));
                reciptData.setHhReceipt_refno(cursor.getString(17));
                reciptData.setHhReceipt_companycode(cursor.getString(18));

                reciptArray.add(reciptData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 656";
            msg = "Get Applied Particular Receipts failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        cursor.close();

        return reciptArray;

    }

    // to get prepayment for export
    public List<HhPrepayment01> getPrepaymentList() {

        List<HhPrepayment01> prepayArray = new ArrayList<HhPrepayment01>();

        cursor = db.rawQuery("select * from hhPrepayment01", null);

        HhPrepayment01 prepayData = null;
        try {
            while (cursor.moveToNext()) {
                prepayData = new HhPrepayment01();

                prepayData.setHhPrePayment_transType(cursor.getString(0));
                prepayData.setHhPrePayment_referenceNumber(cursor.getString(1));
                prepayData.setHhPrePayment_invoiceNumber(cursor.getString(2));
                prepayData.setHhPrePayment_customerNumber(cursor.getString(3));
                prepayData.setHhPrePayment_currency(cursor.getString(4));
                prepayData.setHhPrePayment_orderTotal(cursor.getDouble(5));
                prepayData.setHhPrePayment_amtDue(cursor.getDouble(6));
                prepayData.setHhPrePayment_receiptType(cursor.getString(7));
                prepayData.setHhPrePayment_checkReceiptNo(cursor.getString(8));
                prepayData.setHhPrePayment_receiptDay(cursor.getInt(9));
                prepayData.setHhPrePayment_receiptMonth(cursor.getInt(10));
                prepayData.setHhPrePayment_receiptYear(cursor.getInt(11));
                prepayData.setHhPrePayment_receiptAmount(cursor.getDouble(12));
                prepayData
                        .setHhPrePayment_accpacOrdNumber(cursor.getString(13));
                prepayData.setHhPrePayment_flag(cursor.getString(14));
                prepayData.setHhPrePayment_refNo(cursor.getString(15));
                prepayData.setHhPrePayment_companycode(cursor.getString(16));

                prepayArray.add(prepayData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 650";
            msg = "Get Prepayments failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        cursor.close();

        return prepayArray;

    }

    // to get transaction for export
    public List<HhPrepayment01> getPrepaymentList(String refno,
                                                  List<HhPrepayment01> prepayArray) {

        refno = refno.replace("'", "''");

        cursor = db.rawQuery(
                "select * from hhPrepayment01 where referencenumber = '"
                        + refno + "'", null);

        HhPrepayment01 prepayData = null;
        try {
            while (cursor.moveToNext()) {
                prepayData = new HhPrepayment01();

                prepayData.setHhPrePayment_transType(cursor.getString(0));
                prepayData.setHhPrePayment_referenceNumber(cursor.getString(1));
                prepayData.setHhPrePayment_invoiceNumber(cursor.getString(2));
                prepayData.setHhPrePayment_customerNumber(cursor.getString(3));
                prepayData.setHhPrePayment_currency(cursor.getString(4));
                prepayData.setHhPrePayment_orderTotal(cursor.getDouble(5));
                prepayData.setHhPrePayment_amtDue(cursor.getDouble(6));
                prepayData.setHhPrePayment_receiptType(cursor.getString(7));
                prepayData.setHhPrePayment_checkReceiptNo(cursor.getString(8));
                prepayData.setHhPrePayment_receiptDay(cursor.getInt(9));
                prepayData.setHhPrePayment_receiptMonth(cursor.getInt(10));
                prepayData.setHhPrePayment_receiptYear(cursor.getInt(11));
                prepayData.setHhPrePayment_receiptAmount(cursor.getDouble(12));
                prepayData
                        .setHhPrePayment_accpacOrdNumber(cursor.getString(13));
                prepayData.setHhPrePayment_flag(cursor.getString(14));
                prepayData.setHhPrePayment_refNo(cursor.getString(15));
                prepayData.setHhPrePayment_companycode(cursor.getString(16));

                prepayArray.add(prepayData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 650L";
            msg = "Get Prepayments failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }
        cursor.close();

        return prepayArray;

    }

    // to delete table records exclude tran and setting table.
    public void deleteAllTableRecords(Context context) {

        try {
            db.execSQL("DELETE FROM " + CUSTOMER_TABLE);
            db.execSQL("DELETE FROM " + ITEM_TABLE);
            db.execSQL("DELETE FROM " + ITEM_PRICE_LIST_MASTER_TABLE);
            db.execSQL("DELETE FROM " + PRICE_TABLE);
            db.execSQL("DELETE FROM " + CUSTOMER_GROUPCODE_TABLE);
            db.execSQL("DELETE FROM " + SHIPTO_TABLE);
            db.execSQL("DELETE FROM " + SHIPVIA_TABLE);
            db.execSQL("DELETE FROM " + CONVFACTOR_TABLE);
            db.execSQL("DELETE FROM " + COMPANY_TABLE);
            db.execSQL("DELETE FROM " + CONT_PRICE_TABLE);
            db.execSQL("DELETE FROM " + ITMPRCTAX_TABLE);
            db.execSQL("DELETE FROM " + ITEMTAX_TABLE);
            db.execSQL("DELETE FROM " + LOCMAST_TABLE);
            db.execSQL("DELETE FROM " + MANAGER_TABLE);
            db.execSQL("DELETE FROM " + RECEIPTTYPE_TABLE);
            db.execSQL("DELETE FROM " + MANUF_TABLE);
            db.execSQL("DELETE FROM " + PAYMENT_TABLE);
            db.execSQL("DELETE FROM " + MULTIUOM_TABLE);
            db.execSQL("DELETE FROM " + ROUTE_TABLE);
            db.execSQL("DELETE FROM " + TAXCLASS_TABLE);
            db.execSQL("DELETE FROM " + TAXGROUP_TABLE);
            db.execSQL("DELETE FROM " + TAXRATE_TABLE);
            db.execSQL("DELETE FROM " + TERMS_TABLE);
            db.execSQL("DELETE FROM " + UPC_TABLE);
            db.execSQL("DELETE FROM " + KILOMETER_TABLE);
            db.execSQL("DELETE FROM " + VAN_TABLE);
            db.execSQL("DELETE FROM " + TEMPITEM_TABLE);
            db.execSQL("DELETE FROM " + PREPAYMENT_TABLE);
            db.execSQL("DELETE FROM " + RECEIPT_TABLE);

            // db.execSQL("DELETE FROM "+TRANS_TABLE);
            // db.execSQL("DELETE FROM " + MSPDB_TABLE);
            // db.execSQL("DELETE FROM "+SETTING_TABLE);

            Log.i("Success", "Tables records are successfully deleted...");
        } catch (Exception e) {
            // db.endTransaction();
            db.close();

            errCode = "Error 561";
            msg = "All Table Record Deletion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addCompanyRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.COMPANY_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in company table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 502";
            msg = "Company Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addContractPriceRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.CONT_PRICE_TABLE
                    + " values(" + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in contractprice table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 503";
            msg = "Contract price Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addConversionFactorRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.CONVFACTOR_TABLE
                    + " values(" + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in conversionfactor table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 504";
            msg = "Conversion factor Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addCustomerRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.CUSTOMER_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in customer table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            // writing complete exception in string
			/*
			 * StringWriter stringWritter = new StringWriter(); PrintWriter
			 * printWritter = new PrintWriter(stringWritter, true);
			 * e.printStackTrace(printWritter); printWritter.flush();
			 * stringWritter.flush();
			 */

            errCode = "Error 505";
            msg = "Customer Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            // LogfileCreator.appendLog(errMsg+"\n"+stringWritter.toString());
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addGroupCodeRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.CUSTOMER_GROUPCODE_TABLE
                    + " values(" + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in groupcode table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 506";
            msg = "Customer Group code Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addItemRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.ITEM_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in item table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 507";
            msg = "Item Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addItmPrcTaxRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.ITMPRCTAX_TABLE
                    + " values(" + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in itempricetax table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 508";
            msg = "Item price tax Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addItemTaxRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.ITEMTAX_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in itemtax table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 509";
            msg = "Item tax Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addLocMasterRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.LOCMAST_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in locmaster table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 510";
            msg = "Location master Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addManagerRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.MANAGER_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in manager table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 511";
            msg = "Manager Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addMultiUOMRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.MULTIUOM_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in multiuom table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 512";
            msg = "MultiUOM Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addPriceRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.PRICE_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in price table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 514";
            msg = "Price Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addPriceListMasterRecord(String record) {
        try {
            db.execSQL("insert into "
                    + MspDBHelper.ITEM_PRICE_LIST_MASTER_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in pricelistmaster table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 515";
            msg = "PriceListmaster Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addReceiptTypeRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.RECEIPTTYPE_TABLE
                    + " values(" + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in receipttype table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 516";
            msg = "ReceiptType Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addRouteRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.ROUTE_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in route table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 517";
            msg = "Route Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addShipToRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.SHIPTO_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in shipto table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 518";
            msg = "ShipTo Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addShipViaRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.SHIPVIA_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in shipvia table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 519";
            msg = "Shipvia Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addTaxClassRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.TAXCLASS_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in taxclass table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 520";
            msg = "Taxclass Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addTaxRateRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.TAXRATE_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in taxrate table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 521";
            msg = "TaxRate Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addTaxGroupRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.TAXGROUP_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in taxgroup table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 522";
            msg = "TaxGroup Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addTermsRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.TERMS_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in terms table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 523";
            msg = "Terms Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addUPCRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.UPC_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in upc table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 524";
            msg = "UPC Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addManufactureNumberDataRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.MANUF_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in manuf_numb table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 525";
            msg = "Manufacture Number Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addMspDbDataRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.MSPDB_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in mspdb table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 526";
            msg = "MspDB Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void addPaymentDataRecord(String record) {
        try {
            db.execSQL("insert into " + MspDBHelper.PAYMENT_TABLE + " values("
                    + record + ");");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting in payment table.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 533";
            msg = "Payment Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    // method for transactions
    public void mBeginTransaction() {
        this.db.beginTransaction();
    }

    public void mSetTransactionSuccess() {
        this.db.setTransactionSuccessful();
    }

    public void mEndTransaction() {
        this.db.endTransaction();
    }

    public void closeDatabase() {
        this.db.close();
        Log.i("Db closed", "Db closed.");
    }

    public boolean isDbOpen() {
        return db.isOpen();
    }

    // to open readable database
    public void openReadableDatabase() {

        if (fpath.exists()) {

            db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

                            + File.separator + DATABASE_NAME, null,

                    SQLiteDatabase.OPEN_READONLY);
        }
        Log.i("Db opened", "Readable Db opened.");
    }

    // to open writable database
    public void openWritableDatabase() {
        if (fpath.exists()) {

            db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

                            + File.separator + DATABASE_NAME, null,

                    SQLiteDatabase.OPEN_READWRITE);
        }
        Log.i("Db opened", "Writable Db opened.");
    }

    // status change

    public void statusUpdateTran(String refno, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TRANS_STATUS, 1);
            cv.put(MspDBHelper.TRANS_EDITABLE, 1);

            db.update(MspDBHelper.TRANS_TABLE, cv,
                    "status = 0 and (transtype!='CN'and transtype!='I') and referencenumber = '"
                            + refno + "' and companycode = '" + cmpnyNo + "'",
                    null);

            Log.i("Tran Status Updated",
                    "Transaction status updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            db.close();

            errCode = "Error 571";
            msg = "Transaction Status Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);

        }
    }

    public void updateQty(String qty,String oldtem,String txt_itmno){
        try {
            ContentValues cv = new ContentValues();
            cv.put(MspDBHelper.TEMPITEM_QTY,qty);
            db.update(MspDBHelper.TEMPITEM_TABLE, cv,"" + TEMPITEM_QTY + " ="+oldtem+" and "+ TEMPITEM_NUMBER + " = '"+txt_itmno+"'", null);
            Log.i("updateQty: updated","Success");

        }catch (Exception e){
            e.printStackTrace();
            Log.i( "updateQty: error", String.valueOf(e));

        }

    }

    public void updateTransComments(String comments,String refno){
        try {
            ContentValues cv = new ContentValues();
            cv.put(MspDBHelper.COMMENTS, comments);
            db.update(MspDBHelper.TRANS_TABLE, cv, " " + TRANS_REFERENCENUMBER + " = '" + refno + "' ", null);
            Log.i(TAG, "updateTransComments: Success");
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "updateTransComments: "+ e);

        }
    }

    public void statusUpdateInvoice(String refno, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TRANS_STATUS, 1);
            cv.put(MspDBHelper.TRANS_EDITABLE, 1);

            db.update(MspDBHelper.TRANS_TABLE, cv,
                    "status = 0 and (transtype ='CN' or transtype ='I') and referencenumber = '"
                            + refno + "' and companycode = '" + cmpnyNo + "'",
                    null);

            Log.i("Invoice Status Updated",
                    "Invoice status updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            db.close();

            errCode = "Error 571";
            msg = "Invoice Status Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);

        }
    }

    public void statusUpdateCustomer(String cmpnyNo) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.CUSTOMER_STATUS, 1);
            db.update(MspDBHelper.CUSTOMER_TABLE, cv,
                    "status = 0 and companycode = '" + cmpnyNo + "'", null);

            Log.i("Customer Status Updated",
                    "Customer status updated successfully...");

        } catch (Exception e) {
            db.endTransaction();
            db.close();

            errCode = "Error 572";
            msg = "New Customer Status Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

        }


    public void statusUpdateReceipt(String receiptNum, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.RECEIPT_STATUS, 1);
            db.update(MspDBHelper.RECEIPT_TABLE, cv,
                    "status = 0 and receiptnumber='" + receiptNum
                            + "' and companycode = '" + cmpnyNo + "'", null);

            Log.i("Receipt Status Updated",
                    "Receipt status updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            db.close();

            errCode = "Error 573";
            msg = "Receipt Status Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void statusUpdatePrepayment(String refno, String cmpnyNo) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.PREPAYMENT_FLAG, 1);

            db.update(MspDBHelper.PREPAYMENT_TABLE, cv,
                    "flag = 0 and referencenumber = '" + refno
                            + "' and companycode = '" + cmpnyNo + "'", null);

            Log.i("Prepayment Status Updated",
                    "Prepayment status updated successfully...");

        } catch (Exception e) {
            db.endTransaction();
            db.close();

            errCode = "Error 568";
            msg = "Prepayment Status Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    public boolean isDataAvailableForPost() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("select status from " + MspDBHelper.TRANS_TABLE
                + " where status = 0", null);
        count = cursor.getCount();
        cursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }

		/*
		 * cursor = db.rawQuery("select status from " +
		 * MspDBHelper.CUSTOMER_TABLE + " where status = 0", null); count =
		 * cursor.getCount(); cursor.close(); if (count != 0) { isAvail = true;
		 * count = 0; }
		 */

        cursor = db.rawQuery("select status from " + MspDBHelper.RECEIPT_TABLE
                + " where status = 0", null);
        count = cursor.getCount();
        cursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }

		/*
		 * cursor = db.rawQuery("select flag from " +
		 * MspDBHelper.PREPAYMENT_TABLE + " where flag = 0", null); count =
		 * cursor.getCount(); cursor.close(); if (count != 0) { isAvail = true;
		 * count = 0; }
		 */

        return isAvail;
    }

    // checking unexported data is avail jul 15/2013
    public boolean isDataAvailableForPost(String cmpnyNo) {
        cmpnyNo = cmpnyNo.replace("'", "''");
        boolean isAvail = false;

        int count = 0;

        cursor = db
                .rawQuery("select status from " + MspDBHelper.TRANS_TABLE
                        + " where status = 0 and companycode = '" + cmpnyNo
                        + "'", null);
        count = cursor.getCount();
        cursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }

        cursor = db
                .rawQuery("select status from " + MspDBHelper.RECEIPT_TABLE
                        + " where status = 0 and companycode = '" + cmpnyNo
                        + "'", null);
        count = cursor.getCount();
        cursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }

        return isAvail;
    }

    public boolean isTranAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM " + MspDBHelper.TRANS_TABLE,
                null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    // changed by T. Saravanan for Import Summary View on 5.10.13
    public boolean isDataAvailableForOE() {

        boolean isAvail = false;

        boolean isConFact = isConvFactAvail();
        boolean isCustomer = isCustAvail();
        boolean isItem = isItemDataAvail();
        boolean isPrcMaster = isPrcMasterAvail();
        boolean isPrice = isPriceAvail();
        boolean isMSDB = isMspdbAvail();
        boolean isUPC = isUPCDataAvail();

        if (!isConFact || !isCustomer || !isItem || !isPrcMaster || !isPrice
                || !isMSDB || !isUPC) {
            isAvail = false;
        } else {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isDataAvailableForRecipt() {
        boolean isAvail = false;
        boolean isCustomer = isCustAvail();
        boolean isReciptType = isReciptTypeAvail();
        boolean isPayment = isPaymentAvail();

        if (!isCustomer || !isReciptType || !isPayment) {
            isAvail = false;
        } else {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isConvFactAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM "
                + MspDBHelper.CONVFACTOR_TABLE, null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isCustAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM "
                + MspDBHelper.CUSTOMER_TABLE, null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isItemDataAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM " + MspDBHelper.ITEM_TABLE,
                null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isPrcMasterAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM "
                + MspDBHelper.ITEM_PRICE_LIST_MASTER_TABLE, null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isPriceAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM " + MspDBHelper.PRICE_TABLE,
                null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isMspdbAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM " + MspDBHelper.MSPDB_TABLE,
                null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isUPCDataAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM " + MspDBHelper.UPC_TABLE,
                null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
            count = 0;
        }

        return isAvail;
    }

    public boolean isReciptTypeAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM "
                + MspDBHelper.RECEIPTTYPE_TABLE, null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    public boolean isPaymentAvail() {

        boolean isAvail = false;

        int count = 0;

        cursor = db.rawQuery("SELECT COUNT(*) FROM "
                + MspDBHelper.PAYMENT_TABLE, null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }

        cursor.close();

        if (count != 0) {
            isAvail = true;
        }

        return isAvail;
    }

    /* TO CONVERT BITMAP TO BYTE */
    public byte[] convertBitmapToByte(Bitmap mbitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mbitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bMapArray = stream.toByteArray();
        return bMapArray;
    }

    public void updateCompany(HhCompany company, String companyNo) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.COMPANY_NUMBER, company.getCompany_number());
            cv.put(MspDBHelper.COMPANY_NAME, company.getCompany_name());
            cv.put(MspDBHelper.COMPANY_ADDRESS, company.getCompany_address());
            cv.put(MspDBHelper.COMPANY_CITY, company.getCompany_city());
            cv.put(MspDBHelper.COMPANY_STATE, company.getCompany_state());
            cv.put(MspDBHelper.COMPANY_ZIP, company.getCompany_zip());
            cv.put(MspDBHelper.COMPANY_COUNTRY, company.getCompany_country());
            cv.put(MspDBHelper.COMPANY_PHONE, company.getCompany_phone());
            cv.put(MspDBHelper.COMPANY_CONTACT, company.getCompany_contact());
            cv.put(MspDBHelper.COMPANY_ISSTOCKINGUOM,
                    company.getCompany_isstockinguom());
            cv.put(MspDBHelper.COMPANY_TINNO, company.getCompany_tinno());

            db.update(MspDBHelper.COMPANY_TABLE, cv, "number = '" + companyNo
                    + "'", null);

            Log.i("CompanyTable Updated",
                    "Company record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 903";
            msg = "HhCompany Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void updateContractPrice(HhContractPrice01 contPrzRecord,
                                    String customerNo, String companyNo) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.CONT_PRICE_NUMBER,
                    contPrzRecord.getHhContPrc_customernumber());
            cv.put(MspDBHelper.CONT_PRICE_PRICEBY,
                    contPrzRecord.getHhContPrc_prictby());
            cv.put(MspDBHelper.CONT_PRICE_CATEGORY,
                    contPrzRecord.getHhContPrc_category());
            cv.put(MspDBHelper.CONT_PRICE_ITEMNUMBER,
                    contPrzRecord.getHhContPrc_itemnumber());
            cv.put(MspDBHelper.CONT_PRICE_PRICELISTCODE,
                    contPrzRecord.getHhContPrc_pricelistcode());
            cv.put(MspDBHelper.CONT_PRICE_PRICETYPE,
                    contPrzRecord.getHhContPrc_pricetype());
            cv.put(MspDBHelper.CONT_PRICE_CUSTOMERTYPE,
                    contPrzRecord.getHhContPrc_customertype());
            cv.put(MspDBHelper.CONT_PRICE_DISCPERCENT,
                    contPrzRecord.getHhContPrc_discpercent());
            cv.put(MspDBHelper.CONT_PRICE_DISCAMOUNT,
                    contPrzRecord.getHhContPrc_discamount());
            cv.put(MspDBHelper.CONT_PRICE_COSTMETHOD,
                    contPrzRecord.getHhContPrc_costmethod());
            cv.put(MspDBHelper.CONT_PRICE_PLUSAMOUNT,
                    contPrzRecord.getHhContPrc_plusamount());
            cv.put(MspDBHelper.CONT_PRICE_PLUSPERCENT,
                    contPrzRecord.getHhContPrc_pluspercent());
            cv.put(MspDBHelper.CONT_PRICE_FIXEDPRICE,
                    contPrzRecord.getHhContPrc_fixedprice());
            cv.put(MspDBHelper.CONT_PRICE_CALCUNITPRICE,
                    contPrzRecord.getHhContPrc_calcunitprice());
            cv.put(MspDBHelper.CONT_PRICE_USELOWEST,
                    contPrzRecord.getHhContPrc_uselowest());
            cv.put(MspDBHelper.CONT_PRICE_COMPANYCODE,
                    contPrzRecord.getHhContPrc_companycode());

            db.update(MspDBHelper.CONT_PRICE_TABLE, cv, "customerumber = '"
                            + customerNo + "' and companycode='" + companyNo + "'",
                    null);

            Log.i("ContractPrice Updated",
                    "ContractPrice record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 904";
            msg = "HhContractPrice Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhConversionFactor01 getconversionFactor(String number,
                                                    String uomname, String companyNo) {
        companyNo = companyNo.replace("'", "''");
        uomname = uomname.replace("'", "''");
        number = number.replace("'", "''");
        HhConversionFactor01 convFactor = null;

        cursor = db.rawQuery(
                "select * from hhConversionFactor01 where number = '" + number
                        + "' and uomname = '" + uomname
                        + "' and companycode = '" + companyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                convFactor = new HhConversionFactor01();
                convFactor.setHhConversionFactor_number(cursor.getString(0));
                convFactor.setHhConversionFactor_uom_conv_factor(cursor
                        .getDouble(1));
                convFactor.setHhConversionFactor_name(cursor.getString(2));
                convFactor.setHhConversionFactor_companycode(cursor
                        .getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 905";
            msg = "ConversionFactor data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return convFactor;
    }

    public void updateConversionFactor(HhConversionFactor01 convFactorRecord,
                                       String number, String uomname, String companyNo) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.CONVFACTOR_NUMBER,
                    convFactorRecord.getHhConversionFactor_number());
            cv.put(MspDBHelper.CONVFACTOR_UOMCONVFACTOR,
                    convFactorRecord.getHhConversionFactor_uom_conv_factor());
            cv.put(MspDBHelper.CONVFACTOR_UOMNAME,
                    convFactorRecord.getHhConversionFactor_name());
            cv.put(MspDBHelper.CONVFACTOR_COMPANYCODE,
                    convFactorRecord.getHhConversionFactor_companycode());

            db.update(MspDBHelper.CONVFACTOR_TABLE, cv, "number = '" + number
                    + "' and uomname='" + uomname + "' and companycode='"
                    + companyNo + "'", null);

            Log.i("ConversionFactor Updated",
                    "ConversionFactor record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 906";
            msg = "HhConversionFactor Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhCustomerGroup01 getCustomerGroupCode(String groupCode,
                                                  String terms, String companyNo) {
        companyNo = companyNo.replace("'", "''");
        groupCode = groupCode.replace("'", "''");
        terms = terms.replace("'", "''");
        HhCustomerGroup01 custGroupData = null;

        cursor = db.rawQuery(
                "select * from hhCustomerGroup01 where groupcode = '"
                        + groupCode + "' and terms = '" + terms
                        + "' and companycode = '" + companyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                custGroupData = new HhCustomerGroup01();
                custGroupData
                        .setHhCustomerGroup_group_code(cursor.getString(0));
                custGroupData.setHhCustomerGroup_description(cursor
                        .getString(1));
                custGroupData.setHhCustomerGroup_terms(cursor.getString(2));
                custGroupData.setHhCustomerGroup_companycode(cursor
                        .getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 907";
            msg = "CustomerGroupCode data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return custGroupData;
    }

    public void updateGroupRecord(HhCustomerGroup01 custGroupRecord,
                                  String groupCode, String terms, String companyNo) {
        companyNo = companyNo.replace("'", "''");
        terms = terms.replace("'", "''");
        groupCode = groupCode.replace("'", "''");

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.CUSGCODE_GROUPCODE,
                    custGroupRecord.getHhCustomerGroup_group_code());
            cv.put(MspDBHelper.CUSGCODE_DESCRIPTION,
                    custGroupRecord.getHhCustomerGroup_description());
            cv.put(MspDBHelper.CUSGCODE_TERMS,
                    custGroupRecord.getHhCustomerGroup_terms());
            cv.put(MspDBHelper.CUSGCODE_COMPANYCODE,
                    custGroupRecord.getHhCustomerGroup_companycode());

            db.update(MspDBHelper.CUSTOMER_GROUPCODE_TABLE, cv, "groupcode = '"
                    + groupCode + "' and terms='" + terms
                    + "' and companycode='" + companyNo + "'", null);

            Log.i("CustomerGroupCode Updated",
                    "CustomerGroupCode record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 908";
            msg = "HhCustomerGroupCode Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhItemPriceTax01 getItemPrzTax(String number, String priceList,
                                          String companyNo) {

        companyNo = companyNo.replace("'", "''");
        number = number.replace("'", "''");
        priceList = priceList.replace("'", "''");

        HhItemPriceTax01 itmPrzTaxData = null;

        cursor = db.rawQuery("select * from hhItemPriceTax01 where number = '"
                + number + "' and pricelist = '" + priceList
                + "' and companycode = '" + companyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                itmPrzTaxData = new HhItemPriceTax01();

                itmPrzTaxData.setHhItemPriceTax_number(cursor.getString(0));
                itmPrzTaxData.setHhItemPriceTax_pricelist(cursor.getString(1));
                itmPrzTaxData.setHhItemPriceTax_authority(cursor.getString(2));
                itmPrzTaxData.setHhItemPriceTax_salestaxclass(cursor.getInt(3));
                itmPrzTaxData
                        .setHhItemPriceTax_companycode(cursor.getString(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 909";
            msg = "ItemPriceTax data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return itmPrzTaxData;
    }

    public void updateItemPriceTaxRecord(HhItemPriceTax01 itmPrzTaxRecord,
                                         String number, String priceList, String companyNo) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.ITMPRCTAX_NUMBER,
                    itmPrzTaxRecord.getHhItemPriceTax_number());
            cv.put(MspDBHelper.ITMPRCTAX_PRICELIST,
                    itmPrzTaxRecord.getHhItemPriceTax_pricelist());
            cv.put(MspDBHelper.ITMPRCTAX_AUTHORITY,
                    itmPrzTaxRecord.getHhItemPriceTax_authority());
            cv.put(MspDBHelper.ITMPRCTAX_SALESTAXCLASS,
                    itmPrzTaxRecord.getHhItemPriceTax_salestaxclass());
            cv.put(MspDBHelper.ITMPRCTAX_COMPANYCODE,
                    itmPrzTaxRecord.getHhItemPriceTax_companycode());

            db.update(MspDBHelper.ITMPRCTAX_TABLE, cv, "number = '" + number
                    + "' and pricelist='" + priceList + "' and companycode='"
                    + companyNo + "'", null);

            Log.i("ItemPriceTax Updated",
                    "ItemPriceTax record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 910";
            msg = "ItemPriceTax Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhItemTax01 getItemTax(String itemNumber, String authority,
                                  String companyNo) {
        companyNo = companyNo.replace("'", "''");
        itemNumber = itemNumber.replace("'", "''");
        authority = authority.replace("'", "''");

        HhItemTax01 itmTaxData = null;

        cursor = db.rawQuery("select * from hhItemTax01 where itemnumber = '"
                + itemNumber + "' and authority = '" + authority
                + "' and companycode = '" + companyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                itmTaxData = new HhItemTax01();

                itmTaxData.setHhItemTax_number(cursor.getString(0));
                itmTaxData.setHhItemTax_authority(cursor.getString(1));
                itmTaxData.setHhItemTax_salestaxclass(cursor.getInt(2));
                itmTaxData.setHhItemTax_companycode(cursor.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 911";
            msg = "ItemTax data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return itmTaxData;
    }

    public void updateItemTaxRecord(HhItemTax01 itmTaxRecord,
                                    String itemNumber, String authority, String companyNo) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.ITEMTAX_ITEM_NUMBER,
                    itmTaxRecord.getHhItemTax_number());
            cv.put(MspDBHelper.ITEMTAX_AUTHORITY,
                    itmTaxRecord.getHhItemTax_authority());
            cv.put(MspDBHelper.ITEMTAX_SALESTAXCLASS,
                    itmTaxRecord.getHhItemTax_salestaxclass());
            cv.put(MspDBHelper.ITEMTAX_COMPANYCODE,
                    itmTaxRecord.getHhItemTax_companycode());

            db.update(MspDBHelper.ITEMTAX_TABLE, cv, "itemnumber = '"
                    + itemNumber + "' and authority='" + authority
                    + "' and companycode='" + companyNo + "'", null);

            Log.i("ItemTax Updated", "ItemTax record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 912";
            msg = "ItemTax Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void updateItemRecord(HhItem01 itemRecord, String number,
                                 String locid, String companyNo) {
        try {

            Manf_Number01 manf = new Manf_Number01();
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.ITEM_NUMBER, itemRecord.getHhItem_number());
            cv.put(MspDBHelper.ITEM_DESCRIPTION,
                    itemRecord.getHhItem_description());
            cv.put(MspDBHelper.ITEM_CATEGORY, itemRecord.getHhItem_category());
            cv.put(MspDBHelper.ITEM_LOCID, itemRecord.getHhItem_loc_id());
            cv.put(MspDBHelper.ITEM_QTYONHAND,
                    itemRecord.getHhItem_qty_on_hand());
            cv.put(MspDBHelper.ITEM_CURRENCY, itemRecord.getHhItem_currency());
            cv.put(MspDBHelper.ITEM_PRICELISTCODE,
                    itemRecord.getHhItem_price_list_code());
            cv.put(MspDBHelper.ITEM_ALTITEM, itemRecord.getHhItem_alt_itm());
            cv.put(MspDBHelper.ITEM_ALTITEMDESC,
                    itemRecord.getHhItem_alt_itm_desc());
            cv.put(MspDBHelper.ITEM_COSTINGMETHOD,
                    itemRecord.getHhItem_costing_method());
            cv.put(MspDBHelper.ITEM_STANDARDCOST,
                    itemRecord.getHhItem_standard_cost());
            cv.put(MspDBHelper.ITEM_MOSTRECENTCOST,
                    itemRecord.getHhItem_most_recent_cost());
            cv.put(MspDBHelper.ITEM_AVERAGECOST,
                    itemRecord.getHhItem_avg_cost());
            cv.put(MspDBHelper.ITEM_LASTUNITCOST,
                    itemRecord.getHhItem_last_unit_cost());
            cv.put(MspDBHelper.ITEM_COST1, itemRecord.getHhItem_cost1());
            cv.put(MspDBHelper.ITEM_COST2, itemRecord.getHhItem_cost2());
            cv.put(MspDBHelper.ITEM_STOCKINGUOM,
                    itemRecord.getHhItem_stocking_uom());
            cv.put(MspDBHelper.ITEM_STOCKINGCONVFACTOR,
                    itemRecord.getHhItem_stocking_conv_factor());
            cv.put(MspDBHelper.ITEM_LOCUOM, itemRecord.getHhItem_loc_uom());
            cv.put(MspDBHelper.ITEM_LOCCONVFACTOR,
                    itemRecord.getHhItem_location_conv_factor());
            cv.put(MspDBHelper.ITEM_CARTONDISC,
                    itemRecord.getHhItem_cart_on_disc());
            cv.put(MspDBHelper.ITEM_UNITWGT, itemRecord.getHhItem_unitwgt());
            // stock item
			/*
			 * cv.put(MspDBHelper.ITEM_STOCKITEM,
			 * itemRecord.getHhItem_stockitem());
			 */
            cv.put(MspDBHelper.ITEM_COMPANYCODE,
                    itemRecord.getHhItem_companycode());
            db.update(MspDBHelper.ITEM_TABLE, cv, "number = '" + number
                    + "' and locid='" + locid + "' and companycode='"
                    + companyNo + "'", null);

            Log.i("Item Updated", "Item record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 913";
            msg = "Item Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    public HhLocMast01 getLocationData(String location, String companyNo) {

        companyNo = companyNo.replace("'", "''");
        location = location.replace("'", "''");
        HhLocMast01 locData = null;

        cursor = db.rawQuery("select * from hhLocMast01 where location = '"
                + location + "' and companycode = '" + companyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                locData = new HhLocMast01();

                locData.setHhLocMast_location(cursor.getString(0));
                locData.setHhLocMast_companycode(cursor.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 914";
            msg = "Location data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return locData;

    }

    public void updateLocationData(HhLocMast01 locRecord, String location,
                                   String companyNo) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.LOCMAST_LOCATION,
                    locRecord.getHhLocMast_location());
            cv.put(MspDBHelper.LOCMAST_COMPANYCODE,
                    locRecord.getHhLocMast_companycode());

            db.update(MspDBHelper.LOCMAST_TABLE, cv, "location = '" + location
                    + "' and companycode='" + companyNo + "'", null);

            Log.i("Location Updated", "Location record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 915";
            msg = "Location Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    public void updateManagerData(HhManager managerRecord, String userid,
                                  String companyNo) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.MANAGER_COMPANYID,
                    managerRecord.getHhManager_companyid());
            cv.put(MspDBHelper.MANAGER_USERID,
                    managerRecord.getHhManager_userid());
            cv.put(MspDBHelper.MANAGER_USERPASS,
                    managerRecord.getHhManager_userpass());
            cv.put(MspDBHelper.MANAGER_ACTIVEKEY,
                    managerRecord.getHhManager_activekey());
            cv.put(MspDBHelper.MANAGER_USERNAME,
                    managerRecord.getHhManager_username());
            cv.put(MspDBHelper.MANAGER_LOCID,
                    managerRecord.getHhManager_locid());
            cv.put(MspDBHelper.MANAGER_EMAIL,
                    managerRecord.getHhManager_email());
            cv.put(MspDBHelper.MANAGER_CURRENCY,
                    managerRecord.getHhManager_currency());
            cv.put(MspDBHelper.MANAGER_PRICELISTCODE,
                    managerRecord.getHhManager_pricelistcode());
            cv.put(MspDBHelper.MANAGER_COMPANYNUMBER,
                    managerRecord.getHhManager_companynumber());
            cv.put(MspDBHelper.MANAGER_COMPANYNAME,
                    managerRecord.getHhManager_companyname());
            cv.put(MspDBHelper.MANAGER_COMPANYMAILID,
                    managerRecord.getHhManager_companymailid());
            cv.put(MspDBHelper.MANAGER_UNITPRICEEDITABLE,
                    managerRecord.getHhManager_unitpriceeditable());
            cv.put(MspDBHelper.MANAGER_ACCPACVERSION,
                    managerRecord.getHhManager_accpacversion());
            cv.put(MspDBHelper.MANAGER_ADDCUSTOMERSTATE,
                    managerRecord.getHhManager_addcustomerstate());
            cv.put(MspDBHelper.MANAGER_CUSTOMERBASEDPL,
                    managerRecord.getHhManager_customerbasedpl());
            cv.put(MspDBHelper.MANAGER_ADMINPASS,
                    managerRecord.getHhManager_adminpass());

            db.update(MspDBHelper.MANAGER_TABLE, cv, "userid = '" + userid
                    + "' and companynumber='" + companyNo + "'", null);

            Log.i("Manager Updated", "Manager record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 916";
            msg = "Manager Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    public HhMultiUOM01 getmultiUOMData(String number, String location,
                                        String companyNo) {

        companyNo = companyNo.replace("'", "''");
        location = location.replace("'", "''");
        HhMultiUOM01 multiUomData = null;

        cursor = db.rawQuery("select * from hhMultiUOM01 where number = '"
                + number + "' and location='" + location
                + "' and companycode = '" + companyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                multiUomData = new HhMultiUOM01();

                multiUomData.setHhMultiUOM_number(cursor.getString(0));
                multiUomData.setHhMultiUOM_qtyuom(cursor.getString(1));
                multiUomData.setHhMultiUOM_disctype(cursor.getInt(2));
                multiUomData.setHhMultiUOM_price1(cursor.getDouble(3));
                multiUomData.setHhMultiUOM_price2(cursor.getDouble(4));
                multiUomData.setHhMultiUOM_price3(cursor.getDouble(5));
                multiUomData.setHhMultiUOM_price4(cursor.getDouble(6));
                multiUomData.setHhMultiUOM_price5(cursor.getDouble(7));
                multiUomData.setHhMultiUOM_price6(cursor.getDouble(8));
                multiUomData.setHhMultiUOM_qtylevel1(cursor.getDouble(9));
                multiUomData.setHhMultiUOM_qtylevel2(cursor.getDouble(10));
                multiUomData.setHhMultiUOM_qtylevel3(cursor.getDouble(11));
                multiUomData.setHhMultiUOM_qtylevel4(cursor.getDouble(12));
                multiUomData.setHhMultiUOM_qtylevel5(cursor.getDouble(13));
                multiUomData.setHhMultiUOM_markupcost(cursor.getDouble(14));
                multiUomData.setHhMultiUOM_pricelistcode(cursor.getString(15));
                multiUomData.setHhMultiUOM_location(cursor.getString(16));
                multiUomData.setHhMultiUOM_pricinguom(cursor.getString(17));
                multiUomData.setHhMultiUOM_pricingconvfactor(cursor
                        .getDouble(18));
                multiUomData.setHhMultiUOM_markupuom(cursor.getString(19));
                multiUomData.setHhMultiUOM_markupconvfactor(cursor
                        .getDouble(20));
                multiUomData.setHhMultiUOM_companycode(cursor.getString(21));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 917";
            msg = "Location data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return multiUomData;
    }

    public void updateMultiUomData(HhMultiUOM01 multiUomRecord, String number,
                                   String location, String companyNo) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.MULTIUOM_NUMBER,
                    multiUomRecord.getHhMultiUOM_number());
            cv.put(MspDBHelper.MULTIUOM_QTYUOM,
                    multiUomRecord.getHhMultiUOM_qtyuom());
            cv.put(MspDBHelper.MULTIUOM_DISCTYPE,
                    multiUomRecord.getHhMultiUOM_disctype());
            cv.put(MspDBHelper.MULTIUOM_PRICE1,
                    multiUomRecord.getHhMultiUOM_price1());
            cv.put(MspDBHelper.MULTIUOM_PRICE2,
                    multiUomRecord.getHhMultiUOM_price2());
            cv.put(MspDBHelper.MULTIUOM_PRICE3,
                    multiUomRecord.getHhMultiUOM_price3());
            cv.put(MspDBHelper.MULTIUOM_PRICE4,
                    multiUomRecord.getHhMultiUOM_price4());
            cv.put(MspDBHelper.MULTIUOM_PRICE5,
                    multiUomRecord.getHhMultiUOM_price5());
            cv.put(MspDBHelper.MULTIUOM_PRICE6,
                    multiUomRecord.getHhMultiUOM_price6());
            cv.put(MspDBHelper.MULTIUOM_QTYLEVEL1,
                    multiUomRecord.getHhMultiUOM_qtylevel1());
            cv.put(MspDBHelper.MULTIUOM_QTYLEVEL2,
                    multiUomRecord.getHhMultiUOM_qtylevel2());
            cv.put(MspDBHelper.MULTIUOM_QTYLEVEL3,
                    multiUomRecord.getHhMultiUOM_qtylevel3());
            cv.put(MspDBHelper.MULTIUOM_QTYLEVEL4,
                    multiUomRecord.getHhMultiUOM_qtylevel4());
            cv.put(MspDBHelper.MULTIUOM_QTYLEVEL5,
                    multiUomRecord.getHhMultiUOM_qtylevel5());
            cv.put(MspDBHelper.MULTIUOM_MARKUPCOST,
                    multiUomRecord.getHhMultiUOM_markupcost());
            cv.put(MspDBHelper.MULTIUOM_PRICELISTCODE,
                    multiUomRecord.getHhMultiUOM_pricelistcode());
            cv.put(MspDBHelper.MULTIUOM_LOCATION,
                    multiUomRecord.getHhMultiUOM_location());
            cv.put(MspDBHelper.MULTIUOM_PRICINGUOM,
                    multiUomRecord.getHhMultiUOM_pricinguom());
            cv.put(MspDBHelper.MULTIUOM_PRICINGCONVFACTOR,
                    multiUomRecord.getHhMultiUOM_pricingconvfactor());
            cv.put(MspDBHelper.MULTIUOM_MARKUPUOM,
                    multiUomRecord.getHhMultiUOM_markupuom());
            cv.put(MspDBHelper.MULTIUOM_MARKUPCONVFACTOR,
                    multiUomRecord.getHhMultiUOM_markupconvfactor());
            cv.put(MspDBHelper.MULTIUOM_COMPANYCODE,
                    multiUomRecord.getHhMultiUOM_companycode());

            db.update(MspDBHelper.MULTIUOM_TABLE, cv, "number = '" + number
                    + "' and location='" + location + "' and companycode='"
                    + companyNo + "'", null);

            Log.i("MultiUOM Updated", "MultiUOM record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 918";
            msg = "MultiUOM Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhPayment01 getPaymentData(String cusnumber, String documentnumber,
                                      String companyNo) {
        HhPayment01 paymentData = null;
        documentnumber = documentnumber.replace("'", "''");
        companyNo = companyNo.replace("'", "''");
        cursor = db.rawQuery("select * from hhPayment01 where cusnumber = '"
                + cusnumber + "' and documentnumber='" + documentnumber
                + "' and companycode = '" + companyNo + "'", null);

        try {
            while (cursor.moveToNext()) {
                paymentData = new HhPayment01();

                paymentData.setHhPayment_apply1(cursor.getString(0));
                paymentData.setHhPayment_cusnumber(cursor.getString(1));
                paymentData.setHhPayment_cusname(cursor.getString(2));
                paymentData.setHhPayment_documentnumber(cursor.getString(3));
                paymentData.setHhPayment_pendingbalance(cursor.getFloat(4));
                paymentData.setHhPayment_appliedamount(cursor.getString(5));
                paymentData.setHhPayment_netamount(cursor.getFloat(6));
                paymentData.setHhPayment_status(cursor.getInt(7));
                paymentData.setHhPayment_companycode(cursor.getString(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 919";
            msg = "Payment data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return paymentData;
    }

    public void updatePaymentData(HhPayment01 paymentRecord, String cusnumber,
                                  String documentnumber, String companyNo) {
        documentnumber = documentnumber.replace("'", "''");
        companyNo = companyNo.replace("'", "''");
        cusnumber = cusnumber.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.PAYMENT_APPLY1,
                    paymentRecord.getHhPayment_apply1());
            cv.put(MspDBHelper.PAYMENT_CUSNUMBER,
                    paymentRecord.getHhPayment_cusnumber());
            cv.put(MspDBHelper.PAYMENT_CUSNAME,
                    paymentRecord.getHhPayment_cusname());
            cv.put(MspDBHelper.PAYMENT_DOCUMENTNUMBER,
                    paymentRecord.getHhPayment_documentnumber());
            cv.put(MspDBHelper.PAYMENT_PENDINGBALANCE,
                    paymentRecord.getHhPayment_pendingbalance());
            cv.put(MspDBHelper.PAYMENT_APPLIEDAMOUNT,
                    paymentRecord.getHhPayment_appliedamount());
            cv.put(MspDBHelper.PAYMENT_NETAMOUNT,
                    paymentRecord.getHhPayment_netamount());
            cv.put(MspDBHelper.PAYMENT_STATUS,
                    paymentRecord.getHhPayment_status());
            cv.put(MspDBHelper.PAYMENT_COMPANYCODE,
                    paymentRecord.getHhPayment_companycode());

            db.update(MspDBHelper.PAYMENT_TABLE, cv, "cusnumber = '"
                    + cusnumber + "' and documentnumber='" + documentnumber
                    + "' and companycode='" + companyNo + "'", null);

            Log.i("Payment Updated", "Payment record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 920";
            msg = "Payment Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void updatePriceData(HhPrice01 priceRecord, String pricelistcode,
                                String number, String companyNo) {

        companyNo = companyNo.replace("'", "''");
        number = number.replace("'", "''");
        pricelistcode = pricelistcode.replace("'", "''");


        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.PRICE_NUMBER, priceRecord.getHhPrice_number());
            cv.put(MspDBHelper.PRICE_DISCTYPE,
                    priceRecord.getHhPrice_disctype());
            cv.put(MspDBHelper.PRICE_PRICE1, priceRecord.getHhPrice_price1());
            cv.put(MspDBHelper.PRICE_PRICE2, priceRecord.getHhPrice_price2());
            cv.put(MspDBHelper.PRICE_PRICE3, priceRecord.getHhPrice_price3());
            cv.put(MspDBHelper.PRICE_PRICE4, priceRecord.getHhPrice_price4());
            cv.put(MspDBHelper.PRICE_PRICE5, priceRecord.getHhPrice_price5());
            cv.put(MspDBHelper.PRICE_PRICE6, priceRecord.getHhPrice_price6());
            cv.put(MspDBHelper.PRICE_QTYLEVEL1,
                    priceRecord.getHhPrice_qtylevel1());
            cv.put(MspDBHelper.PRICE_QTYLEVEL2,
                    priceRecord.getHhPrice_qtylevel2());
            cv.put(MspDBHelper.PRICE_QTYLEVEL3,
                    priceRecord.getHhPrice_qtylevel3());
            cv.put(MspDBHelper.PRICE_QTYLEVEL4,
                    priceRecord.getHhPrice_qtylevel4());
            cv.put(MspDBHelper.PRICE_QTYLEVEL5,
                    priceRecord.getHhPrice_qtylevel5());
            cv.put(MspDBHelper.PRICE_MARKUPCOST,
                    priceRecord.getHhPrice_markup_cost());
            cv.put(MspDBHelper.PRICE_PRICELISTCODE,
                    priceRecord.getHhPrice_price_list_code());
            cv.put(MspDBHelper.PRICE_LOCATION,
                    priceRecord.getHhPrice_location());
            cv.put(MspDBHelper.PRICE_PRICINGUOM,
                    priceRecord.getHhPrice_pricing_uom());
            cv.put(MspDBHelper.PRICE_PRICINGCONVFACTOR,
                    priceRecord.getHhPrice_pricing_conv_factor());
            cv.put(MspDBHelper.PRICE_MARKUPUOM,
                    priceRecord.getHhPrice_markup_uom());
            cv.put(MspDBHelper.PRICE_MARKUPCONVFACTOR,
                    priceRecord.getHhPrice_markup_conv_factor());
            cv.put(MspDBHelper.PRICE_PRICETYPE,
                    priceRecord.getHhPrice_price_type());
            cv.put(MspDBHelper.PRICE_SALESPRICE,
                    priceRecord.getHhPrice_sales_price());
            cv.put(MspDBHelper.PRICE_SALESUOM,
                    priceRecord.getHhPrice_sales_uom());
            cv.put(MspDBHelper.PRICE_SALESCONVFACTOR,
                    priceRecord.getHhPrice_sales_conv_factor());
            cv.put(MspDBHelper.PRICE_SALESSTARTDATE,
                    priceRecord.getHhPrice_sales_startdate());
            cv.put(MspDBHelper.PRICE_SALESENDDATE,
                    priceRecord.getHhPrice_sales_enddate());
            cv.put(MspDBHelper.PRICE_COMPANYCODE,
                    priceRecord.getHhPrice_companycode());

            db.update(MspDBHelper.PRICE_TABLE, cv, "number = '" + number
                    + "' and pricelistcode='" + pricelistcode
                    + "' and companycode='" + companyNo + "'", null);

            Log.i("Price Updated", "Price record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 921";
            msg = "Price Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhPriceListMaster01 getPriceListData(String pricelist,
                                                String companycode) {

        companycode = companycode.replace("'", "''");
        pricelist = pricelist.replace("'", "''");

        HhPriceListMaster01 priceListData = null;

        cursor = db.rawQuery(
                "select * from hhPriceListMaster01 where pricelist = '"
                        + pricelist + "' and companycode = '" + companycode
                        + "'", null);

        try {
            while (cursor.moveToNext()) {
                priceListData = new HhPriceListMaster01();

                priceListData.setPriceList(cursor.getString(0));
                priceListData.setPriceList_companycode(cursor.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 922";
            msg = "PriceListMaster data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return priceListData;
    }

    public void updatePriceListData(HhPriceListMaster01 priceListRecord,
                                    String pricelist, String companycode) {
        companycode = companycode.replace("'", "''");
        pricelist = pricelist.replace("'", "''");

        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.ITEM_PRICE_LIST, priceListRecord.getPriceList());
            cv.put(MspDBHelper.ITEM_PRICE_LIST_COMPANYCODE,
                    priceListRecord.getPriceList_companycode());

            db.update(MspDBHelper.ITEM_PRICE_LIST_MASTER_TABLE, cv,
                    "pricelist = '" + pricelist + "' and companycode='"
                            + companycode + "'", null);

            Log.i("PriceListMaster Updated",
                    "PriceListMaster record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 923";
            msg = "PriceListMaster Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhReceiptType01 getReceiptTypeData(String receipttype,
                                              String companycode) {
        companycode = companycode.replace("'", "''");
        receipttype = receipttype.replace("'", "''");
        HhReceiptType01 receiptTypeData = null;

        cursor = db.rawQuery(
                "select * from hhReceiptType01 where receipttype = '"
                        + receipttype + "' and companycode = '" + companycode
                        + "'", null);

        try {
            while (cursor.moveToNext()) {
                receiptTypeData = new HhReceiptType01();

                receiptTypeData.setHhReceiptType_receipttype(cursor
                        .getString(0));
                receiptTypeData.setHhReceiptType_receiptname(cursor
                        .getString(1));
                receiptTypeData.setHhReceiptType_companycode(cursor
                        .getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 924";
            msg = "ReceiptType data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return receiptTypeData;
    }

    public void updateReceiptTypeData(HhReceiptType01 receiptTypeRecord,
                                      String receipttype, String companycode) {

        companycode = companycode.replace("'", "''");
        receipttype = receipttype.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.RECEIPTTYPE_RECEIPTTYPE,
                    receiptTypeRecord.getHhReceiptType_receipttype());
            cv.put(MspDBHelper.RECEIPTTYPE_RECEIPTNAME,
                    receiptTypeRecord.getHhReceiptType_receiptname());
            cv.put(MspDBHelper.RECEIPTTYPE_COMPANYCODE,
                    receiptTypeRecord.getHhReceiptType_companycode());

            db.update(MspDBHelper.RECEIPTTYPE_TABLE, cv, "receipttype = '"
                            + receipttype + "' and companycode='" + companycode + "'",
                    null);

            Log.i("ReceiptType Updated",
                    "ReceiptType record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 925";
            msg = "ReceiptType Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhShipVia01 getShipViaData(String code, String companycode) {

        companycode = companycode.replace("'", "''");
        code = code.replace("'", "''");
        HhShipVia01 shipViaData = null;

        cursor = db.rawQuery("select * from hhShipVia01 where code = '" + code
                + "' and companycode = '" + companycode + "'", null);

        try {
            while (cursor.moveToNext()) {
                shipViaData = new HhShipVia01();

                shipViaData.setHhShipVia_code(cursor.getString(0));
                shipViaData.setHhShipVia_name(cursor.getString(1));
                shipViaData.setHhShipVia_companycode(cursor.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 926";
            msg = "ShipVia data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return shipViaData;
    }

    public void updateShipViaData(HhShipVia01 shipViaRecord, String code,
                                  String companycode) {
        companycode = companycode.replace("'", "''");
        code = code.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.SHIPVIA_CODE, shipViaRecord.getHhShipVia_code());
            cv.put(MspDBHelper.SHIPVIA_NAME, shipViaRecord.getHhShipVia_name());
            cv.put(MspDBHelper.SHIPVIA_COMPANYCODE,
                    shipViaRecord.getHhShipVia_companycode());

            db.update(MspDBHelper.SHIPVIA_TABLE, cv, "code = '" + code
                    + "' and companycode='" + companycode + "'", null);

            Log.i("ShipVia Updated", "ShipVia record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 927";
            msg = "ShipVia Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhTaxClass01 getTaxClassData(String taxauthority, String taxstts,
                                        String companycode) {

        companycode = companycode.replace("'", "''");
        taxauthority = taxauthority.replace("'", "''");
        taxstts = taxstts.replace("'", "''");

        HhTaxClass01 taxClassData = null;

        cursor = db.rawQuery(
                "select * from hhTaxClass01 where taxauthority = '"
                        + taxauthority + "' and taxstts = '" + taxstts
                        + "' and companycode = '" + companycode + "'", null);

        try {
            while (cursor.moveToNext()) {
                taxClassData = new HhTaxClass01();

                taxClassData.setHhTaxClass_taxauthority(cursor.getString(0));
                taxClassData.setHhTaxClass_taxstts(cursor.getString(1));
                taxClassData.setHhTaxClass_description(cursor.getString(2));
                taxClassData.setHhTaxClass_companycode(cursor.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 928";
            msg = "TaxClass data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return taxClassData;
    }

    public void updateTaxClassData(HhTaxClass01 taxClassRecord,
                                   String taxauthority, String taxstts, String companycode) {
        companycode = companycode.replace("'", "''");
        taxauthority = taxauthority.replace("'", "''");
        taxstts = taxstts.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TAXCLASS_TAXAUTHORITY,
                    taxClassRecord.getHhTaxClass_taxauthority());
            cv.put(MspDBHelper.TAXCLASS_TAXSTTS,
                    taxClassRecord.getHhTaxClass_taxstts());
            cv.put(MspDBHelper.TAXCLASS_DESCRIPTION,
                    taxClassRecord.getHhTaxClass_description());
            cv.put(MspDBHelper.TAXCLASS_COMPANYCODE,
                    taxClassRecord.getHhTaxClass_companycode());

            db.update(MspDBHelper.TAXCLASS_TABLE, cv, "taxauthority = '"
                    + taxauthority + "' and taxstts='" + taxstts
                    + "' and companycode='" + companycode + "'", null);

            Log.i("TaxClass Updated", "TaxClass record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 929";
            msg = "TaxClass Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhRoute01 getRouteData(String customernumber, String companycode) {
        companycode = companycode.replace("'", "''");
        customernumber = customernumber.replace("'", "''");

        HhRoute01 routeData = null;

        cursor = db.rawQuery("select * from hhRoute01 where customernumber = '"
                        + customernumber + "' and companycode = '" + companycode + "'",
                null);

        try {
            while (cursor.moveToNext()) {
                routeData = new HhRoute01();

                routeData.setHhRoute_customernumber(cursor.getString(0));
                routeData.setHhRoute_customername(cursor.getString(1));
                routeData.setHhRoute_dayofvisit(cursor.getInt(2));
                routeData.setHhRoute_monthofvisit(cursor.getInt(3));
                routeData.setHhRoute_yearofvisit(cursor.getInt(4));
                routeData.setHhRoute_visitstat(cursor.getInt(5));
                routeData.setHhRoute_companycode(cursor.getString(6));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 930";
            msg = "Route data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return routeData;

    }

    public void updateRouteData(HhRoute01 routeRecord, String customernumber,
                                String companycode) {
        companycode = companycode.replace("'", "''");
        customernumber = customernumber.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.ROUTE_CUSTOMERNUMBER,
                    routeRecord.getHhRoute_customernumber());
            cv.put(MspDBHelper.ROUTE_CUSTOMERNAME,
                    routeRecord.getHhRoute_customername());
            cv.put(MspDBHelper.ROUTE_DAYOFVISIT,
                    routeRecord.getHhRoute_dayofvisit());
            cv.put(MspDBHelper.ROUTE_MONTHOFVISIT,
                    routeRecord.getHhRoute_monthofvisit());
            cv.put(MspDBHelper.ROUTE_YEAROFVISIT,
                    routeRecord.getHhRoute_yearofvisit());
            cv.put(MspDBHelper.ROUTE_VISITSTAT,
                    routeRecord.getHhRoute_visitstat());
            cv.put(MspDBHelper.ROUTE_COMPANYCODE,
                    routeRecord.getHhRoute_companycode());

            db.update(MspDBHelper.ROUTE_TABLE, cv, "customernumber = '"
                    + customernumber + "' and companycode='" + companycode
                    + "'", null);

            Log.i("Route Updated", "Route record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 931";
            msg = "Route Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhShipTo01 getShipToData(String customernumber, String companycode) {
        companycode = companycode.replace("'", "''");
        customernumber = customernumber.replace("'", "''");
        HhShipTo01 shipToData = null;

        cursor = db.rawQuery(
                "select * from hhShipTo01 where customernumber = '"
                        + customernumber + "' and companycode = '"
                        + companycode + "'", null);

        try {
            while (cursor.moveToNext()) {
                shipToData = new HhShipTo01();

                shipToData.setHhShipTo_customerNumber(cursor.getString(0));
                shipToData.setHhShipTo_primaryShipTo(cursor.getInt(1));
                shipToData.setHhShipTo_locCode(cursor.getString(2));
                shipToData.setHhShipTo_description(cursor.getString(3));
                shipToData.setHhShipTo_ship_via(cursor.getString(4));
                shipToData.setHhShipTo_via_description(cursor.getString(5));
                shipToData.setHhShipTo_address(cursor.getString(6));
                shipToData.setHhShipTo_city(cursor.getString(7));
                shipToData.setHhShipTo_state(cursor.getString(8));
                shipToData.setHhShipTo_zip(cursor.getString(9));
                shipToData.setHhShipTo_country(cursor.getString(10));
                shipToData.setHhShipTo_contact(cursor.getString(11));
                shipToData.setHhShipTo_tax_group(cursor.getString(12));
                shipToData.setHhShipTo_taxauthority1(cursor.getString(13));
                shipToData.setHhShipTo_taxable1(cursor.getInt(14));
                shipToData.setHhShipTo_taxstts1(cursor.getString(15));
                shipToData.setHhShipTo_taxauthority2(cursor.getString(16));
                shipToData.setHhShipTo_taxable2(cursor.getInt(17));
                shipToData.setHhShipTo_taxstts2(cursor.getString(18));
                shipToData.setHhShipTo_taxauthority3(cursor.getString(19));
                shipToData.setHhShipTo_taxable3(cursor.getInt(20));
                shipToData.setHhShipTo_taxstts3(cursor.getString(21));
                shipToData.setHhShipTo_taxauthority4(cursor.getString(22));
                shipToData.setHhShipTo_taxable4(cursor.getInt(23));
                shipToData.setHhShipTo_taxstts4(cursor.getString(24));
                shipToData.setHhShipTo_taxauthority5(cursor.getString(25));
                shipToData.setHhShipTo_taxable5(cursor.getInt(26));
                shipToData.setHhShipTo_taxstts5(cursor.getString(27));
                shipToData.setHhShipTo_companycode(cursor.getString(28));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 932";
            msg = "ShipTo data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return shipToData;
    }

    public void updateShipToData(HhShipTo01 shipToDataRecord,
                                 String customernumber, String companycode) {
        companycode = companycode.replace("'", "''");
        customernumber = customernumber.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.SHIPTO_CUSTOMERNUMBER,
                    shipToDataRecord.getHhShipTo_customerNumber());
            cv.put(MspDBHelper.SHIPTO_PRIMARYSHIPTO,
                    shipToDataRecord.getHhShipTo_primaryShipTo());
            cv.put(MspDBHelper.SHIPTO_LOCCODE,
                    shipToDataRecord.getHhShipTo_locCode());
            cv.put(MspDBHelper.SHIPTO_DESCRIPTION,
                    shipToDataRecord.getHhShipTo_description());
            cv.put(MspDBHelper.SHIPTO_SHIPVIA,
                    shipToDataRecord.getHhShipTo_ship_via());
            cv.put(MspDBHelper.SHIPTO_VIADESCRIPTION,
                    shipToDataRecord.getHhShipTo_via_description());
            cv.put(MspDBHelper.SHIPTO_ADDRESS,
                    shipToDataRecord.getHhShipTo_address());
            cv.put(MspDBHelper.SHIPTO_CITY, shipToDataRecord.getHhShipTo_city());
            cv.put(MspDBHelper.SHIPTO_STATE,
                    shipToDataRecord.getHhShipTo_state());
            cv.put(MspDBHelper.SHIPTO_ZIP, shipToDataRecord.getHhShipTo_zip());
            cv.put(MspDBHelper.SHIPTO_COUNTRY,
                    shipToDataRecord.getHhShipTo_country());
            cv.put(MspDBHelper.SHIPTO_CONTACT,
                    shipToDataRecord.getHhShipTo_contact());
            cv.put(MspDBHelper.SHIPTO_TAXGROUP,
                    shipToDataRecord.getHhShipTo_tax_group());
            cv.put(MspDBHelper.SHIPTO_TAXAUTHORITY1,
                    shipToDataRecord.getHhShipTo_taxauthority1());
            cv.put(MspDBHelper.SHIPTO_TAXABLE1,
                    shipToDataRecord.getHhShipTo_taxable1());
            cv.put(MspDBHelper.SHIPTO_TAXSTTS1,
                    shipToDataRecord.getHhShipTo_taxstts1());
            cv.put(MspDBHelper.SHIPTO_TAXAUTHORITY2,
                    shipToDataRecord.getHhShipTo_taxauthority2());
            cv.put(MspDBHelper.SHIPTO_TAXABLE2,
                    shipToDataRecord.getHhShipTo_taxable2());
            cv.put(MspDBHelper.SHIPTO_TAXSTTS2,
                    shipToDataRecord.getHhShipTo_taxstts2());
            cv.put(MspDBHelper.SHIPTO_TAXAUTHORITY3,
                    shipToDataRecord.getHhShipTo_taxauthority3());
            cv.put(MspDBHelper.SHIPTO_TAXABLE3,
                    shipToDataRecord.getHhShipTo_taxable3());
            cv.put(MspDBHelper.SHIPTO_TAXSTTS3,
                    shipToDataRecord.getHhShipTo_taxstts3());
            cv.put(MspDBHelper.SHIPTO_TAXAUTHORITY4,
                    shipToDataRecord.getHhShipTo_taxauthority4());
            cv.put(MspDBHelper.SHIPTO_TAXABLE4,
                    shipToDataRecord.getHhShipTo_taxable4());
            cv.put(MspDBHelper.SHIPTO_TAXSTTS4,
                    shipToDataRecord.getHhShipTo_taxstts4());
            cv.put(MspDBHelper.SHIPTO_TAXAUTHORITY5,
                    shipToDataRecord.getHhShipTo_taxauthority5());
            cv.put(MspDBHelper.SHIPTO_TAXABLE5,
                    shipToDataRecord.getHhShipTo_taxable5());
            cv.put(MspDBHelper.SHIPTO_TAXSTTS5,
                    shipToDataRecord.getHhShipTo_taxstts5());
            cv.put(MspDBHelper.SHIPTO_COMPANYCODE,
                    shipToDataRecord.getHhShipTo_companycode());

            db.update(MspDBHelper.SHIPTO_TABLE, cv, "customernumber = '"
                    + customernumber + "' and companycode='" + companycode
                    + "'", null);

            Log.i("ShipTo Updated", "ShipTo record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 933";
            msg = "ShipTo Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void updateTaxGroupData(HhTaxGroup01 taxGroupRecord,
                                   String taxgroup, String companycode) {
        companycode = companycode.replace("'", "''");
        taxgroup = taxgroup.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TAXGROUP_TAXGROUP,
                    taxGroupRecord.getHhTaxGroup_taxgroup());
            cv.put(MspDBHelper.TAXGROUP_TAXAUTHORITY1,
                    taxGroupRecord.getHhTaxGroup_taxauthority1());
            cv.put(MspDBHelper.TAXGROUP_TAXSTTS1,
                    taxGroupRecord.getHhTaxGroup_taxstts1());
            cv.put(MspDBHelper.TAXGROUP_TAXABLE1,
                    taxGroupRecord.getHhTaxGroup_taxable1());
            cv.put(MspDBHelper.TAXGROUP_TAXAUTHORITY2,
                    taxGroupRecord.getHhTaxGroup_taxauthority2());
            cv.put(MspDBHelper.TAXGROUP_TAXSTTS2,
                    taxGroupRecord.getHhTaxGroup_taxstts2());
            cv.put(MspDBHelper.TAXGROUP_TAXABLE2,
                    taxGroupRecord.getHhTaxGroup_taxable2());
            cv.put(MspDBHelper.TAXGROUP_TAXAUTHORITY3,
                    taxGroupRecord.getHhTaxGroup_taxauthority3());
            cv.put(MspDBHelper.TAXGROUP_TAXSTTS3,
                    taxGroupRecord.getHhTaxGroup_taxstts3());
            cv.put(MspDBHelper.TAXGROUP_TAXABLE3,
                    taxGroupRecord.getHhTaxGroup_taxable3());
            cv.put(MspDBHelper.TAXGROUP_TAXAUTHORITY4,
                    taxGroupRecord.getHhTaxGroup_taxauthority4());
            cv.put(MspDBHelper.TAXGROUP_TAXSTTS4,
                    taxGroupRecord.getHhTaxGroup_taxstts4());
            cv.put(MspDBHelper.TAXGROUP_TAXABLE4,
                    taxGroupRecord.getHhTaxGroup_taxable4());
            cv.put(MspDBHelper.TAXGROUP_TAXAUTHORITY5,
                    taxGroupRecord.getHhTaxGroup_taxauthority5());
            cv.put(MspDBHelper.TAXGROUP_TAXSTTS5,
                    taxGroupRecord.getHhTaxGroup_taxstts5());
            cv.put(MspDBHelper.TAXGROUP_TAXABLE5,
                    taxGroupRecord.getHhTaxGroup_taxable5());
            cv.put(MspDBHelper.TAXGROUP_COMPANYCODE,
                    taxGroupRecord.getHhTaxGroup_companycode());

            db.update(MspDBHelper.TAXGROUP_TABLE, cv, "taxgroup = '" + taxgroup
                    + "' and companycode='" + companycode + "'", null);

            Log.i("TaxGroup Updated", "TaxGroup record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 934";
            msg = "TaxGroup Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhTaxRate01 getTaxRateData(String authority, int buyerclass,
                                      String companycode) {

        companycode = companycode.replace("'", "''");
        authority = authority.replace("'", "''");
        HhTaxRate01 taxRateData = null;

        cursor = db.rawQuery("select * from hhTaxRate01 where authority = '"
                + authority + "' and buyerclass = '" + buyerclass
                + "' and companycode = '" + companycode + "'", null);

        try {
            while (cursor.moveToNext()) {
                taxRateData = new HhTaxRate01();

                taxRateData.setHhTaxRate_authority(cursor.getString(0));
                taxRateData.setHhTaxRate_buyerclass(cursor.getInt(1));
                taxRateData.setHhTaxRate_itemrate1(cursor.getDouble(2));
                taxRateData.setHhTaxRate_itemrate2(cursor.getDouble(3));
                taxRateData.setHhTaxRate_itemrate3(cursor.getDouble(4));
                taxRateData.setHhTaxRate_itemrate4(cursor.getDouble(5));
                taxRateData.setHhTaxRate_itemrate5(cursor.getDouble(6));
                taxRateData.setHhTaxRate_itemrate6(cursor.getDouble(7));
                taxRateData.setHhTaxRate_itemrate7(cursor.getDouble(8));
                taxRateData.setHhTaxRate_itemrate8(cursor.getDouble(9));
                taxRateData.setHhTaxRate_itemrate9(cursor.getDouble(10));
                taxRateData.setHhTaxRate_itemrate10(cursor.getDouble(11));
                taxRateData.setHhTaxRate_companycode(cursor.getString(12));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 935";
            msg = "TaxRate data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return taxRateData;
    }

    public void updateTaxRateData(HhTaxRate01 taxRateRecord, String authority,
                                  int buyerclass, String companycode) {
        companycode = companycode.replace("'", "''");
        authority = authority.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TAXRATE_AUTHORITY,
                    taxRateRecord.getHhTaxRate_authority());
            cv.put(MspDBHelper.TAXRATE_BUYERCLASS,
                    taxRateRecord.getHhTaxRate_buyerclass());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE1,
                    taxRateRecord.getHhTaxRate_itemrate1());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE2,
                    taxRateRecord.getHhTaxRate_itemrate2());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE3,
                    taxRateRecord.getHhTaxRate_itemrate3());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE4,
                    taxRateRecord.getHhTaxRate_itemrate4());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE5,
                    taxRateRecord.getHhTaxRate_itemrate5());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE6,
                    taxRateRecord.getHhTaxRate_itemrate6());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE7,
                    taxRateRecord.getHhTaxRate_itemrate7());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE8,
                    taxRateRecord.getHhTaxRate_itemrate8());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE9,
                    taxRateRecord.getHhTaxRate_itemrate9());
            cv.put(MspDBHelper.TAXRATE_ITEMRATE10,
                    taxRateRecord.getHhTaxRate_itemrate10());
            cv.put(MspDBHelper.TAXRATE_COMPANYCODE,
                    taxRateRecord.getHhTaxRate_companycode());

            db.update(MspDBHelper.TAXRATE_TABLE, cv, "authority = '"
                    + authority + "' and buyerclass = '" + buyerclass
                    + "' and companycode='" + companycode + "'", null);

            Log.i("TaxRate Updated", "TaxRate record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 936";
            msg = "TaxRate Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhTerms01 getTermsData(String codeterm, int countpayment,
                                  String companycode) {
        companycode = companycode.replace("'", "''");
        codeterm = codeterm.replace("'", "''");
        HhTerms01 termsData = null;

        cursor = db.rawQuery("select * from hhTerms01 where codeterm = '"
                + codeterm + "' and countpayment = '" + countpayment
                + "' and companycode = '" + companycode + "'", null);

        try {
            while (cursor.moveToNext()) {
                termsData = new HhTerms01();

                termsData.setHhTerms_codeterm(cursor.getString(0));
                termsData.setHhTerms_countpayment(cursor.getInt(1));
                termsData.setHhTerms_percentdue(cursor.getDouble(2));
                termsData.setHhTerms_percentdisc(cursor.getDouble(3));
                termsData.setHhTerms_numberdays(cursor.getInt(4));
                termsData.setHhTerms_duedays(cursor.getInt(5));
                termsData.setHhTerms_discwithtax(cursor.getInt(6));
                termsData.setHhTerms_companycode(cursor.getString(7));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 937";
            msg = "Terms data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return termsData;
    }

    public void updateTermsData(HhTerms01 termsRecord, String codeterm,
                                int countpayment, String companycode) {
        companycode = companycode.replace("'", "''");
        codeterm = codeterm.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.TERMS_CODETERM,
                    termsRecord.getHhTerms_codeterm());
            cv.put(MspDBHelper.TERMS_COUNTPAYMENT,
                    termsRecord.getHhTerms_countpayment());
            cv.put(MspDBHelper.TERMS_PERCENTDUE,
                    termsRecord.getHhTerms_percentdue());
            cv.put(MspDBHelper.TERMS_PERCENTDISC,
                    termsRecord.getHhTerms_percentdisc());
            cv.put(MspDBHelper.TERMS_NUMBERDAYS,
                    termsRecord.getHhTerms_numberdays());
            cv.put(MspDBHelper.TERMS_DUEDAYS, termsRecord.getHhTerms_duedays());
            cv.put(MspDBHelper.TERMS_DISCWITHTAX,
                    termsRecord.getHhTerms_discwithtax());
            cv.put(MspDBHelper.TERMS_COMPANYCODE,
                    termsRecord.getHhTerms_companycode());

            db.update(MspDBHelper.TERMS_TABLE, cv, "codeterm = '" + codeterm
                    + "' and countpayment = '" + countpayment
                    + "' and companycode='" + companycode + "'", null);

            Log.i("Terms Updated", "Terms record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 938";
            msg = "Terms Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public HhUPC01 getUPCData(String itemnumber, String upcnumber,
                              String companycode) {
        companycode = companycode.replace("'", "''");
        itemnumber = itemnumber.replace("'", "''");
        upcnumber = upcnumber.replace("'", "''");
        HhUPC01 upcData = null;

        cursor = db.rawQuery("select * from hhUPC01 where itemnumber = '"
                + itemnumber + "' and upcnumber = '" + upcnumber
                + "' and companycode = '" + companycode + "'", null);

        try {
            while (cursor.moveToNext()) {
                upcData = new HhUPC01();

                upcData.setHhUPC_itemnumber(cursor.getString(0));
                upcData.setHhUPC_upcnumber(cursor.getString(1));
                upcData.setHhUPC_itemdesc(cursor.getString(2));
                upcData.setHhUPC_companycode(cursor.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 939";
            msg = "UPC data selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return upcData;
    }

    public void updateUPCData(HhUPC01 upcRecord, String itemnumber,
                              String upcnumber, String companycode) {
        companycode = companycode.replace("'", "''");
        itemnumber = itemnumber.replace("'", "''");
        upcnumber = upcnumber.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.UPC_ITEMNUMBER, upcRecord.getHhUPC_itemnumber());
            cv.put(MspDBHelper.UPC_UPCNUMBER, upcRecord.getHhUPC_upcnumber());
            cv.put(MspDBHelper.UPC_ITEMDESC, upcRecord.getHhUPC_itemdesc());
            cv.put(MspDBHelper.UPC_COMPANYCODE,
                    upcRecord.getHhUPC_companycode());

            db.update(MspDBHelper.UPC_TABLE, cv, "itemnumber = '" + itemnumber
                    + "' and upcnumber = '" + upcnumber + "' and companycode='"
                    + companycode + "'", null);

            Log.i("UPC Updated", "UPC record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 940";
            msg = "UPC Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    public void updateManfData(Manf_Number01 manfRecord, String manitemno,
                               String itemno, String uom, String companycode) {
        companycode = companycode.replace("'", "''");
        itemno = itemno.replace("'", "''");
        manitemno = manitemno.replace("'", "''");
        uom = uom.replace("'", "''");
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.MANUF_MANITEMNO, manfRecord.getManuf_manitemno());
            cv.put(MspDBHelper.MANUF_ITEMNO, manfRecord.getManuf_itemno());
            cv.put(MspDBHelper.MANUF_UOM, manfRecord.getManuf_uom());
            cv.put(MspDBHelper.MANUF_COMPNAYCODE, manfRecord.getCompany_code());

            db.update(MspDBHelper.MANUF_TABLE, cv, "manitemno = '" + manitemno
                    + "' and itemno = '" + itemno + "' and uom='" + uom
                    + "' and companycode='" + companycode + "'", null);

            Log.i("ManfNumber Updated",
                    "ManfNumber record updated successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when updating mspdb record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 941";
            msg = "ManfNumber Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    public Manf_Number01 getManufData(String manitemno, String itemno,
                                      String uom, String companycode) {
        companycode = companycode.replace("'", "''");
        itemno = itemno.replace("'", "''");
        manitemno = manitemno.replace("'", "''");
        uom = uom.replace("'", "''");

        Manf_Number01 manufDet = null;

        cursor = db.rawQuery("select * from manf_number01 where manitemno = '"
                + manitemno + "' and itemno = '" + itemno + "' and uom = '"
                + uom + "' and companycode = '" + companycode + "'", null);

        try {
            while (cursor.moveToNext()) {
                manufDet = new Manf_Number01();
                manufDet.setManuf_manitemno(cursor.getString(0));
                manufDet.setManuf_itemno(cursor.getString(1));
                manufDet.setManuf_uom(cursor.getString(2));
                manufDet.setCompany_code(cursor.getString(3));
            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 942";
            msg = "ManfNumber selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();

        return manufDet;
    }

    public void addMspDbData(Mspdb mspdbRecord) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.MSPDB_COMPANYNUMBER,
                    mspdbRecord.getMspdb_companyNumber());
            cv.put(MspDBHelper.MSPDB_SALESPERSON,
                    mspdbRecord.getMspdb_salesPerson());
            cv.put(MspDBHelper.MSPDB_MAPNO, mspdbRecord.getMspdb_mapNo());
            cv.put(MspDBHelper.MSPDB_CUSTOMERNUMBER,
                    mspdbRecord.getMspdb_customerNumber());
            cv.put(MspDBHelper.MSPDB_ORDERNUMBER,
                    mspdbRecord.getMspdb_orderNumber());
            cv.put(MspDBHelper.MSPDB_QUOTENUMBER,
                    mspdbRecord.getMspdb_quoteNumber());
            cv.put(MspDBHelper.MSPDB_LASTINVNO,
                    mspdbRecord.getMspdb_lastinvno());
            cv.put(MspDBHelper.MSPDB_RECEIPTNUMBER,
                    mspdbRecord.getMspdb_receiptnumber());

            db.insert(MspDBHelper.MSPDB_TABLE, null, cv);

            Log.i("MspDB Data Insert", "MSPDB record inserted successfully...");
        } catch (Exception e) {
            db.endTransaction();
            Log.e("Transaction Failure",
                    "Transaction failure when inserting setting.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 943";
            msg = "MSPDB Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }
    }

    /*
	 * The below method used for getting shipvia name based on customer
	 * developed by TISN on 11-Nov-2013
	 */
    public String getShipViaCode(String custNumber, String cmpnyNo) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        custNumber = custNumber.replace("'", "''");

        String shipViaCode = "";

        cursor = db.rawQuery(
                "select shipvia from hhCustomer01 where number = '"
                        + custNumber + "' and companycode = '" + cmpnyNo + "'",
                null);

        try {
            while (cursor.moveToNext()) {

                shipViaCode = cursor.getString(0);

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 604-ShipVia";
            msg = "Customer Name Selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return shipViaCode;
    }

    /*
	 * The below method used for getting stockinguom based on itemnumber
	 * developed by TISN on 11-Nov-2013
	 */
    public String getItemUom(String itemNum, String cmpnyNo) {

        cmpnyNo = cmpnyNo.replace("'", "''");
        itemNum = itemNum.replace("'", "''");

        String itemUOM = "";

        cursor = db.rawQuery(
                "select stockinguom from hhItem01 where number = '" + itemNum
                        + "' and companycode = '" + cmpnyNo + "'", null);

        try {
            while (cursor.moveToNext()) {

                itemUOM = cursor.getString(0);

            }

        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error Getting Item Default UOM";
            msg = "Getting Item UOM Selection failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }

        cursor.close();
        return itemUOM;
    }

    public List<Manf_Number01> showUPC(String cs) {
        cs = cs.replace("'", "''");
        ArrayList<Manf_Number01> list = new ArrayList<Manf_Number01>();
        Manf_Number01 manf = null;
        cursor = db.rawQuery("select * from manf_number01 where manitemno = '"
                + cs + "'", null);

        try {
            while (cursor.moveToNext()) {
                manf = new Manf_Number01();
                manf.setManuf_itemno(cursor.getString(1).toString());
                list.add(manf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    public TempItem getTempData() {

        TempItem temp = null;
        cursor = db.rawQuery("select * from tempitem", null);

        while (cursor.moveToNext()) {
            temp = new TempItem();
            temp.setTemp_itemNum(cursor.getString(0));
            temp.setTemp_qty(cursor.getString(1));
            temp.setTemp_extPrice(cursor.getString(2));
            temp.setTemp_location(cursor.getString(3));
            temp.setTemp_uom(cursor.getString(4));
            temp.setTemp_date(cursor.getString(5));
            temp.setTemp_entryType(cursor.getString(6));
            temp.setTemp_discount(cursor.getString(7));
            temp.setTemp_discType(cursor.getString(8));

        }
        return temp;

    }

    public String getItemCategory(String itemNo, String locId) {
        locId = locId.replace("'", "''");
        itemNo = itemNo.replace("'", "''");
        String itemCategory = "";
        cursor = db.rawQuery("select * from hhItem01 where number='" + itemNo
                + "' and locid='" + locId + "'", null);
        while (cursor.moveToNext()) {
            itemCategory = cursor.getString(2);
        }

        return itemCategory;
    }

    public String getMaxReceiptNo() {
        String receiptNumber = "";
        cursor = db.rawQuery("select max(refno) from hhReceipt01", null);
        while (cursor.moveToNext()) {
            receiptNumber = cursor.getString(0);
        }
        return receiptNumber;
    }

    public boolean isReceiptEmpty() {
        boolean result = false;

        cursor = db.rawQuery("select count(*) from hhReceipt01 ", null);
        cursor.moveToFirst();

        int count = cursor.getInt(0);

        if (count == 0)
            result = true;
        else
            result = false;

        return result;
    }

    public HhEmailSetting getEmail() {
        getReadableDatabase();
        HhEmailSetting emailSetting = null;

        cursor = db.rawQuery("select * from hhEmailSetting", null);

        while (cursor.moveToNext()) {
            emailSetting = new HhEmailSetting();
            emailSetting
                    .setHhEmailSetting_salespersonemail(cursor.getString(0));
            emailSetting.setHhEmailSetting_salespersonpwd(cursor.getString(1));
            emailSetting.setHhEmailSetting_companyemail(cursor.getString(2));
            emailSetting.setHhEmailSetting_salespersonhostname(cursor
                    .getString(3));
            emailSetting.setHhEmailSetting_salespersonportno(cursor.getInt(4));
            emailSetting.setHhEmailSetting_companyportno(cursor.getString(5));

        }

        return emailSetting;
    }

    public void addEmailSetting(HhEmailSetting email) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.EMAILSETTING_SALESPERSON_EMAIL,
                    email.getHhEmailSetting_salespersonemail());
            cv.put(MspDBHelper.EMAILSETTING_SALESPERSON_PWD,
                    email.getHhEmailSetting_salespersonpwd());
            cv.put(MspDBHelper.EMAILSETTING_COMPANY_EMAIL,
                    email.getHhEmailSetting_companyemail());
            cv.put(MspDBHelper.EMAILSETTING_SALESPERSON_HOSTNAME,
                    email.getHhEmailSetting_salespersonhostname());
            cv.put(MspDBHelper.EMAILSETTING_SALESPERSON_PORTNO,
                    email.getHhEmailSetting_salespersonportno());
            cv.put(MspDBHelper.EMAILSETTING_COMPANY_PORTNO,
                    email.getHhEmailSetting_companyportno());
            db.insert(MspDBHelper.EMAIL_SETTING_TABLE, null, cv);
            Log.i("EmailSetting Insert",
                    "Email Setting record inserted successfully...");

        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when inserting email setting table record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 532";
            msg = "Email Setting Record Insertion failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    // update Email setting
    public void updateEmailSetting(HhEmailSetting email) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(MspDBHelper.EMAILSETTING_SALESPERSON_EMAIL,
                    email.getHhEmailSetting_salespersonemail());
            cv.put(MspDBHelper.EMAILSETTING_SALESPERSON_PWD,
                    email.getHhEmailSetting_salespersonpwd());
            cv.put(MspDBHelper.EMAILSETTING_COMPANY_EMAIL,
                    email.getHhEmailSetting_companyemail());
            cv.put(MspDBHelper.EMAILSETTING_SALESPERSON_HOSTNAME,
                    email.getHhEmailSetting_salespersonhostname());
            cv.put(MspDBHelper.EMAILSETTING_SALESPERSON_PORTNO,
                    email.getHhEmailSetting_salespersonportno());
            cv.put(MspDBHelper.EMAILSETTING_COMPANY_PORTNO,
                    email.getHhEmailSetting_companyportno());
            db.update(MspDBHelper.EMAIL_SETTING_TABLE, cv, null, null);
            Log.i("EmailSetting Updated",
                    "Email Setting record updated successfully...");

        } catch (Exception e) {
            // db.endTransaction();
            // Log.e("Transaction Failure",
            // "Transaction failure when inserting email setting table record.");
            this.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");

            errCode = "Error 560";
            msg = "Email Setting Record Updation failed.";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
        }

    }

    public List<HhHistory01> getHistoryData(String companyno) {

        List<HhHistory01> itemlist = new ArrayList<>();
        HhHistory01 his = null;


            cursor = db.rawQuery("select * from " + HISTORY_TABLE + " where companycode = '"
                    + companyno + "' ", null);
            try {
                while (cursor.moveToNext()) {

                    his = new HhHistory01();





                    his.setHhTran_transType_new(cursor.getString(0));
                    his.setHhTran_referenceNumber_new(cursor.getString(1));
                    his.setHhTran_invoiceNumber_new(cursor.getString(2));
                    his.setHhTran_orderNumber_new(cursor.getString(3));
                    his.setHhTran_transDay_new(cursor.getInt(4));
                    his.setHhTran_transMonth_new(cursor.getInt(5));
                    his.setHhTran_transYear_new(cursor.getInt(6));
                    his.setHhTran_expShipDay_new(cursor.getInt(7));
                    his.setHhTran_expShipMonth_new(cursor.getInt(8));
                    his.setHhTran_expShipYear_new(cursor.getInt(9));
                    his.setHhTran_customerNumber_new(cursor.getString(10));
                    his.setHhTran_salesPerson_new(cursor.getString(11));
                    his.setHhTran_itemNumber_new(cursor.getString(12));
                    his.setHhTran_locId_new(cursor.getString(13));
                    his.setHhTran_terms_new(cursor.getString(14));
                    his.setHhTran_currency_new(cursor.getString(15));
                    his.setHhTran_priceListCode_new(cursor.getString(16));
                    his.setHhTran_uom_new(cursor.getString(17));
                    his.setHhTran_qty_new(cursor.getInt(19)); // put float
                    his.setHhTran_price_new(cursor.getDouble(20));
                    his.setHhTran_discPrice_new(cursor.getInt(21));
                    his.setHhTran_netPrice_new(cursor.getDouble(22));
                    his.setHhTran_extPrice_new(cursor.getDouble(23));
                    his.setHhTran_tax_new(cursor.getDouble(24));
                    his.setHhTran_shipToCode_new(cursor.getString(24));
                    his.setHhTran_shipViaCode_new(cursor.getString(25));
                    his.setHhTran_status_new(cursor.getInt(26));
                    his.setHhTran_lineItem_new(cursor.getInt(27));
                    his.setHhTran_discValue_new(cursor.getInt(28));
                    his.setHhTran_discType_new(cursor.getString(29));
                    his.setHhTran_ordShipDay_new(cursor.getInt(30));
                    his.setHhTran_ordShipMonth_new(cursor.getInt(31));
                    his.setHhTran_ordShipYear_new(cursor.getInt(32));
                    his.setHhTran_editable_new(cursor.getInt(33));
                    his.setHhTran_manItemNo_new(cursor.getString(34));
                    his.setHhTran_preTax_new(cursor.getInt(35));
                    his.setHhTran_refNo_new(cursor.getString(36));
                    his.setHhTran_orderdiscount_new(cursor.getInt(37));
                    his.setHhTran_taxamount_new(cursor.getInt(38));
                    his.setHhTran_totalcarton_new(cursor.getInt(39));
                    his.setHhTran_editedcustomername_new(cursor.getString(40));
                    his.setHhTran_itemunitweight_new(cursor.getInt(41));
                    his.setHhTran_companycode_new(cursor.getString(42));
                    his.setHhTran_lat_new(cursor.getInt(43));
                    his.setHhTran_lon_new(cursor.getInt(44));


                    itemlist.add(his);


                }
            } catch (Exception e) {
                e.printStackTrace();
                errCode = "Error 606";
                msg = "Item Data Selection failed(Based on Location Id ).";
                errMsg = errCode + " : " + msg;
                LogfileCreator.appendLog(errMsg);
                db.close();
            }


        cursor.close();
        return itemlist;
    }
    public List<HhHistory01> getHistorySingleData(String refno) {

        List<HhHistory01> itemlist = new ArrayList<>();
        HhHistory01 his = null;


        cursor = db.rawQuery("select * from " + HISTORY_TABLE + " where "+HISTORY_TRANS_REFERENCENUMBER+" = '"
                + refno  + "' ", null);
        try {
            while (cursor.moveToNext()) {

                his = new HhHistory01();





                his.setHhTran_transType_new(cursor.getString(0));
                his.setHhTran_referenceNumber_new(cursor.getString(1));
                his.setHhTran_invoiceNumber_new(cursor.getString(2));
                his.setHhTran_orderNumber_new(cursor.getString(3));
                his.setHhTran_transDay_new(cursor.getInt(4));
                his.setHhTran_transMonth_new(cursor.getInt(5));
                his.setHhTran_transYear_new(cursor.getInt(6));
                his.setHhTran_expShipDay_new(cursor.getInt(7));
                his.setHhTran_expShipMonth_new(cursor.getInt(8));
                his.setHhTran_expShipYear_new(cursor.getInt(9));
                his.setHhTran_customerNumber_new(cursor.getString(10));
                his.setHhTran_salesPerson_new(cursor.getString(11));
                his.setHhTran_itemNumber_new(cursor.getString(12));
                his.setHhTran_locId_new(cursor.getString(13));
                his.setHhTran_terms_new(cursor.getString(14));
                his.setHhTran_currency_new(cursor.getString(15));
                his.setHhTran_priceListCode_new(cursor.getString(16));
                his.setHhTran_uom_new(cursor.getString(17));
                his.setHhTran_qty_new(cursor.getInt(19)); // put float
                his.setHhTran_price_new(cursor.getDouble(20));
                his.setHhTran_discPrice_new(cursor.getInt(21));
                his.setHhTran_netPrice_new(cursor.getDouble(22));
                his.setHhTran_extPrice_new(cursor.getDouble(23));
                his.setHhTran_tax_new(cursor.getDouble(24));
                his.setHhTran_shipToCode_new(cursor.getString(24));
                his.setHhTran_shipViaCode_new(cursor.getString(25));
                his.setHhTran_status_new(cursor.getInt(26));
                his.setHhTran_lineItem_new(cursor.getInt(27));
                his.setHhTran_discValue_new(cursor.getInt(28));
                his.setHhTran_discType_new(cursor.getString(29));
                his.setHhTran_ordShipDay_new(cursor.getInt(30));
                his.setHhTran_ordShipMonth_new(cursor.getInt(31));
                his.setHhTran_ordShipYear_new(cursor.getInt(32));
                his.setHhTran_editable_new(cursor.getInt(33));
                his.setHhTran_manItemNo_new(cursor.getString(34));
                his.setHhTran_preTax_new(cursor.getInt(35));
                his.setHhTran_refNo_new(cursor.getString(36));
                his.setHhTran_orderdiscount_new(cursor.getInt(37));
                his.setHhTran_taxamount_new(cursor.getInt(38));
                his.setHhTran_totalcarton_new(cursor.getInt(39));
                his.setHhTran_editedcustomername_new(cursor.getString(40));
                his.setHhTran_itemunitweight_new(cursor.getInt(41));
                his.setHhTran_companycode_new(cursor.getString(42));
                his.setHhTran_lat_new(cursor.getInt(43));
                his.setHhTran_lon_new(cursor.getInt(44));


                itemlist.add(his);


            }
        } catch (Exception e) {
            e.printStackTrace();
            errCode = "Error 606";
            msg = "Item Data Selection failed(Based on Location Id ).";
            errMsg = errCode + " : " + msg;
            LogfileCreator.appendLog(errMsg);
            db.close();
        }


        cursor.close();
        return itemlist;
    }

//    public List<HhHistory01> getrefno(String s){
//
//        List<HhHistory01> itemlist1 = new ArrayList<>();
//        HhHistory01 history01 =null;
//        try {
//
//            cursor = db.rawQuery("SELECT " + HISTORY_TRANS_REFNO + " FROM " + HISTORY_TABLE + "", null);
//
//            while (cursor.moveToNext()) {
//
//                history01 = new HhHistory01();
//
//                history01.setHhTran_refNo_new(cursor.getString(36));
//
//                itemlist1.add(history01);
//            }
//        }catch(Exception e) {
//            e.printStackTrace();
//            errCode = "Error 606";
//            msg = "Item Data Selection failed(Based on Location Id ).";
//            errMsg = errCode + " : " + msg;
//            LogfileCreator.appendLog(errMsg);
//            db.close();
//        }
//        cursor.close();
//        return itemlist1;
//    }
//

}
