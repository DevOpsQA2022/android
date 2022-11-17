package com.mobilesalesperson.controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.model.HhHistory01;
import com.mobilesalesperson.model.TempItem;
import com.mobilesalesperson.util.Supporter;

import java.util.List;

public class HistoryItemListAdapter extends ArrayAdapter<HhHistory01> {

    private final List<HhHistory01> list;
    private final Activity context;
    private Supporter supporter;
    private MspDBHelper dbHelper;
    private HistoryItemListAdapter hAdapter;

    static class ViewHolder {
        protected TextView itmNum;
        protected TextView itmQty;
        protected TextView itmExtPrice;
        protected TextView itmLoc;
        protected TextView itmUom;

    }

    public HistoryItemListAdapter(Activity context, List<HhHistory01> list) {
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
            final HistoryItemListAdapter.ViewHolder viewHolder = new HistoryItemListAdapter.ViewHolder();
            viewHolder.itmNum = (TextView) view.findViewById(R.id.txt_TransListNum);
            viewHolder.itmQty = (TextView) view.findViewById(R.id.txt_TransListQty);
            viewHolder.itmExtPrice = (TextView) view.findViewById(R.id.txt_TransListExtPrc);
            viewHolder.itmLoc = (TextView) view.findViewById(R.id.txt_TransListLoc);
            viewHolder.itmUom = (TextView) view.findViewById(R.id.txt_TransListUOM);
            view.setTag(viewHolder);

        } else {
            view = convertView;
        }

        String entryType = list.get(position).getHhTran_transType_new();
        int strQty = 0;
        strQty = list.get(position).getHhTran_qty_new();

        HistoryItemListAdapter.ViewHolder holder = (HistoryItemListAdapter.ViewHolder) view.getTag();

        holder.itmNum.setText(list.get(position).getHhTran_itemNumber_new());
        holder.itmQty.setText(String.valueOf(strQty));


        holder.itmExtPrice.setText(String.valueOf(list.get(position).getHhTran_extPrice_new()));
        holder.itmLoc.setText(list.get(position).getHhTran_locId_new());
        holder.itmUom.setText(list.get(position).getHhTran_uom_new());
        return view;
    }

}

