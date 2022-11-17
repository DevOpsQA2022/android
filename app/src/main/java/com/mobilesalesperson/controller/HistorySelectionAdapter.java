package com.mobilesalesperson.controller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhHistory01;

import java.util.ArrayList;
import java.util.List;

public class HistorySelectionAdapter extends ArrayAdapter<HhHistory01> implements Filterable {

    private final List<HhHistory01> list;
    private final Activity context;

    private List<HhHistory01> allModelItemsArray;
    private List<HhHistory01> filteredModelItemsArray;
    private ModelFilter filter;
    private LayoutInflater inflator;

    public HistorySelectionAdapter(Activity context1, List<HhHistory01> list) {
        super(context1, R.layout.single_list_item, list);
        this.list = list;
        this.context = context1;
        this.allModelItemsArray = new ArrayList<HhHistory01>(list);
        this.filteredModelItemsArray = new ArrayList<HhHistory01>(allModelItemsArray);
        inflator = context1.getLayoutInflater();
        getFilter();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ModelFilter();
        }
        return filter;
    }

    static class ViewHolder {
        protected TextView number;
        protected TextView name;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.single_list_item, null);
            final HistorySelectionAdapter.ViewHolder viewHolder = new HistorySelectionAdapter.ViewHolder();
            viewHolder.number = (TextView) view.findViewById(R.id.txt_Number);
            viewHolder.name = (TextView) view.findViewById(R.id.txt_Name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        HistorySelectionAdapter.ViewHolder holder = (HistorySelectionAdapter.ViewHolder) view.getTag();
        holder.number.setText(list.get(position).getHhTran_customerNumber_new());
        holder.name.setText(list.get(position).getHhTran_referenceNumber_new());
        return view;
    }

    private class ModelFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<HhHistory01> filteredItems = new ArrayList<>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    HhHistory01 history01 = allModelItemsArray.get(i);
                    String strNum = history01.getHhTran_customerNumber_new();
                    String strName = history01.getHhTran_referenceNumber_new();
                    if (strNum.toLowerCase().contains(constraint) || strName.toLowerCase().contains(constraint))
                        filteredItems.add(history01);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = allModelItemsArray;
                    result.count = allModelItemsArray.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            filteredModelItemsArray = (ArrayList<HhHistory01>) filterResults.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }
}