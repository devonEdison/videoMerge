package com.dragonplayer.merge.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.adapter.ProjectListAdapter;
import com.dragonplayer.merge.frames.Frames;
import com.dragonplayer.merge.frames.ProjectFiles;

import java.io.File;
import java.lang.reflect.Field;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Iterator;

public class HomeFragment extends BaseFragment {

    private ProjectListAdapter adapter;
    private ProjectFiles projectfiles;
    private int projectsCount;
    private int imagesPerPage;
    private int pageNr;
    private RadioGroup radioGr;
    private ViewPager viewPager;
    
    public HomeFragment() {
        imagesPerPage = 0;
        projectsCount = 1;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }
    
    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.home_fragment, viewgroup, false);
        initBottomTab(view, TABLIST);
        
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        radioGr = (RadioGroup)view.findViewById(R.id.radioGroup1);
        
        projectfiles = ProjectFiles.newInstance(getActivity());
        imagesPerPage = 0;
        projectsCount = projectfiles.getFileCount();
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int i = (int)((0.84999999999999998D * (double)viewPager.getWidth()) / 2D);
                if(i == 0)
                    return;
                
                if(i % 2 != 0)
                    i++;
                
                int itemCount = 2 * (int)((0.95000000000000002D * (double)viewPager.getHeight()) / (double)i);
                
                Log.d("items", (new StringBuilder(" ")).append(itemCount).toString());
                
                imagesPerPage = itemCount;
                
                if(imagesPerPage == 0)
                    imagesPerPage = 1;
                
                Log.d("itemsRefresh", (new StringBuilder(" ")).append(itemCount).toString());
                
                pageNr = projectsCount / imagesPerPage;
                
                if(projectsCount % imagesPerPage != 0) {
                    HomeFragment homefragment = HomeFragment.this;
                    homefragment.pageNr = 1 + homefragment.pageNr;
                }
                
                adapter = new ProjectListAdapter(getChildFragmentManager(), pageNr, imagesPerPage, projectsCount, i, 
                		((int)((0.95000000000000002D * (double)viewPager.getHeight()) / (double)2)), projectfiles);
                
                viewPager.setAdapter(adapter);
                radioGr.removeAllViews();
                radioGr.setDividerPadding(3);
                
                for (int k = 0; k < pageNr; k++) { 
                    RadioButton radiobutton = new RadioButton(getActivity().getApplicationContext());
                    radiobutton.setId(k);
                    radiobutton.setHeight(22);
                    radiobutton.setWidth(22);
                    radiobutton.setPadding(4, 4, 4, 4);
                    radiobutton.setBackgroundResource(0);
                    radiobutton.setButtonDrawable(R.drawable.radio_button);
                    radiobutton.setChecked(false);
                    radiobutton.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

                        public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
                            if(flag)
                                viewPager.setCurrentItem(compoundbutton.getId());
                        }
                    });
                    
                    radioGr.addView(radiobutton);
                }

                ((RadioButton)radioGr.getChildAt(0)).setChecked(true);
                viewPager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {

                    public void onPageScrollStateChanged(int i) {
                    }

                    public void onPageScrolled(int i, float f, int j) {
                    }

                    public void onPageSelected(int i) {
                        ((RadioButton)radioGr.getChildAt(i)).setChecked(true);
                    }
                });
            }
        });
        
        return view;
    }

    public void refreshPage(int nCurItem) {
		File file = projectfiles.getFileNameWithIndex(nCurItem);
        int curPos = viewPager.getCurrentItem();
		
		if (file.exists())
			file.delete();

		projectfiles.removeIndex(nCurItem);
		projectsCount--;
		Log.e("remove", "removepos="+projectsCount);
        adapter.setProjectCount(projectsCount);
		
		if (projectsCount % imagesPerPage == 0) {
			int pages = projectsCount / imagesPerPage;
			adapter.setPageCount(pages);
	        //adapter.notifyDataSetChanged();
			viewPager.setAdapter(null);
			viewPager.setAdapter(adapter);
	        radioGr.removeViewAt(radioGr.getChildCount() - 1);
	        
	        if (curPos == adapter.getPageCount())
	        	viewPager.setCurrentItem(adapter.getPageCount() - 1);
		}
		else {
			GridFragment fragment = adapter.getFragmentbyIndex(adapter.getCount() - 1);

			if (fragment != null && fragment.getAdapter() != null) {
				Log.e("fragment.getAdapter().getCount()", "fragment.getAdapter().getCount()="+fragment.getAdapter().getCount());
				fragment.getAdapter().setCount(fragment.getAdapter().getCount() - 1);
			}
		}
		
//		for (int i = 0; i < adapter.getCount(); i++) {
//			GridFragment fragment = adapter.getFragmentbyIndex(i);
//
//			if (fragment != null && fragment.getAdapter() != null) 
//				fragment.getAdapter().notifyDataSetChanged();
//		}
		
        Iterator iterator = getChildFragmentManager().getFragments().iterator();
        
        while (iterator.hasNext()) {
        	GridFragment fragment = (GridFragment)iterator.next();
        	Log.e("iterator.hashCode()", "iterator.hashCode()="+iterator.hashCode());
        	
        	if (fragment != null && fragment.getAdapter() != null) 
        		fragment.getAdapter().notifyDataSetChanged();
        }
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
}
