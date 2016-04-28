package com.dragonplayer.merge.fragment;

 
import android.os.Bundle;
import android.support.v4.app.Fragment;
 
import android.view.*;
 
 
import com.dragonplayer.merge.R;
 
import java.lang.reflect.Field;
 
/**
 * 
 * @author About this app
 *
 */
public class AboutFragment extends Fragment {

 
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
 
		
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);    		 
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.about_fragment, viewgroup, false);
        
 
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
     
}
