package com.dragonplayer.merge.adapter;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.dragonplayer.merge.frames.Frame;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.FramesLayout;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.frames.FrameView.SetBounds;
import com.dragonplayer.merge.utils.BannerData;
import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.DlgWindow1;
import com.dragonplayer.merge.utils.DlgWindow4;
import com.dragonplayer.merge.utils.Utils;
import com.dragonplayer.merge.utils.WebService;

public class ProjectSubListAdapter extends BaseAdapter {

    private Context context;
    private int firstFramePosition;
    private int frameCount;
    private ProjectFiles projectfiles;
    private int itemHeight;
    private int itemWidth;
    Dialog mDialog;

    public ProjectSubListAdapter(Context contxt, int frmCount, int firstfrmPos, int itemW, int itemH, ProjectFiles frm) {
        context = contxt;
        frameCount = frmCount;
        itemWidth = itemW;
        itemHeight = itemH;
        firstFramePosition = firstfrmPos;
        projectfiles = frm;
    
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        itemWidth = itemHeight * screenWidth / screenHeight;
    }

    public void setCount(int i) {
    	frameCount = i;
    }
    
    public int getCount() {
        return frameCount;
    }

    public int getTotalCount() {
    	return firstFramePosition + frameCount; 
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
        final int realPos = pos + firstFramePosition;
        File prjFile = projectfiles.getFileNameWithIndex(realPos);
        RelativeLayout projectView = (RelativeLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.project_view, null);
        
        ImageButton btnRemove = (ImageButton) projectView.findViewById(R.id.btnremove);
        ImageView imgProjectLogo = (ImageView) projectView.findViewById(R.id.imgprojectlogo);
        TextView txtProjectDate = (TextView) projectView.findViewById(R.id.txtprojectdate);

        btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				if (projectfiles.getFileCount() <= firstFramePosition + frameCount)
//					frameCount--;
				
				mDialog = new DlgWindow1(context, R.style.CustomDialog, "你確定要刪除此拼貼嗎?", "取消", "確定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
					}
	            }, new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((MainActivity)context).refreshPage(realPos);
						mDialog.dismiss();
					}
	            });
				mDialog.show();
			}
        	
        });
        
        imgProjectLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (realPos == 0) {
					((MainActivity)context).onClickNewProject();
				}
				else {
					((MainActivity)context).onClickViewProject(projectfiles.getFileNameWithIndex(realPos).getAbsolutePath());
				}
			}
        	
        });

        if (realPos == 0 ) {

        	btnRemove.setVisibility(View.INVISIBLE);
            txtProjectDate.setText(getDateFromMilliSec(String.valueOf(Calendar.getInstance().getTimeInMillis())));
            ((MainActivity)context).getManager().displayNewProjectThumbImage(context, R.drawable.ic_new_img, imgProjectLogo, itemWidth, itemHeight, 0xFFF0E5D4);
        }
        else {
            int nDirection;
            if (prjFile.getName().contains("mp4")) {
            	File file = Utils.writeToFile(BitmapUtil.videoFrame(prjFile.getAbsolutePath(), 0L));
            	nDirection = Utils.getBitmapDirection(file.getAbsolutePath());
            	file.delete();
            }
            else {
            	nDirection = Utils.getBitmapDirection(prjFile.getAbsolutePath());
            }

            btnRemove.setVisibility(View.VISIBLE);
        	
            txtProjectDate.setText(getDateFromMilliSec(prjFile.getName().substring(0, prjFile.getName().length() - 4)));
            
            if (prjFile.getName().contains("mp4")) {
            	File file = Utils.writeToFile(BitmapUtil.videoFrame(prjFile.getAbsolutePath(), 0L));
            	
            	if (nDirection == Frame.FRAMEVERTICAL)
            		((MainActivity)context).getManager().displayThumbImage(file.getAbsolutePath(), imgProjectLogo, itemWidth, itemHeight);
            	else
            		((MainActivity)context).getManager().displayThumbImage(file.getAbsolutePath(), imgProjectLogo, itemHeight, itemWidth);
            	
            	file.delete();
            }
            else {
            	if (nDirection == Frame.FRAMEVERTICAL)
            		((MainActivity)context).getManager().displayThumbImage(prjFile.getAbsolutePath(), imgProjectLogo, itemWidth, itemHeight);
            	else
            		((MainActivity)context).getManager().displayThumbImage(prjFile.getAbsolutePath(), imgProjectLogo, itemHeight, itemWidth);
            }

            if (nDirection == Frame.FRAMEVERTICAL) {
	            RelativeLayout.LayoutParams btnremoveParams = (RelativeLayout.LayoutParams) btnRemove.getLayoutParams();
	            btnremoveParams.leftMargin = (itemHeight - itemWidth) / 2;
	            btnremoveParams.topMargin = 0;
	            btnRemove.setLayoutParams(btnremoveParams);
            }
            else {
	            RelativeLayout.LayoutParams btnremoveParams = (RelativeLayout.LayoutParams) btnRemove.getLayoutParams();
	            btnremoveParams.leftMargin = 0;
	            btnremoveParams.topMargin = (itemHeight - itemWidth - 40) / 2;
	            btnRemove.setLayoutParams(btnremoveParams);
            }
            
        }

        projectView.setDescendantFocusability(0x20000);
        projectView.setLayoutParams(new android.widget.AbsListView.LayoutParams(itemHeight, itemHeight));
        
        return projectView;
    }

	private class AsyncCallLoadImage extends AsyncTask<String, Void, Void> {
		
		File prjFile;
		ImageButton btnRemove;
		ImageView imgProjectLogo;
		
		public AsyncCallLoadImage(File file, ImageButton btn, ImageView imgView) {
			prjFile = file;
			btnRemove = btn;
			imgProjectLogo = imgView;
		}
		
		@Override
		protected Void doInBackground(String... params) {
            int nDirection;
            if (prjFile.getName().contains("mp4")) {
            	File file = Utils.writeToFile(BitmapUtil.videoFrame(prjFile.getAbsolutePath(), 0L));
            	nDirection = Utils.getBitmapDirection(file.getAbsolutePath());
            	file.delete();
            }
            else {
            	nDirection = Utils.getBitmapDirection(prjFile.getAbsolutePath());
            }

            if (prjFile.getName().contains("mp4")) {
            	File file = Utils.writeToFile(BitmapUtil.videoFrame(prjFile.getAbsolutePath(), 0L));
            	
            	if (nDirection == Frame.FRAMEVERTICAL)
            		((MainActivity)context).getManager().displayThumbImage(file.getAbsolutePath(), imgProjectLogo, itemWidth, itemHeight);
            	else
            		((MainActivity)context).getManager().displayThumbImage(file.getAbsolutePath(), imgProjectLogo, itemHeight, itemWidth);
            	
            	file.delete();
            }
            else {
            	if (nDirection == Frame.FRAMEVERTICAL)
            		((MainActivity)context).getManager().displayThumbImage(prjFile.getAbsolutePath(), imgProjectLogo, itemWidth, itemHeight);
            	else
            		((MainActivity)context).getManager().displayThumbImage(prjFile.getAbsolutePath(), imgProjectLogo, itemHeight, itemWidth);
            }

            if (nDirection == Frame.FRAMEVERTICAL) {
	            RelativeLayout.LayoutParams btnremoveParams = (RelativeLayout.LayoutParams) btnRemove.getLayoutParams();
	            btnremoveParams.leftMargin = (itemHeight - itemWidth) / 2;
	            btnremoveParams.topMargin = 0;
	            btnRemove.setLayoutParams(btnremoveParams);
            }
            else {
	            RelativeLayout.LayoutParams btnremoveParams = (RelativeLayout.LayoutParams) btnRemove.getLayoutParams();
	            btnremoveParams.leftMargin = 0;
	            btnremoveParams.topMargin = (itemHeight - itemWidth - 40) / 2;
	            btnRemove.setLayoutParams(btnremoveParams);
            }

            btnRemove.setVisibility(View.VISIBLE);

            return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}
}
