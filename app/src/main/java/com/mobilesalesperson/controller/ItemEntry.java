package com.mobilesalesperson.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhContractPrice01;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.HhPrice01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhTran01;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

import org.w3c.dom.Text;

/**
 * @author TIVM class for item entry
 */
public class ItemEntry extends AppBaseActivity {

	/** variable declarations */
	private Supporter supporter;
	private MspDBHelper dbHelper;
	private HhSetting setting;
	private int deciValue;
	private int isStockItem; // added for nonstock item 19July2013
	private TextView txtItementryCusNm;
	private TextView txtItemEntNum;
	private TextView txtItemEntDesc;
	private TextView txtItemEntCurrency;
	private TextView txtItemEntPrcLst;
	private TextView txtSalesCommission;
	private EditText edtTxtItemEntQty;
	private TextView txtItemEntQtyHand;
	private EditText edtTxtItemEntUnitPrc;
	private Spinner spnItemEntUOM;
	private Spinner spnItemSalesCommission;
	private TextView txtItemEntBasePrc;
	private EditText edtTxtItemDiscount;
	private EditText edtTxt_Sales_Commission;
	private Spinner spnItemEntDisc;
	private TextView txtItemEntLoc;
	private TextView txtItemEntUOM;
	private EditText edtTxtItemEntShipDate;
	private ImageView btnIncrement;
	private ImageView btnDecrement;
	private Button btnItemEntryOk;
	private Button btnItemEntryToSale;
	private Button btnItemEntryToReturn;
	private SharedPreferences shippingDetails;
	private SharedPreferences orderDetails;
	private SharedPreferences entryDetails;
	private ToastMessage toastMsg;

	private HhItem01 item;
	private String mode;
	private String itemNum;
	private String orderNo;
	private boolean isEdit;
	private String prvUom;
	private String prvPrc;
	private String prvQty;
	private String prvDate;
	private String prvEntType;
	private String preDiscount;
	private String preDiscType;
	private String uom;
	private double uomConFact;
	private double currentItemPrice;
	private double currentDiscPrice;
	private double SalesCommission;
	private List<String> uomList;
	private List<String> listSelectDiscountContent;
	private List<String> listSalesCommision;
	private ArrayAdapter<String> adptUOMSpn;
	private ArrayAdapter<String> adptDiscSpn;
	private ArrayAdapter<String> adtSalesCommission;
	private String locId;
	private HhPrice01 priceDetail;
	private boolean isContPrcDiscAvail;
	private boolean isSalesPriceAvail;
	private HhManager managerData;
	private String cmpnyNo; // added for multicompany
	private double basePrc;
	private String currentItmPrz;
	private String unitPrz;
	private String saleEndDate;
	private String saleStartDate;
	private boolean toHandleSpnExeOnLoad;
	private SimpleDateFormat formatDate;
	private String totype;
	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID_SHIP = 0;

	private DatePickerDialog.OnDateSetListener shipDateListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			edtTxtItemEntShipDate.setText(supporter.getStringDate(mYear,
					mMonth + 1, mDay));

		}
	};


	/**
	 * method to initialize the class activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.item_entry_layout);

		registerBaseActivityReceiver();

		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);
		toastMsg = new ToastMessage();
       
		entryDetails = getSharedPreferences("entryDetails",
				Context.MODE_PRIVATE);
		
		shippingDetails = getSharedPreferences("shippingDetails",
				Context.MODE_PRIVATE);
		orderDetails = getSharedPreferences("orderFile",
				Context.MODE_PRIVATE);


		txtItementryCusNm = (TextView) findViewById(R.id.txt_ItementryCusNm);
		txtItemEntNum = (TextView) findViewById(R.id.txt_ItemEntNum);
		txtItemEntDesc = (TextView) findViewById(R.id.txt_ItemEntDesc);
		txtItemEntCurrency = (TextView) findViewById(R.id.txt_ItemEntCurrency);
		txtItemEntPrcLst = (TextView) findViewById(R.id.txt_ItemEntPrcLst);
		txtSalesCommission = (TextView)findViewById(R.id.txt_Sales_Commission);
		edtTxtItemEntQty = (EditText) findViewById(R.id.edtTxt_ItemEntQty);
		txtItemEntQtyHand = (TextView) findViewById(R.id.txt_ItemEntQtyHand);
		edtTxtItemEntUnitPrc = (EditText) findViewById(R.id.edtTxt_ItemEntUnitPrc);
		spnItemEntUOM = (Spinner) findViewById(R.id.spn_ItemEntUOM);
		txtItemEntBasePrc = (TextView) findViewById(R.id.txt_ItemEntBasePrc);
		edtTxtItemDiscount = (EditText) findViewById(R.id.edtTxt_ItemEntDiscount);
		spnItemEntDisc = (Spinner) findViewById(R.id.spn_ItemEntDisc);
		txtItemEntLoc = (TextView) findViewById(R.id.txt_ItemEntLoc);
		txtItemEntUOM = (TextView) findViewById(R.id.txt_ItemEntUOM);
		edtTxtItemEntShipDate = (EditText) findViewById(R.id.edtTxt_ItemEntShipDate);
		edtTxt_Sales_Commission = (EditText) findViewById(R.id.edtTxt_Sales_Commission);
		spnItemSalesCommission = (Spinner) findViewById(R.id.spn_Sales_Commission);

		btnIncrement = (ImageView) findViewById(R.id.btn_ItemEntInc);
		btnDecrement = (ImageView) findViewById(R.id.btn_ItemEntDec);

		btnItemEntryOk = (Button) findViewById(R.id.btn_ItemEntryOk);
		btnItemEntryToSale = (Button) findViewById(R.id.btn_ItemEntryToSale);
		btnItemEntryToReturn = (Button) findViewById(R.id.btn_ItemEntryToReturn);
		btnItemEntryOk.setEnabled(true);
		btnItemEntryToSale.setEnabled(true);
		btnItemEntryToReturn.setEnabled(true);

		toHandleSpnExeOnLoad = false;
		// For Richard Qoh coming wrong
        totype = supporter.getSaleType();

		mode = supporter.getMode();
		cmpnyNo = supporter.getCompanyNo(); // added jul 15/2013
		itemNum = getIntent().getStringExtra("itemNumber");
		locId = getIntent().getStringExtra("locId");
		isEdit = getIntent().getBooleanExtra("isEdit", false);
		orderNo=getIntent().getStringExtra("orderNumber");
		
		
		currentDiscPrice = 0.00;

		dbHelper.openWritableDatabase();
		setting = dbHelper.getSettingData();
		HhPrice01 price=new HhPrice01();
		
		dbHelper.closeDatabase();
		deciValue = Integer.parseInt(setting.getHhSetting_decimalformat());

		dbHelper.openReadableDatabase();
		managerData = dbHelper.getManagerData(supporter.getSalesPerson(),
				cmpnyNo);
		dbHelper.closeDatabase();

		int isEditable = managerData.getHhManager_unitpriceeditable();

		if (isEditable == 0) {
			edtTxtItemEntUnitPrc.setFocusable(false);
		}

		if (mode.equals("oe") || mode.equals("orderEdit")) {
			btnItemEntryToSale.setVisibility(View.GONE);
			btnItemEntryToReturn.setVisibility(View.GONE);
		} else if (mode.equals("ie") || mode.equals("invoiceEdit")) {
			btnItemEntryOk.setVisibility(View.GONE);
			//edtTxt_Sales_Commission.setVisibility(View.GONE);
			//txtSalesCommission.setVisibility(View.GONE);
			//spnItemSalesCommission.setVisibility(View.GONE);


			 // For Richard Qoh coming wrong
            if (!totype.equals("")) {
                if (totype.equals("tosale")) {
                    btnItemEntryToReturn.setVisibility(View.GONE);
                } else {
                    btnItemEntryToSale.setVisibility(View.GONE);
                }
            }

		}

		// to current date for date spinner
		
		currentDate();
		
			
		// to set current date
		dbHelper.openReadableDatabase();
		SimpleDateFormat df = supporter.getDateFormatter();
		dbHelper.closeDatabase();
		
		
		String shipDate = shippingDetails.getString("shipDate",
				df.format(new Date()));
	    
		if (shipDate.equals("")) {
			shipDate = df.format(new Date()).toString();
		} else {
			
			setValueToDatePicker(shipDate);
		}
	    
		
		String custPriceList = supporter.getPriceList();
		// to load uom spinner
		dbHelper.openReadableDatabase();
		HhCompany company=new HhCompany();
		company=dbHelper.getCompanyData(cmpnyNo);
		int mIsStockingUOM=company.getCompany_isstockinguom();
		price=dbHelper.getPrcData(custPriceList, itemNum, cmpnyNo);
		if(price==null){
			String custNum = shippingDetails.getString("customerNumber", "");

			dbHelper.openReadableDatabase();
			HhCustomer01 customer = dbHelper.getCustomerData(custNum, cmpnyNo);
			dbHelper.closeDatabase();
            
			txtItementryCusNm.setText(customer.getHhCustomer_name());
			txtItemEntNum.setText(itemNum);
			dbHelper.openReadableDatabase();
			item = dbHelper.getItemData(itemNum, locId, cmpnyNo);
			dbHelper.closeDatabase();
			txtItemEntDesc.setText(item.getHhItem_description());
			txtItemEntPrcLst.setText(custPriceList);
			txtItemEntCurrency.setText(item.getHhItem_currency());	
			edtTxtItemEntQty.setText("1");
			edtTxtItemEntQty.setSelection(edtTxtItemEntQty.getText().length());

			double quantity = getActualHandOnQty(itemNum,
					item.getHhItem_qty_on_hand(), locId, dbHelper, supporter);
			txtItemEntQtyHand.setText(quantity + "");
			txtItemEntLoc.setText(item.getHhItem_loc_id());
			edtTxtItemEntShipDate.setText(supporter.getStringDate(mYear,
					mMonth + 1, mDay));
			txtItemEntBasePrc.setText("0.00");
			edtTxtItemEntUnitPrc.setText("0.00");
			dbHelper.openReadableDatabase();
			String mDefaultUOM = dbHelper.getItemUom(itemNum, cmpnyNo);
			
			edtTxtItemDiscount.setText("0.00");
			edtTxtItemDiscount.setSelection(edtTxtItemDiscount.getText().length());
			listSelectDiscountContent = supporter.loadInDiscountStaticData();
			listSalesCommision = supporter.loadInDiscountStaticData();
			uomList = dbHelper.getUOM(itemNum, cmpnyNo, mDefaultUOM);
			dbHelper.closeDatabase();
			adptUOMSpn = new ArrayAdapter<String>(ItemEntry.this,
					android.R.layout.simple_dropdown_item_1line, uomList);
			// adptUOMSpn.setDropDownViewResource(R.layout.spinner_item_layout);

			spnItemEntUOM.setAdapter(adptUOMSpn);
			
			adptDiscSpn = new ArrayAdapter<String>(ItemEntry.this,
					android.R.layout.simple_dropdown_item_1line,
					listSelectDiscountContent);
			// adptDiscSpn.setDropDownViewResource(R.layout.spinner_item_layout);
			spnItemEntDisc.setAdapter(adptDiscSpn);

			//adapter add by Mathes for ListofSalesCommision
			adtSalesCommission = new ArrayAdapter<String>(ItemEntry.this,android.R.layout.simple_dropdown_item_1line,listSalesCommision);
			spnItemSalesCommission.setAdapter(adtSalesCommission);

			uom = (String) spnItemEntUOM.getItemAtPosition(spnItemEntUOM
					.getSelectedItemPosition());
			prvUom = getIntent().getStringExtra("eUom");
			int index = uomList.indexOf(prvUom);
			
			spnItemEntUOM.setSelection(index);
			spnItemEntUOM.setEnabled(true);
			txtItemEntUOM.setText(uom);
			
			
		}else{
		String mPricingUOM=price.getHhPrice_pricing_uom();
		String mDefaultUOM = dbHelper.getItemUom(itemNum, cmpnyNo);
		
		if(mIsStockingUOM==1)
			uomList=dbHelper.getUOM(itemNum, cmpnyNo, mPricingUOM);
		else
		{
		uomList = dbHelper.getUOM(itemNum, cmpnyNo, mDefaultUOM);
		}
		dbHelper.closeDatabase();
		
		adptUOMSpn = new ArrayAdapter<String>(ItemEntry.this,
				android.R.layout.simple_dropdown_item_1line, uomList);
		// adptUOMSpn.setDropDownViewResource(R.layout.spinner_item_layout);

		spnItemEntUOM.setAdapter(adptUOMSpn);

		/*
		 * for Richard White wants to set CS is the default in UOMso T.Saravanan
		 * implement one method for set CS in default field in UOM spinnerthe
		 * method name is setUOMDefault()
		 */
		setUomDefault();
		
		// Load static transfer data account
		listSelectDiscountContent = supporter.loadInDiscountStaticData();
		listSalesCommision = supporter.loadInDiscountStaticData();

		adptDiscSpn = new ArrayAdapter<String>(ItemEntry.this,
				android.R.layout.simple_dropdown_item_1line,
				listSelectDiscountContent);
		// adptDiscSpn.setDropDownViewResource(R.layout.spinner_item_layout);
		spnItemEntDisc.setAdapter(adptDiscSpn);

		adtSalesCommission = new ArrayAdapter<String>(ItemEntry.this,android.R.layout.simple_dropdown_item_1line,listSalesCommision);
		//spnItemSalesCommission.setAdapter(adtSalesCommission);

		String custNum = shippingDetails.getString("customerNumber", "");

		dbHelper.openReadableDatabase();
		HhCustomer01 customer = dbHelper.getCustomerData(custNum, cmpnyNo);
		dbHelper.closeDatabase();

		txtItementryCusNm.setText(customer.getHhCustomer_name());

		dbHelper.openReadableDatabase();
		item = dbHelper.getItemData(itemNum, locId, cmpnyNo);
		dbHelper.closeDatabase();

		
		// the below data hiding for najeeb developed by TISN On 12-Oct-2013
		// isStockItem = item.getHhItem_stockitem();

		txtItemEntNum.setText(itemNum);
		txtItemEntDesc.setText(item.getHhItem_description());
		txtItemEntCurrency.setText(item.getHhItem_currency());
		txtItemEntPrcLst.setText(custPriceList); // setting customer price list
		edtTxtItemEntQty.setText("1");
		edtTxtItemEntQty.setSelection(edtTxtItemEntQty.getText().length());

		double quantity = getActualHandOnQty(itemNum,
				item.getHhItem_qty_on_hand(), locId, dbHelper, supporter);
		txtItemEntQtyHand.setText(quantity + "");

		dbHelper.openReadableDatabase();
		priceDetail = dbHelper.getPrcData(custPriceList, itemNum, cmpnyNo);
		dbHelper.closeDatabase();
		basePrc = 0.0;
		isSalesPriceAvail = false;

		if (priceDetail != null) {
            
         saleEndDate=priceDetail.getHhPrice_sales_enddate();
         saleStartDate=priceDetail.getHhPrice_sales_startdate();
         
         String dateFormat=setting.getHhSetting_dateformat();
         
         if(dateFormat.equals("MM/dd/yyyy"))
         {
		 formatDate=new SimpleDateFormat("MM/dd/yyyy");

         }
         else if(dateFormat.equals("dd/MM/yyyy"))
         {
        	 formatDate=new SimpleDateFormat("dd/MM/yyyy");
         }
         else if(dateFormat.equals("yyyy/MM/dd"))
         {
        	 formatDate=new SimpleDateFormat("yyyy/MM/dd");
         }
         else
         {
        	 formatDate=new SimpleDateFormat("yyyy/dd/MM");
         }
		
			
         String nowDate=(mMonth+1)+"/"+mDay+"/"+mYear;
		
			
			if(!saleEndDate.equals(""))
			{		
		
				try {
					Date endDate = formatDate.parse(saleEndDate);
					Date startDate=formatDate.parse(saleStartDate);	
					Date currentDate=formatDate.parse(nowDate);
					
					if((startDate.before(currentDate)|| startDate.equals(currentDate))
							&& (endDate.after(currentDate) || endDate.equals(currentDate)))
					{
						double salesPrice=Double.parseDouble(priceDetail.getHhPrice_sales_price());
						double convFactor=Double.parseDouble(priceDetail.getHhPrice_sales_conv_factor());
						basePrc=salesPrice/convFactor;
						isSalesPriceAvail = true;
					}
					else
					{
					basePrc = priceDetail.getHhPrice_price1(); // default price
					}
					
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			else
			{
			basePrc = priceDetail.getHhPrice_price1(); // default price
			}
			
		}
		
		
		String strItmDisc = supporter.getCurrencyFormat(currentDiscPrice);
//		edtTxt_Sales_Commission.setText(strItmDisc);
		edtTxtItemDiscount.setText(strItmDisc);
		edtTxtItemDiscount.setSelection(edtTxtItemDiscount.getText().length());

		// contract price calculation
		dbHelper.openReadableDatabase();
		HhContractPrice01 contPrc = dbHelper.getContractPrc(custNum, itemNum,
				custPriceList, supporter.getCompanyCurrency(), cmpnyNo);
		dbHelper.closeDatabase();

		isContPrcDiscAvail = false;
		if ((contPrc != null) && (basePrc > 0.0) && (!isSalesPriceAvail)) {

			int prcType = contPrc.getHhContPrc_pricetype();
			double discValueByContPrc = 0.0;
			int custType = 1;
			int costBase = 0;
			if (prcType == 1) {

				custType = contPrc.getHhContPrc_customertype();
				if (custType == 1) {
					basePrc = priceDetail.getHhPrice_price1();
				} else if (custType == 2) {

					basePrc = priceDetail.getHhPrice_price2();
				} else if (custType == 3) {

					basePrc = priceDetail.getHhPrice_price3();
				} else if (custType == 4) {

					basePrc = priceDetail.getHhPrice_price4();
				} else if (custType == 5) {

					basePrc = priceDetail.getHhPrice_price5();
				} else if (custType == 6) {

					basePrc = priceDetail.getHhPrice_price6();
				}
			} else if (prcType == 2) { // for price type 2--get percent value

				double percent = contPrc.getHhContPrc_discpercent();
				if (percent > 0.0) {
					basePrc = basePrc - ((basePrc * percent) / 100);
				}
			}

			else if (prcType == 3) {
				discValueByContPrc = contPrc.getHhContPrc_discamount();

				basePrc = basePrc - discValueByContPrc;
			}

			else if (prcType == 4) {
				double percent = 0.0;
				costBase = contPrc.getHhContPrc_costmethod();
				double costBaseValue = 0.0;
				if (costBase == 1) {
					costBaseValue = priceDetail.getHhPrice_markup_cost();
					percent = contPrc.getHhContPrc_pluspercent();
					basePrc = costBaseValue + ((costBaseValue * percent) / 100);
				} else if (costBase == 2) {
					costBaseValue = item.getHhItem_standard_cost();
					percent = contPrc.getHhContPrc_pluspercent();
					basePrc = costBaseValue + ((costBaseValue * percent) / 100);
				} else if (costBase == 3) {
					costBaseValue = item.getHhItem_most_recent_cost();
					percent = contPrc.getHhContPrc_pluspercent();
					basePrc = costBaseValue + ((costBaseValue * percent) / 100);
				} else if (costBase == 4) {
					costBaseValue = item.getHhItem_avg_cost();
					percent = contPrc.getHhContPrc_pluspercent();
					basePrc = costBaseValue + ((costBaseValue * percent) / 100);
				} else if (costBase == 5) {
					costBaseValue = item.getHhItem_last_unit_cost();
					percent = contPrc.getHhContPrc_pluspercent();
					basePrc = costBaseValue + ((costBaseValue * percent) / 100);
				}

			} else if (prcType == 5) {
				double fixedAmt = 0.0;
				double costBaseValue = 0.0;
				costBase = contPrc.getHhContPrc_costmethod();
				if (costBase == 1) {
					costBaseValue = priceDetail.getHhPrice_markup_cost();
					fixedAmt = contPrc.getHhContPrc_plusamount();
					basePrc = costBaseValue + fixedAmt;
				} else if (costBase == 2) {
					costBaseValue = item.getHhItem_standard_cost();
					fixedAmt = contPrc.getHhContPrc_plusamount();
					basePrc = costBaseValue + fixedAmt;
				} else if (costBase == 3) {
					costBaseValue = item.getHhItem_most_recent_cost();
					fixedAmt = contPrc.getHhContPrc_plusamount();
					basePrc = costBaseValue + fixedAmt;
				} else if (costBase == 4) {
					costBaseValue = item.getHhItem_avg_cost();
					fixedAmt = contPrc.getHhContPrc_plusamount();
					basePrc = costBaseValue + fixedAmt;
				} else if (costBase == 5) {
					costBaseValue = item.getHhItem_last_unit_cost();
					fixedAmt = contPrc.getHhContPrc_plusamount();
					basePrc = costBaseValue + fixedAmt;
				}

			} else if (prcType == 6) {
				double fixedAmt = contPrc.getHhContPrc_fixedprice();
				basePrc = fixedAmt;
			}

			isContPrcDiscAvail = true;

		} else {
			dbHelper.openReadableDatabase();
			String category = dbHelper.getItemCategory(itemNum, locId);
			HhContractPrice01 contPrice = dbHelper.getContractPrc(custNum,
					category);
			dbHelper.closeDatabase();

			isContPrcDiscAvail = false;

			if ((contPrice != null) && (basePrc > 0.0)) {
				if (contPrice != null) {

					int prcType = contPrice.getHhContPrc_pricetype();
					double discValueByContPrc = 0.0;
					int custType = 1;
					int costBase = 0;
					if (prcType == 1) {

						custType = contPrice.getHhContPrc_customertype();
						if (custType == 1) {
							basePrc = priceDetail.getHhPrice_price1();
						} else if (custType == 2) {

							basePrc = priceDetail.getHhPrice_price2();
						} else if (custType == 3) {

							basePrc = priceDetail.getHhPrice_price3();
						} else if (custType == 4) {

							basePrc = priceDetail.getHhPrice_price4();
						} else if (custType == 5) {

							basePrc = priceDetail.getHhPrice_price5();
						} else if (custType == 6) {

							basePrc = priceDetail.getHhPrice_price6();
						}
					} else if (prcType == 2) { // for price type 2--get percent
												// value

						double percent = contPrice.getHhContPrc_discpercent();
						if (percent > 0.0) {
							basePrc = basePrc - ((basePrc * percent) / 100);
						}
					}

					else if (prcType == 3) {
						discValueByContPrc = contPrice
								.getHhContPrc_discamount();

						basePrc = basePrc - discValueByContPrc;
					}

					else if (prcType == 4) {
						double percent = 0.0;
						costBase = contPrice.getHhContPrc_costmethod();
						double costBaseValue = 0.0;
						if (costBase == 1) {
							costBaseValue = priceDetail
									.getHhPrice_markup_cost();
							percent = contPrice.getHhContPrc_pluspercent();
							basePrc = costBaseValue
									+ ((costBaseValue * percent) / 100);
						} else if (costBase == 2) {
							costBaseValue = item.getHhItem_standard_cost();
							percent = contPrice.getHhContPrc_pluspercent();
							basePrc = costBaseValue
									+ ((costBaseValue * percent) / 100);
						} else if (costBase == 3) {
							costBaseValue = item.getHhItem_most_recent_cost();
							percent = contPrice.getHhContPrc_pluspercent();
							basePrc = costBaseValue
									+ ((costBaseValue * percent) / 100);
						} else if (costBase == 4) {
							costBaseValue = item.getHhItem_avg_cost();
							percent = contPrice.getHhContPrc_pluspercent();
							basePrc = costBaseValue
									+ ((costBaseValue * percent) / 100);
						} else if (costBase == 5) {
							costBaseValue = item.getHhItem_last_unit_cost();
							percent = contPrice.getHhContPrc_pluspercent();
							basePrc = costBaseValue
									+ ((costBaseValue * percent) / 100);
						}

					} else if (prcType == 5) {
						double fixedAmt = 0.0;
						double costBaseValue = 0.0;
						costBase = contPrice.getHhContPrc_costmethod();
						if (costBase == 1) {
							costBaseValue = priceDetail
									.getHhPrice_markup_cost();
							fixedAmt = contPrice.getHhContPrc_plusamount();
							basePrc = costBaseValue + fixedAmt;
						} else if (costBase == 2) {
							costBaseValue = item.getHhItem_standard_cost();
							fixedAmt = contPrice.getHhContPrc_plusamount();
							basePrc = costBaseValue + fixedAmt;
						} else if (costBase == 3) {
							costBaseValue = item.getHhItem_most_recent_cost();
							fixedAmt = contPrice.getHhContPrc_plusamount();
							basePrc = costBaseValue + fixedAmt;
						} else if (costBase == 4) {
							costBaseValue = item.getHhItem_avg_cost();
							fixedAmt = contPrice.getHhContPrc_plusamount();
							basePrc = costBaseValue + fixedAmt;
						} else if (costBase == 5) {
							costBaseValue = item.getHhItem_last_unit_cost();
							fixedAmt = contPrice.getHhContPrc_plusamount();
							basePrc = costBaseValue + fixedAmt;
						}

					} else if (prcType == 6) {
						double fixedAmt = contPrice.getHhContPrc_fixedprice();
						basePrc = fixedAmt;
					}

				}

				isContPrcDiscAvail = true;

			}

		}


	

		if (isEdit) {
			prvUom = getIntent().getStringExtra("eUom");
			prvQty = getIntent().getStringExtra("eQty");
			prvPrc = getIntent().getStringExtra("ePrc");
			prvDate = getIntent().getStringExtra("eDate");
			prvEntType = getIntent().getStringExtra("eType");
			preDiscount = getIntent().getStringExtra("eDiscount");
			preDiscType = getIntent().getStringExtra("eDiscType");

			double dPrvQty = Double.parseDouble(prvQty);

			currentItemPrice = Double.parseDouble(prvPrc) / dPrvQty;

			int index = uomList.indexOf(prvUom);
			spnItemEntUOM.setSelection(index);
			spnItemEntUOM.setEnabled(false);
			txtItemEntUOM.setText(uomList.get(index));
			edtTxtItemEntQty.setText(prvQty);
			edtTxtItemEntQty.setSelection(edtTxtItemEntQty.getText().length());

			// calculate actual quantity entered previously
			uom = (String) spnItemEntUOM.getItemAtPosition(spnItemEntUOM
					.getSelectedItemPosition());
			uomConFact = getUnitOfMeasureConvFact(itemNum, uom, cmpnyNo);
			double actQty = uomConFact * dPrvQty;
            
			
			shipDate = prvDate;
			setValueToDatePicker(shipDate);
			
			
			// For Invoice Edit
				if (prvEntType.equals("tr")) {

					btnItemEntryToSale.setVisibility(View.GONE);
					// For Invoice Edit
					if (mode.equals("invoiceEdit")) {
						final String refNo = entryDetails.getString(
								"referencenumber", "");
						dbHelper.openReadableDatabase();
						HhTran01 transactionData = dbHelper
								.getTransactionList_(refNo, itemNum, locId,
										uom, prvEntType);
						// Boolean flag = dbHelper.checkTransactionList(refNo,
						// itemNum, locId);
						double prevqty = 0;
						if (transactionData != null)// && flag.equals(false)){
							prevqty = Double.parseDouble(transactionData
									.getHhTran_qty() + "");

						prevqty = uomConFact * prevqty;

						double qtyOnHand = dbHelper.getItemQty(itemNum, locId);

						txtItemEntQtyHand.setText((qtyOnHand - prevqty) + "");
						dbHelper.closeDatabase();
					} else {
						txtItemEntQtyHand.setText((quantity - actQty) + ""); // reduce
					} // quantity
						// for
						// return

				} else if (prvEntType.equals("ts")) {
					btnItemEntryToReturn.setVisibility(View.GONE);
					// For Invoice Edit
					if (mode.equals("invoiceEdit")) {
						final String refNo = entryDetails.getString(
								"referencenumber", "");
						dbHelper.openReadableDatabase();
						HhTran01 transactionData = dbHelper
								.getTransactionList_(refNo, itemNum, locId,
										uom, prvEntType);
						// Boolean flag = dbHelper.checkTransactionList(refNo,
						// itemNum, locId);
						double prevqty = 0;
						if (transactionData != null)// && flag.equals(false)){
							prevqty = Double.parseDouble(transactionData
									.getHhTran_qty() + "");

						prevqty = uomConFact * prevqty;
						double qtyOnHand = dbHelper.getItemQty(itemNum, locId);
						txtItemEntQtyHand.setText((qtyOnHand + prevqty) + "");
						dbHelper.closeDatabase();

					} else {
						txtItemEntQtyHand.setText((quantity + actQty) + ""); // increase
					} // quantity
						// for
						// sale
				} else {
					txtItemEntQtyHand.setText((quantity + actQty) + ""); // increase
																			// quantity
																			// for
																			// order
				}

			double prcValue = Double.parseDouble(preDiscount);
			if (preDiscType.equals("P")) {
				spnItemEntDisc.setSelection(1);
				prcValue = (prcValue * 100) / Double.parseDouble(prvPrc);
			}

			String strItmDisc2 = supporter.getCurrencyFormat(prcValue);
			edtTxtItemDiscount.setText(strItmDisc2);
			edtTxtItemDiscount.setSelection(edtTxtItemDiscount.getText()
					.length());

		} else {
			uom = (String) spnItemEntUOM.getItemAtPosition(spnItemEntUOM
					.getSelectedItemPosition());
			uomConFact = getUnitOfMeasureConvFact(itemNum, mDefaultUOM, cmpnyNo);
			currentItemPrice = (basePrc * uomConFact);
			txtItemEntUOM.setText(item.getHhItem_loc_uom());
			
		}

		String strBasePrz = supporter.getCurrencyFormat(basePrc);
		txtItemEntBasePrc.setText(strBasePrz);

		currentItmPrz = supporter.getCurrencyFormat(currentItemPrice);
		edtTxtItemEntUnitPrc.setText(currentItmPrz);
		edtTxtItemEntUnitPrc.setSelection(edtTxtItemEntUnitPrc.getText()
				.length());

		txtItemEntLoc.setText(item.getHhItem_loc_id());
		edtTxtItemEntShipDate.setText(shipDate);
		}
		
		/** to increment the quantity */
		btnIncrement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String cqty = edtTxtItemEntQty.getText().toString();

				if (!cqty.equals("")) {
					int q = Integer.parseInt(cqty);
					q++;
					edtTxtItemEntQty.setText("" + q);
					edtTxtItemEntQty.setSelection(edtTxtItemEntQty.getText()
							.length());

					// to calculate price by quantity level
					if ((!isContPrcDiscAvail) && (basePrc > 0.0)) {
						double rsltPrc = calculateDiscPrcByQtyLvl(priceDetail,
								q);

						if (rsltPrc > 0.0) {
							String strBasePrz = supporter
									.getCurrencyFormat(rsltPrc);
							txtItemEntBasePrc.setText(strBasePrz);
							calculateUnitPrc();
						}

					}

				} else {
					edtTxtItemEntQty.setText("1");
					edtTxtItemEntQty.setSelection(edtTxtItemEntQty.getText()
							.length());

					// to calculate price by quantity level
					if ((!isContPrcDiscAvail) && (basePrc > 0.0)) {
						double rsltPrc = calculateDiscPrcByQtyLvl(priceDetail,
								1);

						if (rsltPrc > 0.0) {
							String strBasePrz = supporter
									.getCurrencyFormat(rsltPrc);
							txtItemEntBasePrc.setText(strBasePrz);
							calculateUnitPrc();
						}

					}
				}

			}
		});

		/** to decrement the quantity */
		btnDecrement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String cqty = edtTxtItemEntQty.getText().toString();
				if (!cqty.equals("")) {
					int q = Integer.parseInt(cqty);
					if (q > 1) {
						q--;
						edtTxtItemEntQty.setText("" + q);
						edtTxtItemEntQty.setSelection(edtTxtItemEntQty
								.getText().length());

						// to calculate price by quantity level
						if ((!isContPrcDiscAvail) && (basePrc > 0.0)) {
							double rsltPrc = calculateDiscPrcByQtyLvl(
									priceDetail, q);

							if (rsltPrc > 0.0) {
								txtItemEntBasePrc.setText(rsltPrc + "");
								calculateUnitPrc();
							}

						}
					}
				} else {
					edtTxtItemEntQty.setText("1");
					edtTxtItemEntQty.setSelection(edtTxtItemEntQty.getText()
							.length());

					// to calculate price by quantity level
					if ((!isContPrcDiscAvail) && (basePrc > 0.0)) {
						double rsltPrc = calculateDiscPrcByQtyLvl(priceDetail,
								1);

						if (rsltPrc > 0.0) {
							txtItemEntBasePrc.setText(rsltPrc + "");
							calculateUnitPrc();
						}

					}
				}

			}
		});

		/** to show date picker dialog */
		edtTxtItemEntShipDate.setOnTouchListener(new OnTouchListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				showDialog(DATE_DIALOG_ID_SHIP);
				return true;
				// return false;
			}
		});

		// second, we create the TextWatcher
		TextWatcher qtyWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1,
					int i2) {

				String stxt = charSequence.toString();

				if (!stxt.equals("")) {

					int quantity = Integer.parseInt(stxt);

					// to calculate price by quantity level
					if ((!isContPrcDiscAvail) && (basePrc > 0.0)) {
						double rsltPrc = calculateDiscPrcByQtyLvl(priceDetail,
								quantity);

						if (rsltPrc > 0.0) {
							txtItemEntBasePrc.setText(rsltPrc + "");
							calculateUnitPrc();
						}

					}

				}

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		};

		edtTxtItemEntQty.addTextChangedListener(qtyWatcher);

		// TextWatcher for unitprice
		TextWatcher unitPrcWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1,
					int i2) {
				String stxt = charSequence.toString();

				if (!stxt.equals("")) {

					String strUnitPrice = edtTxtItemEntUnitPrc.getText()
							.toString();
					int indexOFdec = strUnitPrice.indexOf(".");

					if (strUnitPrice.equals(".")) {
						edtTxtItemEntUnitPrc.setText("");
						edtTxtItemEntUnitPrc.append("0.");
						edtTxtItemEntUnitPrc.setSelection(edtTxtItemEntUnitPrc
								.getText().length());
					} else if (indexOFdec >= 0) {

						if (strUnitPrice.substring(indexOFdec).length() > deciValue) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									edtTxtItemEntUnitPrc.getWindowToken(), 0);
						}
					}

				}

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		};

		edtTxtItemEntUnitPrc.addTextChangedListener(unitPrcWatcher);

		// TextWatcher for discount
		TextWatcher dizcPrcWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1,
					int i2) {
				String stxt = charSequence.toString();

				if (!stxt.equals("")) {

					String strDiscPrice = edtTxtItemDiscount.getText()
							.toString();
					int indexOFdec = strDiscPrice.indexOf(".");

					if (strDiscPrice.equals(".")) {
						edtTxtItemDiscount.setText("");
						edtTxtItemDiscount.append("0.");
						edtTxtItemDiscount.setSelection(edtTxtItemDiscount
								.getText().length());
					} else if (indexOFdec >= 0) {

						if (strDiscPrice.substring(indexOFdec).length() > deciValue) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									edtTxtItemEntUnitPrc.getWindowToken(), 0);
						}
					}

				}

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		};

		edtTxtItemDiscount.addTextChangedListener(dizcPrcWatcher);

		/** to handle uom changes */
		spnItemEntUOM.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				if (toHandleSpnExeOnLoad) {
					calculateUnitPrc();

					txtItemEntUOM.setText(uom);
				}
				toHandleSpnExeOnLoad = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		/** to do ok button click action */
		btnItemEntryOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					btnItemEntryOk.setEnabled(false);

					String uom = (String) spnItemEntUOM
							.getItemAtPosition(spnItemEntUOM
									.getSelectedItemPosition());
					String strQty = edtTxtItemEntQty.getText().toString();
					String uPrc = edtTxtItemEntUnitPrc.getText().toString();
					String strDiscValue = edtTxtItemDiscount.getText()
							.toString();
					/*String strSalesCommission = edtTxt_Sales_Commission.getText().toString();*/
					String strSalesCommission = "0";

					String strDiscType = (String) spnItemEntDisc
							.getItemAtPosition(spnItemEntDisc
									.getSelectedItemPosition());
					String discType = "A";
					if (strDiscType.equals("Percentage")) {
						discType = "P";
					}

					if (strQty.equals("")) {
						toastMsg.showToast(ItemEntry.this,
								"Enter valid quantity.");
						btnItemEntryOk.setEnabled(true);
					} else if (uPrc.equals("")) {
						toastMsg.showToast(ItemEntry.this, "Enter valid price.");
						btnItemEntryOk.setEnabled(true);
					} else if (!supporter.isAmountValid(uPrc)) {
						toastMsg.showToast(ItemEntry.this,
								"Please enter valid unit price.");
						btnItemEntryOk.setEnabled(true);
					} else if (strDiscValue.equals("")) {
						toastMsg.showToast(ItemEntry.this,
								"Enter discount value.");
						btnItemEntryOk.setEnabled(true);
					} else if (!supporter.isAmountValidWithZero(strDiscValue)) {
						toastMsg.showToast(ItemEntry.this,
								"Please enter valid discount.");
						btnItemEntryOk.setEnabled(true);
					} else {
						int qty = Integer.parseInt(strQty);
						double fPrc = Double.parseDouble(uPrc);

						String strhandOnQty = txtItemEntQtyHand.getText()
								.toString();
						double handOnQty = Double.parseDouble(strhandOnQty);

						uomConFact = getUnitOfMeasureConvFact(itemNum, uom,
								cmpnyNo);
						double actualQty = qty * uomConFact;

						double tDiscValue = Double.parseDouble(strDiscValue);

						if (actualQty <= 0) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid quantity.");
							btnItemEntryOk.setEnabled(true);
						} /*
						 * else if ((double) actualQty > handOnQty) {
						 * toastMsg.showToast(ItemEntry.this,
						 * "Quantity on hand not available.");
						 * btnItemEntryOk.setEnabled(true); }
						 */else if (fPrc <= 0.0) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid price.");
							btnItemEntryOk.setEnabled(true);
						} else if (discType.equals("P")
								&& (tDiscValue < 0 || tDiscValue > 100)) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid discount percentage.");
							btnItemEntryOk.setEnabled(true);
						} else if (discType.equals("A")
								&& (tDiscValue > ((fPrc * qty)))) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid discount amount.");
							btnItemEntryOk.setEnabled(true);
						} else {

							double discValue = tDiscValue;

							if (discType.equals("P") && tDiscValue > 0.0) {
								discValue = ((fPrc * qty) * tDiscValue) / 100;
							}
							
							

							String cdate = edtTxtItemEntShipDate.getText()
									.toString();
                           
							TempItem tempItem = new TempItem();
							tempItem.setTemp_itemNum(itemNum);
							tempItem.setTemp_qty(qty + "");
							tempItem.setTemp_extPrice((fPrc * qty) + "");
							tempItem.setTemp_location(txtItemEntLoc.getText()
									.toString());
							tempItem.setTemp_uom(uom);
							tempItem.setTemp_date(cdate);
							tempItem.setTemp_entryType("oe");
							tempItem.setTemp_discount(supporter
									.getCurrencyFormat(discValue));
							tempItem.setTemp_discType(discType);
                            
							dbHelper.openWritableDatabase();
							boolean isItemExist = dbHelper
									.checkItemExist(itemNum);

							if (!isItemExist) { // if item not exist/ perform (
												// addTempItem
												// )
								dbHelper.addTempItem(tempItem);
							} else if (isItemExist && (!isEdit)) { // item
																	// exist, no
																	// itm
																	// avail
																	// with
																	// current
																	// uom) --
																	// perform
																	// (update
																	// to
																	// add)
								dbHelper.updateTempItem(itemNum, uom, tempItem,
										isEdit);
							} else if (isItemExist && (isEdit)) { // just update
																	// the
																	// same
																	// item if
																	// uom
																	// changed
																	// or not

								if (!prvQty.equals(strQty)
										|| (currentItemPrice != fPrc)
										|| !prvUom.equals(uom)
										|| !preDiscount.equals(strDiscValue)
										|| !preDiscType.equals(strDiscType)
										|| !cdate.equals(prvDate)) {
									dbHelper.updateTempItem(itemNum, prvUom,
											tempItem, isEdit);
								}
							}

							dbHelper.closeDatabase();

//							supporter.simpleNavigateTo(Transaction.class);
							Intent i = new Intent(ItemEntry.this, Transaction.class);
							/*String st = edtTxt_Sales_Commission.getText().toString();*/
							String st = "0";
							i.putExtra("Value",st);
							startActivity(i);
							finish();

						}
					}
				} catch (Exception e) {
					Toast.makeText(ItemEntry.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					LogfileCreator.appendLog(e.getMessage());
					btnItemEntryOk.setEnabled(true);
				}
			}
		});

		btnItemEntryToSale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					btnItemEntryToSale.setEnabled(false);

					String uom = (String) spnItemEntUOM
							.getItemAtPosition(spnItemEntUOM
									.getSelectedItemPosition());

					String strQty = edtTxtItemEntQty.getText().toString();
					String uPrc = edtTxtItemEntUnitPrc.getText().toString();
					String strDiscValue = edtTxtItemDiscount.getText()
							.toString();

					String strDiscType = (String) spnItemEntDisc
							.getItemAtPosition(spnItemEntDisc
									.getSelectedItemPosition());
					String discType = "A";
					if (strDiscType.equals("Percentage")) {
						discType = "P";
					}

					if (strQty.equals("")) {
						toastMsg.showToast(ItemEntry.this,
								"Enter valid quantity");
						btnItemEntryToSale.setEnabled(true);
					} else if (uPrc.equals("")) {
						toastMsg.showToast(ItemEntry.this, "Enter valid price");
						btnItemEntryToSale.setEnabled(true);
					} else if (strDiscValue.equals("")) {
						toastMsg.showToast(ItemEntry.this,
								"Enter discount value");
						btnItemEntryToSale.setEnabled(true);
					} else {

						int qty = Integer.parseInt(strQty);
						double fPrc = Double.parseDouble(uPrc);
						String strhandOnQty = txtItemEntQtyHand.getText()
								.toString();
						double handOnQty = Double.parseDouble(strhandOnQty);
						uomConFact = getUnitOfMeasureConvFact(itemNum, uom,
								cmpnyNo);
						double actualQty = qty * uomConFact;
						double tDiscValue = Double.parseDouble(strDiscValue);

						if (actualQty <= 0) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid quantity");
							btnItemEntryToSale.setEnabled(true);
						} else if ((double) actualQty > handOnQty
								/*&& (isStockItem == 1)*/) {
							toastMsg.showToast(ItemEntry.this,
									"Quantity on hand not available");
							btnItemEntryToSale.setEnabled(true);
						} else if (fPrc <= 0.0) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid price");
							btnItemEntryToSale.setEnabled(true);
						} else if (discType.equals("P")
								&& (tDiscValue < 0 || tDiscValue > 100)) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid discount percentage");
							btnItemEntryToSale.setEnabled(true);
						} else if (discType.equals("A")
								&& (tDiscValue > ((fPrc * qty)))) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid discount amount");
							btnItemEntryToSale.setEnabled(true);
						} else {

							double discValue = tDiscValue;

							if (discType.equals("P") && tDiscValue > 0.0) {
								discValue = ((fPrc * qty) * tDiscValue) / 100;
							}

							String cdate = edtTxtItemEntShipDate.getText()
									.toString();

							TempItem tempItem = new TempItem();
							tempItem.setTemp_itemNum(itemNum);
							tempItem.setTemp_qty(qty + "");
							tempItem.setTemp_extPrice((fPrc * qty) + "");
							tempItem.setTemp_location(txtItemEntLoc.getText()
									.toString());
							tempItem.setTemp_uom(uom);
							tempItem.setTemp_date(cdate);
							tempItem.setTemp_entryType("ts"); // manual
																// definition
																// for
																// identification
																// purpose
							tempItem.setTemp_discount(supporter
									.getCurrencyFormat(discValue));
							tempItem.setTemp_discType(discType);

							dbHelper.openWritableDatabase();
							boolean isItemExist = dbHelper
									.checkItemExist(itemNum);

							if (!isItemExist) { // if item not exist/ perform (
												// addTempItem
												// )
								 // For Richard Qoh coming wrong
                                supporter.setSaleType("tosale");
								dbHelper.addTempItem(tempItem);

							} else if (isItemExist && (!isEdit)) { // item
																	// exist, no
																	// itm
																	// avail
																	// with
																	// current
																	// uom) --
																	// perform
																	// (update
																	// to
																	// add)

								// For Richard Qoh coming wrong
								supporter.setSaleType("tosale");

								dbHelper.updateTempItem(itemNum, uom, tempItem,
										isEdit);

							} else if (isItemExist && (isEdit)) { // just update
																	// the
																	// same
																	// item if
																	// uom
																	// changed
																	// or not

								if (!prvQty.equals(strQty)
										|| (currentItemPrice != fPrc)
										|| !prvUom.equals(uom)
										|| !preDiscount.equals(strDiscValue)
										|| !preDiscType.equals(strDiscType)
										|| !cdate.equals(prvDate)) {
									// For Richard Qoh coming wrong
                                    supporter.setSaleType("tosale");
									dbHelper.updateTempItem(itemNum, prvUom,
											tempItem, isEdit);

								}

							}
							dbHelper.closeDatabase();

							supporter.simpleNavigateTo(Transaction.class);
						}
					}
				} catch (Exception e) {
					Toast.makeText(ItemEntry.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					LogfileCreator.appendLog(e.getMessage());
					btnItemEntryToSale.setEnabled(true);
				}
			}
		});

		btnItemEntryToReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					btnItemEntryToReturn.setEnabled(false);

					String uom = (String) spnItemEntUOM
							.getItemAtPosition(spnItemEntUOM
									.getSelectedItemPosition());
					String strQty = edtTxtItemEntQty.getText().toString();
					String uPrc = edtTxtItemEntUnitPrc.getText().toString();
					String strDiscValue = edtTxtItemDiscount.getText()
							.toString();

					String strDiscType = (String) spnItemEntDisc
							.getItemAtPosition(spnItemEntDisc
									.getSelectedItemPosition());
					String discType = "A";
					if (strDiscType.equals("Percentage")) {
						discType = "P";
					}

					if (strQty.equals("")) {
						toastMsg.showToast(ItemEntry.this,
								"Enter valid quantity");
						btnItemEntryToReturn.setEnabled(true);
					} else if (uPrc.equals("")) {
						toastMsg.showToast(ItemEntry.this, "Enter valid price");
						btnItemEntryToReturn.setEnabled(true);
					} else if (strDiscValue.equals("")) {
						toastMsg.showToast(ItemEntry.this,
								"Enter discount value");
						btnItemEntryToReturn.setEnabled(true);
					} else {

						int qty = Integer.parseInt(strQty);
						double fPrc = Double.parseDouble(uPrc);
						// String strhandOnQty =
						// txtItemEntQtyHand.getText().toString();
						// double handOnQty = Double.parseDouble(strhandOnQty);
						uomConFact = getUnitOfMeasureConvFact(itemNum, uom,
								cmpnyNo);
						double actualQty = qty * uomConFact;
						double tDiscValue = Double.parseDouble(strDiscValue);

						if (actualQty <= 0) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid quantity");
							btnItemEntryToReturn.setEnabled(true);
						} /*
						 * else if ((double) actualQty > handOnQty) {
						 * Toast.makeText(ItemEntry.this,
						 * "Quantity on hand not available",
						 * Toast.LENGTH_SHORT).show(); }
						 */else if (fPrc <= 0.0) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid price");
							btnItemEntryToReturn.setEnabled(true);
						} else if (discType.equals("P")
								&& (tDiscValue < 0 || tDiscValue > 100)) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid discount percentage");
							btnItemEntryToReturn.setEnabled(true);
						} else if (discType.equals("A")
								&& (tDiscValue > ((fPrc * qty)))) {
							toastMsg.showToast(ItemEntry.this,
									"Enter valid discount amount");
							btnItemEntryToReturn.setEnabled(true);
						} else {

							double discValue = tDiscValue;

							if (discType.equals("P") && tDiscValue > 0.0) {
								discValue = ((fPrc * qty) * tDiscValue) / 100;
							}

							String cdate = edtTxtItemEntShipDate.getText()
									.toString();

							TempItem tempItem = new TempItem();
							tempItem.setTemp_itemNum(itemNum);
							tempItem.setTemp_qty(qty + "");
							tempItem.setTemp_extPrice((fPrc * qty) + "");
							tempItem.setTemp_location(txtItemEntLoc.getText()
									.toString());
							tempItem.setTemp_uom(uom);
							tempItem.setTemp_date(cdate);
							tempItem.setTemp_entryType("tr"); // manual
																// definition
																// for
																// identification
																// purpose
							tempItem.setTemp_discount(supporter
									.getCurrencyFormat(discValue));
							tempItem.setTemp_discType(discType);

							dbHelper.openWritableDatabase();
							boolean isItemExist = dbHelper
									.checkItemExist(itemNum);

							if (!isItemExist) { // if item not exist/ perform (
												// addTempItem
												// )
								// For Richard Qoh coming wrong
                                supporter.setSaleType("toreturn");
								dbHelper.addTempItem(tempItem);

							} else if (isItemExist && (!isEdit)) { // item
																	// exist, no
																	// itm
																	// avail
																	// with
																	// current
																	// uom) --
																	// perform
																	// (update
																	// to
																	// add)
								 // For Richard Qoh coming wrong
                                supporter.setSaleType("toreturn");
								dbHelper.updateTempItem(itemNum, uom, tempItem,
										isEdit);

							} else if (isItemExist && (isEdit)) { // just update
																	// the
																	// same
																	// item if
																	// uom
																	// changed
																	// or not

								if (!prvQty.equals(strQty)
										|| (currentItemPrice != fPrc)
										|| !prvUom.equals(uom)
										|| !preDiscount.equals(strDiscValue)
										|| !preDiscType.equals(strDiscType)
										|| !cdate.equals(prvDate)) {
									// For Richard Qoh coming wrong
                                    supporter.setSaleType("toreturn");
									dbHelper.updateTempItem(itemNum, prvUom,
											tempItem, isEdit);

								}

							}

							dbHelper.closeDatabase();

							supporter.simpleNavigateTo(Transaction.class);
							
						}
					}
				} catch (Exception e) {
					Toast.makeText(ItemEntry.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
					LogfileCreator.appendLog(e.getMessage());
					btnItemEntryToReturn.setEnabled(true);
				}
			}
		});
		
	}

	private void setUomDefault() {
		if (uomList.contains("CS")) {
			int index1 = uomList.indexOf("CS");
			spnItemEntUOM.setSelection(index1);
			spnItemEntUOM.setEnabled(true);
			spnItemEntUOM.setClickable(true);
		}
	}

	private double getUnitOfMeasureConvFact(String itemNum, String uom,
			String companyNo) {
		dbHelper.openReadableDatabase();
		double uomConvFactor = dbHelper.getUOMConvFactor(itemNum, uom,
				companyNo);
		dbHelper.closeDatabase();
		return uomConvFactor;
	}

	// to update hand on quantity by current hand quantity
	public double getActualHandOnQty(String itmNo, double iQty, String locid,
			MspDBHelper dbHelper, Supporter supporter) {

		double purchasedQty = 0.00;

		dbHelper.openReadableDatabase();
		List<TempItem> OnHandQtyLst = dbHelper.getTempItems(itmNo, locid);
		dbHelper.closeDatabase();

		for (int i = 0; i < OnHandQtyLst.size(); i++) {

			String uom = OnHandQtyLst.get(i).getTemp_uom();
			String strQnty = OnHandQtyLst.get(i).getTemp_qty();
			double Qnty = Double.parseDouble(strQnty);

			String itemNo = OnHandQtyLst.get(i).getTemp_itemNum();

			dbHelper.openReadableDatabase();
			double conFact = dbHelper.getUOMConvFactor(itemNo, uom, cmpnyNo);
			dbHelper.closeDatabase();

			double rQty = Qnty * conFact; // to get quantity required by uom
											// given

			String type = OnHandQtyLst.get(i).getTemp_entryType();

			if (type.equals("oe") || type.equals("ts")) {
				purchasedQty = purchasedQty + rQty;
			} else if (type.equals("tr")) {
				purchasedQty = purchasedQty - rQty;
			}

		}

		double actualAvailQty = iQty - purchasedQty;

		return actualAvailQty;
	}

	private double calculateDiscPrcByQtyLvl(HhPrice01 prcDetail, int qty) {/*

		double result = 0.0;

		double qtyLvl1 = prcDetail.getHhPrice_qtylevel1();
		double qtyLvl2 = prcDetail.getHhPrice_qtylevel2();
		double qtyLvl3 = prcDetail.getHhPrice_qtylevel3();
		double qtyLvl4 = prcDetail.getHhPrice_qtylevel4();
		double qtyLvl5 = prcDetail.getHhPrice_qtylevel5();

		if ((qtyLvl1 > 0.0) && (qtyLvl1 == (double) qty)) {
			result = prcDetail.getHhPrice_price2();
		} else if ((qtyLvl2 > 0.0) && (qtyLvl2 == (double) qty)) {
			result = prcDetail.getHhPrice_price3();
		} else if ((qtyLvl3 > 0.0) && (qtyLvl3 == (double) qty)) {
			result = prcDetail.getHhPrice_price4();
		} else if ((qtyLvl4 > 0.0) && (qtyLvl4 == (double) qty)) {
			result = prcDetail.getHhPrice_price5();
		} else if ((qtyLvl5 > 0.0) && (qtyLvl5 == (double) qty)) {
			result = prcDetail.getHhPrice_price6();
		}

		return result;
	*/
		// Pranesh
		double result = 0.0;

		double qtylevel1 = priceDetail.getHhPrice_qtylevel1();
		double qtylevel2 = priceDetail.getHhPrice_qtylevel2();
		double qtylevel3 = priceDetail.getHhPrice_qtylevel3();
		double qtylevel4 = priceDetail.getHhPrice_qtylevel4();
		double qtylevel5 = priceDetail.getHhPrice_qtylevel5();

		if (qtylevel1 != 0 || qtylevel2 != 0 || qtylevel3 != 0
				|| qtylevel4 != 0 || qtylevel5 != 0) {

			if (qty >= qtylevel5 && qtylevel5 != 0) {
				result = priceDetail.getHhPrice_price6();
			} else if (qty >= qtylevel4 && qtylevel4 != 0) {
				result = priceDetail.getHhPrice_price5();
			} else if (qty >= qtylevel3 && qtylevel3 != 0) {
				result = priceDetail.getHhPrice_price4();
			} else if (qty >= qtylevel2 && qtylevel2 != 0) {
				result = priceDetail.getHhPrice_price3();
			} else if (qty >= qtylevel1 && qtylevel1 != 0) {
				result = priceDetail.getHhPrice_price2();
			} else {
				// Base price price1
				result = priceDetail.getHhPrice_price1();
			}

		} else {
			// Base price price1
			result = priceDetail.getHhPrice_price1();
		}
		return result;

	}

	private void calculateUnitPrc() {/*
		uom = (String) spnItemEntUOM.getItemAtPosition(spnItemEntUOM
				.getSelectedItemPosition());

		uomConFact = getUnitOfMeasureConvFact(itemNum, uom, cmpnyNo);
		String pricingUOM=priceDetail.getHhPrice_pricing_uom();
		double existingUOMConFact=getUnitOfMeasureConvFact(itemNum, pricingUOM, cmpnyNo);
		double basePrc = Double.parseDouble(txtItemEntBasePrc.getText()
				.toString());
		
		double untPrz = (uomConFact * basePrc) / existingUOMConFact;
		   unitPrz = supporter.getCurrencyFormat(untPrz);
		   edtTxtItemEntUnitPrc.setText(unitPrz);
		   
		   
		   
		// Toast.makeText(ItemEntry.this, ""+unitPrz,
		// Toast.LENGTH_SHORT).show(); //to test spinner first time execution
	*/
		uom = (String) spnItemEntUOM.getItemAtPosition(spnItemEntUOM
				.getSelectedItemPosition());

		uomConFact = getUnitOfMeasureConvFact(itemNum, uom, cmpnyNo);

		double basePrc = Double.parseDouble(txtItemEntBasePrc.getText()
				.toString());
		double untPrz = uomConFact * basePrc;
		unitPrz = supporter.getCurrencyFormat(untPrz);
		edtTxtItemEntUnitPrc.setText(unitPrz);
		}

	
	
	// to create date picker dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID_SHIP:
			return new DatePickerDialog(this, shipDateListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	// to set current date
	private void currentDate() {
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	private void setValueToDatePicker(String date) {
		dbHelper.openReadableDatabase();
		Calendar c = supporter.stringDateToCalender(date);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		dbHelper.closeDatabase();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}
}
