package com.dragonplayer.merge.frames;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.*;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.dragonplayer.merge.R;
import com.dragonplayer.merge.TextInputActivity;
import com.dragonplayer.merge.utils.BitmapManager;
import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.MLog;
import com.dragonplayer.merge.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.*;

public class FramesLayout extends RelativeLayout {

	public static final int FRAMEID_EAT = 19;
	public static final int FRAMEID_HAPPYBIRTHDAY = 21;
	private static final int FRAME_DEFAULTVIDEOSIZEWIDTH = 1096;
	private static final int FRAME_DEFAULTVIDEOSIZEHEIGHT = 536;
	
	private static final int TEXTSCALEMODE_NONE = 0;
	private static final int TEXTSCALEMODE_SELECT = 1;
	private static final int TEXTSCALEMODE_MOVE = 2;
	private static final int TEXTSCALEMODE_SCALE = 3;
	private static final int TEXTSCALEMODE_ROTATE = 4;
	
	private static final int ROTATE_ICON_WIDTH = 50;
	private static final int ROTATE_ICON_HEIGHT = 50;
	private static final int SCALE_ICON_WIDTH = 50;
	private static final int SCALE_ICON_HEIGHT = 50;
	
	private static final int STEPMIN = 7;
	
    private long audioEndTime;
    private String audioPath;
    private long audioStartTime;
    private float audioVolume;
    private Paint borderPaint;
    private int borderWidth;
    private Frame frame;
    private int frameID;
    private ArrayList<FrameView> frames;
    private int height;
    private boolean isFillable;
    private boolean isOverride;
    private boolean isSequentially;
    private boolean isWithSelection;
    private BitmapManager manager;
    private String order;
    private int parentHeight;
    private int parentWidth;
    private Button playBtn;
    private int selectedFrame;
    private int changeSelectedFrame = -1;
    private boolean showVideoAsBitmap;
    private int videoDuration;
    private int width;
    private BufferedWriter writer;
    private boolean outputStartFlag;
     private Bitmap backgroundBmp;
     private Bitmap backgroundBmp_bg;
    private String textImagePath;
    private int color;
    
    private Rect rcText = new Rect();
    public float fTxtTmpAngle = 0.0f;
    public float fTxtAngle = 0.0f;
    public int txtX = 0;
    public int txtY = 0;
    public float fZoom = 1.0f;
    private int nOrgPosX = 0;
    private int nOrgPosY = 0;

    private int nTextScaleMode = TEXTSCALEMODE_NONE;
    
    public static interface OnSelectionListener {
        public abstract void onSelected(int i);
    }

    public FramesLayout(Context context, AttributeSet attributeset) {
    	
        super(context, attributeset);
        
        borderPaint = new Paint();
        selectedFrame = 0;
        color = 0;
        frames = new ArrayList<FrameView>();
        showVideoAsBitmap = true;
        audioPath = "";
        isSequentially = false;
        order = "";
        audioVolume = 1.0F;
        isOverride = false;
        videoDuration = 5;
        audioStartTime = -1L;
        audioEndTime = -1L;
        textImagePath = null;
        
        setGravity(17);
    }

    public FramesLayout(Context context, AttributeSet attributeset, int i) {
    	
        super(context, attributeset, i);
        
        borderPaint = new Paint();
        selectedFrame = 0;
        color = 0;
        frames = new ArrayList();
        showVideoAsBitmap = true;
        audioPath = "";
        isSequentially = false;
        order = "";
        audioVolume = 1.0F;
        isOverride = false;
        videoDuration = 5;
        audioStartTime = -1L;
        audioEndTime = -1L;
        textImagePath = null;
        
        setGravity(17);
    }

    public FramesLayout(Context context, Frame frame1, boolean flag, int i, boolean flag1, int j, int k, 
            BitmapManager bitmapmanager, boolean flag2, Button button) {
    	
        super(context);
    	
        Log.d("FrameLayout", "FrameLayout");

    	borderPaint = new Paint();
        selectedFrame = 0;
        frames = new ArrayList<FrameView>();
        showVideoAsBitmap = true;
        audioPath = "";
        isSequentially = false;
        order = "";
        audioVolume = 1.0F;
        isOverride = false;
        videoDuration = 5;
        audioStartTime = -1L;
        audioEndTime = -1L;
        frame = frame1;
        manager = bitmapmanager;
        setGravity(17);
        setWillNotDraw(false);
        isWithSelection = flag;
        frameID = i;
        isFillable = flag1;
        showVideoAsBitmap = flag2;
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(0xFFCCCCCC);
        playBtn = button;
        textImagePath = null;

        color = frame.getColor();
        backgroundBmp = BitmapUtil.getBitmapFromAsset(context, "Frames/"+frame.getFrameName()+".png", j, k);
        backgroundBmp_bg = BitmapUtil.getBitmapFromAsset(context, "Frames/"+frame.getFrameName()+"-bg.png", j, k);
        //backgroundBmp_bg = BitmapUtil.getBitmapFromAsset(context, "Frames/application_bg.png", j, k);
       // application_bg
        initView(j, k);
        
        setBorderWidth(0);
        setDatas();

        if(flag)
        	setSelectedFrame(selectedFrame);//setSelectedFrame(0);
        
        setDrawingCacheEnabled(true);
        setWillNotDraw(false);
        setClickable(true);
    }

    private int getAudioDuration() {
    	
        if(!audioPath.equals(""))
            return MediaPlayer.create(getContext(), Uri.parse(audioPath)).getDuration();
        else
            return 0;
    }

    public static int getOrienation(String s) {
    	
        MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
        String metaData;
        mediametadataretriever.setDataSource(s);
        metaData = mediametadataretriever.extractMetadata(24);

        int orientation = 0;
        if(metaData == null) {
            String cmd[] = {
                    "/data/data/com.dragonplayer.merge/ffmpeg", "-i", s
                };
            
            BufferedReader bufferedreader;
            
            try {
				bufferedreader = new BufferedReader(new InputStreamReader((new ProcessBuilder(cmd)).redirectErrorStream(true).start().getInputStream()));
				
	            Log.v("LOGTAG", "***Starting FFMPEG***");
	            String buffer = bufferedreader.readLine();
	            
	            while (buffer != null) {
	                if(buffer.indexOf("rotate") >= 0) {
	                	
	        	        String cmd1[] = buffer.split(":");
	        	        Log.w("rotationCom", (new StringBuilder()).append(cmd1[1]).toString());
	        	        orientation = Integer.parseInt(cmd1[1].trim());
	                }
	                
	                buffer = bufferedreader.readLine();
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        else {
        	orientation = Integer.parseInt(metaData);
        }

        mediametadataretriever.release();
        
        return orientation;
    }

    private void initView(int w, int h) {
    	 MLog.e(this, "initView");
        parentHeight = h;
        parentWidth = w;
        //borderWidth = (int)(0.02F * (float)parentWidth);
        borderWidth = 0;
        
        if(getChildCount() != 0) 
        	return;

        RelativeLayout relativelayout;
        ArrayList<FramePart> arraylist;
        
        relativelayout = new RelativeLayout(getContext());
        width = (int)Math.ceil((float)w * ((float)frame.getWidth() / 100F));
        height = (int)Math.ceil((float)h * ((float)frame.getHeight() / 100F));
        
        if(width % 2 != 0)
            width = 1 + width;
        
        if(height % 2 != 0)
            height = 1 + height;

        int widthGap = parentWidth - width;
        
        if(widthGap % 2 != 0)
            widthGap++;
        
        int heightGap = parentHeight - height;
        
        if(heightGap % 2 != 0)
            heightGap++;
        
        float f = (float)Math.ceil((float)widthGap / 2.0F);
        float f1 = (float)Math.ceil((float)heightGap / 2.0F);

        addView(relativelayout, new android.widget.RelativeLayout.LayoutParams(width, height));
        arraylist = frame.getParts();
        MLog.e(this, "initViewarraylist" + arraylist );
        for (int k = 0; k < arraylist.size(); k++) {
        	
	        FramePart framepart = (FramePart)arraylist.get(k);
	        
	        int w1 = (int)Math.ceil((float)width * ((float)(framepart.getEnd().x - framepart.getStart().x) / 100F));
	        int h1 = (int)Math.ceil((float)height * ((float)(framepart.getEnd().y - framepart.getStart().y) / 100F));
	        
            int startX = (int)Math.ceil(f + (float)width * ((float)framepart.getStart().x / 100F));
            int startY = (int)Math.ceil(f1 + (float)height * ((float)framepart.getStart().y / 100F));
            int endX = (int)Math.ceil(f + (float)width * ((float)framepart.getEnd().x / 100F));
            int endY = (int)Math.ceil(f1 + (float)height * ((float)framepart.getEnd().y / 100F));

//            Bitmap bmp = Bitmap.createBitmap(backgroundBmp_bg, startX, startY, 
//            		(backgroundBmp_bg.getWidth() < endX) ? backgroundBmp_bg.getWidth() - startX : endX - startX, 
//            		(backgroundBmp_bg.getHeight() < endY) ? backgroundBmp_bg.getHeight() - startY : endY - startY);
            
            Bitmap bmp = BitmapUtil.getFrameBitmap(getContext(), backgroundBmp_bg, startX, startY, endX, endY);
	        FrameView frameview = new FrameView(getContext(), w1, h1, manager, color | 0xFF000000, bmp, 
	        		frame.getFrameDirection() == Frame.FRAMEVERTICAL ? 0 : 90);
	        frameview.setEnabled(isWithSelection);
	        android.widget.RelativeLayout.LayoutParams layoutparams = new android.widget.RelativeLayout.LayoutParams(w1, h1);
	        
	        layoutparams.topMargin = (int)Math.ceil((float)height * ((float)framepart.getStart().y / 100F));
	        layoutparams.leftMargin = (int)Math.ceil((float)width * ((float)framepart.getStart().x / 100F));
	        
	        frameview.setFrameId(k);
	        frameview.setSelectionListener(new OnSelectionListener() {
	
	            public void onSelected(int j1) {
	                setSelectedFrame(j1);
	                MLog.d(this+"FramesLayout", "frameviewsetSelectionListener");
	            }
	        });
	        
	        frameview.setLayoutParams(layoutparams);
	        frameview.setStart(layoutparams.leftMargin);
	        frameview.setEnd(layoutparams.topMargin);
	        relativelayout.addView(frameview);
	        frames.add(frameview);
        }
    }

    private void setDatas() {
    	
        boolean flag = isFillable;
        boolean flag1 = false;
        
        if(!flag) {
            if(flag1 && !showVideoAsBitmap)
                setupPlayPauseButton();
            return;
        }

        String data;
        boolean flag2;
        
        data = Utils.getDatas(getContext());
        flag2 = data.equals("");
        flag1 = false;
        
        if(flag2) {
            if(flag1 && !showVideoAsBitmap)
                setupPlayPauseButton();
            return;
        }

        int tmpAngle = 0;
        
        JSONObject jsonobject;
        int nId;
        try {
			jsonobject = new JSONObject(data);
	        nId = jsonobject.getInt("frameID");
	        flag1 = false;
	        
	        if(nId != frameID) {
//	            if(flag1 && !showVideoAsBitmap)
//	                setupPlayPauseButton();
//	            return;
	        	Frames tmpframes = Frames.newInstance((Activity)getContext());
	        	if (tmpframes.getFrameWithId(nId).getFrameDirection() == Frame.FRAMEVERTICAL && 
	        			tmpframes.getFrameWithId(frameID).getFrameDirection() == Frame.FRAMEHORIZONTAL)
	        		tmpAngle = 90;
	        	else if (tmpframes.getFrameWithId(nId).getFrameDirection() == Frame.FRAMEHORIZONTAL && 
	        			tmpframes.getFrameWithId(frameID).getFrameDirection() == Frame.FRAMEVERTICAL)
	        		tmpAngle = -90;
	        }

	        JSONArray jsonarray;
	        jsonarray = jsonobject.getJSONArray("datas");
	        textImagePath = jsonobject.getString("textImagePath");
	        fTxtAngle = (float)jsonobject.getDouble("fTxtAngle");
	        txtX = jsonobject.getInt("txtX");
	        txtY = jsonobject.getInt("txtY");
	        fZoom = (float)jsonobject.getDouble("fZoom");
	        selectedFrame = jsonobject.getInt("selectedFrame");
	        setAudioPath(jsonobject.getString("audioPath"));

	        Utils.writeLogToFile("FrameLayout-setDatas-selFrame="+selectedFrame + ":len="+jsonarray.length());
	        
	        for (int k = 0; k < jsonarray.length(); k++) {
	        	
	            JSONObject jsonobject1;
	            boolean isVideo;
	            String bmpUri;
	            int tMargin;
	            int lMargin;
	            int width1;
	            int height1;
	            jsonobject1 = jsonarray.getJSONObject(k);
	            isVideo = jsonobject1.getBoolean("video");
	            bmpUri = jsonobject1.getString("bitmap_uri");
	            
	            if (frameID == FRAMEID_EAT || frameID == FRAMEID_HAPPYBIRTHDAY) {
	            	if (isVideo)
	            		continue;
	            }
	            
	            if(!bmpUri.equals("")) {
		            Utils.writeLogToFile("FrameLayout-setDatas-"+"setDatas:"+bmpUri+":"+jsonarray.length()+":"+k);
		            
		            tMargin = jsonobject1.getInt("top_margin");
		            lMargin = jsonobject1.getInt("left_margin");
		            width1 = jsonobject1.getInt("width");
		            height1 = jsonobject1.getInt("height");
		            
		            setAudioVolume(k, (float)jsonobject1.getDouble("audio_volume"));
		            setBorderWidth(jsonobject1.getInt("border_width"));
		            setBorderColor(jsonobject1.getInt("color"));
		            setOrder(jsonobject1.getString("order"));
		            setAudioVolume((float)jsonobject1.getDouble("volume"));
		            setStartTime(k, jsonobject1.getInt("starttime"));
		            setDuration(k, jsonobject1.getInt("duration"));
		            setVideoDuration(jsonobject1.getInt("video_duration"));
		            setOverride(jsonobject1.getBoolean("override"));
		            setRotationForPosition(k, jsonobject1.getInt("rotation")+tmpAngle);
		            setAngleForPosition(k, jsonobject1.getInt("angle"));
		            setVideoHasAudioForPosition(k, jsonobject1.getBoolean("videoHasAudio"));
		            setExtension(k, jsonobject1.getString("extension"));
		            setAudioStartTime(jsonobject1.getLong("audioStartTime"));
		            setAudioEndTime(jsonobject1.getLong("audioEndTime"));
		            
		            if(isVideo) {
		            	
		                flag1 = true;
		                
		                try {
		                    String videoUri = jsonobject1.getString("video_uri");
		                    addVideo(bmpUri, k, tMargin, lMargin, width1, height1);
		                    setVideoUriForPosition(k, videoUri);
		                }
		                catch(JSONException jsonexception) {
		                    jsonexception.printStackTrace();
		                }
		            }
		            else {
		            	addBitmap(bmpUri, k, tMargin, lMargin, width1, height1);
		            }
	            }
	        }

	        setSequentially(jsonobject.getBoolean("isSequentially"));

	        if(flag1 && !showVideoAsBitmap)
	            setupPlayPauseButton();
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void setExtension(int i, String s) {
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(i)).setExtensionParam(s);
    }

    private void setVideoHasAudioForPosition(int i, boolean flag) {
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(i)).setVideoHasAudio(flag);
    }

    public void addBitmap(String bmpUri, int index) {
    	Utils.writeLogToFile("FrameLayout-addBitmap1-s="+bmpUri+":i="+index);
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(index)).addBitmap(bmpUri, null, index);
    }

    public void addBitmap(String bmpUri, int index, int t, int l, int w, int h) {
    	Utils.writeLogToFile("FrameLayout-addBitmap2-s="+bmpUri+":i="+index+":j="+t+":k="+l+":=l"+w+":="+h);
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(index)).addBitmap(bmpUri, t, l, w, h, index);
    }

    public void addBitmap(String bmpUri, FrameView.CorruptVideoError corruptvideoerror) {
    	Utils.writeLogToFile("FrameLayout-addBitmap3-s="+bmpUri+":selected="+selectedFrame);
        FrameView frameview = (FrameView)((RelativeLayout)getChildAt(0)).getChildAt(selectedFrame);
        isSequentially = false;
        order = "";
        frameview.addBitmap(bmpUri, corruptvideoerror, selectedFrame);
    }

    public void addVideo(String bmpUri, int index, int t, int l, int w, int h) {
    	Utils.writeLogToFile("FrameLayout-addVideo1-s="+bmpUri+":i="+index+":j="+t+":k="+l+":l="+w+":=i1"+h);
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(index)).addVideo(bmpUri, t, l, showVideoAsBitmap, w, h, index);
    }

    public void addVideo(String bmpUri, int nStartTime, int nDuration, FrameView.CorruptVideoError corruptvideoerror) {
    	Utils.writeLogToFile("FrameLayout-addVideo2-s="+bmpUri+":selected="+selectedFrame);
        FrameView frameview = (FrameView)((RelativeLayout)getChildAt(0)).getChildAt(selectedFrame);
        isSequentially = false;
        order = "";
        frameview.addVideo(bmpUri, nStartTime, nDuration, corruptvideoerror, selectedFrame);
    }

    public int countVideos() {
        int nRet = 0;
        RelativeLayout relativelayout = (RelativeLayout)getChildAt(0);
        
        for (int j = 0; j < relativelayout.getChildCount(); j++) {
            if(((FrameView)relativelayout.getChildAt(j)).isVideo())
                nRet++;
        } 

        return nRet;
    }

    protected void dispatchDraw(Canvas canvas) {
    	
        super.dispatchDraw(canvas);
        
        int widthGap = parentWidth - width;
        
        if(widthGap % 2 != 0)
            widthGap++;
        
        int heightGap = parentHeight - height;
        
        if(heightGap % 2 != 0)
            heightGap++;
        
        float f = (float)Math.ceil((float)widthGap / 2.0F);
        float f1 = (float)Math.ceil((float)heightGap / 2.0F);
        
        ArrayList<FramePart> arraylist = frame.getParts();

        for (int k = 0; k < arraylist.size(); k++) {
        	
            FramePart framepart = (FramePart)arraylist.get(k);
            
            int startX = (int)Math.ceil(f + (float)width * ((float)framepart.getStart().x / 100F));
            int startY = (int)Math.ceil(f1 + (float)height * ((float)framepart.getStart().y / 100F));
            int endX = (int)Math.ceil(f + (float)width * ((float)framepart.getEnd().x / 100F)) + 1;
            int endY = (int)Math.ceil(f1 + (float)height * ((float)framepart.getEnd().y / 100F)) + 1;
            
            FrameView frameview = (FrameView)((RelativeLayout)getChildAt(0)).getChildAt(k);
            if (frameview != null && ((frameview.getVideoUri() != null && !frameview.getVideoUri().isEmpty()) || 
            		(frameview.getBitmapUri() != null && !frameview.getBitmapUri().isEmpty()))) {
            }
            else {
            	//Paint paint = new Paint();
             	//paint.setColor(color | 0xFF000000);
             	//canvas.drawRect(new Rect(startX, startY, endX, endY), paint);
            }
            
            canvas.drawBitmap(backgroundBmp, new Rect(startX, startY, endX, endY),  new Rect(startX, startY, endX, endY), null);
             
        }
        
        drawCustomText(canvas);
        
        setDrawingCacheEnabled(true);
    }

    private void drawCustomText(Canvas canvas) {
        if (textImagePath != null && !textImagePath.isEmpty()) {
        	Bitmap bmp = BitmapFactory.decodeFile(textImagePath);

        	Matrix matrix = new Matrix();
        	matrix.postScale(fZoom / TextInputActivity.MAXTEXTZOOM, fZoom / TextInputActivity.MAXTEXTZOOM);
        	matrix.postRotate(fTxtAngle);
    	     
        	Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        	      
        	rcText.left = txtX - resizedBitmap.getWidth() / 2;
        	rcText.top = txtY - resizedBitmap.getHeight() / 2;
        	rcText.right = txtX + resizedBitmap.getWidth() / 2;
        	rcText.bottom  = txtY + resizedBitmap.getHeight() / 2;
        	
        	canvas.drawBitmap(resizedBitmap, new Rect(0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight()), 
        			rcText, null);
        	resizedBitmap.recycle();
        	bmp.recycle();
        	
        	if (nTextScaleMode != TEXTSCALEMODE_NONE) {
            	Paint paint = new Paint();
	        	paint.setColor(0xFFFFFFFF);
	        	canvas.drawLine(rcText.left - 2, rcText.top - 2, rcText.right + 2, rcText.top - 2, paint);
	        	canvas.drawLine(rcText.right + 2, rcText.top - 2, rcText.right + 2, rcText.bottom + 2, paint);
	        	canvas.drawLine(rcText.right + 2, rcText.bottom + 2, rcText.left - 2, rcText.bottom + 2, paint);
	        	canvas.drawLine(rcText.left - 2, rcText.bottom + 2, rcText.left - 2, rcText.top - 2, paint);
	        	paint.setColor(0xFF000000);
	        	canvas.drawLine(rcText.left - 1, rcText.top - 1, rcText.right + 1, rcText.top - 1, paint);
	        	canvas.drawLine(rcText.right + 1, rcText.top - 1, rcText.right + 1, rcText.bottom + 1, paint);
	        	canvas.drawLine(rcText.right + 1, rcText.bottom + 1, rcText.left - 1, rcText.bottom + 1, paint);
	        	canvas.drawLine(rcText.left - 1, rcText.bottom + 1, rcText.left - 1, rcText.top - 1, paint);
	        	
	        	Bitmap bmpScale = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_scale);
	        	canvas.drawBitmap(bmpScale, new Rect(0, 0, bmpScale.getWidth(), bmpScale.getHeight()), 
	        			new Rect(rcText.left - SCALE_ICON_WIDTH, rcText.top - SCALE_ICON_HEIGHT, rcText.left - 2, rcText.top - 2), null);
	        	bmpScale.recycle();
	        	bmpScale = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_rotate);
	        	canvas.drawBitmap(bmpScale, new Rect(0, 0, bmpScale.getWidth(), bmpScale.getHeight()), 
	        			new Rect(rcText.right + 2, rcText.top - ROTATE_ICON_WIDTH, rcText.right + ROTATE_ICON_HEIGHT, rcText.top - 2), null);
	        	bmpScale.recycle();
        	}
        }
    }
    
    private Bitmap getCustomTextBitmap() {
        if (textImagePath != null && !textImagePath.isEmpty()) {
        	Bitmap retBmp = Bitmap.createBitmap(rcText.width(), rcText.height(), Bitmap.Config.ARGB_8888);
        	Canvas canvas = new Canvas(retBmp);
        	Bitmap bmp = BitmapFactory.decodeFile(textImagePath);

        	Matrix matrix = new Matrix();
        	matrix.postScale(fZoom / TextInputActivity.MAXTEXTZOOM, fZoom / TextInputActivity.MAXTEXTZOOM);
        	matrix.postRotate(fTxtAngle);
    	    
        	Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        	      
        	canvas.drawBitmap(resizedBitmap, new Rect(0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight()), 
        			new Rect(0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight()), null);
        	resizedBitmap.recycle();
        	bmp.recycle();
        	
        	return retBmp;
        }
        
        return null;
    }

    public long getAudioEndTime() {
        return audioEndTime;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public long getAudioStartTime() {
        return audioStartTime;
    }

    public float getAudioVolume() {
        return audioVolume;
    }

    public int getBorderColor() {
        return borderPaint.getColor();
    }

    public int getBorderWidth() {
        return (int)Math.ceil((100F * (float)borderWidth) / (float)parentWidth);
    }

    public int getBorderWidthh() {
        return borderWidth;
    }

    public Bitmap getDrawingCache() {
        setBackgroundColor(-1);
        Bitmap bitmap = super.getDrawingCache();
        setBackgroundColor(0);
    	
        return bitmap;
    }

    public ArrayList<FrameView> getFrames() {
        return frames;
    }

    public int getLeftDiff() {
        int i = parentWidth - width;
        if(i % 2 != 0)
            i++;
        return (int)Math.ceil((float)i / 2.0F);
    }

    public String getOrder() {
        return order;
    }

    public int getTopDiff() {
        int i = parentHeight - height;
        if(i % 2 != 0)
            i++;
        return (int)Math.ceil((float)i / 2.0F);
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public int getVideoNr() {
    	
        int i = 0;
        
        for (int j = 0; j < frames.size(); j++) { 
            if(((FrameView)frames.get(j)).isVideo())
                i++;
        } 

        return i;
    }

    public String getVideoUri() {
    	
        for (int i = 0; i < frames.size(); i++) {
            FrameView frameview = (FrameView)frames.get(i);
            if(frameview.isVideo())
                return frameview.getVideoUri();
        } 

        return "";
    }

    public boolean isGIF() {
    	
        boolean flag = false;
        
        for (int i = 0; i < frames.size(); i++) {
            FrameView frameview = (FrameView)frames.get(i);
            if(!frameview.isVideo()) {
            	
                String bmpUri = frameview.getBitmapUri();
                
                if(!bmpUri.equals("")) {
                    Log.d("gif", bmpUri);
                    
                    if(bmpUri.substring(bmpUri.lastIndexOf("."), bmpUri.length()).contains("gif"))
                        flag = true;
                }
            }
        } 

        return flag;
    }

    public boolean isOverride() {
        return isOverride;
    }

    public boolean isSelectedVideo() {
        return ((FrameView)frames.get(selectedFrame)).isVideo();
    }

    public boolean isSequentially() {
        return isSequentially;
    }

    public boolean isVideo() {
    
    	for (int i = 0; i < frames.size(); i++) {
            if(((FrameView)frames.get(i)).isVideo())
                return true;
        } 

    	return false;
    }

    public boolean isVideoOrAudio() {
    	
    	if (frameID == FRAMEID_HAPPYBIRTHDAY || frameID == FRAMEID_EAT)
    		return true;
    	
        for (int i = 0; i < frames.size(); i++)
            if(((FrameView)frames.get(i)).isVideo()) 
            	return true;

        if(audioPath.equals(""))
        	return isGIF();

        return true;
    }
    Bitmap mask = null;
    Bitmap  bitmap = null;
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(color | 0xFF000000);
//        canvas.drawBitmap(backgroundBmp, 0, 0, null);
         
    	  
   	 	 canvas.drawBitmap(backgroundBmp, new Rect(0, 0, backgroundBmp.getWidth(), backgroundBmp.getHeight()), new Rect(0, 0, backgroundBmp.getWidth(), backgroundBmp.getHeight()), null);
    	 MLog.d(this, "onDraw_canvas_backgroundBmp_bg");
         
    }
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void quitRendering() throws IOException {
    	
        if(writer != null) {
            writer.write("q");
            writer.flush();
        }
    }

    public void removeAllViews() {
        frames.clear();
        super.removeAllViews();
    }

    public String renderVideo(FrameInfo frameinfo, com.dragonplayer.merge.fragment.FinishFragment.OnProgressUpdate onprogressupdate, com.dragonplayer.merge.fragment.FinishFragment.FFMpegError ffmpegerror)
    {
        boolean flag;
        String strFilterAudioVolume;
        String strFilterAudioVolumeLabel;
        int ai[];
        String s2;
        String strTransPose0;
        String strTransPose1;
        String strTransPose2;
        String strTransPose3;
        int nAngle4;
        String strTransPose4;
        int nAngle5;
        String strTransPose5;
        String cmd1[];
        boolean flag1;
        int frameTotalCount;
        ArrayList arraylist;
        int nDuration;
        int frameCropH0;
        int frameCropW0;
        int frameCropX0;
        int frameCropY0;
        int videoHasAudioCount;
        int k2;
        int nAngle0;
        int frameScaleHeight0;
        int frameScaleWidth0;
        int frameXPos0;
        int frameYPos0;
        int frameCropH1;
        int frameCropW1;
        int frameCropX1;
        int frameCropY1;
        int nAngle1;
        int frameScaleHeight1;
        int frameScaleWidth1;
        int frameXPos1;
        int frameYPos1;
        int frameCropH2;
        int frameCropW2;
        int frameCropX2;
        int frameCropY2;
        int nAngle2;
        int frameScaleHeight2;
        int frameScaleWidth2;
        int frameXPos2;
        int frameYPos2;
        int frameCropH3;
        int frameCropW3;
        int frameCropX3;
        int frameCropY3;
        int nAngle3;
        int frameScaleHeight3;
        int frameScaleWidth3;
        int frameXPos3;
        int frameYPos3;
        int frameCropH4;
        int frameCropW4;
        int frameCropX4;
        int frameCropY4;
        int frameScaleHeight4;
        int frameScaleWidth4;
        int frameXPos4;
        int frameYPos4;
        int frameCropH5;
        int frameCropW5;
        int frameCropX5;
        int frameCropY5;
        int frameScaleHeight5;
        int frameScaleWidth5;
        int frameXPos5;
        int frameYPos5;
        String as2[];
        int i15;
        StringBuilder stringbuilder;
        String renderbuffer;
        int i20;
        String cmd[];
        ProcessBuilder processbuilder;
        int i1;
        int l14;
        ProcessBuilder processbuilder1;
        Process process;
        BufferedReader bufferedreader;
        int frameDuration;
        boolean flag2;
        int firstDuration;
        int j20;
        String s9;
        String s10;
        String s11;
        String s12;
        String s13;
        String s14;
        String s15;
        String s16;
        String s17;
        String s18;
        String strBuffer3[];
        String s20;
        int k15;
        int l15;
        String s21;
        String s22;
        String s23;
        String s24;
        String s25;
        String s26;
        String s27;
        String s28;
        String s29;
        String s30;
        String s31;
        String s32;
        String s33;
        String s34;
        String s35;
        int i16;
        int j16;
        String s36;
        String s37;
        String s38;
        String s39;
        String s40;
        String s41;
        String s42;
        String s43;
        String s44;
        String s45;
        String s46;
        String s47;
        String s48;
        int k16;
        int l16;
        String s49;
        String s50;
        String s51;
        String s52;
        String s53;
        String s54;
        String s55;
        String s56;
        String s57;
        String s58;
        String s59;
        int i17;
        int j17;
        String s60;
        String s61;
        String s62;
        String strFilterColor1;
        String strFilterScale1;
        String strFilterOverlay1;
        String strFilterOverlayBG1;
        String s67;
        String strFilterOverlayBGTemp1;
        int k17;
        int l17;
        String s69;
        String s70;
        String strFilterColor0;
        String strFilterScale0;
        String strFilterOverlay0;

        String strFilterBackground="";
        String strFilterText="";
	    String strFilterTextOverlay="";
        String strFilterForeround0="";
        String strFilterForeround1="";
        String strFilterForeround2="";
        String strFilterForeround3="";
        String strFilterForeround4="";
        String strFilterForeround5="";
        
        Process process1;
        
//        if(!isVideo() && !audioPath.equals(""))
//            flag = true;
//        else
            flag = false;
        
        Log.d("start end", (new StringBuilder(String.valueOf(audioStartTime))).append(" ").append(audioEndTime).toString());
        
        strFilterAudioVolume = "";
        strFilterAudioVolumeLabel = "";
        cmd = (new String[] {
            "/system/bin/chmod", "755", "/data/data/com.dragonplayer.merge/ffmpeg"
        });
        
        processbuilder = new ProcessBuilder(cmd);
        
        try {
			process1 = processbuilder.start();
	        process1.waitFor();
	        process1.destroy();
	        
	        ai = new int[6];
	        s2 = audioPath;
	        strTransPose0 = "";
	        strTransPose1 = "";
	        strTransPose2 = "";
	        strTransPose3 = "";
	        nAngle4 = 0;
	        strTransPose4 = "";
	        nAngle5 = 0;
	        strTransPose5 = "";
	        
	        cmd1 = getOrder().split(" ");
	        
	        Log.w("ORDERS", getOrder());
	        

	        if(cmd1.length > 1)
	            flag1 = true;
	        else
	            flag1 = false;
	        
	        frameTotalCount = frameinfo.getElementSize();
	        arraylist = new ArrayList();
	        arraylist.add("/data/data/com.dragonplayer.merge/ffmpeg");
	        
	        if(!flag1) { 
	            nDuration = frameinfo.getDuration(frameinfo.getLongestDurationIndex());
	        }
	        else {  
		        nDuration = 0;
		        firstDuration = frameinfo.getDuration(Integer.parseInt(cmd1[0]));
		        Log.w("FIRSTLENGTH", (new StringBuilder()).append(firstDuration).toString());
		        
		        for (i20 = 0; i20 < cmd1.length; i20++) { 
		            int k20 = Integer.parseInt(cmd1[i20]);
		            nDuration += frameinfo.getDuration(k20);
		            
		            if(i20 > 0) {
		                ai[k20] = nDuration - frameinfo.getDuration(k20);
		                Log.w("videoDelays", (new StringBuilder("[")).append(k20).append("]").append(ai[k20]).toString());
		            }
		        }
	        }
	        
	        i1 = getAudioDuration();
	        
//	        if(nDuration == 0 && i1 > 0)
//	            nDuration = i1;
	        if(i1 > 0)
	            nDuration = i1;
	        if(nDuration == 0 || isOverride)
	            nDuration = 1000 * videoDuration;
	        
	        frameCropH0 = 0;
	        frameCropW0 = 0;
	        frameCropX0 = 0;
	        frameCropY0 = 0;
	        videoHasAudioCount = 0;
	        k2 = 0;
	        nAngle0 = 0;
	        frameScaleHeight0 = 0;
	        frameScaleWidth0 = 0;
	        frameXPos0 = 0;
	        frameYPos0 = 0;
	        											//修改底圖
	        String strBackground = Utils.writeToPNGFile(backgroundBmp).getAbsolutePath();
	        String strTextPath = "";
	        arraylist.add("-i");
	        arraylist.add(strBackground);

	    
	        strFilterBackground = "[0:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	        		frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ":0:0[imgbg];";

	        if (textImagePath != null && !textImagePath.isEmpty()) {
	        	strTextPath = Utils.writeToPNGFile(getCustomTextBitmap()).getAbsolutePath();
		        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
		        
		        options.inJustDecodeBounds = true;
		        options.inDensity = 180;

		        BitmapFactory.decodeFile(strTextPath, options);
		        Log.e("strTextPath", strTextPath);
		        
		        strFilterText = "scale=" + options.outWidth + ":" + options.outHeight + ",crop=" + 
		        		options.outWidth + ":" + options.outHeight + ":0:0[textfg];";
		        strFilterTextOverlay = "overlay=" + (txtX - options.outWidth / 2) + ":" + (txtY - options.outHeight / 2);
	        }
	        
	        if(frameTotalCount > 0) { 
		        frameDuration = frameinfo.getDuration(0);
		        videoHasAudioCount = 0;
		        if(frameDuration != 0)
		        {
		            if(frameinfo.videoHasAudio(0)) {
		                strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[1:a]volume=").append(frameinfo.getVideoAudioVolume(0)).append("[a0];").toString();
		                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a0]").toString();
		                videoHasAudioCount = 1;
		            }
		        }
		        
		        frameScaleHeight0 = frameinfo.getFrameScaleHeight(0);
		        frameScaleWidth0 = frameinfo.getFrameScaleWidth(0);
		        frameCropX0 = frameinfo.getCropX(0);
		        frameCropY0 = frameinfo.getCropY(0);
		        frameCropH0 = frameinfo.getCropH(0);
		        frameCropW0 = frameinfo.getCropW(0);
		        frameXPos0 = frameinfo.getXpos(0);
		        frameYPos0 = frameinfo.getYpos(0);
		        
		        if(frameinfo.getDuration(0) == 0) {
		        	ExifInterface exifinterface = new ExifInterface(frameinfo.getUrl(0));
		            String strOrientation = exifinterface.getAttribute("Orientation");
		            int nOrientation = Integer.parseInt(strOrientation);
		            Log.w("Imageorientation", (new StringBuilder()).append(strOrientation).toString());
		            nAngle0 = 0;
		            
		            switch (nOrientation) {
		            case ExifInterface.ORIENTATION_ROTATE_180:
		                nAngle0 = 180;
		            	break;
		            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
		            	break;
		            case ExifInterface.ORIENTATION_TRANSPOSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_90:
		                nAngle0 = 90;
		            	break;
		            case ExifInterface.ORIENTATION_TRANSVERSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_270:
		                nAngle0 = 270;
		            	break;
		            }
		            
	                nAngle0 = (nAngle0 + frameinfo.getRotation(0)) % 360;
		        }
		        else { 
		        	nAngle0 = (getOrienation(frameinfo.getUrl(0)) + frameinfo.getRotation(0)) % 360;
		        }
		        
		        switch (nAngle0) {
		        case 90:
		        	strTransPose0 = "transpose=1,";
		        	break;
		        case 180:
		        	strTransPose0 = "transpose=1,transpose=1,";
		        	break;
		        case 270:
		        	strTransPose0 = "transpose=2,";
		        	break;
		        default:
		        	strTransPose0 = "";
		        	break;
		        }
		        
		        if(frameinfo.getUrl(0).indexOf(".gif") > 1) {
		            arraylist.add("-ignore_loop");
		            arraylist.add("0");
		        } 
		        else if(flag) {
		            arraylist.add("-loop");
		            arraylist.add("1");
		        }
		        
		        if(ai[0] > 0) {
		            arraylist.add("-itsoffset");
		            arraylist.add((new StringBuilder()).append((float)ai[0] / 1000F).toString());
		        }
		        
		        if (frameinfo.getDuration(0) > 0) {
		            arraylist.add("-ss");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getStartTime(0) / 1000F).toString());
		            arraylist.add("-t");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getDuration(0) / 1000F).toString());
		        }
		        
		        if(!frameinfo.getExtensions(0).equals("")) {
		            arraylist.add("-c:v");
		            arraylist.add(frameinfo.getExtensions(0));
		        }

		        if (frameinfo.getAngle(0) != 0 && frameinfo.getDuration(0) <= 0) {
			        arraylist.add("-i");
			        arraylist.add(getRotateFile(0, frameinfo));
			        strTransPose0 = "";
		        }
		        else {
			        arraylist.add("-i");
			        arraylist.add(frameinfo.getUrl(0));
		        }
		        
		        arraylist.add("-i");
		        arraylist.add(strBackground);

		        k2 = 1;
	        }
	        
	        frameCropH1 = 0;
	        frameCropW1 = 0;
	        frameCropX1 = 0;
	        frameCropY1 = 0;
	        nAngle1 = 0;
	        frameScaleHeight1 = 0;
	        frameScaleWidth1 = 0;
	        frameXPos1 = 0;
	        frameYPos1 = 0;
	        
	        if(frameTotalCount > 1) { 
		        if(frameinfo.getDuration(1) != 0 && frameinfo.videoHasAudio(1)) {
		            strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[3:a]volume=").append(frameinfo.getVideoAudioVolume(1)).append("[a1];").toString();
		            strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a1]").toString();
		            videoHasAudioCount++;
		        }
		        
		        frameScaleHeight1 = frameinfo.getFrameScaleHeight(1);
		        frameScaleWidth1 = frameinfo.getFrameScaleWidth(1);
		        frameCropX1 = frameinfo.getCropX(1);
		        frameCropY1 = frameinfo.getCropY(1);
		        frameCropH1 = frameinfo.getCropH(1);
		        frameCropW1 = frameinfo.getCropW(1);
		        frameXPos1 = frameinfo.getXpos(1);
		        frameYPos1 = frameinfo.getYpos(1);
		        
		        if(frameinfo.getDuration(1) == 0) {
		        	ExifInterface exifinterface = new ExifInterface(frameinfo.getUrl(1));
		            String strOrientation = exifinterface.getAttribute("Orientation");
		            int nOrientation = Integer.parseInt(strOrientation);
		            Log.w("Imageorientation", (new StringBuilder()).append(strOrientation).toString());
		            nAngle1 = 0;
		            
		            switch (nOrientation) {
		            case ExifInterface.ORIENTATION_ROTATE_180:
		                nAngle1 = 180;
		            	break;
		            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
		            	break;
		            case ExifInterface.ORIENTATION_TRANSPOSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_90:
		                nAngle1 = 90;
		            	break;
		            case ExifInterface.ORIENTATION_TRANSVERSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_270:
		                nAngle1 = 270;
		            	break;
		            }
		            
	                nAngle1 = (nAngle1 + frameinfo.getRotation(1)) % 360;
		        }
		        else { 
		        	nAngle1 = (getOrienation(frameinfo.getUrl(1)) + frameinfo.getRotation(1)) % 360;
		        }
		        
		        switch (nAngle1) {
		        case 90:
		            strTransPose1 = "transpose=1,";
		        	break;
		        case 180:
		            strTransPose1 = "transpose=1,transpose=1,";
		        	break;
		        case 270:
		            strTransPose1 = "transpose=2,";
		        	break;
		        default:
		        	strTransPose1 = "";
		        	break;
		        }
		
		        if(frameinfo.getUrl(1).indexOf(".gif") > 1) {
		            arraylist.add("-ignore_loop");
		            arraylist.add("0");
		        } 
		        else if(flag) {
		            arraylist.add("-loop");
		            arraylist.add("1");
		        }
		        
		        if(ai[1] > 0) {
		            arraylist.add("-itsoffset");
		            arraylist.add((new StringBuilder()).append((float)ai[1] / 1000F).toString());
		        }
		        
		        if (frameinfo.getDuration(1) > 0) {
		            arraylist.add("-ss");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getStartTime(1) / 1000F).toString());
		            arraylist.add("-t");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getDuration(1) / 1000F).toString());
		        }
		        
		        if(!frameinfo.getExtensions(1).equals("")) {
		            arraylist.add("-c:v");
		            arraylist.add(frameinfo.getExtensions(1));
		        }
		        
		        if (frameinfo.getAngle(1) != 0 && frameinfo.getDuration(1) <= 0) {
			        arraylist.add("-i");
			        arraylist.add(getRotateFile(1, frameinfo));
			        strTransPose1 = "";
		        }
		        else {
			        arraylist.add("-i");
			        arraylist.add(frameinfo.getUrl(1));
		        }

		        arraylist.add("-i");
		        arraylist.add(strBackground);

		        k2++;
	        }
	        
	        frameCropH2 = 0;
	        frameCropW2 = 0;
	        frameCropX2 = 0;
	        frameCropY2 = 0;
	        nAngle2 = 0;
	        frameScaleHeight2 = 0;
	        frameScaleWidth2 = 0;
	        frameXPos2 = 0;
	        frameYPos2 = 0;
	        
	        if(frameTotalCount > 2) { 
		        if(frameinfo.getDuration(2) != 0 && frameinfo.videoHasAudio(2)) {
		            strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[5:a]volume=").append(frameinfo.getVideoAudioVolume(2)).append("[a2];").toString();
		            strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a2]").toString();
		            videoHasAudioCount++;
		        }
		        
		        frameScaleHeight2 = frameinfo.getFrameScaleHeight(2);
		        frameScaleWidth2 = frameinfo.getFrameScaleWidth(2);
		        frameCropX2 = frameinfo.getCropX(2);
		        frameCropY2 = frameinfo.getCropY(2);
		        frameCropH2 = frameinfo.getCropH(2);
		        frameCropW2 = frameinfo.getCropW(2);
		        frameXPos2 = frameinfo.getXpos(2);
		        frameYPos2 = frameinfo.getYpos(2);
		        
		        if(frameinfo.getDuration(2) == 0) {
		        	ExifInterface exifinterface = new ExifInterface(frameinfo.getUrl(2));
		            String strOrientation = exifinterface.getAttribute("Orientation");
		            int nOrientation = Integer.parseInt(strOrientation);
		            Log.w("Imageorientation", (new StringBuilder()).append(strOrientation).toString());
		            nAngle2 = 0;
		            
		            switch (nOrientation) {
		            case ExifInterface.ORIENTATION_ROTATE_180:
		                nAngle2 = 180;
		            	break;
		            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
		            	break;
		            case ExifInterface.ORIENTATION_TRANSPOSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_90:
		                nAngle2 = 90;
		            	break;
		            case ExifInterface.ORIENTATION_TRANSVERSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_270:
		                nAngle2 = 270;
		            	break;
		            }
		            
	                nAngle2 = (nAngle2 + frameinfo.getRotation(2)) % 360;
		        }
		        else { 
		        	if (frameID == FRAMEID_EAT)
		        		nAngle2 = 0;
		        	else
		        		nAngle2 = (getOrienation(frameinfo.getUrl(2)) + frameinfo.getRotation(2)) % 360;
		        }
		        
		        switch (nAngle2) {
		        case 90:
		        	strTransPose2 = "transpose=1,";
		        	break;
		        case 180:
		        	strTransPose2 = "transpose=1,transpose=1,";
		        	break;
		        case 270:
		        	strTransPose2 = "transpose=2,";
		        	break;
		        default:
		        	strTransPose2 = "";
		        	break;
		        }
		        
		        if(frameinfo.getUrl(2).indexOf(".gif") > 1) {
		            arraylist.add("-ignore_loop");
		            arraylist.add("0");
		        } 
		        else if(flag) {
		            arraylist.add("-loop");
		            arraylist.add("1");
		        }
		        
		        if(ai[2] > 0) {
		            arraylist.add("-itsoffset");
		            arraylist.add((new StringBuilder()).append((float)ai[2] / 1000F).toString());
		        }
		        
		        if (frameinfo.getDuration(2) > 0) {
		            arraylist.add("-ss");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getStartTime(2) / 1000F).toString());
		            arraylist.add("-t");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getDuration(2) / 1000F).toString());
		        }

		        if(!frameinfo.getExtensions(2).equals("")) {
		            arraylist.add("-c:v");
		            arraylist.add(frameinfo.getExtensions(2));
		        }
		        
		        if (frameinfo.getAngle(2) != 0 && frameinfo.getDuration(2) <= 0) {
			        arraylist.add("-i");
			        arraylist.add(getRotateFile(2, frameinfo));
			        strTransPose2 = "";
		        }
		        else {
			        arraylist.add("-i");
			        arraylist.add(frameinfo.getUrl(2));
		        }

		        arraylist.add("-i");
		        arraylist.add(strBackground);

		        k2++;
	        }
	        
	        frameCropH3 = 0;
	        frameCropW3 = 0;
	        frameCropX3 = 0;
	        frameCropY3 = 0;
	        nAngle3 = 0;
	        frameScaleHeight3 = 0;
	        frameScaleWidth3 = 0;
	        frameXPos3 = 0;
	        frameYPos3 = 0;
	        
	        if(frameTotalCount > 3) { 
		        if(frameinfo.getDuration(3) != 0 && frameinfo.videoHasAudio(3)) {
		            strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[7:a]volume=").append(frameinfo.getVideoAudioVolume(3)).append("[a3];").toString();
		            strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a3]").toString();
		            videoHasAudioCount++;
		        }
		        
		        frameScaleHeight3 = frameinfo.getFrameScaleHeight(3);
		        frameScaleWidth3 = frameinfo.getFrameScaleWidth(3);
		        frameCropX3 = frameinfo.getCropX(3);
		        frameCropY3 = frameinfo.getCropY(3);
		        frameCropH3 = frameinfo.getCropH(3);
		        frameCropW3 = frameinfo.getCropW(3);
		        frameXPos3 = frameinfo.getXpos(3);
		        frameYPos3 = frameinfo.getYpos(3);
		        
		        if(frameinfo.getDuration(3) == 0) {
		        	ExifInterface exifinterface = new ExifInterface(frameinfo.getUrl(3));
		            String strOrientation = exifinterface.getAttribute("Orientation");
		            int nOrientation = Integer.parseInt(strOrientation);
		            Log.w("Imageorientation", (new StringBuilder()).append(strOrientation).toString());
		            nAngle3 = 0;
		            
		            switch (nOrientation) {
		            case ExifInterface.ORIENTATION_ROTATE_180:
		            	nAngle3 = 180;
		            	break;
		            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
		            	break;
		            case ExifInterface.ORIENTATION_TRANSPOSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_90:
		            	nAngle3 = 90;
		            	break;
		            case ExifInterface.ORIENTATION_TRANSVERSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_270:
		            	nAngle3 = 270;
		            	break;
		            }
		            
	                nAngle3 = (nAngle3 + frameinfo.getRotation(3)) % 360;
		        }
		        else { 
		        	if (frameID == FRAMEID_HAPPYBIRTHDAY)
		        		nAngle3 = 0;
		        	else
		        		nAngle3 = (getOrienation(frameinfo.getUrl(3)) + frameinfo.getRotation(3)) % 360;
		        }
		        
		        switch (nAngle3) {
		        case 90:
		        	strTransPose3 = "transpose=1,";
		        	break;
		        case 180:
		        	strTransPose3 = "transpose=1,transpose=1,";
		        	break;
		        case 270:
		        	strTransPose3 = "transpose=2,";
		        	break;
		    	default:
		    		strTransPose3 = "";
		    		break;
		        }
		
		        if(frameinfo.getUrl(3).indexOf(".gif") > 1) {
		            arraylist.add("-ignore_loop");
		            arraylist.add("0");
		        } 
		        else if(flag) {
		            arraylist.add("-loop");
		            arraylist.add("1");
		        }
		        
		        if(ai[3] > 0) {
		            arraylist.add("-itsoffset");
		            arraylist.add((new StringBuilder()).append((float)ai[3] / 1000F).toString());
		        }
		        
		        if (frameinfo.getDuration(3) > 0) {
		            arraylist.add("-ss");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getStartTime(3) / 1000F).toString());
		            arraylist.add("-t");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getDuration(3) / 1000F).toString());
		        }

		        if(!frameinfo.getExtensions(3).equals("")) {
		            arraylist.add("-c:v");
		            arraylist.add(frameinfo.getExtensions(3));
		        }
		        
		        if (frameinfo.getAngle(3) != 0 && frameinfo.getDuration(3) <= 0) {
			        arraylist.add("-i");
			        arraylist.add(getRotateFile(3, frameinfo));
			        strTransPose3 = "";
		        }
		        else {
			        arraylist.add("-i");
			        arraylist.add(frameinfo.getUrl(3));
		        }

		        arraylist.add("-i");
		        arraylist.add(strBackground);

		        k2++;
	        }
	        
	        frameCropH4 = 0;
	        frameCropW4 = 0;
	        frameCropX4 = 0;
	        frameCropY4 = 0;
	        frameScaleHeight4 = 0;
	        frameScaleWidth4 = 0;
	        frameXPos4 = 0;
	        frameYPos4 = 0;
	        
	        if(frameTotalCount > 4) { 
		        if(frameinfo.getDuration(4) != 0 && frameinfo.videoHasAudio(4)) {
		            strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[9:a]volume=").append(frameinfo.getVideoAudioVolume(4)).append("[a4];").toString();
		            strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a4]").toString();
		            videoHasAudioCount++;
		        }
		        
		        frameScaleHeight4 = frameinfo.getFrameScaleHeight(4);
		        frameScaleWidth4 = frameinfo.getFrameScaleWidth(4);
		        frameCropX4 = frameinfo.getCropX(4);
		        frameCropY4 = frameinfo.getCropY(4);
		        frameCropH4 = frameinfo.getCropH(4);
		        frameCropW4 = frameinfo.getCropW(4);
		        frameXPos4 = frameinfo.getXpos(4);
		        frameYPos4 = frameinfo.getYpos(4);
		        
		        if(frameinfo.getDuration(4) == 0) {
		        	ExifInterface exifinterface = new ExifInterface(frameinfo.getUrl(4));
		            String strOrientation = exifinterface.getAttribute("Orientation");
		            int nOrientation = Integer.parseInt(strOrientation);
		            Log.w("Imageorientation", (new StringBuilder()).append(strOrientation).toString());
		            
		            nAngle4 = 0;
		            switch (nOrientation) {
		            case ExifInterface.ORIENTATION_ROTATE_180:
		            	nAngle4 = 180;
		            	break;
		            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
		            	break;
		            case ExifInterface.ORIENTATION_TRANSPOSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_90:
		            	nAngle4 = 90;
		            	break;
		            case ExifInterface.ORIENTATION_TRANSVERSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_270:
		            	nAngle4 = 270;
		            	break;
		            }
		            
	                nAngle4 = (nAngle4 + frameinfo.getRotation(4)) % 360;
		        }
		        else { 
		        	nAngle4 = (getOrienation(frameinfo.getUrl(4)) + frameinfo.getRotation(4)) % 360;
		        }
		        
		        switch (nAngle4) {
		        case 90:
		        	strTransPose4 = "transpose=1,";
		        	break;
		        case 180:
		        	strTransPose4 = "transpose=1,transpose=1,";
		        	break;
		        case 270:
		        	strTransPose4 = "transpose=2,";
		        	break;
		        default:
		        	strTransPose4 = "";
		        	break;
		        }
		        
		        if(frameinfo.getUrl(4).indexOf(".gif") > 1) {
		            arraylist.add("-ignore_loop");
		            arraylist.add("0");
		        } 
		        else if(flag) {
		            arraylist.add("-loop");
		            arraylist.add("1");
		        }
		        
		        if(ai[4] > 0) {
		            arraylist.add("-itsoffset");
		            arraylist.add((new StringBuilder()).append((float)ai[4] / 1000F).toString());
		        }

		        if (frameinfo.getDuration(4) > 0) {
		            arraylist.add("-ss");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getStartTime(4) / 1000F).toString());
		            arraylist.add("-t");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getDuration(4) / 1000F).toString());
		        }

		        if(!frameinfo.getExtensions(4).equals("")) {
		            arraylist.add("-c:v");
		            arraylist.add(frameinfo.getExtensions(4));
		        }
		        
		        if (frameinfo.getAngle(4) != 0 && frameinfo.getDuration(4) <= 0) {
			        arraylist.add("-i");
			        arraylist.add(getRotateFile(4, frameinfo));
			        strTransPose4 = "";
		        }
		        else {
			        arraylist.add("-i");
			        arraylist.add(frameinfo.getUrl(4));
		        }

		        arraylist.add("-i");
		        arraylist.add(strBackground);

		        k2++;
	        }
	        
	        frameCropH5 = 0;
	        frameCropW5 = 0;
	        frameCropX5 = 0;
	        frameCropY5 = 0;
	        frameScaleHeight5 = 0;
	        frameScaleWidth5 = 0;
	        frameXPos5 = 0;
	        frameYPos5 = 0;
	        if(frameTotalCount > 5) { 
		        if(frameinfo.getDuration(5) != 0 && frameinfo.videoHasAudio(5)) {
		            strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[11:a]volume=").append(frameinfo.getVideoAudioVolume(5)).append("[a5];").toString();
		            strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a5]").toString();
		            videoHasAudioCount++;
		        }
		        
		        frameScaleHeight5 = frameinfo.getFrameScaleHeight(5);
		        frameScaleWidth5 = frameinfo.getFrameScaleWidth(5);
		        frameCropX5 = frameinfo.getCropX(5);
		        frameCropY5 = frameinfo.getCropY(5);
		        frameCropH5 = frameinfo.getCropH(5);
		        frameCropW5 = frameinfo.getCropW(5);
		        frameXPos5 = frameinfo.getXpos(5);
		        frameYPos5 = frameinfo.getYpos(5);
		        
		        if(frameinfo.getDuration(5) == 0) {
		        	ExifInterface exifinterface = new ExifInterface(frameinfo.getUrl(5));
		            String strOrientation = exifinterface.getAttribute("Orientation");
		            int nOrientation = Integer.parseInt(strOrientation);
		            Log.w("Imageorientation", (new StringBuilder()).append(strOrientation).toString());
		            nAngle5 = 0;
		            switch (nOrientation) {
		            case ExifInterface.ORIENTATION_ROTATE_180:
		            	nAngle5 = 180;
		            	break;
		            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
		            	break;
		            case ExifInterface.ORIENTATION_TRANSPOSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_90:
		            	nAngle5 = 90;
		            	break;
		            case ExifInterface.ORIENTATION_TRANSVERSE:
		            	break;
		            case ExifInterface.ORIENTATION_ROTATE_270:
		            	nAngle5 = 270;
		            	break;
		            }
		            
	                nAngle5 = (nAngle5 + frameinfo.getRotation(5)) % 360;
		        }
		        else { 
		        	nAngle5 = (getOrienation(frameinfo.getUrl(5)) + frameinfo.getRotation(5)) % 360;
		        }
		        
		        switch (nAngle5) {
		        case 90:
		        	strTransPose5 = "transpose=1,";
		        	break;
		        case 180:
		        	strTransPose5 = "transpose=1,transpose=1,";
		        	break;
		        case 270:
		        	strTransPose5 = "transpose=2,";
		        	break;
		        default:
		        	strTransPose5 = "";
		        	break;
		        }
		        
		        if(frameinfo.getUrl(5).indexOf(".gif") > 1) {
		            arraylist.add("-ignore_loop");
		            arraylist.add("0");
		        } 
		        else if(flag) {
		            arraylist.add("-loop");
		            arraylist.add("1");
		        }
		        
		        if(ai[5] > 0) {
		            arraylist.add("-itsoffset");
		            arraylist.add((new StringBuilder()).append((float)ai[5] / 1000F).toString());
		        }
		        
		        if (frameinfo.getDuration(5) > 0) {
		            arraylist.add("-ss");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getStartTime(5) / 1000F).toString());
		            arraylist.add("-t");
		            arraylist.add((new StringBuilder()).append((float)frameinfo.getDuration(5) / 1000F).toString());
		        }

		        if(!frameinfo.getExtensions(5).equals("")) {
		            arraylist.add("-c:v");
		            arraylist.add(frameinfo.getExtensions(5));
		        }
		        
		        if (frameinfo.getAngle(5) != 0 && frameinfo.getDuration(5) <= 0) {
			        arraylist.add("-i");
			        arraylist.add(getRotateFile(5, frameinfo));
			        strTransPose5 = "";
		        }
		        else {
			        arraylist.add("-i");
			        arraylist.add(frameinfo.getUrl(5));
		        }

		        arraylist.add("-i");
		        arraylist.add(strBackground);

		        k2++;
	        }
	        
	        if (textImagePath != null && !textImagePath.isEmpty()) {
	        	arraylist.add("-i");
	        	arraylist.add(strTextPath);
	        }

//	        if (frameID == FRAMEID_HAPPYBIRTHDAY) {
//		        arraylist.add("-i");
//		        arraylist.add("/data/data/com.dragonplayer.merge/happybirthday.avi");
//	        }
//	        else if (frameID == FRAMEID_EAT) {
//		        arraylist.add("-i");
//		        arraylist.add("/data/data/com.dragonplayer.merge/eat.avi");
//	        }

	        switch (frameTotalCount) {
	        case 1:
	            if(!s2.equals("")) {
	                if(audioStartTime != -1L && audioEndTime != -1L) {
	                    arraylist.add("-ss");
	                    arraylist.add((new StringBuilder()).append(audioStartTime).toString());
	                    arraylist.add("-t");
	                    arraylist.add((new StringBuilder()).append(audioEndTime - audioStartTime).toString());
	                }
	                
	                arraylist.add("-i");
	                arraylist.add(s2);
	                
//	                strFilterAudioVolume = strFilterAudioVolume + "[3:a]volume=" + audioVolume + "[a1];";
//	                strFilterAudioVolumeLabel = strFilterAudioVolumeLabel + "[a1]";
		            if (textImagePath != null && !textImagePath.isEmpty()) {
		            	strFilterAudioVolume = "[4:a]volume=" + audioVolume + "[a1];";
		            }
		            else {
		            	strFilterAudioVolume = "[3:a]volume=" + audioVolume + "[a1];";
		            }
	                strFilterAudioVolumeLabel = "[a1]";
	                
	                videoHasAudioCount = 1;
	                k2++;
	            }
	            
	            if(videoHasAudioCount > 0)
	                strFilterAudioVolumeLabel = strFilterAudioVolumeLabel + "amix=inputs=" + videoHasAudioCount + "[audio]";

	            strFilterForeround0 = "[2:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW0 + ":" + frameCropH0 + ":" + frameXPos0 + ":" + frameYPos0 + "[firstfg];";

	            strFilterColor0 = "color=color=" + frameinfo.getBorderColor() + ":size=" + frameinfo.getScaleWidth() + "x" + frameinfo.getScaleHeight();
	            strFilterScale0 = strTransPose0 + "scale=" + frameScaleWidth0 + ":" + frameScaleHeight0 + ",crop=" + frameCropW0 + ":" + frameCropH0 + ":" + frameCropX0 + ":" + frameCropY0;
	            strFilterOverlay0 = strFilterColor0 + "[bg];" + strFilterBackground + "[1:v]" + strFilterScale0 + "[first];" + strFilterForeround0;
	            
	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	strFilterOverlay0 = strFilterOverlay0 + "[3:v]" + strFilterText;
	            }
	            
	            strFilterOverlay0 = strFilterOverlay0 + "[bg][imgbg]overlay=0:0[bg1];" + "[bg1][first]overlay=" + frameXPos0 + ":" + frameYPos0 + "[bg2];" + "[bg2][firstfg]overlay=" + frameXPos0 + ":" + frameYPos0; 

	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	strFilterOverlay0 = strFilterOverlay0 + "[bg3];[bg3][textfg]" + strFilterTextOverlay;
	            }

	            if(videoHasAudioCount > 0)
	                strFilterOverlay0 = strFilterOverlay0 + "[video];" + strFilterAudioVolume + strFilterAudioVolumeLabel;
	            
	            arraylist.add("-filter_complex");
	            arraylist.add(strFilterOverlay0);
	            
	        	break;
	        case 2:
	            if(!s2.equals("")) {
	                if(audioStartTime != -1L && audioEndTime != -1L) {
	                    arraylist.add("-ss");
	                    arraylist.add((new StringBuilder()).append(audioStartTime).toString());
	                    arraylist.add("-t");
	                    arraylist.add((new StringBuilder()).append(audioEndTime - audioStartTime).toString());
	                }
	                
	                arraylist.add("-i");
	                arraylist.add(s2);
	                
//	                strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[5:a]volume=").append(audioVolume).append("[a2];").toString();
//	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a2]").toString();
	                if (textImagePath != null && !textImagePath.isEmpty()) {
//		                if (frameID == FRAMEID_EAT)
//		                	strFilterAudioVolume = "[7:a]volume=" + audioVolume + "[a2];";
//		                else
		                	strFilterAudioVolume = "[6:a]volume=" + audioVolume + "[a2];";
	                }
	                else {
//		                if (frameID == FRAMEID_EAT)
//		                	strFilterAudioVolume = "[6:a]volume=" + audioVolume + "[a2];";
//		                else
		                	strFilterAudioVolume = "[5:a]volume=" + audioVolume + "[a2];";
	                }
	                strFilterAudioVolumeLabel = "[a2]";
	                
	                videoHasAudioCount = 1;
	                k2++;
	            }
	            
	            if(videoHasAudioCount > 0)
	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("amix=inputs=").append(videoHasAudioCount).append("[audio]").toString();
	            
	            strFilterForeround0 = "[2:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW0 + ":" + frameCropH0 + ":" + frameXPos0 + ":" + frameYPos0 + "[firstfg];";
	            strFilterForeround1 = "[4:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW1 + ":" + frameCropH1 + ":" + frameXPos1 + ":" + frameYPos1 + "[secondfg];";

	            strFilterColor1 = "color=color=" + frameinfo.getBorderColor() + ":size=" + frameinfo.getScaleWidth() + "x" + frameinfo.getScaleHeight();
	            strFilterScale1 = strTransPose0 + "scale=" + frameScaleWidth0 + ":" + frameScaleHeight0 + ",crop=" + frameCropW0 + ":" + frameCropH0 + ":" + frameCropX0 + ":" + frameCropY0;
	            strFilterOverlay1 = strTransPose1 + "scale=" + frameScaleWidth1 + ":" + frameScaleHeight1 + ",crop=" + frameCropW1 + ":" + frameCropH1 + ":" + frameCropX1 + ":" + frameCropY1;

	            if(!flag1) {
	                strFilterOverlayBG1 = strFilterColor1 + "[bg];";
	            }
	            else { 
	            	strFilterOverlayBGTemp1 = (new StringBuilder(String.valueOf(strFilterColor1))).append("[bg0];").toString();
	            	k17 = 0;
	            	
	    	        for (l17 = 0; l17 < frameTotalCount; l17++) {
	    	            if(ai[l17] > 0) { 
	    	    	        arraylist.add("-i");
	    	    	        arraylist.add(frameinfo.getUrl(l17));
	    	    	        strFilterOverlayBGTemp1 = (new StringBuilder(String.valueOf(strFilterOverlayBGTemp1))).append("[").append(k2).append("]select='eq(n\\,1)',").toString();
	    	    	        switch (l17) {
	    	    	        case 0:
	    	    	            s70 = (new StringBuilder(String.valueOf(strFilterOverlayBGTemp1))).append(strFilterScale1).toString();
	    	    	            k17++;
	    	    	            strFilterOverlayBGTemp1 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s70))).append("[ov").append(k17).append("];").toString()))).append("[bg").append(k17 - 1).append("][ov").append(k17).append("]overlay=").append(frameXPos0).append(":").append(frameYPos0).append("[bg").append(k17).append("];").toString();
	    	    	        	break;
	    	    	        case 1:
	    	    	            s69 = (new StringBuilder(String.valueOf(strFilterOverlayBGTemp1))).append(strFilterOverlay1).toString();
	    	    	            k17++;
	    	    	            strFilterOverlayBGTemp1 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s69))).append("[ov").append(k17).append("];").toString()))).append("[bg").append(k17 - 1).append("][ov").append(k17).append("]overlay=").append(frameXPos1).append(":").append(frameYPos1).append("[bg").append(k17).append("];").toString();
	    	    	        	break;
	    	    	        }
	    	    	        
	    	    	        k2++;
	    	            }
	    	        }
	    	        	
	    	        strFilterOverlayBG1 = (new StringBuilder(String.valueOf(strFilterOverlayBGTemp1))).append("[bg").append(k17).append("]null[bg];").toString();
	            }
	            
	            s67 = strFilterOverlayBG1 + strFilterBackground + "[1:v]" + strFilterScale1 + "[first];" + strFilterForeround0 + "[3:v]" + strFilterOverlay1 + "[second];" + strFilterForeround1;
	            
	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s67 = s67 + "[5:v]" + strFilterText;

//	            	if (frameID == FRAMEID_EAT) {
//	            		s67 = s67 + "[6:v]" + "scale="+frameinfo.getScaleWidth() + ":" + (536 * frameinfo.getScaleWidth() /1096) + ",crop=" + frameinfo.getScaleWidth() + ":" + (536 * frameinfo.getScaleWidth() /1096) + ":0:0[defaultbg];";
//			        }
	            }
	            else {
//			        if (frameID == FRAMEID_EAT) {
//			        	s67 = s67 + "[5:v]" + "scale="+frameinfo.getScaleWidth() + ":" + (536 * frameinfo.getScaleWidth() /1096) + ",crop=" + frameinfo.getScaleWidth() + ":" + (536 * frameinfo.getScaleWidth() /1096) + ":0:0[defaultbg];";
//			        }
	            }
	            
	            s67 = s67 + "[bg][imgbg]overlay=0:0[bg1];" + "[bg1][first]overlay=" + frameXPos0 + ":" + frameYPos0 + "[bg2];" + "[bg2][firstfg]overlay=" + frameXPos0 + ":" + frameYPos0 + 
	            		"[bg3];[bg3][second]overlay=" + frameXPos1 + ":" + frameYPos1 + "[bg4];" + "[bg4][secondfg]overlay=" + frameXPos1 + ":" + frameYPos1; 

	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s67 = s67 + "[bg5];[bg5][textfg]" + strFilterTextOverlay;
	            	
//	            	if (frameID == FRAMEID_EAT) {
//	            		s67 = s67 + "[bg6];[bg6][defaultbg]" + "overlay=0:" + (frameinfo.getScaleHeight() * 1384 / 1920);
//	            	}
	            }
	            else {
//	            	if (frameID == FRAMEID_EAT) {
//	            		s67 = s67 + "[bg5];[bg5][defaultbg]" + "overlay=0:" + (frameinfo.getScaleHeight() * 1384 / 1920);
//	            	}
	            }

	            if(videoHasAudioCount > 0)
	                s67 = s67 + "[video];" + strFilterAudioVolume + strFilterAudioVolumeLabel;
	            
	            arraylist.add("-filter_complex");
	            arraylist.add(s67);
	        	break;
	        case 3:
	            if(!s2.equals("")) {
	                if(audioStartTime != -1L && audioEndTime != -1L) {
	                    arraylist.add("-ss");
	                    arraylist.add((new StringBuilder()).append(audioStartTime).toString());
	                    arraylist.add("-t");
	                    arraylist.add((new StringBuilder()).append(audioEndTime - audioStartTime).toString());
	                }
	                
	                arraylist.add("-i");
	                arraylist.add(s2);
	                
//	                strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[7:a]volume=").append(audioVolume).append("[a3];").toString();
//	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a3]").toString();
	                
		            if (textImagePath != null && !textImagePath.isEmpty()) {
//		                if (frameID == FRAMEID_HAPPYBIRTHDAY)
//		                	strFilterAudioVolume = "[9:a]volume=" + audioVolume + "[a3];";
//		                else 
		                	strFilterAudioVolume = "[8:a]volume=" + audioVolume + "[a3];";
		            }
		            else {
//		                if (frameID == FRAMEID_HAPPYBIRTHDAY)
//		                	strFilterAudioVolume = "[8:a]volume=" + audioVolume + "[a3];";
//		                else 
		                	strFilterAudioVolume = "[7:a]volume=" + audioVolume + "[a3];";
		            }
		            
	                strFilterAudioVolumeLabel = "[a3]";
	                videoHasAudioCount = 1;
	                k2++;
	            }
	            
	            if(videoHasAudioCount > 0)
	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("amix=inputs=").append(videoHasAudioCount).append("[audio]").toString();
	            
	            strFilterForeround0 = "[2:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW0 + ":" + frameCropH0 + ":" + frameXPos0 + ":" + frameYPos0 + "[firstfg];";
	            strFilterForeround1 = "[4:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW1 + ":" + frameCropH1 + ":" + frameXPos1 + ":" + frameYPos1 + "[secondfg];";
	            if (frameID == FRAMEID_EAT)
		            strFilterForeround2 = "";
	            else
		            strFilterForeround2 = "[6:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
		            		frameCropW2 + ":" + frameCropH2 + ":" + frameXPos2 + ":" + frameYPos2 + "[thirdfg];";

	            s53 = "color=color=" + frameinfo.getBorderColor() + ":size=" + frameinfo.getScaleWidth() + "x" + frameinfo.getScaleHeight() + "";
	            s54 = strTransPose0 + "scale=" + frameScaleWidth0 + ":" + frameScaleHeight0 + ",crop=" + frameCropW0 + ":" + frameCropH0 + ":" + frameCropX0 + ":" + frameCropY0;
	            s55 = strTransPose1 + "scale=" + frameScaleWidth1 + ":" + frameScaleHeight1 + ",crop=" + frameCropW1 + ":" + frameCropH1 + ":" + frameCropX1 + ":" + frameCropY1;
	            s56 = strTransPose2 + "scale=" + frameScaleWidth2 + ":" + frameScaleHeight2 + ",crop=" + frameCropW2 + ":" + frameCropH2 + ":" + frameCropX2 + ":" + frameCropY2;
	            
	            if(!flag1) {
	            	s57 = (new StringBuilder(String.valueOf(s53))).append("[bg];").toString();
	            }
	            else { 
	    	        s59 = (new StringBuilder(String.valueOf(s53))).append("[bg0];").toString();
	    	        i17 = 0;
	    	
	    	        for (j17 = 0; j17 < frameTotalCount; j17++) {
	    	            if(ai[j17] > 0) { 
	    	    	        arraylist.add("-i");
	    	    	        arraylist.add(frameinfo.getUrl(j17));
	    	    	        s59 = (new StringBuilder(String.valueOf(s59))).append("[").append(k2).append("]select='eq(n\\,1)',").toString();

	    	    	        switch (j17) {
	    	    	        case 0:
	    	    	            s62 = (new StringBuilder(String.valueOf(s59))).append(s54).toString();
	    	    	            i17++;
	    	    	            s59 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s62))).append("[ov").append(i17).append("];").toString()))).append("[bg").append(i17 - 1).append("][ov").append(i17).append("]overlay=").append(frameXPos0).append(":").append(frameYPos0).append("[bg").append(i17).append("];").toString();
	    	    	        	break;
	    	    	        case 1:
	    	    	            s61 = (new StringBuilder(String.valueOf(s59))).append(s55).toString();
	    	    	            i17++;
	    	    	            s59 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s61))).append("[ov").append(i17).append("];").toString()))).append("[bg").append(i17 - 1).append("][ov").append(i17).append("]overlay=").append(frameXPos1).append(":").append(frameYPos1).append("[bg").append(i17).append("];").toString();
	    	    	        	break;
	    	    	        case 2:
	    	    	            s60 = (new StringBuilder(String.valueOf(s59))).append(s56).toString();
	    	    	            i17++;
	    	    	            s59 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s60))).append("[ov").append(i17).append("];").toString()))).append("[bg").append(i17 - 1).append("][ov").append(i17).append("]overlay=").append(frameXPos2).append(":").append(frameYPos2).append("[bg").append(i17).append("];").toString();
	    	    	        	break;
	    	    	        }
	    	    	        
	    	    	        k2++;
	    	            }
	    	        }
	    	
	    	        s57 = (new StringBuilder(String.valueOf(s59))).append("[bg").append(i17).append("]null[bg];").toString();
	            }
	            
	            s58 = s57 + strFilterBackground + "[1:v]" + s54 + "[first];" + strFilterForeround0 + "[3:v]" + s55 + "[second];" + strFilterForeround1 + 
	            		"[5:v]" + s56 + "[third];" + strFilterForeround2; 
	            
	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s58 = s58 + "[7:v]" + strFilterText;
	            	
//	            	if (frameID == FRAMEID_HAPPYBIRTHDAY) {
//	            		s58 = s58 + "[8:v]" + "scale="+frameinfo.getScaleWidth() + ":" + (536 * frameinfo.getScaleWidth() /1096) + ",crop=" + frameinfo.getScaleWidth() + ":" + (536 * frameinfo.getScaleWidth() /1096) + ":0:0[defaultbg];";
//			        }
	            }
	            else {
//			        if (frameID == FRAMEID_HAPPYBIRTHDAY) {
//			        	s58 = s58 + "[7:v]" + "scale="+frameinfo.getScaleWidth() + ":" + (536 * frameinfo.getScaleWidth() /1096) + ",crop=" + frameinfo.getScaleWidth() + ":" + (536 * frameinfo.getScaleWidth() /1096) + ":0:0[defaultbg];";
//			        }
	            }

	            if (frameID == FRAMEID_EAT)
		            s58 = s58 + "[bg][imgbg]overlay=0:0[bg1];" + "[bg1][first]overlay=" + frameXPos0 + ":" + frameYPos0 + "[bg2];" + "[bg2][firstfg]overlay=" + frameXPos0 + ":" + frameYPos0 + 
		            		"[bg3];[bg3][second]overlay=" + frameXPos1 + ":" + frameYPos1 + "[bg4];" + "[bg4][secondfg]overlay=" + frameXPos1 + ":" + frameYPos1 + 
		            		"[bg5];" + "[bg5][third]overlay=" + frameXPos2 + ":" + frameYPos2;
	            else
		            s58 = s58 + "[bg][imgbg]overlay=0:0[bg1];" + "[bg1][first]overlay=" + frameXPos0 + ":" + frameYPos0 + "[bg2];" + "[bg2][firstfg]overlay=" + frameXPos0 + ":" + frameYPos0 + 
		            		"[bg3];[bg3][second]overlay=" + frameXPos1 + ":" + frameYPos1 + "[bg4];" + "[bg4][secondfg]overlay=" + frameXPos1 + ":" + frameYPos1 + 
		            		"[bg5];" + "[bg5][third]overlay=" + frameXPos2 + ":" + frameYPos2 + "[bg6];" + "[bg6][thirdfg]overlay=" + frameXPos2 + ":" + frameYPos2;


	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s58 = s58 + "[bg7];[bg7][textfg]" + strFilterTextOverlay;
	            	
//	            	if (frameID == FRAMEID_HAPPYBIRTHDAY) {
//	            		s58 = s58 + "[bg8];[bg8][defaultbg]" + "overlay=0:" + (frameinfo.getScaleHeight() * 0 / 1920);
//	            	}
	            }
	            else {
//	            	if (frameID == FRAMEID_HAPPYBIRTHDAY) {
//	            		s58 = s58 + "[bg7];[bg7][defaultbg]" + "overlay=0:" + (frameinfo.getScaleHeight() * 0 / 1920);
//	            	}
	            }
	            
	            if(videoHasAudioCount > 0)
	                s58 = (new StringBuilder(String.valueOf(s58))).append("[video];").append(strFilterAudioVolume).append(strFilterAudioVolumeLabel).toString();
	            
	            arraylist.add("-filter_complex");
	            arraylist.add(s58);
	        	break;
	        case 4:
	            if(!s2.equals("")) {
	                if(audioStartTime != -1L && audioEndTime != -1L) {
	                    arraylist.add("-ss");
	                    arraylist.add((new StringBuilder()).append(audioStartTime).toString());
	                    arraylist.add("-t");
	                    arraylist.add((new StringBuilder()).append(audioEndTime - audioStartTime).toString());
	                }
	                
	                arraylist.add("-i");
	                arraylist.add(s2);
	                
//	                strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[9:a]volume=").append(audioVolume).append("[a4];").toString();
//	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a4]").toString();
	                if (textImagePath != null && !textImagePath.isEmpty()) 
	                	strFilterAudioVolume = "[10:a]volume=" + audioVolume + "[a4];";
	                else
	                	strFilterAudioVolume = "[9:a]volume=" + audioVolume + "[a4];";
	                
	                strFilterAudioVolumeLabel = "[a4]";

	                videoHasAudioCount = 1;
	                k2++;
	            }
	            
	            if(videoHasAudioCount > 0)
	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("amix=inputs=").append(videoHasAudioCount).append("[audio]").toString();
	            
	            strFilterForeround0 = "[2:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW0 + ":" + frameCropH0 + ":" + frameXPos0 + ":" + frameYPos0 + "[firstfg];";
	            strFilterForeround1 = "[4:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW1 + ":" + frameCropH1 + ":" + frameXPos1 + ":" + frameYPos1 + "[secondfg];";
	            strFilterForeround2 = "[6:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW2 + ":" + frameCropH2 + ":" + frameXPos2 + ":" + frameYPos2 + "[thirdfg];";
	            if (frameID == FRAMEID_HAPPYBIRTHDAY)
		            strFilterForeround3 = "";
	            else
		            strFilterForeround3 = "[8:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
		            		frameCropW3 + ":" + frameCropH3 + ":" + frameXPos3 + ":" + frameYPos3 + "[fourthfg];";

	            s41 = "color=color=" + frameinfo.getBorderColor() + ":size=" + frameinfo.getScaleWidth() + "x" + frameinfo.getScaleHeight();
	            s42 = strTransPose0 + "scale=" + frameScaleWidth0 + ":" + frameScaleHeight0 + ",crop=" + frameCropW0 + ":" + frameCropH0 + ":" + frameCropX0 + ":" + frameCropY0;
	            s43 = strTransPose1 + "scale=" + frameScaleWidth1 + ":" + frameScaleHeight1 + ",crop=" + frameCropW1 + ":" + frameCropH1 + ":" + frameCropX1 + ":" + frameCropY1;
	            s44 = strTransPose2 + "scale=" + frameScaleWidth2 + ":" + frameScaleHeight2 + ",crop=" + frameCropW2 + ":" + frameCropH2 + ":" + frameCropX2 + ":" + frameCropY2;
	            s45 = strTransPose3 + "scale=" + frameScaleWidth3 + ":" + frameScaleHeight3 + ",crop=" + frameCropW3 + ":" + frameCropH3 + ":" + frameCropX3 + ":" + frameCropY3;
	            
	            if(!flag1) {
	            	s46 = (new StringBuilder(String.valueOf(s41))).append("[bg];").toString();
	            }
	            else { 
	    	        s48 = (new StringBuilder(String.valueOf(s41))).append("[bg0];").toString();
	    	        k16 = 0;
	    	        
	    	        for (l16 = 0; l16 < frameTotalCount; l16++) {
	    	            if(ai[l16] > 0) {
	    	    	        arraylist.add("-i");
	    	    	        arraylist.add(frameinfo.getUrl(l16));
	    	    	
	    	    	        s48 = (new StringBuilder(String.valueOf(s48))).append("[").append(k2).append("]select='eq(n\\,1)',").toString();
	    	    	        
	    	    	        switch (l16) {
	    	    	        case 0:
	    	    	            s52 = (new StringBuilder(String.valueOf(s48))).append(s42).toString();
	    	    	            k16++;
	    	    	            s48 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s52))).append("[ov").append(k16).append("];").toString()))).append("[bg").append(k16 - 1).append("][ov").append(k16).append("]overlay=").append(frameXPos0).append(":").append(frameYPos0).append("[bg").append(k16).append("];").toString();
	    	    	        	break;
	    	    	        case 1:
	    	    	            s51 = (new StringBuilder(String.valueOf(s48))).append(s43).toString();
	    	    	            k16++;
	    	    	            s48 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s51))).append("[ov").append(k16).append("];").toString()))).append("[bg").append(k16 - 1).append("][ov").append(k16).append("]overlay=").append(frameXPos1).append(":").append(frameYPos1).append("[bg").append(k16).append("];").toString();
	    	    	        	break;
	    	    	        case 2:
	    	    	            s50 = (new StringBuilder(String.valueOf(s48))).append(s44).toString();
	    	    	            k16++;
	    	    	            s48 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s50))).append("[ov").append(k16).append("];").toString()))).append("[bg").append(k16 - 1).append("][ov").append(k16).append("]overlay=").append(frameXPos2).append(":").append(frameYPos2).append("[bg").append(k16).append("];").toString();
	    	    	        	break;
	    	    	        case 3:
	    	    	            s49 = (new StringBuilder(String.valueOf(s48))).append(s45).toString();
	    	    	            k16++;
	    	    	            s48 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s49))).append("[ov").append(k16).append("];").toString()))).append("[bg").append(k16 - 1).append("][ov").append(k16).append("]overlay=").append(frameXPos3).append(":").append(frameYPos3).append("[bg").append(k16).append("];").toString();
	    	    	        	break;
	    	    	        }
	    	    	        
	    	    	        k2++;
	    	            }
	    	        }
	    	        
	    	        s46 = (new StringBuilder(String.valueOf(s48))).append("[bg").append(k16).append("]null[bg];").toString();
	            }
	            
	            s47 = s46 + strFilterBackground + "[1:v]" + s42 + "[first];" + strFilterForeround0 + "[3:v]" + s43 + "[second];" + strFilterForeround1 + 
	            		"[5:v]" + s44 + "[third];" + strFilterForeround2 + "[7:v]" + s45 + "[fourth];" + strFilterForeround3; 
	            
	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s47 = s47 + "[9:v]" + strFilterText;
	            }

	            if (frameID == FRAMEID_HAPPYBIRTHDAY)
		            s47 = s47 + "[bg][imgbg]overlay=0:0[bg1];" + "[bg1][first]overlay=" + frameXPos0 + ":" + frameYPos0 + "[bg2];" + "[bg2][firstfg]overlay=" + frameXPos0 + ":" + frameYPos0 + 
		            		"[bg3];[bg3][second]overlay=" + frameXPos1 + ":" + frameYPos1 + "[bg4];" + "[bg4][secondfg]overlay=" + frameXPos1 + ":" + frameYPos1 + 
		            		"[bg5];[bg5][third]overlay=" + frameXPos2 + ":" + frameYPos2 + "[bg6];" + "[bg6][thirdfg]overlay=" + frameXPos2 + ":" + frameYPos2 + 
		            		"[bg7];[bg7][fourth]overlay=" + frameXPos3 + ":" + frameYPos3;
	            else
		            s47 = s47 + "[bg][imgbg]overlay=0:0[bg1];" + "[bg1][first]overlay=" + frameXPos0 + ":" + frameYPos0 + "[bg2];" + "[bg2][firstfg]overlay=" + frameXPos0 + ":" + frameYPos0 + 
            		"[bg3];[bg3][second]overlay=" + frameXPos1 + ":" + frameYPos1 + "[bg4];" + "[bg4][secondfg]overlay=" + frameXPos1 + ":" + frameYPos1 + 
            		"[bg5];[bg5][third]overlay=" + frameXPos2 + ":" + frameYPos2 + "[bg6];" + "[bg6][thirdfg]overlay=" + frameXPos2 + ":" + frameYPos2 + 
            		"[bg7];[bg7][fourth]overlay=" + frameXPos3 + ":" + frameYPos3 + "[bg8];" + "[bg8][fourthfg]overlay=" + frameXPos3 + ":" + frameYPos3;


	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s47 = s47 + "[bg9];[bg9][textfg]" + strFilterTextOverlay;
	            }

	            if(videoHasAudioCount > 0)
	                s47 = (new StringBuilder(String.valueOf(s47))).append("[video];").append(strFilterAudioVolume).append(strFilterAudioVolumeLabel).toString();
	            
	            arraylist.add("-filter_complex");
	            arraylist.add(s47);
	        	break;
	        case 5:
	            if(!s2.equals("")) {
	            	
	                if(audioStartTime != -1L && audioEndTime != -1L) {
	                    arraylist.add("-ss");
	                    arraylist.add((new StringBuilder()).append(audioStartTime).toString());
	                    arraylist.add("-t");
	                    arraylist.add((new StringBuilder()).append(audioEndTime - audioStartTime).toString());
	                }
	                
	                arraylist.add("-i");
	                arraylist.add(s2);
//	                strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[11:a]volume=").append(audioVolume).append("[a5];").toString();
//	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a5]").toString();
	                if (textImagePath != null && !textImagePath.isEmpty()) 
	                	strFilterAudioVolume = "[12:a]volume=" + audioVolume + "[a5];";
	                else
	                	strFilterAudioVolume = "[11:a]volume=" + audioVolume + "[a5];";
	                strFilterAudioVolumeLabel = "[a5]";

	                videoHasAudioCount = 1;
	                k2++;
	            }
	            
	            if(videoHasAudioCount > 0)
	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("amix=inputs=").append(videoHasAudioCount).append("[audio]").toString();
	            
	            strFilterForeround0 = "[2:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW0 + ":" + frameCropH0 + ":" + frameXPos0 + ":" + frameYPos0 + "[firstfg];";
	            strFilterForeround1 = "[4:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW1 + ":" + frameCropH1 + ":" + frameXPos1 + ":" + frameYPos1 + "[secondfg];";
	            strFilterForeround2 = "[6:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW2 + ":" + frameCropH2 + ":" + frameXPos2 + ":" + frameYPos2 + "[thirdfg];";
	            strFilterForeround3 = "[8:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW3 + ":" + frameCropH3 + ":" + frameXPos3 + ":" + frameYPos3 + "[fourthfg];";
	            strFilterForeround4 = "[10:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW4 + ":" + frameCropH4 + ":" + frameXPos4 + ":" + frameYPos4 + "[fifthfg];";

	            s27 = (new StringBuilder(String.valueOf(""))).append("color=color=").append(frameinfo.getBorderColor()).append(":size=").append(frameinfo.getScaleWidth()).append("x").append(frameinfo.getScaleHeight()).append("").toString();
	            s28 = (new StringBuilder(String.valueOf(strTransPose0))).append("scale=").append(frameScaleWidth0).append(":").append(frameScaleHeight0).append(",crop=").append(frameCropW0).append(":").append(frameCropH0).append(":").append(frameCropX0).append(":").append(frameCropY0).toString();
	            s29 = (new StringBuilder(String.valueOf(strTransPose1))).append("scale=").append(frameScaleWidth1).append(":").append(frameScaleHeight1).append(",crop=").append(frameCropW1).append(":").append(frameCropH1).append(":").append(frameCropX1).append(":").append(frameCropY1).toString();
	            s30 = (new StringBuilder(String.valueOf(strTransPose2))).append("scale=").append(frameScaleWidth2).append(":").append(frameScaleHeight2).append(",crop=").append(frameCropW2).append(":").append(frameCropH2).append(":").append(frameCropX2).append(":").append(frameCropY2).toString();
	            s31 = (new StringBuilder(String.valueOf(strTransPose3))).append("scale=").append(frameScaleWidth3).append(":").append(frameScaleHeight3).append(",crop=").append(frameCropW3).append(":").append(frameCropH3).append(":").append(frameCropX3).append(":").append(frameCropY3).toString();
	            s32 = (new StringBuilder(String.valueOf(strTransPose4))).append("scale=").append(frameScaleWidth4).append(":").append(frameScaleHeight4).append(",crop=").append(frameCropW4).append(":").append(frameCropH4).append(":").append(frameCropX4).append(":").append(frameCropY4).toString();
	            
	            if(!flag1) {
	                s33 = (new StringBuilder(String.valueOf(s27))).append("[bg];").toString();
	            }
	            else { 
	    	        s35 = (new StringBuilder(String.valueOf(s27))).append("[bg0];").toString();
	    	        i16 = 0;
	    	        for (j16 = 0; j16 < frameTotalCount; j16++) {
	    	            if(ai[j16] > 0) {
	    	    	        arraylist.add("-i");
	    	    	        arraylist.add(frameinfo.getUrl(j16));
	    	    	        s35 = (new StringBuilder(String.valueOf(s35))).append("[").append(k2).append("]select='eq(n\\,1)',").toString();
	    	    	        switch (j16) {
	    	    	        case 0:
	    	    	            s40 = (new StringBuilder(String.valueOf(s35))).append(s28).toString();
	    	    	            i16++;
	    	    	            s35 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s40))).append("[ov").append(i16).append("];").toString()))).append("[bg").append(i16 - 1).append("][ov").append(i16).append("]overlay=").append(frameXPos0).append(":").append(frameYPos0).append("[bg").append(i16).append("];").toString();
	    	    	        	break;
	    	    	        case 1:
	    	    	            s39 = (new StringBuilder(String.valueOf(s35))).append(s29).toString();
	    	    	            i16++;
	    	    	            s35 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s39))).append("[ov").append(i16).append("];").toString()))).append("[bg").append(i16 - 1).append("][ov").append(i16).append("]overlay=").append(frameXPos1).append(":").append(frameYPos1).append("[bg").append(i16).append("];").toString();
	    	    	        	break;
	    	    	        case 2:
	    	    	            s38 = (new StringBuilder(String.valueOf(s35))).append(s30).toString();
	    	    	            i16++;
	    	    	            s35 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s38))).append("[ov").append(i16).append("];").toString()))).append("[bg").append(i16 - 1).append("][ov").append(i16).append("]overlay=").append(frameXPos2).append(":").append(frameYPos2).append("[bg").append(i16).append("];").toString();
	    	    	        	break;
	    	    	        case 3:
	    	    	            s37 = (new StringBuilder(String.valueOf(s35))).append(s31).toString();
	    	    	            i16++;
	    	    	            s35 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s37))).append("[ov").append(i16).append("];").toString()))).append("[bg").append(i16 - 1).append("][ov").append(i16).append("]overlay=").append(frameXPos3).append(":").append(frameYPos3).append("[bg").append(i16).append("];").toString();
	    	    	        	break;
	    	    	        case 4:
	    	    	            s36 = (new StringBuilder(String.valueOf(s35))).append(s32).toString();
	    	    	            i16++;
	    	    	            s35 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s36))).append("[ov").append(i16).append("];").toString()))).append("[bg").append(i16 - 1).append("][ov").append(i16).append("]overlay=").append(frameXPos4).append(":").append(frameYPos4).append("[bg").append(i16).append("];").toString();
	    	    	        	break;
	    	    	        }
	    	    	        
	    	    	        k2++;
	    	            }
	    	        }
	    	        
	    	        s33 = (new StringBuilder(String.valueOf(s35))).append("[bg").append(i16).append("]null[bg];").toString();
	            }

	            s34 = s33 + strFilterBackground + "[1:v]" + s28 + "[first];" + strFilterForeround0 + "[3:v]" + s29 + "[second];" + strFilterForeround1 + 
	            		"[5:v]" + s30 + "[third];" + strFilterForeround2 + "[7:v]" + s31 + "[fourth];" + strFilterForeround3 + 
	            		"[9:v]" + s32 + "[fifth];" + strFilterForeround4; 
	            
	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s34 = s34 + "[11:v]" + strFilterText;
	            }

	            s34 = s34 + "[bg][imgbg]overlay=0:0[bg1];" + "[bg1][first]overlay=" + frameXPos0 + ":" + frameYPos0 + "[bg2];" + "[bg2][firstfg]overlay=" + frameXPos0 + ":" + frameYPos0 + 
	            		"[bg3];[bg3][second]overlay=" + frameXPos1 + ":" + frameYPos1 + "[bg4];" + "[bg4][secondfg]overlay=" + frameXPos1 + ":" + frameYPos1 + 
	            		"[bg5];[bg5][third]overlay=" + frameXPos2 + ":" + frameYPos2 + "[bg6];" + "[bg6][thirdfg]overlay=" + frameXPos2 + ":" + frameYPos2 + 
	            		"[bg7];[bg7][fourth]overlay=" + frameXPos3 + ":" + frameYPos3 + "[bg8];" + "[bg8][fourthfg]overlay=" + frameXPos3 + ":" + frameYPos3 + 
	            		"[bg9];[bg9][fifth]overlay=" + frameXPos4 + ":" + frameYPos4 + "[bg10];" + "[bg10][fifthfg]overlay=" + frameXPos4 + ":" + frameYPos4;

	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s34 = s34 + "[bg11];[bg11][textfg]" + strFilterTextOverlay;
	            }
	            
	            if(videoHasAudioCount > 0)
	                s34 = (new StringBuilder(String.valueOf(s34))).append("[video];").append(strFilterAudioVolume).append(strFilterAudioVolumeLabel).toString();
	            
	            arraylist.add("-filter_complex");
	            arraylist.add(s34);
	        	break;
	        case 6:
	            if(!s2.equals("")) {
	                if(audioStartTime != -1L && audioEndTime != -1L) {
	                    arraylist.add("-ss");
	                    arraylist.add((new StringBuilder()).append(audioStartTime).toString());
	                    arraylist.add("-t");
	                    arraylist.add((new StringBuilder()).append(audioEndTime - audioStartTime).toString());
	                }
	                
	                arraylist.add("-i");
	                arraylist.add(s2);
	                
//	                strFilterAudioVolume = (new StringBuilder(String.valueOf(strFilterAudioVolume))).append("[13:a]volume=").append(audioVolume).append("[a6];").toString();
//	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("[a6]").toString();
	                if (textImagePath != null && !textImagePath.isEmpty())
	                	strFilterAudioVolume = "[14:a]volume=" + audioVolume + "[a6];";
	                else
	                	strFilterAudioVolume = "[13:a]volume=" + audioVolume + "[a6];";
	                
	                strFilterAudioVolumeLabel = "[a6]";

	                videoHasAudioCount = 1;
	                k2++;
	            }
	            
	            if(videoHasAudioCount > 0)
	                strFilterAudioVolumeLabel = (new StringBuilder(String.valueOf(strFilterAudioVolumeLabel))).append("amix=inputs=").append(videoHasAudioCount).append("[audio]").toString();
	            
	            strFilterForeround0 = "[2:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW0 + ":" + frameCropH0 + ":" + frameXPos0 + ":" + frameYPos0 + "[firstfg];";
	            strFilterForeround1 = "[4:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW1 + ":" + frameCropH1 + ":" + frameXPos1 + ":" + frameYPos1 + "[secondfg];";
	            strFilterForeround2 = "[6:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW2 + ":" + frameCropH2 + ":" + frameXPos2 + ":" + frameYPos2 + "[thirdfg];";
	            strFilterForeround3 = "[8:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW3 + ":" + frameCropH3 + ":" + frameXPos3 + ":" + frameYPos3 + "[fourthfg];";
	            strFilterForeround4 = "[10:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW4 + ":" + frameCropH4 + ":" + frameXPos4 + ":" + frameYPos4 + "[fifthfg];";
	            strFilterForeround5 = "[12:v]scale=" + frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight() + ",crop=" + 
	            		frameCropW5 + ":" + frameCropH5 + ":" + frameXPos5 + ":" + frameYPos5 + "[sixthfg];";

	            s9 = (new StringBuilder(String.valueOf(""))).append("color=color=").append(frameinfo.getBorderColor()).append(":size=").append(frameinfo.getScaleWidth()).append("x").append(frameinfo.getScaleHeight()).append("").toString();
	            s10 = (new StringBuilder(String.valueOf(strTransPose0))).append("scale=").append(frameScaleWidth0).append(":").append(frameScaleHeight0).append(",crop=").append(frameCropW0).append(":").append(frameCropH0).append(":").append(frameCropX0).append(":").append(frameCropY0).toString();
	            s11 = (new StringBuilder(String.valueOf(strTransPose1))).append("scale=").append(frameScaleWidth1).append(":").append(frameScaleHeight1).append(",crop=").append(frameCropW1).append(":").append(frameCropH1).append(":").append(frameCropX1).append(":").append(frameCropY1).toString();
	            s12 = (new StringBuilder(String.valueOf(strTransPose2))).append("scale=").append(frameScaleWidth2).append(":").append(frameScaleHeight2).append(",crop=").append(frameCropW2).append(":").append(frameCropH2).append(":").append(frameCropX2).append(":").append(frameCropY2).toString();
	            s13 = (new StringBuilder(String.valueOf(strTransPose3))).append("scale=").append(frameScaleWidth3).append(":").append(frameScaleHeight3).append(",crop=").append(frameCropW3).append(":").append(frameCropH3).append(":").append(frameCropX3).append(":").append(frameCropY3).toString();
	            s14 = (new StringBuilder(String.valueOf(strTransPose4))).append("scale=").append(frameScaleWidth4).append(":").append(frameScaleHeight4).append(",crop=").append(frameCropW4).append(":").append(frameCropH4).append(":").append(frameCropX4).append(":").append(frameCropY4).toString();
	            s15 = (new StringBuilder(String.valueOf(strTransPose5))).append("scale=").append(frameScaleWidth5).append(":").append(frameScaleHeight5).append(",crop=").append(frameCropW5).append(":").append(frameCropH5).append(":").append(frameCropX5).append(":").append(frameCropY5).toString();
	            
	            if(!flag1) {
	                s16 = (new StringBuilder(String.valueOf(s9))).append("[bg];").toString();
	            }
	            else { 
	    	        s20 = (new StringBuilder(String.valueOf(s9))).append("[bg0];").toString();
	    	        k15 = 0;
	    	        
	    	        for (l15 = 0; l15 < frameTotalCount; l15++) {
	    	            if(ai[l15] > 0) {
	    	    	        arraylist.add("-i");
	    	    	        arraylist.add(frameinfo.getUrl(l15));
	    	    	        s20 = (new StringBuilder(String.valueOf(s20))).append("[").append(k2).append("]select='eq(n\\,1)',").toString();
	    	    	        switch (l15) {
	    	    	        case 0:
	    	    	            s26 = (new StringBuilder(String.valueOf(s20))).append(s10).toString();
	    	    	            k15++;
	    	    	            s20 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s26))).append("[ov").append(k15).append("];").toString()))).append("[bg").append(k15 - 1).append("][ov").append(k15).append("]overlay=").append(frameXPos0).append(":").append(frameYPos0).append("[bg").append(k15).append("];").toString();
	    	    	        	break;
	    	    	        case 1:
	    	    	            s25 = (new StringBuilder(String.valueOf(s20))).append(s11).toString();
	    	    	            k15++;
	    	    	            s20 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s25))).append("[ov").append(k15).append("];").toString()))).append("[bg").append(k15 - 1).append("][ov").append(k15).append("]overlay=").append(frameXPos1).append(":").append(frameYPos1).append("[bg").append(k15).append("];").toString();
	    	    	        	break;
	    	    	        case 2:
	    	    	            s24 = (new StringBuilder(String.valueOf(s20))).append(s12).toString();
	    	    	            k15++;
	    	    	            s20 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s24))).append("[ov").append(k15).append("];").toString()))).append("[bg").append(k15 - 1).append("][ov").append(k15).append("]overlay=").append(frameXPos2).append(":").append(frameYPos2).append("[bg").append(k15).append("];").toString();
	    	    	        	break;
	    	    	        case 3:
	    	    	            s23 = (new StringBuilder(String.valueOf(s20))).append(s13).toString();
	    	    	            k15++;
	    	    	            s20 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s23))).append("[ov").append(k15).append("];").toString()))).append("[bg").append(k15 - 1).append("][ov").append(k15).append("]overlay=").append(frameXPos3).append(":").append(frameYPos3).append("[bg").append(k15).append("];").toString();
	    	    	        	break;
	    	    	        case 4:
	    	    	            s22 = (new StringBuilder(String.valueOf(s20))).append(s14).toString();
	    	    	            k15++;
	    	    	            s20 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s22))).append("[ov").append(k15).append("];").toString()))).append("[bg").append(k15 - 1).append("][ov").append(k15).append("]overlay=").append(frameXPos4).append(":").append(frameYPos4).append("[bg").append(k15).append("];").toString();
	    	    	        	break;
	    	    	        case 5:
	    	    	            s21 = (new StringBuilder(String.valueOf(s20))).append(s15).toString();
	    	    	            k15++;
	    	    	            s20 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s21))).append("[ov").append(k15).append("];").toString()))).append("[bg").append(k15 - 1).append("][ov").append(k15).append("]overlay=").append(frameXPos5).append(":").append(frameYPos5).append("[bg").append(k15).append("];").toString();
	    	    	        	break;
	    	    	        }
	    	    	        
	    	    	        k2++;
	    	            }
	    	        }
	    	        
	    	        s16 = (new StringBuilder(String.valueOf(s20))).append("[bg").append(k15).append("]null[bg];").toString();
	            }
	            
	            s17 = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf((new StringBuilder(String.valueOf((new StringBuilder(String.valueOf((new StringBuilder(String.valueOf((new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s16))).append("[0:v]").append(s10).append("[first];").toString()))).append("[1:v]").append(s11).append("[second];").toString()))).append("[2:v]").append(s12).append("[third];").toString()))).append("[3:v]").append(s13).append("[fourth];").toString()))).append("[4:v]").append(s14).append("[fifth];").toString()))).append("[5:v]").append(s15).append("[sixth];").toString()))).append("[bg][first]overlay=").append(frameXPos0).append(":").append(frameYPos0).append("[bg1];").append("[bg1][second]overlay=").append(frameXPos1).append(":").append(frameYPos1).append("[bg2];").append("[bg2][third]overlay=").append(frameXPos2).append(":").append(frameYPos2).append("[bg3];").append("[bg3][fourth]overlay=").append(frameXPos3).append(":").append(frameYPos3).append("[bg4];").append("[bg4][fifth]overlay=").append(frameXPos4).append(":").append(frameYPos4).append("[bg5];").append("[bg5][sixth]overlay=").append(frameXPos5).append(":").append(frameYPos5).toString();

	            s17 = s16 + strFilterBackground + "[1:v]" + s10 + "[first];" + strFilterForeround0 + "[3:v]" + s11 + "[second];" + strFilterForeround1 + 
	            		"[5:v]" + s12 + "[third];" + strFilterForeround2 + "[7:v]" + s13 + "[fourth];" + strFilterForeround3 + 
	            		"[9:v]" + s14 + "[fifth];" + strFilterForeround4 + "[11:v]" + s15 + "[sixth];" + strFilterForeround5; 
	            
	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s17 = s17 + "[13:v]" + strFilterText;
	            }

	            s17 = s17 + "[bg][imgbg]overlay=0:0[bg1];" + "[bg1][first]overlay=" + frameXPos0 + ":" + frameYPos0 + "[bg2];" + "[bg2][firstfg]overlay=" + frameXPos0 + ":" + frameYPos0 + 
	            		"[bg3];[bg3][second]overlay=" + frameXPos1 + ":" + frameYPos1 + "[bg4];" + "[bg4][secondfg]overlay=" + frameXPos1 + ":" + frameYPos1 + 
	            		"[bg5];[bg5][third]overlay=" + frameXPos2 + ":" + frameYPos2 + "[bg6];" + "[bg6][thirdfg]overlay=" + frameXPos2 + ":" + frameYPos2 + 
	            		"[bg7];[bg7][fourth]overlay=" + frameXPos3 + ":" + frameYPos3 + "[bg8];" + "[bg8][fourthfg]overlay=" + frameXPos3 + ":" + frameYPos3 + 
	            		"[bg9];[bg9][fifth]overlay=" + frameXPos4 + ":" + frameYPos4 + "[bg10];" + "[bg10][fifthfg]overlay=" + frameXPos4 + ":" + frameYPos4 + 
	            		"[bg11];[bg11][sixth]overlay=" + frameXPos5 + ":" + frameYPos5 + "[bg12];" + "[bg12][sixthfg]overlay=" + frameXPos5 + ":" + frameYPos5;

	            if (textImagePath != null && !textImagePath.isEmpty()) {
	            	s17 = s17 + "[bg13];[bg13][textfg]" + strFilterTextOverlay;
	            }

	            if(videoHasAudioCount > 0)
	                s17 = (new StringBuilder(String.valueOf(s17))).append("[video];").append(strFilterAudioVolume).append(strFilterAudioVolumeLabel).toString();
	            
	            arraylist.add("-filter_complex");
	            arraylist.add(s17);
	        	break;
	        }
	        
	        arraylist.add("-async");
	        arraylist.add("1");
	        
	        if(videoHasAudioCount > 0) {
	            arraylist.add("-map");
	            arraylist.add("[video]");
	            arraylist.add("-map");
	            arraylist.add("[audio]");
	            arraylist.add("-acodec");
	            arraylist.add("aac");
	            arraylist.add("-strict");
	            arraylist.add("experimental");
	            arraylist.add("-ar");
	            arraylist.add("44100");
	            arraylist.add("-ac");
	            arraylist.add("2");
	            arraylist.add("-ab");
	            arraylist.add("192k");
	        }
	        
	        arraylist.add("-vcodec");
	        arraylist.add("libx264");
	        arraylist.add("-preset");
	        arraylist.add("ultrafast");
	        arraylist.add("-aspect");
	        arraylist.add(frameinfo.getScaleWidth() + ":" + frameinfo.getScaleHeight());
	        arraylist.add("-t");
	        arraylist.add((new StringBuilder()).append((float)nDuration / 1000F).toString());
	        arraylist.add("-y");
	        arraylist.add(frameinfo.getVideoPath());
	        as2 = (String[])arraylist.toArray(new String[arraylist.size()]);
	        l14 = as2.length;
	        
	        for (i15 = 0; i15 < l14; i15++) { 
	            s18 = as2[i15];
	            Log.d("Command", (new StringBuilder()).append(s18).toString());
	        }
	        
            processbuilder1 = new ProcessBuilder(as2);
	        
            process = processbuilder1.redirectErrorStream(true).start();
	        bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
	        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	        
	        Log.v("LOGTAG", "***Starting FFMPEG***");
	        
	        stringbuilder = new StringBuilder();
	        stringbuilder.append(as2);

	        renderbuffer = bufferedreader.readLine();
	        
	        while (renderbuffer != null) {
		        
		        float fcurTime;
		        
		        Log.w("LOGTAG", (new StringBuilder("***")).append(renderbuffer).append("***").toString());
		        
		        stringbuilder.append(renderbuffer);
		        stringbuilder.append("\n");
		        
		        if(renderbuffer.indexOf("time=") > 0) {
			        strBuffer3 = renderbuffer.substring(5 + renderbuffer.indexOf("time"), 16 + renderbuffer.indexOf("time")).split(":");
			        
			        if(strBuffer3.length > 2) { 
				        fcurTime = Float.parseFloat(strBuffer3[2]) * 1000.0f + 
				        		Float.parseFloat(strBuffer3[1]) * 60000.0f + Float.parseFloat(strBuffer3[0]) * 3600000.0f;
				        
				        onprogressupdate.onUpdate((100 * Math.round(fcurTime)) / nDuration, nDuration);
				        
				        Log.w("time", (new StringBuilder()).append(Math.round(fcurTime)).toString());
			        }
		        }
		        
		        renderbuffer = bufferedreader.readLine();
	        } 
	        
            writer = null;
            
            if(!stringbuilder.toString().contains("frame=")) {
                stringbuilder.append("Commands");
                stringbuilder.append("\n");
                stringbuilder.append(Arrays.toString(as2));
                //ffmpegerror.onError(stringbuilder);
                Log.e("FFMpeg Error", stringbuilder.toString());
                Utils.writeLogToFile("FramesLayout-Render-"+stringbuilder.toString());
            }
            
            Log.v("LOGTAG", "*********************************************************************************************Ending FFMPEG***");
            return frameinfo.getVideoPath();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
    }

    public JSONArray saveDatas() throws JSONException {
    	
        RelativeLayout relativelayout = (RelativeLayout)getChildAt(0);
        JSONArray jsonarray = new JSONArray();
        
        for (int i = 0; i < relativelayout.getChildCount(); i++) {
            JSONObject jsonobject = new JSONObject();
            FrameView frameview = (FrameView)relativelayout.getChildAt(i);
            
            jsonobject.put("bitmap_uri", frameview.getBitmapUri());
            jsonobject.put("top_margin", frameview.getTopMargin());
            jsonobject.put("left_margin", frameview.getLeftMargin());
            jsonobject.put("border_width", getBorderWidth());
            jsonobject.put("width", frameview.bitmapWidth());
            jsonobject.put("height", frameview.bitmapHeight());
            jsonobject.put("video", frameview.isVideo());
            jsonobject.put("color", getBorderColor());
            jsonobject.put("video_uri", frameview.getVideoUri());
            jsonobject.put("audio_volume", frameview.getAudioVolume());
            jsonobject.put("order", order);
            jsonobject.put("volume", audioVolume);
            jsonobject.put("rotation", frameview.getRotation());
            jsonobject.put("angle", frameview.getAngle());
            jsonobject.put("starttime", frameview.getStartTime());
            jsonobject.put("duration", frameview.getVideoDuration());
            jsonobject.put("video_duration", videoDuration);
            jsonobject.put("override", isOverride);
            jsonobject.put("videoHasAudio", frameview.isVideoHasAudio());
            jsonobject.put("extension", frameview.getExtensionParam());
            jsonobject.put("audioStartTime", audioStartTime);
            jsonobject.put("audioEndTime", audioEndTime);
            jsonarray.put(jsonobject);
        }
        
        return jsonarray;
    }

    public void setAudioEndTime(long l) {
        Log.d("endtime", (new StringBuilder()).append(l).toString());
        audioEndTime = l;
    }

    public void setAudioPath(String s) {
        audioPath = s;
        audioStartTime = -1L;
        audioEndTime = -1L;
    }

    public void setAudioStartTime(long l) {
        Log.d("starttime", (new StringBuilder()).append(l).toString());
        audioStartTime = l;
    }

    public void setAudioVolume(float f) {
        audioVolume = f;
    }

    public void setAudioVolume(int i, float f) {
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(i)).setAudioVolume(f);
    }

    public void setBorderColor(int borderClr) {
        borderPaint.setColor(borderClr);
        requestLayout();
        invalidate();
    }

    public void setBorderWidth(int borderW) {
        borderWidth = (int)((float)parentWidth * ((float)borderW / 100F));
        requestLayout();
        invalidate();
    }

    public void setFrames(ArrayList arraylist) {
        frames = arraylist;
    }

    public void setOrder(String strOrder) {
        order = strOrder;
    }

    public void setOverride(boolean flag) {
        isOverride = flag;
    }

    public void setRotationForPosition(int index, int angle) {
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(index)).setRotation(angle);
    }
    
    public void setAngleForPosition(int index, int angle) {
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(index)).setAngle(angle);
    }

    public void setStartTime(int index, int time) {
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(index)).setStartTime(time);
    }
    
    public void setDuration(int index, int time) {
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(index)).setVideoDuration(time);
    }

    public int getSelectedFrame() {
    	return selectedFrame;
    }
    
    public void setSelectedFrame(int frmIndex) {
        selectedFrame = frmIndex;
        RelativeLayout relativelayout = (RelativeLayout)getChildAt(0);
        MLog.d(this, "selectedFrame:"+selectedFrame );
        for (int j = 0; j < relativelayout.getChildCount(); j++) {
            if(j == selectedFrame)
            	{MLog.d(this, "true"+j);
                ((FrameView)relativelayout.getChildAt(j)).setSelectedBackground(true);}
            else
            	{MLog.d(this, "false"+j);
                ((FrameView)relativelayout.getChildAt(j)).setSelectedBackground(false);}
        } 
    }

    public void setChangeSelectedFrame() {
    	changeSelectedFrame = selectedFrame;
        RelativeLayout relativelayout = (RelativeLayout)getChildAt(0);
        
        for (int j = 0; j < relativelayout.getChildCount(); j++) {
        	((FrameView)relativelayout.getChildAt(j)).setChangePosFlag(true);
            if(j == selectedFrame)
                ((FrameView)relativelayout.getChildAt(j)).setChangeSelectedBackground(false);
            else
                ((FrameView)relativelayout.getChildAt(j)).setChangeSelectedBackground(true);
        } 
    }

    public void changeFrame() {
        RelativeLayout relativelayout = (RelativeLayout)getChildAt(0);
        
        for (int j = 0; j < relativelayout.getChildCount(); j++) {
        	((FrameView)relativelayout.getChildAt(j)).setChangePosFlag(false);
        	((FrameView)relativelayout.getChildAt(j)).setChangeSelectedBackground(false);
        } 

        if (changeSelectedFrame == selectedFrame) {
        	changeSelectedFrame = -1;
        	return;
        }
        
        String strVideoPath = null;
        String strPicturePath = null;
        
        if (((FrameView)relativelayout.getChildAt(selectedFrame)).isVideo()) {
        	strVideoPath = ((FrameView)relativelayout.getChildAt(selectedFrame)).getVideoUri();
        }
        else {
        	strPicturePath = ((FrameView)relativelayout.getChildAt(selectedFrame)).getBitmapUri();
        }
        	
        if (((FrameView)relativelayout.getChildAt(changeSelectedFrame)).isVideo()) {
        	addVideo(((FrameView)relativelayout.getChildAt(changeSelectedFrame)).getVideoUri(), 0, 0, new com.dragonplayer.merge.frames.FrameView.CorruptVideoError() {

                public void corruptVideo() {
                }
            });
        }
        else {
        	addBitmap(((FrameView)relativelayout.getChildAt(changeSelectedFrame)).getBitmapUri(), new com.dragonplayer.merge.frames.FrameView.CorruptVideoError() {

                public void corruptVideo() {
                }
            });
        }

        int nTemp = selectedFrame;
        selectedFrame = changeSelectedFrame;
        
        if (strVideoPath != null && !strVideoPath.isEmpty()) {
        	addVideo(strVideoPath, 0, 0, new com.dragonplayer.merge.frames.FrameView.CorruptVideoError() {

                public void corruptVideo() {
                }
            });
        }
        else if (strPicturePath != null && !strPicturePath.isEmpty()) {
        	addBitmap(strPicturePath, new com.dragonplayer.merge.frames.FrameView.CorruptVideoError() {

                public void corruptVideo() {
                }
            });
        }
        else {
        	((FrameView)relativelayout.getChildAt(changeSelectedFrame)).initFrame();
        }
        	
        selectedFrame = nTemp;
        changeSelectedFrame = -1;
    }

    public void setSequentially(boolean flag) {
        isSequentially = flag;
    }

    public void setVideoDuration(int i) {
        videoDuration = i;
    }

    public void setVideoUriForPosition(int index, String videoUri) {
        ((FrameView)((RelativeLayout)getChildAt(0)).getChildAt(index)).setVideoUri(videoUri);
    }

    public void setupPlayPauseButton() {
    }

    public void startStopVideo() {
    	
        boolean flag = true;
        int cntVideo = countVideos();
        RelativeLayout relativelayout = (RelativeLayout)getChildAt(0);
        
        for (int j = 0; j < relativelayout.getChildCount(); j++) {
        	
            FrameView frameview = (FrameView)relativelayout.getChildAt(j);
            if(frameview.isVideo()) {
                if(frameview.isVideoPlaying()) {
                    frameview.pauseVideo();
                    playBtn.setSelected(false);
                    return;
                }
                
                playBtn.setSelected(flag);
                android.media.MediaPlayer.OnCompletionListener oncompletionlistener = new android.media.MediaPlayer.OnCompletionListener() {

                    public void onCompletion(MediaPlayer mediaplayer) {
                        playBtn.setSelected(false);
                    }
                };
                
                if(cntVideo != j - 1)
                    flag = false;
                
                frameview.playVideo(oncompletionlistener, flag);
                return;
            }
        } 
    }

    public String writeVideo(com.dragonplayer.merge.fragment.FinishFragment.OnProgressUpdate onprogressupdate, File file, com.dragonplayer.merge.fragment.FinishFragment.FFMpegError ffmpegerror) {
    	
        FrameInfo frameinfo;
        frameinfo = new FrameInfo();
        frameinfo.setScaleWidth(width);
        frameinfo.setScaleHeight(height);
        
        StringBuilder stringbuilder = new StringBuilder("0x");
        Object clrObj[] = new Object[1];

        clrObj[0] = Integer.valueOf(0xffffff & getBorderColor());
        frameinfo.setBorderColor(stringbuilder.append(String.format("%06X", clrObj)).toString());
        frameinfo.setVideoPath(file.getAbsolutePath());

        FrameView frameview;
        
        for (int i = 0; i < frames.size(); i++) {
        	
	        frameview = (FrameView)frames.get(i);
	        String filePath;
	        
	        if(frameview.isVideo())
	            filePath = frameview.getVideoUri();
	        else
	            filePath = frameview.getBitmapUri();
	        
	        if(!frameview.isVideo() && !filePath.equals("") && !filePath.substring(filePath.lastIndexOf("."), filePath.length()).contains("gif"))
	            filePath = filePath.replace("%", "%%");
	        
	        frameinfo.addUrls(filePath);
	        frameinfo.addBitmapHeight(Integer.valueOf(frameview.bitmapHeight()));
	        frameinfo.addBitmapWidth(Integer.valueOf(frameview.bitmapWidth()));
	        frameinfo.addAngle(Float.valueOf(frameview.getAngle()));
	        
	        if(frameview.getHeightt() + frameview.getEnd() >= height) {
	        	
	            if(borderWidth >= 1) {
	                if(frameview.getEnd() <= 0)
	                    frameinfo.addFrameHeight(Integer.valueOf(frameview.getHeightt() - (int)Math.floor(2 * borderWidth)));
	                else
	                    frameinfo.addFrameHeight(Integer.valueOf(frameview.getHeightt() - (int)Math.floor(1.5D * (double)borderWidth)));
	            } 
	            else {
	                frameinfo.addFrameHeight(Integer.valueOf(height - frameview.getEnd()));
	            }
	        } 
	        else if(frameview.getEnd() <= 0)
	            frameinfo.addFrameHeight(Integer.valueOf(frameview.getHeightt() - (int)Math.floor(1.5D * (double)borderWidth)));
	        else
	            frameinfo.addFrameHeight(Integer.valueOf(frameview.getHeightt() - (int)Math.floor(1 * borderWidth)));
	
	        if(frameview.getWidthh() + frameview.getStart() < width) {
	            if(frameview.getStart() <= 0)
	                frameinfo.addFrameWidth(Integer.valueOf(frameview.getWidthh() - (int)Math.floor(1.5D * (double)borderWidth)));
	            else
	                frameinfo.addFrameWidth(Integer.valueOf(frameview.getWidthh() - (int)Math.floor(1 * borderWidth)));
	        }
	        else {
		        if(borderWidth >= 1) {
		            if(frameview.getStart() <= 0)
		                frameinfo.addFrameWidth(Integer.valueOf(frameview.getWidthh() - (int)Math.floor(2 * borderWidth)));
		            else
		                frameinfo.addFrameWidth(Integer.valueOf(frameview.getWidthh() - (int)Math.floor(1.5D * (double)borderWidth)));
		        } 
		        else
		        {
		            frameinfo.addFrameWidth(Integer.valueOf(width - frameview.getStart()));
		        }
	        }
	
	        if(frameview.getStart() <= 0) {
	            frameinfo.addLeft(Integer.valueOf(frameview.getStart() + borderWidth));
	            frameinfo.addLeftMargins(Integer.valueOf(frameview.getLeftMargin() - borderWidth));
	        } 
	        else {
	            frameinfo.addLeft(Integer.valueOf(frameview.getStart() + borderWidth / 2));
	            frameinfo.addLeftMargins(Integer.valueOf(frameview.getLeftMargin() - (int)Math.floor(borderWidth / 2)));
	        }
	        
	        if(frameview.getEnd() <= 0) {
	            frameinfo.addTop(Integer.valueOf(frameview.getEnd() + borderWidth));
	            frameinfo.addTopMargins(Integer.valueOf(frameview.getTopMargin() - borderWidth));
	        } 
	        else {
	            frameinfo.addTop(Integer.valueOf(frameview.getEnd() + (int)Math.floor(borderWidth / 2)));
	            frameinfo.addTopMargins(Integer.valueOf(frameview.getTopMargin() - (int)Math.floor(borderWidth / 2)));
	        }
	        
	        frameinfo.addStartTime(frameview.getStartTime());
	        frameinfo.addVideoDuration(frameview.getVideoDuration());
	        frameinfo.addVideoAudioVolume(Float.valueOf(frameview.getAudioVolume()));
	        
	        if (Float.valueOf(frameview.getAngle()) != 0) {
	        	frameinfo.addRotations(Integer.valueOf((int)frameview.getRotation()));
	        	Log.e("asd","asd"+i+":"+Float.valueOf(frameview.getAngle())+":"+((int)frameview.getRotation()));
	        }
	        else {
	        	Log.e("asd","asd11"+i+":"+((int)frameview.getRotation()));
	        	frameinfo.addRotations(Integer.valueOf((int)frameview.getRotation()));
	        }
	        
	        frameinfo.addVideoHasAudio(Boolean.valueOf(frameview.isVideoHasAudio()));
	        frameinfo.addExtension(frameview.getExtensionParam());
	        Log.d("audio volume", (new StringBuilder()).append(frameview.getAudioVolume()).toString());
        }

    	if (frameID == FRAMEID_EAT) {
	        frameinfo.addUrls("/data/data/com.dragonplayer.merge/eat.avi");
	        frameinfo.addBitmapHeight((536 * frameinfo.getScaleWidth() /1096));
	        frameinfo.addBitmapWidth(frameinfo.getScaleWidth());
	        frameinfo.addFrameHeight((536 * frameinfo.getScaleWidth() /1096));
	        frameinfo.addFrameWidth(frameinfo.getScaleWidth());
	        frameinfo.addAngle(0);
	
	        frameinfo.addLeft(0);
	        frameinfo.addLeftMargins(0);
	        frameinfo.addTop((frameinfo.getScaleHeight() * 1384 / 1920));
	        frameinfo.addTopMargins(0);
	        
	        frameinfo.addStartTime(0);
	        frameinfo.addVideoDuration(6006);
	        frameinfo.addVideoAudioVolume(1.0f);
	        frameinfo.addRotations(0);
	        frameinfo.addVideoHasAudio(false);
	        frameinfo.addExtension("");
    	}
    	else if (frameID == FRAMEID_HAPPYBIRTHDAY) {
	        frameinfo.addUrls("/data/data/com.dragonplayer.merge/happybirthday.avi");
	        frameinfo.addBitmapHeight((536 * frameinfo.getScaleWidth() /1096));
	        frameinfo.addBitmapWidth(frameinfo.getScaleWidth());
	        frameinfo.addFrameHeight((536 * frameinfo.getScaleWidth() /1096));
	        frameinfo.addFrameWidth(frameinfo.getScaleWidth());
	        frameinfo.addAngle(0);
	
	        frameinfo.addLeft(0);
	        frameinfo.addLeftMargins(0);
	        frameinfo.addTop(0);
	        frameinfo.addTopMargins(0);
	        
	        frameinfo.addStartTime(0);
	        frameinfo.addVideoDuration(7986);
	        frameinfo.addVideoAudioVolume(1.0f);
	        frameinfo.addRotations(0);
	        frameinfo.addVideoHasAudio(false);
	        frameinfo.addExtension("");
    	}
        
        Log.d("order", (new StringBuilder(" ")).append(order).toString());
        renderVideo(frameinfo, onprogressupdate, ffmpegerror);
        return "";
    }
    
    public boolean isExistSelectFrame() {
        FrameView frameview = (FrameView)frames.get(selectedFrame);
        String filePath;
        
        if(frameview.isVideo())
            filePath = frameview.getVideoUri();
        else
            filePath = frameview.getBitmapUri();
        
        if(filePath == null || filePath.equals(""))
        	return false;
        
        return true;
    }

    public int getFrameCount() {
    	return frame.getParts().size();
    }
    
    public boolean isExistVideoFrame() {
        ArrayList arraylist = frame.getParts();
    	String filePath;
        
        for (int k = 0; k < arraylist.size(); k++) {
        	FrameView frameview = (FrameView)frames.get(k);
        	filePath = null;
        	
        	if(frameview.isVideo())
        		filePath = frameview.getVideoUri();
        
        	if(filePath != null && !filePath.isEmpty())
        		return true;
        }
        
        return false;
    }
    
    public boolean isExistVideoSelectedFrame() {
        ArrayList arraylist = frame.getParts();
    	String filePath;
        
    	FrameView frameview = (FrameView)frames.get(selectedFrame);
    	filePath = null;
        	
    	if(frameview.isVideo())
    		filePath = frameview.getVideoUri();
        
    	if(filePath != null && !filePath.isEmpty())
    		return true;
        
        return false;
    }

    public void setTextImagePath(String strFileName) {
        textImagePath = strFileName;
        fTxtAngle = 0;
        txtX = getWidth() / 2;
        txtY = getHeight() / 2;
        fZoom = 1;
        invalidate();
    }

    public String getTextImagePath() {
        return textImagePath;
    }

    public boolean isExistContent() {
        for (int i = 0; i < frames.size(); i++) {
        	
        	FrameView frameview = (FrameView)frames.get(i);
	        String filePath;
	        
	        if(frameview.isVideo())
	            filePath = frameview.getVideoUri();
	        else
	            filePath = frameview.getBitmapUri();

	        if (filePath == null || filePath.isEmpty())
	        	return false;
        }
        
        return true;
    }

    public String getVideoPath() {
        for (int i = 0; i < frames.size(); i++) {
        	
        	FrameView frameview = (FrameView)frames.get(i);
	        String filePath;
	        
	        if(frameview.isVideo())
	            return frameview.getVideoUri();
        }
        
        return null;
    }

    public void removeFrame() {
        FrameView frameview = (FrameView)((RelativeLayout)getChildAt(0)).getChildAt(selectedFrame);
        frameview.initFrame();
    }

    public boolean onTouchEvent(MotionEvent event) {

    	if (textImagePath == null || textImagePath.isEmpty() || !isWithSelection) {
    		return super.onTouchEvent(event);
    	}
    	
    	if (nTextScaleMode == TEXTSCALEMODE_NONE)
    		return super.onTouchEvent(event);

    	switch (event.getAction() & 0xFF) {
    	case MotionEvent.ACTION_DOWN:
    	    nOrgPosX = (int)event.getX();
    	    nOrgPosY = (int)event.getY();
    	    
    		if ((new Rect(rcText.left - SCALE_ICON_WIDTH, rcText.top - SCALE_ICON_HEIGHT, rcText.left - 2, rcText.top - 2)).contains((int)event.getX(), (int)event.getY())) {
    			nTextScaleMode = TEXTSCALEMODE_SCALE;
    		}
    		else if ((new Rect(rcText.right + 2, rcText.top - ROTATE_ICON_WIDTH, rcText.right + ROTATE_ICON_HEIGHT, rcText.top - 2)).contains((int)event.getX(), (int)event.getY())) {
    			nTextScaleMode = TEXTSCALEMODE_ROTATE;
    			fTxtTmpAngle = fTxtAngle + 45;
    		}
    		else if (rcText.contains((int)event.getX(), (int)event.getY())) {
    			nTextScaleMode = TEXTSCALEMODE_MOVE;
    		}
    		break;
    	case MotionEvent.ACTION_MOVE:
    		int stepX = (int)event.getX() - nOrgPosX;
    		int stepY = (int)event.getY() - nOrgPosY;
    		
    		if (nTextScaleMode == TEXTSCALEMODE_SCALE) {
    			
    			if (Math.abs(stepX) >= STEPMIN || Math.abs(stepY) >= STEPMIN) {
	    			Rect rcTemp = new Rect(rcText);
	    			rcTemp.left += stepX * 2; 
	    			rcTemp.top += stepY * 2;
	    			float scaleX = (float)rcTemp.width() / (float)rcText.width();
	    			float scaleY = (float)rcTemp.height() / (float)rcText.height();
	    			
	    			if (scaleX > scaleY)
	    				fZoom = fZoom * scaleX; 
	    			else
	    				fZoom = fZoom * scaleY;
	    			
	    			if (fZoom < 0.4f)
	    				fZoom = 0.4f;
	    			
		    		nOrgPosX = (int)event.getX();
		    	    nOrgPosY = (int)event.getY();
		    	    invalidate();
    			}
    		}
    		else if (nTextScaleMode == TEXTSCALEMODE_ROTATE) {
    			double tx = (int)event.getX() - txtX, ty = (int)event.getY() - txtY;
    			double t_length = Math.sqrt(tx*tx + ty*ty);
    			double a = Math.acos(ty / t_length);
    			float angle = (float)getAngle((int)event.getX(), (int)event.getY());
    			fTxtAngle = fTxtTmpAngle - angle;
    			
    			if (fTxtAngle > 360)
    				fTxtAngle -= 360;
    			if (fTxtAngle < 0)
    				fTxtAngle += 360;

	    		nOrgPosX = (int)event.getX();
	    	    nOrgPosY = (int)event.getY();
	    	    invalidate();
    		}
    		else if (nTextScaleMode == TEXTSCALEMODE_MOVE) {
	    		txtX += stepX;
	    		txtY += stepY;
	    		nOrgPosX = (int)event.getX();
	    	    nOrgPosY = (int)event.getY();
	    	    invalidate();
    		}
    		break;
    	case MotionEvent.ACTION_UP:
    		if (nTextScaleMode != TEXTSCALEMODE_SELECT)
    			nTextScaleMode = TEXTSCALEMODE_SELECT;
    		else {
    			nTextScaleMode = TEXTSCALEMODE_NONE;

                for (int i = 0; i < frames.size(); i++) {
                    FrameView frameview = (FrameView)frames.get(i);
                    frameview.setEnabled(true);
                } 
                
                invalidate();
    		}

    		nOrgPosX = 0;
    	    nOrgPosY = 0;
    		break;
    	}

    	return super.onTouchEvent(event);
    }
    
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	if (textImagePath == null || textImagePath.isEmpty() || !isWithSelection) {
    		return super.onInterceptTouchEvent(event);
    	}
    	
    	if (nTextScaleMode != TEXTSCALEMODE_NONE)
    		return super.onInterceptTouchEvent(event);
    	
    	if ((event.getAction() & 0xFF) == MotionEvent.ACTION_DOWN && 
    			rcText.contains((int)event.getX(), (int)event.getY())) {
            for (int i = 0; i < frames.size(); i++) {
                FrameView frameview = (FrameView)frames.get(i);
                frameview.setEnabled(false);
            } 
            
            nTextScaleMode = TEXTSCALEMODE_SELECT;
            
            return true;
    	}
    	
    	return super.onInterceptTouchEvent(event);
    }

    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - txtX;
        //double y = getHeight() - yTouch - txtY;
        double y = txtY - yTouch;
     
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

    public void setFrameBitmap(Bitmap bmp, int index) {
    	FrameView frameview = (FrameView)frames.get(index);
    	frameview.setFrameBitmap(bmp);
    }
    
    public String getRotateFile(int index, FrameInfo frameinfo) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float)frameinfo.getAngle(index));
        Bitmap frameBitmap = ((FrameView)frames.get(index)).getFrameBitmap();
        Bitmap bitmap = Bitmap.createBitmap(frameBitmap, 0, 0, frameBitmap.getWidth(), frameBitmap.getHeight(), matrix, false);
//        bitmap = Bitmap.createScaledBitmap(bitmap, ((FrameView)frames.get(index)).getFrameBitmap().getWidth(), ((FrameView)frames.get(index)).getFrameBitmap().getHeight(), false);
        int frameScaleHeight = frameinfo.getFrameScaleHeight(index);
        int frameScaleWidth = frameinfo.getFrameScaleWidth(index);
//        int frameCropX = frameinfo.getCropX(index);
//        int frameCropY = frameinfo.getCropY(index);
//        int frameCropH = frameinfo.getCropH(index);
//        int frameCropW = frameinfo.getCropW(index);
        
        frameScaleWidth = frameScaleWidth * bitmap.getWidth() / frameBitmap.getWidth();
        frameScaleHeight = frameScaleHeight * bitmap.getHeight() / frameBitmap.getHeight();
        
        frameinfo.getBitmapWidth().set(index, frameScaleWidth);
        frameinfo.getBitmapHeight().set(index, frameScaleHeight);

        String strFile = Utils.writeToPNGFile(bitmap).getAbsolutePath();
        bitmap.recycle();
        
        return strFile;
    }
}
