package com.dragonplayer.merge.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.ProjectListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.utils.AppConnectivity;
import com.dragonplayer.merge.utils.DlgWindow4;
 


import com.dragonplayer.merge.utils.MLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
/** 
 * 
 * @author 設置bar功能位置
 *
 */
public class BaseFragment extends Fragment {

	public static final int TABLIST = 1;
	public static final int TABLOCK = 2;
	public static final int TABUPLOAD = 3;
	
    ImageView btnList;
    ImageView btnLock;
    ImageView btnUpload;
    
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }
    
    @SuppressLint("ResourceAsColor")
	public void initBottomTab(View view, int selIndex) {
    	
    	LinearLayout bottomLayout = (LinearLayout) view.findViewById(R.id.bottom_button_layout);
    	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
    	DisplayMetrics displaymetrics = getActivity().getResources().getDisplayMetrics();
    	params.height = displaymetrics.widthPixels / 6;
    	bottomLayout.setLayoutParams(params);
    	
    	btnList = (ImageView) view.findViewById(R.id.btnList);
    	btnList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).onClickList();
			}
    		
    	});
    	//修改流程後不需要
    	/** btnLock = (ImageView) view.findViewById(R.id.btnLock);
    	btnLock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).onClickLock();
			}
    		
    	}); */

    	btnUpload = (ImageView) view.findViewById(R.id.btnUpload);
    	btnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).onClickUpload();
				//((MainActivity)getActivity()).onClickAbout();
			}
    		
    	});
    	 //修改流程後 btnLock不需要
		btnList.setEnabled(true);
		//btnLock.setEnabled(true);
		btnUpload.setEnabled(true);

		btnList.setSelected(false);
		//btnLock.setSelected(false);
		btnUpload.setSelected(false);
		
    	if (selIndex == TABLIST) {
    		btnList.setEnabled(false);
    		btnList.setSelected(true);
    	}
    	else if (selIndex == TABLOCK) {
    	//	btnLock.setEnabled(false);
    	//	btnLock.setSelected(true);
    	}
    	else if (selIndex == TABUPLOAD) {
    		btnUpload.setEnabled(false);
    		btnUpload.setSelected(true);
    	}

    	AppConnectivity connection = new AppConnectivity(getActivity().getApplicationContext());
        boolean isInternet = connection.isConnectingToInternet();
        if (!isInternet) {
        	//btnUpload.setImageResource(R.drawable.btn_lock_off);
             
        }else 
        {	String resAPI = getWebApi();
        	//btnUpload.setImageResource(R.drawable.btn_lock_on);
        	
        }
        
    	 
    }
    public String getWebApi(){
    	Thread a = new Thread();
    	{
    		 MLog.d(this, "testThread");
    	}
    	a.start();
    	String str = "";
    	String url = "http://211.78.89.41/ap/WService.asmx" ;
    	HttpPost httppost = new HttpPost( url ) ; 
    	List <NameValuePair> params = new ArrayList <NameValuePair> ( ) ; 
    	params.add ( new BasicNameValuePair ( "id" , "Frank" ) ) ; 
    	try  
    	{
    	    httppost.setEntity ( new UrlEncodedFormEntity ( params , HTTP.UTF_8 ) ) ; 

    	    HttpResponse res = new DefaultHttpClient () . execute ( httppost ) ; 

    	    if ( res.getStatusLine() .getStatusCode( ) == 200 ) //判斷回傳的狀態是不是200

    	        str = EntityUtils.toString(res.getEntity()) ;  
    	    MLog.d(this, "WebApiResponse"+str);
    	}
    	catch(Exception e)
    	{ }

    	return str;

    	}
}
