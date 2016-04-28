package com.dragonplayer.merge.frames;

import android.annotation.SuppressLint;
import android.content.Context;
 
import android.graphics.Bitmap;
 
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.TextInputActivity;
import com.dragonplayer.merge.utils.*;

import java.io.*;

public class FrameView extends FrameLayout
{
    private static final String TAG = "FrameView";
    private float audioVolume;
    private FrameLayout background;
    private String bitmapUri;
    private ImageView contentView;
    private int end;
    private String extensionParam;
    private int frameId;
    private int height;
    private float initialX;
    private float initialY;
    boolean isLongClick;
    private boolean isLongTapStarted;
    private boolean isVideo;
    private BitmapManager manager;
    private int maxMarginLeft;
    private int maxMarginTop;
    private int maxZoomHeight;
    private int maxZoomWidth;
    private float pointerX;
    private float pointerY;
    private int rotation;
    private double angle;
    private int screenHeight;
    private int screenWidth;
    private FramesLayout.OnSelectionListener selectionListener;
    private Context mContext;
    private boolean bChangePosFlag = false;
    Bitmap mBitmap = null;
    MotionEvent motio;
    private SetBounds setBounds = new SetBounds() {

        public void setBounds(int left, int top, int width, int height) {
            maxMarginLeft = left;
            maxMarginTop = top;
            maxZoomHeight = height;
            maxZoomWidth = width;
            Log.e("FrameView7setBounds", left+":"+top+":"+height+":"+width);
        }
    };

    private int start;
    private RelativeLayout videoContent;
    private int starttime;
    private int videoDuration;
    private boolean videoHasAudio;
    private String videoUri;
    private VideoView videoView;
    private int width;
    private int mrot = 0;
    
    public static interface CorruptVideoError {
        public abstract void corruptVideo();
    }

    public static interface SetBounds {
        public abstract void setBounds(int left, int top, int width, int height);
    }

    public enum ZOOM_TYPE {
    	UNKNOWN("UNKNOWN"),
    	ZOOM_IN("ZOOM_IN"),
    	ZOOM_OUT("ZOOM_OUT");

	    ZOOM_TYPE(String description) {
	    }
    }

    public FrameView(Context context) {
    	
        super(context);
        mContext = context;
        initialX = -1F;
        initialY = -1F;
        pointerX = -1F;
        pointerY = -1F;
        maxMarginLeft = 2;
        maxMarginTop = 2;
        bitmapUri = "";
        videoUri = "";
        videoDuration = 0;
        audioVolume = 1.0F;
        videoHasAudio = false;
        extensionParam = "";
        isLongClick = false;
        isVideo = false;
        isLongTapStarted = false;
        rotation = 0;
        angle = 0;
        starttime = 0;
    }

    public FrameView(Context context, int w, int h, BitmapManager bitmapmanager, int clr, Bitmap bmp, int rot) {
    	
        super(context);

        mContext = context;
        initialX = -1F;
        initialY = -1F;
        pointerX = -1F;
        pointerY = -1F;
        maxMarginLeft = 2;
        maxMarginTop = 2;
        bitmapUri = "";
        videoUri = "";
        videoDuration = 0;
        audioVolume = 1.0F;
        videoHasAudio = false;
        extensionParam = "";
        isLongClick = false;
        isVideo = false;
        isLongTapStarted = false;
        rotation = 0;
        angle = 0;
        starttime = 0;
        contentView = new ImageView(getContext());
       
//        contentView.setBackgroundColor(clr);
        setBackground(new BitmapDrawable(bmp));
        addView(contentView);
        width = w;
        height = h;
        manager = bitmapmanager;
        background = new FrameLayout(getContext());
        android.widget.FrameLayout.LayoutParams layoutparams = new android.widget.FrameLayout.LayoutParams(w, h);
//        MLog.d("FrameView1",w+":"+h);
        layoutparams.gravity = 51;
        //
        background.setLayoutParams(layoutparams);
        addView(background);
        background.bringToFront();
        mrot = rot;
        
        //touch function name
        contentView.setOnTouchListener(new android.view.View.OnTouchListener() {
        	@Override
            public boolean onTouch(View view, MotionEvent motionevent) {
        		 
        		
//        		MLog.d(this, "timeout");
                int pointerId;

                if(!isEnabled())
                	return false;
                
                pointerId = motionevent.getPointerId(motionevent.getActionIndex());
                	
                switch (motionevent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = motionevent.getX();
                    initialY = motionevent.getY();
//                    MLog.d(this, "MotionEvent.action_down");
                    Log.d(TAG,"devon 1111111111111111111111111");
                    if(!isLongTapStarted) {
                        Log.d(TAG,"devon 22222222222 isLongTapStarted is "+isLongTapStarted);
                        contentView.postDelayed(new Runnable() {
                            public void run() {
                               if(isLongClick) {
                                   Log.d(TAG,"devon 3333333333333333333 isLongClick is "+isLongClick);
                                   longClick();
                                   //MLog.i(this, "run");
                               }
                                Log.d(TAG,"devon 444444444444  isLongClick is "+isLongClick);
                                isLongClick = false;
                                isLongTapStarted = false;
                            }
                        }, 500L);
                        //1000L
                        isLongClick = true;
                        Log.d(TAG,"devon 555555555555  isLongClick is "+isLongClick);
                        isLongTapStarted = true;
                    }
                    
                    selectionListener.onSelected(frameId);
                    return true;
                case MotionEvent.ACTION_UP:
//                	MLog.d(this, "MotionEvent.action_up");
//                	MLog.i(this, "X=10F if else:"+(initialX - motionevent.getX()));
//                	MLog.i(this, "Y=10F if else:"+(initialY - motionevent.getY()));
                     if(Math.abs(initialX - motionevent.getX()) < 5F && Math.abs(initialY - motionevent.getY()) < 5F) {
                    	//longClick();
//                    	MLog.i(this, "X<7F:"+(initialX - motionevent.getX()));
//                    	MLog.i(this, "Y<7F:"+(initialY - motionevent.getY()));
                    }
                    initialX = -1F;
                    initialY = -1F;
                    isLongClick = false;
                    Log.d(TAG,"devon 6666666666666666666  isLongClick is "+isLongClick);
                    return true;
                case MotionEvent.ACTION_MOVE:
//                	MLog.d(this, "MotionEvent.action_move");
//                    if(Math.abs(initialX - motionevent.getX()) > 10F || Math.abs(initialY - motionevent.getY()) > 10F)
//                        isLongClick = false;
//                    Log.d(TAG,"devon 7777777777777777777777  isLongClick is "+isLongClick);
                    
                    if(initialX != -1F && initialY != -1F) {
                        if(motionevent.getPointerCount() == 1) {
                            calculateMove(motionevent.getX(), motionevent.getY());
                            return true;
                        }
                        
                        isLongClick = false;
                        Log.d(TAG,"devon 88888888888888888888888  isLongClick is "+isLongClick);
                        float f = 0.0F;
                        float f1 = 0.0F;
                        float f2 = 0.0F;
                        float f3 = 0.0F;
                        
                        for (int l = 0; l < motionevent.getPointerCount(); l++) {
                            if(l == 0) {
                                f = (int)motionevent.getX(l);
                                f1 = (int)motionevent.getY(l);
                            } 
                            else {
                                f2 = (int)motionevent.getX(l);
                                f3 = (int)motionevent.getY(l);
                            }
                        } 

                        float x1 = initialX;
                        float y1 = initialY;
                        float x2 = pointerX;
                        float y2 = pointerY;

                        if (zoomImage(f, f1, f2, f3) == 0)
                        	rotateImage(f, f1, f2, f3, x1, y1, x2, y2);
//                        MLog.d("FrameView2zoomImage",f+":"+f1+":"+f2+":"+f3+":"+x1+":"+y1+":"+x2+":"+y2);
                        contentView.invalidate();
                     }
                	
                	return true;
                case MotionEvent.ACTION_CANCEL:
                	 MLog.i(this, "ActionCancel");
                	return false; 
                case MotionEvent.ACTION_OUTSIDE:
                 MLog.i(this, "ActionOUTSIDE");
                	return false; 
                case MotionEvent.ACTION_POINTER_DOWN:
                    isLongClick = false;
                    Log.d(TAG,"devon 99999999999999999999999  isLongClick is "+isLongClick);
                    
                    if(pointerId == 1) {
                        pointerX = motionevent.getX(pointerId);
                        pointerY = motionevent.getY(pointerId);
                        return true;
                    }
                    MLog.i(this, "ActionPOINTER_DOWN");
                	return false;
                case MotionEvent.ACTION_POINTER_UP:
                    pointerX = -1F;
                    pointerY = -1F;
                    isLongClick = false;
                    Log.d(TAG,"devon 1010101010101011010  isLongClick is "+isLongClick);
                    MLog.i(this, "ActionPOINTER_UP");
                    return true;
                }
                
                return false;
            }
        });  
      /**  contentView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				longClick();
				int pointerId ;   
			 	pointerId = motio.getPointerId(motio.getActionIndex());
				v.setTouchDelegate(getTouchDelegate());
			 
				 switch (motio.getActionMasked()) {
				 case MotionEvent.ACTION_MOVE:
				 {
					  if(initialX != -1F && initialY != -1F) {
	                        if(motio.getPointerCount() == 1) {
	                            calculateMove(motio.getX(), motio.getY());
	                             
	                        }
	                        
	                        isLongClick = false;
	                        float f = 0.0F;
	                        float f1 = 0.0F;
	                        float f2 = 0.0F;
	                        float f3 = 0.0F;
	                        
	                        for (int l = 0; l < motio.getPointerCount(); l++) {
	                            if(l == 0) {
	                                f = (int)motio.getX(l);
	                                f1 = (int)motio.getY(l);
	                            } 
	                            else {
	                                f2 = (int)motio.getX(l);
	                                f3 = (int)motio.getY(l);
	                            }
	                        } 

	                        float x1 = initialX;
	                        float y1 = initialY;
	                        float x2 = pointerX;
	                        float y2 = pointerY;

	                        if (zoomImage(f, f1, f2, f3) == 0)
	                        	rotateImage(f, f1, f2, f3, x1, y1, x2, y2);
	                        MLog.d("FrameView2zoomImage",f+":"+f1+":"+f2+":"+f3+":"+x1+":"+y1+":"+x2+":"+y2);
	                        contentView.invalidate();
	                     }
				 }
				 }
			}
        	
        });*/
       
        
    }
   
    private void checkImage(final String command[], final boolean isFirst, final CorruptVideoError error, final int index) {
        (new Thread() {

            public void run() {
                Process process;
                
				try {
					process = (new ProcessBuilder(new String[] {
					    "/system/bin/chmod", "755", "/data/data/com.dragonplayer.merge/ffmpeg"
					})).start();
	                process.waitFor();

	                BufferedReader bufferedreader;
	                process.destroy();
	                bufferedreader = new BufferedReader(new InputStreamReader((new ProcessBuilder(command)).redirectErrorStream(true).start().getInputStream()));

	                while (true) {
		                String buffer = bufferedreader.readLine();
		                
		                if(buffer == null) {
	                        if(!isFirst)
	                            contentView.post(new Runnable() {

	                                public void run() {
	                                    String ext;
	                                    
	                                    if(bitmapUri.endsWith(".png"))
	                                        ext = "mjpeg";
	                                    else
	                                        ext = "png";
	                                    
	                                    FrameView.this.setExtensionParam(ext);
	                                }
	                            });
	                        
		                    super.run();
		                    return;
		                }
		                
		                Log.w("LOGTAG", (new StringBuilder("***")).append(buffer).append("***").toString());
		                if(buffer.contains("failed") || buffer.contains("No such file") || buffer.contains("no frame") || buffer.contains("not found") || buffer.contains("could not find codec")) 
		                	break;
	                }

	                if(!isFirst) {
	                    contentView.post(new Runnable() {

	                        public void run() {
	                            error.corruptVideo();
	                            addBitmap("", null, index);
	                        }
	                    });
	                    
	                    return;
	                }
	                
	                String cmd[];
	                String ext;
	                
	                cmd = new String[5];
	                cmd[0] = "/data/data/com.dragonplayer.merge/ffmpeg";
	                cmd[1] = "-c:v";
	                
	                if(bitmapUri.endsWith(".png"))
	                    ext = "mjpeg";
	                else
	                    ext = "png";
	                
	                cmd[2] = ext;
	                cmd[3] = "-i";
	                cmd[4] = bitmapUri;
	                
	                checkImage(cmd, false, error, index);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }).start();
    }
    //zoom in/out 
    private void zoomIN(float f) {
        android.widget.FrameLayout.LayoutParams layoutparams = (android.widget.FrameLayout.LayoutParams)contentView.getLayoutParams();
        float f1 = (float)layoutparams.width / (float)layoutparams.height;
        int w = (int)Math.ceil(f + (float)layoutparams.width);
        int h = (int)Math.ceil((float)w / f1);
        int l = (int)Math.ceil((float)layoutparams.leftMargin - f / 2.0F);
        int t = (int)Math.ceil((float)layoutparams.topMargin - f / 2.0F);
        //MLog.d("FrameView3ZoomIn",w+":"+h+":"+l+":"+t);
        
        if(w <= maxZoomWidth && h <= maxZoomHeight) {
            layoutparams.width = w;
            layoutparams.height = h;
            
            if(t <= 0 && t >= maxMarginTop)
                layoutparams.topMargin = t;
            
            if(l <= 0 && l >= maxMarginLeft)
                layoutparams.leftMargin = l;
            
            maxMarginLeft = width - layoutparams.width;
            maxMarginTop = height - layoutparams.height;
            
            contentView.setLayoutParams(layoutparams);
            Log.e("FrameView4ZoomIn",maxMarginLeft+":"+maxMarginTop);
            contentView.requestLayout();
        }
    }
    // zoom in/out 
    private void zoomOUT(float f) {
        android.widget.FrameLayout.LayoutParams layoutparams = (android.widget.FrameLayout.LayoutParams)contentView.getLayoutParams();
        float f1 = (float)layoutparams.width / (float)layoutparams.height;
        int w = (int)Math.ceil((float)layoutparams.width - f);
        int h = (int)Math.ceil((float)w / f1);
        int l = (int)Math.ceil((float)layoutparams.leftMargin + f / 2.0F);
        int t = (int)Math.ceil((float)layoutparams.topMargin + f / 2.0F);
        
        if(w >= width && h >= height) {
        	
            layoutparams.width = w;
            layoutparams.height = h;
            if(t <= 0)
                if(t >= maxMarginTop)
                    layoutparams.topMargin = t;
                else
                    layoutparams.topMargin = (int)Math.ceil(f + (float)layoutparams.topMargin);
            
            if(l <= 0)
                if(l >= maxMarginLeft)
                    layoutparams.leftMargin = l;
                else
                    layoutparams.leftMargin = (int)Math.ceil(f + (float)layoutparams.leftMargin);
            
            maxMarginLeft = width - layoutparams.width;
            maxMarginTop = height - layoutparams.height;
            
            contentView.setLayoutParams(layoutparams);
            contentView.requestLayout();
        }
    }

    public void addBitmap(String bmpPath, int t, int l, int w, int h, int index) {
    	Utils.writeLogToFile("FrameView-addBitmap1-"+"bmpPath:"+bmpPath+":t="+t+":=l"+l+":w="+w+":="+h+":index="+index);
        isVideo = false;
        bitmapUri = bmpPath;
        contentView.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        DisplayMetrics displaymetrics = getContext().getResources().getDisplayMetrics();
        contentView.setTag(bmpPath);
        manager.displayImage(bmpPath, contentView, displaymetrics.widthPixels, t, l, width, height, setBounds, w, h, rotation+(int)angle, index,getContext());
       //MLog.d("FrameView4addBitmap",displaymetrics.widthPixels+":"+t+":"+l+":"+width+":"+height);
    }
    Bitmap bitmap = null;
    Bitmap mask = null;
    public void addBitmap(String uri, CorruptVideoError corruptvideoerror, int index) {
    	Utils.writeLogToFile("FrameView-addBitmap2-"+"bmpPath:"+uri+":index="+index);
        isVideo = false;
        bitmapUri = uri;
        contentView.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        DisplayMetrics displaymetrics = getContext().getResources().getDisplayMetrics();
        setExtensionParam("");
        
//        if(corruptvideoerror != null) {
//            String cmd[] = new String[3];
//            cmd[0] = "/data/data/com.dragonplayer.merge/ffmpeg";
//            cmd[1] = "-i";
//            cmd[2] = bitmapUri;
//            checkImage(cmd, true, corruptvideoerror, index);
//        }
        /**mask image*/
      	// bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.color01);
       /** 
        * Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
        BitmapDrawable bitmapDrawble = (BitmapDrawable) drawable;
        mask = bitmapDrawble.getBitmap();
         int size = 300;*/
       
        rotation = mrot;
      // contentView.setImageBitmap(MaskBitmap(bitmap, mask,size,  new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)));
        //MLog.d(this, "getcontext" + getContext() );
        contentView.setTag(uri);
        manager.displayImage(uri, contentView, displaymetrics.widthPixels, -1, -1, width, height, setBounds, -1, -1, rotation+(int)angle, index,getContext());
        //MLog.d("FrameView5addBitmap",uri+displaymetrics.widthPixels+":"+width+":"+height);
    }
    
    private Bitmap MaskBitmap(Bitmap bitmap, Bitmap mask, int size, Xfermode mode) {
        if (null == bitmap || mask == null) {
            return null;
        }
        Bitmap dstBmp = Bitmap.createBitmap(size, size, Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect src = new Rect(0, 0, mask.getWidth(), mask.getHeight());
        Rect dst = new Rect(0, 0, size, size);
        canvas.drawBitmap(mask, src, dst, paint);
        paint.setXfermode(mode);
        src.right = bitmap.getWidth();
        src.bottom = bitmap.getHeight();
        canvas.drawBitmap(bitmap, src, dst, paint);
        return dstBmp;
    }
    public void addVideo(String uri, int t, int l, boolean flag, int w, int h, int index) {

    	Utils.writeLogToFile("FrameView-a"
    			+ "ddVideo1-"+"videoPath:"+uri+":t="+t+":=l"+l+":w="+w+":="+h+":index="+index);
    	
        if(flag) {
            addBitmap(uri, t, l, w, h, index);
        } 
        else {
            addBitmap(uri, t, l, w, h, index);
            showVideo(uri, t, l);
        }
        
        isVideo = true;
        videoUri = uri;
    }

	public void addVideo(final String videoUri, int nStartTime, int nDuration, final CorruptVideoError error, final int index) {
		
		Utils.writeLogToFile("FrameView-addVideo2-videouri="+videoUri+":index="+index);
		
        File file = Utils.writeToFile(BitmapUtil.videoFrame(videoUri, nStartTime));
        if(file != null)
            addBitmap(file.getAbsolutePath(), null, index);
        
        rotation = mrot;
        isVideo = true;
        setVideoDuration(BitmapUtil.getDuration(videoUri));
//        setVideoDuration(nDuration);
        setStartTime(nStartTime);
        this.videoUri = videoUri;
        
        String cmd[];
        cmd = new String[3];
        cmd[0] = "/data/data/com.dragonplayer.merge/ffmpeg";
        cmd[1] = "-i";
        cmd[2] = videoUri;
        Process process;
        
		try {
			process = (new ProcessBuilder(new String[] {
			    "/system/bin/chmod", "755", "/data/data/com.dragonplayer.merge/ffmpeg"
			})).start();
            process.waitFor();

            BufferedReader bufferedreader;
            process.destroy();
            bufferedreader = new BufferedReader(new InputStreamReader((new ProcessBuilder(cmd)).redirectErrorStream(true).start().getInputStream()));
            int i = 0;
        	String s = bufferedreader.readLine();

            while (s != null) {
                Log.w("LOGTAG", (new StringBuilder("***")).append(s).append("***").toString());
                if(s.matches("(.*)Stream(.*)Audio(.*)")) {
                    i++;
                    break;
                }
                
                if(s.contains("failed") || s.contains("no frame") || s.contains("not found") || s.contains("could not find codec")) {
	                contentView.post(new Runnable() {

	                    public void run()
	                    {
	                        addBitmap("", null, index);
	                        error.corruptVideo();
	                    }
	                });

	                return;
                }
                
            	s = bufferedreader.readLine();
            }

            if(i > 0) {
                setVideoHasAudio(true);
            }
            else {
            	setVideoHasAudio(false);
            }
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public int bitmapHeight() {
        return contentView.getLayoutParams().height;
    }

    public int bitmapWidth() {
        return contentView.getLayoutParams().width;
    }
    //move function
    public void calculateMove(float f, float f1) {
        float f2 = f - initialX;
        float f3 = f1 - initialY;
        float f4 = (f2 * 100F) / (float)screenWidth;
        float f5 = (f3 * 100F) / (float)screenHeight;
        
        android.widget.FrameLayout.LayoutParams layoutparams = (android.widget.FrameLayout.LayoutParams)contentView.getLayoutParams();
        
        int l = (int)Math.ceil((float)layoutparams.leftMargin + (float)getWidth() * (f4 / 100F));
        int t = (int)Math.ceil((float)layoutparams.topMargin + (float)getHeight() * (f5 / 100F));
        
        if(l <= 0 && l >= maxMarginLeft)
            layoutparams.leftMargin = l;
        
        if(t <= 0 && t >= maxMarginTop)
            layoutparams.topMargin = t;
        //MLog.d("FrameView6addBitmap",f2+":"+f3+":"+f4+":"+f5+":"+l+":"+t);
        contentView.setLayoutParams(layoutparams);
        /**Drawable drawable = getResources().getDrawable(R.drawable.ic_launchergreen);
       	BitmapDrawable bitmapDrawble =  (BitmapDrawable) drawable;
       	mask = bitmapDrawble.getBitmap();
       	int size = 300;    
       	contentView.setImageBitmap(MaskBitmap(mBitmap, mask,size,  new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)));*/
        contentView.requestLayout();
    }

    public float getAudioVolume() {
        return audioVolume;
    }

    public String getBitmapUri() {
        return bitmapUri;
    }

    public int getEnd() {
        return end;
    }

    public String getExtensionParam() {
        return extensionParam;
    }

    public int getFrameId() {
        return frameId;
    }

    public int getHeightt() {
        return height;
    }

    public int getLeftMargin() {
        return ((android.widget.FrameLayout.LayoutParams)contentView.getLayoutParams()).leftMargin;
    }

    public float getRotation() {
        return (float)rotation;
    }
    
    public float getAngle() {
        return (float)angle;
    }

    public FramesLayout.OnSelectionListener getSelectionListener() {
        return selectionListener;
    }

    public int getStart() {
        return start;
    }

    public int getTopMargin() {
        return ((android.widget.FrameLayout.LayoutParams)contentView.getLayoutParams()).topMargin;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public int getWidthh() {
        return width;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public boolean isVideoHasAudio() {
        return videoHasAudio;
    }

    public boolean isVideoPlaying() {
        if(videoView != null)
            return videoView.isPlaying();
        else
            return false;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        DisplayMetrics displaymetrics = getContext().getResources().getDisplayMetrics();
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    public void pauseVideo() {
        videoView.pause();
    }

    public void playVideo(final android.media.MediaPlayer.OnCompletionListener listener, boolean flag) {
    	
        if(videoView == null) {
            videoContent = new RelativeLayout(getContext());
            videoContent.setId(333);
            videoView = new VideoView(getContext());
            videoView.setMediaController(null);
            videoView.setBackgroundColor(0);
            
            android.widget.RelativeLayout.LayoutParams layoutparams = new android.widget.RelativeLayout.LayoutParams(-1, -1);
            
            layoutparams.addRule(10);
            layoutparams.addRule(11);
            layoutparams.addRule(12);
            layoutparams.addRule(9);
            
            videoContent.addView(videoView, layoutparams);
            
            if(findViewById(333) == null)
                addView(videoContent);
        }
        
        videoContent.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        
        android.widget.FrameLayout.LayoutParams layoutparams = (android.widget.FrameLayout.LayoutParams)contentView.getLayoutParams();
        android.widget.FrameLayout.LayoutParams layoutparams1 = new android.widget.FrameLayout.LayoutParams(layoutparams.width, layoutparams.height);
        
        layoutparams1.gravity = 51;
        layoutparams1.leftMargin = layoutparams.leftMargin;
        layoutparams1.topMargin = layoutparams.topMargin;
        
        videoContent.setLayoutParams(layoutparams1);
        videoContent.bringToFront();
        videoContent.requestLayout();
        videoView.requestLayout();
        
        videoView.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mediaplayer) {
                contentView.bringToFront();
                videoContent.setVisibility(View.GONE);
                contentView.setVisibility(View.VISIBLE);
                listener.onCompletion(mediaplayer);
            }
        });
        
        videoView.setVideoPath(videoUri);
        Log.d("videoPath", videoUri);
        videoView.start();
    }

    public void setAudioVolume(float f) {
        audioVolume = f;
    }

    public void setEnd(int i) {
        end = i;
    }

    public void setExtensionParam(String s) {
        extensionParam = s;
    }

    public void setFrameId(int i) {
        frameId = i;
    }

    public void setHeight(int i) {
        height = i;
    }

    public void setRotation(float f) {
        rotation = (int)f;
    }
    
    public void setAngle(float f) {
        angle = (int)f;
    }

    public void setSelectedBackground(boolean flag) {
        if(flag) {
//            background.setBackgroundResource(R.drawable.ic_imagechange);
        	background.setBackgroundResource(0);
        } 
        else {
            background.setBackgroundResource(0);
        }
    }

    public void setChangeSelectedBackground(boolean flag) {
        if(flag) {
            background.setBackgroundResource(R.drawable.ic_imagechange);
//        	background.setBackgroundResource(0);
        } 
        else {
            background.setBackgroundResource(0);
        }
    }
    
    public void setSelectionListener(FramesLayout.OnSelectionListener onselectionlistener) {
        selectionListener = onselectionlistener;
    }

    public void setStart(int i) {
        start = i;
    }

    public void setVideo(boolean flag) {
        isVideo = flag;
    }

    public void setVideoDuration(int i) {
        videoDuration = i;
    }

    public void setVideoHasAudio(boolean flag) {
        videoHasAudio = flag;
    }

    public void setVideoUri(String s) {
        videoUri = s;
        //setVideoDuration(BitmapUtil.getDuration(s));
    }

    public void setWidth(int i) {
        width = i;
    }

    public void showVideo(String s, int i, int j) {
        contentView.bringToFront();
    }

    public int zoomImage(float f, float f1, float f2, float f3) {
    	
        if(pointerX == -1F || pointerY == -1F || initialX == -1F || initialY == -1F) 
        	return -1;

        float f4;
        f4 = (float)Math.sqrt(Math.pow(initialX - pointerX, 2D) + Math.pow(initialY - pointerY, 2D)) - (float)Math.sqrt(Math.pow(f - f2, 2D) + Math.pow(f1 - f3, 2D));
        
        ZOOM_TYPE zoom_type;
        
        if(f4 > 0.0F)
            zoom_type = ZOOM_TYPE.ZOOM_OUT;
        else if(f4 < 0.0F)
            zoom_type = ZOOM_TYPE.ZOOM_IN;
        else
            zoom_type = ZOOM_TYPE.UNKNOWN;
        
        if(zoom_type == ZOOM_TYPE.UNKNOWN) {
            initialX = f;
            initialY = f1;
            pointerX = f2;
            pointerY = f3;
            return 0;
        }
        
        if (zoom_type == ZOOM_TYPE.ZOOM_IN){
        	zoomIN(Math.abs(f4 + f4 / 2.0F));
        }
        else if (zoom_type == ZOOM_TYPE.ZOOM_OUT){
        	zoomOUT(Math.abs(f4 + f4 / 2.0F));
        }

        initialX = f;
        initialY = f1;
        pointerX = f2;
        pointerY = f3;
        
        return 0;
    }
    //旋轉 function
    public void rotateImage(float f, float f1, float f2, float f3, float x1, float y1, float x2, float y2) {
    	
    	if (isVideo)
    		return;
    	
        if(x1 == -1F || y1 == -1F || x2 == -1F || y2 == -1F) 
        	return;

        double angle1 = getAngle(x1, y1, x2, y2);
        double angle2 = getAngle(f, f1, f2, f3);
        
        angle = angle - (angle2 - angle1);
        
        if (angle > 360)
        	angle = angle - 360;
        if (angle < 0)
        	angle = angle + 360;
        
        if (mBitmap != null) {
	        Matrix matrix = new Matrix();
	        matrix.postRotate((float)angle + rotation);
	        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
	        contentView.setImageBitmap(bitmap);
        }
    }
    
    private void longClick() {
        if (timeout.isFastDoubleClick()) {
            Log.d(TAG,"devon longClick return");
            return;
        }
//    	MLog.i(this, "FastDoubleclick");
        Log.d(TAG,"devon longClick ");
        if (bChangePosFlag) {
            Log.d(TAG,"devon bChangePosFlag bChangePosFlag " + bChangePosFlag);
    		((MainActivity)mContext).changePosImage();
    		bChangePosFlag = false;
    	}
    	else {
            ((MainActivity) mContext).selectFromGallery();
        }
    }

    public void setStartTime(int nStartTime) {
    	starttime = nStartTime;	
    }
    
    public int getStartTime() {
    	return starttime;	
    }

    public void initFrame() {
        initialX = -1F;
        initialY = -1F;
        pointerX = -1F;
        pointerY = -1F;
        maxMarginLeft = 2;
        maxMarginTop = 2;
        bitmapUri = "";
        videoUri = "";
        videoDuration = 0;
        audioVolume = 1.0F;
        videoHasAudio = false;
        extensionParam = "";
        isLongClick = false;
        isVideo = false;
        isLongTapStarted = false;
        rotation = 0;
        starttime = 0;
 
       	contentView.setImageBitmap(null);
       	   
    }
   
    public void setChangePosFlag(boolean flag) {
    	bChangePosFlag = flag;
    }

    public void setFrameBitmap(Bitmap bmp) {
    	mBitmap = bmp;
    }

    public Bitmap getFrameBitmap() {
    	return mBitmap;
    }
    
    private double getAngle(double x1, double y1, double xTouch, double yTouch) {
        double x = xTouch - x1;
        //double y = getHeight() - yTouch - txtY;
        double y = y1 - yTouch;
     
        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }
     
    /**
     * @return The selected quadrant.
     */
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }
}
