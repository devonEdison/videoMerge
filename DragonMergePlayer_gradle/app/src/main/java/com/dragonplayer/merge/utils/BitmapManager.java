package com.dragonplayer.merge.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;



import java.io.File;
import java.io.IOException;
import java.util.Stack;

import com.dragonplayer.merge.R;
 

public class BitmapManager
{
	public final static int BITMAPLOADSUCCESS = 1;
	public final static int BITMAPLOADERROR = 2;

	private File cacheDir;
    private Thread imageLoaderThread;
    private LruCache imageMap;
    private ImageQueue imageQueue;
    private Handler frameLoadCheckHandler;
    /** */
    Bitmap bitmap = null;
    Bitmap mask = null;
	private Context context;
    
    private class BitmapDisplayer implements Runnable {

        Bitmap bitmap;
        Drawable drawable ;
        int bitmapHeight;
        int bitmapWidth;
        int height;
        ImageView imageView;
        int leftMargin;
        int rotation;
        com.dragonplayer.merge.frames.FrameView.SetBounds setBounds;
        int topMargin;
        String url;
        int width;
        int frameIndex;
    
        
        public void rotateImage(int angle) {
            if(!bitmap.isRecycled()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            }
        }
       
        
        public void run() {
       
        	Utils.writeLogToFile("BitmapManager-BitmapDisplay-start:index="+frameIndex+":url="+url);
            if(bitmap == null) {
                imageView.setImageBitmap(null);
                return;
            }
            
            Utils.writeLogToFile("BitmapManager-BitmapDisplay-start:Bitmap!=null");

            int attribute;
			try {
				attribute = (new ExifInterface(url)).getAttributeInt("Orientation", 1);
	            switch (attribute) {
	            case ExifInterface.ORIENTATION_UNDEFINED:
	                Log.d("undefined", "#%#%^$&$");
	            	break;
	            case ExifInterface.ORIENTATION_NORMAL:
	                Log.d("normal", "&&&&&&&l");
	            	break;
	            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
	                Log.d("flip", "horizontal");
	            	break;
	            case ExifInterface.ORIENTATION_ROTATE_180:
	                rotateImage(180);
	            	break;
	            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
	                Log.d("flip", "vertical");
	            	break;
	            case ExifInterface.ORIENTATION_TRANSPOSE:
	                Log.d("transpose", "#%$*&^*(&");
	            	break;
	            case ExifInterface.ORIENTATION_ROTATE_90:
	            	rotateImage(90);
	            	break;
	            case ExifInterface.ORIENTATION_TRANSVERSE:
	                rotateImage(270);
	            	break;
	            case ExifInterface.ORIENTATION_ROTATE_270:
	                Log.d("transverse", "$#%#%^$^");
	            	break;
	            }
	            
	            if (bitmap != null)
	            	Utils.writeLogToFile("BitmapManager-BitmapDisplay-Rotation1:Bitmap!=null");
	            else
	            	Utils.writeLogToFile("BitmapManager-BitmapDisplay-Rotation1:Bitmap==null");
	            
	            Bitmap tmp = Bitmap.createBitmap(bitmap);

	            if(rotation != 0)
	                rotateImage(rotation);
	            
	            if (bitmap != null)
	            	Utils.writeLogToFile("BitmapManager-BitmapDisplay-Rotation2:Bitmap!=null");
	            else
	            	Utils.writeLogToFile("BitmapManager-BitmapDisplay-Rotation2:Bitmap==null");

	            android.widget.FrameLayout.LayoutParams layoutparams;

	            if(bitmapWidth == -1 && bitmapHeight == -1) {
	                int bmpWidth = bitmap.getWidth();
	                int bmpHeight = bitmap.getHeight();
	                float rate = (float)bitmap.getWidth() / (float)bitmap.getHeight();
	                
	                if(bmpWidth < width) {
	                	bmpWidth = width;
	                	bmpHeight = (int)Math.ceil((float)width / rate);
	                }
	                
	                if(bmpHeight < height) {
	                	bmpHeight = height;
	                    bmpWidth = (int)Math.ceil(rate * (float)height);
	                }
	                
	                layoutparams = new android.widget.FrameLayout.LayoutParams(bmpWidth, bmpHeight);
	            } 
	            else {
	                int bmpWidth = bitmapWidth;
	                int bmpHeight = bitmapHeight;
	                float rate = (float)bitmap.getWidth() / (float)bitmap.getHeight();
	                
	                if(bmpWidth < width) {
	                    bmpWidth = width;
	                    bmpHeight = (int)Math.ceil((float)width / rate);
	                }
	                
	                if(bmpHeight < height) {
	                    bmpHeight = height;
	                    bmpWidth = (int)Math.ceil(rate * (float)height);
	                }
	                
	                layoutparams = new android.widget.FrameLayout.LayoutParams(bmpWidth, bmpHeight);
	            }
	            
	            layoutparams.gravity = Gravity.TOP | Gravity.LEFT;
	            
	            if(topMargin == -1 && leftMargin == -1) {
	                if(layoutparams.height < height)
	                	layoutparams.topMargin = 0;
	                else
	                	layoutparams.topMargin = (height - layoutparams.height) / 2;
	                
	                if(layoutparams.width < width)
	                	layoutparams.leftMargin = 0;
	                else
	                	layoutparams.leftMargin = (width - layoutparams.width) / 2;
	            }
	            else {
	                layoutparams.topMargin = topMargin;
	                layoutparams.leftMargin = leftMargin;
	            }
	            

	            Utils.writeLogToFile("BitmapManager-BitmapDisplay-Params Success");
	            // bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.color01);
	            /**遮罩圖*/			
	              
	            //Drawable drawable = context.getResources().getDrawable(R.drawable.ic_launcher);
	            		//getResources("R.drawable.ic_launcher");
	       /**    	MLog.e("this",context.toString());
	            Drawable drawable =  context.getResources().getDrawable(R.drawable.v40s);
	             BitmapDrawable bitmapDrawble =  (BitmapDrawable) drawable;
	            mask = bitmapDrawble.getBitmap();
	            int size = 300;      */      
	            
	           
	          //  **遮罩圖*/
	          //  imageView.setImageBitmap(MaskBitmap(bitmap, mask,size,  new PorterDuffXfermode(PorterDuff.Mode.SRC_IN))); 
	            
	            imageView.setLayoutParams(layoutparams);
	            imageView.setImageBitmap(bitmap);
	            
	            setBounds.setBounds(width - layoutparams.width, height - layoutparams.height, 
	            					2 * layoutparams.width, 2 * layoutparams.height);
	            
	            Utils.writeLogToFile("BitmapManager-BitmapDisplay-SetImageBitmap Success");
	            
	            Message msg = new Message();
	            msg.what = BITMAPLOADSUCCESS;
	            msg.arg1 = frameIndex;
	            msg.obj = (Object)tmp;
	            frameLoadCheckHandler.sendMessage(msg);
	            
			} catch (IOException e) {
				// TODO Auto-generated catch block
	            Message msg = new Message();
	            msg.what = BITMAPLOADERROR;
	            msg.arg1 = frameIndex;
	            frameLoadCheckHandler.sendMessage(msg);
				e.printStackTrace();
			}
        }

        public BitmapDisplayer(Bitmap bmp, ImageView imgview, int t, int l, int w, int h, 
                com.dragonplayer.merge.frames.FrameView.SetBounds setbounds, int bmpWidth, int bmpHeight, 
                String urlImage, int rotationImage, int index) {
            
        	super();
        	
            if(bitmap != null)
                bitmap.recycle();
            
            bitmap = bmp;
            imageView = imgview;
            topMargin = t;
            leftMargin = l;
            width = w;
            height = h;
            setBounds = setbounds;
            bitmapHeight = bmpHeight;
            bitmapWidth = bmpWidth;
            url = urlImage;
            rotation = rotationImage;
            frameIndex = index;
            
        }

		 
    }
    public BitmapManager(Context context)
    {	MLog.e(this, "this"+context);
    	this.context = context;	
    }
    private Bitmap MaskBitmap(Bitmap bitmap, Bitmap mask, int size, Xfermode mode) {
        if (null == bitmap || mask == null) {
            return null;
        }
        //定义期望大小的bitmap
        Bitmap dstBmp = Bitmap.createBitmap(size, size, Config.ARGB_8888);
        //定义一个画布
        Canvas canvas = new Canvas(dstBmp);
        //创建一个取消锯齿画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //定义需要绘制的某图片上的那一部分矩形空间
        Rect src = new Rect(0, 0, mask.getWidth(), mask.getHeight());
        //定义需要将上面的矩形绘制成新的矩形大小
        Rect dst = new Rect(0, 0, size, size);
        //将蒙版图片绘制成imageview本身的大小,这样从大小才会和UE标注的一样大
        canvas.drawBitmap(mask, src, dst, paint);
        //设置两张图片的相交模式
        //至于这个函数介绍参考网址:http://blog.csdn.net/wm111/article/details/7299294
        paint.setXfermode(mode);
        //将src修改为需要添加mask的bitmap大小,因为是要将此bitmap整个添加上蒙版
        src.right = bitmap.getWidth();
        src.bottom = bitmap.getHeight();
        //在已经绘制的mask上叠加bitmap
        canvas.drawBitmap(bitmap, src, dst, paint);
        return dstBmp;
    }
    private class ImageQueue {
    	
        private Stack<ImageRef> imageRefs;

        public void Clean(ImageView imageview) {
            
        	int i = 0;
            for (i = 0; i < imageRefs.size(); i++) { 
	            if(((ImageRef)imageRefs.get(i)).imageView == imageview) {
	                imageRefs.remove(i);
	            }
            }
        }

        public ImageQueue() {
            super();
            imageRefs = new Stack<ImageRef>();
        }
    }

    public class ImageQueueManager
        implements Runnable {

        public void run() {

        	Utils.writeLogToFile("BitmapManager-ImageQueueManager-start Success");
        	
        	do {
        		Utils.writeLogToFile("BitmapManager-ImageQueueManager-start1");
	            if(imageQueue.imageRefs.size() == 0) {
	            	Utils.writeLogToFile("BitmapManager-ImageQueueManager-start2");
	                synchronized(imageQueue.imageRefs) {
	                	Utils.writeLogToFile("BitmapManager-ImageQueueManager-start3");
	                    try {
							imageQueue.imageRefs.wait();
							Utils.writeLogToFile("BitmapManager-ImageQueueManager-start4");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							Utils.writeLogToFile("BitmapManager-ImageQueueManager-start5");
							e.printStackTrace();
						}
	                }
	            }
	            
	            Utils.writeLogToFile("BitmapManager-ImageQueueManager-start6, size="+imageQueue.imageRefs.size());
	            Log.e("ErrorTest", "ImageQueueManager:"+imageQueue.imageRefs.size());
	            
	            if(imageQueue.imageRefs.size() != 0) {
		            ImageRef imageref;
		        
		            Utils.writeLogToFile("BitmapManager-ImageQueueManager-start7");
		            
		            synchronized(imageQueue.imageRefs) {
		                imageref = (ImageRef)imageQueue.imageRefs.firstElement();
		                imageQueue.imageRefs.remove(imageref);
		            }
		            
		            Utils.writeLogToFile("BitmapManager-ImageQueueManager-start8");
		            
		            Bitmap bitmap;
		            Object obj;
		            BitmapDisplayer bitmapdisplayer;

		            bitmap = BitmapUtil.getBitmapImage(imageref.url, cacheDir, imageref.size, imageref.heigth);
		            
		            Utils.writeLogToFile("BitmapManager-ImageQueueManager-start9");
		            
		            if(bitmap != null) {
		            	Utils.writeLogToFile("BitmapManager-ImageQueueManager-start10, url="+imageref.url);
		            	imageMap.put(imageref.url, bitmap);
		            }
		            
		            obj = imageref.imageView.getTag();
		            
		            Utils.writeLogToFile("BitmapManager-ImageQueueManager-start11, tag="+(String)obj);
		            
		            if(obj != null) {
		            	Utils.writeLogToFile("BitmapManager-ImageQueueManager-start12");
		            	
			            if(((String)obj).equals(imageref.url)) {
			            	Utils.writeLogToFile("BitmapManager-ImageQueueManager-start13");
			                bitmapdisplayer = new BitmapDisplayer(bitmap, imageref.imageView, imageref.topMargin, imageref.leftMargin, imageref.width, imageref.height, imageref.setbounds, imageref.bitmapWidth, imageref.bitmapHeight, imageref.url, imageref.rotation, imageref.frameIndex );
			                ((Activity)imageref.imageView.getContext()).runOnUiThread(bitmapdisplayer);
			            }
		            }
		            
		            Utils.writeLogToFile("BitmapManager-ImageQueueManager-start14");
		            if(imageref.url.contains("data/dragonmergeplayer/picture")) {
		            	Utils.writeLogToFile("BitmapManager-ImageQueueManager-start15");
		                Utils.deleteTemporayFile(imageref.url);
		            }
	            }
	            
        	} while (!Thread.interrupted()); 
        }
    }

    private class ImageRef {

        public int bitmapHeight;
        public int bitmapWidth;
        public int height;
        public int heigth;
        public ImageView imageView;
        public int leftMargin;
        public int rotation;
        public com.dragonplayer.merge.frames.FrameView.SetBounds setbounds;
        public int size;
        public int topMargin;
        public String url;
        public int width;
        public int frameIndex;

        public ImageRef(String urlImage, ImageView imgview, int s, int t, int l, 
                int w, int h, com.dragonplayer.merge.frames.FrameView.SetBounds setbnd, 
                int bmpWidth, int bmpHeight, int rotationImg, int index) {
            super();
            url = urlImage;
            imageView = imgview;
            size = s;
            heigth = h;
            topMargin = t;
            leftMargin = l;
            width = w;
            height = h;
            setbounds = setbnd;
            bitmapHeight = bmpHeight;
            bitmapWidth = bmpWidth;
            rotation = rotationImg;
            frameIndex = index;
        }
    }


    public BitmapManager(Context context, Handler handler) {
    	
        frameLoadCheckHandler = handler; 
        imageQueue = new ImageQueue();
        imageLoaderThread = new Thread(new ImageQueueManager());
        imageMap = new LruCache((int)(Runtime.getRuntime().maxMemory() / 1024L) / 12) {

            protected int sizeOf(Object obj, Object obj1) {
                return sizeOf((String)obj, (Bitmap)obj1);
            }

            protected int sizeOf(String s, Bitmap bitmap) {
                return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
            }
        };
        
        if(Environment.getExternalStorageState().equals("mounted"))
            cacheDir = new File(Environment.getExternalStorageDirectory(), "data/dragonmergeplayer");
        else
            cacheDir = context.getCacheDir();
        
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    private void queueImage(String urlImg, ImageView imgview, int s, int t, int l, int w, 
            int h, com.dragonplayer.merge.frames.FrameView.SetBounds setbounds, 
            int bmpWidth, int bmpHeight, int rotationImg, int index) {
    	
    	Utils.writeLogToFile("BitmapManager-queueImage-start:index="+index+":url="+urlImg);
    	
        imageQueue.Clean(imgview);
        ImageRef imageref = new ImageRef(urlImg, imgview, s, t, l, w, h, setbounds, bmpWidth, bmpHeight, rotationImg, index);
        
        synchronized(imageQueue.imageRefs) {
        	Utils.writeLogToFile("BitmapManager-queueImage-push:index="+index+":url="+urlImg);
            imageQueue.imageRefs.push(imageref);
            imageQueue.imageRefs.notifyAll();
        }
        
        if(imageLoaderThread.getState() == Thread.State.NEW) {
        	Utils.writeLogToFile("BitmapManager-queueImage-new:index="+index+":url="+urlImg);
            imageLoaderThread.start();
        }
    }

    public void displayImage(String urlImg, ImageView imgview, int s, int t, int l, int w, 
            int h, com.dragonplayer.merge.frames.FrameView.SetBounds setbounds, 
            int bmpWidth, int bmpHeight, int rotationImg, int index ,Context maskcontext) {
    	context = maskcontext;
    	Utils.writeLogToFile("BitmapManager-displayImae-imgPath="+urlImg+":index="+index+":s="+s+":t="+t+":l="+l+":w="+w+":h="+h+":bmpWidth="+bmpWidth+":bmpHeight="+bmpHeight);
    	
        Bitmap bitmap = (Bitmap)imageMap.get(urlImg);
        if(bitmap == null) {
        	Utils.writeLogToFile("BitmapManager-displayImae-Bitmap==null");
            queueImage(urlImg, imgview, s, t, l, w, h, setbounds, bmpWidth, bmpHeight, rotationImg, index);
            imgview.setImageBitmap(null);
        }
        else {
        	Utils.writeLogToFile("BitmapManager-displayImae-Bitmap!=null");
        	BitmapDisplayer bitmapdisplayer = new BitmapDisplayer(bitmap, imgview, t, l, w, h, setbounds, 
        			bmpWidth, bmpHeight, urlImg, rotationImg, index);
        	((Activity)imgview.getContext()).runOnUiThread(bitmapdisplayer);
        }
    }

    public void displayThumbImage(String urlImg, ImageView imgview, int bmpWidth, int bmpHeight) {

    	imgview.setImageBitmap(BitmapUtil.getBitmapFromPath(urlImg, bmpWidth, bmpHeight));
    }

    public void displayNewProjectThumbImage(Context context, int nId, ImageView imgview, int bmpWidth, int bmpHeight, int color) {

    	imgview.setImageBitmap(BitmapUtil.getBitmapForNewProject(context, nId, bmpWidth, bmpHeight, color));
    }
}
