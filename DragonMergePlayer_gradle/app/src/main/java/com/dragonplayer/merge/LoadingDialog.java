package com.dragonplayer.merge;
  
 
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

public final class LoadingDialog extends Dialog {  
      
    
    

    
    public  LoadingDialog(Context context) {  
        super(context);  
        
        // TODO Auto-generated constructor stub  
    }  
      
    @Override  
    public void onCreate( Bundle $savedInstanceState ) {  
        super.onCreate( $savedInstanceState ) ;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView( R.layout.loadingdlg ) ;  
        
        
    }  
    
   
      
   

}  
