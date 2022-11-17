package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author T.SARAVANAN class to showing transaction details for particular from
 *         date to end date...
 */

public class TransactionSummary extends AppBaseActivity {

    /* Variable declarations */
    private Supporter supporter;
    private MspDBHelper dbhelpher;
    private TextView spName;
    private TextView currency;
    private TextView summaryTotal;
    private EditText fromDate;
    private EditText toDate;
    private Spinner mailTo;
    private Button loadSummary;
    private String[] mail;
    private ArrayAdapter<String> mailSpnAdapter;
    private ListView tranSummaryListView;
    private List<HhTran01> tranSummaryList;
    private List<HhTran01> transAllList;
    private ArrayAdapter<HhTran01> adptTranSummary;
    private ToastMessage toastMsg;

    private int fromYear, toYear;
    private int fromMonth, toMonth;
    private int fromDay, toDay;
    static final int FROM_DATE_DIALOG_ID = 0;
    static final int TO_DATE_DIALOG_ID = 1;

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
    private String devAddress;
    private String devName;
    private String prntSpname;
    private String prntCompName;
    private String prntCompAddrs;
    private String prntCompState;
    private String prntCompCountry;
    private String prntCompPhone;
    private String str;
    private Format format1;
    private Date dateNow;
    private String prntDate;
    private int sNo;
    private String prntDate1;
    private String prntOrdNo;
    private String prntCusNo;
    private String prntTotQty;
    private String prntNetAmt;
    private String prntTotal;
    private double totAmt;
    private DecimalFormat formats;
    private int day;
    private int month;
    private int year;
    private int qty;
    private int tempQty;
    private String cmpnyNo; // added for multicompany
    private int noOfPrint;
    private String printerModel;
    private HhSetting setting;
    private SharedPreferences transdata;
    private SharedPreferences refno;

    private int xSno;
    private int xDate;
    private int xOrdNo;
    private int xCusNo;
    private int xQty;
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
    String filename = "msp_trans_summary.pdf";
    String toasttext = "";

    // hiding fields
    private TextView transSummaryMailTo;

    // fromDatepickerDialog
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
        setContentView(R.layout.transaction_summary_layout);

        registerBaseActivityReceiver();

        dbhelpher = new MspDBHelper(this);
        supporter = new Supporter(this, dbhelpher);
        toastMsg = new ToastMessage();

        spName = (TextView) findViewById(R.id.txt_salesPersonName);
        currency = (TextView) findViewById(R.id.txt_TransSummaryCurrency);
        summaryTotal = (TextView) findViewById(R.id.txt_transSummaryTotal);
        fromDate = (EditText) findViewById(R.id.edt_TranSummaryFromDate);
        toDate = (EditText) findViewById(R.id.edt_TranSummaryToDate);
        mailTo = (Spinner) findViewById(R.id.spn_transSummaryMailTo);
        tranSummaryListView = (ListView) findViewById(R.id.lst_TransSummaryItems);
        loadSummary = (Button) findViewById(R.id.btn_loadTransummary);
        transSummaryMailTo = (TextView) findViewById(R.id.txt_transSummaryMailTo);
        mailTo.setVisibility(View.GONE);
        transSummaryMailTo.setVisibility(View.GONE);

        currentDate();
        cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013
        tranSummaryList = new ArrayList<HhTran01>();

        // Transaction Summary Custom adapter loading datas
        adptTranSummary = new TransactionSummaryAdapter(
                TransactionSummary.this, tranSummaryList);
        tranSummaryListView.setAdapter(adptTranSummary);

        // to load current day summary data
        fromDate.setText(supporter.getStringDate(fromYear, fromMonth + 1,
                fromDay));
        toDate.setText(supporter.getStringDate(toYear, toMonth + 1, toDay));
        listLoaded();

        String salespersonName = supporter.getSalesPerson();
        dbhelpher.openReadableDatabase();
        HhManager manager = dbhelpher.getManagerData(salespersonName, cmpnyNo);
        dbhelpher.closeDatabase();

        spName.setText(manager.getHhManager_username());
        currency.setText(manager.getHhManager_currency());

        transdata =  getSharedPreferences("transdata",
                Context.MODE_PRIVATE);
        refno =  getSharedPreferences("refno",
                Context.MODE_PRIVATE);


        mail = new String[2];
        mail = loadMailToData(mail);

        mailSpnAdapter = new ArrayAdapter<String>(TransactionSummary.this,
                android.R.layout.simple_dropdown_item_1line, mail);
        // mailSpnAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        mailTo.setAdapter(mailSpnAdapter);

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

        loadSummary.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int currentMonth = c.get(Calendar.MONTH);
                int currentDay = c.get(Calendar.DAY_OF_MONTH);

                if ((fromYear > currentYear) || (toYear > currentYear)) {
                    tranSummaryList.clear();
                    adptTranSummary.clear();
                    adptTranSummary.notifyDataSetChanged();
                    summaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(TransactionSummary.this,
                            "Enter valid from and to date");
                } else if (fromYear > toYear) {
                    tranSummaryList.clear();
                    adptTranSummary.clear();
                    adptTranSummary.notifyDataSetChanged();
                    summaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(TransactionSummary.this,
                            "Enter valid from and to date");
                } else if ((fromYear == toYear) && (fromMonth > toMonth)) {
                    tranSummaryList.clear();
                    adptTranSummary.clear();
                    adptTranSummary.notifyDataSetChanged();
                    summaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(TransactionSummary.this,
                            "Enter valid from and to date");
                } else if ((fromYear == toYear) && (fromDay > toDay) && (fromMonth > toMonth)){
                    tranSummaryList.clear();
                    adptTranSummary.clear();
                    adptTranSummary.notifyDataSetChanged();
                    summaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(TransactionSummary.this,
                            "Enter valid from and to date");
                } else if (((fromMonth > currentMonth) || (toMonth > currentMonth))
                        && (fromYear == toYear)) {
                    tranSummaryList.clear();
                    adptTranSummary.clear();
                    adptTranSummary.notifyDataSetChanged();
                    summaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(TransactionSummary.this,
                            "Enter valid from and to date");
                } else if ((/*(fromDay > currentDay) || */(toDay < currentDay))
                        && (fromYear == toYear)) {
                    tranSummaryList.clear();
                    adptTranSummary.clear();
                    adptTranSummary.notifyDataSetChanged();
                    summaryTotal.setText(supporter.getCurrencyFormat(0.00));
                    toastMsg.showToast(TransactionSummary.this,
                            "Enter valid from and to date");
                } else {
                    listLoaded();
                }
            }
        });

		/*
         * This Section used for printing purpose.. Developed by T.Saravanan
		 */

        // paired device list
        formats = new DecimalFormat("0.00");

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
        prntCompPhone = company.getCompany_phone();
        prntSpname = manager.getHhManager_username();

        // getting date for printing ...
        dateNow = new Date();
        format1 = new SimpleDateFormat("MMM dd yyyy");
        str = format1.format(dateNow);
        prntDate = "Date : " + str;

        prntTotal = summaryTotal.getText().toString();

    }

    private String[] loadMailToData(String[] mail) {
        mail[0] = "Company";
        mail[1] = "Sales Person";
        return mail;

    }

    /**
     * method for menu control
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trans_summary_menu, menu);
        return true;
    }

    /**
     * method for menu control
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trans_summary_print:

		/*	deviceList = supporter.getPairedDevices();

			if (deviceList.size() != 0) {
				showDialog(MENU_PRINT_DIALOG_ID);
			} else {
				toastMsg.showToast(TransactionSummary.this,
						"Paired printer not available for printing");
			}
			break;*/
                showDialog(MENU_PRINT_DIALOG_ID);
                // This Email hiding only for MSPV1.0
                /*
                 * case R.id.trans_summary_mail: // implement mail method break;
                 */
            case R.id.trans_save_comments:

                String comen = supporter.getcommentsdata();
                String refno = supporter.getrefno();

                    dbhelpher.openWritableDatabase();
                    dbhelpher.updateTransComments(comen,refno);
                    dbhelpher.closeDatabase();


                Toast.makeText(TransactionSummary.this,"updated",Toast.LENGTH_SHORT).show();
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
                if (printerModel.equals("PDF") && printerModel != null && printerModel != "") {
                    new PrinterPDFConnectOperation().execute();
                    break;
                } else {
                    dialog = new Dialog(TransactionSummary.this);
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
                    devAdapter = new DeviceArrayAdapter(TransactionSummary.this,
                            deviceList);
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

    // for Zebra QL 420plus 4inch printer...
    private class PrinterConnectOperation extends
            AsyncTask<Void, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(TransactionSummary.this);
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
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
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
            dialog = new ProgressDialog(TransactionSummary.this);
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
                toastMsg.showToast(TransactionSummary.this,
                        toasttext);
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(pdfFile), "application/pdf"));

                } catch (Exception e) {
                    toastMsg.showToast(TransactionSummary.this, "Not Software Match to Open Print File");
                    supporter.simpleNavigateTo(MainMenu.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                toastMsg.showToast(TransactionSummary.this,
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

                Toast.makeText(TransactionSummary.this,
                        "Connection : Unknown Printer Language",
                        Toast.LENGTH_LONG).show();
                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                Toast.makeText(TransactionSummary.this,
                        "Language : Unknown Printer Language",
                        Toast.LENGTH_LONG).show();
                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            }
        }

        return printer;
    }

    public void doPrintErrorAlert() {
        AlertDialog.Builder alertMemory = new AlertDialog.Builder(
                TransactionSummary.this);
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
        byte[] title = getConfigTitle();
        try {
            zebraPrinterConnection.write(title);
        } catch (ZebraPrinterConnectionException e) {
            Log.e("ZebraPrinterConnectionException", e.toString());
        }

    }

    private void writeDetail() {
        if (printerModel.equals("3-inch")) {
            y += 25;
            writeText(prntCompName, 14, y);
            y += 25;
            writeText(prntCompAddrs, 14, y);
            y += 25;
            writeText(prntCompState, 14, y);
            y += 25;
            writeText(prntCompCountry, 14, y);
            y += 25;
            writeText("Phone No. " + prntCompPhone, 14, y);
            y += 25;
            writeText("", 12, y);
            y += 25;
            writeText("Salesperson : " + prntSpname, 14, y);
            writeText(prntDate, 390, y);

            y += 25;
            writeText("", 12, y);

        } else if (printerModel.equals("4-inch")) {

            y += 32;
            writeText(prntCompName, 24, y);
            y += 32;
            writeText(prntCompAddrs, 24, y);
            y += 32;
            writeText(prntCompState, 24, y);
            y += 32;
            writeText(prntCompCountry, 24, y);
            y += 32;
            writeText("Phone No. " + prntCompPhone, 24, y);

            y += 32;
            writeText("", 24, y);
            y += 32;
            writeText("Salesperson : " + prntSpname, 24, y);
            writeText(prntDate, 560, y);

            y += 32;
            writeText("", 24, y);

        }
    }

    private void writeTableTitleDetail() {
        if (printerModel.equals("3-inch")) {
            y += 25;
            writeText("SN", 12, y);
            writeText("Date", 64, y);
            writeText("Order Ref.No.", 180, y);
            writeText("Cus. No.", 306, y);
            writeText("Tot Qty.", 418, y);
            writeText("Net Amt", 490, y);
        } else if (printerModel.equals("4-inch")) {
            y += 32;
            writeText("S.No.", 24, y);
            writeText("Date", 92, y);
            writeText("Order Ref.No.", 240, y);
            writeText("Cus. No.", 432, y);
            writeText("Qty.", 560, y);
            writeText("Net Amt", 664, y);
        }
    }

    private void writeLine() {
        if (printerModel.equals("3-inch")) {
            y += 15;
            writeText("----", 12, y);
            writeText("----------", 64, y);
            writeText("-------------", 180, y);
            writeText("-----------", 306, y);
            writeText("--------", 418, y);
            writeText("----------", 490, y);
        } else if (printerModel.equals("4-inch")) {
            y += 24;
            writeLines("------", 24, y);
            writeLines("-------------", 92, y);
            writeLines("-----------------------", 240, y);
            writeLines("---------------", 432, y);
            writeLines("--------", 560, y);
            writeLines("--------------", 664, y);
        }

    }

    private void writeItemDetail() {
        // PrinterLanguage printerLanguage =
        // printer.getPrinterControlLanguage();

        byte[] configLabel1 = null;
        StringBuffer strBuff = new StringBuffer();

        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
                    .getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {

            if (printerModel.equals("3-inch")) {
                xSno = 14;
                xDate = 64;
                xOrdNo = 165;
                xCusNo = 306;
            } else if (printerModel.equals("4-inch")) {
                xSno = 32;
                xDate = 92;
                xOrdNo = 240;
                xCusNo = 456;
                xQty = 576;
            }

            int serialPosition = 0;
            int exp = 0;
            int finaly = y;
            sNo = 1;
            totAmt = 0.00;

            for (int i = 0; i < tranSummaryList.size(); i++) {
                y = finaly + exp;
                y += 25;
                serialPosition += y;

                day = tranSummaryList.get(i).getHhTran_transDay();
                month = tranSummaryList.get(i).getHhTran_transMonth();
                year = tranSummaryList.get(i).getHhTran_transYear();
                prntDate1 = supporter.getStringDate(year, month, day);

                prntOrdNo = tranSummaryList.get(i).getHhTran_referenceNumber();
                dbhelpher.openReadableDatabase();
                transAllList = dbhelpher.getTransactionList(prntOrdNo, cmpnyNo);
                dbhelpher.closeDatabase();

                qty = 0;
                double prz = 0.00;
                for (int j = 0; j < transAllList.size(); j++) {
                    HhTran01 tran = transAllList.get(j);
                    tempQty = tran.getHhTran_qty();
                    prntCusNo = tran.getHhTran_customerNumber();
                    qty = qty + tempQty;
                    double tempPrz = tran.getHhTran_extPrice();
                    String type = tran.getHhTran_transType();
                    if (type.equals("CN")) {
                        prz = prz - tempPrz;
                    } else {
                        prz = prz + tempPrz;
                    }
                }

                prntTotQty = "" + qty;
                prntNetAmt = formats.format(prz);

                // for itemQty printing...
                appendLineToPrint(strBuff, "" + sNo, xSno, y);

                // for itemNo printing...
                if (prntDate1.length() <= 10) {
                    appendLineToPrint(strBuff, prntDate1, xDate, y);
                    finaly = y;
                } else {
                    int len1 = 0;
                    String tempstr = "";
                    int tempy = 0;
                    Boolean chk = false;

                    tempy = y;
                    tempstr = prntDate1;
                    while (chk == false) {
                        if (tempstr.length() > 10) {
                            appendLineToPrint(strBuff,
                                    tempstr.substring(0, 10), xDate, y);
                            len1 = tempstr.length();
                            if (len1 > 20) {
                                len1 = 10;
                            } else {
                                len1 = tempstr.length() - 10;
                            }

                            tempstr = tempstr.substring(10, 10 + len1);
                            y += 25;
                        } else {
                            appendLineToPrint(strBuff, tempstr, xDate, y);
                            chk = true;
                        }
                    }
                    finaly = y;
                    y = tempy;
                }

                // for description printing...
                if (prntOrdNo.length() <= 15) {
                    appendLineToPrint(strBuff, prntOrdNo, xOrdNo, y);
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
                    tempstr = prntOrdNo;
                    while (chk == false) {
                        if (tempstr.length() > 15) {
                            appendLineToPrint(strBuff,
                                    tempstr.substring(0, 15), xOrdNo, y);
                            len1 = tempstr.length();
                            if (len1 > 30) {
                                len1 = 15;
                            } else {
                                len1 = tempstr.length() - 15;
                            }

                            tempstr = tempstr.substring(15, 15 + len1);
                            y += 25;
                        } else {
                            appendLineToPrint(strBuff, tempstr, xOrdNo, y);
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
                    appendLineToPrint(strBuff, prntCusNo, xCusNo, y);
                    appendLineToPrint(strBuff, prntTotQty, 434, y);

                    int tempSize2 = prntNetAmt.length();
                    int tempPixSpace2 = tempSize2 * 8; // Each pixel have 8
                    // dots.
                    appendLineToPrint(strBuff, prntNetAmt,
                            488 + (80 - tempPixSpace2), y);
                } else if (printerModel.equals("4-inch")) {

                    appendLineToPrint(strBuff, prntCusNo, xCusNo, y);
                    appendLineToPrint(strBuff, prntTotQty, xQty, y);

                    int tempSize2 = prntNetAmt.length();
                    int tempPixSpace2 = tempSize2 * 8;

                    appendLineToPrint(strBuff, prntNetAmt,
                            664 + (80 - tempPixSpace2), y);
                }

                sNo++;
                totAmt = totAmt + prz;
            }
            if (printerModel.equals("3-inch")) {
                y += 25;
                appendLineToPrint(strBuff, "", 12, y);

                y += 25;
                appendLineToPrint(strBuff, "--------------------------", 360, y);
                y += 25;
                appendLineToPrint(strBuff, "Total :", 390, y);
                prntTotal = formats.format(totAmt);
                int tempSize1 = prntTotal.length();
                int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
                appendLineToPrint(strBuff, prntTotal,
                        488 + (80 - tempPixSpace1), y);
                y += 25;
                appendLineToPrint(strBuff, "--------------------------", 360, y);

                y += 25;
                appendLineToPrint(strBuff, "", 12, y);
                y += 25;
                appendLineToPrint(strBuff, "", 12, y);
                y += 25;
                appendLineToPrint(strBuff, "", 12, y);
            } else if (printerModel.equals("4-inch")) {
                y += 32;
                appendLineToPrint(strBuff, "", 24, y);

                y += 32;
                appendLineToPrint(strBuff, "------------------------", 432, y);
                y += 32;
                appendLineToPrint(strBuff, "Total :", 432, y);
                prntTotal = formats.format(totAmt);
                int tempSize1 = prntTotal.length();
                int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
                appendLineToPrint(strBuff, prntTotal,
                        664 + (80 - tempPixSpace1), y);
                y += 32;
                appendLineToPrint(strBuff, "------------------------", 432, y);

                y += 32;
                appendLineToPrint(strBuff, "", 24, y);
                y += 32;
                appendLineToPrint(strBuff, "", 24, y);
                y += 32;
                appendLineToPrint(strBuff, "", 24, y);
            }

            try {
                zebraPrinterConnection.write(strBuff.toString().getBytes());
            } catch (ZebraPrinterConnectionException e) {
                e.printStackTrace();
            }

        }

    }

    public boolean sendPDFDataToPrinter() {
        boolean resultSent = false;
        try {

            createfile();
            //document = new Document();
            document = new Document(new Rectangle(595, 842), 25, 25, 25, 25);
            //  document.setMargins(13, 3, 17, 20);
            // document.setMargins(0, 0, 0, 0);
            // document = new Document(new Rectangle(595 , 842 ), 0, 0, 0, 0);
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
            Paragraph childParagraph = new Paragraph("Order Summary Report", FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.add(childParagraph);
            addEmptyLine(paragraph, 2);
            // paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ZebraPrintConnException", e.toString());
        }

    }

    private void writePDFDetail() {
        try {
            Paragraph paragraph = new Paragraph();

            Paragraph childParagraph = new Paragraph(prntCompName, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

            childParagraph = new Paragraph(prntCompAddrs, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph(prntCompState, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph(prntCompCountry, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Phone No. " + prntCompPhone, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

            float[] columnWidthForTwoValues = {5.0f, 5.0f};
            Tablewithtwocoloum(paragraph, columnWidthForTwoValues, 100, "Salesperson : " + prntSpname, prntDate, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, Element.ALIGN_LEFT, Element.ALIGN_RIGHT, Rectangle.NO_BORDER, Rectangle.NO_BORDER);

            addEmptyLine(paragraph, 2);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void writePDFTableTitleDetail() {
        try {
            Paragraph paragraph = new Paragraph();
            //list all the products sold to the customer
            float[] columnWidths = {1.5f, 3f, 5f, 4f, 3f, 3f};
            //create PDF table with the given widths
            table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(100);


            cell = new PdfPCell(new Phrase("S.No.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //  cell.setBorder((Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT));
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Date", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Order Ref.No.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Cus. No.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Qty.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Net Amt", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writePDFItemDetail(Paragraph reportBody) {
        sNo = 1;
        totAmt = 0.00;
        for (int i = 0; i < tranSummaryList.size(); i++) {
            day = tranSummaryList.get(i).getHhTran_transDay();
            month = tranSummaryList.get(i).getHhTran_transMonth();
            year = tranSummaryList.get(i).getHhTran_transYear();
            prntDate1 = supporter.getStringDate(year, month, day);

            prntOrdNo = tranSummaryList.get(i).getHhTran_referenceNumber();
            dbhelpher.openReadableDatabase();
            transAllList = dbhelpher.getTransactionList(prntOrdNo, cmpnyNo);
            dbhelpher.closeDatabase();

            qty = 0;
            double prz = 0.00;
            for (int j = 0; j < transAllList.size(); j++) {
                HhTran01 tran = transAllList.get(j);
                tempQty = tran.getHhTran_qty();
                prntCusNo = tran.getHhTran_customerNumber();
                qty = qty + tempQty;
                double tempPrz = tran.getHhTran_extPrice();
                String type = tran.getHhTran_transType();
                if (type.equals("CN")) {
                    prz = prz - tempPrz;
                } else {
                    prz = prz + tempPrz;
                }
            }

            prntTotQty = "" + qty;
            prntNetAmt = formats.format(prz);

            // for sNo printing...
            cell = new PdfPCell(new Phrase("" + sNo, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            // for Date printing...
            cell = new PdfPCell(new Phrase(prntDate1, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            // for OrdNo printing...
            cell = new PdfPCell(new Phrase(prntOrdNo, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            // for CusNo printing...
            cell = new PdfPCell(new Phrase(prntCusNo, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            // for TotQty printing...
            cell = new PdfPCell(new Phrase(prntTotQty, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            // for NetAmt printing...
            cell = new PdfPCell(new Phrase(prntNetAmt, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            sNo++;
            totAmt = totAmt + prz;
        }
        reportBody.add(table);
    }


    private void writePDFTotal() {
        try {
            Paragraph paragraph = new Paragraph();
            addEmptyLine(paragraph, 2);
            prntTotal = formats.format(totAmt);
            Paragraph childParagraph = new Paragraph("Total :" + prntTotal, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
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


    private StringBuffer appendLineToPrint(StringBuffer strBuff,
                                           String strValue, int x, int y) {
        if (printerModel.equals("3-inch")) {
            String prntFormat = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
                    + "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
                    + strValue;

            strBuff = strBuff.append(prntFormat);
        } else if (printerModel.equals("4-inch")) {

            String prntFormat = "! U1 SETLP 5 0 24 " + "! U1 SETBOLD 0 "
                    + "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
                    + strValue;

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
        String title = "Order Summary Report";
        // PrinterLanguage printerLanguage =
        // printer.getPrinterControlLanguage();

        byte[] configLabel1 = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel1 = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ"
                    .getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            if (printerModel.equals("3-inch")) {
                String cpclConfigLabel1 = "! 0 200 200 40 1"
                        + "ON-FEED IGNORE\r\n" + "CENTER\r\n" + "T 5 0 10 15"
                        + " " + title + "\r\n" + "PRINT\r\n";
                configLabel1 = cpclConfigLabel1.getBytes();
            } else if (printerModel.equals("4-inch")) {
                String cpclConfigLabel1 = "! 0 200 200 60 1"
                        + "ON-FEED IGNORE\r\n" + "CENTER\r\n" + "T 5 2 10 15"
                        + " " + title + "\r\n" + "PRINT\r\n";
                configLabel1 = cpclConfigLabel1.getBytes();
            }

        }
        return configLabel1;
    }

    public void disconnect() {
        try {
            if (zebraPrinterConnection != null) {
                zebraPrinterConnection.close();
            }
        } catch (ZebraPrinterConnectionException e) {
            Toast.makeText(TransactionSummary.this, "COMM Error! Disconnected",
                    Toast.LENGTH_LONG).show();
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

    private void listLoaded() {

        tranSummaryList.clear();
        dbhelpher.openReadableDatabase();
        tranSummaryList = dbhelpher.getTransactionSummaryDatas(fromDay,
                fromMonth + 1, fromYear, toDay, toMonth + 1, toYear, cmpnyNo);
        dbhelpher.closeDatabase();

        int listSize = tranSummaryList.size();
        if (listSize == 0) {
            adptTranSummary.clear();
            adptTranSummary.notifyDataSetChanged();
            toastMsg.showToast(TransactionSummary.this, "Data not available..");
            summaryTotal.setText(supporter.getCurrencyFormat(0.00));
        } else {
            adptTranSummary.clear();

            // to refresh list items in listview
            adptTranSummary = new TransactionSummaryAdapter(
                    TransactionSummary.this, tranSummaryList);
            tranSummaryListView.setAdapter(adptTranSummary);
            tranSummaryListView.setSelectionAfterHeaderView();

            String sumTotPrz = supporter
                    .getCurrencyFormat(getSummaryTotal(tranSummaryList));
            summaryTotal.setText(sumTotPrz);
        }
    }

    private double getSummaryTotal(List<HhTran01> tranSummaryList2) {

        double summaryTotal = 0.00;

        for (int i = 0; i < tranSummaryList2.size(); i++) {

            HhTran01 tran = tranSummaryList2.get(i);
            String refNum = tran.getHhTran_referenceNumber();

            dbhelpher.openReadableDatabase();
            summaryTotal = summaryTotal
                    + dbhelpher.getSummaryTotal(refNum, cmpnyNo);
            dbhelpher.closeDatabase();

        }

        return summaryTotal;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

}