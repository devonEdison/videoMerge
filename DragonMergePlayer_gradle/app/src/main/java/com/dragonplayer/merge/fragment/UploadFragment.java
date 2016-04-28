package com.dragonplayer.merge.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import com.dragonplayer.merge.LoadingDialog;
import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.ProjectListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.utils.AppConnectivity;
import com.dragonplayer.merge.utils.BannerData;
import com.dragonplayer.merge.utils.DlgWindow2;
import com.dragonplayer.merge.utils.DlgWindow4;
import com.dragonplayer.merge.utils.Utils;
import com.dragonplayer.merge.utils.WebService;

import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author 抽獎活動畫面
 *
 */
public class UploadFragment extends BaseFragment {

	ImageView imgSelectMedia;
	TextView txtExplain;
	
	LoadingDialog mLoadingProgress;
	boolean bRegSuccess = false;

	Dialog mDialog;
	
    public UploadFragment() {
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);

    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.upload_fragment, viewgroup, false);
        
        initBottomTab(view, TABUPLOAD);
        
    	txtExplain = (TextView) view.findViewById(R.id.txtexplain);
    	
    	WebView wv = (WebView)view.findViewById(R.id.webView1);
    	String data= "<pre><p style=\"font-size:14px;font-family:'Apple LiGothic';color:#4F81BD;\">(活動說明)</p>"
    			+ "<p style=\"font-size:14px;font-family:'Apple LiGothic';color:#C01900;\">新光金寶拼貼趣</p>" 
    			+ "<p style=\"font-size:14px;font-family:'Apple LiGothic';\">活動日期：暫定9/15 – 10/31</p><p style=\"font-size:14px;font-family:'Apple LiGothic';\">活動辦法：完成以下步驟即可參加抽獎喔！</p><ol><li style=\"font-size:14px;font-family:'Apple LiGothic';\">"
    			+ "點選”隱藏版型”進行解鎖。"
    			+ "</li><li style=\"font-size:14px;font-family:'"
    			+ "Apple LiGothic';\">加入SK2U粉絲團後即可獲得<br s"
    			+ "tyle=\"font-size:14px;font-family:'Apple LiGothi"
    			+ "c';\">五款隱藏版型</br></li><li style=\"font-size:14"
    			+ "px;font-family:'Apple LiGothic';\">選擇其中一款隱藏版型進行編"
    			+ "輯</li><li style=\"font-size:14px;font-family:'Apple LiG"
    			+ "othic';\">完成作品後點選”抽獎活動”，上傳作<br style=\"font-size:"
    			+ "14px;font-family:'Apple LiGothic';\">品並填寫基本資料，成功送出後"
    			+ "即可<br>參加抽獎。</br></li></ol><p style=\"font-size:14px;font-f"
    			+ "amily:'Apple LiGothic';color:#C01900;\">活動獎項：</p>雙人香港來回機票2"
    			+ "名、原燒優質原味燒肉雙<br/>人餐券2名、陶阪屋和風創意料理雙人餐券4名<br/>、"
    			+ "TASTy西堤牛排雙人餐券1名、品田牧場日<br/>式豬"
    			+ "排咖哩雙人餐券4名、聚北海道昆布鍋雙<br/>人餐券1名</p>"
    			+ "<p style=\"font-size:14px;font-family:'Apple LiGothic'"
    			+ ";\"></p><p style=\"font-size:14px;font-family:"
    			+ "'Apple LiGothic';color:#C01900;\">注意事項：</p><ol><l"
    			+ "i style=\"font-size:14px;font-family:'Apple LiGothic'"
    			+ ";\">每人僅限中獎一次，本活動僅限<br/>台灣地區(含台澎金馬)中華民國<br/>國"
    			+ "民參加。活動獎項寄送地址僅<br/>限中華民國境內。獎項不得折換<br/>現金或更換。主辦"
    			+ "單位確認符合<br/>獲贈資格後將公布獲獎名單於活<br/>動官網，並由專人通知得獎用戶。"
    			+ "</li><li style=\"font-size:14px;font-fa"
    			+ "mily:'Apple LiGothic';\">依中華民國所得稅法規定"
    			+ "，獎項<br/>超過1,000元，須開立扣繳憑單；<br/>超過(含)20,"
    			+ "000元，需事先繳交<br/>10%稅金，依法辦理扣繳。故得<br/>獎人需提供主辦單"
    			+ "位身份證影本<br/>且依規定填寫並繳交相關收據方<br/>可領獎，若不願意提供，則視為<br"
    			+ "/>自動棄權，不具得獎資格。</li><li style=\"font-size:14px;font-family:'Apple LiGothic';\">除法令另有規定者外，主辦單位<br/>保留隨時修改、變更或終止本活<br/>動∕本注意事項之權利，活動如<br/>發生任何爭議，概以主辦單位活<br/>動網站之公告為準。</li><li style=\"font-size:14px;font-family:'Apple LiGothic';\">關於本活動相關問題，可撥打服<br/>務電話(02)7746-4100，服務時間<br/>：週一~五(9:30~18:00)或mail<br/>至<a href=\"mailto:service@skywind.com.tw\">service@skywind.com.tw</a>詢問。</li></ol></pre>";
  
    	wv.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    	 
    	wv.setWebViewClient(new WebViewClient(){
    		
    		@Override
    		public boolean shouldOverrideUrlLoading(WebView view, String url) {

    			if(url.startsWith("mailto"))
    			{	//針對郵件動作
    			Uri uri = Uri.parse("mailto:service@skywind.com.tw"); 
    				Intent it = new Intent(Intent.ACTION_SENDTO, uri); 
    				startActivity(it); 
    				
    				 
    			}
    			if (url.startsWith("tel"))
    			{	//針對電話動作
    				Uri uri = Uri.parse("tel:0277464100"); 
    				Intent it = new Intent(Intent.ACTION_DIAL, uri); 
    				startActivity(it); 
    			}
    		    return true;
    		}
    	});
    	
    	imgSelectMedia = (ImageButton) view.findViewById(R.id.btnselectmedia);
    	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgSelectMedia.getLayoutParams();
    	DisplayMetrics displaymetrics = getActivity().getResources().getDisplayMetrics();
    	params.height = displaymetrics.widthPixels * 120 / 721;
    	imgSelectMedia.setLayoutParams(params);
    	imgSelectMedia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppConnectivity connection = new AppConnectivity(getActivity().getApplicationContext());
		        boolean isInternet = connection.isConnectingToInternet();
		        
		        if (!isInternet) {
					mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "無網路連線", "確定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mDialog.dismiss();
						}
		            });
					mDialog.show();
		            return;
		        }
				
		        SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
		        String actId = sp.getString("actId", "");
		        String tel = sp.getString("tel", "");
			         
				mLoadingProgress = new LoadingDialog(getActivity());
				mLoadingProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				mLoadingProgress.show();
				
		        AsyncCallWS task = new AsyncCallWS();
				task.execute(actId, tel);
			}
    		
    	});
     
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
		
		@Override
		protected Void doInBackground(String... params) {
			String actId = params[0];
			String tel = params[1];
			
			mResult = WebService.ChkAct(actId, tel, "ChkAct");
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mLoadingProgress != null) {
				mLoadingProgress.dismiss();
				mLoadingProgress = null;
			}
			
			if (mResult.toUpperCase().equals("YES")) {
				mDialog = new DlgWindow2(getActivity(), R.style.CustomDialog, "您已參加過此活動", "瀏覽參與資料", "確定", 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
								
			    		        SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
			    		        String actId = sp.getString("actId", "");
			    		        String tel = sp.getString("tel", "");
			    		        String username = sp.getString("username", "");
			    		        String email = sp.getString("email", "");
			    		        String filepath = sp.getString("filepath", "");
			                    
			                    ((MainActivity)getActivity()).onClickUploadConfirm(actId, username, email, tel, filepath);
							}
						}, 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
							}
	            });
				mDialog.show();
			}
			else {	//針對第一次使用上傳須SHOW出個資說明
					String data =  getString(R.string.uploadmessage);
					AlertDialog.Builder builder = new AlertDialog.Builder(((MainActivity ) getActivity()));
					builder.setMessage(data);
					builder.setPositiveButton(getString(R.string.upload_alertdialog_yes), 
						    new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    // TODO Auto-generated method stub
					    	((MainActivity)getActivity()).onClickUploadInfo();
					         
					    }});
					builder.setNegativeButton(getString(R.string.upload_alertdialog_no), 
							new DialogInterface.OnClickListener() {
					         @Override
					         public void onClick(DialogInterface dialog, int which) {
					         // TODO Auto-generated method stub
					         //關閉AlertDialog視窗
					         dialog.cancel();
					    }
					}); 
					builder.create();
					builder.show();
				
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}
    
}
