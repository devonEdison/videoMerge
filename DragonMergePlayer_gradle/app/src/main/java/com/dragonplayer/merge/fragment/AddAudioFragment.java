package com.dragonplayer.merge.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.dragonplayer.merge.FacebookActivity;
import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.frames.*;
import com.dragonplayer.merge.utils.*;
import com.dragonplayer.merge.utils.Constants.Extra;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.lang.Runnable;

import org.json.*;

public class AddAudioFragment extends Fragment {
    
    private ImageButton btnPlay1;
    private ImageButton btnPlay2;
    private ImageButton btnPlay3;
    private ImageButton btnPlay4;
    private ImageButton btnPlay5;
    private ImageButton btnPlay6;
    private ImageButton btnPlay7;
    private ImageButton btnPlay8;
    
    private RadioButton btnRadio1;
    private RadioButton btnRadio2;
    private RadioButton btnRadio3;
    private RadioButton btnRadio4;
    private RadioButton btnRadio5;
    private RadioButton btnRadio6;
    private RadioButton btnRadio7;
    private RadioButton btnRadio8;
    
    private String[] audioPath = {"", 
    							  "/data/data/com.dragonplayer.merge/01.mp3", 
    							  "/data/data/com.dragonplayer.merge/02.mp3", 
    							  "/data/data/com.dragonplayer.merge/03.mp3",
    							  "/data/data/com.dragonplayer.merge/04.mp3",
    							  "/data/data/com.dragonplayer.merge/05.mp3",
    							  "/data/data/com.dragonplayer.merge/06.mp3", 
    							  "/data/data/com.dragonplayer.merge/07.mp3" };

    private int nSelectedAudio;
    private int nPlayAudio;
    private MediaPlayer player;
    
    public AddAudioFragment() {
        nSelectedAudio = 0;
        nPlayAudio = -1;
        player = null;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onActivityResult(int i, int j, Intent intent) {
        super.onActivityResult(i, j, intent);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        audioPath[0] = getArguments().getString("videoPath");
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View view = layoutinflater.inflate(R.layout.addaudio_fragment, null);

        btnRadio1 = (RadioButton) view.findViewById(R.id.radioButton1);
        btnRadio1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudio(0);
			}
        	
        });
        
        btnRadio2 = (RadioButton) view.findViewById(R.id.radioButton2);
        btnRadio2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudio(1);
			}
        	
        });
        
        btnRadio3 = (RadioButton) view.findViewById(R.id.radioButton3);
        btnRadio3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudio(2);
			}
        	
        });

        btnRadio4 = (RadioButton) view.findViewById(R.id.radioButton4);
        btnRadio4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudio(3);
			}
        	
        });

        btnRadio5 = (RadioButton) view.findViewById(R.id.radioButton5);
        btnRadio5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudio(4);
			}
        	
        });

        btnRadio6 = (RadioButton) view.findViewById(R.id.radioButton6);
        btnRadio6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudio(5);
			}
        	
        });
        
        btnRadio7 = (RadioButton) view.findViewById(R.id.radioButton7);
        btnRadio7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudio(6);
			}
        	
        });

        btnRadio8 = (RadioButton) view.findViewById(R.id.radioButton8);
        btnRadio8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudio(7);
			}
        	
        });

        btnPlay1 = (ImageButton) view.findViewById(R.id.imgButton1);
        btnPlay1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(0);
			}
        	
        });
        

        btnPlay2 = (ImageButton) view.findViewById(R.id.imgButton2);
        btnPlay2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(1);
			}
        	
        });

        btnPlay3 = (ImageButton) view.findViewById(R.id.imgButton3);
        btnPlay3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(2);
			}
        	
        });

        btnPlay4 = (ImageButton) view.findViewById(R.id.imgButton4);
        btnPlay4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(3);
			}
        	
        });

        btnPlay5 = (ImageButton) view.findViewById(R.id.imgButton5);
        btnPlay5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(4);
			}
        	
        });

        btnPlay6 = (ImageButton) view.findViewById(R.id.imgButton6);
        btnPlay6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(5);
			}
        	
        });

        btnPlay7 = (ImageButton) view.findViewById(R.id.imgButton7);
        btnPlay7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(6);
			}
        	
        });

        btnPlay8 = (ImageButton) view.findViewById(R.id.imgButton8);
        btnPlay8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(7);
			}
        	
        });

        player = new MediaPlayer();
        
        player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				startPlay(nPlayAudio); 
			}
        	
        });
        
        player.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				return false;
			}
        	
        });

        selectAudio(0);
        
        return view;
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }
 
    public void onDestroy() {
        super.onDestroy();
         
        player.stop();
        player.reset();
        player.release();

        player = null;
    }
    
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }
    
    public void playAudio(int nIndex) {
    	
    	if (nPlayAudio == -1) {
    		startPlay(nIndex);
    		return;
    	}
    	
    	if (nPlayAudio == nIndex) {
    		stopPlay(nIndex);
    		return;
    	}
    	
    	stopPlay(nPlayAudio);
    	startPlay(nIndex);
    }
    
    public void selectAudio(int nIndex) {
    	btnRadio1.setChecked(false);
    	btnRadio2.setChecked(false);
    	btnRadio3.setChecked(false);
    	btnRadio4.setChecked(false);
    	btnRadio5.setChecked(false);
    	btnRadio6.setChecked(false);
    	btnRadio7.setChecked(false);
    	btnRadio8.setChecked(false);
    	
    	switch (nIndex) {
    	case 0:
    		btnRadio1.setChecked(true);
    		break;
    	case 1:
    		btnRadio2.setChecked(true);
    		break;
    	case 2:
    		btnRadio3.setChecked(true);
    		break;
    	case 3:
    		btnRadio4.setChecked(true);
    		break;
    	case 4:
    		btnRadio5.setChecked(true);
    		break;
    	case 5:
    		btnRadio6.setChecked(true);
    		break;
    	case 6:
    		btnRadio7.setChecked(true);
    		break;
    	case 7:
    		btnRadio8.setChecked(true);
    		break;
    	}
    	
    	nSelectedAudio = nIndex;
    }

    private void startPlay(int nIndex) {
    	nPlayAudio = nIndex;
    	
        player.stop();
        player.reset();
         
        try {
                player.setDataSource(audioPath[nIndex]);
                player.prepare();
                player.start();
        } catch (IllegalArgumentException e) {
                e.printStackTrace();
        } catch (IllegalStateException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }

    	switch (nIndex) {
    	case 0:
    		btnPlay1.setSelected(true);
    		break;
    	case 1:
    		btnPlay2.setSelected(true);
    		break;
    	case 2:
    		btnPlay3.setSelected(true);
    		break;
    	case 3:
    		btnPlay4.setSelected(true);
    		break;
    	case 4:
    		btnPlay5.setSelected(true);
    		break;
    	case 5:
    		btnPlay6.setSelected(true);
    		break;
    	case 6:
    		btnPlay7.setSelected(true);
    		break;
    	case 7:
    		btnPlay8.setSelected(true);
    		break;
    	}
    }
    
    private void stopPlay(int nIndex) {
    	nPlayAudio = -1;
        player.stop();
        player.reset();

    	switch (nIndex) {
    	case 0:
    		btnPlay1.setSelected(false);
    		break;
    	case 1:
    		btnPlay2.setSelected(false);
    		break;
    	case 2:
    		btnPlay3.setSelected(false);
    		break;
    	case 3:
    		btnPlay4.setSelected(false);
    		break;
    	case 4:
    		btnPlay5.setSelected(false);
    		break;
    	case 5:
    		btnPlay6.setSelected(false);
    		break;
    	case 6:
    		btnPlay7.setSelected(false);
    		break;
    	case 7:
    		btnPlay8.setSelected(false);
    		break;
    	}
    }
    
    public String getAudio() {
    	if (nSelectedAudio == 0)
    		return "";
    	
    	return audioPath[nSelectedAudio];
    }
}
