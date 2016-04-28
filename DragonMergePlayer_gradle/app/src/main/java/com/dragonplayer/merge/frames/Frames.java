package com.dragonplayer.merge.frames;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;

public class Frames {

    private static ArrayList<Frame> frames1 = new ArrayList<Frame>();
    private static ArrayList<Frame> frames2 = new ArrayList<Frame>();
    private static Frames instance;

    private Frames() {
    }

    private static void createFrames(Activity activity) {
        BufferedReader bufferedreader;
        int i;
        String s;
        Frame frame;
        String cmd[];
        String cmd1[];
        String cmd2[];
        String cmd3[];
        int j;
        int k;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(activity.getAssets().open("frames.txt")));
        }
        catch(IOException ioexception) {
            return;
        }
        
        i = 0;
        while (true) {
	        try {
				s = bufferedreader.readLine();
		        if(s == null) {
		        	bufferedreader.close();
		        	return;
		        }
		        
		        frame = new Frame();
		        frame.setFrame_id(i);
		        do {
			        if(s.contains(";")) {
			            cmd = s.split(";");
			            cmd1 = cmd[0].split(" ");
			            cmd2 = cmd[1].split(" ");
			            
			            if (cmd.length == 2)
			            	frame.addPart(new FramePart(Integer.parseInt(cmd1[0]), Integer.parseInt(cmd1[1]), Integer.parseInt(cmd2[0]), Integer.parseInt(cmd2[1])));
			            else
			            	frame.addPart(new FramePart(Integer.parseInt(cmd1[0]), Integer.parseInt(cmd1[1]), Integer.parseInt(cmd2[0]), Integer.parseInt(cmd2[1]), true));
			        }
			        else if(s.contains(",")) {
			            cmd = s.split(",");
			            frame.setLockFlag(Integer.parseInt(cmd[0]));
			            frame.setFrameDirection(Integer.parseInt(cmd[1]));
			            frame.setFrameType(Integer.parseInt(cmd[2]));
			            frame.setFrameName(cmd[3]);
			            frame.setColor(Integer.parseInt(cmd[4], 16));
			        }
			        else {
				        cmd3 = s.split(" ");
				        j = Integer.parseInt(cmd3[0]);
				        k = Integer.parseInt(cmd3[1]);
				        frame.setWidth(j);
				        frame.setHeight(k);
			        }
			
			        s = bufferedreader.readLine();
		        } while (!s.equals(""));
		
		        	i++;
		        	
		        if (frame.getFrameType() == Frame.FRAMETYPEIMAGE)
		        	frames1.add(frame);
		        else
		        	frames2.add(frame);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public static Frames newInstance(Activity activity) {
        if(instance == null) {
            instance = new Frames();
            createFrames(activity);
        }
        
        return instance;
    }

    public Frame getFrameWithId(int i) {
    	if (i < 18)
    		return (Frame)frames1.get(i);
    	else
    		return (Frame)frames2.get(i-18);
    }

    public int getFramesCount() {
        return frames1.size() + frames2.size();
    }

    public Frame getFrameWithId1(int i) {
        return (Frame)frames1.get(i);
    }

    public int getFramesCount1() {
        return frames1.size();
    }

    public Frame getFrameWithId2(int i) {
        return (Frame)frames2.get(i);
    }

    public int getFramesCount2() {
        return frames2.size();
    }
}
