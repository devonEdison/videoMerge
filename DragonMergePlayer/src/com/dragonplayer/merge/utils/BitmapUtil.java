package com.dragonplayer.merge.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.dragonplayer.merge.R;

public class BitmapUtil
{
    static class FlushedInputStream extends FilterInputStream {

        public long skip(long time) throws IOException {
        	
            long time1 = 0L;
            
            while (time1 < time) {
            	long interval;
            	interval = in.skip(time - time1);
            
            	if(interval == 0L)
            		if(read() >= 0)
            			interval = 1L;

            	time1 += interval;
            }
            
            return time1;
        }

        public FlushedInputStream(InputStream inputstream) {
            super(inputstream);
        }
    }

    public BitmapUtil() {
    }

    public static Bitmap getBitmap(String bmpPath, int width, int height) {
    	
        Bitmap bitmap = null;
        URL url;
		try {
			url = new URL(bmpPath);
			
	        URLConnection urlconnection = url.openConnection();
	        urlconnection.connect();

	        InputStream inputstream = urlconnection.getInputStream();
	        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
	        
	        options.inJustDecodeBounds = true;
	        options.inDensity = 180;
	        BitmapFactory.decodeStream(new FlushedInputStream(inputstream), null, options);
	        
	        options.inSampleSize = getScale(options.outWidth, options.outHeight, width, height);
	        options.inJustDecodeBounds = false;
	        options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
	        
	        inputstream.close();
	        
	        urlconnection = url.openConnection();
	        urlconnection.connect();
	        
	        inputstream = urlconnection.getInputStream();
	        bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputstream), null, options);
	        inputstream.close();
	        
	        return bitmap;
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }

    public static Bitmap getBitmapForNewProject(Context context, int nId, int width, int height, int color) {
    	
		try {
	        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), nId);
	        Canvas canvas = new Canvas(bitmap);
	        
	        canvas.drawColor(color);
	        canvas.drawBitmap(icon, (bitmap.getWidth() - icon.getWidth()) / 2, (bitmap.getHeight() - icon.getHeight()) / 2, null);
	        icon.recycle();
	        
	        return bitmap;
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }

    public static Bitmap getBitmapFromPath(String bmpPath, int width, int height) {
    	
        Bitmap bitmap = null;
		try {
	        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
	        
	        options.inJustDecodeBounds = true;
	        options.inDensity = 180;

	        BitmapFactory.decodeFile(bmpPath, options);
	        
	        options.inSampleSize = getScale(options.outWidth, options.outHeight, width, height);
	        options.inJustDecodeBounds = false;
	        options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
	        
	        bitmap = BitmapFactory.decodeFile(bmpPath, options);
	        
	        return Bitmap.createScaledBitmap(bitmap, width, height, false);
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }

    public static Bitmap getFrameBitmap(Context context, Bitmap bgBmp, int startX, int startY, int endX, int endY) {
        Bitmap bmp = Bitmap.createBitmap(bgBmp, startX, startY, 
        		(bgBmp.getWidth() < endX) ? bgBmp.getWidth() - startX : endX - startX, 
        		(bgBmp.getHeight() < endY) ? bgBmp.getHeight() - startY : endY - startY);
        Canvas canvas = new Canvas(bmp);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_add_frame);
        int left;
        int top;
        int right;
        int bottom;
        int iconsize;
        
        if (bmp.getWidth() > bmp.getHeight()) {
        	iconsize = bmp.getHeight() / 2;
        }
        else {
        	iconsize = bmp.getWidth() / 2;
        }
        
        left = bmp.getWidth() / 2 - iconsize / 2;
        top = bmp.getHeight() / 2 - iconsize / 2;
        right = bmp.getWidth() / 2 + iconsize / 2;
        bottom = bmp.getHeight() / 2 + iconsize / 2;
        	 
        canvas.drawBitmap(icon, new Rect(0, 0, icon.getWidth(), icon.getHeight()), new Rect(left, top, right, bottom), null);
    	return bmp;
    }

    public static Bitmap getBitmapFromAsset(Context context, String bmpFileName, int width, int height) {
    	return getBitmapFromAsset(context, bmpFileName, width, height, false,0);
    }
    
    public static Bitmap getBitmapFromAsset(Context context, String bmpFileName, int width, int height, boolean lockflag,int lockmessage) {
    	
        Bitmap bitmap = null;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
        try {
        	InputStream ims = context.getAssets().open(bmpFileName);
	        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
	        
	        options.inJustDecodeBounds = true;
	        options.inDensity = 180;

	        BitmapFactory.decodeStream(ims, null, options);

	        if (options.outWidth * options.outHeight > displaymetrics.widthPixels * displaymetrics.heightPixels)
	        	options.inSampleSize = getScale(options.outWidth, options.outHeight, displaymetrics.widthPixels, displaymetrics.heightPixels);
	        else
	        	//options.inSampleSize = getScale(options.outWidth, options.outHeight, width, height);
	        	options.inSampleSize = 1;
	        
	        options.inJustDecodeBounds = false;
	        options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
	        
	        ims = context.getAssets().open(bmpFileName);
	        bitmap = BitmapFactory.decodeStream(ims, null, options);
	        
	        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
	        
	        if (lockflag) {
	        	Canvas canvas = new Canvas(bitmap);
	        	Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.lock_off_thumb);
	        	Paint paint = new Paint();
	        	paint.setColor(0x60000000);
	        	
	            int iconsizeWidth;
	            int iconsizeHeight;
	            
	            if (width < height) {
	            	iconsizeWidth = width / 2;
	            	iconsizeHeight = iconsizeWidth * 80 / 61;
	            }
	            else {
	            	iconsizeHeight = height / 3 * 2;
	            	iconsizeWidth = iconsizeHeight * 61 / 80;
	            }

	        	canvas.drawRect(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), paint);
	            canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), 
	            			new Rect(bitmap.getWidth() / 2 - iconsizeWidth / 2, 
	            					bitmap.getHeight() / 2 - iconsizeHeight / 2, 
	            					bitmap.getWidth() / 2 + iconsizeWidth / 2, 
	            					bitmap.getHeight() / 2 + iconsizeHeight / 2), null);
	        	bmp.recycle();
	        }
	        if (lockmessage==1) {
	        	Canvas canvas = new Canvas(bitmap);
	        	// å�¯èƒ½æ˜¯è§£éŽ–åœ–æ¡ˆå‘ˆç�¾åœ°æ–¹
	        	//Log.e("aaaaaaaaaaaaa2","locFlag=true"); 
	        	Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_unlock_prize);
	        	Paint paint = new Paint();
	        	paint.setColor(0x60000000);
	        	
	            int iconsizeWidth;
	            int iconsizeHeight;
	            
	            if (width < height) {
	            	iconsizeWidth = width / 2;
	            	iconsizeHeight = iconsizeWidth * 80 / 61;
	            }
	            else {
	            	iconsizeHeight = height / 3 * 2;
	            	iconsizeWidth = iconsizeHeight * 61 / 80;
	            } 

	        	canvas.drawRect(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), paint);
	            canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), 
	            			new Rect(0, 0, iconsizeWidth , iconsizeHeight ), null);
	        	bmp.recycle();
	        }  
	        return bitmap;
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }

    public static Bitmap getBitmapImage(String bmpPath, File tempDir, int width, int height) {
    	
        File file;
        android.graphics.BitmapFactory.Options options;
        Bitmap bitmap;
        file = new File(tempDir, String.valueOf(bmpPath.hashCode()));
        options = new android.graphics.BitmapFactory.Options();
        
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        
        Log.d("outWidth outHeight", (new StringBuilder(String.valueOf(options.outWidth))).append(" ").append(options.outHeight).toString());
        
        options.inSampleSize = getScale(options.outWidth, options.outHeight, width, height);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file.getPath(), options);

        if(bitmap == null) {
        	
            Log.d("selected url", bmpPath);
            if(bmpPath.startsWith("http"))
                bitmap = getBitmap(bmpPath, width, height);
            else
                bitmap = Utils.getBitmap(bmpPath, width, height);
            
            if(bitmap != null)
                writeFile(bitmap, file);
        }
        
        return bitmap;
    }

    public static int getDuration(String videoPath) {
    	
        MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
        int duration;
        
        try {
            mediametadataretriever.setDataSource(videoPath);
        } catch(IllegalArgumentException illegalargumentexception) {
            illegalargumentexception.printStackTrace();
            return 0;
        }
        
        duration = Integer.parseInt(mediametadataretriever.extractMetadata(9));
        mediametadataretriever.release();
        
        return duration;
    }

    public static int getOrienation(String videoPath) {
        MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
        mediametadataretriever.setDataSource(videoPath);
        int i = Integer.parseInt(mediametadataretriever.extractMetadata(24));
        mediametadataretriever.release();
        return i;
    }

    public static String getPhotoUrl(Context context, String bmpPath, File file, int width, int height) {
        Bitmap bitmap = getBitmapImage(bmpPath, file, width, height);
        String dirName;
        File dir;
        String fileName;
        
        if(Environment.getExternalStorageState().equals("mounted"))
            dirName = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("Pictures").toString();
        else
            dirName = (new StringBuilder(String.valueOf(context.getFilesDir().getAbsolutePath()))).append(File.separator).append("Pictures").toString();
        
        dir = new File(dirName);
        
        if(!dir.exists())
            dir.mkdir();
        
        fileName = (new StringBuilder(String.valueOf(dirName))).append(File.separator).append("picture").append(".jpg").toString();
        writeFile(bitmap, new File(fileName));
        bitmap.recycle();
        
        return fileName;
    }

    public static int getScale(int w1, int h1, int w2, int h2) {
        int scale = 1;
        
        if(w1 > w2 || h1 > h2)
            if(w1 < h1)
                scale = Math.round((float)w1 / (float)w2);
            else
                scale = Math.round((float)h1 / (float)h2);
        
        if((w1 >= 1024 || h1 > 1024) && scale == 1)
            if(w1 > h1) {
                if(w1 / 2 > 1024)
                    scale = 4;
                else
                    scale = 2;
            } 
            else if(h1 / 2 > 1024)
                scale = 4;
            else
                scale = 2;
        
        Log.d("scale", (new StringBuilder(" ")).append(scale).toString());
        
        return scale;
    }

    public static void rotateBitmap(Bitmap bitmap, String filePath) {
        try {
			switch ((new ExifInterface(filePath)).getAttributeInt("Orientation", 1)) {
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
				rotateImage(180, bitmap);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			    Log.d("flip", "vertical");
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
			    Log.d("transpose", "#%$*&^*(&");
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
			    rotateImage(90, bitmap);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
			    Log.d("transverse", "$#%#%^$^");
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
			    rotateImage(270, bitmap);
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
    	
        if(!bitmap.isRecycled()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        }
        
        return bitmap;
    }

    public static Bitmap videoFrame(String url, long time) {
        MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
        Bitmap bitmap;
        
        mediametadataretriever.setDataSource(url);
        bitmap = mediametadataretriever.getFrameAtTime(time);
        
        try {
            mediametadataretriever.release();
        } catch(RuntimeException runtimeexception4) {
        }
        
        return bitmap;
    }

    public static void writeFile(Bitmap bitmap, File file)
    {
        if(file.exists())
            file.delete();
        
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, bytearrayoutputstream);
        
        try {
            file.createNewFile();
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            fileoutputstream.write(bytearrayoutputstream.toByteArray());
            fileoutputstream.close();
        } catch(Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void writeFilePNG(Bitmap bitmap, File file) {
        if(file.exists())
            file.delete();
        
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
        
        try {
            file.createNewFile();
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            fileoutputstream.write(bytearrayoutputstream.toByteArray());
            fileoutputstream.close();
        } catch(Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void writeIStoFile(InputStream inputstream, String s) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(s);
        byte buffer[] = new byte[1024];
        
        do {
            int len = inputstream.read(buffer);
            
            if(len == -1) {
                fileoutputstream.close();
                return;
            }
            
            fileoutputstream.write(buffer, 0, len);
        } while(true);
    }
}
