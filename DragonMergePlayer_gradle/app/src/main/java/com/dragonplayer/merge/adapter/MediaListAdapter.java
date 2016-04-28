package com.dragonplayer.merge.adapter;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.FramesLayout;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.frames.FrameView.SetBounds;
import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.Utils;

public class MediaListAdapter extends BaseAdapter {

    private Context context;
    private ProjectFiles projectfiles;
    private int itemHeight;
    private int itemWidth;

    public MediaListAdapter(Context contxt, int itemH, ProjectFiles frm) {
        context = contxt;
        itemHeight = itemH;
        projectfiles = frm;
    
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        itemWidth = itemHeight * screenWidth / screenHeight;
    }

    public int getCount() {
        return projectfiles.getFileCount() - 1;
    }

    public Object getItem(int i) {
        return Integer.valueOf(i);
    }

    public long getItemId(int i) {
        return (long)i;
    }

    private String getDateFromMilliSec(String milli) {
    	String strTime;
    	Date date = new Date();date.setTime(Long.valueOf(milli));
    	strTime = (date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes();
    	
    	return strTime;
    }
    
    public View getView(int pos, View view, ViewGroup viewgroup) {
        final int realPos = pos;
        File prjFile = projectfiles.getFileNameWithIndex(realPos + 1);
        
        RelativeLayout projectView = (RelativeLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.project_view, null);
        
        ImageButton btnRemove = (ImageButton) projectView.findViewById(R.id.btnremove);
        ImageView imgProjectLogo = (ImageView) projectView.findViewById(R.id.imgprojectlogo);
        TextView txtProjectDate = (TextView) projectView.findViewById(R.id.txtprojectdate);

        imgProjectLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)context).onClickUploadInfo((projectfiles.getFileNameWithIndex(realPos+1).getAbsolutePath()));
			}
        	
        });

    	btnRemove.setVisibility(View.INVISIBLE);
        txtProjectDate.setText(getDateFromMilliSec(prjFile.getName().substring(0, prjFile.getName().length() - 4)));
        
        if (prjFile.getName().contains("mp4")) {
        	File file = Utils.writeToFile(BitmapUtil.videoFrame(prjFile.getAbsolutePath(), 0L));
        	((MainActivity)context).getManager().displayThumbImage(file.getAbsolutePath(), imgProjectLogo, itemWidth, itemHeight);
        	file.delete();
        }
        else {
            ((MainActivity)context).getManager().displayThumbImage(prjFile.getAbsolutePath(), imgProjectLogo, itemWidth, itemHeight);
        }
        
        projectView.setDescendantFocusability(0x20000);
        projectView.setLayoutParams(new android.widget.AbsListView.LayoutParams(itemHeight, itemHeight));
        
        return projectView;
    }
}
