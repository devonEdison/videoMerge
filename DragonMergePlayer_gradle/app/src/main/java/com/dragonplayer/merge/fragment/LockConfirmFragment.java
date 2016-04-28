package com.dragonplayer.merge.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.dragonplayer.merge.LoadingDialog;
import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.ProjectListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.utils.AppConnectivity;
import com.dragonplayer.merge.utils.BannerData;
import com.dragonplayer.merge.utils.MLog;
import com.dragonplayer.merge.utils.WebService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author 說明解鎖版型
 *
 */
public class LockConfirmFragment extends Fragment {

	ImageButton btnSite;
	ImageButton btnConfirm;

	ArrayList actList = new ArrayList();
	
	LoadingDialog mLoadingProgress;

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        MLog.e(this, "OnActivityCreated");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.lock_confirm_fragment, viewgroup, false);
        
    	btnSite = (ImageButton) view.findViewById(R.id.btnsite);
    	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnSite.getLayoutParams();
    	DisplayMetrics displaymetrics = getActivity().getResources().getDisplayMetrics();
    	params.height = displaymetrics.widthPixels * 121 / 721;
    	btnSite.setLayoutParams(params);
    	btnSite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLoadingProgress = new LoadingDialog(getActivity());
				mLoadingProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				mLoadingProgress.show();
				
		        AsyncCallWS task = new AsyncCallWS();
				task.execute();
			}
    		
    	});
        
    	btnConfirm = (ImageButton) view.findViewById(R.id.btnconfirm);
    	params = (RelativeLayout.LayoutParams) btnConfirm.getLayoutParams();
    	params.height = displaymetrics.widthPixels * 121 / 721;
    	btnConfirm.setLayoutParams(params);
    	btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//((MainActivity)getActivity()).onClickLockConfirmBack(); //原本流程
				//再確定已解鎖,進入預覽作品畫面
				((MainActivity)getActivity()).onClickUploadConfirm(getArguments().getString("actId"),
						getArguments().getString("username"),
						getArguments().getString("email"),
						getArguments().getString("tel"),
						getArguments().getString("filepath"));
				
			}
    		
    	});
    	MLog.e(this, "Fortable"+getArguments().getString("actId"));
    	MLog.e(this, "Fortable"+getArguments().getString("username"));
    	MLog.e(this, "Fortable"+getArguments().getString("email"));
    	MLog.e(this, "Fortable"+getArguments().getString("tel"));
    	MLog.e(this, "Fortable"+getArguments().getString("filepath"));
        return view;
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

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {
		
		private String mResult = "";
		private String mActId = "";
		private String mTel = "";
		
		@Override
		protected Void doInBackground(String... params) {

			mResult = WebService.Banner("Act");
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mLoadingProgress != null) {
				mLoadingProgress.dismiss();
				mLoadingProgress = null;
			}
			
	        SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
	        String actId = sp.getString("actId", "");

	        JSONObject jsonobject;
			try {
				JSONArray jsonarray = new JSONArray(mResult);
				actList.clear();
				for (int i = 0; i < jsonarray.length(); i++) {
					ActData data = new ActData();
					jsonobject = jsonarray.getJSONObject(i);
					data.actId = jsonobject.getString("ActId");
					data.actName = jsonobject.getString("ActName");
					data.actContent = jsonobject.getString("ActContent");
					data.actUrl = jsonobject.getString("Url");
					actList.add(data);
					
//					if (actId == data.actId) {
						Uri uriUrl = Uri.parse(data.actUrl);
						Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
						startActivity(launchBrowser);
						break;
//					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}
	
	public class ActData {
		public String actId = "";
		public String actName = "";
		public String actContent = "";
		public String actUrl = "";
	}
}
