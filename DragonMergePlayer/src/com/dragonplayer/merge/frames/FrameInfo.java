package com.dragonplayer.merge.frames;

import android.util.Log;

import java.util.ArrayList;

public class FrameInfo {

    private ArrayList<Integer> bitmapHeight;
    private ArrayList<Integer> bitmapWidth;
    private String borderColor;
    private ArrayList<String> extensions;
    private ArrayList<Integer> frameHeight;
    private ArrayList<Integer> frameWidth;
    private ArrayList<Integer> left;
    private ArrayList<Integer> leftMargins;
    private ArrayList<Integer> rotations;
    private int scaleHeight;
    private int scaleWidth;
    private ArrayList<Integer> top;
    private ArrayList<Integer> topMargins;
    private ArrayList<String> urls;
    private ArrayList<Float> videoAudioVolumes;
    private ArrayList<Integer> startTime;
    private ArrayList<Integer> videoDuration;
    private ArrayList<Boolean> videoHasAudio;
    private ArrayList<Float> angle;
    private String videoPath;

    public FrameInfo() {
        urls = new ArrayList<String>();
        angle = new ArrayList<Float>();
        scaleWidth = 0;
        scaleHeight = 0;
        topMargins = new ArrayList<Integer>();
        leftMargins = new ArrayList<Integer>();
        frameHeight = new ArrayList<Integer>();
        frameWidth = new ArrayList<Integer>();
        top = new ArrayList<Integer>();
        left = new ArrayList<Integer>();
        bitmapWidth = new ArrayList<Integer>();
        bitmapHeight = new ArrayList<Integer>();
        startTime = new ArrayList<Integer>();
        videoDuration = new ArrayList<Integer>();
        videoAudioVolumes = new ArrayList<Float>();
        rotations = new ArrayList<Integer>();
        videoHasAudio = new ArrayList<Boolean>();
        extensions = new ArrayList<String>();
    }

    public void addBitmapHeight(Integer h) {
        bitmapHeight.add(h);
    }

    public void addBitmapWidth(Integer w) {
        bitmapWidth.add(w);
    }

    public void addExtension(String ext) {
        extensions.add(ext);
    }

    public void addFrameHeight(Integer frmH) {
        frameHeight.add(frmH);
        Log.d("frameHeight", (new StringBuilder()).append(frmH).toString());
    }

    public void addFrameWidth(Integer frmW) {
        frameWidth.add(frmW);
        Log.d("frameWidth", (new StringBuilder()).append(frmW).toString());
    }

    public void addLeft(Integer l) {
        left.add(l);
        Log.d("left", (new StringBuilder()).append(l).toString());
    }

    public void addLeftMargins(Integer lMargin) {
        leftMargins.add(Integer.valueOf(Math.abs(lMargin.intValue())));
        Log.d("leftMargin", (new StringBuilder()).append(lMargin).toString());
    }

    public void addRotations(Integer rot) {
        rotations.add(rot);
    }

    public void addTop(Integer t) {
        top.add(t);
        Log.d("top", (new StringBuilder()).append(t).toString());
    }

    public void addTopMargins(Integer tMargin) {
        topMargins.add(Integer.valueOf(Math.abs(tMargin.intValue())));
        Log.d("topMargin", (new StringBuilder()).append(tMargin).toString());
    }

    public void addUrls(String url) {
        urls.add(url);
        Log.d("url", url);
    }

    public void addAngle(float fangle) {
        angle.add(fangle);
    }

    public void addVideoAudioVolume(Float volume) {
        videoAudioVolumes.add(volume);
    }

    public void addVideoDuration(int duration) {
        videoDuration.add(Integer.valueOf(duration));
        Log.d("duration", (new StringBuilder()).append(duration).toString());
    }
    
    public void addStartTime(int duration) {
        startTime.add(Integer.valueOf(duration));
    }
    
    public void addVideoHasAudio(Boolean isHasAudio) {
        videoHasAudio.add(isHasAudio);
    }

    public ArrayList<Integer> getBitmapHeight() {
        return bitmapHeight;
    }

    public ArrayList<Integer> getBitmapWidth() {
        return bitmapWidth;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public int getCropH(int i) {
        return ((Integer)frameHeight.get(i)).intValue();
    }

    public int getCropW(int i) {
        return ((Integer)frameWidth.get(i)).intValue();
    }

    public int getCropX(int i) {
        return ((Integer)leftMargins.get(i)).intValue();
    }

    public int getCropY(int i) {
        return ((Integer)topMargins.get(i)).intValue();
    }

    public int getDuration(int i) {
        return ((Integer)videoDuration.get(i)).intValue();
    }

    public int getStartTime(int i) {
        return ((Integer)startTime.get(i)).intValue();
    }

    public int getElementSize() {
        return urls.size();
    }

    public int getAngleSize() {
        return angle.size();
    }

    public String getExtensions(int i) {
        return (String)extensions.get(i);
    }

    public ArrayList<String> getExtensions() {
        return extensions;
    }

    public ArrayList<Integer> getFrameHeight() {
        return frameHeight;
    }

    public int getFrameScaleHeight(int i) {
        return ((Integer)bitmapHeight.get(i)).intValue();
    }

    public int getFrameScaleWidth(int i) {
        return ((Integer)bitmapWidth.get(i)).intValue();
    }

    public ArrayList<Integer> getFrameWidth() {
        return frameWidth;
    }

    public ArrayList<Integer> getLeft() {
        return left;
    }

    public ArrayList<Integer> getLeftMargins() {
        return leftMargins;
    }

    public int getLongestDurationIndex() {
        int i = 0;
        int j = ((Integer)videoDuration.get(0)).intValue();
        
        for (int k = 1; k < videoDuration.size(); k++) { 
            if(((Integer)videoDuration.get(k)).intValue() > j) {
                i = k;
                j = ((Integer)videoDuration.get(k)).intValue();
            }
        }

        return i;
    }

    public int getRotation(int i) {
        return ((Integer)rotations.get(i)).intValue();
    }

    public ArrayList<Integer> getRotations() {
        return rotations;
    }

    public int getScaleHeight() {
        return scaleHeight;
    }

    public int getScaleWidth() {
        return scaleWidth;
    }

    public ArrayList<Integer> getTop() {
        return top;
    }

    public ArrayList<Integer> getTopMargins() {
        return topMargins;
    }

    public String getUrl(int i) {
        return (String)urls.get(i);
    }

    public float getAngle(int i) {
        return (float)angle.get(i);
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public ArrayList<Float> getAngles() {
        return angle;
    }

    public Float getVideoAudioVolume(int i) {
        return (Float)videoAudioVolumes.get(i);
    }

    public ArrayList<Float> getVideoAudioVolumes() {
        return videoAudioVolumes;
    }

    public ArrayList<Integer> getVideoDuration() {
        return videoDuration;
    }

    public ArrayList<Integer> getStartTime() {
        return startTime;
    }

    public ArrayList<Boolean> getVideoHasAudio() {
        return videoHasAudio;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public int getXpos(int i) {
        return ((Integer)left.get(i)).intValue();
    }

    public int getYpos(int i) {
        return ((Integer)top.get(i)).intValue();
    }

    public void setBitmapHeight(ArrayList<Integer> arraylist) {
        bitmapHeight = arraylist;
    }

    public void setBitmapWidth(ArrayList<Integer> arraylist) {
        bitmapWidth = arraylist;
    }

    public void setBorderColor(String s) {
        borderColor = s;
        Log.d("color", s);
    }

    public void setExtensions(ArrayList<String> arraylist) {
        extensions = arraylist;
    }

    public void setFrameHeight(ArrayList<Integer> arraylist) {
        frameHeight = arraylist;
    }

    public void setFrameWidth(ArrayList<Integer> arraylist) {
        frameWidth = arraylist;
    }

    public void setLeft(ArrayList<Integer> arraylist) {
        left = arraylist;
    }

    public void setLeftMargins(ArrayList<Integer> arraylist) {
        leftMargins = arraylist;
    }

    public void setRotations(ArrayList<Integer> arraylist) {
        rotations = arraylist;
    }

    public void setScaleHeight(int i) {
        scaleHeight = i;
        Log.d("scaleHeight", (new StringBuilder()).append(i).toString());
    }

    public void setScaleWidth(int i) {
        scaleWidth = i;
        Log.d("scaleWidth", (new StringBuilder()).append(i).toString());
    }

    public void setTop(ArrayList<Integer> arraylist) {
        top = arraylist;
    }

    public void setTopMargins(ArrayList<Integer> arraylist) {
        topMargins = arraylist;
    }

    public void setUrls(ArrayList<String> arraylist) {
        urls = arraylist;
    }

    public void setAngles(ArrayList<Float> arraylist) {
        angle = arraylist;
    }

    public void setVideoAudioVolumes(ArrayList<Float> arraylist) {
        videoAudioVolumes = arraylist;
    }

    public void setVideoDuration(ArrayList<Integer> arraylist) {
        videoDuration = arraylist;
    }

    public void setStartTime(ArrayList<Integer> arraylist) {
        startTime = arraylist;
    }

    public void setVideoHasAudio(ArrayList<Boolean> arraylist) {
        videoHasAudio = arraylist;
    }

    public void setVideoPath(String s) {
        videoPath = s;
    }

    public boolean videoHasAudio(int i) {
        return ((Boolean)videoHasAudio.get(i)).booleanValue();
    }
}
