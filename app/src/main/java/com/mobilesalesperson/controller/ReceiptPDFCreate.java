package com.mobilesalesperson.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.itextpdf.text.Document;
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
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;
import com.zebra.android.printer.PrinterLanguage;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;

public class ReceiptPDFCreate extends AppBaseActivity {

    private MspDBHelper dbhelpher;
    private Supporter supporter;


    private List<HhReceipt01> receiptAllList;
    private String reciptNum;
    private String devName;
    private String devAddress;
    private ToastMessage toastMsg;

    private int day;
    private int month;
    private int year;
    private String date;

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
    public static Font FONT_TABLE_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.NORMAL);
    public static Font FONT_NOTE = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
    String filename = "msp_receipt.pdf";
    String toasttext = "";

    private String prntCompName;
    private String prntCompAddrs;
    private String prntCompState;
    private String prntCompCountry;
    private String prntCompPhoneNo;
    private String prntTitle;
    private String prntDate;
    private String prntCusNo;
    private String prntCusName;
    private String prntReciptNo;
    private int sNo;
    private String prntInvNo;
    private String prntApplyAmt;
    private String prntPayMode;
    private String prntUnapplied;
    private double applyAmt;
    private double reciptTotAmt;
    private DecimalFormat formats;

    private HhSetting setting;
    private PrinterLanguage printerLanguage;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.create_pdf);
        registerBaseActivityReceiver();
        dbhelpher = new MspDBHelper(this);
        supporter = new Supporter(this, dbhelpher);
        toastMsg = new ToastMessage();
        formats = new DecimalFormat("0.00");
 reciptNum = getIntent().getStringExtra("reciptNo");

        dbhelpher.openReadableDatabase();
        receiptAllList = dbhelpher.getReceiptListofData(reciptNum,
                supporter.getCompanyNo());
        dbhelpher.closeDatabase();

        formats = new DecimalFormat("0.00");


        PDFCreate();


    }

    // ReceiptPDFCreate.this.dismiss();
    public void PDFCreate() {

        String cmpNo = supporter.getCompanyNo();
        dbhelpher.openReadableDatabase();
        HhCompany company = dbhelpher.getCompanyData(cmpNo);
        setting = dbhelpher.getSettingData();
        dbhelpher.closeDatabase();


        prntCompName = company.getCompany_name();
        prntCompAddrs = company.getCompany_address() + ","
                + company.getCompany_city();
        prntCompState = company.getCompany_state() + " - "
                + company.getCompany_zip();
        prntCompCountry = company.getCompany_country();
        prntCompPhoneNo = company.getCompany_phone();
        prntTitle = "RECEIPT";

        HhReceipt01 recipt = receiptAllList.get(0);

        // getting date from receipt table
        day = recipt.getHhReceipt_receiptday();
        month = recipt.getHhReceipt_receiptmonth();
        year = recipt.getHhReceipt_receiptyear();

        date = supporter.getStringDate(year, month, day);
        // getting date for printing ...
        prntDate = date;

        prntCusNo = recipt.getHhReceipt_customernumber();
        prntCusName = recipt.getHhReceipt_customername();
        prntPayMode = recipt.getHhReceipt_receipttype();
        prntReciptNo = recipt.getHhReceipt_receiptnumber();
        prntUnapplied = "" + recipt.getHhReceipt_receiptunapplied();

        new PrinterConnectOperation().execute();

    }// end of oncreate method...

    private class PrinterConnectOperation extends
            AsyncTask<Void, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(ReceiptPDFCreate.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... params) {

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
                toastMsg.showToast(ReceiptPDFCreate.this,
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
                                toastMsg.showToast(ReceiptPDFCreate.this,
                                        "No Application match to Open Print File");
                            }
}
}, 1000);
                } catch (Exception e) {
                    toastMsg.showToast(ReceiptPDFCreate.this, "Not Software Match to Open Print File");
                    supporter.simpleNavigateTo(MainMenu.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                toastMsg.showToast(ReceiptPDFCreate.this,
                        toasttext);
                supporter.simpleNavigateTo(MainMenu.class);

            }


        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Connecting...");
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
           // document = new Document();
             document = new Document(new Rectangle(595 , 842 ), 25, 25, 25, 25);
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
            reportBody.setFont(FONT_BODY);
            writeTableTitleDetail();
            writeItemDetail(reportBody);
            document.add(reportBody);
            writenotesandtotal();
            document.close();
            resultSent = true;
            return resultSent;
        } catch (Exception e) {

            resultSent = false;
            return resultSent;
        } finally {
            return resultSent;
        }
    }


    private void writeHeader() {
        try {
            Paragraph paragraph = new Paragraph();


            if (!prntCompName.equals("")) {
                Paragraph childParagraph = new Paragraph(prntCompName, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }

            if (!prntCompAddrs.equals("")) {
                Paragraph childParagraph = new Paragraph(prntCompAddrs, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            if (!prntCompState.equals("")) {
                Paragraph childParagraph = new Paragraph(prntCompState, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            if (!prntCompPhoneNo.equals("")) {
                Paragraph childParagraph = new Paragraph("Phone No. "+prntCompPhoneNo, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            addEmptyLine(paragraph, 1);
            if (!prntTitle.equals("")) {
                Paragraph childParagraph = new Paragraph(prntTitle, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            addEmptyLine(paragraph, 2);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ZebraPrintConnException", e.toString());
        }

    }

    private void writeDetail() {
        try {
            Paragraph paragraph = new Paragraph();
            if (!prntDate.equals("")) {
                Paragraph childParagraph = new Paragraph("Date : " +prntDate, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.add(childParagraph);
            }
            if (!prntCusNo.equals("")) {
                Paragraph childParagraph = new Paragraph("Customer Number : " + prntCusNo, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.add(childParagraph);
            }
            if (!prntCusName.equals("")) {
                Paragraph childParagraph = new Paragraph("Customer Name : " +prntCusName, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.add(childParagraph);
            }
            if (!prntReciptNo.equals("")) {
                Paragraph childParagraph = new Paragraph("Receipt Number : " +prntReciptNo, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.add(childParagraph);
            }
             addEmptyLine(paragraph, 2);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ZebraPrintConnException", e.toString());
        }
    }

    private void writeTableTitleDetail() {
        try {
            Paragraph paragraph = new Paragraph();
            //list all the products sold to the customer
            float[] columnWidths = {2f, 2f, 8f, 4f};
            //create PDF table with the given widths
            table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(100);


            cell = new PdfPCell(new Phrase("SNo.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //  cell.setBorder((Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT));
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Apply", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Invoice No.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Applied Amount", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writeItemDetail(Paragraph reportBody) {


        int serialPosition = 0;
        int exp = 0;

        sNo = 1;
        reciptTotAmt = 0.00;

        for (int i = 0; i < receiptAllList.size(); i++) {


            HhReceipt01 recipt = receiptAllList.get(i);

            // for serial no printing...
            cell = new PdfPCell(new Phrase("" + sNo, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Yes", FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            prntInvNo = recipt.getHhReceipt_docnumber();
            prntApplyAmt = supporter.getCurrencyFormat(recipt
                    .getHhReceipt_appliedamount());
            applyAmt = recipt.getHhReceipt_appliedamount();

            cell = new PdfPCell(new Phrase(prntInvNo, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(prntApplyAmt, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

           /* cell = new PdfPCell(new Phrase("" + applyAmt, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);*/

            sNo++;
            reciptTotAmt = reciptTotAmt + applyAmt;

        }
        reportBody.add(table);
    }

    private void writenotesandtotal() {
        try {
            Paragraph paragraph = new Paragraph();
            addEmptyLine(paragraph, 2);
            Paragraph childParagraph = new Paragraph("Payment Mode : " + prntPayMode, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

             childParagraph = new Paragraph( "Receipt Unapplied : "
                            + supporter.getCurrencyFormat(Double.parseDouble(prntUnapplied)), FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

             childParagraph = new Paragraph("Receipt Total : "
                            + supporter.getCurrencyFormat(reciptTotAmt), FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            addEmptyLine(paragraph, 3);
             childParagraph = new Paragraph("Received by/Signature", FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.add(childParagraph);

           // paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);
        } catch (Exception e) {
             e.printStackTrace();
        }

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
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
}
