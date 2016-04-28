package com.dragonplayer.merge.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.ProjectSubListAdapter;
import com.dragonplayer.merge.adapter.FrameSubListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;

public class FrameListFragment extends Fragment {

    private int firstImagePos;
    private Frames frames;
    private GridView gridView;
    private int imageCount;
    private int itemSize;
    private int pageNr;
    private int itemtype;
    
    public FrameListFragment() {
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
        
        super.onActivityCreated(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle bundle1 = getArguments();
        pageNr = bundle1.getInt("number");
        firstImagePos = bundle1.getInt("firstImage");
        imageCount = bundle1.getInt("imageCount");
        itemSize = bundle1.getInt("itemSize");
        itemtype = bundle1.getInt("itemType");
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.grid_view, viewgroup, false);
        gridView = (GridView)view.findViewById(R.id.gridFrames);
        gridView.setNumColumns(3);
        gridView.setAdapter(new FrameSubListAdapter(getActivity(), imageCount, firstImagePos, itemSize, frames, itemtype));
        gridView.setTag(Integer.valueOf(pageNr));
        return view;
    }

    public void setProjectFiles(Frames frm) {
    	frames = frm;
    }
}
