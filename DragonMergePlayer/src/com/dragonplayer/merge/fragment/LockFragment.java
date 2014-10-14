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
import com.dragonplayer.merge.utils.Constants;
import com.dragonplayer.merge.utils.DlgWindow4;
import com.dragonplayer.merge.utils.Utils;
import com.dragonplayer.merge.utils.WebService;
//import com.facebook.HttpMethod;
//import com.facebook.Request;
////import com.facebook.Response;
////import com.facebook.Session;
////import com.facebook.SessionState;
////import com.facebook.UiLifecycleHelper;
////import com.facebook.model.GraphUser;
////import com.facebook.widget.FacebookDialog;
////import com.facebook.widget.LoginButton;
//
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.android.DialogError;
//import com.facebook.android.Facebook;
//import com.facebook.android.Facebook.DialogListener;
//import com.facebook.android.FacebookError;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;




import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author 解除fb鎖
 *
 */
public class LockFragment extends BaseFragment {

	ImageView imgLock;
//	TextView txtExplain;
	ImageButton btnsk;
	LoadingDialog mLoadingProgress;
    boolean mFacebookLiked;
    
    final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
    final static int LIKE_ACTIVITY_RESULT_CODE = 1;

    Dialog mDialog;
    
    public LockFragment() {
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.lock_fragment, viewgroup, false);
        
        initBottomTab(view, TABLOCK);
        
    	imgLock = (ImageView) view.findViewById(R.id.imglock);
//    	txtExplain = (TextView) view.findViewById(R.id.txtexplain);

//    	fb = new Facebook(APP_ID);
    	/** */
    	SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
        mFacebookLiked = sp.getBoolean("isLike", false);
        
        if (mFacebookLiked) {
        	//因為修改新流程關係故不需要此行
        	//((MainActivity)getActivity()).onClickLockConfirm();
        	 
        }
        
        
        
    	btnsk = (ImageButton) view.findViewById(R.id.btnsk);
    	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnsk.getLayoutParams();
    	DisplayMetrics displaymetrics = getActivity().getResources().getDisplayMetrics();
    	params.height = displaymetrics.widthPixels * 120 / 721;
    	btnsk.setLayoutParams(params);
    	btnsk.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
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
		        mFacebookLiked = sp.getBoolean("isLike", false);
		        
		        if (mFacebookLiked) {
		        	//因為修改新流程關係故不需要此行
		        	//((MainActivity)getActivity()).onClickLockConfirm();
		        	return;
		        }
		        
        		Intent intent = new Intent(getActivity(), FBLikeActivity.class);
            	intent.putExtra(Constants.Common.INTENT_FB_LIKE, mFacebookLiked);
        		startActivityForResult(intent, LIKE_ACTIVITY_RESULT_CODE);

//		        SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
//		        String access_token = sp.getString("access_token", null);
//		        long expires = sp.getLong("access_expires", 0);
//
//		        if(access_token != null) {
//		            fb.setAccessToken(access_token);
//		        }
//		        if(expires != 0) {
//		            fb.setAccessExpires(expires);
//		        }
//		        
//		        if (fb.isSessionValid()) {
//            		
//		        	((DragonMainActivity)getActivity()).onClickLockConfirm();
//		        	return;
//            		
//            	}
//
//		    	fb.authorize(getActivity(), new DialogListener() {
//		            
//		        	@Override
//					public void onComplete(Bundle values) {
//
//		        		
//		        		Log.d("Info", "Login Complete");
//		        		Log.d("Info", "ACCESS TOKEN: " + fb.getAccessToken());
//		        		Log.d("Info", "ACCESS EXPIRES: " + fb.getAccessExpires());
//		        		Log.d("Info", "Logged IN");
//						SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
//						Editor editor = sp.edit();
//						editor.putString("access_token", fb.getAccessToken());
//						editor.putLong("access_expires", fb.getAccessExpires());
//	                    editor.commit();
//	                    
//	                    Bundle params = new Bundle(); 
//	                    params.putString(Facebook.TOKEN, fb.getAccessToken());
//	                    if (Session.getActiveSession() != null)
//	                    	Log.d("Error!", "Error!");
//	                    else
//	                    	Log.d("Error!", "Error111");
//
//	                    if (fb.getSession() != null)
//	                    	Log.d("Error222", "Error!");
//	                    else
//	                    	Log.d("Error222", "Error111");
//
//	                    Bundle params1 = new Bundle();
//	                    params1.putString("object", "https://www.facebook.com/KolkataKnightRiders");
//
//	                    Request request = new Request(
//	                    		fb.getSession(),
//	                    		"me/og.likes",
//	                    		params1,
//	                    		HttpMethod.POST
//	                    );
//	                    Response response = request.executeAndWait();
//	                    Log.e("like Post_test1", response.toString());
//	                    
//	                    new Request(
//	                    		fb.getSession(),
//	                    	    "/me",
//	                    	    null,
//	                    	    HttpMethod.GET,
//	                    	    new Request.Callback() {
//	                    	        public void onCompleted(Response response) {
//	                    	        	JSONObject jsonobj = response.getGraphObject().getInnerJSONObject();
//	                    	        	try {
//											String username = jsonobj.get("id").toString();
//		        	                    	Request likeRequest = new Request(fb.getSession(), "/"+username+"/likes/426637074073195", null, HttpMethod.GET, new Request.Callback()
//		        	                    	{
//		        	                    		@Override
//		      	                    	      	public void onCompleted(Response response)
//		      	                    	      	{
//		        	                    			Log.e("like Post1", response.toString());
//		      	                    	      	}
//		      	                    	  	});
//		      	                    	
//		        	                    	likeRequest.executeAsync();
//										} catch (JSONException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//
//	      	                    	Log.e("like Post_me", jsonobj.toString());
//	                    	        }
//	                    	    }
//	                    	).executeAsync();
//	                    
//	                    try {
//
//	                    	Request likeRequest = new Request(fb.getSession(), "/CococodeCo", null, HttpMethod.GET, new Request.Callback()
//	                    	{
//	                    		@Override
//	                    	      	public void onCompleted(Response response)
//	                    	      	{
//	                    			Log.e("like Post2", response.toString());
//	                    	      	}
//	                    	  	});
//	                    	
//	                    	likeRequest.executeAsync();
//	                    	
////	                    	Log.e("like Post", fb.request("426637074073195/likes", values, "POST"));
////							Log.e("like Post", fb.request("CococodeCo/likes", params, "POST"));
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} 
//	                    
//	                    
////	                    String url = "http://www.facebook.com/plugins/like.php?" +
////	                            "href=" + URLEncoder.encode( "http://www.facebook.com/TheChennaiSuperKings" ) + "&" +
////	                            "layout=standard&" +
////	                            "show_faces=false&" +
////	                            "width=375&" +
////	                            "action=like&" +
////	                            "colorscheme=light&" +
////	                            "access_token=" + URLEncoder.encode( fb.getAccessToken() );
////	                    Uri uriUrl = Uri.parse(url);
////	            		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
////	            		startActivity(launchBrowser);
//
//		        		AlertDialog iDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
//		                iDialog.setIcon(R.drawable.ic_launcher);
//		                iDialog.setTitle("驗證成功");
//		                iDialog.setMessage("您是粉絲團成員！");
//		                
//		                iDialog.setCancelable(true);
//		                iDialog.setButton("確定", new DialogInterface.OnClickListener() {
//		                    public void onClick(DialogInterface dialog, int which) {
//		                        dialog.dismiss();
//		                        ((MainActivity)getActivity()).onClickLockConfirm();
//		                    }
//		                });            
//		                iDialog.setOnCancelListener(new OnCancelListener(){
//		                    @Override
//		                    public void onCancel(DialogInterface dialog){
//		                    	dialog.dismiss();
//		                 }});  
//		                
//		                iDialog.show();
//		        	}
//
//					@Override
//					public void onFacebookError(FacebookError e) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onError(DialogError e) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onCancel() {
//						// TODO Auto-generated method stub
//						Log.d("Info", "Login Canceled");
//						
//					}
//
//		        });
//		        
////		    	// start Facebook Login
////		        Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {
////
////				@Override
////				public void call(Session session, SessionState state,
////						Exception exception) {
////					// TODO Auto-generated method stub
////		            if (session.isOpened()) {
////
////		                // make request to the /me API
////		                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
////
////		                  // callback after Graph API response with user object
////
////		  				@Override
////		  				public void onCompleted(GraphUser user, Response response) {
////		  					// TODO Auto-generated method stub
////		  					if (user != null)
////		  						((DragonMainActivity)getActivity()).onClickLockConfirm();
////		  				}
////		                });
////		              }
////					
////				}
////		        });
//		        
////		        SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
////		        String actId = sp.getString("actId", "");
////		        String tel = sp.getString("tel", "");
////			         
////				mLoadingProgress = new LoadingDialog(getActivity());
////				mLoadingProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////				mLoadingProgress.show();
////				
////		        AsyncCallWS task = new AsyncCallWS();
////				task.execute(actId, tel);
			}
    		
    	});
     
//    	if (bRegSuccess) {
//        	AlertDialog iDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
//            iDialog.setIcon(R.drawable.ic_launcher);
//            iDialog.setTitle("驗證成功");
//            iDialog.setMessage("您是粉絲團成員！");
//            
//            iDialog.setCancelable(true);
//            iDialog.setButton("確定", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    ((MainActivity)getActivity()).onClickLockConfirm();
//                }
//            });            
//            iDialog.setOnCancelListener(new OnCancelListener(){
//                @Override
//                public void onCancel(DialogInterface dialog){
//                	dialog.dismiss();
//             }});  
//            
//            iDialog.show();
//    	}
    	
        return view;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	//fb.authorizeCallback(requestCode, resultCode, data);

        switch (requestCode) {
        /*
         * if this is the activity result from authorization flow, do a call
         * back to authorizeCallback Source Tag: login_tag
         */
            case AUTHORIZE_ACTIVITY_RESULT_CODE: {
                break;
            }
            case LIKE_ACTIVITY_RESULT_CODE:{
            	if (resultCode == -1 /*RESULT_OK*/){
            		boolean liked = data.getExtras().getBoolean(Constants.Common.INTENT_FB_LIKE);
            		if (mFacebookLiked == liked)
            			break;
            		mFacebookLiked = liked;
            		if (liked) {
        				SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
        				Editor editor = sp.edit();
        				editor.putBoolean("isLike", liked);
        				editor.commit();
            			
    					mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "您是粉絲團成員！", "確定", new OnClickListener() {

    						@Override
    						public void onClick(View v) {
    							// TODO Auto-generated method stub
    							mDialog.dismiss();
    							//因為修改新流程關係故不需要此行
    							//((MainActivity)getActivity()).onClickLockConfirm();
    						}
    		            });
    					mDialog.show();
            		}
            	}
            	break;
            }
        }
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
				mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "隱藏版型已解鎖", "確定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
						//因為修改新流程關係故不需要此行
						//((MainActivity)getActivity()).onClickLockConfirm();
					}
	            });
				mDialog.show();
			}
			else {
				mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "未加入粉絲團", "確定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
						((MainActivity)getActivity()).onClickLockRegister();
					}
	            });
				mDialog.show();
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
