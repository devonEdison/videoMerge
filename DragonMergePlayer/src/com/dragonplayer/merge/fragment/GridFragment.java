package com.dragonplayer.merge.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.ProjectSubListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;
import com.dragonplayer.merge.utils.MLog;

public class GridFragment extends Fragment {

    private int firstImagePos;
    private Frames frames;
    private GridView gridView;
    private int imageCount;
    private int itemSizeW;
    private int itemSizeH;
    private int pageNr;
    private ProjectFiles projectFiles;
    private ProjectSubListAdapter adapter;
    
    public GridFragment() {
    }

    public Frames getFrames() {
        return frames;
    }

    public void onActivityCreated(Bundle bundle) {
        gridView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterview, View view, int pos, long l) {
                ((MainActivity)getActivity()).SelectedFramePage(pos + firstImagePos);
            }
        });
        MLog.d(this, "onActivityCreated");
        super.onActivityCreated(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle bundle1 = getArguments();
        pageNr = bundle1.getInt("number");
        firstImagePos = bundle1.getInt("firstImage");
        imageCount = bundle1.getInt("imageCount");
        itemSizeW = bundle1.getInt("itemSizeW");
        itemSizeH = bundle1.getInt("itemSizeH");
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.grid_view, viewgroup, false);
        adapter = new ProjectSubListAdapter(getActivity(), imageCount, firstImagePos, itemSizeW, itemSizeH, projectFiles);
        gridView = (GridView)view.findViewById(R.id.gridFrames);
        gridView.setAdapter(adapter);
        gridView.setTag(Integer.valueOf(pageNr));
        return view;
    }

    public void setProjectFiles(ProjectFiles frm) {
    	projectFiles = frm;
    }
    
    public ProjectSubListAdapter getAdapter() {
    	return adapter;
    }
}
