package com.dragonplayer.merge.frames;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.Utils;

public class ProjectFiles {

    private static ArrayList<File> projectfiles = new ArrayList<File>();
    private static ProjectFiles instance;

    private ProjectFiles() {
    }

    private static void createFrames(Activity activity) {
    	
    	projectfiles.clear();
    	projectfiles.add(null);
    	
        String dir = Environment.getExternalStorageDirectory() + "/data/DragonMergePlayer";
        File file1 = new File(dir);
        
        if(!file1.exists())
            file1.mkdir();
        
        File file2 = new File(dir, "ProjectFiles");
        
        if(!file2.exists())
            file2.mkdir();
        
        File[] fileArray = file2.listFiles();
        
        for (int i = fileArray.length - 1; i >= 0; i--) {
        	String fileName = fileArray[i].getName();
        	
        	if (fileName.contains(".mp4") || fileName.contains(".jpg")) {
        		try {
	                if (fileName.contains("mp4")) {
	                	if (BitmapUtil.videoFrame(fileArray[i].getAbsolutePath(), 0L) == null)
	                		continue;
	                }
	        		projectfiles.add(fileArray[i]);
        		}
        		catch (Exception e) {
        		}
        	}
        }
    }

    public static ProjectFiles newInstance(Activity activity) {
        if(instance == null) {
            instance = new ProjectFiles();
        }

        createFrames(activity);
        
        return instance;
    }

    public File getFileNameWithIndex(int i) {
        return (File)projectfiles.get(i);
    }

    public void removeIndex(int i) {
        projectfiles.remove(i);
    }

    public int getFileCount() {
        return projectfiles.size();
    }
}
