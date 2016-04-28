package com.dragonplayer.merge.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.dragonplayer.merge.FacebookActivity;
import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.frames.*;
import com.dragonplayer.merge.utils.*;
import com.dragonplayer.merge.utils.Constants.Extra;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.lang.Runnable;

import org.json.*;

public class FinishFragment extends Fragment {
    
	private Bitmap bitmap;
    private Frame frame;
    public LinearLayout frameHolder;
    private int frameID;
    private FramesLayout frameLayout;
    private boolean isRenderKilled;
    private boolean isRenderedFile;
    private File pictureFile;
    private Button playPauseButton;
    private ImageButton btnSave;
    private ImageButton btnShare;
    private ImageButton btnFacebook;
    private ImageButton btnMail;
    private ImageButton btnLine;
    private File videoFile;
    private VideoView videoView;
    private int width;
    private LinearLayout shareLayout;
    private String audioPath;
    private boolean isRenderStart = false;

    public static interface FFMpegError {
        public abstract void onError(StringBuilder stringbuilder);
    }

    public static interface OnProgressUpdate {
        public abstract void onUpdate(int i, int j);
    }

    private class VideoRendering extends AsyncTask {

        ProgressDialog dialog;
        boolean isOk;
        boolean isSocial;
        int social;

        protected Boolean doInBackground(Void avoid[]) {
            try {
                frameLayout.writeVideo(new OnProgressUpdate() {

                    public void onUpdate(int prs, int totalTime) {
                        VideoRendering videorendering = VideoRendering.this;
                        Integer progress[] = new Integer[1];
                        progress[0] = Integer.valueOf(prs);
                        videorendering.publishProgress(progress);
                    }
                }, videoFile, new FFMpegError() {

                    public void onError(final StringBuilder stringbuilder) {
                        isOk = false;
                        frameLayout.post(new java.lang.Runnable() {

                            public void run() {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                builder.setMessage("Something went wrong, one or more files you used might be corrupted. If the issue persist with other files too please send us the log so we can examine the issue.").setPositiveButton("Send log file", new android.content.DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        PackageInfo packageinfo1;
										try {
											packageinfo1 = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
	                                        PackageInfo packageinfo = packageinfo1;
	                                        Intent intent = new Intent("android.intent.action.SEND");
	                                        intent.setType("plain/text");
	                                        intent.putExtra("android.intent.extra.SUBJECT", "DragonMergePlayer Support");
	                                        intent.putExtra("android.intent.extra.EMAIL", new String[] {
	                                            "idragon712@outlook.com"
	                                        });
										} catch (NameNotFoundException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
                                    }
                                }).setNegativeButton("Cancel", null);
                                builder.create();
                                builder.show();
                            }
                        });
                    }
                });
            }
            catch(Throwable throwable) {
                throwable.printStackTrace();
                isOk = false;
            }
            
            return Boolean.valueOf(isOk);
        }

        protected Object doInBackground(Object aobj[]) {
            return doInBackground((Void[])aobj);
        }

        @SuppressWarnings("deprecation")
		protected void onPostExecute(Boolean boolean1) {
            dialog.dismiss();
            
            if(!boolean1.booleanValue() || isRenderKilled) {
                if(videoFile.exists())
                    videoFile.delete();
                
                super.onPostExecute(boolean1);
                isRenderStart = false;
                return;
            }

            new SingleMediaScanner(getActivity(), videoFile);
        	AlertDialog iDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
            iDialog.setMessage("儲存至: \n" + videoFile.getAbsolutePath().toString());
            
            iDialog.setCancelable(true);
            iDialog.setButton("確定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    ((MainActivity)getActivity()).onBackPressed();
                    ((MainActivity)getActivity()).HomePage();
                }
            });            
            iDialog.setOnCancelListener(new OnCancelListener(){
                @Override
                public void onCancel(DialogInterface dialog){
                	dialog.dismiss();
             }});  
            
            iDialog.show();
            
            if(!isSocial) {
                super.onPostExecute(boolean1);
                isRenderStart = false;
                return;
            }

            switch (social) {
            case 0:
                if(frameLayout.isVideoOrAudio())
                    setVideo();
                //facebookHelper.LoginToFacebook(getActivity(), FinishFragment.this, facebookHelper, videoFile, true, bitmap);
            	break;
            case 1:
            	break;
            case 2:
            	String strPath = Utils.copyFile(videoFile.getAbsolutePath(), true);
            	shareAllApp(strPath, "video/*");
            	break;
            case 3:
                setVideo();
                videoView.start();
            	break;
            }
            
            super.onPostExecute(boolean1);
            isRenderStart = false;
        }

        protected void onPostExecute(Object obj) {
            onPostExecute((Boolean)obj);
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setProgressStyle(1);
            dialog.setCancelable(false);
            dialog.setMessage("重製影片中……");
            dialog.setButton(-2, "停止", new android.content.DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialoginterface, int i) {
                    if(frameLayout == null)
                        return;

                    try {
						frameLayout.quitRendering();
						
	                    if (videoFile.exists()) {
	                    	videoFile.delete();
	                    }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    isRenderKilled = true;
                }
            });
            
            dialog.show();
            isRenderKilled = false;
            super.onPreExecute();
        }

        protected void onProgressUpdate(Integer ainteger[]) {
            dialog.setProgress(ainteger[0].intValue());
            super.onProgressUpdate(ainteger);
        }

        protected void onProgressUpdate(Object aobj[]) {
            onProgressUpdate((Integer[])aobj);
        }

        public VideoRendering(int i, boolean flag) {
            super();
            isOk = true;
            social = i;
            isSocial = flag;
        }
    }


    public FinishFragment() {
        isRenderKilled = false;
    }

    public void onActivityCreated(Bundle bundle) {
        if(frameID <= -1) {
            super.onActivityCreated(bundle);
            return;
        }

        String datas;
        boolean flag;
        
        datas = Utils.getDatas(getActivity().getApplicationContext());
        flag = true;
        
        if(datas.equals(""))
        	flag = false;
        else {
	        try {
	        	JSONObject jsonobject = new JSONObject(datas);
	        	if(jsonobject.getInt("frameID") == frameID) {
				    JSONArray jsonarray = jsonobject.getJSONArray("datas");
				    int i = 0;
				    int j = jsonarray.length();
				    for (i = 0; i < j; i++) {
				        if(jsonarray.getJSONObject(i).getString("bitmap_uri").equals(""))
				            flag = false;
				    }
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

//        btnSave.setEnabled(flag);
        playPauseButton.setEnabled(flag);

        super.onActivityCreated(bundle);
        return;
    }

    public void onActivityResult(int i, int j, Intent intent) {
        super.onActivityResult(i, j, intent);
    }

    public void onCreate(Bundle bundle) {
        frameID = getArguments().getInt("frameID");
        String strFileName = getArguments().getString("renderFile");
        audioPath = getArguments().getString("audioPath");
        
        String dir = Environment.getExternalStorageDirectory() + "/data/DragonMergePlayer";
        File file1 = new File(dir);
        
        if(!file1.exists())
            file1.mkdir();
        
        File file2 = new File(dir, "ProjectFiles");
        
        if(!file2.exists())
            file2.mkdir();
        
        isRenderKilled = false;
        
        if (strFileName != null && !strFileName.isEmpty()) {
        	if (strFileName.contains(".mp4")) {
        		videoFile = new File(strFileName);
        		pictureFile = null;
        	}
        	else {
        		videoFile = null;
        		pictureFile = new File(strFileName);
        	}
        	
        	isRenderedFile = true;
        }
        else {
            videoFile = new File(file2, (new StringBuilder("")).append(Calendar.getInstance().getTimeInMillis()).append(".mp4").toString());
            
            File file3 = new File(dir, "ProjectFiles");
            
            if(!file3.exists())
                file3.mkdir();
            
            pictureFile = new File(file3, (new StringBuilder("")).append(Calendar.getInstance().getTimeInMillis()).append(".jpg").toString());
            
            isRenderedFile = false;
        }
        
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.finish_fragment, null);
        if(frameID > -1)
            frame = Frames.newInstance(getActivity()).getFrameWithId(frameID);
        
        ((MainActivity)getActivity()).clearImageLoadCheckList();

        frameHolder = (LinearLayout)view.findViewById(R.id.frameHolder);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        playPauseButton = (Button)view.findViewById(R.id.buttonPlayPause);
        playPauseButton.setVisibility(View.GONE);
        playPauseButton.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1) {
                if(!videoFile.exists()) {
                	startRender(3, true);
                } 
                else {
                    setVideo();
                    videoView.start();
                }
            }
        });
        
        shareLayout = (LinearLayout)view.findViewById(R.id.shareLayout);
        shareLayout.setVisibility(View.INVISIBLE);
        
        width = (int)(0.84999999999999998D * (double)displaymetrics.widthPixels);
        if(frameID == -1 && !isRenderedFile) {
        	
//            TextView textview = new TextView(getActivity());
//            textview.setText(R.string.no_frame_selected);
//            textview.setTextColor(0xFF000000);
//            textview.setGravity(17);
//            textview.setTextSize(0.02F * (float)displaymetrics.heightPixels);
//            frameHolder.addView(textview);
        } 
        else {
            frameHolder.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressLint("NewApi")
				public void onGlobalLayout() {
                    frameHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if(frameHolder.getHeight() >= width) {
                        DisplayMetrics displaymetrics1 = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics1);
                        int holderHeight = (int)(0.90999999999999998D * (double)frameHolder.getHeight());
                        int realW;
                        int realH;
                      
                        if (isRenderedFile) {
                            int nDirection;
                            if (videoFile != null) {
                            	File file = Utils.writeToFile(BitmapUtil.videoFrame(videoFile.getAbsolutePath(), 0L));
                            	nDirection = Utils.getBitmapDirection(file.getAbsolutePath());
                            	file.delete();
                            }
                            else {
                            	nDirection = Utils.getBitmapDirection(pictureFile.getAbsolutePath());
                            }

                            if (nDirection == Frame.FRAMEVERTICAL) {
                            	realH = holderHeight;
                            	realW = realH * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT; 
                            }
                            else {
                            	realW = (int)((double)displaymetrics1.widthPixels * (0.93333D)); 
                            	realH = realW * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT;
                            }
                        	
                        	int w = realW;
                            int h = realH;
                            if(w != -1)
                                width = w;

                            android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(w, h);
                            frameHolder.removeAllViews();
                            
                            ImageView imgView = new ImageView(getActivity());
                            
                            if (videoFile != null) {
                            	File file = Utils.writeToFile(BitmapUtil.videoFrame(videoFile.getAbsolutePath(), 0L));
                            	((MainActivity)getActivity()).getManager().displayThumbImage(file.getAbsolutePath(), imgView, w, h);
                            	file.delete();
                            }
                            else {
                                ((MainActivity)getActivity()).getManager().displayThumbImage(pictureFile.getAbsolutePath(), imgView, w, h);
                            }
                            
                            frameHolder.addView(imgView, layoutparams);
                        }
                        else {
                        	realW = Utils.getFrameWidth(getActivity());
                        	realH = Utils.getFrameHeight(getActivity());
                        	
                        	if (realW == -1 || realH == -1) {
//	                            if (frame.getFrameDirection() == Frame.FRAMEVERTICAL) {
	                            	realH = holderHeight;
	                            	realW = realH * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT; 
//	                            }
//	                            else {
//	                            	realW = (int)((double)displaymetrics1.widthPixels * (0.93333D)); 
//	                            	realH = realW * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT;
//	                            }
                        	}
                        	
                        	int w = realW;
                            int h = realH;
                            if(w != -1)
                                width = w;

                            frameLayout = new FramesLayout(getActivity(), frame, false, frameID, true, w, h, ((MainActivity)getActivity()).getManager(), false, playPauseButton);
	                        android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(w, h);
	                        
	                        if (!audioPath.isEmpty()) {
	                        	frameLayout.setAudioPath(audioPath);
	                        }
	                        
	                        frameHolder.removeAllViews();
	                        frameHolder.addView(frameLayout, layoutparams);
                        }
                    }
                    else {
	                    int height = frameHolder.getHeight();
	                    
	                    if(getActivity() == null) 
	                    	return;
	
                        DisplayMetrics displaymetrics1 = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics1);
                        int holderHeight = (int)(0.90999999999999998D * (double)frameHolder.getHeight());
                        int realW;
                        int realH;
                      
                        if (isRenderedFile) {
                            int nDirection;
                            if (videoFile != null) {
                            	File file = Utils.writeToFile(BitmapUtil.videoFrame(videoFile.getAbsolutePath(), 0L));
                            	nDirection = Utils.getBitmapDirection(file.getAbsolutePath());
                            	file.delete();
                            }
                            else {
                            	nDirection = Utils.getBitmapDirection(pictureFile.getAbsolutePath());
                            }

                            if (nDirection == Frame.FRAMEVERTICAL) {
                            	realH = holderHeight;
                            	realW = realH * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT; 
                            }
                            else {
                            	realW = (int)((double)displaymetrics1.widthPixels * (0.93333D)); 
                            	realH = realW * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT;
                            }
                        	
                        	int w = realW;
                            int h = realH;
    	                    if(w != -1)
    	                    	height = w;

                            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(w, h);
		                    frameHolder.removeAllViews();

		                    ImageView imgView = new ImageView(getActivity());
                            
                            if (videoFile != null) {
                            	File file = Utils.writeToFile(BitmapUtil.videoFrame(videoFile.getAbsolutePath(), 0L));
                            	((MainActivity)getActivity()).getManager().displayThumbImage(file.getAbsolutePath(), imgView, w, h);
                            	file.delete();
                            }
                            else {
                                ((MainActivity)getActivity()).getManager().displayThumbImage(pictureFile.getAbsolutePath(), imgView, w, h);
                            }
                            
                            frameHolder.addView(imgView, params);
                        }
                        else {
                        	realW = Utils.getFrameWidth(getActivity());
                        	realH = Utils.getFrameHeight(getActivity());
                        	
                        	if (realW == -1 || realH == -1) {
//	                            if (frame.getFrameDirection() == Frame.FRAMEVERTICAL) {
	                            	realH = holderHeight;
	                            	realW = realH * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT; 
//	                            }
//	                            else {
//	                            	realW = (int)((double)displaymetrics1.widthPixels * (0.93333D)); 
//	                            	realH = realW * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT;
//	                            }
                        	}
                        	
                        	int w = realW;
                            int h = realH;
    	                    if(w != -1)
    	                    	height = w;

    	                    frameLayout = new FramesLayout(getActivity(), frame, false, frameID, 
		                    		true, w, h, ((MainActivity)getActivity()).getManager(), false, playPauseButton);
		                    android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(w, h);
	                        
		                    if (!audioPath.isEmpty()) {
	                        	frameLayout.setAudioPath(audioPath);
	                        }

	                        frameHolder.removeAllViews();
		                    frameHolder.addView(frameLayout, params);
                        }
                    }
                    
                    if (isRenderedFile) {
                    	if (videoFile != null)
                    		playPauseButton.setVisibility(View.VISIBLE);
                    }
                    else {
	                    if(frameLayout.isVideoOrAudio()) {
	                        playPauseButton.setVisibility(View.VISIBLE);
	                    }
                    }
                }
            });
            
        }
        
        btnSave = (ImageButton)view.findViewById(R.id.btnSave);
    	btnSave.setOnClickListener(new android.view.View.OnClickListener() {

            @SuppressWarnings("deprecation")
			public void onClick(View view1) {
            	view1.setEnabled(false);
            	
                if(frameLayout.isVideoOrAudio()) {
                    if(videoFile.exists() && !isRenderKilled) {
                    	AlertDialog iDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
                        iDialog.setMessage("儲存至: \n" + videoFile.getAbsolutePath().toString());
                        
                        iDialog.setCancelable(true);
                        iDialog.setButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                ((MainActivity)getActivity()).onBackPressed();
                                ((MainActivity)getActivity()).HomePage();
                            }
                        });            
                        iDialog.setOnCancelListener(new OnCancelListener(){
                            @Override
                            public void onCancel(DialogInterface dialog){
                            	dialog.dismiss();
                         }});  
                        
                        iDialog.show();
                    } 
                    else {
                    	startRender(0, false);
                    }
                } 
                else {
                	
                	while (((MainActivity)getActivity()).getImageLoadCheckListCount() < frameLayout.getFrames().size()) {
                		try {
                			Thread.sleep(100);
                		}
                		catch (Exception e) {
                			
                		}
                	}
                	
                    if(bitmap == null || bitmap.isRecycled())
                        bitmap = frameLayout.getDrawingCache();
                	
                    BitmapUtil.writeFile(bitmap, pictureFile);
                    new SingleMediaScanner(getActivity(), pictureFile);
                	AlertDialog iDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).create();
                    iDialog.setMessage("儲存至: \n" + pictureFile.getAbsolutePath().toString());
                    
                    iDialog.setCancelable(true);
                    iDialog.setButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
//                            ((MainActivity)getActivity()).onBackPressed();
                            ((MainActivity)getActivity()).HomePage();
                        }
                    });            
                    iDialog.setOnCancelListener(new OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialog){
                        	dialog.dismiss();
                     }});  
                    
                    iDialog.show();
                }
                
                view1.setEnabled(true);
            }
        });
    	
        btnShare = (ImageButton)view.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1) {
//            	if (shareLayout.getVisibility() == View.VISIBLE)
//            		shareLayout.setVisibility(View.INVISIBLE);    	
//            	else
//            		shareLayout.setVisibility(View.VISIBLE);

            	String strPath;
            	String strType;
            	if (isRenderedFile) {
            		if (videoFile != null) {
            			strType = "video/*";
            			strPath = Utils.copyFile(videoFile.getAbsolutePath(), true);
            		}
            		else {
            			strType = "image/*";
            			strPath = Utils.copyFile(pictureFile.getAbsolutePath(), false);
            		}
            	}
            	else {
            		if(bitmap == null || bitmap.isRecycled())
            			bitmap = frameLayout.getDrawingCache();
            		if(frameLayout.isVideoOrAudio()) {
            			if(videoFile.exists()) {
            				strType = "video/*";
            				strPath = videoFile.getAbsolutePath();
            			} 
            			else {
            				startRender(2, true);
            				return;
            			}
            		} 
            		else {
            			strType = "image/*";
            			strPath = Utils.writeToFile(bitmap).getAbsolutePath();
            		}
            	}
            	
            	shareAllApp(strPath, strType);
            }
        });
        
        btnFacebook = (ImageButton)view.findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1) {
            	String strPath;
                if (isRenderedFile) {
                    if (videoFile != null) {
                    	File file = Utils.writeToFile(BitmapUtil.videoFrame(videoFile.getAbsolutePath(), 0L));
                    	strPath = file.getAbsolutePath();
                    }
                    else {
                    	strPath = pictureFile.getAbsolutePath();
                    }
                }
                else {
	                if(bitmap == null || bitmap.isRecycled())
	                    bitmap = frameLayout.getDrawingCache();
	                
	                strPath = Utils.writeToFile(bitmap).getAbsolutePath();
                }
                
        		Intent intent = new Intent(getActivity(), FacebookActivity.class);
        		intent.putExtra(Extra.POST_MESSAGE, Constants.FACEBOOK_SHARE_MESSAGE);
        		intent.putExtra(Extra.POST_LINK, Constants.FACEBOOK_SHARE_LINK);
        		intent.putExtra(Extra.POST_LINK_NAME, Constants.FACEBOOK_SHARE_LINK_NAME);
        		intent.putExtra(Extra.POST_LINK_DESCRIPTION, Constants.FACEBOOK_SHARE_LINK_DESCRIPTION);
        		intent.putExtra(Extra.POST_PICTURE, strPath);
        		startActivity(intent);

                //facebookHelper.LoginToFacebook(getActivity(), FinishFragment.this, facebookHelper, Utils.writeToFile(bitmap), false, bitmap);
            }
        });

        btnMail = (ImageButton)view.findViewById(R.id.btnMail);
        btnMail.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1) {
                if(bitmap == null || bitmap.isRecycled())
                    bitmap = frameLayout.getDrawingCache();
                if(frameLayout.isVideoOrAudio()) {
                    if(videoFile.exists()) {
                        sendMail("mail", videoFile);
                    } 
                    else {
                    	startRender(2, true);
                    }
                } 
                else {
                    sendMail("mail", Utils.writeToFile(bitmap));
                }
            }
        });

//        btnLine = (ImageButton)view.findViewById(R.id.btnLine);
//        btnLine.setOnClickListener(new android.view.View.OnClickListener() {
//
//            public void onClick(View view1) {
//            }
//        });

        if(frameID == -1) {
            btnSave.setEnabled(false);
            
            if (isRenderedFile)
            	playPauseButton.setEnabled(true);
            else
            	playPauseButton.setEnabled(false);
        } 
        else {
        	btnSave.setEnabled(true);
            playPauseButton.setEnabled(true);
        }

        if (isRenderedFile)
        	btnSave.setEnabled(false);
        else
        	btnSave.setEnabled(true);

        return view;
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        if(frameLayout != null && frameLayout.isVideoOrAudio() && videoView != null && videoFile.exists() && !videoView.isPlaying())
            videoView.seekTo(1);
        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    public void setAudioToNull() {
        frameLayout.setAudioPath("");
    }

    public boolean isRendered() {
    	return isRenderedFile;
    }
    
    public void setVideo() {
        videoView = new VideoView(getActivity());
        android.widget.LinearLayout.LayoutParams layoutparams;
        
        playPauseButton.setVisibility(View.GONE);
        
        if (isRenderedFile) {
            DisplayMetrics displaymetrics1 = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics1);
            int holderHeight = (int)(0.90999999999999998D * (double)frameHolder.getHeight());
            int realW;
            int realH;
          
            int nDirection;
            if (videoFile != null) {
            	File file = Utils.writeToFile(BitmapUtil.videoFrame(videoFile.getAbsolutePath(), 0L));
            	nDirection = Utils.getBitmapDirection(file.getAbsolutePath());
            	file.delete();
            }
            else {
            	nDirection = Utils.getBitmapDirection(pictureFile.getAbsolutePath());
            }

            if (nDirection == Frame.FRAMEVERTICAL) {
                realH = holderHeight;
            	realW = realH * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT; 
            }
            else {
            	realW = (int)((double)displaymetrics1.widthPixels * (0.93333D)); 
            	realH = realW * WorkFragment.FRAMEORGWIDTH / WorkFragment.FRAMEORGHEIGHT;
            }
        	
            layoutparams = new android.widget.LinearLayout.LayoutParams(realW, realH);
        }
        else {
        	layoutparams = (android.widget.LinearLayout.LayoutParams)frameLayout.getLayoutParams();
        
        	frameLayout.setAudioPath("");
        
	        try {
	            frameLayout.saveDatas();
	        }
	        catch(JSONException jsonexception) {
	            jsonexception.printStackTrace();
	        }
        }
        
        frameHolder.removeAllViews();
        frameHolder.addView(videoView, layoutparams);
        videoView.setVideoPath(videoFile.getAbsolutePath());
        videoView.setMediaController(new MediaController(getActivity()));
        videoView.seekTo(1);
    }

    private void sendMail(String s, File file) {
        ArrayList arraylist;
        List list;
        arraylist = new ArrayList();
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/jpeg");
        list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        if(list.isEmpty()) 
        	return;

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            ResolveInfo resolveinfo = (ResolveInfo)iterator.next();
            Intent intent1 = new Intent("android.intent.action.VIEW");
            intent1.setType("image/jpeg");
            
            if(resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains(s) || resolveinfo.activityInfo.name.toLowerCase(Locale.US).contains(s)) {
                intent1.setData(Uri.parse("mailto:?subject=DragonPlayer&body=" + Uri.encode(Uri.encode((getActivity().getPackageName())))));
                intent1.putExtra("android.intent.extra.STREAM", Uri.parse((new StringBuilder("file://")).append(file.getAbsolutePath()).toString()));
                intent1.setPackage(resolveinfo.activityInfo.packageName);
                arraylist.add(intent1);
            }
        }

        Intent intent2 = Intent.createChooser((Intent)arraylist.remove(0), "Select app");
        intent2.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[])arraylist.toArray(new Parcelable[0]));
        startActivity(intent2);
    }
    
    private void shareAllApp(String strFile, String strType) {
    	Intent shareIntent = new Intent();
    	shareIntent.setAction(Intent.ACTION_SEND);

    	shareIntent.setType(strType);
    	shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + strFile));

    	
    	PackageManager pm = getActivity().getPackageManager();
//    	List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
//    	for (final ResolveInfo app : activityList) 
//    	{
//    		if ((app.activityInfo.name).contains("facebook")) 
//    		{
//    			final ActivityInfo activity = app.activityInfo;
//    			final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//    			shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//    			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//    			shareIntent.setComponent(name);
//    		}
//    		else if ((app.activityInfo.name).contains("gmail")) 
//            {
//    			final ActivityInfo activity = app.activityInfo;
//    			final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//    			shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//    			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//    			shareIntent.setComponent(name);
//            }
//    		else if ("com.twitter.android.PostActivity".equals(app.activityInfo.name))
//            {
//    			final ActivityInfo activity = app.activityInfo;
//    			final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//    			shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//    			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//    			shareIntent.setComponent(name);
//            }
//    	}
//    	
//		getActivity().startActivity(shareIntent);
//    	startActivity(Intent.createChooser(shareIntent, "傳送"));

	    Intent sendIntent = new Intent(Intent.ACTION_SEND);     
	    Intent openInChooser = Intent.createChooser(shareIntent, "傳送");

	    List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
	    List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();        
	    for (int i = 0; i < resInfo.size(); i++) {
	        // Extract the label, append it, and repackage it in a LabeledIntent
	        ResolveInfo ri = resInfo.get(i);
	        String packageName = ri.activityInfo.packageName;
	        if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
	            Intent intent = new Intent();
	            intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
	            intent.setAction(Intent.ACTION_SEND);
	            intent.setType(strType);
	            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+strFile));
	            
	            if(packageName.contains("twitter")) {
	    			final ActivityInfo activity = ri.activityInfo;
	    			final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
	    			intent.setComponent(name);
	    			intent.addCategory(Intent.CATEGORY_LAUNCHER);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	            } else if(packageName.contains("facebook")) {
	    			final ActivityInfo activity = ri.activityInfo;
	    			final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
	    			intent.setComponent(name);
	    			intent.addCategory(Intent.CATEGORY_LAUNCHER);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	            } else if(packageName.contains("mms")) {
	            } else if(packageName.contains("gmail") || packageName.contains("android.gm")) {
	    			final ActivityInfo activity = ri.activityInfo;
	    			final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
	    			intent.setComponent(name);
	    			intent.addCategory(Intent.CATEGORY_LAUNCHER);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	            } 
	            
	            intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
	        }
	    }

	    // convert intentList to array
	    LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

	    openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
	    startActivity(openInChooser);   
    }

    public void setFrameBitmap(Bitmap bmp, int index) {
    	frameLayout.setFrameBitmap(bmp, index);
    }
    
    public void startRender(int type, boolean flag) {
    	if (isRenderStart == true)
    		return;
    	
    	isRenderStart = true;
    	(new VideoRendering(type, flag)).execute(new Void[0]);
    }
}
