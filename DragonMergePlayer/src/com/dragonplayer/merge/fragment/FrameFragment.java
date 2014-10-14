package com.dragonplayer.merge.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.*;
import android.content.pm.*;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.frames.*;
import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.Utils;
import java.io.*;
import java.util.*;
import org.json.JSONException;
import org.json.JSONObject;

public class FrameFragment extends Fragment
{
    private static final int RECORD_VIDEO = 44;
    private static final int SELECT_PHOTO = 11;
    private static final int SELECT_TRACK = 55;
    private static final int SELECT_VIDEO = 33;
    private static final int TAKE_PHOTO = 22;
    private Uri audioUri;
    private com.dragonplayer.merge.frames.FrameView.CorruptVideoError error;
    private Frame frame;
    private int frameID;
    private FramesLayout frameLayout;
    private float initialY;
    private boolean isAnimating;
    private String mBaseFolderPath;
    private Dialog optionsDialog;
    private final ArrayList order = new ArrayList();
    private Uri outputFileUri;
    private LinearLayout recordVideoBtn;
    private LinearLayout selectPhotoBtn;
    private LinearLayout selectVideoBtn;
    private TextView selectedTrack;
    private LinearLayout takePhotoBtn;
    private ImageView upgrade;
    private Uri videoUri;
    private int nActivityResultValue = 0;
    private static String strActivityResultPath = "";

    public FrameFragment() {
        initialY = 0.0F;
        isAnimating = false;
    }

    private void startCameraIntent() {
        outputFileUri = Uri.fromFile(new File((new StringBuilder(String.valueOf(mBaseFolderPath))).append(File.separator).append("picture_").append(Calendar.getInstance().getTimeInMillis()).append(".jpg").toString()));
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", outputFileUri);
        
        try {
            startActivityForResult(intent, TAKE_PHOTO);
        }
        catch(ActivityNotFoundException activitynotfoundexception) {
            activitynotfoundexception.printStackTrace();
        }
        
        try {
            frameLayout.addBitmap(outputFileUri.getPath(), null);
        }
        catch(Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void startGalleryIntent() {
        ArrayList arraylist = new ArrayList();
        List list;
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        ResolveInfo resolveinfo;
        Intent intent3;

        intent.setType("image/*");
        list = getActivity().getPackageManager().queryIntentActivities(intent, 0);

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
	        resolveinfo = (ResolveInfo)iterator.next();
	        intent3 = new Intent("android.intent.action.GET_CONTENT");
	        intent3.setType("image/*");
	        
	        if(resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains("gallery") || resolveinfo.activityInfo.name.toLowerCase(Locale.US).contains("gallery") || resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains("album")) {
	            intent3.setPackage(resolveinfo.activityInfo.packageName);
	            arraylist.add(intent3);
	        }
        }

        if(arraylist.size() > 0) {
            Intent intent2 = Intent.createChooser((Intent)arraylist.remove(0), "Select Source");
            intent2.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[])arraylist.toArray(new Parcelable[0]));
            startActivityForResult(intent2, SELECT_PHOTO);
        } 
        else {
            Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
            intent1.setType("image/*");
            startActivityForResult(intent1, SELECT_PHOTO);
        }
    }

    private void startVideoIntent() {
        videoUri = Uri.fromFile(new File((new StringBuilder(String.valueOf(mBaseFolderPath))).append(File.separator).append("video_").append(Calendar.getInstance().getTimeInMillis()).append(".mp4").toString()));
        
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        intent.putExtra("android.intent.extra.durationLimit", 15);
        intent.putExtra("android.intent.extra.videoQuality", 1);
        intent.putExtra("output", videoUri);

        startActivityForResult(intent, RECORD_VIDEO);
    }

    private void startVideoPickerIntent() {
        ArrayList arraylist = new ArrayList();
        List list;
        ResolveInfo resolveinfo;

        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("video/*");
        list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        
        if(!list.isEmpty()) { 
	        Iterator iterator = list.iterator();
	        while (iterator.hasNext()) {
	            resolveinfo = (ResolveInfo)iterator.next();
	            intent = new Intent("android.intent.action.GET_CONTENT");
	            intent.setType("video/*");
	            
	            if(resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains("gallery") || resolveinfo.activityInfo.name.toLowerCase(Locale.US).contains("gallery") || resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains("album")) {
	                intent.setPackage(resolveinfo.activityInfo.packageName);
	                arraylist.add(intent);
	            }
	        }
        }
        
        if(arraylist.size() > 0) {
            intent = Intent.createChooser((Intent)arraylist.remove(0), "Select Source");
            intent.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[])arraylist.toArray(new Parcelable[0]));
            startActivityForResult(intent, SELECT_VIDEO);
        } 
        else {
            intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("video/*");
            startActivityForResult(intent, SELECT_VIDEO);
        }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        nActivityResultValue = 0;
        Log.d("onActivityResult", "fragment");
        Utils.writeLogToFile("FrameFragment-onActivityResult-Start, i="+requestCode+":j="+resultCode);
        
        super.onActivityResult(requestCode, resultCode, data);
    
        if(resultCode != -1) 
        	return;

        if(requestCode == SELECT_PHOTO && data != null) {
        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log1");
        	Uri uri = data.getData();
        	
        	if(uri != null) {
        		Utils.writeLogToFile("FrameFragment-onActivityResult-Log2="+uri.getPath());
        		String fileList[];
        		
        		Log.d("selectedImageUri", uri.getPath());
        		fileList = (new String[] { "_data" });
        
        		if(getActivity() == null) 
        			return;
        		
        		Utils.writeLogToFile("FrameFragment-onActivityResult-Log3");
        		
		        Cursor cursor = getActivity().getContentResolver().query(uri, fileList, null, null, null);
		        cursor.moveToFirst();
		        
		        int columnIndex = cursor.getColumnIndex(fileList[0]);
		        
		        if(!uri.getPath().contains("picasa")) {
		        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log4");
		        	
			        if(columnIndex != -1) {
			        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log5");
			            uri = Uri.parse((new StringBuilder("file://")).append(cursor.getString(columnIndex)).toString());
			        }
		        }
		        else {
		        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log6");
		        	
		            if(getActivity() == null) 
		            	return;
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log7");
		            
		            InputStream inputstream;
		            
					try {
						Utils.writeLogToFile("FrameFragment-onActivityResult-Log8");
						inputstream = getActivity().getContentResolver().openInputStream(uri);
						
			            String fileName;
			            
			            if(inputstream == null) 
			            	return;
			            
			            Utils.writeLogToFile("FrameFragment-onActivityResult-Log9");
			            
			            if(!Environment.getExternalStorageState().equals("mounted")) {
			            	fileName = (new StringBuilder(String.valueOf(getActivity().getFilesDir().getAbsolutePath()))).append(File.separator).append("Pictures").toString();
			            }
			            else {
			            	fileName = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("Pictures").toString();
			            }
			            
			            Utils.writeLogToFile("FrameFragment-onActivityResult-Log10");
			            
			            File file = new File(fileName);
			            
			            if(!file.exists())
			                file.mkdir();
			            
			            String dstFile = (new StringBuilder(String.valueOf(fileName))).append(File.separator).append("picture_").append(Calendar.getInstance().getTimeInMillis()).append(".jpg").toString();
			            BitmapUtil.writeIStoFile(inputstream, dstFile);
			            uri = Uri.parse(dstFile);
			            
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		        
		        Utils.writeLogToFile("FrameFragment-onActivityResult-Log11");
		        cursor.close();
		        
		        if(uri != null) {
		        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log12");
		        	
			        String urlPath = uri.getPath();
			        
			        if(urlPath.contains("http")) { 
			        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log13");
				        List list = uri.getPathSegments();
				        String path;
				        path = "";
			
				        for (int l1 = 0; l1 < list.size(); l1++) {
				        	path = (new StringBuilder(String.valueOf(path))).append(((String)list.get(l1)).replace("/", "%2F").replace(":", "%3A")).append(File.separator).toString();
				        }
				        
				        int j2 = -1 + path.length();
				        urlPath = path.substring(0, j2);
			        }
			        
			        Utils.writeLogToFile("FrameFragment-onActivityResult-Log14");
			        
			        try {
			        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log15");
			        	
			            frameLayout.addBitmap(urlPath, error);
			            
			            Utils.writeLogToFile("FrameFragment-onActivityResult-Log16");
			            
			            if(urlPath.substring(urlPath.lastIndexOf("."), urlPath.length()).contains("gif")) {
			                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
			                builder.setMessage(R.string.gif_warning).setPositiveButton("Ok", null);
			                builder.create();
			                builder.show();
			            }
			        }
			        catch(Throwable throwable4) {
			        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log17");
			        	
			            nActivityResultValue = SELECT_PHOTO;
			            strActivityResultPath = urlPath;
			            
			            throwable4.printStackTrace();
			        }
		        }
		    }
        }

        if(requestCode == TAKE_PHOTO)
            try {
            	Utils.writeLogToFile("FrameFragment-onActivityResult-Log18");
                frameLayout.addBitmap(outputFileUri.getPath(), error);
            }
            catch(Throwable throwable3) {
            	
            	Utils.writeLogToFile("FrameFragment-onActivityResult-Log19");
	            
            	nActivityResultValue = TAKE_PHOTO;
	            
            	if (outputFileUri != null) {
	            	Utils.writeLogToFile("FrameFragment-onActivityResult-Log20");
	            	strActivityResultPath = outputFileUri.getPath();
	            }
            	
                throwable3.printStackTrace();
            }
        
        if(requestCode == SELECT_VIDEO) {
        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log21");
	        Uri uri = data.getData();
	        String filePre[]  = (new String[] {
	            "_data"
	        });
	        
	        if(getActivity() == null) 
	        	return;
	        
	        Utils.writeLogToFile("FrameFragment-onActivityResult-Log22");
	        
	        Cursor cursor = getActivity().getContentResolver().query(uri, filePre, null, null, null);
	        cursor.moveToFirst();
	        
	        int columnIndex = cursor.getColumnIndex(filePre[0]);
	        
	        if(uri.getPath().contains("picasa")) {
	        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log23");
	        	
	            if(getActivity() == null) 
	            	return;
	            
	            InputStream inputstream;
	            
				try {
					Utils.writeLogToFile("FrameFragment-onActivityResult-Log24");
					inputstream = getActivity().getContentResolver().openInputStream(uri);
		            String dirPath;
		            
		            if(inputstream == null) 
		            	return;
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log25");
		            
		            if(!Environment.getExternalStorageState().equals("mounted")) {
		            	dirPath = (new StringBuilder(String.valueOf(getActivity().getFilesDir().getAbsolutePath()))).append(File.separator).append("Pictures").toString();
		            }
		            else {
		            	dirPath = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("Pictures").toString();
		            }
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log26");
		            
		            File file = new File(dirPath);
		            if(!file.exists())
		                file.mkdir();
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log27");
		            
		            String dstFile = (new StringBuilder(String.valueOf(dirPath))).append(File.separator).append("video_").append(Calendar.getInstance().getTimeInMillis()).append(".mp4").toString();
		            BitmapUtil.writeIStoFile(inputstream, dstFile);
		            uri = Uri.parse(dstFile);
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log28");
		            
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					Utils.writeLogToFile("FrameFragment-onActivityResult-Log29");
				}
	        }
	        else {
	        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log30");
	        	
		        if(columnIndex != -1) {
		        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log31");
		            uri = Uri.parse((new StringBuilder("file://")).append(cursor.getString(columnIndex)).toString());
		        }
	        }
	        
	        Utils.writeLogToFile("FrameFragment-onActivityResult-Log32");
	        
	        cursor.close();
	        
	        Log.d("videoURL", uri.getPath());
	        
//	        try {
//	        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log33="+uri.getPath());
//	            frameLayout.addVideo(uri.getPath(), error);
//	        }
//	        catch(Throwable throwable2) {
//	        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log34");
//	        	
//	            nActivityResultValue = SELECT_VIDEO;
//	            strActivityResultPath = uri.getPath();
//	            
//	            throwable2.printStackTrace();
//	        }
        }
        
        if(requestCode == RECORD_VIDEO)
            try
            {
            	Utils.writeLogToFile("FrameFragment-onActivityResult-Log35");
//                frameLayout.addVideo(videoUri.getPath(), new com.dragonplayer.merge.frames.FrameView.CorruptVideoError() {
//
//                    public void corruptVideo() {
//                    }
//                });
            }
            catch(Throwable throwable1) {
            	Utils.writeLogToFile("FrameFragment-onActivityResult-Log36");
	            nActivityResultValue = RECORD_VIDEO;
	            
	            if (videoUri != null)
	            	strActivityResultPath = videoUri.getPath();
                throwable1.printStackTrace();
            }
        
        if(requestCode == SELECT_TRACK) {
        	Uri uri;
	        try {
	            uri = data.getData();
	        }
	        catch(Throwable throwable) {
	            throwable.printStackTrace();
	            return;
	        }
	        
	        if(uri == null) 
	        	return;
	
	        Log.d("selectedImageUri", uri.getPath());
	        
	        if(getActivity() == null) 
	        	return;
	
	        String filePre[] = (new String[] {
	            "_data"
	        });
	        
	        Cursor cursor = (new CursorLoader(getActivity(), uri, filePre, null, null, null)).loadInBackground();
	        int columnIndex = cursor.getColumnIndexOrThrow("_data");
	        
	        cursor.moveToFirst();
	        
	        audioUri = Uri.parse(cursor.getString(columnIndex));
	        cursor.close();
	        frameLayout.setAudioPath(audioUri.getPath());
        }
    }

    public void onCreate(Bundle bundle) {
    	
    	Utils.writeLogToFile("FramesFragment-onCreate****************************");
    	
        if(bundle != null) {
        	
        	Utils.writeLogToFile("FrameFragment-onCreate-Log1");
        	
            if(bundle.containsKey("outPutPicture")) {
            	
            	Utils.writeLogToFile("FrameFragment-onCreate-Log2");
            	
                outputFileUri = Uri.parse(bundle.getString("outPutPicture"));
                
	            if (outputFileUri != null) {
	            	Utils.writeLogToFile("FrameFragment-onCreate-Log3="+outputFileUri.getPath());
	            	strActivityResultPath = outputFileUri.getPath();
	            }
	            
	            Utils.writeLogToFile("FrameFragment-onCreate-Log4");
            }
            
            Utils.writeLogToFile("FrameFragment-onCreate-Log5");
            
            if(bundle.containsKey("outPutVideo")) {
            	
            	Utils.writeLogToFile("FrameFragment-onCreate-Log6");
            	
                videoUri = Uri.parse(bundle.getString("outPutVideo"));
                
	            if (videoUri != null) {
	            	Utils.writeLogToFile("FrameFragment-onCreate-Log7");
	            	strActivityResultPath = videoUri.getPath();
	            }
	            
	            Utils.writeLogToFile("FrameFragment-onCreate-Log8");
            }
            
            Utils.writeLogToFile("FrameFragment-onCreate-Log9");
            Log.d("onCreate", "state not null");
        }
        
        Utils.writeLogToFile("FrameFragment-onCreate-Log10");
        Log.d("onCreate", "state not null");
        
        frameID = getArguments().getInt("frameID");
        File file;
        
        if(Environment.getExternalStorageState().equals("mounted"))
            mBaseFolderPath = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("Pictures").toString();
        else
            mBaseFolderPath = (new StringBuilder(String.valueOf(getActivity().getFilesDir().getAbsolutePath()))).append(File.separator).append("Pictures").toString();
        
        Utils.writeLogToFile("FrameFragment-onCreate-Log11");
        file = new File(mBaseFolderPath);
        
        if(!file.exists())
            file.mkdir();
        
        Utils.writeLogToFile("FrameFragment-onCreate-Log12");
        
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
    
    	Utils.writeLogToFile("FramesFragment-onCreateView****************************");
    	
    	Utils.writeLogToFile("FrameFragment-onCreateView-Log1");
        View view = layoutinflater.inflate(R.layout.frame_fragment, null);
        
        if(frameID > -1)
            frame = Frames.newInstance(getActivity()).getFrameWithId(frameID);
        
        final LinearLayout frameHolder = (LinearLayout)view.findViewById(R.id.frameHolder);
        frameHolder.removeAllViews();
        
        Utils.writeLogToFile("FrameFragment-onCreateView-Log2");
        
        error = new com.dragonplayer.merge.frames.FrameView.CorruptVideoError() {

            public void corruptVideo() {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setMessage("The file you selected is corrupted! Please try again with another file.").setPositiveButton("Ok", null);
                builder.create();
                builder.show();
            }
        };
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        selectPhotoBtn = (LinearLayout)view.findViewById(R.id.selectPhoto);
        selectPhotoBtn.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1) {
                startGalleryIntent();
            }
        });
        
        takePhotoBtn = (LinearLayout)view.findViewById(R.id.takePhoto);
        takePhotoBtn.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1) {
                startCameraIntent();
            }
        });
        
        selectVideoBtn = (LinearLayout)view.findViewById(R.id.selectVideo);
        selectVideoBtn.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1) {
                startVideoPickerIntent();
            }
        });
        
        recordVideoBtn = (LinearLayout)view.findViewById(R.id.recordVideo);
        recordVideoBtn.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1) {
                startVideoIntent();
            }
        });
        
        DisplayMetrics displaymetrics1;
        final int width;
        
        if(frameID == -1) {
            selectPhotoBtn.setEnabled(false);
            takePhotoBtn.setEnabled(false);
            selectVideoBtn.setEnabled(false);
            recordVideoBtn.setEnabled(false);
        } 
        else {
            selectPhotoBtn.setEnabled(true);
            takePhotoBtn.setEnabled(true);
            selectVideoBtn.setEnabled(true);
            recordVideoBtn.setEnabled(true);
        }
        
        displaymetrics1 = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics1);
        width = (int)(0.84999999999999998D * (double)displaymetrics1.widthPixels);
        
        if(frameID == -1) {
            TextView textview = new TextView(getActivity());
            textview.setText(R.string.no_frame_selected);
            textview.setTextColor(0xFF000000);
            textview.setGravity(17);
            textview.setTextSize(0.02F * (float)displaymetrics1.heightPixels);
            frameHolder.addView(textview);
        } 
        else {
            frameHolder.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressLint("NewApi")
				public void onGlobalLayout() {
                	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log1");
                	
                    frameHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if(frameHolder.getHeight() < width) {
                    	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log2");
                        int i = frameHolder.getHeight();
                        frameLayout = new FramesLayout(getActivity(), frame, true, frameID, true, i, i, ((MainActivity)getActivity()).getManager(), true, null);
                        android.widget.LinearLayout.LayoutParams layoutparams1 = new android.widget.LinearLayout.LayoutParams(i, i);
                        frameHolder.addView(frameLayout, layoutparams1);
                        Utils.setFrameWidth(getActivity().getBaseContext(), i,i);
                    } 
                    else {
                    	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log3");
                        frameLayout = new FramesLayout(getActivity(), frame, true, frameID, true, width, width, ((MainActivity)getActivity()).getManager(), true, null);
                        android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(width, width);
                        frameHolder.addView(frameLayout, layoutparams);
                        Utils.setFrameWidth(getActivity().getBaseContext(), width,width);
                    }

                    Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log4");
                    
        	        try {
        	        	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log5="+strActivityResultPath);
        	        	
        	            if (nActivityResultValue == SELECT_PHOTO) {
    			            frameLayout.addBitmap(strActivityResultPath, error);
    			            int j1 = strActivityResultPath.lastIndexOf(".");
    			            int k1 = strActivityResultPath.length();
    			            Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log6");
    			            if(strActivityResultPath.substring(j1, k1).contains("gif")) {
    			                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
    			                builder.setMessage(R.string.gif_warning).setPositiveButton("Ok", null);
    			                builder.create();
    			                builder.show();
    			            }
        	            }
        	            else if (nActivityResultValue == TAKE_PHOTO) {
        	            	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log7");
        	            	frameLayout.addBitmap(strActivityResultPath, error);
        	            }
        	            else if (nActivityResultValue == SELECT_VIDEO) {
        	            	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log8");
//        	            	frameLayout.addVideo(strActivityResultPath, error);
        	            	
        	            }
        	            else if (nActivityResultValue == RECORD_VIDEO) {
        	            	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log9");
//        	                frameLayout.addVideo(strActivityResultPath, new com.dragonplayer.merge.frames.FrameView.CorruptVideoError() {
//
//        	                    public void corruptVideo() {
//        	                    }
//        	                });
        	            }
        	        }
        	        catch(Throwable throwable2) {
        	        	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log10");
        	            throwable2.printStackTrace();
        	        }
    	            
        	        Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log11");
    	            nActivityResultValue = 0;
    	            strActivityResultPath = "";
                }
            });
        }
        
        return view;
    }

    public void onSaveInstanceState(Bundle bundle) {
    	Log.d("onSaveInstanceState", "framefragment");
    	
        if(outputFileUri != null)
            bundle.putString("outPutPicture", outputFileUri.getPath());
        if(videoUri != null)
            bundle.putString("outPutVideo", videoUri.getPath());
        
        super.onSaveInstanceState(bundle);
    }

    public void onStop() {
        if(frameLayout != null)
            try {
                JSONObject jsonobject = new JSONObject();
                jsonobject.put("frameID", frameID);
                jsonobject.put("selectedFrame", frameLayout.getSelectedFrame());
                jsonobject.put("audioPath", frameLayout.getAudioPath());
                jsonobject.put("isSequentially", frameLayout.isSequentially());
                jsonobject.put("datas", frameLayout.saveDatas());
                Utils.setDatas(getActivity().getApplicationContext(), jsonobject.toString());
            }
            catch(JSONException jsonexception) {
                jsonexception.printStackTrace();
            }
        
        super.onStop();
    }

    public void setAudioSection(long startTime, long endTime) {
        if(frameLayout != null) {
            frameLayout.setAudioStartTime(startTime);
            frameLayout.setAudioEndTime(endTime);
        }
    }
}
