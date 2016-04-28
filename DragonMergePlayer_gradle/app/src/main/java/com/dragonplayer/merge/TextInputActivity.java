package com.dragonplayer.merge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dragonplayer.merge.utils.FileMover;
import com.dragonplayer.merge.utils.MLog;
import com.dragonplayer.merge.utils.Utils;

import java.io.*;

public class TextInputActivity extends Activity {
	
	public static final int MAXTEXTZOOM = 1;
	boolean isTextColor = true;
	ImageButton btnTextColor;
	ImageButton btnBackColor;
	EditText editText;
	int textClr = 0xFF000000;
	int backClr = 0x00FFFFFF;
	FrameLayout colorLayout;
	int nTextSize = 70;
	
	int[] clrIds = {R.drawable.color01, R.drawable.color02, R.drawable.color03, R.drawable.color04, 
					R.drawable.color05, R.drawable.color06, R.drawable.color07, R.drawable.color08, 
					R.drawable.color09, R.drawable.color10, R.drawable.color11, R.drawable.color12, 
					R.drawable.color13, R.drawable.color14, R.drawable.color15, R.drawable.color16, 
					R.drawable.color17, R.drawable.color18, R.drawable.color19, R.drawable.color20, 
					R.drawable.color21, R.drawable.color22, R.drawable.color23, R.drawable.color24, 
					R.drawable.color25, R.drawable.color26, R.drawable.color27, R.drawable.color28, 
					R.drawable.color29, R.drawable.color30, R.drawable.color31, R.drawable.color32 };
	
	int[] clrs = {0xFFBF272D, 0xFFEF5A24, 0xFFF59E1E, 0xFFF9AE3B, 0xFFFAEC21, 0xFFD7DE21, 0xFF8AC43F, 0xFF39B34A,
				  0xFF009045, 0xFF006837, 0xFF22B373, 0xFF00A79B, 0xFF29A9E0, 0xFF0071BA, 0xFF2E3190, 0xFF186414,
				  0xFF662D8F, 0xFF91278D, 0xFF9C005D, 0xFFD2145A, 0xFFEB1E79, 0xFFC5B097, 0xFF978475, 0xFF736357, 
				  0xFF534741, 0xFF000000, 0xFF4D4D4D, 0xFF666666, 0xFF979797, 0xFFB1B1B1, 0xFFD4D4D4, 0xFFFFFFFF };
	
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_text_input);

        nTextSize = getResources().getDisplayMetrics().widthPixels / 12;
        
        colorLayout = (FrameLayout) findViewById(R.id.color_layout);
	    
        btnTextColor = (ImageButton) findViewById(R.id.btn_textcolor);
        btnTextColor.setSelected(true);
	    btnTextColor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isTextColor = true;
				btnTextColor.setSelected(true);
				btnBackColor.setSelected(false);
			}
	    	
	    });
	    
	    btnBackColor = (ImageButton) findViewById(R.id.btn_backcolor);
	    btnBackColor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isTextColor = false;
				btnTextColor.setSelected(false);
				btnBackColor.setSelected(true);
			}
	    	
	    });

	    editText = (EditText) findViewById(R.id.edit_text);
	    editText.setTextSize(nTextSize / 2);

	    ImageButton btnComplete = (ImageButton) findViewById(R.id.btn_complete);
	    btnComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String txt = editText.getText().toString();
				
				if (txt != null && !txt.isEmpty()) {
					String strFileName = textToImage();
				    Bundle conData = new Bundle();
				    conData.putString("TextImagePath", strFileName);
				    Intent intent = new Intent();
				    intent.putExtras(conData);
				    setResult(RESULT_OK, intent);
				}
				
			    finish();
			}
	    	
	    });
	    
        ImageButton btnBack = (ImageButton)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
        	
        });

    	RelativeLayout titlebar = (RelativeLayout) findViewById(R.id.titlebg_layout);
    	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)titlebar.getLayoutParams();
    	DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
    	params.width = displaymetrics.widthPixels;
    	params.height = displaymetrics.widthPixels * 81 / 721;
    	titlebar.setLayoutParams(params);
    	
    	RelativeLayout.LayoutParams btnparams = (RelativeLayout.LayoutParams)btnBack.getLayoutParams();
    	btnparams.width = params.height; 
    	btnparams.height = params.height;
    	btnBack.setLayoutParams(btnparams);
    	
    	btnparams = (RelativeLayout.LayoutParams)btnComplete.getLayoutParams();
    	btnparams.width = params.height; 
    	btnparams.height = params.height;
    	btnComplete.setLayoutParams(btnparams);

        BitmapFactory.Options options = new BitmapFactory.Options();
        
        options.inJustDecodeBounds = true;
        options.inDensity = 180;
        BitmapFactory.decodeResource(getResources(), R.drawable.txt_16, options);
    	
        ImageView txtTitle = (ImageView) findViewById(R.id.txttitle);
        
    	RelativeLayout.LayoutParams txtparams = (RelativeLayout.LayoutParams)txtTitle.getLayoutParams();
    	txtparams.height = params.height * options.outHeight / 81;
    	txtparams.width = txtparams.height * options.outWidth / options.outHeight;
    	txtTitle.setBackgroundResource(R.drawable.txt_16);
    	txtTitle.setLayoutParams(txtparams);

		titlebar.setBackgroundResource(R.drawable.bar_4);
    	btnBack.setVisibility(View.VISIBLE);
    	btnComplete.setVisibility(View.VISIBLE);
        
	    colorLayout.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
			public void onGlobalLayout() {
            	
            	colorLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            	
            	int width = colorLayout.getWidth();
            	int height = colorLayout.getHeight();
            	int btnWidth = width / 11; 
            	int btnHeight = height / 6;
            	int btnsize = 0;
            	int wgap = 0;
            	int hgap = 0;
            	
            	if (btnWidth < btnHeight) {
            		btnsize = btnWidth;
            	}
            	else {
            		btnsize = btnHeight;
            	}
            	
            	wgap = (width - btnsize * 8) / 9;
            	hgap = (height - btnsize * 4) / 5;
            	
            	Log.e("Size", "w="+width+":h="+height+":wg="+wgap+":hg="+hgap);
            	
            	for (int y = 0; y < 4; y++) {
                	for (int x = 0; x < 8; x++) {
                        Button button = new Button(TextInputActivity.this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(btnsize, btnsize);
                        
                        button.setBackgroundResource(clrIds[y * 8 + x]);
                        params.leftMargin = x * btnsize + (x + 1) * wgap;
                        params.topMargin = y * btnsize + (y + 1) * hgap;
                        button.setLayoutParams(params);
                        button.setId(y * 8 + x);
                        
                        button.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								
								if (isTextColor) { 
									textClr = clrs[v.getId()];
									editText.setTextColor(textClr);
								}
								else {
									backClr = clrs[v.getId()];
									editText.setBackgroundColor(backClr);
								}
							}
                        	
                        });
                        
                        colorLayout.addView(button);
                	}
            	}
            }
	    });
    }

    private String textToImage() {
    	Canvas canvas;
    	Bitmap bitmap ;
    	Paint paint = new Paint();
    	String txt = editText.getText().toString();
    	Rect rc = new Rect();
    	
		paint.setAntiAlias(true);
    	paint.setColor(textClr);
    	paint.setTextSize(nTextSize * MAXTEXTZOOM);
        paint.setTextAlign(Paint.Align.LEFT);
    	
    	String sTexts[] = txt.split("\n");
    	
		int ascent = (int) Math.ceil(-paint.ascent());
        int descent = (int) Math.ceil(paint.descent());
        int textHeight = ascent + descent;
        int nTop = (sTexts.length * textHeight - (textHeight * sTexts.length)) / 2;
        int nLeft = 0;
        int nWidth = 0;

        nTop = nTop + ascent;

		for (int i=0;i<sTexts.length;i++)
		{
			paint.getTextBounds(sTexts[i], 0, sTexts[i].length(), rc);
			
			if (nWidth < rc.width())
				nWidth = rc.width();
		}
    	
    	paint.getTextBounds(txt, 0, txt.length(), rc);
    	 bitmap = Bitmap.createBitmap((int)(nWidth * 1.2D), sTexts.length * textHeight, Bitmap.Config.ARGB_8888);

    	 
    	MLog.i(this, "Bitmapcreaterotate");
    	canvas = new Canvas(bitmap);
    	/** */
        
    	/** */
		canvas.drawColor(backClr);

		nLeft = (int)(nWidth * 0.1D);
		 
    	for (int i=0;i<sTexts.length;i++)
		{
			canvas.drawText(sTexts[i], nLeft, nTop, paint);
			nTop = nTop + textHeight;
		}

    	return Utils.writeToPNGFile(bitmap).getAbsolutePath();
    }
}
