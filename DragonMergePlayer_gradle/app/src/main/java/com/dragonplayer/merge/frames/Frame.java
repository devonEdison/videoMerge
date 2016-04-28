package com.dragonplayer.merge.frames;

import java.util.ArrayList;

public class Frame {

	public static final int FRAMETYPEIMAGE = 1;
	public static final int FRAMETYPEVIDEO = 2;
	public static final int FRAMEUNLOCK = 0;
	public static final int FRAMELOCK = 1;
	public static final int FRAMEVERTICAL = 0;
	public static final int FRAMEHORIZONTAL = 1;
	
    private int frame_id;
    private int height;
    private ArrayList<FramePart> parts = new ArrayList<FramePart>();
    private int width;
    private String framename;
    private int frametype; // 1:image, 2:video
    private int color;
    private int lockflag; // 0:unlock, 1:lock
    private int framedirection; // 0:vertical, 1:horizontal

    public Frame() {
    }

    public Frame(int frmId, int w, int h, ArrayList<FramePart> partList, int type, String name, int clr, int lflag, int fdir) {
        frame_id = frmId;
        width = w;
        height = h;
        parts = partList;
        framename = name;
        frametype = type;
        color = clr;
        lockflag = clr;
        framedirection = fdir;
    }

    public void addPart(FramePart framepart) {
        parts.add(framepart);
    }

    public int getFrame_id() {
        return frame_id;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<FramePart> getParts() {
        return parts;
    }

    public int getWidth() {
        return width;
    }

    public int getColor() {
        return color;
    }

    public int getLockFlag() {
        return lockflag;
    }

    public int getFrameDirection() {
        return framedirection;
    }

    public int getFrameType() {
        return frametype;
    }
    
    public String getFrameName() {
        return framename;
    }

    public void setFrame_id(int id) {
        frame_id = id;
    }

    public void setHeight(int h) {
        height = h;
    }

    public void setParts(ArrayList<FramePart> partList) {
        parts = partList;
    }

    public void setWidth(int w) {
        width = w;
    }

    public void setColor(int clr) {
        color = clr;
    }

    public void setLockFlag(int flag) {
        lockflag = flag;
    }

    public void setFrameDirection(int fdir) {
        framedirection = fdir;
    }

    public void setFrameType(int t) {
        frametype = t;
    }

    public void setFrameName(String name) {
        framename = name;
    }
}
