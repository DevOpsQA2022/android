package com.mobilesalesperson.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhPayment01;
import com.mobilesalesperson.model.HhReceipt01;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;

/**
 * @author TISN class to used for receipt page background processing
 */
public class ReceiptInvoiceListAdapter extends ArrayAdapter<HhPayment01> {

	private final List<HhPayment01> list;
	private final Activity context;

	private final EditText edtReciptAmt;
	private final TextView unapplyamt;
	private final ListView lstReciptItems;
	private double unAmount;
	private Supporter supporter;
	private MspDBHelper dbHelper;
	private ToastMessage toastMsg;

	private SharedPreferences prefs;

	static class ViewHolder {
		protected TextView txtReceiptApply1;
		protected TextView txtReceiptDocNo;
		protected TextView txtReceiptApplyAmt;
		protected TextView txtReceiptPendingAmt;
		protected TextView txtReceiptNetAmt;

	}

	public ReceiptInvoiceListAdapter(Activity context, List<HhPayment01> list,
			EditText edtReciptAmt, TextView txtReciptUnApplied,
			ListView lstReciptItems) {
		super(context, R.layout.receipt_list_items, list);
		this.context = context;
		this.list = list;
		this.edtReciptAmt = edtReciptAmt;
		this.unapplyamt = txtReciptUnApplied;
		this.lstReciptItems = lstReciptItems;
		dbHelper = new MspDBHelper(context);
		supporter = new Supporter(context, dbHelper);
		toastMsg = new ToastMessage();

		prefs = context.getSharedPreferences("prefReceiptList",
				Context.MODE_PRIVATE);
		supporter.clearPreference(prefs); // to clean preference
	}

	/** Sort document number ascending */
	public void sortByDocumentNumberAsc() {
		Comparator<HhPayment01> comparator = new Comparator<HhPayment01>() {

			@Override
			public int compare(HhPayment01 object1, HhPayment01 object2) {
				return object1.getHhPayment_documentnumber()
						.compareToIgnoreCase(
								object2.getHhPayment_documentnumber());
			}
		};
		Collections.sort(list, comparator);
		notifyDataSetChanged();
	}

	/** Sort document number descending */
	public void sortByDocumentNumberDesc() {
		Comparator<HhPayment01> comparator = new Comparator<HhPayment01>() {

			@Override
			public int compare(HhPayment01 object1, HhPayment01 object2) {
				return object2.getHhPayment_documentnumber()
						.compareToIgnoreCase(
								object1.getHhPayment_documentnumber());
			}
		};
		Collections.sort(list, comparator);
		notifyDataSetChanged();
	}

	/** Sort pending amount ascending */
	public void sortByPendingAscAmount() {
		Comparator<HhPayment01> comparator = new Comparator<HhPayment01>() {

			@Override
			public int compare(HhPayment01 object1, HhPayment01 object2) {
				return ((Float) object1.getHhPayment_pendingbalance())
						.compareTo((Float) object2
								.getHhPayment_pendingbalance());
			}
		};
		Collections.sort(list, comparator);
		notifyDataSetChanged();
	}

	/** Sort pending amount descending */
	public void sortByPendingDescAmount() {
		Comparator<HhPayment01> comparator = new Comparator<HhPayment01>() {

			@Override
			public int compare(HhPayment01 object1, HhPayment01 object2) {
				return ((Float) object2.getHhPayment_pendingbalance())
						.compareTo((Float) object1
								.getHhPayment_pendingbalance());
			}
		};
		Collections.sort(list, comparator);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		final String strYes = "Yes";
		final String strNo = "No";
		final String strPending = "Pending";
		final int pos = position;

		if (convertView == null) {

			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.receipt_list_items, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtReceiptApply1 = (TextView) view
					.findViewById(R.id.txt_ReceiptApply1);
			viewHolder.txtReceiptDocNo = (TextView) view
					.findViewById(R.id.txt_ReceiptDocNo);
			viewHolder.txtReceiptApplyAmt = (TextView) view
					.findViewById(R.id.txt_ReceiptApplyAmt);
			viewHolder.txtReceiptPendingAmt = (TextView) view
					.findViewById(R.id.txt_ReceiptPendingAmt);
			viewHolder.txtReceiptNetAmt = (TextView) view
					.findViewById(R.id.txt_ReceiptNetAmt);

			// Apply Yes/No Click methods
			viewHolder.txtReceiptApply1
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							int lstPos = lstReciptItems
									.getPositionForView(viewHolder.txtReceiptApply1);

							String docNumb = viewHolder.txtReceiptDocNo
									.getText().toString();
							doApply(strYes, strNo, strPending, viewHolder,
									docNumb, lstPos);
						}

					});

			// Apply Yes/No Click methods
			viewHolder.txtReceiptDocNo
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							int lstPos = lstReciptItems
									.getPositionForView(viewHolder.txtReceiptDocNo);

							String docNumb = viewHolder.txtReceiptDocNo
									.getText().toString();
							doApply(strYes, strNo, strPending, viewHolder,
									docNumb, lstPos);
						}

					});

			// Enter amount for Applied amount field
			viewHolder.txtReceiptApplyAmt
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							try {

								String txt = viewHolder.txtReceiptApply1
										.getText().toString();

								final int lstPos = lstReciptItems
										.getPositionForView(viewHolder.txtReceiptApplyAmt);

								if (txt.equals(strYes)) {
									final Dialog dialog = new Dialog(context);
									dialog.setContentView(R.layout.receipt_dialog_layout);
									dialog.setCancelable(false);
									dialog.setTitle("Enter Amount");
									dialog.show();

									final EditText edtDlgAmt = (EditText) dialog
											.findViewById(R.id.edtTxt_applyAmt);
									Button btnOk = (Button) dialog
											.findViewById(R.id.btn_dlgOk);
									String strRecApplyAmt = viewHolder.txtReceiptApplyAmt
											.getText().toString();
									final double tempRecApplyAmt = Double
											.parseDouble(strRecApplyAmt);

									final String strRecPendAmt = viewHolder.txtReceiptPendingAmt
											.getText().toString();
									final double tempRecPendAmt = Double
											.parseDouble(strRecPendAmt);

									edtDlgAmt.setText("" + tempRecApplyAmt);

									Map<Integer, HhReceipt01> receiptList = supporter
											.getPrefReceiptList();

									final int receiptsListSize = receiptList
											.size();
									final String docNumb = viewHolder.txtReceiptDocNo
											.getText().toString();
									// dialog ok button click event
									btnOk.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {

											String strDlgAmt = edtDlgAmt
													.getText().toString();

											if (!strDlgAmt.equals("")) {

												double dlgAmt = Double
														.parseDouble(strDlgAmt);
												String strDlgAmtCurFormat = supporter
														.getCurrencyFormat(dlgAmt);

												unAmount = Double
														.parseDouble(unapplyamt
																.getText()
																.toString());
												double posibleAppAmt = unAmount
														+ tempRecApplyAmt;
												if (dlgAmt == 0) {
													toastMsg.showToast(context,
															"Amount should be greater than zero");
													dialog.setCancelable(false);
												} else if (receiptsListSize == 1) {
													String strReceiptAmt = edtReciptAmt
															.getText()
															.toString();
													double totalRecpAmt = Double
															.parseDouble(strReceiptAmt);
													if ((dlgAmt <= tempRecPendAmt)
															&& (dlgAmt <= totalRecpAmt)) {
														dialog.dismiss();
														// Receipt Applied
														// Amount getTag
														// method..
														HhPayment01 elementAppAmt = (HhPayment01) viewHolder.txtReceiptApplyAmt
																.getTag();
														viewHolder.txtReceiptApplyAmt
																.setText(strDlgAmtCurFormat);
														elementAppAmt
																.setHhPayment_appliedamount(strDlgAmtCurFormat);
														double tempUnApplyAmt = totalRecpAmt
																- dlgAmt;
														String unAmt = supporter
																.getCurrencyFormat(tempUnApplyAmt);

														unapplyamt
																.setText(unAmt);

														double netAmt1 = tempRecPendAmt
																- dlgAmt;
														String netAmt = supporter
																.getCurrencyFormat(netAmt1);

														// Receipt Net amount
														// getTag
														// method..
														HhPayment01 elementNetAmt = (HhPayment01) viewHolder.txtReceiptNetAmt
																.getTag();

														viewHolder.txtReceiptNetAmt
																.setText(netAmt);
														elementNetAmt
																.setHhPayment_netamount(Float
																		.parseFloat(netAmt));

														addReceiptToPrefList(
																lstPos,
																docNumb,
																strDlgAmtCurFormat,
																strRecPendAmt,
																netAmt); // to
																			// update
																			// the
																			// list
																			// of
																			// receipts
																			// in
																			// preference

													} else if (dlgAmt > tempRecPendAmt) {
														toastMsg.showToast(
																context,
																"Amount should not be greater than pending amount");
														dialog.setCancelable(false);
													} else {
														toastMsg.showToast(
																context,
																"Amount should not be greater than receipt amount");
														dialog.setCancelable(false);
													}

												} else if ((dlgAmt <= tempRecPendAmt)
														&& (dlgAmt <= posibleAppAmt)) {
													dialog.dismiss();
													// Receipt Applied Amount
													// getTag method..
													HhPayment01 elementAppAmt = (HhPayment01) viewHolder.txtReceiptApplyAmt
															.getTag();
													viewHolder.txtReceiptApplyAmt
															.setText(strDlgAmtCurFormat);
													elementAppAmt
															.setHhPayment_appliedamount(strDlgAmtCurFormat);

													double tempUnApplyAmt = posibleAppAmt
															- dlgAmt;
													String unAmt = supporter
															.getCurrencyFormat(tempUnApplyAmt);

													unapplyamt.setText(unAmt);

													double netAmt1 = tempRecPendAmt
															- dlgAmt;
													String netAmt = supporter
															.getCurrencyFormat(netAmt1);

													// Receipt Net amount getTag
													// method..
													HhPayment01 elementNetAmt = (HhPayment01) viewHolder.txtReceiptNetAmt
															.getTag();

													viewHolder.txtReceiptNetAmt
															.setText(netAmt);
													elementNetAmt
															.setHhPayment_netamount(Float
																	.parseFloat(netAmt));

													addReceiptToPrefList(
															lstPos, docNumb,
															strDlgAmtCurFormat,
															strRecPendAmt,
															netAmt); // to
																		// update
																		// the
																		// list
																		// of
																		// receipts
																		// in
																		// preference

												} else if (dlgAmt > tempRecPendAmt) {
													toastMsg.showToast(context,
															"Amount should not be greater than pending amount");
													dialog.setCancelable(false);
												} else if (dlgAmt > posibleAppAmt) {
													toastMsg.showToast(
															context,
															"Amount should not be greater than "
																	+ posibleAppAmt);
													dialog.setCancelable(false);
												}

											} else {
												toastMsg.showToast(context,
														"Enter amount");
												dialog.setCancelable(false);
											}

										}
									});
								}
							} catch (Exception e) {
								e.printStackTrace();
								LogfileCreator
										.appendLog("ReceiptInvoiceListAdapter(Applied Amount Click):"
												+ e.getMessage());
							}
						}
					});

			viewHolder.txtReceiptApply1.setTag(list.get(position));
			viewHolder.txtReceiptDocNo.setTag(list.get(position));
			viewHolder.txtReceiptApplyAmt.setTag(list.get(position));
			viewHolder.txtReceiptPendingAmt.setTag(list.get(position));
			viewHolder.txtReceiptNetAmt.setTag(list.get(position));

			view.setTag(viewHolder);

		} else {
			view = convertView;
			((ViewHolder) view.getTag()).txtReceiptApply1.setTag(list
					.get(position));
			((ViewHolder) view.getTag()).txtReceiptDocNo.setTag(list
					.get(position));
			((ViewHolder) view.getTag()).txtReceiptApplyAmt.setTag(list
					.get(position));
			((ViewHolder) view.getTag()).txtReceiptPendingAmt.setTag(list
					.get(position));
			((ViewHolder) view.getTag()).txtReceiptNetAmt.setTag(list
					.get(position));
		}

		// here only view the listview data's operation...
		final ViewHolder holder = (ViewHolder) view.getTag();

		double pendAmt = (double) list.get(position)
				.getHhPayment_pendingbalance();

		String pendingAmount = supporter.getCurrencyFormat(pendAmt);

		double netAmnt = (double) list.get(position).getHhPayment_netamount();
		String netAmount = supporter.getCurrencyFormat(netAmnt);

		holder.txtReceiptApply1.setText(""
				+ list.get(position).getHhPayment_apply1());
		holder.txtReceiptDocNo.setText(""
				+ list.get(position).getHhPayment_documentnumber());
		holder.txtReceiptApplyAmt.setText(""
				+ list.get(position).getHhPayment_appliedamount());
		holder.txtReceiptPendingAmt.setText(pendingAmount);
		holder.txtReceiptNetAmt.setText(netAmount);

		return view;

	}

	private void doApply(final String strYes, final String strNo,
			final String strPending, final ViewHolder viewHolder,
			String docNumb, int position) {

		try {
			String strReceiptAmt = edtReciptAmt.getText().toString();

			if (strReceiptAmt.equals("")) {
				toastMsg.showToast(context, "Enter Receipt Amount");
			} else {
				String txt = viewHolder.txtReceiptApply1.getText().toString();

				String strUnAppAmount = unapplyamt.getText().toString();
				double unAppAmount = Double.parseDouble(strUnAppAmount);

				if (txt.equals(strNo) || txt.equals(strPending)) { // for no or
																	// pending
					if (unAppAmount == 0) {
						toastMsg.showToast(context,
								"Receipt Amount already entered");
					} else {
						// Receipt Apply getTag method..
						HhPayment01 elementRecApp = (HhPayment01) viewHolder.txtReceiptApply1
								.getTag();

						viewHolder.txtReceiptApply1.setText(strYes);

						// to set editReceiptAmount focusable
						// false
						edtReciptAmt.setFocusable(false);

						elementRecApp.setHhPayment_apply1(strYes);

						// Receipt Applied Amount getTag
						// method..
						String strPendAmt = viewHolder.txtReceiptPendingAmt
								.getText().toString();
						double pendAmt = Double.parseDouble(strPendAmt);

						HhPayment01 elementApplyAmt = (HhPayment01) viewHolder.txtReceiptApplyAmt
								.getTag();

						// Receipt Net amount getTag method
						HhPayment01 elementNetAmt = (HhPayment01) viewHolder.txtReceiptNetAmt
								.getTag();

						if (unAppAmount > pendAmt) { // new changes
														// in
														// receipt
														// applyment
														// field...
							double newval = unAppAmount - pendAmt;

							String strngpApliedAmt = strPendAmt; // pending
																	// amount is
																	// applied
																	// amount
							viewHolder.txtReceiptApplyAmt
									.setText(strngpApliedAmt);
							elementApplyAmt
									.setHhPayment_appliedamount(strngpApliedAmt);

							String strNewVal = supporter
									.getCurrencyFormat(newval);
							unapplyamt.setText(strNewVal);
							unAppAmount = Double.parseDouble(unapplyamt
									.getText().toString());

							String strnetAmt = supporter.getCurrencyFormat(0); // net
																				// amount
																				// become
																				// zero

							viewHolder.txtReceiptNetAmt.setText(strnetAmt);

							elementNetAmt.setHhPayment_netamount(Float
									.parseFloat(strnetAmt));

							addReceiptToPrefList(position, docNumb,
									strngpApliedAmt, strPendAmt, strnetAmt); // pending
																				// and
																				// applied
																				// amount
																				// is
																				// same

						} else {

							viewHolder.txtReceiptApplyAmt
									.setText(strUnAppAmount);

							elementApplyAmt
									.setHhPayment_appliedamount(strUnAppAmount);

							double netAmt = pendAmt - unAppAmount;
							String strnetAmt = supporter
									.getCurrencyFormat(netAmt);

							unapplyamt.setText(supporter
									.getCurrencyFormat(0.00));
							unAppAmount = Double.parseDouble(unapplyamt
									.getText().toString());

							viewHolder.txtReceiptNetAmt.setText(strnetAmt);

							elementNetAmt.setHhPayment_netamount(Float
									.parseFloat(strnetAmt));

							addReceiptToPrefList(position, docNumb,
									strUnAppAmount, strPendAmt, strnetAmt); // here
																			// unapplied
																			// amount
																			// is
																			// same
																			// as
																			// pending
																			// and
																			// applied
																			// amount
						}
					}
				} else {
					// Receipt Apply getTag method..
					HhPayment01 elementRecApp = (HhPayment01) viewHolder.txtReceiptApply1
							.getTag();

					viewHolder.txtReceiptApply1.setText(strNo);

					// to set editReceiptAmount focusable true
					removeReceiptFromPref(position); // to remove the current
														// position from
														// preference list
					Map<Integer, HhReceipt01> receiptList = supporter
							.getPrefReceiptList();

					int receiptsListSize = receiptList.size();

					if (receiptsListSize == 0) {
						edtReciptAmt.setFocusableInTouchMode(true);
						edtReciptAmt.setFocusable(true);
					}

					elementRecApp.setHhPayment_apply1(strNo); // Above apply is
																// assigned as
																// no

					unAppAmount = Double.parseDouble(unapplyamt.getText()
							.toString());
					String applyAmt1 = viewHolder.txtReceiptApplyAmt.getText()
							.toString();
					double tempValue2 = Double.parseDouble(applyAmt1);

					double unApplyAmout = unAppAmount + tempValue2;
					unapplyamt.setText(unApplyAmout + "");

					String strAppAmt = supporter.getCurrencyFormat(0.00);
					viewHolder.txtReceiptApplyAmt.setText(strAppAmt);
					HhPayment01 elementAppAmt = (HhPayment01) viewHolder.txtReceiptApplyAmt
							.getTag(); // Receipt Applied Amount getTag method..
					elementAppAmt.setHhPayment_appliedamount(strAppAmt);

					String netAmpNew = viewHolder.txtReceiptPendingAmt
							.getText().toString(); // pending amount is assigned
													// to net amount
					// Receipt Net amount getTag method..
					HhPayment01 elementNetAmt = (HhPayment01) viewHolder.txtReceiptNetAmt
							.getTag();
					viewHolder.txtReceiptNetAmt.setText(netAmpNew);
					elementNetAmt.setHhPayment_netamount(Float
							.parseFloat(netAmpNew));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogfileCreator
					.appendLog("ReceiptInvoiceListAdapter(doApply method):"
							+ e.getMessage());
		}

	}

	private void addReceiptToPrefList(int position, String docNum,
			String appliedAmt, String pndAmt, String netAmount) {

		Map<Integer, HhReceipt01> receiptList = supporter.getPrefReceiptList();

		HhReceipt01 receipt = new HhReceipt01();

		receipt.setHhReceipt_apply1("Y");
		receipt.setHhReceipt_docnumber(docNum);
		receipt.setHhReceipt_pendingbal(pndAmt);
		receipt.setHhReceipt_appliedamount(Double.parseDouble(appliedAmt));

		receiptList.put(position, receipt); // it will automatically update if
											// key already present

		supporter.refreshPrefReceiptList(receiptList);
	}

	private void removeReceiptFromPref(int position) {
		Map<Integer, HhReceipt01> receiptList = supporter.getPrefReceiptList();
		receiptList.remove(position);
		supporter.refreshPrefReceiptList(receiptList);
	}
}
