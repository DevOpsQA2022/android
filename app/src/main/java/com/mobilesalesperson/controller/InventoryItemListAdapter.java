package com.mobilesalesperson.controller;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.util.Supporter;

/**
 * @author T.SARAVANAN class to used to get the List of Inventory Items based on
 *         Location
 */

public class InventoryItemListAdapter extends ArrayAdapter<HhItem01> {

	private final List<HhItem01> list;
	private final Activity context;
	private String priceList;
	private String location;
	private MspDBHelper dbHelper;
	private Supporter supporter;

	static class ViewHolder {
		protected TextView txtInventItemSno;
		protected TextView txtInventItemNo;
		protected TextView txtInventItemDesc;
		protected TextView txtInventItemQtyHand;
		protected TextView txtInventItemPrice;

	}

	public InventoryItemListAdapter(Activity context, List<HhItem01> list,
			String priceList, String loc, MspDBHelper dbHelper) {
		super(context, R.layout.inventory_list_items, list);
		this.context = context;
		this.list = list;
		this.priceList = priceList;
		this.location = loc;
		this.dbHelper = dbHelper;
		this.supporter = new Supporter(context, dbHelper);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.inventory_list_items, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtInventItemSno = (TextView) view
					.findViewById(R.id.txt_InventorySerialNum);
			viewHolder.txtInventItemNo = (TextView) view
					.findViewById(R.id.txt_InventoryListItemNo);
			viewHolder.txtInventItemDesc = (TextView) view
					.findViewById(R.id.txt_InventoryListItemDesc);
			viewHolder.txtInventItemQtyHand = (TextView) view
					.findViewById(R.id.txt_InventoryListItemQty);
			viewHolder.txtInventItemPrice = (TextView) view
					.findViewById(R.id.txt_InventoryListItemPrice);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		final ViewHolder holder = (ViewHolder) view.getTag();

		// 10-04-13 scrolling price issue fixed by TISN
		String itemNo = list.get(position).getHhItem_number();

		dbHelper.openReadableDatabase();
		double price = dbHelper.getCurrentPrice(location, priceList, itemNo,
				supporter.getCompanyNo());
		dbHelper.closeDatabase();
		Supporter supporter = new Supporter(context, dbHelper);
		String itmPrz = supporter.getCurrencyFormat(price);
		double qty = list.get(position).getHhItem_qty_on_hand();
		String qtyHand = supporter.getQtyFormat(qty);

		holder.txtInventItemSno.setText((position + 1) + "");
		holder.txtInventItemNo.setText("" + itemNo);
		holder.txtInventItemDesc.setText(""
				+ list.get(position).getHhItem_description());
		holder.txtInventItemQtyHand.setText(qtyHand);
		holder.txtInventItemPrice.setText(itmPrz);
		return view;
	}
}
