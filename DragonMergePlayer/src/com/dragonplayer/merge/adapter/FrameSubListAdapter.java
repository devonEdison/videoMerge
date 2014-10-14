package com.dragonplayer.merge.adapter;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.fragment.GiftFragment;
import com.dragonplayer.merge.frames.Frame;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.FramesLayout;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.frames.FrameView.SetBounds;
import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.DlgWindow1;
import com.dragonplayer.merge.utils.Utils;

public class FrameSubListAdapter extends BaseAdapter {

    private Context context;
    private FragmentActivity activity;
    private int firstFramePosition;
    private int frameCount;
    private Frames frames;
    private int itemHeight;
    private int itemWidth;
    private int itemtype;
    Dialog mDialog;

    public FrameSubListAdapter(Context contxt, int frmCount, int firstfrmPos, int itemH, Frames frm, int type) {
        context = contxt;
        frameCount = frmCount;
        itemWidth = itemH;
        firstFramePosition = firstfrmPos;
        frames = frm;
        itemtype = type;
        
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        itemHeight = itemWidth * screenHeight / screenWidth;
    }

    public int getCount() {
        return frameCount;
    }

    public Object getItem(int i) {
        return Integer.valueOf(i);
    }

    public long getItemId(int i) {
        return (long)i;
    }

    private String getDateFromMilliSec(String milli) {
    	String strTime;
    	Date date = new Date();date.setTime(Long.valueOf(milli));
    	strTime = (date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes();
    	
    	return strTime;
    }
    
    public View getView(int pos, View view, ViewGroup viewgroup) {
        final int realPos = pos + firstFramePosition;
        Frame frame;
        if (itemtype == 0)
        	frame = frames.getFrameWithId1(realPos);
        else
        	frame = frames.getFrameWithId2(realPos);
        
        RelativeLayout frmView = (RelativeLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.frame_view, null);
        
        ImageView imgFrameThumb = (ImageView) frmView.findViewById(R.id.imgframethumb);

        imgFrameThumb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        SharedPreferences sp = context.getSharedPreferences("iDragon", Context.MODE_PRIVATE);
		        boolean bFacebookLiked = sp.getBoolean("isLike", false);
		        int nLock;
		        if (itemtype == 0)
		        	nLock = frames.getFrameWithId1(realPos).getLockFlag();
		        else
		        	nLock = frames.getFrameWithId2(realPos).getLockFlag();

		        if (!bFacebookLiked && nLock == Frame.FRAMELOCK) {
		        	// dexter
				/**	 Dialog dialog = new Dialog(context);
					dialog.setContentView(R.layout.gift_layout);
					
					dialog.setTitle("抽獎活動");
					dialog.show();
					WebView wv=(WebView)dialog.findViewById(R.id.gift_web);
					GiftFragment gf=new GiftFragment(activity, wv); */
		        	
		        	AlertDialog.Builder builder = new AlertDialog.Builder(context);
		            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		            builder.setView(inflater.inflate(R.layout.gift_layout, null));
		            builder.setNegativeButton("確定", null);
		            AlertDialog dialog = builder.create();
		            dialog.setTitle("抽獎活動");
		            dialog.show();
		            WebView wv=(WebView)dialog.findViewById(R.id.gift_web);
					GiftFragment gf=new GiftFragment(activity, wv); 
					
		        	/**mDialog = new DlgWindow1(context, R.style.CustomDialog, "驗證SK2U粉絲團", "取消", "前往解鎖", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mDialog.dismiss();
						}
		            }, new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							((MainActivity)context).onClickLock();
							mDialog.dismiss();
						}
		            });
					mDialog.show();*/
		        	return;
		        }
		        else 
		        	((MainActivity)context).selectFrameFragment(itemtype==0 ? realPos:realPos+18);
			}
        	
        });
        
        SharedPreferences sp = context.getSharedPreferences("iDragon", Context.MODE_PRIVATE);
        boolean bFacebookLiked = sp.getBoolean("isLike", false);

        boolean lockflag = (!bFacebookLiked && (itemtype == 0 ? frames.getFrameWithId1(realPos) : frames.getFrameWithId2(realPos)).getLockFlag() == Frame.FRAMELOCK) ? true : false;
        int lockmessage = 0 ;
        //先行判斷是否有fb解鎖,如果沒有預設lockmessage為0
        if (!bFacebookLiked)
        {	 
       	 lockmessage = 0;         
        }else{	 
        //如果FB有解鎖,判斷哪一張圖片是原本有鎖定的
       	 lockmessage = frames.getFrameWithId1(realPos).getLockFlag();
       	 
        } 
//        if (frame.getFrameDirection() == Frame.FRAMEVERTICAL) {
	        imgFrameThumb.setLayoutParams(new RelativeLayout.LayoutParams(itemWidth, itemHeight));
	        imgFrameThumb.setImageBitmap(BitmapUtil.getBitmapFromAsset(context, "Frames/"+frame.getFrameName()+"-thumb.png", itemHeight * 72 / 128, itemHeight, lockflag, lockmessage));
	        
	        frmView.setDescendantFocusability(0x20000);
	        frmView.setLayoutParams(new android.widget.AbsListView.LayoutParams(itemHeight * 72 / 128, itemHeight));
//        }
//        else {
//	        imgFrameThumb.setLayoutParams(new RelativeLayout.LayoutParams(itemWidth, itemHeight));
//	        imgFrameThumb.setImageBitmap(BitmapUtil.getBitmapFromAsset(context, "Frames/"+frame.getFrameName()+"-thumb.png", itemHeight * 72 / 128, itemHeight * 72 / 128 * 72 / 128, lockflag));
//	        
//	        frmView.setDescendantFocusability(0x20000);
//	        frmView.setLayoutParams(new android.widget.AbsListView.LayoutParams(itemHeight * 72 / 128, itemHeight));
//        }
        
        return frmView;
    }
}
