package com.dragonplayer.merge.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.dragonplayer.merge.FBLikeActivity;
import com.dragonplayer.merge.LoadingDialog;
import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.ProjectListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.utils.AppConnectivity;
import com.dragonplayer.merge.utils.BannerData;
import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.Constants;
import com.dragonplayer.merge.utils.DlgWindow1;
import com.dragonplayer.merge.utils.MLog;
import com.dragonplayer.merge.utils.Utils;
import com.dragonplayer.merge.utils.WebService;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author 參加活動明細 預覽上傳結果
 *
 */
public class UploadConfirmFragment extends Fragment {

	TextView txtUrl;
	TextView txtUserName;
	TextView txtMail;
	TextView txtTel;

	ImageView imgMedia;
	
	LoadingDialog mLoadingProgress;
	public static int firsttime =0;
	String filePath;
    Dialog mDialog;
    Context mContext  ;
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        MLog.e(this,"onActivityCreated");
        /** goto unlock*/
    	boolean liked = true ;
    	SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isLike", liked);
		editor.commit();
		/** show a dialog*/
		MLog.e(this, "firsttime"+firsttime);
		if (firsttime ==0)
		{
			Dialogforfacebook();
		}
		
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);    		 
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.upload_confirm_fragment, viewgroup, false);
        
    	txtUrl = (TextView) view.findViewById(R.id.url2);
    	txtUserName = (TextView) view.findViewById(R.id.username2);
    	txtMail = (TextView) view.findViewById(R.id.email2);
    	txtTel = (TextView) view.findViewById(R.id.tel2);
    	
    	imgMedia = (ImageView) view.findViewById(R.id.imgmedia);
    	
    	txtUrl.setText("http://www.skywind.com.tw/sky2u/");
    	txtUrl.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialogforfacebook();
			}
    		
    	});
    	txtUserName.setText(getArguments().getString("username"));
    	MLog.e(this, getArguments().getString("email"));
    	MLog.e(this, getArguments().getString("tel"));
    	StringBuffer mail = new StringBuffer(getArguments().getString("email"));
    	//遮掩身分證字號跟電話號碼
    	mail.replace(3, 8, "xxxxx");
    	StringBuffer tel = new StringBuffer(getArguments().getString("tel"));
    	
    	tel.replace(4, 8, "xxxx");
    	
    	
    	txtMail.setText(mail);
    	txtTel.setText(tel);
    	filePath = getArguments().getString("filepath");
    	
    	imgMedia.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
            	imgMedia.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        
            	if (filePath == null || filePath.isEmpty())
            		return;
            	
                int screenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getActivity().getResources().getDisplayMetrics().heightPixels;
                int itemHeight = imgMedia.getHeight(); 
                int itemWidth = itemHeight * screenWidth / screenHeight;
                
                if (filePath.contains("mp4")) {
                	File file = Utils.writeToFile(BitmapUtil.videoFrame(filePath, 0L));
                	((MainActivity)getActivity()).getManager().displayThumbImage(file.getAbsolutePath(), imgMedia, itemWidth, itemHeight);
                	file.delete();
                }
                else {
                    ((MainActivity)getActivity()).getManager().displayThumbImage(filePath, imgMedia, itemWidth, itemHeight);
                }
            }
        });
        return view;
    }
    private void Dialogforfacebook(){
    	 
		/** show a dialog for user*/ 
		 firsttime = 1;
		mDialog = new DlgWindow1(getActivity(), R.style.CustomDialog, "請至粉絲團按讚後即可有獲得獎品資格", "取消", "確認", new OnClickListener() {
			//粉絲團
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).HomePage();
				mDialog.dismiss();
			}
        }, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = "http://www.skywind.com.tw/sky2u/";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				
				mDialog.dismiss();
			}
        });
		mDialog.show(); 
    }
    public void onDetach() {
    	
        try {
            Field field = Fragment.class.getDeclaredField("mChildFragmentManager");
            field.setAccessible(true);
            field.set(this, null);
        }
        catch(NoSuchFieldException nosuchfieldexception) {
            throw new RuntimeException(nosuchfieldexception);
        }
        catch(IllegalAccessException illegalaccessexception) {
            throw new RuntimeException(illegalaccessexception);
        }
        
        super.onDetach();
    }
     
}
