package com.mobilesalesperson.controller;

import android.app.AlertDialog;
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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.HhManager;
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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author T.SARAVANAN class to Showing location based item list...
 */

public class Inventory extends AppBaseActivity {

    /**
     * variable declarations
     */
    private Supporter supporter;
    private MspDBHelper dbHelper;
    private Spinner spnLocation;
    private List<String> locList;
    private List<HhItem01> itemList;
    private ArrayAdapter<HhItem01> itemListAdapter;
    private ArrayAdapter<String> adptLoctionSpn;
    private String locSpnData;
    private ListView lstViewItem;
    private ToastMessage toastMsg;

    // printing variables
    private ZebraPrinterConnection zebraPrinterConnection;
    private ZebraPrinter printer;
    private int y;
    static final int MENU_PRINT_DIALOG_ID = 0;
    private Dialog dialog;
    private ListView blListView;
    private List<Devices> deviceList;
    private ArrayAdapter<Devices> devAdapter;
    private String devName;
    private String devAddress;
    private HhSetting setting;
    private int noOfPrint;
    private String printerModel;
    private String prntCompName;
    private String prntSpname;
    private String str;
    private Format format1;
    private Date dateNow;
    private String prntDate;
    private int sNo;
    private String prntQty;
    private String prntItemNo;
    private String prntDesc;
    private String prntTotQty;
    private double totQty;
    private DecimalFormat formats;
    private String cmpnyNo; // added for multicompany

    private int xSno;
    private int xItemNo;
    private int xDesc;
    private int xQty;

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
    String filename = "msp_inventory.pdf";
    String toasttext = "";

    private PrinterLanguage printerLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.inventory_report_layout);

        registerBaseActivityReceiver();

        dbHelper = new MspDBHelper(this);
        supporter = new Supporter(this, dbHelper);
        toastMsg = new ToastMessage();

        spnLocation = (Spinner) findViewById(R.id.spn_inventoryLoc);
        lstViewItem = (ListView) findViewById(R.id.lst_InventoryItems);
        cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013

        // spinner events
        dbHelper.openReadableDatabase();
        locList = dbHelper.getAvailableLocation(cmpnyNo);
        dbHelper.closeDatabase();
        adptLoctionSpn = new ArrayAdapter<String>(Inventory.this,
                android.R.layout.simple_dropdown_item_1line, locList);
        // adptLoctionSpn.setDropDownViewResource(R.layout.spinner_item_layout);
        spnLocation.setAdapter(adptLoctionSpn);

        locSpnData = (String) spnLocation.getItemAtPosition(spnLocation
                .getSelectedItemPosition());

        // to get pricelist from manager table..
        dbHelper.openReadableDatabase();
        HhManager manager = dbHelper.getManagerData(supporter.getSalesPerson(),
                cmpnyNo);
        dbHelper.closeDatabase();
        final String priceList = manager.getHhManager_pricelistcode();

        // inventory item events
        dbHelper.openReadableDatabase();
        itemList = dbHelper
                .getInventoryItemDatas(itemList, locSpnData, cmpnyNo);
        dbHelper.closeDatabase();
        itemListAdapter = new InventoryItemListAdapter(Inventory.this,
                itemList, priceList, locSpnData, dbHelper);
        lstViewItem.setAdapter(itemListAdapter);

        spnLocation.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                locSpnData = (String) spnLocation.getItemAtPosition(spnLocation
                        .getSelectedItemPosition());
                dbHelper.openReadableDatabase();
                itemList = dbHelper.getInventoryItemDatas(itemList, locSpnData,
                        cmpnyNo);
                dbHelper.closeDatabase();

                // to refresh list items in listview
                itemListAdapter = new InventoryItemListAdapter(Inventory.this,
                        itemList, priceList, locSpnData, dbHelper);
                lstViewItem.setAdapter(itemListAdapter);
                lstViewItem.setSelectionAfterHeaderView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

		/*
         * This Section used for printing purpose.. Developed by T.Saravanan
		 */

        // paired device list

        formats = new DecimalFormat("0.00");

        dbHelper.openReadableDatabase();
        HhCompany company = dbHelper.getCompanyData(cmpnyNo);
        setting = dbHelper.getSettingData();
        dbHelper.closeDatabase();

        noOfPrint = setting.getHhSetting_numcopiesprint();
        printerModel = setting.getHhSetting_printerModel();
        prntCompName = company.getCompany_name();
        prntSpname = supporter.getSalesPerson();

        // getting date for printing ...
        dateNow = new Date();
        format1 = new SimpleDateFormat("MMM dd yyyy");
        str = format1.format(dateNow);
        prntDate = "Date : " + str;

    }

    /**
     * method for menu control
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.login, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_menu, menu);
        return true;
    }

    /**
     * method for menu control
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.inventory_print:

		/*	deviceList = supporter.getPairedDevices();

			if (deviceList.size() != 0) {
				showDialog(MENU_PRINT_DIALOG_ID);
			} else {
				toastMsg.showToast(Inventory.this,
						"Paired printer not available for printing");
			}*/
                showDialog(MENU_PRINT_DIALOG_ID);
                break;

        }
        return true;
    }

    /* Info Dialog creation */
    @Override
    protected Dialog onCreateDialog(int id) {

        dialog = null;

        switch (id) {
            case MENU_PRINT_DIALOG_ID:
                //</History> Suresh 05-Oct-2017 Added for Print model Select
						printerModel = setting.getHhSetting_printerModel();
						if(printerModel.equals("PDF") && printerModel != null && printerModel!=""){
					 new PrinterPDFConnectOperation().execute();
                break;
						}
						else {
			dialog = new Dialog(Inventory.this);
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
			devAdapter = new DeviceArrayAdapter(Inventory.this, deviceList);
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

        return dialog;
    }

    private class PrinterConnectOperation extends
            AsyncTask<Void, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(Inventory.this);
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
            dialog = new ProgressDialog(Inventory.this);
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
                toastMsg.showToast(Inventory.this,
                        toasttext);
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(pdfFile), "application/pdf"));

                } catch (Exception e) {
                    toastMsg.showToast(Inventory.this, "Not Software Match to Open Print File");
                    supporter.simpleNavigateTo(MainMenu.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                toastMsg.showToast(Inventory.this,
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
                Inventory.this);
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
                toastMsg.showToast(Inventory.this,
                        "Connection : Unknown Printer Language..");
                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                toastMsg.showToast(Inventory.this,
                        "Language : Unknown Printer Language..");
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
        byte[] title = getConfigTitle();
        try {
            zebraPrinterConnection.write(title);
        } catch (ZebraPrinterConnectionException e) {
            Log.e("ZebraPrinterConnectionException", e.toString());
        }

    }

    private void writeDetail() {
        if (printerModel.equals("3-inch")) {
            writeText("Company Name : " + prntCompName, 12, y);
            y += 25;
            writeText("Sales Person : " + prntSpname, 12, y);
            y += 25;
            writeText("Location : " + locSpnData, 12, y);
            y += 25;
            writeText(prntDate, 12, y);
            y += 25;
            writeText("", 12, y);
        } else if (printerModel.equals("4-inch")) {

            writeText("Company Name : " + prntCompName, 24, y);
            y += 32;
            writeText("Sales Person : " + prntSpname, 24, y);
            y += 32;
            writeText("Location : " + locSpnData, 24, y);
            y += 32;
            writeText(prntDate, 24, y);
            y += 32;
            writeText("", 24, y);
        }

    }

    private void writeTableTitleDetail() {
        if (printerModel.equals("3-inch")) {
            y += 25;
            writeText("SNo.", 12, y);
            writeText("Item No.", 64, y);
            writeText("Description", 165, y);
            writeText("Qty.", 430, y);
        } else if (printerModel.equals("4-inch")) {
            y += 32;
            writeText("SNo.", 24, y);
            writeText("Item No.", 116, y);
            writeText("Description", 272, y);
            writeText("Qty.", 664, y);
        }

    }

    private void writeLine() {
        if (printerModel.equals("3-inch")) {
            y += 15;
            writeText("----", 12, y);
            writeText("----------", 64, y);
            writeText("-------------", 165, y);
            writeText("----------", 430, y);
        } else if (printerModel.equals("4-inch")) {
            y += 24;
            writeLines("------", 24, y);
            writeLines("-------------", 116, y);
            writeLines("-----------------------", 272, y);
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
                xItemNo = 64;
                xDesc = 165;
                xQty = 306;
            } else if (printerModel.equals("4-inch")) {
                xSno = 32;
                xItemNo = 116;
                xDesc = 272;
                xQty = 576;
            }

            int serialPosition = 0;
            int exp = 0;
            int finaly = 0;
            sNo = 1;

            for (int i = 0; i < itemList.size(); i++) {
                y = finaly + exp;
                if (i != 0)
                    y += 25;
                serialPosition += y;

                HhItem01 item = itemList.get(i);

                prntItemNo = item.getHhItem_number();
                prntDesc = item.getHhItem_description();
                double tempQty = item.getHhItem_qty_on_hand();
                prntQty = formats.format(tempQty);
                totQty = totQty + tempQty;

                // for serial no printing...
                appendLineToPrint(strBuff, "" + sNo, xSno, y);

                // for itemNo printing...
                if (prntItemNo.length() <= 8) {
                    appendLineToPrint(strBuff, prntItemNo, xItemNo, y);
                    finaly = y;
                } else {
                    int len1 = 0;
                    String tempstr = "";
                    int tempy = 0;
                    Boolean chk = false;

                    tempy = y;
                    tempstr = prntItemNo;
                    while (chk == false) {
                        if (tempstr.length() > 8) {
                            appendLineToPrint(strBuff, tempstr.substring(0, 7),
                                    xItemNo, y);
                            len1 = tempstr.length();
                            if (len1 > 16) {
                                len1 = 7;
                            } else {
                                len1 = tempstr.length() - 8;
                            }

                            tempstr = tempstr.substring(8, 8 + len1);
                            y += 25;
                        } else {
                            appendLineToPrint(strBuff, tempstr, xItemNo, y);
                            chk = true;
                        }
                    }
                    finaly = y;
                    y = tempy;
                }

                // for description printing...
                if (prntDesc.length() <= 30) {
                    appendLineToPrint(strBuff, prntDesc, xDesc, y);
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
                    tempstr = prntDesc;
                    while (chk == false) {
                        if (tempstr.length() > 30) {
                            appendLineToPrint(strBuff,
                                    tempstr.substring(0, 30), xDesc, y);
                            len1 = tempstr.length();
                            if (len1 > 60) {
                                len1 = 30;
                            } else {
                                len1 = tempstr.length() - 30;
                            }

                            tempstr = tempstr.substring(30, 30 + len1);
                            y += 25;
                        } else {
                            appendLineToPrint(strBuff, tempstr, xDesc, y);
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
                    // for quantity on hand printing...
                    int tempSize1 = prntQty.length();
                    int tempPixSpace1 = tempSize1 * 8;
                    appendLineToPrint(strBuff, prntQty, 510 - tempPixSpace1, y);
                } else if (printerModel.equals("4-inch")) {
                    // for quantity on hand printing...
                    int tempSize1 = prntQty.length();
                    int tempPixSpace1 = tempSize1 * 8;
                    appendLineToPrint(strBuff, prntQty, 752 - tempPixSpace1, y);
                }

                sNo++;
            }

            if (printerModel.equals("3-inch")) {
                y += 25;
                appendLineToPrint(strBuff, "", 12, y);

                y += 25;
                appendLineToPrint(strBuff, "-------------------------------",
                        305, y);
                y += 25;
                appendLineToPrint(strBuff, "Total Qty :", 290, y);
                prntTotQty = formats.format(totQty);
                int tempSize1 = prntTotQty.length();
                int tempPixSpace1 = tempSize1 * 8; // Each pixel have 8 dots.
                appendLineToPrint(strBuff, prntTotQty, 510 - tempPixSpace1, y);
                y += 25;
                appendLineToPrint(strBuff, "-------------------------------",
                        305, y);

                y += 25;
                appendLineToPrint(strBuff, "", 12, y);
                y += 25;
                appendLineToPrint(strBuff, "", 12, y);

                String str = "! 0 200 200 " + y + " 1 \r\n" + "SETBOLD 1 \r\n";
                strBuff.insert(0, str);
                strBuff.append("PRINT\r\n\n");

            } else if (printerModel.equals("4-inch")) {
                y += 32;
                appendLineToPrint(strBuff, "", 24, y);

                y += 32;
                appendLineToPrint(strBuff, "------------------------", 432, y);

                y += 32;
                appendLineToPrint(strBuff, "Total Qty :", 432, y);
                prntTotQty = formats.format(totQty);
                int tempSize1 = prntTotQty.length();
                int tempPixSpace1 = tempSize1 * 8;
                appendLineToPrint(strBuff, prntTotQty, 752 - tempPixSpace1, y);
                y += 32;
                appendLineToPrint(strBuff, "------------------------", 432, y);

                y += 32;
                appendLineToPrint(strBuff, "", 24, y);
                y += 32;
                appendLineToPrint(strBuff, "", 24, y);

                String str = "! 0 200 200 " + y + " 1 \r\n";//+"SETBOLD 1 \r\n";
                strBuff.insert(0, str);
                strBuff.append("PRINT\r\n\n");

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

    private void writePDFHeader() {
        try {
            Paragraph paragraph = new Paragraph();

            Paragraph childParagraph = new Paragraph("Inventory Report", FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
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


            Paragraph childParagraph = new Paragraph("Company Name : " + prntCompName, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Sales Person : " + prntSpname, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph("Location : " + locSpnData, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(childParagraph);
            childParagraph = new Paragraph(prntDate, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
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
            float[] columnWidths = {2f, 4f, 6f, 3f};
            //create PDF table with the given widths
            table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(100);


            cell = new PdfPCell(new Phrase("SNo.", FONT_TABLE_TITLE));
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

            cell = new PdfPCell(new Phrase("Qty.", FONT_TABLE_TITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writePDFItemDetail(Paragraph reportBody) {
            int exp = 0;
            sNo = 1;
            for (int i = 0; i < itemList.size(); i++) {
                HhItem01 item = itemList.get(i);
                prntItemNo = item.getHhItem_number();
                prntDesc = item.getHhItem_description();
                double tempQty = item.getHhItem_qty_on_hand();
                prntQty = formats.format(tempQty);
                totQty = totQty + tempQty;

                // for serial no printing...
                cell = new PdfPCell(new Phrase(""+sNo, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                //cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                  // for itemNo printing...
                cell = new PdfPCell(new Phrase(prntItemNo, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // for description printing...
                cell = new PdfPCell(new Phrase(prntDesc, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // for quantity on hand printing...
                 cell = new PdfPCell(new Phrase(prntQty, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                sNo++;
            }
            reportBody.add(table);
        }
    private void writePDFTotal() {
        try {
            Paragraph paragraph = new Paragraph();
            addEmptyLine(paragraph, 2);
            prntTotQty = formats.format(totQty);
            Paragraph childParagraph = new Paragraph("Total Qty :" + prntTotQty, FONT_TITLE); //public static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 22,Font.BOLD);
            childParagraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.add(childParagraph);


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

    private StringBuffer appendLineToPrint(StringBuffer strBuff,
                                           String strValue, int x, int y) {
        if (printerModel.equals("3-inch")) {/*
			String prntFormat = "! U1 SETLP 0 2 24 " + "! U1 SETBOLD 1 "
					+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
					+ strValue;

			strBuff = strBuff.append(prntFormat);
		*/
            String prntFormat = "T 0 2 " + " " + x + " " + y + " "
                    + strValue + "\r\n";

            strBuff = strBuff.append(prntFormat);
        } else if (printerModel.equals("4-inch")) {/*
			String prntFormat = "! U1 SETLP 5 0 24 " + "! U1 SETBOLD 0 "
					+ "! U1 X" + " " + x + " " + "! U1 Y" + " " + y + " "
					+ strValue;

			strBuff = strBuff.append(prntFormat);
		*/
            String prntFormat = "T 5 0 " + " " + x + " " + y + " "
                    + strValue + "\r\n";

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
                String cpclConfigLabel1 = "! 0 200 200 40 1"
                        + "ON-FEED IGNORE\r\n" + "CENTER\r\n"
                        + "T 5 0 10 15 Inventory Report\r\n" + "PRINT\r\n";
                configLabel1 = cpclConfigLabel1.getBytes();
            } else if (printerModel.equals("4-inch")) {
                String cpclConfigLabel1 = "! 0 200 200 60 1"
                        + "ON-FEED IGNORE\r\n" + "CENTER\r\n"
                        + "T 5 2 10 15 Inventory Report\r\n" + "PRINT\r\n";
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
            toastMsg.showToast(Inventory.this, "COMM Error! Disconnected.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

}