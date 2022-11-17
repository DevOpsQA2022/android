package com.mobilesalesperson.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhHistory01;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

import static android.content.DialogInterface.*;

/**
 * @author TIVM class to display customer list
 */
public class CustomerSelection extends AppBaseActivity {

	/** variable declarations */
	private Supporter supporter;
	private MspDBHelper dbhelpher;
	private List<HhCustomer01> custList;
	private ListView lstViewCustomer;
	private CustomerSelectionAdapter adapter;
	private HistorySelectionAdapter hAdapter;
	private EditText edtTxtCustSearch;
	private LinearLayout lay_SelectPriceList;
	private Spinner spn_priceList;
	private ArrayAdapter<String> adptCustPriceListSpinner;
	private String mode;
	private HhSetting status;
	private HhManager manager;
	private int shipStatus;
	private int mCustEditPriceList;
	private List<String> lstShipToLocDet, lstShipVia;
	private String shipLoc, shipVia, shipDate;
	private String mCustShipViacode;// 11-11-2013 added in TISN
	private List<String> custPriceList;

	private List<HhHistory01> histList;

	private int mYear;
	private int mMonth;
	private int mDay;
	private String cmpnyNo; // added for multicompany

	private String strCusPriceList;
	private String strDefaultPriceList;
	private String pricelist;


	/**
	 * method to initialize the class activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.customer_selection_layout);

        registerBaseActivityReceiver();

        edtTxtCustSearch = (EditText) findViewById(R.id.edtTxt_CustSearch);
        lstViewCustomer = (ListView) findViewById(R.id.lstView_Customer);
        lay_SelectPriceList = (LinearLayout) findViewById(R.id.lay_SelectPriceList);
        spn_priceList = (Spinner) findViewById(R.id.spn_CustPriceList);

        dbhelpher = new MspDBHelper(this);
        supporter = new Supporter(this, dbhelpher);

        mode = supporter.getMode();

        cmpnyNo = supporter.getCompanyNo(); // added jul 12/2013

        dbhelpher.openReadableDatabase();
        status = dbhelpher.getSettingData();
        manager = dbhelpher.getManagerData(supporter.getSalesPerson(), cmpnyNo);
        dbhelpher.closeDatabase();
        shipStatus = status.getHhSetting_showship();
        mCustEditPriceList = manager.getHhManager_customerbasedpl();
        strDefaultPriceList = manager.getHhManager_pricelistcode();

        lay_SelectPriceList.setVisibility(View.GONE);

        if (mode.equals("ie") || mode.equals("oe")) {

            if (mCustEditPriceList == 0) {
                lay_SelectPriceList.setVisibility(View.GONE);
            } else {
                lay_SelectPriceList.setVisibility(View.VISIBLE);

                // to load customer price list
                dbhelpher.openReadableDatabase();
                custPriceList = dbhelpher.getPriceList(cmpnyNo);
                dbhelpher.closeDatabase();
                adptCustPriceListSpinner = new ArrayAdapter<String>(
                        CustomerSelection.this,
                        android.R.layout.simple_dropdown_item_1line,
                        custPriceList);
                //adptCustPriceListSpinner.setDropDownViewResource(R.layout.spinner_item_layout);

                spn_priceList.setAdapter(adptCustPriceListSpinner);
            }

        }

        if (mode.equals("editdeletcust")) {
            dbhelpher.openReadableDatabase();
            custList = dbhelpher.getNewCustomerDataList(cmpnyNo);
            dbhelpher.closeDatabase();
            int listSize = custList.size();
            if (listSize == 0) {
                String msg = "New Customer Not Available";
                AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
                alertUser.setTitle("Information");
                alertUser.setMessage(msg);
                alertUser.setIcon(R.drawable.tick);
                alertUser.setCancelable(false);
                alertUser.setPositiveButton("OK",
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                supporter.simpleNavigateTo(MainMenu.class);
                                dialog.cancel();
                            }
                        });
                	//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

            } else {
                adapter = new CustomerSelectionAdapter(this, custList);
                lstViewCustomer.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                // for long press customer list
                registerForContextMenu(lstViewCustomer);
            }
        } else if (mode.equals("ie") || mode.equals("recipt") || mode.equals("oe")) {
            dbhelpher.openReadableDatabase();
            custList = dbhelpher.getCustomerDataList(cmpnyNo);
            dbhelpher.closeDatabase();
            adapter = new CustomerSelectionAdapter(this, custList);

            lstViewCustomer.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if (mode.equals("he")) {

            dbhelpher.openReadableDatabase();
            histList = dbhelpher.getHistoryData(cmpnyNo);
            dbhelpher.closeDatabase();

            hAdapter = new HistorySelectionAdapter(this, histList);
            lstViewCustomer.setAdapter(hAdapter);
            hAdapter.notifyDataSetChanged();
            registerForContextMenu(lstViewCustomer);

        }


        /**
         * Enabling Search Filter
         * */
        edtTxtCustSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                CustomerSelection.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable edtxt) {
                // TODO Auto-generated method stub
            }
        });

        // customer list item click function

            lstViewCustomer.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {



                    if(mode.equals("he")) {
                        HhHistory01 history01 = hAdapter.getItem(position);
                     String cusname =  history01.getHhTran_editedcustomername_new();
                     double price = history01.getHhTran_price_new();
                     String refno = history01.getHhTran_referenceNumber_new();
                     int quaty = history01.getHhTran_qty_new();
                     double discprice = history01.getHhTran_discPrice_new();
                     Intent intent = new Intent(CustomerSelection.this,Transaction.class);
                     intent.putExtra("value1",cusname);
                     intent.putExtra("value2",String.valueOf(price));
                     intent.putExtra("value3",String.valueOf(discprice));
                     intent.putExtra("value4",String.valueOf(quaty));
                     intent.putExtra("refno",refno);

                     startActivity(intent);





                     } else if(mode.equals("ie") || mode.equals("recipt") || mode.equals("oe")) {
                        HhCustomer01 customer = (HhCustomer01) adapter
                                .getItem(position);
                        String custNumber = customer.getHhCustomer_number();
                        dbhelpher.openReadableDatabase();
                        final String cusName = dbhelpher.getCustomerName(custNumber,
                                cmpnyNo);
                        boolean isNewCustomer = dbhelpher.isNewCustomer(custNumber,
                                cmpnyNo);
                        strCusPriceList = dbhelpher.getCustomerPricelist(custNumber,
                                cmpnyNo);
                        dbhelpher.closeDatabase();

                        if (mCustEditPriceList == 0) {
                            if (strCusPriceList.equals("")) {
                                pricelist = strDefaultPriceList;
                            } else {
                                pricelist = strCusPriceList;
                            }
                        } else {
                            pricelist = (String) spn_priceList
                                    .getItemAtPosition(spn_priceList
                                            .getSelectedItemPosition());
                        }

                        if (shipStatus == 0) { // only if shipping screen not shown

                            currentDate();
                            shipDate = supporter.getStringDate(mYear, mMonth + 1, mDay);

                            shipLoc = "";

                            dbhelpher.openReadableDatabase();
                            lstShipToLocDet = dbhelpher.getShipToLocDet(custNumber,
                                    cmpnyNo);
                            dbhelpher.closeDatabase();

                            if (lstShipToLocDet.size() != 0) {
                                shipLoc = lstShipToLocDet.get(0);
                                if (shipLoc.equals("DEFAULT")) {
                                    shipLoc = "";
                                }
                            }

                            shipVia = "";
                            dbhelpher.openReadableDatabase();
                            mCustShipViacode = dbhelpher.getShipViaCode(custNumber,
                                    cmpnyNo);
                            lstShipVia = dbhelpher.getShipViaList(cmpnyNo,
                                    mCustShipViacode);
                            dbhelpher.closeDatabase();
                            if (lstShipVia.size() != 0) {
                                String[] strArr = lstShipVia.get(0).split(" ");
                                shipVia = strArr[0].trim();
                                if (shipVia.equals("DEFAULT")) {
                                    shipVia = "";
                                }
                            }
                        }

                        if (mode.equals("ie")) {

                            boolean isCreditLimitExceeds = supporter.calCreditLimit(
                                    custNumber, cmpnyNo);

                            if (isCreditLimitExceeds && !isNewCustomer) {
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                        CustomerSelection.this);
                                alertDialog.setTitle("Warning");
                                alertDialog.setIcon(R.drawable.warning);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("Proceed",
                                        new OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                supporter
                                                        .setCreditAllowConformation("yes");
                                                if (shipStatus == 0) {
                                                    supporter.addShippingDetails(
                                                            custNumber, cusName,
                                                            shipLoc, shipVia, shipDate);
                                                    supporter.setPriceList(pricelist);
                                                    navigationProcess(custNumber,
                                                            ItemSelection.class);
                                                } else {
                                                    supporter.setPriceList(pricelist);
                                                    navigationProcess(custNumber,
                                                            ShipTo.class);
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.setNegativeButton("Cancel",
                                        new OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                alertDialog.setMessage("Credit Limit Exceeded");
                                ////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

                            } else {
                                // to navigate without alert
                                if (shipStatus == 0) {
                                    supporter.addShippingDetails(custNumber, cusName,
                                            shipLoc, shipVia, shipDate);
                                    supporter.setPriceList(pricelist);
                                    navigationProcess(custNumber, ItemSelection.class);
                                } else {
                                    supporter.setPriceList(pricelist);
                                    navigationProcess(custNumber, ShipTo.class);
                                }
                            }
                        }else if (mode.equals("oe")) {

                            boolean isCreditLimitExceeds = supporter.calCreditLimit(
                                    custNumber, cmpnyNo);

                            if (isCreditLimitExceeds && !isNewCustomer) {

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                        CustomerSelection.this);
                                alertDialog.setTitle("Warning");
                                alertDialog.setIcon(R.drawable.warning);
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("Proceed",
                                        new OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                supporter
                                                        .setCreditAllowConformation("yes");
                                                if (shipStatus == 0) {
                                                    supporter.addShippingDetails(
                                                            custNumber, cusName,
                                                            shipLoc, shipVia, shipDate);
                                                    supporter.setPriceList(pricelist);
                                                    navigationProcess(custNumber,
                                                            ItemSelection.class);
                                                } else {
                                                    supporter.setPriceList(pricelist);
                                                    navigationProcess(custNumber,
                                                            ShipTo.class);
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.setNegativeButton("Cancel",
                                        new OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                alertDialog.setMessage("Credit Limit Exceeded");
                                ////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);

                            } else {
                                if (shipStatus == 0) {
                                    supporter.addShippingDetails(custNumber, cusName,
                                            shipLoc, shipVia, shipDate);
                                    supporter.setPriceList(pricelist);
                                    navigationProcess(custNumber, ItemSelection.class);
                                } else {
                                    supporter.setPriceList(pricelist);
                                    navigationProcess(custNumber, ShipTo.class);
                                }
                            }

                        } else if (mode.equals("view")) {
                            navigationProcess(custNumber, CustomerInfo.class);
                        } else if (mode.equals("recipt")) {
                            navigationProcess(custNumber, Receipt.class);
                        } else if (mode.equals("editdeletcust")) {
                            dbhelpher.openReadableDatabase();
                            boolean isTranExist = dbhelpher.checkTranForNewCust(
                                    custNumber, cmpnyNo);
                            dbhelpher.closeDatabase();


                            if (isTranExist) {
                                alertBeforeEditOrDelete("This customer is having transaction, you are not allowed to edit.");

                            } else {
                                navigationProcess(custNumber, AddCustomer.class);
                            }
                        }
                    }

                }

            });

        }


	protected void navigationProcess(String custNumber, Class<?> cls) {

		Intent intent = new Intent(CustomerSelection.this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("customerNumber", custNumber);
		startActivity(intent);
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			supporter.simpleNavigateTo(MainMenu.class);

		}
		return super.onKeyDown(keyCode, event);
	}

	// method to delete list item if it long pressed
	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view,
			final ContextMenuInfo menuInfo) {
		if (view.getId() == R.id.lstView_Customer) {

			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			final HhCustomer01 customer = (HhCustomer01) adapter
					.getItem(info.position);

			final String custNum = customer.getHhCustomer_number();

			dbhelpher.openReadableDatabase();
			boolean isTranExist = dbhelpher.checkTranForNewCust(custNum,
					cmpnyNo);
			dbhelpher.closeDatabase();

			if (isTranExist) {
				alertBeforeEditOrDelete("This customer is having transaction, you are not allowed to delete.");
			} else {

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						CustomerSelection.this);
				alertDialog.setTitle("Confirm Delete");
				alertDialog.setIcon(R.drawable.dlg_delete);
				alertDialog.setCancelable(false);

				alertDialog.setPositiveButton("Yes",
						new OnClickListener() {


							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dbhelpher.openWritableDatabase();
								dbhelpher.deleteNewCustomer(custNum, cmpnyNo);
								dbhelpher.closeDatabase();
								adapter.remove(customer);
								custList.remove(customer);
								adapter.notifyDataSetChanged();
								supporter.simpleNavigateTo(MainMenu.class);
							}
						});

				alertDialog.setNegativeButton("No",
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				alertDialog
						.setMessage("Do you want to Delete ?");
                ////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
			}

		}
	}

	private void alertBeforeEditOrDelete(String message) {
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				CustomerSelection.this);
		alertDialog.setTitle("Information");
		alertDialog.setIcon(R.drawable.tick);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("OK",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.setMessage(message);
		////alertDialog.show();
		AlertDialog alert = alertDialog.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	// to set current date
	private void currentDate() {
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
