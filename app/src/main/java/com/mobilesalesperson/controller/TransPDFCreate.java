package com.mobilesalesperson.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.printer.ZebraPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class TransPDFCreate extends AppBaseActivity {

    private MspDBHelper dbhelpher;
    private Supporter supporter;
    private HhSetting setting;

    private List<HhTran01> transAllList;
    private TempItem temp;
    private String referNum;
    private String devAddress;
    private String devName;
    private ToastMessage toastMsg;

    // currency format variables
    private String tranTot;
    private String taxValue;
    private String tranDiscount;
    private String preTaxValue;
    private String netTotValue;
    private String prePayValue;
    private double netTotal;
    private double prepayAmt;
    private String strTranQty;

    // printing format variables
    private ZebraPrinterConnection zebraPrinterConnection;
    private ZebraPrinter printer;
    private String transType;
    private int day;
    private int month;
    private int year;
    private String strDate;

    private int y;
    private String returnType;
    private int noOfPrint;
    private String printerModel;
    private String prntCompName;
    private String prntCompAddrs;
    private String prntCompState;
    private String prntCompCountry;
    private String prntCompPhoneNo;
    private String prntTitle;
    private String prntDate;
    private String prntCusNo;
    private String prntCusName;
    private String prntCusAddrs;
    private String prntShipLoc;
    private String prntOrdNumTitle;
    private String prntSalesPerson;
    private String prntQty;
    private String prntItmNo;
    private String prntDesc;
    private String prntUnitPrz;
    private String prntUOM;
    private String prntExtPrz;
    private String prntSubTot;
    private String prntVAT;
    private String prntTotAmt;
    private String prntLessPrepay;
    private String prntDisc;
    private String prntAmtDue;
    private int tempSize;
    private int tempPixSpace;
    private DecimalFormat formats;

    private int xQtyno;
    private int xItemNo;
    private int xDescNo;
    private int xUomNo;

    private File pdfFile = null;
    private Document document;
    private BaseFont bfBold;
    private PdfContentByte cb;
    private PdfPTable table;
    private PdfWriter docWriter;
    private PdfPCell cell;
    private String mPath;
    public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
    public static Font FONT_TITLE_SMALL_LETTERS = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    public static Font FONT_TITLE_SALES_OREDER = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    public static Font FONT_TABLE_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.NORMAL);
    public static Font FONT_NOTE = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
    String filename = "msp.pdf";
    String toasttext = "";

     private SharedPreferences shippingDetails;
    private SharedPreferences entryDetails;


    public void onCreate(Bundle savedInstanceState) {
        toastMsg = new ToastMessage();
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.create_pdf);
        registerBaseActivityReceiver();

   entryDetails = getSharedPreferences("entryDetails", Context.MODE_PRIVATE);
        shippingDetails = getSharedPreferences("shippingDetails", Context.MODE_PRIVATE);
        dbhelpher = new MspDBHelper(this);
        supporter = new Supporter(this, dbhelpher);
        toastMsg = new ToastMessage();
        formats = new DecimalFormat("0.00");

      //  custNo = shippingDetails.getString("customerNumber", "");
        referNum = getIntent().getStringExtra("refno");

        supporter.clearPreference(shippingDetails, entryDetails);
        PDFCreate();


					/*new PrinterConnectOperation().execute();*/

    }

    public void PDFCreate() {
         dbhelpher.openReadableDatabase();
        transAllList = dbhelpher.getTransactionList(referNum,
                supporter.getCompanyNo());
        setting = dbhelpher.getSettingData();
        dbhelpher.closeDatabase();

        // calculation of listview items price & tax details...
        calculateTransDetails(transAllList);


        HhTran01 tran = transAllList.get(0);
        transType = tran.getHhTran_transType();
        if (transType.equals("S")) {
            prntTitle = "SALES ORDER";
            prntOrdNumTitle = "Sales Order Number : ";
        } else if (transType.equals("I") || transType.equals("CN")) {
            prntTitle = "VAT INVOICE";
            prntOrdNumTitle = "VAT Invoice Number : ";
        } else {
            prntTitle = "QUOTATION";
            prntOrdNumTitle = "Quotation Number : ";
        }

        // getting date from trans table
        day = tran.getHhTran_transDay();
        month = tran.getHhTran_transMonth();
        year = tran.getHhTran_transYear();
        strDate = supporter.getStringDate(year, month, day);

        String cmpNo = supporter.getCompanyNo();
        dbhelpher.openReadableDatabase();
        HhCompany company = dbhelpher.getCompanyData(cmpNo);
        dbhelpher.closeDatabase();

        noOfPrint = setting.getHhSetting_numcopiesprint();
        printerModel = setting.getHhSetting_printerModel();
        prntCompName = company.getCompany_name();
        prntCompAddrs = company.getCompany_address() + ","
                + company.getCompany_city();
        prntCompState = company.getCompany_state() + " - "
                + company.getCompany_zip();
        prntCompCountry = company.getCompany_country();
        prntCompPhoneNo = company.getCompany_phone();

        // getting date for printing ...
        prntDate = "Date : " + strDate;

        // getting customer number...
        prntCusNo = tran.getHhTran_customerNumber();

        // getting customer name...
        prntCusName = tran.getHhTran_editedcustomername();

        // getting customer address...
        dbhelpher.openReadableDatabase();
        HhCustomer01 customer = dbhelpher.getCustomerData(prntCusNo,
                supporter.getCompanyNo());
        dbhelpher.closeDatabase();
        prntCusAddrs = customer.getHhCustomer_address();

        // getting ship to location name...
        String tempShipto = tran.getHhTran_shipToCode();
        if (tempShipto.equals("")) {
            prntShipLoc = "DEFAULT";
        } else {
            prntShipLoc = tempShipto;
        }

        // getting salesperson name...
        prntSalesPerson = tran.getHhTran_salesPerson();

        // getting subtotal value...
        prntSubTot = formats.format(Double.parseDouble(tranTot));
        System.out.println("Sub Total : " + prntSubTot);

        // getting tax value...
        prntVAT = formats.format(Double.parseDouble(taxValue));
        System.out.println("VAT : " + prntVAT);

        // setting total amount value...
        double tempSubTot = Double.parseDouble(prntSubTot);
        double tempVat = Double.parseDouble(prntVAT);
        double totAmt = tempSubTot + tempVat;
        prntTotAmt = formats.format(totAmt);
        System.out.println("Total Amt : " + prntTotAmt);

        // getting less prepayment value...
        prntLessPrepay = formats.format(Double.parseDouble(prePayValue));
        System.out.println("Prepayment : " + prntLessPrepay);

        // getting discount value...
        prntDisc = formats.format(Double.parseDouble(tranDiscount));
        System.out.println("Discount : " + prntDisc);

        // setting amount due value...
        double tempLessPrepay = Double.parseDouble(prntLessPrepay);
        double tempDisc = Double.parseDouble(prntDisc);
        double amtDue = totAmt - (tempLessPrepay + tempDisc);
        prntAmtDue = formats.format(amtDue);

        System.out.println("AmtDue : " + prntAmtDue);
        new PrinterConnectOperation().execute();
    }


    // to calculate resultant transaction values
    private void calculateTransDetails(List<HhTran01> transItemList2) {

        double transTotal = 0.00;
        int transQty = 0;
        double tax = 0.00;
        double preTax = 0.00;
        double netTot = 0.00;
        double discount = 0.00;

        for (int i = 0; i < transItemList2.size(); i++) {
            HhTran01 item = transItemList2.get(i);

            String type = item.getHhTran_transType();
            double cDiscount = item.getHhTran_discValue();
            if (type.equals("CN")) {

                int cQty = item.getHhTran_qty();
                transQty = transQty - cQty;

                double cTot = item.getHhTran_price() * cQty;
                transTotal = transTotal - cTot;

                discount = discount - cDiscount;

                double cTax = item.getHhTran_tax();
                tax = tax - cTax;

            } else {
                int cQty = item.getHhTran_qty();
                transQty = transQty + cQty;

                double cTot = item.getHhTran_price() * cQty;
                transTotal = transTotal + cTot;

                discount = discount + cDiscount;

                double cTax = item.getHhTran_tax();
                tax = tax + cTax;
            }

        }

        tranTot = supporter.getCurrencyFormat(transTotal);
        tranDiscount = supporter.getCurrencyFormat(discount);
        taxValue = supporter.getCurrencyFormat(tax);
        preTaxValue = supporter.getCurrencyFormat(preTax);

        netTot = transTotal + tax + preTax - discount;
        netTotal = netTot;
        netTotValue = supporter.getCurrencyFormat(netTotal);
        strTranQty = transQty + "";

        dbhelpher.openReadableDatabase();
        prepayAmt = dbhelpher.getPrepayAmount(referNum,
                supporter.getCompanyNo());
        dbhelpher.closeDatabase();
        prePayValue = supporter.getCurrencyFormat(prepayAmt);
    }

    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(TransPDFCreate.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "failed";
            try {
                boolean isDataSentSuccess;
                isDataSentSuccess = sendDataToPrinter();
                //isDataSentSuccess = sendDataToPrinter();
                if (isDataSentSuccess) {
                    result = "success";
                }
                return result;

            } catch (Exception e) {

                Log.e("tag", "error", e);
                LogfileCreator.appendLog("In doInBackground method: "
                        + e.getMessage());
                result = "error";
                toasttext = "File Createion Failed";
            }

            return result;


        }


        @Override
        protected void onPostExecute(final String result) {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            if (result.equals("success")) {
                toasttext = "Print PDF creation Success ";
                boolean isDataSentSuccess = true;
                toastMsg.showToast(TransPDFCreate.this,
                        toasttext);
                try {
                    supporter.simpleNavigateTo(MainMenu.class);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(pdfFile), "application/pdf"));
                             } catch (Exception e) {
                                toastMsg.showToast(TransPDFCreate.this,
                                        "No Application match to Open Print File");
                            }
                        }
                    }, 1000);
                    // startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(pdfFile), "application/pdf"));

                } catch (Exception e) {
                    toastMsg.showToast(TransPDFCreate.this, "Not Software Match to Open Print File");
                    supporter.simpleNavigateTo(MainMenu.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                toastMsg.showToast(TransPDFCreate.this,
                        toasttext);
                supporter.simpleNavigateTo(MainMenu.class);

            }


        }// end of PostExecute method...

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Creating PDF...");
            this.dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            this.dialog.setMessage(values[0]);
        }
    }

    public boolean sendDataToPrinter() {
        boolean resultSent = false;
        try {

            createfile();
            document = new Document();
            //  document.setMargins(13, 3, 17, 20);
            // document.setMargins(0, 0, 0, 0);

            // document = new Document(new Rectangle(595 , 842 ), 0, 0, 0, 0);
            docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            cb = docWriter.getDirectContent();
            //initialize fonts for text printing
            // initializeFonts();
            writeHeader();
            writeDetail();

            Paragraph reportBody = new Paragraph();
            reportBody.setFont(FONT_BODY); //

            writeTableTitleDetail();

            writeItemDetail(reportBody);
            document.add(reportBody);


            writetotal();
            writesignature();

            document.close();

            resultSent = true;

        } catch (Exception e) {
            resultSent = false;
        } finally {
            return resultSent;
        }
    }




    private void writeHeader() {
        try {
            Paragraph paragraph = new Paragraph();
            Paragraph childParagraph = new Paragraph(prntCompName, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.add(childParagraph);

            if (!prntCompAddrs.equals("")) {
                childParagraph = new Paragraph(prntCompAddrs, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            if (!prntCompState.equals("")) {
                childParagraph = new Paragraph(prntCompState, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            if (!prntCompPhoneNo.equals("")) {
                childParagraph = new Paragraph(prntCompPhoneNo, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            addEmptyLine(paragraph, 1);
            childParagraph = new Paragraph(prntTitle, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.add(childParagraph);

            addEmptyLine(paragraph, 1);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeDetail() {
        try {
            Paragraph paragraph = new Paragraph();
            Paragraph childParagraph = new Paragraph(prntDate, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph(prntOrdNumTitle + referNum, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Customer Number : " + prntCusNo, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Customer Name : " + prntCusName, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Customer Address : " + prntCusAddrs, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Ship To Location : " + prntShipLoc, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Reference Number : " + referNum, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Salesperson : " + prntSalesPerson, FONT_TITLE_SALES_OREDER); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

            addEmptyLine(paragraph, 1);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeTableTitleDetail() {
        try {
            Paragraph paragraph = new Paragraph();
            //list all the products sold to the customer
            float[] columnWidths = {2f, 5f, 7f, 3f, 3f, 3f};
            //create PDF table with the given widths
            table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(100);


            cell = new PdfPCell(new Phrase("Qty.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //  cell.setBorder((Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT));
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Item No.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Description", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Unit Price", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("UOM", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Ext Price", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeItemDetail(Paragraph reportBody) {


        for (int i = 0; i < transAllList.size(); i++) {

            HhTran01 tran = transAllList.get(i);

            returnType = tran.getHhTran_transType();
            double tempQty = tran.getHhTran_qty();
            if (returnType.equals("CN")) {
                prntQty = "-" + tempQty;
            } else {
                prntQty = "" + tempQty;
            }

            prntItmNo = tran.getHhTran_itemNumber();

            // getting item description
            dbhelpher.openReadableDatabase();
                /*temp=dbhelpher.getTempData();
                String location=temp.getTemp_location();*/
            HhItem01 item = dbhelpher.getItemDataByNum(prntItmNo,
                    supporter.getCompanyNo());
            dbhelpher.closeDatabase();
            prntDesc = item.getHhItem_description();

            // getting item unitprize
            double tempUnitPrz = tran.getHhTran_price();
            prntUnitPrz = formats.format(tempUnitPrz);

            // getting item UOM
            prntUOM = tran.getHhTran_uom();


            // getting item Ext. Price...
            double tempExtPrz = tempUnitPrz * (double) tempQty;
            // double tempExtPrz = tran.getHhTran_extPrice();
            if (returnType.equals("CN")) {
                prntExtPrz = "-" + formats.format(tempExtPrz);
            } else {
                prntExtPrz = formats.format(tempExtPrz);
            }

            double d = Double.parseDouble(prntQty);
            int intprintQty = (int) d;
            cell = new PdfPCell(new Phrase(intprintQty + "", FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(prntItmNo, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(prntDesc, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(prntUnitPrz, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(prntUOM, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase(prntExtPrz, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        }
        reportBody.add(table);
    }



    private void writetotal() {
        try {
            Paragraph paragraph = new Paragraph();
            float[] columnWidthForThreeValues = {5.0f, 3.0f, 3.0f};
            int coloumn1alignment = Element.ALIGN_LEFT;
            int coloumn2alignment = Element.ALIGN_RIGHT;
            int coloumn3alignment = Element.ALIGN_RIGHT;
            addEmptyLine(paragraph, 2);
            Tablewiththreecoloum(paragraph, columnWidthForThreeValues, 100, "", "Sub Total", prntSubTot, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, coloumn1alignment, coloumn2alignment, coloumn3alignment, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER);
            Tablewiththreecoloum(paragraph, columnWidthForThreeValues, 100, "", "VAT", prntVAT, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, coloumn1alignment, coloumn2alignment, coloumn3alignment, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER);
            Tablewiththreecoloum(paragraph, columnWidthForThreeValues, 100, "", "Total Amount", prntTotAmt, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, coloumn1alignment, coloumn2alignment, coloumn3alignment, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER);
            Tablewiththreecoloum(paragraph, columnWidthForThreeValues, 100, "", "Prepayment", prntLessPrepay, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, coloumn1alignment, coloumn2alignment, coloumn3alignment, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER);
            Tablewiththreecoloum(paragraph, columnWidthForThreeValues, 100, "", "Discount", prntDisc, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, coloumn1alignment, coloumn2alignment, coloumn3alignment, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER);
            Tablewiththreecoloum(paragraph, columnWidthForThreeValues, 100, "", "Amount Due", prntAmtDue, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, coloumn1alignment, coloumn2alignment, coloumn3alignment, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER);

            addEmptyLine(paragraph, 1);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writesignature() {
        try {
            Paragraph paragraph = new Paragraph();
            float[] columnWidthForTwoValues = {5.0f, 5.0f};
            int coloumn1alignment = Element.ALIGN_LEFT;
            int coloumn2alignment = Element.ALIGN_RIGHT;

            addEmptyLine(paragraph, 2);
            Tablewithtwocoloum(paragraph, columnWidthForTwoValues, 100, "Delivered by/Signature", "Received by/Signature", FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, coloumn1alignment, coloumn2alignment, Rectangle.NO_BORDER, Rectangle.NO_BORDER);

            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createfile() {
        try {

            // filename = referNum+".pdf";

            File root_path = new File(Environment.getExternalStorageDirectory() + "/Android/AMSP/PrintReport/");

            if (!root_path.exists()) {
                root_path.mkdirs();
            }

            mPath = (Environment.getExternalStorageDirectory() + "/Android/AMSP/PrintReport/" + filename);

            pdfFile = new File(mPath);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void Tablewiththreecoloum(Paragraph paragraph, float[] columnWidths, int width, String column1value, String column2value, String column3value, Font coloumn1fontsize1, Font coloumn1fontsize2, Font coloumn1fontsize3, int coloumn1alignment, int coloumn2alignment, int coloumn3alignment, int border1, int border2, int border3) {

        PdfPTable tbl = new PdfPTable(columnWidths);
        tbl.setWidthPercentage(width);
        PdfPCell cell = new PdfPCell(new Phrase(column1value, coloumn1fontsize1));
        cell.setHorizontalAlignment(coloumn1alignment);
        cell.setBorder(border1);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase(column2value, coloumn1fontsize2));
        cell.setHorizontalAlignment(coloumn2alignment);
        cell.setBorder(border2);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase(column3value, coloumn1fontsize3));
        cell.setHorizontalAlignment(coloumn3alignment);
        cell.setBorder(border3);
        tbl.addCell(cell);
        paragraph.add(tbl);

    }

    private void Tablewithtwocoloum(Paragraph paragraph, float[] columnWidths, int width, String column1, String column2, Font coloumn1fontsize1, Font coloumn1fontsize2, int coloumn1alignment, int coloumn2alignment, int border1, int border2) {

        PdfPTable tbl = new PdfPTable(columnWidths);
        tbl.setWidthPercentage(width);
        PdfPCell cell = new PdfPCell(new Phrase(column1, coloumn1fontsize1));
        cell.setHorizontalAlignment(coloumn1alignment);
        cell.setBorder(border1);
        tbl.addCell(cell);
        cell = new PdfPCell(new Phrase(column2, coloumn1fontsize2));
        cell.setHorizontalAlignment(coloumn2alignment);
        cell.setBorder(border2);
        tbl.addCell(cell);
        paragraph.add(tbl);

    }

    private void initializeFonts() {


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
