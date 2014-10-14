package com.dragonplayer.merge.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.*;
import android.util.Log;

import com.dragonplayer.merge.fragment.FrameListFragment;
import com.dragonplayer.merge.fragment.GridFragment;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;

public class FrameListAdapter extends FragmentStatePagerAdapter {

    private Frames frames;
    private int framesCount;
    private int imagesPerPage;
    private int itemHeight;
    private int pageCount;
    private int itemtype;
    
    public FrameListAdapter(FragmentManager fragmentmanager, int pgCnt, int imgPerPg, int frmCount, int itemH, Frames frm, int flag) {
        super(fragmentmanager);
        fragmentmanager.popBackStack(null, 1);
        pageCount = pgCnt;
        imagesPerPage = imgPerPg;
        framesCount = frmCount;
        itemHeight = itemH;
        frames = frm;
        itemtype = flag;
    }

    public int getCount() {
        return pageCount;
    }

    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("number", i);
        bundle.putInt("firstImage", i * imagesPerPage);
        int imgPerPage = imagesPerPage;
        
        if(i == -1 + pageCount) {
            int k = framesCount % imagesPerPage;
            if(k > 0)
                imgPerPage = k;
        }
        
        bundle.putInt("imageCount", imgPerPage);
        bundle.putInt("itemSize", itemHeight);
        bundle.putInt("itemType", itemtype);
        FrameListFragment framefragment = new FrameListFragment();
        framefragment.setArguments(bundle);
        framefragment.setProjectFiles(frames);
        return framefragment;
    }

    public int getPageCount() {
        return pageCount;
    }

    public Parcelable saveState() {
        return null;
    }

    public void setPageCount(int cnt) {
        pageCount = cnt;
    }
}
