package com.dragonplayer.merge.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.*;

import com.dragonplayer.merge.fragment.GridFragment;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;

public class ProjectListAdapter extends FragmentStatePagerAdapter {

	private ProjectFiles projectFiles;
    private int projectCount;
    private int imagesPerPage;
    private int itemWidth;
    private int itemHeight;
    private int pageCount;
    private HashMap fragmentList = new HashMap();
    
    public ProjectListAdapter(FragmentManager fragmentmanager, int pgCnt, int imgPerPg, int frmCount, int itemW, int itemH, ProjectFiles frm) {
        super(fragmentmanager);
        fragmentmanager.popBackStack(null, 1);
        pageCount = pgCnt;
        imagesPerPage = imgPerPg;
        projectCount = frmCount;
        itemWidth = itemW;
        itemHeight = itemH;
        projectFiles = frm;
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
            int k = projectCount % imagesPerPage;
            if(k > 0)
                imgPerPage = k;
        }
        
        bundle.putInt("imageCount", imgPerPage);
        bundle.putInt("itemSizeW", itemWidth);
        bundle.putInt("itemSizeH", itemHeight);
        GridFragment gridfragment = new GridFragment();
        gridfragment.setArguments(bundle);
        gridfragment.setProjectFiles(projectFiles);
        
        if (fragmentList.containsKey(i)) {
        	fragmentList.remove(i);
        	fragmentList.put(i, gridfragment);
        }
        else {
        	fragmentList.put(i, gridfragment);
        }
        
        return gridfragment;
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
    
    public GridFragment getFragmentbyIndex(int index) {
    	if (fragmentList.containsKey(index)) {
    		return (GridFragment)fragmentList.get(index);
    	}
    	
    	return null;
    }
    
    public ProjectFiles getProjectFiles() {
        return projectFiles;
    }

    public void setProjectFiles(ProjectFiles cnt) {
    	projectFiles = cnt;
    }

    public int getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(int cnt) {
        projectCount = cnt;
    }
}
