package com.mobilesalesperson.controller;

import java.util.List;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.Supporter;

public class TransItemListAdapter extends ArrayAdapter<TempItem> {

	private final List<TempItem> list;
	private final Activity context;
	private Supporter supporter;
	private MspDBHelper dbHelper;

	static class ViewHolder {
		protected TextView itmNum;
		protected EditText itmQty;
		protected TextView itmExtPrice;
		protected TextView itmLoc;
		protected TextView itmUom;

	}

	public TransItemListAdapter(Activity context, List<TempItem> list) {
		super(context, R.layout.transaction_order_item_list, list);
		this.context = context;
		this.list = list;
		dbHelper = new MspDBHelper(context);
		supporter = new Supporter(context, dbHelper);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.transaction_order_item_list, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.itmNum = (TextView) view
					.findViewById(R.id.txt_TransListNum);
			viewHolder.itmQty = (EditText) view
					.findViewById(R.id.txt_TransListQty);
			viewHolder.itmExtPrice = (TextView) view
					.findViewById(R.id.txt_TransListExtPrc);
			viewHolder.itmLoc = (TextView) view
					.findViewById(R.id.txt_TransListLoc);
			viewHolder.itmUom = (TextView) view
					.findViewById(R.id.txt_TransListUOM);
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		String entryType = list.get(position).getTemp_entryType();

		String strQty = list.get(position).getTemp_qty();

		if (entryType.equals("tr")) {
			strQty = "-" + strQty;
		}

		ViewHolder holder = (ViewHolder) view.getTag();

		holder.itmNum.setText("" + list.get(position).getTemp_itemNum());



		holder.itmQty.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {




			}

			@Override
			public void afterTextChanged(Editable editable) {

				String txt_qty = holder.itmQty.getText().toString().trim();

				dbHelper.openWritableDatabase();
				TempItem temp = new TempItem();
				String txt_itmno = list.get(position).getTemp_itemNum();
				String oldTem = list.get(position).getTemp_qty();
  				  if(txt_qty.equals("1")){
					/*  Toast.makeText(context,
							  "not updated",
							  Toast.LENGTH_SHORT).show();*/
					}else{

					  dbHelper.updateQty(txt_qty, oldTem, txt_itmno);
					/*  Toast.makeText(context,
							  "updated",
							  Toast.LENGTH_SHORT).show();*/



				  }
				dbHelper.closeDatabase();

			}
		});


		holder.itmQty.setText(strQty);

		double extPrz = Double.parseDouble(list.get(position)
				.getTemp_extPrice());
		// double disc =
		// Double.parseDouble(list.get(position).getTemp_discount());
		// double extPrz = extPrz1-disc;
		String extPrize = supporter.getCurrencyFormat(extPrz);

		holder.itmExtPrice.setText(extPrize);
		holder.itmLoc.setText(list.get(position).getTemp_location());
		holder.itmUom.setText(list.get(position).getTemp_uom());
		return view;
	}

}
