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

public class DlgWindow4 extends Dialog {

    Context context;
    String mTxtContent;
    String mBtnText;
    android.view.View.OnClickListener mListener;
    
    public DlgWindow4(Context context, String txtContent, String btnText, android.view.View.OnClickListener listener) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        mTxtContent = txtContent;
        mBtnText = btnText;
        mListener = listener;
    }
    public DlgWindow4(Context context, int theme, String txtContent, String btnText, android.view.View.OnClickListener listener){
        super(context, theme);
        this.context = context;
        mTxtContent = txtContent;
        mBtnText = btnText;
        mListener = listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_window4);
        
        TextView txtContent = ((TextView)(findViewById(R.id.txtcontent)));
        txtContent.setText(mTxtContent);
        Button btnOK = ((Button)(findViewById(R.id.btnok)));
        btnOK.setText(mBtnText);
        btnOK.setOnClickListener(mListener);
    }

}