package com.mobilesalesperson.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.mobilesalesperson.controller.AppBaseActivity;
import com.mobilesalesperson.controller.R;
import com.mobilesalesperson.database.MspDBHelper;
import com.mobilesalesperson.util.Supporter;
import com.mobilesalesperson.util.ThemeUtil;

public class HistoryView extends AppBaseActivity {
    private EditText edtTextHistorySearch;
    private ListView History_listView;
    private Supporter supporter;
    private MspDBHelper dbHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.history_layout);

        registerBaseActivityReceiver();


        edtTextHistorySearch = findViewById(R.id.edt_text_history_search);
        History_listView = findViewById(R.id.history_listview);

        dbHelper = new MspDBHelper(this);
        supporter = new Supporter(this,dbHelper);




    }
}
