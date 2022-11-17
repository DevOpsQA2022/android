package com.mobilesalesperson.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesalesperson.controller.MainMenu.ImageAdapter.ViewHolder;
import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.util.ActionItem;
import com.mobilesalesperson.util.QuickAction;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

/**
 * @author TIVM class to control menu page
 */
public class MainMenu extends AppBaseActivity {

	/** variable declarations */
	private Supporter supporter;
	private MspDBHelper dbHelper;
	private static ViewHolder vHoldMenu;
	private GridView grdViewMenu;
	private static int pos = 0;

	static final int MENU_INFO_DIALOG_ID = 0;
	private ListView dialog_ListView;
	private ListView menu_ListView;
	private String[] listContent;
	private Dialog dialog;
	private ImageView imgSelectView;
	private QuickAction orderAction, invoiceAction, customerAction,
			reportAction;
	private static final int ID_ORD = 1;
	private static final int ID_EDT = 2;
	private static final int ID_DELETE = 3;
	private static final int ID_HISTORY=4;
	private static final int ID_ORD_INV = 1;
	private static final int ID_SUMMARY = 2;
	private static final int ID_INVENTORY = 3;
	private static final int ID_RECIPTSUMMARY = 4;
	private static final int ID_RECIPTDETAIL = 5;
	private static final int ID_SALESUMMARY = 6;
	private static final int ID_LOCATION = 7;
	

	private static final int ID_ADDCUST = 1;
	private static final int ID_EDITCUST = 2;
	private ToastMessage toastMsg;
	private String cmpnyNo; // added for multicompany

	/**
	 * method to initialize the class activity
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.menu_layout);

		registerBaseActivityReceiver();

		dbHelper = new MspDBHelper(this);
		supporter = new Supporter(this, dbHelper);
		toastMsg = new ToastMessage();

		cmpnyNo = supporter.getCompanyNo();

		Log.i("MenuPage CompanyNo : ", cmpnyNo);

		dbHelper.openWritableDatabase();
		if (dbHelper.checkTempDataExist()) {

			dbHelper.deleteTempTableRecords();
			supporter.clearPreference(
					getSharedPreferences("shippingDetails",
							Context.MODE_PRIVATE),
					getSharedPreferences("entryDetails", Context.MODE_PRIVATE));

		}
		supporter.clearSignPreference(getSharedPreferences("signPref",
				Context.MODE_PRIVATE));
		dbHelper.closeDatabase();

		supporter.setMode("main");
		boolean amLogIn = supporter.isLogedIn();

		if (!amLogIn) {
			supporter.setLogedIn(true);
			doCheckExportData("login");
		}

		listContent = new String[5];
		listContent = loadInfoDialogStaticData(listContent);

		imgSelectView = (ImageView) findViewById(R.id.img_selectView);
		grdViewMenu = (GridView) findViewById(R.id.grd_Menu);
		menu_ListView = (ListView) findViewById(R.id.menuList);
		menu_ListView.setVisibility(View.GONE);
		imgSelectView.setImageResource(R.drawable.gridview);

		String viewName = supporter.getMainPageViewType();
		setViewMode(viewName);

		imgSelectView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String viewName = supporter.getMainPageViewType();

				if (viewName.equals("list")) {
					setViewMode("grid");
				} else if (viewName.equals("grid")) {
					setViewMode("list");
				}
			}

		});

		grdViewMenu.setAdapter(new ImageAdapter(this));

		grdViewMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				pos = position;
				eventPage(pos, v);
			}
		});

		// Menu ListView
		Menus menu_data[] = new Menus[] { new Menus(R.drawable.order_new, "Order"),
				new Menus(R.drawable.invs, "Invoice"),
				new Menus(R.drawable.rept, "Reciepts"),
				new Menus(R.drawable.cust, "Customer"),
				new Menus(R.drawable.reprt, "Report"),
				new Menus(R.drawable.seting, "Settings"),
				/*new Menus(R.drawable.themes, "Themes"),
				new Menus(R.drawable.currency_small, "CurrencyCounter")*/};

		MenuAdapter adapter = new MenuAdapter(this,
				R.layout.menu_listview_layout, menu_data);
		menu_ListView.setAdapter(adapter);

		menu_ListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				pos = position;
				eventPage(pos, v);
			}
		});

		/* Order Action Implemented */
		ActionItem createOrder = new ActionItem(ID_ORD, "Create",
				getResources().getDrawable(R.drawable.menu_ok));
		ActionItem edtOrder = new ActionItem(ID_EDT, "Edit", getResources()
				.getDrawable(R.drawable.edit));
		ActionItem deleteOrder = new ActionItem(ID_DELETE, "Delete",
				getResources().getDrawable(R.drawable.delete));
		ActionItem HistoryOrder = new ActionItem(ID_HISTORY,"history",
				getResources().getDrawable(R.drawable.recipt_detail));

		// use setSticky(true) to disable QuickAction dialog being dismissed
		// after an item is clicked
		createOrder.setSticky(true);
		edtOrder.setSticky(true);
		deleteOrder.setSticky(true);
		HistoryOrder.setSticky(true);

		// create QuickAction. Use QuickAction.VERTICAL or
		// QuickAction.HORIZONTAL param to define layout
		orderAction = new QuickAction(this, QuickAction.VERTICAL);

		// add action items into QuickAction
		orderAction.addActionItem(createOrder);
		orderAction.addActionItem(edtOrder);
		orderAction.addActionItem(deleteOrder);
		orderAction.addActionItem(HistoryOrder);

		// Set listener for order action item clicked
		orderAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if (actionId == ID_ORD) {

							supporter.setMode("oe");

							supporter.simpleNavigateTo(CustomerSelection.class);

							orderAction.dismiss();

						} else if (actionId == ID_EDT) {
							supporter.setMode("orderEdit");

							supporter.simpleNavigateTo(OrderSelection.class);

							orderAction.dismiss();
						} else if (actionId == ID_DELETE) {
							supporter.setMode("orderDelete");

							supporter.simpleNavigateTo(OrderSelection.class);

							orderAction.dismiss();

						}else if(actionId == ID_HISTORY){
							supporter.setMode("he");
							supporter.simpleNavigateTo(CustomerSelection.class);
							orderAction.dismiss();
						}
					}
				});

		/* Invoice Action Implemented */
		ActionItem createInvoice = new ActionItem(ID_ORD, "Create",
				getResources().getDrawable(R.drawable.menu_ok));
		ActionItem edtInvoice = new ActionItem(ID_EDT, "Edit", getResources()
				.getDrawable(R.drawable.edit));
		ActionItem deleteInvoice = new ActionItem(ID_DELETE, "Delete",
				getResources().getDrawable(R.drawable.delete));

		// use setSticky(true) to disable QuickAction dialog being dismissed
		// after an item is clicked
		createInvoice.setSticky(true);
		edtInvoice.setSticky(true);
		deleteInvoice.setSticky(true);

		// create QuickAction. Use QuickAction.VERTICAL or
		// QuickAction.HORIZONTAL param to define layout
		invoiceAction = new QuickAction(this, QuickAction.VERTICAL);

		// add action items into QuickAction
		invoiceAction.addActionItem(createInvoice);
		invoiceAction.addActionItem(edtInvoice);
		invoiceAction.addActionItem(deleteInvoice);

		// Set listener for invoice action item clicked
		invoiceAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if (actionId == ID_ORD) {

							supporter.setMode("ie");

							supporter.simpleNavigateTo(CustomerSelection.class);

							invoiceAction.dismiss();

						} else if (actionId == ID_EDT) {
							supporter.setMode("invoiceEdit");

							supporter.simpleNavigateTo(OrderSelection.class);

							invoiceAction.dismiss();
						} else if (actionId == ID_DELETE) {
							supporter.setMode("invoiceDelete");

							supporter.simpleNavigateTo(OrderSelection.class);

							invoiceAction.dismiss();
						}
					}
				});

		/* Report Action Implemented */
		ActionItem viewOrdInvReport = new ActionItem(ID_ORD_INV,
				"Order / Invoice", getResources().getDrawable(
						R.drawable.order_invoice));
		/*ActionItem viewSummary = new ActionItem(ID_SUMMARY, "Summary",
				getResources().getDrawable(R.drawable.summary));*/
		ActionItem viewInventory = new ActionItem(ID_INVENTORY, "Inventory",
				getResources().getDrawable(R.drawable.inventory_report));

		ActionItem viewReciptSummary = new ActionItem(ID_RECIPTSUMMARY,
				"Receipt Summary", getResources().getDrawable(
						R.drawable.recipt_summary));
		ActionItem viewReciptDetail = new ActionItem(ID_RECIPTDETAIL,
				"Receipt Details", getResources().getDrawable(
						R.drawable.recipt_detail));
		ActionItem viewSalesSummary = new ActionItem(ID_SALESUMMARY,
				"Sales Summary", getResources().getDrawable(
						R.drawable.sales_summary));
		/*ActionItem viewMap = new ActionItem(ID_LOCATION,
				"Locations Visit", getResources().getDrawable(
						R.drawable.map));*/

		// use setSticky(true) to disable QuickAction dialog being dismissed
		// after an item is clicked
		viewOrdInvReport.setSticky(true);
		//viewSummary.setSticky(true);
		viewInventory.setSticky(true);
		viewReciptSummary.setSticky(true);
		viewReciptDetail.setSticky(true);
		viewSalesSummary.setSticky(true);
		//viewMap.setSticky(true);
		// create QuickAction. Use QuickAction.VERTICAL or
		// QuickAction.HORIZONTAL param to define layout
		reportAction = new QuickAction(this, QuickAction.VERTICAL);

		// add action items into QuickAction
		reportAction.addActionItem(viewOrdInvReport);
		//reportAction.addActionItem(viewSummary);
		reportAction.addActionItem(viewInventory);

		reportAction.addActionItem(viewReciptSummary);
		reportAction.addActionItem(viewReciptDetail);
		reportAction.addActionItem(viewSalesSummary);
		//reportAction.addActionItem(viewMap);
		// Set listener for invoice action item clicked
		reportAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if (actionId == ID_ORD_INV) {
							supporter.setMode("report");
							supporter.simpleNavigateTo(OrderSelection.class);
							reportAction.dismiss();
						} /*else if (actionId == ID_SUMMARY) {
							supporter
									.simpleNavigateTo(TransactionSummary.class);
							reportAction.dismiss();
						}*/ else if (actionId == ID_INVENTORY) {
							supporter.simpleNavigateTo(Inventory.class);
							reportAction.dismiss();
						} else if (actionId == ID_RECIPTSUMMARY) {
							supporter.simpleNavigateTo(ReceiptSummary.class);
							reportAction.dismiss();
						} else if (actionId == ID_RECIPTDETAIL) {
							supporter.simpleNavigateTo(ReceiptSelection.class);
							reportAction.dismiss();
						} else if (actionId == ID_SALESUMMARY) {
							supporter.simpleNavigateTo(SalesSummary.class);
							reportAction.dismiss();
						} 
						 /*else if (actionId == ID_LOCATION) {
								supporter.simpleNavigateTo(DisplayLocation.class);
								reportAction.dismiss();
							} */
							
						else {
							toastMsg.showToast(MainMenu.this,
									"Let's do some search action");
						}
					}
				});

		/* Customer Action implemented */
		ActionItem addCust = new ActionItem(ID_ORD_INV, "Add Customer",
				getResources().getDrawable(R.drawable.menu_ok));
		ActionItem editdeletCust = new ActionItem(ID_SUMMARY, "Edit / Delete",
				getResources().getDrawable(R.drawable.edituser));

		// use setSticky(true) to disable QuickAction dialog being dismissed
		// after an item is clicked
		addCust.setSticky(true);
		editdeletCust.setSticky(true);

		// create QuickAction. Use QuickAction.VERTICAL or
		// QuickAction.HORIZONTAL param to define layout
		customerAction = new QuickAction(this, QuickAction.VERTICAL);

		// add action items into QuickAction
		customerAction.addActionItem(addCust);
		customerAction.addActionItem(editdeletCust);

		// Set listener for invoice action item clicked
		customerAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {

						dbHelper.openReadableDatabase();
						HhManager manager = dbHelper.getManagerData(
								supporter.getSalesPerson(), cmpnyNo);
						dbHelper.closeDatabase();

						int addCustPermission = manager
								.getHhManager_addcustomerstate();

						if (addCustPermission == 1) {
							if (actionId == ID_ADDCUST) {
								supporter.setMode("addcust");
								supporter.simpleNavigateTo(AddCustomer.class);
								customerAction.dismiss();
							} else if (actionId == ID_EDITCUST) {
								supporter.setMode("editdeletcust");
								supporter
										.simpleNavigateTo(CustomerSelection.class);
								customerAction.dismiss();
							} else {
								toastMsg.showToast(MainMenu.this,
										"Let's do some search action");
							}
						} else {
							toastMsg.showToast(MainMenu.this,
									"You are not allowed to add or edit customer");
						}
					}
				});

	}// end of oncreate...

	private void setViewMode(String imgName) {

		if (imgName.equals("list")) {
			imgSelectView.setImageResource(R.drawable.listview);
			grdViewMenu.setVisibility(View.GONE);
			menu_ListView.setVisibility(View.VISIBLE);
			supporter.setMainPageViewType("list");
		} else if (imgName.equals("grid")) {
			imgSelectView.setImageResource(R.drawable.gridview);
			grdViewMenu.setVisibility(View.VISIBLE);
			menu_ListView.setVisibility(View.GONE);
			supporter.setMainPageViewType("grid");
		}
	}

	public String[] loadInfoDialogStaticData(String[] lstStr) {
		lstStr[0] = "Company";
		lstStr[1] = "Customer";
		lstStr[2] = "Item";
		lstStr[3] = "Sales Person";
		lstStr[4] = "Change Company";
		return lstStr;
	}

	protected void eventPage(int pos2, View v) {
		if (pos2 == 0) {

			orderAction.show(v);

		} else if (pos2 == 1) {
			invoiceAction.show(v);
		} else if (pos2 == 2) {
			supporter.setMode("recipt");
			supporter.simpleNavigateTo(CustomerSelection.class);
		} else if (pos2 == 3) {
			customerAction.show(v);
		} else if (pos2 == 4) {
			reportAction.show(v);
		} else if (pos2 == 5) {
			supporter.simpleNavigateTo(Settings.class);
		} /*else if (pos2 == 6) {
			supporter.simpleNavigateTo(Aboutus.class);
		}*/ /*else if (pos2 == 6) {
			supporter.simpleNavigateTo(Themes.class);
		} else if(pos2 == 7) {
			supporter.simpleNavigateTo(CurrencyCounter.class);
		}*/

	}

	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater mLayoutInflater;

		public Integer[] mThumbIds = { R.drawable.order, R.drawable.invoice,
				R.drawable.receipts, R.drawable.newcustomer, R.drawable.newreport,
				R.drawable.newsettings,  /*R.drawable.theme,
				R.drawable.currency*/

		};

		public ImageAdapter(Context c) {
			mLayoutInflater = LayoutInflater.from(c);
		}

		public int getCount() {
			return Menus.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {

				convertView = mLayoutInflater.inflate(R.layout.customgrid,
						parent, false);
				vHoldMenu = new ViewHolder();

				vHoldMenu.mImageView = (ImageView) convertView
						.findViewById(R.id.imgview);
				vHoldMenu.mTextView = (TextView) convertView
						.findViewById(R.id.text);
				vHoldMenu.mImageView
						.setScaleType(ImageView.ScaleType.CENTER_CROP);
				/*
				 * vHoldMenu.mImageView.setMaxHeight(65);
				 * vHoldMenu.mImageView.setMaxWidth(75);
				 */
				vHoldMenu.mImageView.setPadding(6, 6, 6, 6);

				convertView.setTag(vHoldMenu);
			} else {
				vHoldMenu = (ViewHolder) convertView.getTag();
			}
			//vHoldMenu.mTextView.setText(Menus[position]);
			vHoldMenu.mImageView.setImageResource(mThumbIds[position]);
			return convertView;
		}

		class ViewHolder {
			ImageView mImageView;
			TextView mTextView;
		}

		final String[] Menus = new String[] { "Order", "Invoice", "Receipts",
				"Customer", "Report", "Settings" /*"Themes", "Currency Counter"*/ };

	}

	// method to control menu options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu_menu, menu);
		return true;
	}

	// method to control menu options
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_mnu_export:
			supporter.simpleNavigateTo(Export.class);
			break;

		case R.id.main_mnu_info:
			showDialog(MENU_INFO_DIALOG_ID);
			break;

		case R.id.main_mnu_exit:

			logoutAlert();

			break;

		}
		return true;
	}

	/* Info Dialog creation */
	@Override
	protected Dialog onCreateDialog(int id) {

		dialog = null;

		switch (id) {
		case MENU_INFO_DIALOG_ID:
			dialog = new Dialog(MainMenu.this);
			dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
			dialog.setContentView(R.layout.info_layout);
			dialog.setTitle("Information");
			dialog.setCancelable(true);
			dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
					R.drawable.dlg_info);
			dialog.setCanceledOnTouchOutside(true);

			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});

			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
				}
			});

			// Prepare ListView in dialog
			dialog_ListView = (ListView) dialog.findViewById(R.id.lst_Dialog);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.drawable.dialog_list_item, R.id.txt_Dlg_ListDetails,
					listContent);

			dialog_ListView.setAdapter(adapter);
			dialog_ListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					if (position == 0) {

						supporter.simpleNavigateTo(CompanyInfo.class);

					} else if (position == 1) {

						supporter.simpleNavigateTo(CustomerSelection.class);
						supporter.setMode("view");

					} else if (position == 2) {

						supporter.simpleNavigateTo(ItemSelection.class);
						supporter.setMode("view");

					} else if (position == 3) {

						supporter.simpleNavigateTo(SalesPersonInfo.class);

					} else if (position == 4) {
						supporter.simpleNavigateTo(Login.class);
						supporter.clearCommonPreference();
						supporter.setLogedIn(false);
					}

					dialog.dismiss();
				}
			});

			break;
		}

		return dialog;
	}

	// back button click event
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			logoutAlert();
		}
		return super.onKeyDown(keyCode, event);
	}

	// to check export data availability
	public void doCheckExportData(final String event) {
		dbHelper.openReadableDatabase();
		boolean isExpDataAvail = dbHelper.isDataAvailableForPost(cmpnyNo);
		dbHelper.closeDatabase();

		if (isExpDataAvail) {
			final AlertDialog.Builder alertExport = new AlertDialog.Builder(
					MainMenu.this);
			alertExport.setTitle("Confirmation");
			alertExport.setIcon(R.drawable.warning);
			alertExport.setCancelable(false);
			alertExport.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							supporter.simpleNavigateTo(Export.class);
						}
					});

			alertExport.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogExp, int which) {

							if (event.equals("logout")) {
								supporter.simpleNavigateTo(Login.class);
								supporter.clearCommonPreference();
								supporter.setLogedIn(false);
							}

							dialogExp.dismiss();
						}
					});
			alertExport
					.setMessage("Do you want to export the orders now?");
			//alertExport.show();
			AlertDialog alert = alertExport.create();
			alert.show();
			alert.getWindow().getAttributes();
			TextView textView = (TextView) alert.findViewById(android.R.id.message);
			textView.setTextSize(29);
		} else {
			if (event.equals("logout")) {
				logoutAlert();
			}
		}

	}

	private void logoutAlert() {
		AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
		alertUser.setTitle("Confirmation");
		alertUser.setIcon(R.drawable.warning);
		alertUser.setCancelable(false);
		alertUser.setMessage("Do you want to logout?");
		alertUser.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						supporter.simpleNavigateTo(Login.class);
						supporter.clearCommonPreference();
						supporter.setLogedIn(false);
					}
				});

		alertUser.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

			//alertUser.show();
		AlertDialog alert = alertUser.create();
		alert.show();
		alert.getWindow().getAttributes();
		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(29);
	}

	// Menus Class
	public class Menus {
		public int icon;
		public String title;

		public Menus() {
			super();
		}

		public Menus(int icon, String title) {
			super();
			this.icon = icon;
			this.title = title;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
