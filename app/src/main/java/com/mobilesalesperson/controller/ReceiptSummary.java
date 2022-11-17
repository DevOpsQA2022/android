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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.model.HhSetting;
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

public class ReceiptSummary extends AppBaseActivity {

    /* Variable declarations */
    private Supporter supporter;
    private MspDBHelper dbhelpher;
    private TextView spName;
    private TextView currency;
    private TextView reciptSummaryTotal;
    private EditText fromDate;
    private EditText toDate;
    private Button loadReciptSummary;
    private List<HhReceipt01> reciptSummaryList;
    private ListView receiptSummaryListView;
    private ArrayAdapter<HhReceipt01> adptReceiptSummary;
    private String sumTotPrz;
    private int fromYear, toYear;
    private int fromMonth, toMonth;
    private int fromDay, toDay;
    private double sumTotal;
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

    private String prntSpname;
    private String prntCompName;
    private String prntCompAddrs;
    private String prntCompState;
    private String prntCompCountry;
    private String prntCompPhone;
    private String prntFromDate;
    private String prntToDate;
    private int prntSNo;
    private String prntCusNo;
    private String prntReciptNo;
    private String prntAmount;
    private String prntTotAmt;

    private HhSetting setting;
    private int noOfPrint;
    private String printerModel;
    private int xSno;
    private int xCusNo;
    private int xReciptNo;
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
        setContentView(R.layout.receipt_summary_layout);

        registerBaseActivityReceiver();

        dbhelpher = new MspDBHelper(this);
        supporter = new Supporter(this, dbhelpher);
        toastMsg = new ToastMessage();

        spName = (TextView) findViewById(R.id.txt_salesPersonNameRecipt);
        currency = (TextView) findViewById(R.id.txt_ReciptSummaryCurrency);
        reciptSummaryTotal = (TextView) findViewById(R.id.txt_reciptSummaryTotal);
        fromDate = (EditText) findViewById(R.id.edt_ReciptSummaryFromDate);
        toDate = (EditText) findViewById(R.id.edt_ReciptSummaryToDate);
        loadReciptSummary = (Button) findViewById(R.id.btn_loadReciptsummary);
        receiptSummaryListView = (ListView) findViewById(R.id.lst_ReceiptSummaryItems);

        currentDate();

        reciptSummaryList = new ArrayList<HhReceipt01>();

        // Receipt Summary Custom adapter loading datas
        adptReceiptSummary = new ReceiptSummaryAdapter(ReceiptSummary.this,
                reciptSummaryList);
        receiptSummaryListView.setAdapter(adptReceiptSummary);

        // to load current day summary data
        fromDate.setText(supporter.getStringDate(fromYear, fromMonth + 1,
                fromDay));
        toDate.setText(supporter.getStringDate(toYear, toMonth + 1, toDay));

        loadReceiptData();

        String salespersonName = supporter.getSalesPerson();
        dbhelpher.openReadableDatabase();
        HhManager manager = dbhelpher.getManagerData(salespersonName,
                supporter.getCompanyNo());
        dbhelpher.closeDatabase();

        spName.setText(manager.getHhManager_username());
        currency.setText(manager.getHhManager_currency());

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

        loadReciptSummary.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int currentMonth = c.get(Calendar.MONTH);
                int currentDay = c.get(Calendar.DAY_OF_MONTH);

                if ((fromYear > currentYear) || (toYear > currentYear)) {
                    reciptSummaryList.clear();
                    adptReceiptSummary.clear();
                    adptReceiptSummary.notifyDataSetChanged();
                    reciptSummaryTotal.setText(supporter
                            .getCurrencyFormat(0.00));
                    toastMsg.showToast(ReceiptSummary.this,
                            "Enter valid from and to date");
                } else if (fromYear > toYear) {
                    reciptSummaryList.clear();
                    adptReceiptSummary.clear();
                    adptReceiptSummary.notifyDataSetChanged();
                    reciptSummaryTotal.setText(supporter
                            .getCurrencyFormat(0.00));
                    toastMsg.showToast(ReceiptSummary.this,
                            "Enter valid from and to date");
                } else if ((fromYear == toYear) && (fromMonth > toMonth)) {
                    reciptSummaryList.clear();
                    adptReceiptSummary.clear();
                    adptReceiptSummary.notifyDataSetChanged();
                    reciptSummaryTotal.setText(supporter
                            .getCurrencyFormat(0.00));
                    toastMsg.showToast(ReceiptSummary.this,
                            "Enter valid from and to date");
                } else if ((fromYear == toYear) && (fromDay > toDay)) {
                    reciptSummaryList.clear();
                    adptReceiptSummary.clear();
                    adptReceiptSummary.notifyDataSetChanged();
                    reciptSummaryTotal.setText(supporter
                            .getCurrencyFormat(0.00));
                    toastMsg.showToast(ReceiptSummary.this,
                            "Enter valid from and to date");
                } else if (((fromMonth > currentMonth) || (toMonth > currentMonth))
                        && (fromYear == toYear)) {
                    reciptSummaryList.clear();
                    adptReceiptSummary.clear();
                    adptReceiptSummary.notifyDataSetChanged();
                    reciptSummaryTotal.setText(supporter
                            .getCurrencyFormat(0.00));
                    toastMsg.showToast(ReceiptSummary.this,
                            "Enter valid from and to date");
                } else if (((fromDay > currentDay) || (toDay > currentDay))
                        && (fromYear == toYear)) {
                    reciptSummaryList.clear();
                    adptReceiptSummary.clear();
                    adptReceiptSummary.notifyDataSetChanged();
                    reciptSummaryTotal.setText(supporter
                            .getCurrencyFormat(0.00));
                    toastMsg.showToast(ReceiptSummary.this,
                            "Enter valid from and to date");
                } else {
                    loadReceiptData();
                }
            }
        });

		/*
         * This Section used for printing purpose.. Developed by T.Saravanan
		 */

        formats = new DecimalFormat("0.00");

        // printing values...
        String cmpNo = supporter.getCompanyNo();
        dbhelpher.openReadableDatabase();
        HhCompany company = dbhelpher.getCompanyData(cmpNo);
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
        prntFromDate = fromDate.getText().toString();
        prntToDate = toDate.getText().toString();
        // end of oncreate...
    }

    private void loadReceiptData() {

        reciptSummaryList.clear();
        dbhelpher.openReadableDatabase();
        reciptSummaryList = dbhelpher.getReceiptSummaryDatas(fromDay,
                fromMonth + 1, fromYear, toDay, toMonth + 1, toYear,
                supporter.getCompanyNo());
        dbhelpher.closeDatabase();

        int listSize = reciptSummaryList.size();
        if (listSize == 0) {
            adptReceiptSummary.clear();
            adptReceiptSummary.notifyDataSetChanged();
            toastMsg.showToast(ReceiptSummary.this, "Data not available..");
            reciptSummaryTotal.setText(supporter.getCurrencyFormat(0.00));
        } else {
            sumTotal = 0.00;

            adptReceiptSummary.clear();

            // to refresh list items in listview
            adptReceiptSummary = new ReceiptSummaryAdapter(ReceiptSummary.this,
                    reciptSummaryList);
            receiptSummaryListView.setAdapter(adptReceiptSummary);
            receiptSummaryListView.setSelectionAfterHeaderView();

            for (int i = 0; i < listSize; i++) {
                sumTotal = sumTotal
                        + reciptSummaryList.get(i).getHhReceipt_amount();
            }

            sumTotPrz = supporter.getCurrencyFormat(sumTotal);
            reciptSummaryTotal.setText(sumTotPrz);
            prntTotAmt = sumTotPrz;
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

			/*deviceList = supporter.getPairedDevices();

			if (deviceList.size() != 0) {
				showDialog(MENU_PRINT_DIALOG_ID);
			} else {
				toastMsg.showToast(ReceiptSummary.this,
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
			dialog = new Dialog(ReceiptSummary.this);
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
			devAdapter = new DeviceArrayAdapter(ReceiptSummary.this, deviceList);
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
            dialog = new ProgressDialog(ReceiptSummary.this);
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
            dialog = new ProgressDialog(ReceiptSummary.this);
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
                toastMsg.showToast(ReceiptSummary.this,
                        toasttext);
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(pdfFile), "application/pdf"));

                } catch (Exception e) {
                    toastMsg.showToast(ReceiptSummary.this, "Not Software Match to Open Print File");
                    supporter.simpleNavigateTo(MainMenu.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                toastMsg.showToast(ReceiptSummary.this,
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

    public void doPrintErrorAlert() {
        AlertDialog.Builder alertMemory = new AlertDialog.Builder(
                ReceiptSummary.this);
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

    // printer disconnecting method...
    public void disconnect() {
        try {
            if (zebraPrinterConnection != null) {
                zebraPrinterConnection.close();
            }
        } catch (ZebraPrinterConnectionException e) {
            showToast("COMM Error! Disconnected.");
        }
    }

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
                        + "ON-FEED IGNORE\r\n" + "CENTER\r\n"
                        + "T 5 0 10 15 RECEIPT SUMMARY REPORT\r\n"
                        + "PRINT\r\n";
                configLabel1 = cpclConfigLabel1.getBytes();
            } else if (printerModel.equals("4-inch")) {
                String cpclConfigLabel1 = "! 0 200 200 60 1"
                        + "ON-FEED IGNORE\r\n" + "CENTER\r\n"
                        + "T 5 2 10 15 RECEIPT SUMMARY REPORT\r\n"
                        + "PRINT\r\n";
                configLabel1 = cpclConfigLabel1.getBytes();
            }

        }
        return configLabel1;
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
            writeText("From Date : " + prntFromDate, 14, y);
            writeText("To Date : " + prntToDate, 370, y);

            y += 25;
            writeText("", 12, y);
            y += 25;
            writeText("Salesperson : " + prntSpname, 14, y);
            y += 25;
            writeText("", 12, y);
        } else if (printerModel.equals("4-inch")) {
            y += 32;
            writeText(prntCompName, 40, y);
            y += 32;
            writeText(prntCompAddrs, 40, y);
            y += 32;
            writeText(prntCompState, 40, y);
            y += 32;
            writeText(prntCompCountry, 40, y);
            y += 32;
            writeText("Phone No. " + prntCompPhone, 40, y);
            y += 32;
            writeText("", 12, y);
            y += 32;
            writeText("From Date : " + prntFromDate, 40, y);
            writeText("To Date : " + prntToDate, 560, y);

            y += 32;
            writeText("", 24, y);
            y += 32;
            writeText("Salesperson : " + prntSpname, 40, y);
            y += 32;
            writeText("", 24, y);
        }

    }

    private void writeTableTitleDetail() {
        if (printerModel.equals("3-inch")) {
            y += 25;
            writeText("SNo.", 12, y);
            writeText("Customer No.", 64, y);
            writeText("Receipt No.", 266, y);
            writeText("Amount", 448, y);
        } else if (printerModel.equals("4-inch")) {
            y += 25;
            writeText("SNo.", 40, y);
            writeText("Customer No.", 132, y);
            writeText("Receipt No.", 414, y);
            writeText("Amount", 624, y);
        }

    }

    private void writeLine() {
        if (printerModel.equals("3-inch")) {
            y += 15;
            writeText("----", 12, y);
            writeText("---------------", 64, y);
            writeText("---------------", 266, y);
            writeText("---------------", 424, y);
        } else if (printerModel.equals("4-inch")) {
            y += 24;
            writeLines("------", 40, y);
            writeLines("---------------------", 132, y);
            writeLines("-----------------", 414, y);
            writeLines("------------------", 624, y);
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
                xSno = 12;
                xCusNo = 64;
                xReciptNo = 266;
                xAmt = 490;
            } else if (printerModel.equals("4-inch")) {
                xSno = 48;
                xCusNo = 132;
                xReciptNo = 414;
                xAmt = 624;
            }

            int exp = 0;
            int finaly = y;

            for (int i = 0; i < reciptSummaryList.size(); i++) {

                y = finaly + exp;
                y += 25;
                System.out.println(reciptSummaryList.get(i));

                HhReceipt01 recipt = reciptSummaryList.get(i);

                prntCusNo = recipt.getHhReceipt_customernumber();
                prntReciptNo = recipt.getHhReceipt_receiptnumber();
                prntAmount = supporter.getCurrencyFormat(recipt
                        .getHhReceipt_amount());

                int pos = i + 1;
                String strPos = pos + "";

                // adding sno in string buffer...
                appendLineToPrint(strBuff, strPos, xSno, y);

                if (prntCusNo.length() <= 20) {
                    appendLineToPrint(strBuff, prntCusNo, xCusNo, y);
                    finaly = y;
                } else {
                    int len1 = 0;
                    String tempstr = "";
                    int tempy = 0;
                    Boolean chk = false;

                    tempy = y;
                    tempstr = prntCusNo;
                    while (chk == false) {
                        if (tempstr.length() > 20) {
                            // writeText(tempstr.substring(0, 14), 64, y);
                            appendLineToPrint(strBuff,
                                    tempstr.substring(0, 19), xCusNo, y);
                            len1 = tempstr.length();
                            if (len1 > 40) {
                                len1 = 20;
                            } else {
                                len1 = tempstr.length() - 20;
                            }

                            tempstr = tempstr.substring(20, 20 + len1);
                            y += 25;
                        } else {
                            // writeText(tempstr, 64, y);
                            appendLineToPrint(strBuff, tempstr, xCusNo, y);
                            chk = true;
                        }
                    }
                    finaly = y;
                    y = tempy;

                }

                appendLineToPrint(strBuff, prntReciptNo, xReciptNo, y);

                if (devName.equals("Zebra MZ 320")) {
                    int tempSize2 = prntAmount.length();
                    int tempPixSpace2 = tempSize2 * 8; // Each pixel have 8
                    // dots.
                    appendLineToPrint(strBuff, prntAmount,
                            424 + (80 - tempPixSpace2), y);
                } else if (devName.equals("Printer23")) {
                    int tempSize2 = prntAmount.length();
                    int tempPixSpace2 = tempSize2 * 8;
                    appendLineToPrint(strBuff, prntAmount,
                            624 + (80 - tempPixSpace2), y);
                }

            }
            if (printerModel.equals("3-inch")) {
                y += 25;
                appendLineToPrint(strBuff, "", 12, y);

                y += 25;
                appendLineToPrint(strBuff, "-------------------------", 360, y);

                y += 25;
                appendLineToPrint(strBuff, "Total :", 340, y);

                int tempSize2 = prntAmount.length();
                int tempPixSpace2 = tempSize2 * 8;
                appendLineToPrint(strBuff, prntTotAmt,
                        424 + (80 - tempPixSpace2), y);

                y += 25;
                appendLineToPrint(strBuff, "-------------------------", 360, y);

                y += 25;
                appendLineToPrint(strBuff, "", 12, y);

                y += 25;
                appendLineToPrint(strBuff, "", 12, y);
            } else if (printerModel.equals("4-inch")) {
                y += 32;
                appendLineToPrint(strBuff, "", 24, y);

                y += 32;
                appendLineToPrint(strBuff, "------------------------", 398, y);

                y += 32;
                appendLineToPrint(strBuff, "Total Amount :", 406, y);

                int tempSize1 = prntTotAmt.length();
                int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
                appendLineToPrint(strBuff, prntTotAmt,
                        624 + (80 - tempPixSpace1), y);

                y += 32;
                appendLineToPrint(strBuff, "------------------------", 398, y);

                y += 32;
                appendLineToPrint(strBuff, "", 24, y);

                y += 25;
                appendLineToPrint(strBuff, "", 24, y);
            }

            try {
                zebraPrinterConnection.write(strBuff.toString().getBytes());
            } catch (ZebraPrinterConnectionException e) {
                e.printStackTrace();
            }

        }

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

    // Toast is here...
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean sendPDFDataToPrinter() {
        boolean resultSent = false;
        try {

            createfile();
            //document = new Document();
             document = new Document(new Rectangle(595 , 842 ), 25, 25, 25, 25);
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
            Paragraph childParagraph = new Paragraph("RECEIPT SUMMARY REPORT", FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.add(childParagraph);


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
            if (!prntCompName.equals("")) {
                Paragraph childParagraph = new Paragraph(prntCompName, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.add(childParagraph);
            }
            if (!prntCompAddrs.equals("")) {
                Paragraph childParagraph = new Paragraph(prntCompAddrs, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.add(childParagraph);
            }
            if (!prntCompState.equals("")) {
                Paragraph childParagraph = new Paragraph(prntCompState, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.add(childParagraph);
            }
            if (!prntCompCountry.equals("")) {
                Paragraph childParagraph = new Paragraph(prntCompCountry, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.add(childParagraph);
            }
            Paragraph childParagraph = new Paragraph("Phone No. " + prntCompPhone, FONT_TITLE_SMALL_LETTERS); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

            addEmptyLine(paragraph, 1);
            float[] columnWidthForTwoValues = {5.0f, 5.0f};
            Tablewithtwocoloum(paragraph, columnWidthForTwoValues, 100, "From Date : " + prntFromDate, "To Date : " + prntToDate, FONT_TITLE_SMALL_LETTERS, FONT_TITLE_SMALL_LETTERS, Element.ALIGN_LEFT, Element.ALIGN_RIGHT, Rectangle.NO_BORDER, Rectangle.NO_BORDER);
            addEmptyLine(paragraph, 1);
            childParagraph = new Paragraph("Salesperson : " + prntSpname, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);

            addEmptyLine(paragraph, 1);
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
            float[] columnWidths = {2f, 3f, 7f, 4f};
            //create PDF table with the given widths
            table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(100);


            cell = new PdfPCell(new Phrase("SNo.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //  cell.setBorder((Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT));
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Customer No.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Receipt No.", FONT_TABLE_TITLE));
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



      for (int i = 0; i < reciptSummaryList.size(); i++) {


           HhReceipt01 recipt = reciptSummaryList.get(i);

                prntCusNo = recipt.getHhReceipt_customernumber();
                prntReciptNo = recipt.getHhReceipt_receiptnumber();
                prntAmount = supporter.getCurrencyFormat(recipt
                        .getHhReceipt_amount());

                int pos = i + 1;
                String strPos = pos + "";



            // for serial no printing...
            cell = new PdfPCell(new Phrase( strPos, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(prntCusNo, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(prntReciptNo, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(prntAmount, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

           /* cell = new PdfPCell(new Phrase("" + applyAmt, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);*/
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

}
