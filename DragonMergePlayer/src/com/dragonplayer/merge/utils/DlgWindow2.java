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

public class DlgWindow2 extends Dialog {

    Context context;
    String mTxtContent;
    String mBtnText1;
    String mBtnText2;
    android.view.View.OnClickListener mListener1;
    android.view.View.OnClickListener mListener2;
    
    public DlgWindow2(Context context, String txtContent, String btnText1, String btnText2, android.view.View.OnClickListener listener1, android.view.View.OnClickListener listener2) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        mTxtContent = txtContent;
        mBtnText1 = btnText1;
        mBtnText2 = btnText2;
        mListener1 = listener1;
        mListener2 = listener2;
    }
    public DlgWindow2(Context context, int theme, String txtContent, String btnText1, String btnText2, android.view.View.OnClickListener listener1, android.view.View.OnClickListener listener2) {
        super(context, theme);
        this.context = context;
        mTxtContent = txtContent;
        mBtnText1 = btnText1;
        mBtnText2 = btnText2;
        mListener1 = listener1;
        mListener2 = listener2;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_window2);
        
        TextView txtContent = ((TextView)(findViewById(R.id.txtcontent)));
        txtContent.setText(mTxtContent);
        Button btnOK = ((Button)(findViewById(R.id.btnok)));
        btnOK.setText(mBtnText1);
        btnOK.setOnClickListener(mListener1);
        Button btnCancel = ((Button)(findViewById(R.id.btncancel)));
        btnCancel.setText(mBtnText2);
        btnCancel.setOnClickListener(mListener2);
    }

}