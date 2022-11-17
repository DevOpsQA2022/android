package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mobilesalesperson.model.Devices;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.HhPrepayment01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;
import com.zebra.android.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SalesSummary extends AppBaseActivity {

    /* Variable declarations */
    private Supporter supporter;
    private MspDBHelper dbhelpher;
    private TextView spName;
    private Spinner spnPayType;
    private TextView salesSummaryTotal;
    private EditText fromDate;
    private EditText toDate;
    private Button loadSalesSummary;

    private ArrayAdapter<String> adptPayTypeListSpinner;
    private List<String> payTypeList;

    private List<HhPrepayment01> salesSummaryList;
    private ListView salesSummaryListView;
    private ArrayAdapter<HhPrepayment01> adptPrepaySaleSummary;
    private List<HhTran01> salesTranSummaryList;
    private ArrayAdapter<HhTran01> adptTranSaleSummary;
    private int fromYear, toYear;
    private int fromMonth, toMonth;
    private int fromDay, toDay;
    private double sumTotal;
    private String strPayType;
    static final int FROM_DATE_DIALOG_ID = 0;
    static final int TO_DATE_DIALOG_ID = 1;
    private ToastMessage toastMsg;

    // printing variables
    private ZebraPrinterConnection zebraPrinterConnection;
    private ZebraPrinter printer;
    private int y;
    static final int MENU_PRINT_DIALOG_ID = 2;
    private Dialog dialog;
    private ListView blListView;
    private List<Devices> deviceList;
    private Devices devPojo;
    private ArrayAdapter<Devices> devAdapter;
    private String devName;
    private String devAddress;
    private DecimalFormat formats;

    private String prntCompName;
    private String prntCompAddrs;
    private String prntCompState;
    private String prntCompCountry;
    private String prntCompPhoneNo;
    private String prntTitle;
    private String prntFromDate;
    private String prntToDate;
    private String prntSpName;
    private String prntPayType;

    private String prntOrdNo;
    private String prntCusName;
    private String prntPayNo;
    private String prntPayAmt;
    private String prntTotAmt;

    private String cmpnyNo; // added for multicompany
    private HhSetting setting;
    private int noOfPrint;
    private String printerModel;
    private int xOrdNo;
    private int xCusName;
    private int xPayNo;
    private int xAmt;
    private PrinterLanguage printerLanguage;

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
    String filename = "msp_receipt_summary.pdf";
    String toasttext = "";


    private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            fromYear = year;
            fromMonth = monthOfYear;
            fromDay = dayOfMonth;
            fromDate.setText(supporter.getStringDate(fromYear, fromMonth + 1,
                    fromDay));
        }
    };

    // toDatepickerDialog
    private DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            toYear = year;
            toMonth = monthOfYear;
            toDay = dayOfMonth;
            toDate.setText(supporter.getStringDate(toYear, toMonth + 1, toDay));
            // listLoaded();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.sales_summary_layout);

        registerBaseActivityReceiver();

        dbhelpher = new MspDBHelper(this);
        supporter = new Supporter(this, dbhelpher);
        toastMsg = new ToastMessage();

        spName = (TextView) findViewById(R.id.txt_salesPersonNamePrepay);
        salesSummaryTotal = (TextView) findViewById(R.id.txt_SalesSummaryTotal);
        fromDate = (EditText) findViewById(R.id.edt_SalesSummaryFromDate);
        toDate = (EditText) findViewById(R.id.edt_SalesSummaryToDate);
        spnPayType = (Spinner) findViewById(R.id.spn_SalesSummaryType);
        loadSalesSummary = (Button) findViewById(R.id.btn_loadSalessummary);
        salesSummaryListView = (ListView) findViewById(R.id.lst_SalesSummaryItems);

        currentDate();

        cmpnyNo = supporter.getCompanyNo(); // added jul 12/2013

        // to load customer group code
        dbhelpher.openReadableDatabase();
        payTypeList = dbhelpher.getPayTypeList(cmpnyNo);
        dbhelpher.closeDatabase();

        adptPayTypeListSpinner = new ArrayAdapter<String>(SalesSummary.this,
                android.R.layout.simple_dropdown_item_1line, payTypeList);
        // adptPayTypeListSpinner.setDropDownViewResource(R.layout.spinner_item_layout);
        spnPayType.setAdapter(adptPayTypeListSpinner);

        strPayType = "";
        strPayType = (String) spnPayType.getItemAtPosition(spnPayType
                .getSelectedItemPosition());

        // SalesPrepaySummary Custom adapter loading datas
        salesSummaryList = new ArrayList<HhPrepayment01>();
        adptPrepaySaleSummary = new SalesSummaryAdapter(SalesSummary.this,
                salesSummaryList);
        salesSummaryListView.setAdapter(adptPrepaySaleSummary);

        // SalesTranSummary Custom adapter loading datas
        salesTranSummaryList = new ArrayList<HhTran01>();
        adptTranSaleSummary = new SalesTranSummaryAdapter(SalesSummary.this,
                salesTranSummaryList);
        // salesSummaryListView.setAdapter(adptTranSaleSummary);

        // to load current day summary data
        fromDate.setText(supporter.getStringDate(fromYear, fromMonth + 1,
                fromDay));
        toDate.setText(supporter.getStringDate(toYear, toMonth + 1, toDay));

        loadSalesSummaryData();

        String salespersonName = supporter.getSalesPerson();
        dbhelpher.openReadableDatabase();
        HhManager manager = dbhelpher.getManagerData(salespersonName, cmpnyNo);
        dbhelpher.closeDatabase();

        spName.setText(manager.getHhManager_username());

        // to handle fromDate click funtion
        fromDate.setOnTouchListener(new OnTouchListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                showDialog(FROM_DATE_DIALOG_ID);
                return true;
            }
        });

        // to handle toDate click funtion
        toDate.setOnTouchListener(new OnTouchListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                showDialog(TO_DATE_DIALOG_ID);
                return true;
            }
        });

        // payment type spinner click events...
        spnPayType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                strPayType = (String) spnPayType.getItemAtPosition(spnPayType
                        .getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        loadSalesSummary.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int currentMonth = c.get(Calendar.MONTH);
                int currentDay = c.get(Calendar.DAY_OF_MONTH);

                if ((fromYear > currentYear) || (toYear > currentYear)) {
                    clearListViewItems();
                    salesSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(SalesSummary.this,
                            "Enter valid from and to date");
                } else if (fromYear > toYear) {
                    clearListViewItems();
                    salesSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(SalesSummary.this,
                            "Enter valid from and to date");
                } else if ((fromYear == toYear) && (fromMonth > toMonth)) {
                    clearListViewItems();
                    salesSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(SalesSummary.this,
                            "Enter valid from and to date");
                } else if ((fromYear != toYear) && (fromDay > toDay)) {
                    clearListViewItems();
                    salesSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(SalesSummary.this,
                            "Enter valid from and to date");
                } else if (((fromMonth > currentMonth) || (toMonth > currentMonth))
                        && (fromYear == toYear)) {
                    clearListViewItems();
                    salesSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(SalesSummary.this,
                            "Enter valid from and to date");
                } else if (((toDay > currentDay))
                        && (fromYear == toYear)) {
                    clearListViewItems();
                    salesSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(SalesSummary.this,
                            "Enter valid from and to date");
                } else {
                    loadSalesSummaryData();
                }
            }

        });

		/*
         * This Section used for printing purpose.. Developed by T.Saravanan
		 */
        formats = new DecimalFormat("0.00");

        // printing values...
        dbhelpher.openReadableDatabase();
        HhCompany company = dbhelpher.getCompanyData(cmpnyNo);
        setting = dbhelpher.getSettingData();
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

        prntTitle = "SALES SUMMARY REPORT";
        prntSpName = manager.getHhManager_username();
        prntFromDate = fromDate.getText().toString();
        prntToDate = toDate.getText().toString();

    }// end of oncreate...

    private void clearListViewItems() {
        salesSummaryList.clear();
        adptPrepaySaleSummary.clear();
        adptPrepaySaleSummary.notifyDataSetChanged();

        salesTranSummaryList.clear();
        adptTranSaleSummary.clear();
        adptTranSaleSummary.notifyDataSetChanged();
    }

    private void loadSalesSummaryData() {

        if (strPayType.equals("CHARGE")) {

            clearListViewItems();
            dbhelpher.openReadableDatabase();
            salesTranSummaryList = dbhelpher.getTranSalesSummaryDatas(fromDay,
                    fromMonth + 1, fromYear, toDay, toMonth + 1, toYear,
                    cmpnyNo);
            dbhelpher.closeDatabase();

            int listSize = salesTranSummaryList.size();
            if (listSize == 0) {
                toastMsg.showToast(SalesSummary.this, "Data not available..");
                salesSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
            } else {
                sumTotal = 0.00;

                // to refresh list items in listview
                adptTranSaleSummary = new SalesTranSummaryAdapter(
                        SalesSummary.this, salesTranSummaryList);
                salesSummaryListView.setAdapter(adptTranSaleSummary);
                salesSummaryListView.setSelectionAfterHeaderView();

                for (int i = 0; i < listSize; i++) {
                    sumTotal = sumTotal
                            + salesTranSummaryList.get(i).getHhTran_extPrice();
                }

                String sumTotPrz = supporter.getCurrencyFormat(sumTotal);
                salesSummaryTotal.setText(sumTotPrz);

                prntTotAmt = salesSummaryTotal.getText().toString();
                prntPayType = strPayType;
            }

        } else {
            clearListViewItems();
            dbhelpher.openReadableDatabase();
            salesSummaryList = dbhelpher.getPrepaymentSalesSummaryDatas(
                    fromDay, fromMonth + 1, fromYear, toDay, toMonth + 1,
                    toYear, strPayType, cmpnyNo);
            dbhelpher.closeDatabase();

            int listSize = salesSummaryList.size();
            if (listSize == 0) {
                toastMsg.showToast(SalesSummary.this, "Data not available..");
                salesSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
            } else {
                sumTotal = 0.00;
                // to refresh list items in listview
                adptPrepaySaleSummary = new SalesSummaryAdapter(
                        SalesSummary.this, salesSummaryList);
                salesSummaryListView.setAdapter(adptPrepaySaleSummary);
                salesSummaryListView.setSelectionAfterHeaderView();

                for (int i = 0; i < listSize; i++) {
                    sumTotal = sumTotal
                            + salesSummaryList.get(i)
                            .getHhPrePayment_receiptAmount();
                }

                String sumTotPrz = supporter.getCurrencyFormat(sumTotal);
                salesSummaryTotal.setText(sumTotPrz);

                prntTotAmt = salesSummaryTotal.getText().toString();
                prntPayType = strPayType;
            }
        }

    }

    /**
     * method for menu control
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.receipt_summary_menu, menu);
        return true;
    }

    /**
     * method for menu control
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recipt_summary_print:

		/*	deviceList = supporter.getPairedDevices();

			if (deviceList.size() != 0) {
				showDialog(MENU_PRINT_DIALOG_ID);
			} else {
				toastMsg.showToast(SalesSummary.this,
						"Paired printer not available for printing");
			}*/
                showDialog(MENU_PRINT_DIALOG_ID);
                break;
        }
        return true;
    }

    // to create date picker dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog myDialog = null;
        dialog = null;
        switch (id) {
            case FROM_DATE_DIALOG_ID:

                myDialog = new DatePickerDialog(this, fromDateListener, fromYear,
                        fromMonth, fromDay);
                break;

            case TO_DATE_DIALOG_ID:

                myDialog = new DatePickerDialog(this, toDateListener, toYear,
                        toMonth, toDay);
                break;
            case MENU_PRINT_DIALOG_ID:
 //</History> Suresh 05-Oct-2017 Added for Print model Select
						printerModel = setting.getHhSetting_printerModel();
						if(printerModel.equals("PDF") && printerModel != null && printerModel!=""){
					 new PrinterPDFConnectOperation().execute();
                break;
						}
						else {
                dialog = new Dialog(SalesSummary.this);
                dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                dialog.setContentView(R.layout.print_list_layout);
                dialog.setTitle("Select Printer");
                dialog.setCancelable(false);
                dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                        R.drawable.tick);
                dialog.setCanceledOnTouchOutside(true);

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });

                // Prepare ListView in dialog
                blListView = (ListView) dialog.findViewById(R.id.lst_Device);
                devAdapter = new DeviceArrayAdapter(SalesSummary.this, deviceList);
                blListView.setAdapter(devAdapter);

                blListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int pos, long arg3) {
                        if (deviceList.size() != 0) {
                            final Devices devce = deviceList.get(pos);
                            devName = devce.getName();
                            devAddress = devce.getDeviceIP();

                            new PrinterConnectOperation().execute();
                        }
                        dialog.dismiss();
                    }
                });

        }
        }
        if (id == 2) {
            return dialog;
        } else {
            return myDialog;
        }
    }

    // to set current date
    private void currentDate() {
        Calendar c = Calendar.getInstance();
        fromYear = c.get(Calendar.YEAR);
        fromMonth = c.get(Calendar.MONTH);
        fromDay = c.get(Calendar.DAY_OF_MONTH);

        toYear = c.get(Calendar.YEAR);
        toMonth = c.get(Calendar.MONTH);
        toDay = c.get(Calendar.DAY_OF_MONTH);
    }

    private class PrinterConnectOperation extends
            AsyncTask<Void, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(SalesSummary.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            try {
                printer = connect();
                if (printer != null) {
                    result = "success";
                } else {
                    disconnect();
                    result = "error";
                }

                return result;
            } catch (Exception e) {
                disconnect();
                Log.e("tag", "error", e);
                result = "error";
            }

            return result;

        }

        @Override
        protected void onPostExecute(final String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result.equals("success")) {
                dialog.dismiss();
                sendDataToPrinter();
            } else {
                doPrintErrorAlert();
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

    private class PrinterPDFConnectOperation extends
            AsyncTask<Void, String, String> {

        private ProgressDialog dialog;

        public PrinterPDFConnectOperation() {
            dialog = new ProgressDialog(SalesSummary.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "failed";
            try {
                boolean isDataSentSuccess;
                isDataSentSuccess = sendPDFDataToPrinter();
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
                toastMsg.showToast(SalesSummary.this,
                        toasttext);
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(pdfFile), "application/pdf"));

                } catch (Exception e) {
                    toastMsg.showToast(SalesSummary.this, "Not Software Match to Open Print File");
                    supporter.simpleNavigateTo(MainMenu.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                toastMsg.showToast(SalesSummary.this,
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

    public boolean sendPDFDataToPrinter() {
        boolean resultSent = false;
        try {

            createfile();
           // document = new Document();
            //  document.setMargins(13, 3, 17, 20);
            // document.setMargins(0, 0, 0, 0);
            // document = new Document(new Rectangle(595 , 842 ), 0, 0, 0, 0);
             document = new Document(new Rectangle(595 , 842 ), 25, 25, 25, 25);
            docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            cb = docWriter.getDirectContent();
            //initialize fonts for text printing
            // initializeFonts();
            writePDFHeader();
            writePDFDetail();
            Paragraph reportBody = new Paragraph();
            reportBody.setFont(FONT_BODY);
            writePDFTableTitleDetail();
            writePDFItemDetail(reportBody);
            document.add(reportBody);
            writePDFTotal();
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

    private void writePDFHeader() {
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
            if (!prntCompCountry.equals("")) {
                Paragraph childParagraph = new Paragraph(prntCompCountry, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            if (!prntCompPhoneNo.equals("")) {
                Paragraph childParagraph = new Paragraph("Phone No. " + prntCompPhoneNo, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.add(childParagraph);
            }
            addEmptyLine(paragraph, 1);
            if (!prntTitle.equals("")) {
                Paragraph childParagraph = new Paragraph(prntTitle, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
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

    private void writePDFDetail() {
        try {
            Paragraph paragraph = new Paragraph();

            float[] columnWidthForTwoValues = {5.0f, 5.0f};
            Tablewithtwocoloum(paragraph, columnWidthForTwoValues, 100, "From Date : " + prntFromDate, "To Date : " + prntToDate, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, Element.ALIGN_LEFT, Element.ALIGN_RIGHT, Rectangle.NO_BORDER, Rectangle.NO_BORDER);
            addEmptyLine(paragraph, 1);

            Paragraph childParagraph = new Paragraph("Sales Person : " + prntSpName, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

            childParagraph = new Paragraph("Prepayment Type : " + prntPayType, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

            addEmptyLine(paragraph, 2);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ZebraPrintConnException", e.toString());
        }
    }

    private void writePDFTableTitleDetail() {
        try {
            Paragraph paragraph = new Paragraph();
            //list all the products sold to the customer
            float[] columnWidths = {5f, 5f, 3f, 3f};
            //create PDF table with the given widths
            table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(100);


            cell = new PdfPCell(new Phrase("Order No.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //  cell.setBorder((Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT));
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Cust. Name", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Payment No.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Amount", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writePDFItemDetail(Paragraph reportBody) {


        int serialPosition = 0;
        int exp = 0;
        int finaly = 0;
        if (strPayType.equals("CHARGE")) {
            for (int i = 0; i < salesTranSummaryList.size(); i++) {


                HhTran01 trans = salesTranSummaryList.get(i);

                prntOrdNo = trans.getHhTran_referenceNumber();

                dbhelpher.openReadableDatabase();
                String cusName = dbhelpher.getCustomerName(
                        trans.getHhTran_customerNumber(), cmpnyNo);
                dbhelpher.closeDatabase();


                prntCusName = cusName;
                prntPayNo = " ";
                prntPayAmt = supporter.getCurrencyFormat(trans
                        .getHhTran_extPrice());

                // for OrdNo printing...
                cell = new PdfPCell(new Phrase(prntOrdNo, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                //cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // for CusName printing...
                cell = new PdfPCell(new Phrase(prntCusName, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // for PayNo printing...
                cell = new PdfPCell(new Phrase(prntPayNo, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // for prntPayAmt printing...
                cell = new PdfPCell(new Phrase(prntPayAmt, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }
        } else {
            for (int i = 0; i < salesSummaryList.size(); i++) {

                HhPrepayment01 prePay = salesSummaryList.get(i);

                prntOrdNo = prePay.getHhPrePayment_referenceNumber();

                dbhelpher.openReadableDatabase();
                String cusName = dbhelpher.getCustomerName(
                        prePay.getHhPrePayment_customerNumber(), cmpnyNo);
                dbhelpher.closeDatabase();

                prntCusName = cusName;
                prntPayNo = prePay.getHhPrePayment_checkReceiptNo();
                prntPayAmt = supporter.getCurrencyFormat(prePay
                        .getHhPrePayment_receiptAmount());

                // for OrdNo printing...
                cell = new PdfPCell(new Phrase(prntOrdNo, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // for CusName printing...
                cell = new PdfPCell(new Phrase(prntCusName, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // for PayNo printing...
                cell = new PdfPCell(new Phrase(prntPayNo, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // for prntPayAmt printing...
                cell = new PdfPCell(new Phrase(prntPayAmt, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }
        }
        reportBody.add(table);
    }


    private void writePDFTotal() {
        try {
            Paragraph paragraph = new Paragraph();
            addEmptyLine(paragraph, 2);
            Paragraph childParagraph = new Paragraph("Total Amount : " + prntTotAmt, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.add(childParagraph);


            document.add(paragraph);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public void doPrintErrorAlert() {
        AlertDialog.Builder alertMemory = new AlertDialog.Builder(
                SalesSummary.this);
        alertMemory.setTitle("Warning");
        alertMemory.setIcon(R.drawable.warning);
        alertMemory.setCancelable(false);
        alertMemory.setMessage("Printer connection problem");
        alertMemory.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertMemory.show();
    }

    // printer connection method....
    public ZebraPrinter connect() {
        zebraPrinterConnection = null;
        zebraPrinterConnection = new BluetoothPrinterConnection(devAddress);
        try {
            zebraPrinterConnection.open();
        } catch (ZebraPrinterConnectionException e) {
            Log.e("Connection Error", "Comm Error! Disconnecting");
        }

        ZebraPrinter printer = null;

        if (zebraPrinterConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory
                        .getInstance(zebraPrinterConnection);
                printerLanguage = printer.getPrinterControlLanguage().CPCL;
                Log.i("Printer Language is", printerLanguage.toString());
            } catch (ZebraPrinterConnectionException e) {
                showToast("Connection : Unknown Printer Language.");
                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                showToast("Language : Unknown Printer Language.");
                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            }
        }

        return printer;
    }

    // printing data to Zebra printer....
    private void sendDataToPrinter() {
        try {
            for (int i = 0; i < noOfPrint; i++) {
                writeHeaderSpace();
                writeHeader();
                writeDetail();
                writeTableTitleDetail();
                writeLine();
                writeItemDetail();
            }
            if (zebraPrinterConnection instanceof BluetoothPrinterConnection) {
                String friendlyName = ((BluetoothPrinterConnection) zebraPrinterConnection)
                        .getFriendlyName();
            }
        } catch (Exception e) {
            Log.e("ZebraPrinterConnectionException", e.toString());
        } finally {
            disconnect();
        }
    }

    private void writeHeaderSpace() {
        y = 0;
        y += 25;
        writeText("", 12, y);
    }

    private void writeHeader() {
        try {
            byte[] compName = getCompAddrs(prntCompName);
            byte[] title = getConfigTitle();

            zebraPrinterConnection.write(compName);

            if (!prntCompAddrs.equals("")) {
                byte[] compAddr = getCompAddrs(prntCompAddrs);
                zebraPrinterConnection.write(compAddr);
            }

            if (!prntCompState.equals("")) {
                byte[] compState = getCompAddrs(prntCompState);
                zebraPrinterConnection.write(compState);
            }

            // if u need print country name add below code...
            /*
             * if (!prntCompCountry.equals("")) { byte[] compCountry =
			 * getCompAddrs(prntCompCountry);
			 * zebraPrinterConnection.write(compCountry); }
			 */

            if (!prntCompPhoneNo.equals("")) {
                byte[] compPhoneNo = getCompAddrs("Phone No. "
                        + prntCompPhoneNo);
                zebraPrinterConnection.write(compPhoneNo);
            }

            zebraPrinterConnection.write(title);
        } catch (ZebraPrinterConnectionException e) {
            Log.e("ZebraPrinterConnectionException", e.toString());
        }

    }

    private void writeDetail() {
        if (printerModel.equals("3-inch")) {
            y += 25;
            writeText("From Date : " + prntFromDate, 14, y);
            writeText("To Date : " + prntToDate, 370, y);

            y += 25;
            writeText("", 12, y);

            y += 25;
            writeText("Sales Person : " + prntSpName, 12, y);

            y += 25;
            writeText("Prepayment Type : " + prntPayType, 12, y);

            y += 25;
            writeText("", 12, y);
        } else if (printerModel.equals("4-inch")) {/*
													 * y += 32;
													 * writeText("From Date : "
													 * + prntFromDate, 24, y);
													 * writeText("To Date : " +
													 * prntToDate, 560, y);
													 * 
													 * y += 32; writeText("",
													 * 24, y);
													 * 
													 * y += 32;
													 * writeText("Sales Person : "
													 * + prntSpName, 24, y);
													 * 
													 * y += 32; writeText(
													 * "Prepayment Type : " +
													 * prntPayType, 24, y);
													 * 
													 * y += 32; writeText("",
													 * 24, y);
													 */
            writeInfoDetail();
        }

    }

    // Troy Facing some issue in printing Information in 4-inch printer
    private void writeInfoDetail() {

        byte[] configLabel1 = null;
        StringBuffer strBuff = new StringBuffer();

        y += 25;
        appendLineToPrint(strBuff, "From Date : " + prntFromDate, 24, y);

        y += 32;
        appendLineToPrint(strBuff, "To Date : " + prntToDate, 560, y);

        y += 32;
        appendLineToPrint(strBuff, "", 24, y);

        y += 32;
        appendLineToPrint(strBuff, "Sales Person : " + prntSpName, 24, y);

        y += 32;
        appendLineToPrint(strBuff, "Prepayment Type : " + prntPayType, 24, y);

        y += 32;
        appendLineToPrint(strBuff, "", 24, y);

        y += 80;

        appendLineToPrint(strBuff, "Order No.", 24, y);
        appendLineToPrint(strBuff, "Cust. Name", 240, y);
        appendLineToPrint(strBuff, "Payment No.", 486, y);
        appendLineToPrint(strBuff, "Amount", 664, y);

        y += 24;
        appendLineToPrint(strBuff, "--------", 24, y);
        appendLineToPrint(strBuff, "---------", 240, y);
        appendLineToPrint(strBuff, "----------", 486, y);
        appendLineToPrint(strBuff, "------", 664, y);

        y += 32;
        String str = "! 0 200 200 " + y + " 1\r\n";// +"SETBOLD 1\r\n";
        strBuff.insert(0, str);
        strBuff.append("PRINT\r\n");

        try {
            zebraPrinterConnection.write(strBuff.toString().getBytes());
        } catch (ZebraPrinterConnectionException e) {
            e.printStackTrace();
        }

    }

    private void writeTableTitleDetail() {
        if (printerModel.equals("3-inch")) {
            y += 25;
            writeText("Order No.", 12, y);
            writeText("Cust. Name", 164, y);
            writeText("Payment No.", 296, y);
            writeText("Amount", 448, y);
        } else if (printerModel.equals("4-inch")) {/*
													 * y += 25;
													 * writeText("Order No.",
													 * 24, y);
													 * writeText("Cust. Name",
													 * 240, y);
													 * writeText("Payment No.",
													 * 456, y);
													 * writeText("Amount", 664,
													 * y);
													 */
        }

    }

    private void writeLine() {
        if (printerModel.equals("3-inch")) {
            y += 15;
            writeText("------------", 12, y);
            writeText("---------------", 148, y);
            writeText("---------------", 288, y);
            writeText("-------------", 440, y);
        } else if (printerModel.equals("4-inch")) {/*
													 * y += 24; writeLines(
													 * "-----------------", 24,
													 * y); writeLines(
													 * "-----------------------"
													 * , 240, y); writeLines(
													 * "---------------------",
													 * 456, y);
													 * writeLines("--------------"
													 * , 664, y);
													 */
        }

    }

    private void writeItemDetail() {

        byte[] configLabel1 = null;
        StringBuffer strBuff = new StringBuffer();

        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
                    .getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            if (printerModel.equals("3-inch")) {
                xOrdNo = 14;
                xCusName = 156;
                xPayNo = 296;
                xAmt = 410;
            } else if (printerModel.equals("4-inch")) {
                xOrdNo = 24;
                xCusName = 240;
                xPayNo = 486;
                xAmt = 664;
            }

            int serialPosition = 0;
            int exp = 0;
            int finaly = 0;
            if (strPayType.equals("CHARGE")) {
                for (int i = 0; i < salesTranSummaryList.size(); i++) {
                    y = finaly + exp;
                    if (i != 0)
                        y += 32;
                    serialPosition += y;

                    HhTran01 trans = salesTranSummaryList.get(i);

                    prntOrdNo = trans.getHhTran_referenceNumber();

                    dbhelpher.openReadableDatabase();
                    String cusName = dbhelpher.getCustomerName(
                            trans.getHhTran_customerNumber(), cmpnyNo);
                    dbhelpher.closeDatabase();


                    prntCusName = cusName;
                    prntPayNo = " ";
                    prntPayAmt = supporter.getCurrencyFormat(trans
                            .getHhTran_extPrice());

                    // for OrdNo printing...
                    if (prntOrdNo.length() <= 10) {
                        appendLineToPrint(strBuff, prntOrdNo, xOrdNo, y);
                        finaly = y;
                    } else {
                        int len1 = 0;
                        String tempstr = "";
                        int tempy = 0;
                        Boolean chk = false;

                        tempy = y;
                        tempstr = prntOrdNo;
                        while (chk == false) {
                            if (tempstr.length() > 10) {
                                appendLineToPrint(strBuff,
                                        tempstr.substring(0, 10), xOrdNo, y);
                                len1 = tempstr.length();
                                if (len1 > 20) {
                                    len1 = 10;
                                } else {
                                    len1 = tempstr.length() - 10;
                                }

                                tempstr = tempstr.substring(10, 10 + len1);
                                y += 25;
                            } else {
                                appendLineToPrint(strBuff, tempstr, xOrdNo, y);
                                chk = true;
                            }
                        }
                        finaly = y;
                        y = tempy;
                    }

                    // for CusName printing...
                    if (prntCusName.length() <= 11) {
                        appendLineToPrint(strBuff, prntCusName, xCusName, y);

                        finaly = y;

                        serialPosition = y;
                    } else {
                        int len1 = 0;
                        String tempstr = "";
                        int tempy = 0;
                        Boolean chk = false;

                        tempy = y;
                        tempstr = prntCusName;
                        while (chk == false) {
                            if (tempstr.length() > 11) {
                                appendLineToPrint(strBuff,
                                        tempstr.substring(0, 10), xCusName, y);
                                len1 = tempstr.length();
                                if (len1 > 22) {
                                    len1 = 11;
                                } else {
                                    len1 = tempstr.length() - 11;
                                }

                                tempstr = tempstr.substring(11, 11 + len1);
                                y += 25;
                            } else {
                                appendLineToPrint(strBuff, tempstr, xCusName, y);
                                chk = true;
                            }
                        }
                        finaly = y;
                        y = tempy;
                    }

                    // for PayNo printing...
                    if (prntPayNo.length() <= 11) {
                        appendLineToPrint(strBuff, prntPayNo, xPayNo, y);
                        if (finaly < y) {
                            finaly = y;
                        }
                        serialPosition = y;
                    } else {
                        int len1 = 0;
                        String tempstr = "";
                        int tempy = 0;
                        Boolean chk = false;

                        tempy = y;
                        tempstr = prntPayNo;
                        while (chk == false) {
                            if (tempstr.length() > 11) {
                                appendLineToPrint(strBuff,
                                        tempstr.substring(0, 10), xPayNo, y);
                                len1 = tempstr.length();
                                if (len1 > 22) {
                                    len1 = 11;
                                } else {
                                    len1 = tempstr.length() - 11;
                                }

                                tempstr = tempstr.substring(11, 11 + len1);
                                y += 25;
                            } else {
                                appendLineToPrint(strBuff, tempstr, xPayNo, y);
                                chk = true;
                            }
                        }
                        if (finaly < y) {
                            finaly = y;
                        }
                        serialPosition = y;
                        y = tempy;
                    }

                    if (printerModel.equals("3-inch")) {
                        int tempSize2 = prntPayAmt.length();
                        int tempPixSpace2 = tempSize2 * 8;
                        appendLineToPrint(strBuff, prntPayAmt,
                                448 + (80 - tempPixSpace2), y);
                    } else if (printerModel.equals("4-inch")) {
                        int tempSize2 = prntPayAmt.length();
                        int tempPixSpace2 = tempSize2 * 8;
                        appendLineToPrint(strBuff, prntPayAmt,
                                664 + (80 - tempPixSpace2), y);
                    }

                }
            } else {
                for (int i = 0; i < salesSummaryList.size(); i++) {
                    y = finaly + exp;
                    if (i != 0)
                        y += 32;
                    serialPosition += y;

                    HhPrepayment01 prePay = salesSummaryList.get(i);

                    prntOrdNo = prePay.getHhPrePayment_referenceNumber();

                    dbhelpher.openReadableDatabase();
                    String cusName = dbhelpher.getCustomerName(
                            prePay.getHhPrePayment_customerNumber(), cmpnyNo);
                    dbhelpher.closeDatabase();

                    prntCusName = cusName;
                    prntPayNo = prePay.getHhPrePayment_checkReceiptNo();
                    prntPayAmt = supporter.getCurrencyFormat(prePay
                            .getHhPrePayment_receiptAmount());

                    // for OrdNo printing...
                    if (prntOrdNo.length() <= 10) {
                        appendLineToPrint(strBuff, prntOrdNo, xOrdNo, y);
                        finaly = y;
                    } else {
                        int len1 = 0;
                        String tempstr = "";
                        int tempy = 0;
                        Boolean chk = false;

                        tempy = y;
                        tempstr = prntOrdNo;
                        while (chk == false) {
                            if (tempstr.length() > 10) {
                                appendLineToPrint(strBuff,
                                        tempstr.substring(0, 10), xOrdNo, y);
                                len1 = tempstr.length();
                                if (len1 > 20) {
                                    len1 = 10;
                                } else {
                                    len1 = tempstr.length() - 10;
                                }

                                tempstr = tempstr.substring(10, 10 + len1);
                                y += 25;
                            } else {
                                appendLineToPrint(strBuff, tempstr, xOrdNo, y);
                                chk = true;
                            }
                        }
                        finaly = y;
                        y = tempy;
                    }

                    // for CusName printing...
                    if (prntCusName.length() <= 11) {
                        appendLineToPrint(strBuff, prntCusName, xCusName, y);

                        finaly = y;

                        serialPosition = y;
                    } else {
                        int len1 = 0;
                        String tempstr = "";
                        int tempy = 0;
                        Boolean chk = false;

                        tempy = y;
                        tempstr = prntCusName;
                        while (chk == false) {
                            if (tempstr.length() > 11) {
                                appendLineToPrint(strBuff,
                                        tempstr.substring(0, 10), xCusName, y);
                                len1 = tempstr.length();
                                if (len1 > 22) {
                                    len1 = 11;
                                } else {
                                    len1 = tempstr.length() - 11;
                                }

                                tempstr = tempstr.substring(11, 11 + len1);
                                y += 25;
                            } else {
                                appendLineToPrint(strBuff, tempstr, xCusName, y);
                                chk = true;
                            }
                        }
                        finaly = y;
                        y = tempy;
                    }

                    // for PayNo printing...
                    if (prntPayNo.length() <= 11) {
                        appendLineToPrint(strBuff, prntPayNo, xPayNo, y);
                        if (finaly < y) {
                            finaly = y;
                        }
                        serialPosition = y;
                    } else {
                        int len1 = 0;
                        String tempstr = "";
                        int tempy = 0;
                        Boolean chk = false;

                        tempy = y;
                        tempstr = prntPayNo;
                        while (chk == false) {
                            if (tempstr.length() > 11) {
                                appendLineToPrint(strBuff,
                                        tempstr.substring(0, 10), xPayNo, y);
                                len1 = tempstr.length();
                                if (len1 > 22) {
                                    len1 = 11;
                                } else {
                                    len1 = tempstr.length() - 11;
                                }

                                tempstr = tempstr.substring(11, 11 + len1);
                                y += 25;
                            } else {
                                appendLineToPrint(strBuff, tempstr, xPayNo, y);
                                chk = true;
                            }
                        }
                        if (finaly < y) {
                            finaly = y;
                        }
                        serialPosition = y;
                        y = tempy;
                    }

                    if (printerModel.equals("3-inch")) {
                        int tempSize2 = prntPayAmt.length();
                        int tempPixSpace2 = tempSize2 * 8;
                        appendLineToPrint(strBuff, prntPayAmt,
                                448 + (80 - tempPixSpace2), y);
                    } else if (printerModel.equals("4-inch")) {
                        int tempSize2 = prntPayAmt.length();
                        int tempPixSpace2 = tempSize2 * 8;
                        appendLineToPrint(strBuff, prntPayAmt,
                                664 + (80 - tempPixSpace2), y);
                    }

                }
            }


        }
        if (printerModel.equals("3-inch")) {
            y += 25;
            appendLineToPrint(strBuff, "", 12, y);

            y += 25;
            appendLineToPrint(strBuff, "-------------------------", 360, y);

            y += 25;
            appendLineToPrint(strBuff, "Total :", 348, y);

            int tempSize2 = prntPayAmt.length();
            int tempPixSpace1 = tempSize2 * 8;
            appendLineToPrint(strBuff, prntTotAmt, 448 + (80 - tempPixSpace1),
                    y);

            y += 25;
            appendLineToPrint(strBuff, "-------------------------", 360, y);

            y += 25;
            appendLineToPrint(strBuff, "", 12, y);

            y += 25;
            appendLineToPrint(strBuff, "", 12, y);
            String str = "! 0 200 200 " + y + " 1 \r\n" + "SETBOLD 1 \r\n";
            strBuff.insert(0, str);
            strBuff.append("PRINT\r\n\n");
        } else if (printerModel.equals("4-inch")) {
            y += 32;
            appendLineToPrint(strBuff, "", 12, y);

            y += 32;
            appendLineToPrint(strBuff, "------------------------", 432, y);

            y += 32;
            appendLineToPrint(strBuff, "Total :", 440, y);

            int tempSize2 = prntTotAmt.length();
            int tempPixSpace2 = tempSize2 * 8;
            appendLineToPrint(strBuff, prntTotAmt, 664 + (80 - tempPixSpace2),
                    y);

            y += 32;
            appendLineToPrint(strBuff, "------------------------", 432, y);

            y += 32;
            appendLineToPrint(strBuff, "", 12, y);

            y += 32;
            appendLineToPrint(strBuff, "", 12, y);
            String str = "! 0 200 200 " + y + " 1 \r\n";// +"SETBOLD 1 \r\n";
            strBuff.insert(0, str);
            strBuff.append("PRINT\r\n\n");
        }

        try {
            zebraPrinterConnection.write(strBuff.toString().getBytes());
        } catch (ZebraPrinterConnectionException e) {
            e.printStackTrace();
        }

    }

    private StringBuffer appendLineToPrint(StringBuffer strBuff,
                                           String strValue, int x, int y) {/*
											 * if
											 * (printerModel.equals("3-inch")) {
											 * String prntFormat =
											 * "! U1 SETLP 0 2 24 " +
											 * "! U1 SETBOLD 1 " + "! U1 X" +
											 * " " + x + " " + "! U1 Y" + " " +
											 * y + " " + strValue;
											 * 
											 * strBuff =
											 * strBuff.append(prntFormat); }
											 * else if
											 * (printerModel.equals("4-inch")) {
											 * 
											 * String prntFormat =
											 * "! U1 SETLP 5 0 24 " +
											 * "! U1 SETBOLD 0 " + "! U1 X" +
											 * " " + x + " " + "! U1 Y" + " " +
											 * y + " " + strValue;
											 * 
											 * strBuff =
											 * strBuff.append(prntFormat); }
											 * 
											 * return strBuff;
											 */

        if (printerModel.equals("3-inch")) {/*
											 * String prntFormat =
											 * "! U1 SETLP 0 2 24 " +
											 * "! U1 SETBOLD 1 " + "! U1 X" +
											 * " " + x + " " + "! U1 Y" + " " +
											 * y + " " + strValue;
											 * 
											 * strBuff =
											 * strBuff.append(prntFormat);
											 */
            String prntFormat = "T 0 2 " + " " + x + " " + y + " " + strValue
                    + "\r\n";

            strBuff = strBuff.append(prntFormat);
        } else if (printerModel.equals("4-inch")) {/*
													 * String prntFormat =
													 * "! U1 SETLP 5 0 24 " +
													 * "! U1 SETBOLD 0 " +
													 * "! U1 X" + " " + x + " "
													 * + "! U1 Y" + " " + y +
													 * " " + strValue;
													 * 
													 * strBuff =
													 * strBuff.append(prntFormat
													 * );
													 */
            String prntFormat = "T 5 0 " + " " + x + " " + y + " " + strValue
                    + "\r\n";

            strBuff = strBuff.append(prntFormat);
        }

        return strBuff;

    }

    // common method for printing data...
    private void writeText(String str, int x, int y) {
        // PrinterLanguage printerLanguage =
        // printer.getPrinterControlLanguage();

        byte[] configLabel1 = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
                    .getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            if (printerModel.equals("3-inch")) {
                String data = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
                        + "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
                        + str;

                configLabel1 = data.getBytes();
            } else if (printerModel.equals("4-inch")) {
                String data = "! U1 SETLP 5 0 24 " + "! U1 SETBOLD 0 "
                        + "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
                        + str;

                configLabel1 = data.getBytes();
            }

        }
        try {
            zebraPrinterConnection.write(configLabel1);
        } catch (ZebraPrinterConnectionException e) {
            e.printStackTrace();
        }

    }

    // common method for printing data...
    private void writeLines(String str, int x, int y) {
        // PrinterLanguage printerLanguage =
        // printer.getPrinterControlLanguage();

        byte[] configLabel1 = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
                    .getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            if (printerModel.equals("3-inch")) {
                String data = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
                        + "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
                        + str;

                configLabel1 = data.getBytes();
            } else if (printerModel.equals("4-inch")) {
                String data = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
                        + "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
                        + str;

                configLabel1 = data.getBytes();
            }

        }
        try {
            zebraPrinterConnection.write(configLabel1);
        } catch (ZebraPrinterConnectionException e) {
            e.printStackTrace();
        }

    }

    // printing Title for transaction...
    private byte[] getConfigTitle() {
        // PrinterLanguage printerLanguage =
        // printer.getPrinterControlLanguage();

        byte[] configLabel1 = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
                    .getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            if (printerModel.equals("3-inch")) {
                String cpclConfigLabel1 = "! 0 200 200 45 1"
                        + "ON-FEED IGNORE\r\n" + "CENTER\r\n" + "T 5 0 10 15"
                        + " " + prntTitle + "\r\n" + "PRINT\r\n";
                configLabel1 = cpclConfigLabel1.getBytes();
            } else if (printerModel.equals("4-inch")) {
                String cpclConfigLabel1 = "! 0 200 200 60 1"
                        + "ON-FEED IGNORE\r\n" + "CENTER\r\n" + "T 5 2 10 15"
                        + " " + prntTitle + "\r\n" + "PRINT\r\n";
                configLabel1 = cpclConfigLabel1.getBytes();
            }

        }
        return configLabel1;
    }

    // printing company address in center...
    private byte[] getCompAddrs(String cmpAddrs) {
        // PrinterLanguage printerLanguage =
        // printer.getPrinterControlLanguage();

        byte[] configLabel1 = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
                    .getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            String cpclConfigLabel1 = "! 0 200 200 40 1" + "ON-FEED IGNORE\r\n"
                    + "CENTER\r\n" + "T 5 0 10 15" + " " + cmpAddrs + "\r\n"
                    + "PRINT\r\n";
            configLabel1 = cpclConfigLabel1.getBytes();
        }
        return configLabel1;
    }

    public void disconnect() {
        try {
            if (zebraPrinterConnection != null) {
                zebraPrinterConnection.close();
            }
        } catch (ZebraPrinterConnectionException e) {
            showToast("COMM Error! Disconnected.");
        }
    }

    // Toast is here...
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

}
