package com.dragonplayer.merge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.*;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;




//import com.flurry.android.FlurryAgent;
//import com.google.analytics.tracking.android.EasyTracker;
import com.dragonplayer.merge.utils.FileMover;
import com.dragonplayer.merge.utils.Utils;

import java.io.*;
/**
 * 
 * @author 啟動畫面
 *
 */
public class SplashActivity extends Activity {
	
	Handler handler;
	
    private class Splash extends AsyncTask {

        protected Integer doInBackground(Void avoid[]) {
            String cmd[] = {
                "ffmpeg", "Cicle Fina.ttf", "01.mp3", "02.mp3", "03.mp3", "04.mp3", "05.mp3", "06.mp3", "07.mp3", "eat.avi", "happybirthday.avi" 
            };
            
            for (int i = 0; i < cmd.length; i++) {
                try {
                    (new FileMover(getAssets().open(cmd[i]), (new StringBuilder("/data/data/com.dragonplayer.merge/")).append(cmd[i]).toString())).moveIt();
                }
                catch(FileNotFoundException filenotfoundexception) {
                    filenotfoundexception.printStackTrace();
                    clearApplicationData();
                    return Integer.valueOf(2);
                }
                catch(IOException ioexception) {
                    ioexception.printStackTrace();
                    return Integer.valueOf(1);
                }
            }

            return Integer.valueOf(0);
        }

        protected Object doInBackground(Object aobj[]) {
            return doInBackground((Void[])aobj);
        }

        protected void onPostExecute(Integer integer) {
            if(integer.intValue() == 0)
                handler.postDelayed(new Runnable() {

                    public void run() {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        finish();
                    }
                }, 500);
            else if(integer.intValue() == 1) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("ERROR!").setMessage("You don't have enough free space on your device!").setCancelable(true).setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                        finish();
                    }
                });
                
                builder.create().show();
            } 
            else {
                (new Splash()).execute(new Void[0]);
            }
            
            super.onPostExecute(integer);
        }

        protected void onPostExecute(Object obj) {
            onPostExecute((Integer)obj);
        }

        private Splash() {
            super();
        }

        Splash(Splash splash) {
            this();
        }
    }


    public SplashActivity() {
        handler = new Handler();
    }

    public static boolean deleteDir(File file) {
        
    	if(file == null || !file.isDirectory()) 
    		return file.delete();

    	String fileList[];
        fileList = file.list();

        for (int i = 0; i < fileList.length; i++) 
	        if(!deleteDir(new File(file, fileList[i])))
	            return false;
        
        return file.delete();
	}

    public void clearApplicationData() {
    	
        File file = new File(getCacheDir().getParent());
        
        if(!file.exists()) 
        	return;

        String fileList[];
        fileList = file.list();

        for (int j = 0; j < fileList.length; j++) {
        	if(!fileList[j].equals("lib"))
        		deleteDir(new File(file, fileList[j]));
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        setContentView(R.layout.splash_layout);
        
//        ImageView logoimage = (ImageView)findViewById(R.id.logoimglaunchapp);
//        LayoutParams params = (LayoutParams) logoimage.getLayoutParams();
//        
//        Options options = new Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), R.drawable.launchapp, options);
//        
//        int screenWidth = getResources().getDisplayMetrics().widthPixels;
//        int screenHeight = getResources().getDisplayMetrics().heightPixels;
//        
//        params.height = options.outHeight * (screenWidth / 5 * 4) / options.outWidth;
//        params.width = screenWidth / 5 * 4;
//        
//        logoimage.setLayoutParams(params);
//        
//        Utils.deleteLogToFile();
//        Utils.writeLogToFile("---------------Start App---------------");
        Utils.deleteAllJPGS();
        
        (new Splash(null)).execute(new Void[0]);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }
}
