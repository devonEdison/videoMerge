package com.dragonplayer.merge.frames;

import android.graphics.Point;

public class FramePart {

    private Point end;
    private Point start;
    private boolean bVideoFrame;

    public FramePart(int x1, int y1, int x2, int y2) {
        start = new Point(x1, y1);
        end = new Point(x2, y2);
        bVideoFrame = false;
    }

    public FramePart(int x1, int y1, int x2, int y2, boolean flag) {
        start = new Point(x1, y1);
        end = new Point(x2, y2);
        bVideoFrame = flag;
    }

    public Point getEnd() {
        return end;
    }

    public Point getStart() {
        return start;
    }
    
    public boolean isVideoFrame() {
    	return bVideoFrame;
    }

    public void setEnd(Point point) {
        end = point;
    }

    public void setStart(Point point) {
        start = point;
    }

    public void setVideoFrame(boolean bvideo) {
    	bVideoFrame = bvideo;
    }
}
