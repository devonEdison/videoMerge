package com.dragonplayer.merge.fragment;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.MediaListAdapter;
import com.dragonplayer.merge.adapter.ProjectSubListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;

public class UploadSelectMediaFragment extends Fragment {

    private GridView gridView;
    private ProjectFiles projectFiles;
    private MediaListAdapter adapter;
    private int itemSize = 0;
    
    public UploadSelectMediaFragment() {
    	projectFiles = ProjectFiles.newInstance(getActivity()); 
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.grid_view, viewgroup, false);
        gridView = (GridView)view.findViewById(R.id.gridFrames);
        
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
            	gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            	itemSize = (int)((0.84999999999999998D * (double)gridView.getWidth()) / 2D);
            	adapter = new MediaListAdapter(getActivity(), itemSize, projectFiles);
            	gridView.setAdapter(adapter);
            }
        });
        
        return view;
    }

    public void onDetach() {
    	
        try {
            Field field = Fragment.class.getDeclaredField("mChildFragmentManager");
            field.setAccessible(true);
            field.set(this, null);
        }
        catch(NoSuchFieldException nosuchfieldexception) {
            throw new RuntimeException(nosuchfieldexception);
        }
        catch(IllegalAccessException illegalaccessexception) {
            throw new RuntimeException(illegalaccessexception);
        }
        
        super.onDetach();
    }

    public void setProjectFiles(ProjectFiles frm) {
    	projectFiles = frm;
    }

    public void setItemSize(int size) {
    	itemSize = size;
    }
}
