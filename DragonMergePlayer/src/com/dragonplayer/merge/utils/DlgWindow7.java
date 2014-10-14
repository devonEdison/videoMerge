package com.dragonplayer.merge.utils;


import com.dragonplayer.merge.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DlgWindow7 extends Dialog {

    Context context;
    String mBtnText1;
    android.view.View.OnClickListener mListener1;
    
    public DlgWindow7(Context context, String btnText1, android.view.View.OnClickListener listener1) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        mBtnText1 = btnText1;
        mListener1 = listener1;
    }
    public DlgWindow7(Context context, int theme, String btnText1, android.view.View.OnClickListener listener1){
        super(context, theme);
        this.context = context;
        mBtnText1 = btnText1;
        mListener1 = listener1;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_window7);
        
        Button btnCancel = ((Button)(findViewById(R.id.btn1)));
        btnCancel.setText(mBtnText1);
        btnCancel.setOnClickListener(mListener1);
    }

}