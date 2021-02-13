package com.example.averiados;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.R.id;
import in.galaxyofandroid.spinerdialog.R.layout;
import java.util.ArrayList;

public class SpinnerDialogCustom {
    ArrayList<String> items;
    Activity context;
    String dTitle;
    OnSpinerItemClick onSpinerItemClick;
    AlertDialog alertDialog;
    int pos;
    int style;

    public SpinnerDialogCustom(Activity activity, ArrayList<String> items, String dialogTitle) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
    }

    public SpinnerDialogCustom(Activity activity, ArrayList<String> items, String dialogTitle, int style) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.style = style;
    }

    public void bindOnSpinerListener(OnSpinerItemClick onSpinerItemClick1) {
        this.onSpinerItemClick = onSpinerItemClick1;
    }

    public void showSpinerDialog() {
        Builder adb = new Builder(this.context);
        View v = this.context.getLayoutInflater().inflate(layout.dialog_layout, (ViewGroup)null);
        TextView rippleViewClose = (TextView)v.findViewById(id.close);
        rippleViewClose.setText("CERRAR");
        TextView title = (TextView)v.findViewById(id.spinerTitle);
        title.setText(this.dTitle);
        ListView listView = (ListView)v.findViewById(id.list);
        final EditText searchBox = (EditText)v.findViewById(id.searchBox);
        final ArrayAdapter<String> adapter = new ArrayAdapter(this.context, layout.items_view, this.items);
        listView.setAdapter(adapter);
        adb.setView(v);
        this.alertDialog = adb.create();
        this.alertDialog.getWindow().getAttributes().windowAnimations = this.style;
        this.alertDialog.setCancelable(false);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t = (TextView)view.findViewById(id.text1);

                for(int j = 0; j <  com.example.averiados.SpinnerDialogCustom.this.items.size(); ++j) {
                    if (t.getText().toString().equalsIgnoreCase(((String)  com.example.averiados.SpinnerDialogCustom.this.items.get(j)).toString())) {
                        com.example.averiados.SpinnerDialogCustom.this.pos = j;
                    }
                }

                com.example.averiados.SpinnerDialogCustom.this.onSpinerItemClick.onClick(t.getText().toString(),  com.example.averiados.SpinnerDialogCustom.this.pos);
                com.example.averiados.SpinnerDialogCustom.this.alertDialog.dismiss();
            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(searchBox.getText().toString());
            }
        });
        rippleViewClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                com.example.averiados.SpinnerDialogCustom.this.alertDialog.dismiss();
            }
        });
        this.alertDialog.show();
    }
}
