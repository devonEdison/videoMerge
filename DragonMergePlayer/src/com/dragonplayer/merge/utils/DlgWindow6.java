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

public class DlgWindow6 extends Dialog {

    Context context;
    String mBtnText1;
    String mBtnText2;
    String mBtnText3;
    android.view.View.OnClickListener mListener1;
    android.view.View.OnClickListener mListener2;
    android.view.View.OnClickListener mListener3;
    
    public DlgWindow6(Context context, String btnText1, String btnText2, String btnText3, 
    		android.view.View.OnClickListener listener1, 
    		android.view.View.OnClickListener listener2, 
    		android.view.View.OnClickListener listener3) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        mBtnText1 = btnText1;
        mBtnText2 = btnText2;
        mBtnText3 = btnText3;
        mListener1 = listener1;
        mListener2 = listener2;
        mListener3 = listener3;
    }
    public DlgWindow6(Context context, int theme, String btnText1, String btnText2, String btnText3, 
    		android.view.View.OnClickListener listener1, 
    		android.view.View.OnClickListener listener2, 
    		android.view.View.OnClickListener listener3) {
        super(context, theme);
        this.context = context;
        mBtnText1 = btnText1;
        mBtnText2 = btnText2;
        mBtnText3 = btnText3;
        mListener1 = listener1;
        mListener2 = listener2;
        mListener3 = listener3;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_window6);
        
        Button btnCancel = ((Button)(findViewById(R.id.btn1)));
        btnCancel.setText(mBtnText1);
        btnCancel.setOnClickListener(mListener1);
        Button btnOK = ((Button)(findViewById(R.id.btn2)));
        btnOK.setText(mBtnText2);
        btnOK.setOnClickListener(mListener2);
        Button btn3 = ((Button)(findViewById(R.id.btn3)));
        btn3.setText(mBtnText3);
        btn3.setOnClickListener(mListener3);
    }

}