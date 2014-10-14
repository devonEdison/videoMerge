package com.dragonplayer.merge.fragment;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

import com.dragonplayer.merge.LoadingDialog;
import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.ProjectListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.utils.AppConnectivity;
import com.dragonplayer.merge.utils.BannerData;
import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.DlgWindow1;
import com.dragonplayer.merge.utils.DlgWindow4;
import com.dragonplayer.merge.utils.MLog;
import com.dragonplayer.merge.utils.Utils;
import com.dragonplayer.merge.utils.WeTouch_network_interface;
import com.dragonplayer.merge.utils.WeTouch_uploadImage;
import com.dragonplayer.merge.utils.WebService;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author 輸入上傳資訊
 *
 */
public class UploadInfoFragment extends Fragment {

	EditText editUserName;
	EditText editEMail;
	EditText editTel;
	ImageView imgMedia;

	String filePath;
	Button btnRegister;
	LoadingDialog mLoadingProgress;

	String strUserName = "";
	String strEMail = "";
	String strTel = "";
	Dialog mDialog;
	
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        MLog.e(this,"onActivityCreated");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        
        filePath = getArguments().getString("filepath");

        if (bundle != null) {
	        if (bundle.containsKey("editusername"))
	        	strUserName = bundle.getString("editusername");
	        if (bundle.containsKey("editemail"))
	        	strEMail = bundle.getString("editemail");
	        if (bundle.containsKey("edittel"))
	        	strTel = bundle.getString("edittel");
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
    	
    	bundle.putString("editusername", editUserName.getText().toString());
    	bundle.putString("editemail", editEMail.getText().toString());
    	bundle.putString("edittel", editTel.getText().toString());
        
        super.onSaveInstanceState(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.upload_info_fragment, viewgroup, false);
        
    	editUserName = (EditText) view.findViewById(R.id.username);
    	editEMail = (EditText) view.findViewById(R.id.email);
    	editTel = (EditText) view.findViewById(R.id.tel);

    	editUserName.setText(strUserName);
    	editEMail.setText(strEMail);
    	editTel.setText(strTel);
    	
    	imgMedia = (ImageView) view.findViewById(R.id.imgmedia);
    	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgMedia.getLayoutParams();
    	DisplayMetrics displaymetrics = getActivity().getResources().getDisplayMetrics();
    	params.height = displaymetrics.widthPixels * 120 / 721;
    	imgMedia.setLayoutParams(params);
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
        
    	imgMedia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).onClickUploadMedia();
			}
    		
    	});
    	
    	btnRegister = (Button) view.findViewById(R.id.btnregister);
    	btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String username = editUserName.getText().toString();
				String tel = editTel.getText().toString();
				String email = editEMail.getText().toString();

				if (username.isEmpty() || tel.isEmpty() || email.isEmpty()) {
					
					mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "欄位不能空白", "確定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mDialog.dismiss();
						}
		            });
					mDialog.show();
		            return;
				}
				if (tel.length()<7) {
					mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "電話輸入錯誤", "確定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mDialog.dismiss();
						}
		            });
					mDialog.show();
		            return;
				}
				if (!isValidVerification(email)) {
					mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "身份證字號錯誤", "確定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mDialog.dismiss();
						}
		            });
					mDialog.show();
		            return;
				}
				
				mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "上傳後不可修改內容", "確定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();

	                    AppConnectivity connection = new AppConnectivity(getActivity().getApplicationContext());
	    		        boolean isInternet = connection.isConnectingToInternet();
	    		        
	    		        if (!isInternet) {
	    					mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "請檢查網際網路連線", "確定", new OnClickListener() {

	    						@Override
	    						public void onClick(View v) {
	    							// TODO Auto-generated method stub
	    							mDialog.dismiss();
	    						}
	    		            });
	    					mDialog.show();
	    		            return;
	    		        }

	    		        String actId = String.valueOf(Calendar.getInstance().getTimeInMillis());
	    				String username = editUserName.getText().toString();
	    				String tel = editTel.getText().toString();
	    				String email = editEMail.getText().toString();
	    		        String facebook = "";
	    			         
	    				mLoadingProgress = new LoadingDialog(getActivity());
	    				mLoadingProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	    				mLoadingProgress.show();
	    				
	    		        AsyncCallWS task = new AsyncCallWS();
	    				task.execute(actId, username, tel, email, facebook);
					}
	            });
				mDialog.show();
			}
    		
    	});
        
        return view;
    }

    private boolean isValidVerification(String id) {
    	int cityCode[] = {10, 11, 12, 13, 14, 15, 16, 17, 34, 18, 19, 20, 21, 22, 35, 23, 24, 25, 26, 27, 
    					28, 29, 32, 30, 31, 33};
    	int mulCode[] = {1, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    	int idCode[] = new int[10];
    	int verificationCode = 0;
    	int i;
    	
    	if (id.length() != 10)
    		return false;
    	
    	if (id.charAt(0) < 'A' || id.charAt(0) > 'Z')
    		return false;

    	idCode[0] = cityCode[id.charAt(0) - 'A'] / 10;
    	idCode[1] = cityCode[id.charAt(0) - 'A'] % 10;
    	
    	for (i = 1; i < 10; i++) {
        	if (id.charAt(i) < '0' || id.charAt(i) > '9')
        		return false;
        	
        	if (i < 9) {
        		idCode[i+1] = id.charAt(i) - '0';
        	}
        	else {
        		verificationCode = id.charAt(i) - '0';
        	}
    	}
    	
    	int sum = 0;
    	for (i = 0; i < 10; i++) {
    		sum = sum + mulCode[i] * idCode[i]; 
    	}
    	
    	if ((10 - (sum % 10)) == verificationCode)
    		return true;
    	
    	return false;
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
		String actId;
		String username;
		String tel;
		String email;
		String facebook;
		
		@Override
		protected Void doInBackground(String... params) {
			actId = params[0];
			username = params[1];
			tel = params[2];
			email = params[3];
			facebook = params[4];
			
			mResult = WebService.JAct(actId, username, tel, email, facebook, "JAct");
	        
			WeTouch_uploadImage task = new WeTouch_uploadImage(getActivity(), new WeTouch_network_interface() {

				@Override
				public void onImageUploadComplete(String result) {
					// TODO Auto-generated method stub
					Log.e("upload result", result);
				}
				
			});
			
			task.execute("http://211.78.89.41:81/pp/upload_test/upload_test.php", filePath);
			 
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mLoadingProgress != null) {
				mLoadingProgress.dismiss();
				mLoadingProgress = null;
			}
			
			if (mResult.toUpperCase().equals("YES")) {
				SharedPreferences sp = getActivity().getSharedPreferences("iDragon", Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("actId", actId);
				editor.putString("tel", tel);
				editor.putString("username", username);
				editor.putString("email", email);
				editor.putString("filepath", filePath);
				editor.commit();
				
				mDialog = new DlgWindow4(getActivity(), R.style.CustomDialog, "上傳成功", "確定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//((MainActivity)getActivity()).onClickUploadConfirm(actId, username, email, tel, filePath);
						//修改為新流程.將資訊傳送到解鎖版面
						((MainActivity)getActivity()).onClickLockConfirm(actId, username, email, tel, filePath);
						mDialog.dismiss();
					}
	            });
				mDialog.show();
			}
			else {
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
