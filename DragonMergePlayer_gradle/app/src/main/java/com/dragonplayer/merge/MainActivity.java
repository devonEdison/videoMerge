package com.dragonplayer.merge;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.*;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragonplayer.merge.fragment.AboutFragment;
import com.dragonplayer.merge.fragment.AddAudioFragment;
import com.dragonplayer.merge.fragment.FinishFragment;
import com.dragonplayer.merge.fragment.FrameFragment;
import com.dragonplayer.merge.fragment.HomeFragment;
import com.dragonplayer.merge.fragment.LockConfirmFragment;
import com.dragonplayer.merge.fragment.LockFragment;
import com.dragonplayer.merge.fragment.UploadConfirmFragment;
import com.dragonplayer.merge.fragment.UploadFragment;
import com.dragonplayer.merge.fragment.UploadInfoFragment;
import com.dragonplayer.merge.fragment.UploadSelectMediaFragment;
import com.dragonplayer.merge.fragment.WorkFragment;
import com.dragonplayer.merge.frames.FramesLayout;
import com.dragonplayer.merge.utils.BannerData;
import com.dragonplayer.merge.utils.BitmapManager;
import com.dragonplayer.merge.utils.DlgWindow4;
import com.dragonplayer.merge.utils.Flag;
import com.dragonplayer.merge.utils.Utils;
import com.dragonplayer.merge.utils.WebService;
import com.loopj.android.image.SmartImageView;
 
@SuppressLint("ResourceAsColor")
/** 
 * 
 * @author 主畫面
 *
 */
public class MainActivity extends FragmentActivity {

    private static final int HOME_PAGE = 1;
    private static final int WORKING_PAGE = 2;
    private static final int RENDERING_PAGE = 4;
    private static final int LOCK_PAGE = 5;
//    private static final int LOCKREGISTER_PAGE = 6;
    private static final int LOCKCONFIRM_PAGE = 7;
    private static final int UPLOAD_PAGE = 8;
    private static final int UPLOADINFO_PAGE = 9;
    private static final int UPLOADSELMEDIA_PAGE = 10;
    private static final int UPLOADCONFIRM_PAGE = 11;
    private static final int ADDAUDIO_PAGE = 12;
    private static final int ABOUT_PAGE = 13;
    private static final int BARSTYLE_LEFT = 0;
    private static final int BARSTYLE_RIGHT = 1;
    private static final int BARSTYLE_NONE = 2;
    private static final int BARSTYLE_BOTH = 3;
    
    private int current_page;
    private FinishFragment finishFragment;
    private WorkFragment workFragment;
    private HomeFragment homeFragment;
    private LockFragment lockFragment;
    private LockConfirmFragment lockconfirmFragment;
    private UploadFragment uploadFragment;
    private UploadInfoFragment uploadinfoFragment;
    private UploadSelectMediaFragment uploadselmediaFragment;
    private UploadConfirmFragment uploadconfirmFragment;
    private AddAudioFragment addaudioFragment;
    private AboutFragment aboutFragment;
    
    private int lastFrameSelected;
    private DrawerLayout mDrawerLayout;
    private BitmapManager manager;
    private ImageView txtTitle;
    
    private SmartImageView imgBanner;
    private ImageButton btnBack;
    private ImageButton btnComplete;
    private ImageButton btnNew;
    
    private LoadingDialog mLoadingProgress;
    Dialog mDialog;
    
    private int mBeforePageNum = -1;
    
    private ArrayList<Integer> mImageLoadCheckList = new ArrayList<Integer>(); 
    
    public MainActivity() {
        lastFrameSelected = -1;
    }

	public void onClickViewProject(String filePath) {
        if(current_page != RENDERING_PAGE) {
        	bannerHeight(true);
        	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_15);

            if(finishFragment == null)
                finishFragment = new FinishFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("frameID", lastFrameSelected);
            bundle.putString("renderFile", filePath);
            finishFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.content_frame, finishFragment, "finish").commit();

            current_page = RENDERING_PAGE;
        }
	}

	public void FinishPage() {
		FinishPage("");
		
    }

	public void FinishPage(String audioPath) {
        if(current_page != RENDERING_PAGE) {
        	bannerHeight(true);
        	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_15);

            if(finishFragment == null)
                finishFragment = new FinishFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("frameID", lastFrameSelected);
            bundle.putString("audioPath", audioPath);
            finishFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.content_frame, finishFragment, "finish").commit();
            current_page = RENDERING_PAGE;
        }
    }

    public void HomePage() {
        if(current_page != HOME_PAGE) {
        	bannerHeight(true);
        	initTitleBar(BARSTYLE_RIGHT, R.drawable.icon_03, false);

            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if(homeFragment == null)
                homeFragment = new HomeFragment();
            
            fragmenttransaction.replace(R.id.content_frame, homeFragment, "home").commit();
            current_page = HOME_PAGE;
        }
    }

    public void SelectedFramePage(int i) {
    	SelectedFramePage(i, false);
    }
    
    public void SelectedFramePage(int i, boolean bNew) {
        if(current_page == WORKING_PAGE)
            return;
    	
        bannerHeight(false);
        initTitleBar(BARSTYLE_LEFT, R.drawable.txt_10);

        if(workFragment == null) {
            workFragment = new WorkFragment();
        }
        
        Bundle bundle = new Bundle();
        if (bNew) {
        	bundle.putInt("frameID", -1);
        	lastFrameSelected = -1;
        }
        else {
	        if(i != -1) {
	            bundle.putInt("frameID", i);
	            lastFrameSelected = i;
	        } 
	        else {
	            bundle.putInt("frameID", lastFrameSelected);
	        }
        }

        bundle.putBoolean("NewProject", bNew);
        Log.e("Workfragment", "NewProject="+lastFrameSelected);
        workFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, workFragment, "frame").commit();
        
        current_page = WORKING_PAGE;
    }

	public void onClickList() {
		HomePage();
		mDrawerLayout.closeDrawers();
	}

	public void onClickUpload() {
        if(current_page != UPLOAD_PAGE) {
        	bannerHeight(true);
        	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_11);

            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if(uploadFragment == null)
            	uploadFragment = new UploadFragment();
            
            fragmenttransaction.replace(R.id.content_frame, uploadFragment, "upload").commit();
            current_page = UPLOAD_PAGE;
        }
        
        mDrawerLayout.closeDrawers();
	}

	public void onClickUploadInfo() {
		onClickUploadInfo("");
	}
	
	public void onClickUploadInfo(String path) {
        if(current_page != UPLOADINFO_PAGE) {
        	bannerHeight(true);
        	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_13);

            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if(uploadinfoFragment == null)
            	uploadinfoFragment = new UploadInfoFragment();

            Bundle bundle = new Bundle();
            bundle.putString("filepath", path);
            uploadinfoFragment.setArguments(bundle);
            
            fragmenttransaction.replace(R.id.content_frame, uploadinfoFragment, "uploadinfo").commit();
            current_page = UPLOADINFO_PAGE;
            
            mDrawerLayout.closeDrawers();
        }
	}

	public void onClickUploadMedia() {
        if(current_page != UPLOADSELMEDIA_PAGE) {
        	bannerHeight(true);
        	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_14);

            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if(uploadselmediaFragment == null)
            	uploadselmediaFragment = new UploadSelectMediaFragment();
            
            fragmenttransaction.replace(R.id.content_frame, uploadselmediaFragment, "uploadselectmedia").commit();
            current_page = UPLOADSELMEDIA_PAGE;
            
            mDrawerLayout.closeDrawers();
        }
	}

	public void onClickUploadConfirm(String actId, String username, String email, String tel, String filepath) {
        if(current_page != UPLOADCONFIRM_PAGE) {
        	 bannerHeight(true);
        	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_08);

            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if(uploadconfirmFragment == null)
            	uploadconfirmFragment = new UploadConfirmFragment();

            Bundle bundle = new Bundle();
            bundle.putString("actId", actId);
            bundle.putString("username", username);
            bundle.putString("email", email);
            bundle.putString("tel", tel);
            bundle.putString("filepath", filepath);
            uploadconfirmFragment.setArguments(bundle);

            fragmenttransaction.replace(R.id.content_frame, uploadconfirmFragment, "uploadconfirm").commit();
            current_page = UPLOADCONFIRM_PAGE;
            mDrawerLayout.closeDrawers();
 
            
        }
	}

	public void onClickAddAudio(String videoPath) {
        if(current_page != ADDAUDIO_PAGE) {
        	bannerHeight(false);
        	initTitleBar(BARSTYLE_BOTH, R.drawable.icon_09, true);

            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if(addaudioFragment == null)
            	addaudioFragment = new AddAudioFragment();

            Bundle bundle = new Bundle();
            bundle.putString("videoPath", videoPath);
            addaudioFragment.setArguments(bundle);

            fragmenttransaction.replace(R.id.content_frame, addaudioFragment, "addaudio").commit();
            
            current_page = ADDAUDIO_PAGE;
            
            mDrawerLayout.closeDrawers();
           
        }
	}

	public void onClickLock() {
        if(current_page != LOCK_PAGE) {
        	mBeforePageNum = current_page;
        	bannerHeight(true);
        	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_07);

            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if(lockFragment == null)
            	lockFragment = new LockFragment();
            
            fragmenttransaction.replace(R.id.content_frame, lockFragment, "lock").commit();

            current_page = LOCK_PAGE;
            mDrawerLayout.closeDrawers();
        }
	}

	public void onClickLockRegister() {
//    	bannerHeight(true);
//        btnComplete.setVisibility(View.INVISIBLE);
//        btnNew.setVisibility(View.INVISIBLE);
//
//        if(current_page != LOCKREGISTER_PAGE) {
//            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//
//            if(lockregisterFragment == null)
//            	lockregisterFragment = new LockRegisterFragment();
//            
//            fragmenttransaction.replace(R.id.content_frame, lockregisterFragment, "lockregister").commit();
//
//            getActionBar().setTitle(R.string.lockregister);
//            txtTitle.setText(R.string.lockregister);
//            
//            current_page = LOCKREGISTER_PAGE;
//        }
//        
//        mDrawerLayout.closeDrawers();
		
		Uri uriUrl = Uri.parse("https://www.facebook.com/CococodeCo");
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		startActivity(launchBrowser);
	}
	// 原本舊流程不需要傳入資訊,因為新流程所以更改傳入資料
	//public void onClickLockConfirm() {
	public void onClickLockConfirm(String actId, String username, String email, String tel, String filepath) {
        if(current_page != LOCKCONFIRM_PAGE) {
        	bannerHeight(true);
        	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_12);

            FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if(lockconfirmFragment == null)
            	lockconfirmFragment = new LockConfirmFragment();
            /** 因為更改新流程所以傳入資訊*/
            Bundle bundle = new Bundle();
            bundle.putString("actId", actId);
            bundle.putString("username", username);
            bundle.putString("email", email);
            bundle.putString("tel", tel);
            bundle.putString("filepath", filepath);
            lockconfirmFragment.setArguments(bundle);
            
            fragmenttransaction.replace(R.id.content_frame, lockconfirmFragment, "lockconfirm").commit();

            current_page = LOCKCONFIRM_PAGE;
            mDrawerLayout.closeDrawers();
        }
       
	}
	 public void onClickAbout() {
         if(current_page != ABOUT_PAGE ) {
         	bannerHeight(true);
         	initTitleBar(BARSTYLE_LEFT, R.drawable.txt_11);

             FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

             if(aboutFragment == null)
            	 aboutFragment = new AboutFragment();
             
             fragmenttransaction.replace(R.id.content_frame, aboutFragment, "about").commit();
             current_page = ABOUT_PAGE ;
         }
         
         mDrawerLayout.closeDrawers();
 	}
	 
	public BitmapManager getManager() {
        return manager;
    }

    protected void onActivityResult(int i, int j, Intent intent) {
    	Utils.writeLogToFile("---------------Main ActivityResult---------------");
    	Utils.writeLogToFile("i="+i+":j="+j);
        Log.d("onActivityResult", "main activity");
        super.onActivityResult(i, j, intent);
        
        if (current_page == LOCK_PAGE && lockFragment != null) 
        	lockFragment.onActivityResult(i, j, intent);
    }

    public void onBackPressed() {
    	
    	onClickBack();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        setContentView(R.layout.activity_main);
        setManager(new BitmapManager(getApplicationContext(), mHandler));
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(0);
        //Flag.PUBLIC = true;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().hide();
    
        txtTitle = (ImageView) findViewById(R.id.txttitle);

        imgBanner = (SmartImageView) findViewById (R.id.imgbanner);
        imgBanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickBanner();
			}
        	
        });
        
        imgBanner.setVisibility(View.VISIBLE);
        imgBanner.setBackground(null);
        bannerHeight(true);
        AsyncCallWS task = new AsyncCallWS();
		task.execute("Banner");

        btnBack = (ImageButton)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickBack();
			}
        	
        });
        
        btnComplete = (ImageButton)findViewById(R.id.btn_complete);
        btnComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        if(current_page == WORKING_PAGE) {
			        if(workFragment == null) {
			            return;
			        }
			        
			        if (!workFragment.isExistContent()) {
    					mDialog = new DlgWindow4(MainActivity.this, R.style.CustomDialog, "尚有內容未放入", "確定", new OnClickListener() {

    						@Override
    						public void onClick(View v) {
    							// TODO Auto-generated method stub
    							mDialog.dismiss();
    						}
    		            });
    					mDialog.show();
			        	return;
			        }
			        
			        String videoPath = workFragment.getVideoPath();
			        if (videoPath == null) {
			        	if (workFragment.getFrameID() == FramesLayout.FRAMEID_EAT || 
			        			workFragment.getFrameID() == FramesLayout.FRAMEID_HAPPYBIRTHDAY)
			        		onClickAddAudio(videoPath);
			        	else
			        		FinishPage();
			        }
			        else 
			        	onClickAddAudio(videoPath);
		        }
		        else if (current_page == ADDAUDIO_PAGE) {
		        	FinishPage(addaudioFragment.getAudio());
		        }
			}
        	
        });

        btnNew = (ImageButton)findViewById(R.id.btn_new);
        btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickNewProject();
			}
        	
        });

        initTitleBar(BARSTYLE_RIGHT, R.drawable.icon_03);
        
        if(bundle == null) 
        	HomePage();
        else {
	        int curPage;
	        curPage = bundle.getInt("current_page");
	        lastFrameSelected = bundle.getInt("frame_id");
	        Log.d("current_page", (new StringBuilder()).append(curPage).toString());
	        switch (curPage) {
	        case HOME_PAGE:
	        	HomePage();
	        	break;
	        case WORKING_PAGE:
	        	bannerHeight(false);
	        	SelectedFramePage(lastFrameSelected);
	        	break;
	        case RENDERING_PAGE:
	        	FinishPage();
	        	break;
	        }
        }
    }

    void initTitleBar(int style, int txtId) {
    	initTitleBar(style, txtId, false);	
    }
    
    void initTitleBar(int style, int txtId, boolean bComplete) {
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

    	btnparams = (RelativeLayout.LayoutParams)btnNew.getLayoutParams();
    	btnparams.width = params.height; 
    	btnparams.height = params.height;
    	btnNew.setLayoutParams(btnparams);

        BitmapFactory.Options options = new BitmapFactory.Options();
        
        options.inJustDecodeBounds = true;
        options.inDensity = 180;
        BitmapFactory.decodeResource(getResources(), txtId, options);
    	
    	RelativeLayout.LayoutParams txtparams = (RelativeLayout.LayoutParams)txtTitle.getLayoutParams();
    	txtparams.height = params.height * options.outHeight / 81;
    	txtparams.width = txtparams.height * options.outWidth / options.outHeight;
    	txtTitle.setBackgroundResource(txtId);
    	txtTitle.setLayoutParams(txtparams);

    	switch (style) {
    	case BARSTYLE_LEFT:
    		titlebar.setBackgroundResource(R.drawable.bar_1);
        	btnBack.setVisibility(View.VISIBLE);
        	btnComplete.setVisibility(View.INVISIBLE);
            btnNew.setVisibility(View.INVISIBLE);
    		break;
    	case BARSTYLE_RIGHT:
    		titlebar.setBackgroundResource(R.drawable.bar_2);
        	btnBack.setVisibility(View.INVISIBLE);
        	if (bComplete) {
        		btnComplete.setVisibility(View.VISIBLE);
        		btnNew.setVisibility(View.INVISIBLE);
        	}
        	else {
        		btnComplete.setVisibility(View.INVISIBLE);
        		btnNew.setVisibility(View.VISIBLE);
        	}
    		break;
    	case BARSTYLE_NONE:
    		titlebar.setBackgroundResource(R.drawable.bar_3);
        	btnBack.setVisibility(View.INVISIBLE);
        	btnComplete.setVisibility(View.INVISIBLE);
            btnNew.setVisibility(View.INVISIBLE);
    		break;
    	case BARSTYLE_BOTH:
    		titlebar.setBackgroundResource(R.drawable.bar_4);
        	btnBack.setVisibility(View.VISIBLE);
        	if (bComplete) {
        		btnComplete.setVisibility(View.VISIBLE);
        		btnNew.setVisibility(View.INVISIBLE);
        	}
        	else {
        		btnComplete.setVisibility(View.INVISIBLE);
        		btnNew.setVisibility(View.VISIBLE);
        	}
    		break;
    	}
    }
    
    protected void onDestroy() {
    	Log.d("onDestroy", "main activity");
//        Utils.setDatas(getApplicationContext(), "");
//        Utils.deleteAllJPGS();
        super.onDestroy();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
            return super.onOptionsItemSelected(menuitem);
    }

    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("current_page", current_page);
        bundle.putInt("frame_id", lastFrameSelected);
        super.onSaveInstanceState(bundle);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    public void setManager(BitmapManager bitmapmanager) {
        manager = bitmapmanager;
    }

    private Handler mHandler = new Handler(new Callback() {
		public boolean handleMessage(Message msg) {
			
			if (msg.what == BitmapManager.BITMAPLOADSUCCESS) {
				mImageLoadCheckList.add(msg.arg1);
				
		        if(current_page == WORKING_PAGE) {
			        if(workFragment != null) {
			        	workFragment.setFrameBitmap((Bitmap)msg.obj, msg.arg1);
			        }
		        }
		        
		        if(current_page == RENDERING_PAGE) {
			        if(finishFragment != null) {
			        	finishFragment.setFrameBitmap((Bitmap)msg.obj, msg.arg1);
			        }
		        }
			}
			else if (msg.what == BitmapManager.BITMAPLOADSUCCESS) {
			}
			
			return true;
		}
	});
    
    public void clearImageLoadCheckList() {
    	mImageLoadCheckList.clear();
    }

    public int getImageLoadCheckListCount() {
    	return mImageLoadCheckList.size();
    }
    
    public void refreshPage(int curItem) {
    	if (homeFragment != null)
    		homeFragment.refreshPage(curItem);
    }

    public void onClickNewProject() {
		SelectedFramePage(-1, true);
		mDrawerLayout.closeDrawers();
    }

    public void selectFrameFragment(int nFrameNum) {
    	if(workFragment != null) {
            lastFrameSelected = nFrameNum;
    		workFragment.setFrame(nFrameNum);
    	}
    }
    
    public void selectFromGallery() {
    	if(workFragment != null)
    		workFragment.selectFromGallery();
    }

    public void changePosImage() {
    	if(workFragment != null)
    		workFragment.changePosImage();
    }

    public void onClickBanner() {
		if (mBannerData.size() == 0)
			return;
		
		Uri uriUrl = Uri.parse(((BannerData)mBannerData.get(mCurBannerIndex)).url);
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		startActivity(launchBrowser);
	}

	public void onCompleteVisibility(int value) {
		if (value == View.VISIBLE) 
			initTitleBar(BARSTYLE_BOTH, R.drawable.txt_10, true);
		else
			initTitleBar(BARSTYLE_LEFT, R.drawable.txt_10);
	}
	
	public void onClickLockConfirmBack() {
        if(mBeforePageNum == HOME_PAGE) {
        	HomePage();
        } 
        else if(mBeforePageNum == WORKING_PAGE) {
        	bannerHeight(false);
        	SelectedFramePage(lastFrameSelected);
        } 
        else if(mBeforePageNum == UPLOAD_PAGE) {
        	onClickUpload();
        } 
        else {
        	HomePage();
        }
	
	}
	/**
	 * 針對再不同的Fragment 做不一樣的動作
	 */
	public void onClickBack() {
        if(current_page == HOME_PAGE) {
        	super.onBackPressed();
        } 
        else if(current_page == WORKING_PAGE) {
        	HomePage();
        } 
        else if(current_page == RENDERING_PAGE) {
        	if (!finishFragment.isRendered()) {
	        	bannerHeight(false);
	        	SelectedFramePage(lastFrameSelected);
        	}
        	else {
        		HomePage();
        	}
        } 
        else if(current_page == LOCK_PAGE) {
        	HomePage();
        } 
        else if(current_page == LOCKCONFIRM_PAGE) {
        	//onClickLock();
        	HomePage();
        } 
        else if(current_page == UPLOAD_PAGE) {
        	HomePage();
        } 
        else if(current_page == UPLOADINFO_PAGE) {
        	onClickUpload();
        } 
        else if(current_page == UPLOADSELMEDIA_PAGE) {
        	onClickUploadInfo();
        } 
        else if(current_page == UPLOADCONFIRM_PAGE) {
        	//onClickUploadInfo();
        	HomePage();
        }
        else if (current_page == ADDAUDIO_PAGE) {
        	bannerHeight(false);
        	SelectedFramePage(lastFrameSelected);
        }
        else {
        	HomePage();
        }
	
	}
	
	public void bannerHeight(boolean bVisible) {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)imgBanner.getLayoutParams();
        
        if (bVisible) {
        	DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        	param.height = displaymetrics.widthPixels / 6;
        }
        else {
        	param.height = 0;
        }
        
        imgBanner.setLayoutParams(param);

	}
	
	private class AsyncCallWS extends AsyncTask<String, Void, Void> {
		
		private String mResult;
		
		@Override
		protected Void doInBackground(String... params) {
			mResult = WebService.Banner("Banner");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		
	        JSONObject jsonobject;
			try {
				JSONArray jsonarray = new JSONArray(mResult);
				mBannerData.clear();
				for (int i = 0; i < jsonarray.length(); i++) {
					BannerData data = new BannerData();
					jsonobject = jsonarray.getJSONObject(i);
					
					data.banner = jsonobject.getString("Banner");
					data.content = jsonobject.getString("BannerContent");
					data.url = jsonobject.getString("Url");
					
					mBannerData.add(data);
				}
				
				mBannerHandler.sendEmptyMessage(0);
				mCurBannerIndex = 0;
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

	ArrayList mBannerData = new ArrayList();
	int mCurBannerIndex = 0;
    private Handler mBannerHandler = new Handler(new Callback() {
		public boolean handleMessage(Message msg) {
			
			if (mBannerData.size() == 0)
				return true;
			
			imgBanner.setImageUrl("http://211.78.89.41/ap/upload/" + ((BannerData)mBannerData.get(mCurBannerIndex)).banner);
	        mCurBannerIndex = (mCurBannerIndex + 1) % mBannerData.size(); 

			return true;
		}
	});
}
