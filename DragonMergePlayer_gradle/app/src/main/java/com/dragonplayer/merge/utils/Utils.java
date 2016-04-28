package com.dragonplayer.merge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.dragonplayer.merge.R;
import com.dragonplayer.merge.frames.Frame;

public class Utils {
	
    public Utils() {
    }

    public static void deleteAllJPGS() {
    	
    	Utils.writeLogToFile("Util-deleteAllJPEGS-start");
    	
        File file = new File((new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("data").append(File.separator).append("dragonmergeplayer/temp").toString());

        if(!file.exists())
            file.mkdir();
        
        if(!file.isDirectory()) {
        	
        	Utils.writeLogToFile("Util-deleteAllJPEGS-noDir");
        	
            File file1 = new File((new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("data").append(File.separator).append("dragonmergeplayer/temp").append(File.separator).append("pics").toString());
            if(!file1.exists()) 
            	return;

            String fileList[] = file1.list();

            for (int i = 0; i < fileList.length; i++)
            	(new File(file1, fileList[i])).delete();
            file1.delete();
        }
        else {
        	Utils.writeLogToFile("Util-deleteAllJPEGS-dir");
        	
	        String fileList[] = file.list();
	
	        for (int j = 0; j < fileList.length; j++) {
	            String fileName = fileList[j];
//	            if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg"))
	                (new File(file, fileName)).delete();
	        }
        }
    }

    public static void deleteTemporayFile(String fileName) {
    	Utils.writeLogToFile("Util-deleteTemporaryFile-name="+fileName);
        File file = new File(fileName);
        if(file.exists())
            file.delete();
    }

    public static Bitmap getBitmap(int id, Resources resources, int width, int height) {
    	Utils.writeLogToFile("Util-getBitmap_res-id="+id+":width="+width+":height="+height);
        Options options = new Options();
        options.inJustDecodeBounds = true;
        
        BitmapFactory.decodeResource(resources, id, options);
        
        Utils.writeLogToFile("Util-getBitmap_res-id="+id+":outwidth="+options.outWidth+":outheight="+options.outHeight);
        
        options.inSampleSize = BitmapUtil.getScale(options.outWidth, options.outHeight, width, height);
        options.inJustDecodeBounds = false;
        
        return BitmapFactory.decodeResource(resources, id, options);
    }

    public static Bitmap getBitmap(String filePath, int width, int height) {
    	Utils.writeLogToFile("Util-getBitmap_file-file="+filePath+":width="+width+":height="+height);
        Options options = new Options();
        options.inJustDecodeBounds = true;
        
        BitmapFactory.decodeFile(filePath, options);
        
        Utils.writeLogToFile("Util-getBitmap_file-file="+filePath+":outwidth="+options.outWidth+":outheight="+options.outHeight);
        
        options.inSampleSize = BitmapUtil.getScale(options.outWidth, options.outHeight, width, height);
        options.inJustDecodeBounds = false;
        
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int getBitmapDirection(String filePath) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        
        BitmapFactory.decodeFile(filePath, options);

        if (options.outWidth < options.outHeight)
        	return Frame.FRAMEVERTICAL;
        
        return Frame.FRAMEHORIZONTAL;
    }

    public static String getDatas(Context context) {
        return context.getSharedPreferences(context.getString(R.string.PREFS), 1).getString("selected_data", "");
    }

    public static boolean getFirstUse(Context context) {
        return context.getSharedPreferences(context.getString(R.string.PREFS), 1).getBoolean("first_use", true);
    }

    public static int getFrameWidth(Context context) {
        return context.getSharedPreferences(context.getString(R.string.PREFS), 1).getInt("frame_width", -1);
    }

    public static int getFrameHeight(Context context) {
        return context.getSharedPreferences(context.getString(R.string.PREFS), 1).getInt("frame_height", -1);
    }

    public static void setDatas(Context context, String data) {
        Editor editor = context.getSharedPreferences(context.getString(R.string.PREFS), 0).edit();
        editor.putString("selected_data", data);
        editor.commit();
    }

    public static void setFirstUse(Context context, boolean flag) {
        Editor editor = context.getSharedPreferences(context.getString(R.string.PREFS), 0).edit();
        editor.putBoolean("first_use", flag);
        editor.commit();
    }

    public static void setFrameWidth(Context context, int w, int h) {
        Editor editor = context.getSharedPreferences(context.getString(R.string.PREFS), 0).edit();
        editor.putInt("frame_width", w);
        editor.putInt("frame_height", h);
        editor.commit();
    }

	public static void deleteLogToFile() {
	    try {
		    File file = new File(
		    		(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())))
		    		.append(File.separator).append("log.txt").toString());
		    if (file.exists())
		    	file.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public static void writeLogToFile(String strLog) {
//		Log.e("Util", strLog);
//	    try {
//	    	String endline="\n";
//		    FileOutputStream localFileOutputStream = new FileOutputStream(
//		    		(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())))
//		    		.append(File.separator).append("log.txt").toString(), true);
//			localFileOutputStream.write(strLog.getBytes(), 0, strLog.getBytes().length);
//			localFileOutputStream.write(endline.getBytes(), 0, endline.getBytes().length);
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }

	public static String copyFile(String strSrc, boolean isVideo) {
    	
        File file;
        
        String strDst = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("data").append(File.separator).append("dragonmergeplayer/temp").toString();
        
        file = new File(strDst);
        
        if(!file.exists())
            file.mkdir();
        
        if (isVideo)
        	file = new File((new StringBuilder(String.valueOf(strDst))).append(File.separator).append(Calendar.getInstance().getTimeInMillis()).append(".mp4").toString());
        else
        	file = new File((new StringBuilder(String.valueOf(strDst))).append(File.separator).append(Calendar.getInstance().getTimeInMillis()).append(".jpg").toString());
        
        if(file.exists())
            file.delete();

    	InputStream inStream = null;
    	OutputStream outStream = null;
 
    	try{
 
    	    inStream = new FileInputStream(new File(strSrc));
    	    outStream = new FileOutputStream(file);
 
    	    byte[] buffer = new byte[1024];
 
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
 
    	    	outStream.write(buffer, 0, length);
 
    	    }
    	    inStream.close();
    	    outStream.close();

    	    return file.getAbsolutePath();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	
    	return null;
	}

	public static File writeToFile(Bitmap bitmap) {
    	
    	Utils.writeLogToFile("Util-writeToFile-start");    	
        
    	ByteArrayOutputStream bytearrayoutputstream;
        File file;
        
        bytearrayoutputstream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, bytearrayoutputstream);
        
        String s = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("data").append(File.separator).append("dragonmergeplayer/temp").toString();
        
        Utils.writeLogToFile("Util-writeToFile-outDirPath="+s);        
        
        file = new File(s);
        
        if(!file.exists())
            file.mkdir();
        
        file = new File((new StringBuilder(String.valueOf(s))).append(File.separator).append("picture").append(Calendar.getInstance().getTimeInMillis()).append(".jpg").toString());
        
        if(file.exists())
            file.delete();

        Utils.writeLogToFile("Util-writeToFile-file1");
        
        try {
			file.createNewFile();
			Utils.writeLogToFile("Util-writeToFile-file2");
	        FileOutputStream fileoutputstream = new FileOutputStream(file);
	        fileoutputstream.write(bytearrayoutputstream.toByteArray());
	        fileoutputstream.close();
	        Utils.writeLogToFile("Util-writeToFile-file3="+file.getName());
	        return file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Utils.writeLogToFile("Util-writeToFile-file4");
			e.printStackTrace();
		}
        
        return null;
    }

    public static File writeToPNGFile(Bitmap bitmap) {
    	
    	ByteArrayOutputStream bytearrayoutputstream;
        File file;
        
        bytearrayoutputstream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
        
        String s = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("data").append(File.separator).append("dragonmergeplayer/temp").toString();
        
        file = new File(s);
        
        if(!file.exists())
            file.mkdir();
        
        file = new File((new StringBuilder(String.valueOf(s))).append(File.separator).append("picture").append(Calendar.getInstance().getTimeInMillis()).append(".png").toString());
        
        if(file.exists())
            file.delete();

        try {
			file.createNewFile();
	        FileOutputStream fileoutputstream = new FileOutputStream(file);
	        fileoutputstream.write(bytearrayoutputstream.toByteArray());
	        fileoutputstream.close();
	        return file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
    }
}
