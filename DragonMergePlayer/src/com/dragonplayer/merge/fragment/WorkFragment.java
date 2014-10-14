package com.dragonplayer.merge.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.dragonplayer.merge.MainActivity;
import com.dragonplayer.merge.R;
import com.dragonplayer.merge.TextInputActivity;
import com.dragonplayer.merge.adapter.FrameListAdapter;
import com.dragonplayer.merge.adapter.ProjectListAdapter;
import com.dragonplayer.merge.frames.*;
import com.dragonplayer.merge.utils.BitmapUtil;
import com.dragonplayer.merge.utils.DlgWindow1;
import com.dragonplayer.merge.utils.DlgWindow2;
import com.dragonplayer.merge.utils.DlgWindow4;
import com.dragonplayer.merge.utils.DlgWindow5;
import com.dragonplayer.merge.utils.DlgWindow6;
import com.dragonplayer.merge.utils.DlgWindow7;
import com.dragonplayer.merge.utils.DlgWindow8;
import com.dragonplayer.merge.utils.Utils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkFragment extends Fragment
{
    private static final int RECORD_VIDEO = 44;
    private static final int SELECT_PHOTO = 11;
    private static final int SELECT_TRACK = 55;
    private static final int SELECT_VIDEO = 33;
    private static final int TAKE_PHOTO = 22;
    private static final int ADD_TEXT = 66;
    private static final int TRIM_VIDEO = 77;
	private static final int IMAGE_SHOOT = 88;
	private static final int EDIT_FROM_GOOGLE = 99;
    
    public static final int FRAMEORGWIDTH = 1080;
    public static final int FRAMEORGHEIGHT = 1920;
    
    private static String TAG = "Workfragment";
    
    static Uri selectedImageUri_google=null; 
    static Uri selectedImageUri=null;
	int GOOGLE=0;

	private Uri audioUri;
    private com.dragonplayer.merge.frames.FrameView.CorruptVideoError error;
    private Frame frame;
    private int frameID;
    private FramesLayout frameLayout;
    private float initialY;
    private boolean isAnimating;
    private String mBaseFolderPath;
    private Dialog optionsDialog;
    private final ArrayList order = new ArrayList();
    private Uri outputFileUri;
    private TextView selectedTrack;
    private LinearLayout takePhotoBtn;
    private ImageView upgrade;
    private Uri videoUri;
    private int nActivityResultValue = 0;
    private static String strActivityResultPath = "";
    private static int nStartTime = 0;
    private static int nEndTime = 0;
    
    private ImageButton btnFrameList;
    private ImageButton btnAddText;
    private RelativeLayout frameList;

    private int projectsCount1;
    private int projectsCount2;
    private int imagesPerPage;
    private int pageNr1;
    private int pageNr2;
    private RadioGroup radioGr1;
    private ViewPager viewPager1;
    private RadioGroup radioGr2;
    private ViewPager viewPager2;
    private Frames frames;
    private HorizontalListView curGallery;
    FrameListAdapter adapter1;
    FrameListAdapter adapter2;
    ImageAdapter adapter3;
    LinearLayout frameHolder;
    Dialog mDialog;
    boolean bNewFlag;
    String filePath;
    
    public WorkFragment() {
        initialY = 0.0F;
        isAnimating = false;
        bNewFlag = true;
    }

    private void startCameraIntent() {
        outputFileUri = Uri.fromFile(new File((new StringBuilder(String.valueOf(mBaseFolderPath))).append(File.separator).append("picture_").append(Calendar.getInstance().getTimeInMillis()).append(".jpg").toString()));
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", outputFileUri);
        
        try {
            startActivityForResult(intent, TAKE_PHOTO);
        }
        catch(ActivityNotFoundException activitynotfoundexception) {
            activitynotfoundexception.printStackTrace();
        }
        
        try {
            frameLayout.addBitmap(outputFileUri.getPath(), null);
        }
        catch(Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void startGalleryIntent() {
        ArrayList arraylist = new ArrayList();
        List list;
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        ResolveInfo resolveinfo;
        Intent intent3;

        intent.setType("image/*");
        list = getActivity().getPackageManager().queryIntentActivities(intent, 0);

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
	        resolveinfo = (ResolveInfo)iterator.next();
	        intent3 = new Intent("android.intent.action.GET_CONTENT");
	        intent3.setType("image/*");
	        
	        if(resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains("gallery") || resolveinfo.activityInfo.name.toLowerCase(Locale.US).contains("gallery") || resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains("album")) {
	            intent3.setPackage(resolveinfo.activityInfo.packageName);
	            arraylist.add(intent3);
	        }
        }

        if(arraylist.size() > 0) {
            Intent intent2 = Intent.createChooser((Intent)arraylist.remove(0), "Select Source");
            intent2.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[])arraylist.toArray(new Parcelable[0]));
            startActivityForResult(intent2, SELECT_PHOTO);
        } 
        else {
            Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
            intent1.setType("image/*");
            startActivityForResult(intent1, SELECT_PHOTO);
        }
    }

    private void startVideoIntent() {
        videoUri = Uri.fromFile(new File((new StringBuilder(String.valueOf(mBaseFolderPath))).append(File.separator).append("video_").append(Calendar.getInstance().getTimeInMillis()).append(".mp4").toString()));
        
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        intent.putExtra("android.intent.extra.durationLimit", 15);
        intent.putExtra("android.intent.extra.videoQuality", 1);
        intent.putExtra("output", videoUri);

        startActivityForResult(intent, RECORD_VIDEO);
    }

    private void startVideoPickerIntent() {
        ArrayList arraylist = new ArrayList();
        List list;
        ResolveInfo resolveinfo;

        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("video/*");
        list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        
        if(!list.isEmpty()) { 
	        Iterator iterator = list.iterator();
	        while (iterator.hasNext()) {
	            resolveinfo = (ResolveInfo)iterator.next();
	            intent = new Intent("android.intent.action.GET_CONTENT");
	            intent.setType("video/*");
	            
	            if(resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains("gallery") || resolveinfo.activityInfo.name.toLowerCase(Locale.US).contains("gallery") || resolveinfo.activityInfo.packageName.toLowerCase(Locale.US).contains("album")) {
	                intent.setPackage(resolveinfo.activityInfo.packageName);
	                arraylist.add(intent);
	            }
	        }
        }
        
        if(arraylist.size() > 0) {
            intent = Intent.createChooser((Intent)arraylist.remove(0), "Select Source");
            intent.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[])arraylist.toArray(new Parcelable[0]));
            startActivityForResult(intent, SELECT_VIDEO);
        } 
        else {
            intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("video/*");
            startActivityForResult(intent, SELECT_VIDEO);
        }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        nActivityResultValue = 0;

    	Utils.writeLogToFile("---------------Workspace ActivityResult---------------");
    	Utils.writeLogToFile("requestCode="+requestCode+":resultCode="+resultCode);
        
        super.onActivityResult(requestCode, resultCode, data);
    
//        if(resultCode != -1) 
//        	return;

        if(requestCode == SELECT_PHOTO && data != null) {
			selectedImageUri = data.getData();
			Utils.writeLogToFile("selectedImageUri="+selectedImageUri.getPath());
			//save file
			String url = data.getData().toString();
			Bitmap bitmap = null;
			InputStream is = null;
			Uri a = null;
			if (url.startsWith("content://com.google.android.apps.photos.content")) {
				try {
					is = getActivity().getContentResolver().openInputStream(Uri.parse(url));
					bitmap = BitmapFactory.decodeStream(is);
					a = getImageUri(getActivity(), bitmap);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//call edit from google
			if (url.startsWith("content://com.google.android.apps.photos.content") && a != null){
				Utils.writeLogToFile("selectedImageUri_google="+selectedImageUri_google.getPath());
				selectedImageUri_google = a;
				GOOGLE = 1;
				final Uri aa = a;
				mDialog = new DlgWindow2(getActivity(), R.style.CustomDialog, "請選擇編輯模式", "確定", "取消", 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
								callEdit(aa);
							}
						}, new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
								filePath = getRealPathFromURI(getActivity(), aa);
						        try {
						            frameLayout.addBitmap(filePath, error);
						        }
						        catch(Throwable throwable4) {
						            nActivityResultValue = SELECT_PHOTO;
						            strActivityResultPath = filePath;
						            
						            throwable4.printStackTrace();
						        }
							}
	            });
				mDialog.show();
			}else if (url.startsWith("content://com.google.android.apps.photos.content") && a == null){
				Toast.makeText(getActivity(), "please upload local file", Toast.LENGTH_LONG);
			}else if (selectedImageUri != null){
				selectedImageUri_google = null;
				GOOGLE = 0;
				mDialog = new DlgWindow2(getActivity(), R.style.CustomDialog, "請選擇編輯模式", "確定", "取消", 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
								callEdit(selectedImageUri);
							}
						}, new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
								filePath = getRealPathFromURI(getActivity(), selectedImageUri);
						        try {
						            frameLayout.addBitmap(filePath, error);
						        }
						        catch(Throwable throwable4) {
						            nActivityResultValue = SELECT_PHOTO;
						            strActivityResultPath = filePath;
						            
						            throwable4.printStackTrace();
						        }
							}
	            });
				mDialog.show();
			}

			
//			Uri uri = data.getData();
//        	
//        	if(uri != null) {
//        		String fileList[];
//        		
//        		Log.d("selectedImageUri", uri.getPath());
//        		fileList = (new String[] { "_data" });
//        
//        		if(getActivity() == null) 
//        			return;
//        		
//		        Cursor cursor = getActivity().getContentResolver().query(uri, fileList, null, null, null);
//		        cursor.moveToFirst();
//		        
//		        int columnIndex = cursor.getColumnIndex(fileList[0]);
//		        
//		        if(!uri.getPath().contains("picasa")) {
//			        if(columnIndex != -1) {
//			            uri = Uri.parse((new StringBuilder("file://")).append(cursor.getString(columnIndex)).toString());
//			        }
//		        }
//		        else {
//		            if(getActivity() == null) 
//		            	return;
//		            
//		            InputStream inputstream;
//		            
//					try {
//						inputstream = getActivity().getContentResolver().openInputStream(uri);
//						
//			            String fileName;
//			            
//			            if(inputstream == null) 
//			            	return;
//			            
//			            if(!Environment.getExternalStorageState().equals("mounted")) {
//			            	fileName = (new StringBuilder(String.valueOf(getActivity().getFilesDir().getAbsolutePath()))).append(File.separator).append("Pictures").toString();
//			            }
//			            else {
//			            	fileName = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("Pictures").toString();
//			            }
//			            
//			            File file = new File(fileName);
//			            
//			            if(!file.exists())
//			                file.mkdir();
//			            
//			            String dstFile = (new StringBuilder(String.valueOf(fileName))).append(File.separator).append("picture_").append(Calendar.getInstance().getTimeInMillis()).append(".jpg").toString();
//			            BitmapUtil.writeIStoFile(inputstream, dstFile);
//			            uri = Uri.parse(dstFile);
//			            
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		        }
//		        
//		        cursor.close();
//		        
//		        if(uri != null) {
//			        String urlPath = uri.getPath();
//			        
//			        if(urlPath.contains("http")) { 
//				        List list = uri.getPathSegments();
//				        String path;
//				        path = "";
//			
//				        for (int l1 = 0; l1 < list.size(); l1++) {
//				        	path = (new StringBuilder(String.valueOf(path))).append(((String)list.get(l1)).replace("/", "%2F").replace(":", "%3A")).append(File.separator).toString();
//				        }
//				        
//				        int j2 = -1 + path.length();
//				        urlPath = path.substring(0, j2);
//			        }
//			        
//			        try {
//			            frameLayout.addBitmap(urlPath, error);
//			            
//			            if(urlPath.substring(urlPath.lastIndexOf("."), urlPath.length()).contains("gif")) {
//			                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
//			                builder.setMessage(R.string.gif_warning).setPositiveButton("Ok", null);
//			                builder.create();
//			                builder.show();
//			            }
//			        }
//			        catch(Throwable throwable4) {
//			            nActivityResultValue = SELECT_PHOTO;
//			            strActivityResultPath = urlPath;
//			            
//			            throwable4.printStackTrace();
//			        }
//		        }
//		    }
        }

        if(requestCode == EDIT_FROM_GOOGLE) {
        	Utils.writeLogToFile("EDIT_FROM_GOOGLE");
			if (resultCode == getActivity().RESULT_OK && data != null) {
				Utils.writeLogToFile("EDIT_FROM_GOOGLE_OK");
				try {
					Bitmap bitmap = null;
					InputStream is = null;
					String url = data.getData().toString();
					Uri a = null;
					try {
						is = getActivity().getContentResolver().openInputStream(Uri.parse(url));
						try {
					        File f = new File(getRealPathFromURI(getActivity(), Uri.parse(url)));
					        ExifInterface exif = new ExifInterface(f.getPath());
					        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					        int angle = 0;
					        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
					            angle = 90;
					        } 
					        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
					            angle = 180;
					        } 
					        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
					            angle = 270;
					        }

					        Matrix mat = new Matrix();
					        mat.postRotate(angle);
					        BitmapFactory.Options opt = new BitmapFactory.Options();  
					        opt.inPreferredConfig = Bitmap.Config.RGB_565;   
					        opt.inPurgeable = true;  
					        opt.inInputShareable = true;  
					        Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);
					        Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);  
					        a = getImageUri(getActivity(), correctBmp);
					    }catch (IOException e) {
					        Log.w("TAG", "-- Error in setting image");
					    }catch(OutOfMemoryError oom) {
					        Log.w("TAG", "-- OOM Error in setting image");
					    }
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Log.v(TAG, "getActivity().EDIT_FROM_GOOGLE get edit picture " + Uri.parse(url));
					String AIpath = getRealPathFromURI(getActivity(), a);
					if (AIpath != null) {
						Log.v(TAG, "AIpath is " + AIpath);
						filePath = AIpath;
					} else {
						Toast.makeText(getActivity(), "Unknown path",Toast.LENGTH_LONG).show();
						Log.e("Bitmap", "Unknown path");
					}
		            //sending 
					//do anythin you want with filePath
//		            Intent mIntent = new Intent();
//		            mIntent.setClass(getActivity(), ActivityEditPost.class);
//		            Bundle mBundle = new Bundle();
//		            mBundle.putString("filePath",filePath);
//		            mIntent.putExtras(mBundle);
//		            startActivity(mIntent);

			        try {
			            frameLayout.addBitmap(filePath, error);
			            
			            if(filePath.substring(filePath.lastIndexOf("."), filePath.length()).contains("gif")) {
//			                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
//			                builder.setMessage(R.string.gif_warning).setPositiveButton("Ok", null);
//			                builder.create();
//			                builder.show();
			            }
			        }
			        catch(Throwable throwable4) {
			            nActivityResultValue = SELECT_PHOTO;
			            strActivityResultPath = filePath;
			            
			            throwable4.printStackTrace();
			        }
					
				} catch (Exception e) {
					Toast.makeText(getActivity(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}
			}
			else if (resultCode == Activity.RESULT_CANCELED){
				Utils.writeLogToFile("EDIT_FROM_GOOGLE_CANCEL");
				Utils.writeLogToFile("selectedImageUri_google " + selectedImageUri_google);
				Utils.writeLogToFile("selectedImageUri " + selectedImageUri);
				Uri editUri = null;
				filePath = "";
				Log.v(TAG, "Activity.RESULT_CANCELED selectedImageUri_google " + selectedImageUri_google);
				Log.v(TAG, "Activity.RESULT_CANCELED selectedImageUri " + selectedImageUri);
				//call edit from google
				if (selectedImageUri_google !=null){
					editUri = selectedImageUri_google;
				}else if (selectedImageUri != null){
					editUri = selectedImageUri;
				}
				
				filePath = getRealPathFromURI(getActivity(), editUri); 
				
//				Bitmap bitmap = null;
//				InputStream is = null;
//				Uri a = null;
//				Utils.writeLogToFile("Activity.RESULT_CANCELED editUri " + editUri);
//				Log.v(TAG, "Activity.RESULT_CANCELED editUri " + editUri);
//				try {is = getActivity().getContentResolver().openInputStream(editUri);
//					try {
//				        File f = new File(getRealPathFromURI(getActivity(), editUri));
//				        ExifInterface exif = new ExifInterface(f.getPath());
//				        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//				        int angle = 0;
//				        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//				            angle = 90;
//				        } 
//				        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//				            angle = 180;
//				        } 
//				        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//				            angle = 270;
//				        }
//
//				        Matrix mat = new Matrix();
//				        mat.postRotate(angle);
//				        BitmapFactory.Options opt = new BitmapFactory.Options();  
//				        opt.inPreferredConfig = Bitmap.Config.RGB_565;   
//				        opt.inPurgeable = true;  
//				        opt.inInputShareable = true;  
//				        Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);
//				        Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);  
//				        a = getImageUri(getActivity(), correctBmp);
//				    }
//				    catch (IOException e) {
//				    	Utils.writeLogToFile("-- Error in setting image");
//				        Log.w("TAG", "-- Error in setting image");
//				    }   
//				    catch(OutOfMemoryError oom) {
//				    	Utils.writeLogToFile("-- OOM Error in setting image");
//				        Log.w("TAG", "-- OOM Error in setting image");
//				    }
//				Utils.writeLogToFile("getActivity().RESULT_CANCELED a " + a);
//				Log.v(TAG, "getActivity().RESULT_CANCELED a " + a);
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//				Utils.writeLogToFile("Activity.RESULT_CANCELED get edit a " + a);
//				Log.v(TAG, "Activity.RESULT_CANCELED get edit a " + a);
				try {// OI FILE Manager
//					// true path
//					String AIpath = getRealPathFromURI(getActivity(), a);
//					Utils.writeLogToFile("getActivity().RESULT_CANCELED AIpath is " + AIpath);
//					Log.v(TAG, "getActivity().RESULT_CANCELED AIpath is " + AIpath);
//
//					if (AIpath != null) {
//						Utils.writeLogToFile("Activity.RESULT_CANCELED AIpath is " + AIpath);
//						Log.v(TAG, "Activity.RESULT_CANCELED AIpath is " + AIpath);
//						filePath = AIpath;
//					} else {
//						Toast.makeText(getActivity(), "Unknown path",
//								Toast.LENGTH_LONG).show();
//						Utils.writeLogToFile("Unknown path");
//						Log.e("Bitmap", "Unknown path");
//					}
//
//		            //sending 
//					//do anythin you want with filePath
////		            Intent mIntent = new Intent();
////		            mIntent.setClass(getActivity(), ActivityEditPost.class);
////		            Bundle mBundle = new Bundle();
////		            mBundle.putString("filePath",filePath);
////		            mIntent.putExtras(mBundle);
////		            startActivity(mIntent);

			        try {
			            frameLayout.addBitmap(filePath, error);
			            
			            if(filePath.substring(filePath.lastIndexOf("."), filePath.length()).contains("gif")) {
//			                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
//			                builder.setMessage(R.string.gif_warning).setPositiveButton("Ok", null);
//			                builder.create();
//			                builder.show();
			            }
			        }
			        catch(Throwable throwable4) {
			            nActivityResultValue = SELECT_PHOTO;
			            strActivityResultPath = filePath;
			            
			            throwable4.printStackTrace();
			        }
					
				} catch (Exception e) {
					Toast.makeText(getActivity(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}
			}
        }

        if (resultCode != getActivity().RESULT_OK)
        	return;
        
        if(requestCode == TAKE_PHOTO)
            try {
                frameLayout.addBitmap(outputFileUri.getPath(), error);
            }
            catch(Throwable throwable3) {
            	
            	nActivityResultValue = TAKE_PHOTO;
	            
            	if (outputFileUri != null) {
	            	strActivityResultPath = outputFileUri.getPath();
	            }
            	
                throwable3.printStackTrace();
            }
        
        if(requestCode == ADD_TEXT)
            try {
            	String result = data.getExtras().getString("TextImagePath");
                frameLayout.setTextImagePath(result);
            }
            catch(Throwable throwable3) {
            	
            	nActivityResultValue = ADD_TEXT;
            	strActivityResultPath = data.getExtras().getString("TextImagePath");
                throwable3.printStackTrace();
            }

        if(requestCode == TRIM_VIDEO) {
        	String result = data.getExtras().getString("FilePath");
        	nStartTime = data.getExtras().getInt("StartTime", 0);
        	nEndTime = data.getExtras().getInt("EndTime", 0);

        	try {
        		frameLayout.addVideo(result, nStartTime, nEndTime - nStartTime, error);
        	}
        	catch(Throwable throwable1) {
        		nActivityResultValue = TRIM_VIDEO;
        		strActivityResultPath = result;
        	}
        }
        
        if(requestCode == SELECT_VIDEO) {
        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log21");
	        Uri uri = data.getData();
	        String filePre[]  = (new String[] {
	            "_data"
	        });
	        
	        if(getActivity() == null) 
	        	return;
	        
	        Utils.writeLogToFile("FrameFragment-onActivityResult-Log22");
	        
	        Cursor cursor = getActivity().getContentResolver().query(uri, filePre, null, null, null);
	        cursor.moveToFirst();
	        
	        int columnIndex = cursor.getColumnIndex(filePre[0]);
	        
	        if(uri.getPath().contains("picasa")) {
	        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log23");
	        	
	            if(getActivity() == null) 
	            	return;
	            
	            InputStream inputstream;
	            
				try {
					Utils.writeLogToFile("FrameFragment-onActivityResult-Log24");
					inputstream = getActivity().getContentResolver().openInputStream(uri);
		            String dirPath;
		            
		            if(inputstream == null) 
		            	return;
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log25");
		            
		            if(!Environment.getExternalStorageState().equals("mounted")) {
		            	dirPath = (new StringBuilder(String.valueOf(getActivity().getFilesDir().getAbsolutePath()))).append(File.separator).append("Pictures").toString();
		            }
		            else {
		            	dirPath = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("Pictures").toString();
		            }
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log26");
		            
		            File file = new File(dirPath);
		            if(!file.exists())
		                file.mkdir();
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log27");
		            
		            String dstFile = (new StringBuilder(String.valueOf(dirPath))).append(File.separator).append("video_").append(Calendar.getInstance().getTimeInMillis()).append(".mp4").toString();
		            BitmapUtil.writeIStoFile(inputstream, dstFile);
		            uri = Uri.parse(dstFile);
		            
		            Utils.writeLogToFile("FrameFragment-onActivityResult-Log28");
		            
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					Utils.writeLogToFile("FrameFragment-onActivityResult-Log29");
				}
	        }
	        else {
	        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log30");
	        	
		        if(columnIndex != -1) {
		        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log31");
		            uri = Uri.parse((new StringBuilder("file://")).append(cursor.getString(columnIndex)).toString());
		        }
	        }
	        
	        Utils.writeLogToFile("FrameFragment-onActivityResult-Log32");
	        
	        cursor.close();
	        
	        Log.d("videoURL", uri.getPath());
	        
	        try {
	        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log33="+uri.getPath());
	            frameLayout.addVideo(uri.getPath(), 0, 0, error);
	        }
	        catch(Throwable throwable2) {
	        	Utils.writeLogToFile("FrameFragment-onActivityResult-Log34");
	        	
	            nActivityResultValue = SELECT_VIDEO;
	            strActivityResultPath = uri.getPath();
	            
	            throwable2.printStackTrace();
	        }
	        
//    		Intent intent = new Intent(getActivity(), VideoTrimActivity.class);
//            intent.putExtra("path", uri.getPath());
//    		startActivityForResult(intent, TRIM_VIDEO);
        }
        
        if(requestCode == RECORD_VIDEO) {
//    		Intent intent = new Intent(getActivity(), VideoTrimActivity.class);
//            intent.putExtra("path", videoUri.getPath());
//    		startActivityForResult(intent, TRIM_VIDEO);
        }
        
        if(requestCode == SELECT_TRACK) {
        	Uri uri;
	        try {
	            uri = data.getData();
	        }
	        catch(Throwable throwable) {
	            throwable.printStackTrace();
	            return;
	        }
	        
	        if(uri == null) 
	        	return;
	
	        Log.d("selectedImageUri", uri.getPath());
	        
	        if(getActivity() == null) 
	        	return;
	
	        String filePre[] = (new String[] {
	            "_data"
	        });
	        
	        Cursor cursor = (new CursorLoader(getActivity(), uri, filePre, null, null, null)).loadInBackground();
	        int columnIndex = cursor.getColumnIndexOrThrow("_data");
	        
	        cursor.moveToFirst();
	        
	        audioUri = Uri.parse(cursor.getString(columnIndex));
	        cursor.close();
	        frameLayout.setAudioPath(audioUri.getPath());
        }
    }

    public void onCreate(Bundle bundle) {
    	
    	Utils.writeLogToFile("FramesFragment-onCreate****************************");
    	
        if(bundle != null) {
        	
        	Utils.writeLogToFile("FrameFragment-onCreate-Log1");
        	
            if(bundle.containsKey("outPutPicture")) {
            	
            	Utils.writeLogToFile("FrameFragment-onCreate-Log2");
            	
                outputFileUri = Uri.parse(bundle.getString("outPutPicture"));
                
	            if (outputFileUri != null) {
	            	Utils.writeLogToFile("FrameFragment-onCreate-Log3="+outputFileUri.getPath());
	            	strActivityResultPath = outputFileUri.getPath();
	            }
	            
	            Utils.writeLogToFile("FrameFragment-onCreate-Log4");
            }
            
            Utils.writeLogToFile("FrameFragment-onCreate-Log5");
            
            if(bundle.containsKey("outPutVideo")) {
            	
            	Utils.writeLogToFile("FrameFragment-onCreate-Log6");
            	
                videoUri = Uri.parse(bundle.getString("outPutVideo"));
                
	            if (videoUri != null) {
	            	Utils.writeLogToFile("FrameFragment-onCreate-Log7");
	            	strActivityResultPath = videoUri.getPath();
	            }
	            
	            Utils.writeLogToFile("FrameFragment-onCreate-Log8");
            }
            
            Utils.writeLogToFile("FrameFragment-onCreate-Log9");
            Log.d("onCreate", "state not null");
        }
        
        Utils.writeLogToFile("FrameFragment-onCreate-Log10");
        
        frameID = getArguments().getInt("frameID");
        bNewFlag = getArguments().getBoolean("NewProject");
        Log.e("FrameID", "FrameID1="+frameID);
        File file;
        
        if(Environment.getExternalStorageState().equals("mounted"))
            mBaseFolderPath = (new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString()))).append(File.separator).append("Pictures").toString();
        else
            mBaseFolderPath = (new StringBuilder(String.valueOf(getActivity().getFilesDir().getAbsolutePath()))).append(File.separator).append("Pictures").toString();
        
        Utils.writeLogToFile("FrameFragment-onCreate-Log11");
        file = new File(mBaseFolderPath);
        
        if(!file.exists())
            file.mkdir();
        
        Utils.writeLogToFile("FrameFragment-onCreate-Log12");
        
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
    
    	Utils.writeLogToFile("FramesFragment-onCreateView****************************");
    	
    	Utils.writeLogToFile("FrameFragment-onCreateView-Log1");
        View view = layoutinflater.inflate(R.layout.work_fragment, null);
        
        if(frameID > -1)
            frame = Frames.newInstance(getActivity()).getFrameWithId(frameID);
        
        frameHolder = (LinearLayout)view.findViewById(R.id.frameHolder);
        frameHolder.removeAllViews();
        
        Utils.writeLogToFile("FrameFragment-onCreateView-Log2");
        
        error = new com.dragonplayer.merge.frames.FrameView.CorruptVideoError() {

            public void corruptVideo() {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
//                builder.setMessage("The file you selected is corrupted! Please try again with another file.").setPositiveButton("Ok", null);
//                builder.create();
//                builder.show();
            }
        };
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        frameList = (RelativeLayout)view.findViewById(R.id.frameList);
        		
        viewPager1 = (ViewPager)view.findViewById(R.id.viewPager1);
        frames = Frames.newInstance(getActivity());
        imagesPerPage = 0;
        projectsCount1 = frames.getFramesCount1();
        radioGr1 = (RadioGroup)view.findViewById(R.id.radioGroup1);
        viewPager1.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                viewPager1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int i = (int)((0.84999999999999998D * (double)viewPager1.getWidth()) / 3D);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameList.getLayoutParams();
                params.height = i * 5 / 2;
                frameList.setLayoutParams(params);

                if(i == 0)
                    return;
                
                if(i % 2 != 0)
                    i++;
                
                int itemCount = 3;
                
                Log.d("items", (new StringBuilder(" ")).append(itemCount).toString());
                
                imagesPerPage = itemCount;
                
                if(imagesPerPage == 0)
                    imagesPerPage = 1;
                
                Log.d("itemsRefresh", (new StringBuilder(" ")).append(itemCount).toString());
                
                pageNr1 = projectsCount1 / imagesPerPage;
                
                if(projectsCount1 % imagesPerPage != 0) {
                    WorkFragment.this.pageNr1 = 1 + WorkFragment.this.pageNr1;
                }
                
                adapter1 = new FrameListAdapter(getChildFragmentManager(), pageNr1, imagesPerPage, projectsCount1, i, frames, 0);
                
                viewPager1.setAdapter(adapter1);
                radioGr1.removeAllViews();
                radioGr1.setDividerPadding(3);
                
                for (int k = 0; k < pageNr1; k++) { 
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
                                viewPager1.setCurrentItem(compoundbutton.getId());
                        }
                    });
                    
                    radioGr1.addView(radiobutton);
                    
                }

                ((RadioButton)radioGr1.getChildAt(0)).setChecked(true);
                viewPager1.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {

                    public void onPageScrollStateChanged(int i) {
                    }

                    public void onPageScrolled(int i, float f, int j) {
                    }

                    public void onPageSelected(int i) {
                        ((RadioButton)radioGr1.getChildAt(i)).setChecked(true);
                    }
                });
            }
        });

        curGallery = (HorizontalListView)view.findViewById(R.id.viewPager3);
        curGallery.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
            	curGallery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int i = (int)((0.84999999999999998D * (double)curGallery.getWidth()) / 3D);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameList.getLayoutParams();
                params.height = i * 5 / 2;
                frameList.setLayoutParams(params);

                if(i == 0)
                    return;
                
                if(i % 2 != 0)
                    i++;
                
                int count = -1;
                
                if (frameID != -1) {
                	count = frames.getFrameWithId(frameID).getParts().size();
                }
                
                adapter3 = new ImageAdapter(getActivity(), count, i, i*1920/1080);
                
                curGallery.setAdapter(adapter3);
                curGallery.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
			        	int count = -1;
			        	int i;
			        	if (frameID < 18) {
				        	for (i = 0; i < frames.getFramesCount1(); i++) {
				        		if (frames.getFrameWithId1(i).getParts().size() == adapter3.getFrameCount())
				        			count++;
				        		
				        		if (count == position)
				        			break;
				        	}
			        	}
			        	else {
			            	for (i = 0; i < frames.getFramesCount2(); i++) {
			            		if (frames.getFrameWithId2(i).getParts().size() == adapter3.getFrameCount())
			            			count++;
			            		
			            		if (count == position)
			            			break;
			            	}
			            	
			            	i += 18;
			        	}

			            final int realPos = i;
			            final Context mContext = getActivity();

	    		        SharedPreferences sp = mContext.getSharedPreferences("iDragon", Context.MODE_PRIVATE);
	    		        boolean bFacebookLiked = sp.getBoolean("isLike", false);
	    		        int nLock;
	    		        nLock = frames.getFrameWithId(realPos).getLockFlag();

	    		        if (!bFacebookLiked && nLock == Frame.FRAMELOCK) {
	    					mDialog = new DlgWindow1(mContext, R.style.CustomDialog, "驗證SK2U粉絲團", "取消", "前往解鎖", new OnClickListener() {
	    						//粉絲團
	    						@Override
	    						public void onClick(View v) {
	    							// TODO Auto-generated method stub
	    							mDialog.dismiss();
	    						}
	    		            }, new OnClickListener() {

	    						@Override
	    						public void onClick(View v) {
	    							// TODO Auto-generated method stub
	    							((MainActivity)mContext).onClickLock();
	    							mDialog.dismiss();
	    						}
	    		            });
	    					mDialog.show();
	    		        	return;
	    		        }
	    		        else 
	    		        	((MainActivity)mContext).selectFrameFragment(realPos);
					}
                	
                });
            }
        });

        viewPager2 = (ViewPager)view.findViewById(R.id.viewPager2);
        projectsCount2 = frames.getFramesCount2();
        radioGr2 = (RadioGroup)view.findViewById(R.id.radioGroup2);
        viewPager2.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                viewPager2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int i = (int)((0.84999999999999998D * (double)viewPager2.getWidth()) / 3D);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameList.getLayoutParams();
                params.height = i * 5 / 2;
                frameList.setLayoutParams(params);

                if(i == 0)
                    return;
                
                if(i % 2 != 0)
                    i++;
                
                int itemCount = 3;
                
                Log.d("items", (new StringBuilder(" ")).append(itemCount).toString());
                
                imagesPerPage = itemCount;
                
                if(imagesPerPage == 0)
                    imagesPerPage = 1;
                
                Log.d("itemsRefresh", (new StringBuilder(" ")).append(itemCount).toString());
                
                pageNr2 = projectsCount2 / imagesPerPage;
                
                if(projectsCount2 % imagesPerPage != 0) {
                    WorkFragment.this.pageNr2 = 1 + WorkFragment.this.pageNr2;
                }
                
                adapter2 = new FrameListAdapter(getChildFragmentManager(), pageNr2, imagesPerPage, projectsCount2, i, frames, 1);
                
                viewPager2.setAdapter(adapter2);
                radioGr2.removeAllViews();
                radioGr2.setDividerPadding(3);
                
                for (int k = 0; k < pageNr2; k++) { 
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
                                viewPager2.setCurrentItem(compoundbutton.getId());
                        }
                    });
                    
                    radioGr2.addView(radiobutton);
                    
                }

                ((RadioButton)radioGr2.getChildAt(0)).setChecked(true);
                viewPager2.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {

                    public void onPageScrollStateChanged(int i) {
                    }

                    public void onPageScrolled(int i, float f, int j) {
                    }

                    public void onPageSelected(int i) {
                        ((RadioButton)radioGr2.getChildAt(i)).setChecked(true);
                    }
                });
            }
        });
        
        btnFrameList = (ImageButton)view.findViewById(R.id.btnList);
        btnFrameList.setOnClickListener(new android.view.View.OnClickListener() {

        	public void onClick(View view1) {
        		if (frameID > -1) {
            		if (frameList.getVisibility() == View.VISIBLE)
            			frameList.setVisibility(View.GONE);
            		else { 
		        		frameList.setVisibility(View.VISIBLE);
		        	    radioGr1.setVisibility(View.GONE);
		        	    viewPager1.setVisibility(View.GONE);
		        	    radioGr2.setVisibility(View.GONE);
		        	    viewPager2.setVisibility(View.GONE);
		        	    curGallery.setVisibility(View.VISIBLE);
            		}
	        	    return;
        		}
        		
        		curGallery.setVisibility(View.GONE);

//        		if (frameList.getVisibility() == View.VISIBLE)
        			frameList.setVisibility(View.GONE);
//        		else 
        		{
					mDialog = new DlgWindow1(getActivity(), R.style.CustomDialog, "選擇模板", " 靜態模板", "動態模板", new OnClickListener() {
	
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							frameList.setVisibility(View.VISIBLE);			        		
			        	    radioGr1.setVisibility(View.VISIBLE);
			        	    viewPager1.setVisibility(View.VISIBLE);
			        	    radioGr2.setVisibility(View.GONE);
			        	    viewPager2.setVisibility(View.GONE);
			        		
							mDialog.dismiss();
							mDialog = null;
						}
		            }, new OnClickListener() {
	
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							frameList.setVisibility(View.VISIBLE);
			        	    radioGr1.setVisibility(View.GONE);
			        	    viewPager1.setVisibility(View.GONE);
			        	    radioGr2.setVisibility(View.VISIBLE);
			        	    viewPager2.setVisibility(View.VISIBLE);
			        	    
			        		mDialog.dismiss();
			        		mDialog = null;
						}
		            });
					mDialog.show();
        		}
        	}
        });
        
        btnAddText = (ImageButton)view.findViewById(R.id.btnAddText);
        btnAddText.setOnClickListener(new android.view.View.OnClickListener() {

        	public void onClick(View view1) {
        		Intent intent = new Intent(getActivity(), TextInputActivity.class);
        		startActivityForResult(intent, ADD_TEXT);
        	}
        });
        
        DisplayMetrics displaymetrics1;
//        Log.e("WorkFragment", "frameid="+frameID);
//        if (mDialog != null) {
//        	try {
//        		mDialog.dismiss();
//        	}
//        	catch (Exception e) {
//        		
//        	}
//        	
//        	mDialog = null;
//        }
        if(frameID == -1) {
            btnAddText.setEnabled(false);
            frameList.setVisibility(View.INVISIBLE);
//    	    radioGr1.setVisibility(View.INVISIBLE);
//    	    viewPager1.setVisibility(View.INVISIBLE);
//    	    radioGr2.setVisibility(View.INVISIBLE);
//    	    viewPager2.setVisibility(View.INVISIBLE);
            
			mDialog = new DlgWindow1(getActivity(), R.style.CustomDialog, "選擇模板", " 靜態模板", "動態模板", new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					frameList.setVisibility(View.VISIBLE);			        		
	        	    radioGr1.setVisibility(View.VISIBLE);
	        	    viewPager1.setVisibility(View.VISIBLE);
	        	    radioGr2.setVisibility(View.GONE);
	        	    viewPager2.setVisibility(View.GONE);
	        	    curGallery.setVisibility(View.GONE);
	        		
					mDialog.dismiss();
					mDialog = null;
				}
            }, new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					frameList.setVisibility(View.VISIBLE);
	        	    radioGr1.setVisibility(View.GONE);
	        	    viewPager1.setVisibility(View.GONE);
	        	    radioGr2.setVisibility(View.VISIBLE);
	        	    viewPager2.setVisibility(View.VISIBLE);
	        	    curGallery.setVisibility(View.GONE);
	        	    
	        		mDialog.dismiss();
	        		mDialog = null;
				}
            });
			mDialog.show();

        } 
        else {
        	btnAddText.setEnabled(true);
            frameList.setVisibility(View.INVISIBLE);
        }
        
        displaymetrics1 = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics1);
        
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnFrameList.getLayoutParams();
        params.width = displaymetrics1.widthPixels / 2;
        params.height = params.width * 121 / 361;
        btnFrameList.setLayoutParams(params);
        
        params = (LinearLayout.LayoutParams) btnAddText.getLayoutParams();
        params.width = displaymetrics1.widthPixels / 2;
        params.height = params.width * 121 / 361;
        btnAddText.setLayoutParams(params);

        if(frameID == -1) {
//            TextView textview = new TextView(getActivity());
//            textview.setText(R.string.no_frame_selected);
//            textview.setTextColor(0xFF000000);
//            textview.setGravity(17);
//            textview.setTextSize(0.02F * (float)displaymetrics1.heightPixels);
//            frameHolder.addView(textview);
        	((MainActivity)getActivity()).onCompleteVisibility(View.INVISIBLE);
        } 
        else {
        	((MainActivity)getActivity()).onCompleteVisibility(View.VISIBLE);
            frameHolder.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressLint("NewApi")
				public void onGlobalLayout() {
                	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log1");
                	
                    frameHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setFrame();
                    
                    Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log4");
                    
        	        try {
        	        	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log5="+strActivityResultPath);
        	        	
        	            if (nActivityResultValue == SELECT_PHOTO) {
        	            	Utils.writeLogToFile("path="+strActivityResultPath);
    			            frameLayout.addBitmap(strActivityResultPath, error);
    			            int j1 = strActivityResultPath.lastIndexOf(".");
    			            int k1 = strActivityResultPath.length();
    			            Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log6");
    			            if(strActivityResultPath.substring(j1, k1).contains("gif")) {
//    			                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
//    			                builder.setMessage(R.string.gif_warning).setPositiveButton("Ok", null);
//    			                builder.create();
//    			                builder.show();
    			            }
        	            }
        	            else if (nActivityResultValue == TAKE_PHOTO) {
        	            	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log7");
        	            	frameLayout.addBitmap(strActivityResultPath, error);
        	            }
        	            else if (nActivityResultValue == SELECT_VIDEO) {
        	            	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log8");
        	            	frameLayout.addVideo(strActivityResultPath, 0, 0, error);
        	            	
        	            }
        	            else if (nActivityResultValue == RECORD_VIDEO) {
        	            	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log9");
        	                frameLayout.addVideo(strActivityResultPath, 0, 0, error);
        	            }
        	            else if (nActivityResultValue == ADD_TEXT) {
        	            	frameLayout.setTextImagePath(strActivityResultPath);
        	            }
        	            else if(nActivityResultValue == TRIM_VIDEO) {
        	            	frameLayout.addVideo(strActivityResultPath, nStartTime, nEndTime - nStartTime, error);
        	            }

        	        }
        	        catch(Throwable throwable2) {
        	        	Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log10");
        	            throwable2.printStackTrace();
        	        }
    	            
        	        Utils.writeLogToFile("FrameFragment-onGlobalLayout-Log11");
    	            nActivityResultValue = 0;
    	            strActivityResultPath = "";
                }
            });
        }
        
        return view;
    }

    public void onSaveInstanceState(Bundle bundle) {
    	Log.d("onSaveInstanceState", "framefragment");
    	
        if(outputFileUri != null)
            bundle.putString("outPutPicture", outputFileUri.getPath());
        if(videoUri != null)
            bundle.putString("outPutVideo", videoUri.getPath());
        
        getArguments().putInt("frameID", frameID);
        
        super.onSaveInstanceState(bundle);
    }

    public void onStop() {
        if(frameLayout != null)
            try {
            	String fileName = frameLayout.getTextImagePath();
            	if (fileName == null)
            		fileName = "";
                JSONObject jsonobject = new JSONObject();
                jsonobject.put("frameID", frameID);
                jsonobject.put("textImagePath", fileName);
                jsonobject.put("fTxtAngle", frameLayout.fTxtAngle);
                jsonobject.put("txtX", frameLayout.txtX);
                jsonobject.put("txtY", frameLayout.txtY);
                jsonobject.put("fZoom", frameLayout.fZoom);
                jsonobject.put("selectedFrame", frameLayout.getSelectedFrame());
                jsonobject.put("audioPath", frameLayout.getAudioPath());
                jsonobject.put("isSequentially", frameLayout.isSequentially());
                jsonobject.put("datas", frameLayout.saveDatas());
                Utils.setDatas(getActivity().getApplicationContext(), jsonobject.toString());
            }
            catch(JSONException jsonexception) {
                jsonexception.printStackTrace();
            }
        
        super.onStop();
    }

    public void setAudioSection(long startTime, long endTime) {
        if(frameLayout != null) {
            frameLayout.setAudioStartTime(startTime);
            frameLayout.setAudioEndTime(endTime);
        }
    }

    public void setFrame(int no) {
    	boolean flag = false;
    	if (no != -1 && frameID != -1) {
    		if (frames.getFrameWithId(no).getParts().size()	== frames.getFrameWithId(frameID).getParts().size()) {
    			flag = true;
    	        if(frameLayout != null)
    	            try {
    	            	String fileName = frameLayout.getTextImagePath();
    	            	if (fileName == null)
    	            		fileName = "";
    	                JSONObject jsonobject = new JSONObject();
    	                jsonobject.put("frameID", frameID);
    	                jsonobject.put("textImagePath", fileName);
    	                jsonobject.put("fTxtAngle", frameLayout.fTxtAngle);
    	                jsonobject.put("txtX", frameLayout.txtX);
    	                jsonobject.put("txtY", frameLayout.txtY);
    	                jsonobject.put("fZoom", frameLayout.fZoom);
    	                jsonobject.put("selectedFrame", frameLayout.getSelectedFrame());
    	                jsonobject.put("audioPath", frameLayout.getAudioPath());
    	                jsonobject.put("isSequentially", frameLayout.isSequentially());
    	                jsonobject.put("datas", frameLayout.saveDatas());
    	                Utils.setDatas(getActivity().getApplicationContext(), jsonobject.toString());
    	            }
    	            catch(JSONException jsonexception) {
    	                jsonexception.printStackTrace();
    	            }
    		}
    	}
    	
    	frameID = no;
        frameList.setVisibility(View.GONE);
        btnAddText.setEnabled(true);
    	
        if(frameID > -1) {
        	if (flag)
        		bNewFlag = false;
        	else
        		bNewFlag = true;
        	((MainActivity)getActivity()).onCompleteVisibility(View.VISIBLE);
            frame = Frames.newInstance(getActivity()).getFrameWithId(frameID);
            setFrame();
            adapter3.setFrameCount(frames.getFrameWithId(frameID).getParts().size());
            adapter3.notifyDataSetChanged();
        }
    }
    
    public void setFrame() {
        frameHolder.removeAllViews();

        DisplayMetrics displaymetrics1 = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics1);
        int holderHeight = frameHolder.getHeight();
        int realW;
        int realH;
      
//        if (frame.getFrameDirection() == Frame.FRAMEVERTICAL) {
	    	realH = holderHeight;
	    	realW = realH * FRAMEORGWIDTH / FRAMEORGHEIGHT; 
//        }
//        else {
//        	realW = (int)((double)displaymetrics1.widthPixels * (0.93333D)); 
//        	realH = realW * FRAMEORGWIDTH / FRAMEORGHEIGHT;
//        }
        
        frameLayout = new FramesLayout(getActivity(), frame, true, frameID, !bNewFlag, realW, realH, ((MainActivity)getActivity()).getManager(), true, null);
        android.widget.LinearLayout.LayoutParams layoutparams1 = new android.widget.LinearLayout.LayoutParams(realW, realH);
        frameHolder.addView(frameLayout, layoutparams1);
        Utils.setFrameWidth(getActivity().getBaseContext(), realW, realH);
    }
    
    public void changePosImage() {
    	frameLayout.changeFrame();
    }

	public void selectFromGallery() {
        if (frameLayout.isExistSelectFrame()) {
            if (frame.getFrameType() == Frame.FRAMETYPEVIDEO && 
	        		frameID != FramesLayout.FRAMEID_EAT && frameID != FramesLayout.FRAMEID_HAPPYBIRTHDAY && (frameLayout.isExistVideoSelectedFrame() || !frameLayout.isExistVideoFrame())) {

				mDialog = new DlgWindow8(getActivity(), R.style.CustomDialog, "更換位置", "更換為照片", "更換影片", "刪除", 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								frameLayout.setChangeSelectedFrame();
								mDialog.dismiss();
							}
	            		}, new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
		    					startGalleryIntent();
								mDialog.dismiss();
							}
			            }, new OnClickListener() {
		
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
		    					startVideoPickerIntent();
								mDialog.dismiss();
							}
			            }, new OnClickListener() {
								
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
		    					frameLayout.removeFrame();
								mDialog.dismiss();
							}
			    });
				mDialog.show();
            }
            else if (frameLayout.getFrameCount() == 1){
				mDialog = new DlgWindow5(getActivity(), R.style.CustomDialog, "更換照片", "刪除", 
			            new OnClickListener() {
		
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								startGalleryIntent();
								mDialog.dismiss();
							}
			            }, new OnClickListener() {
								
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
		    					frameLayout.removeFrame();
								mDialog.dismiss();
							}
			    });
				mDialog.show();
            }
            else {
				mDialog = new DlgWindow6(getActivity(), R.style.CustomDialog, "更換位置", "更換照片", "刪除", 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								frameLayout.setChangeSelectedFrame();
								mDialog.dismiss();
							}
			            }, new OnClickListener() {
		
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								startGalleryIntent();
								mDialog.dismiss();
							}
			            }, new OnClickListener() {
								
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
		    					frameLayout.removeFrame();
								mDialog.dismiss();
							}
			    });
				mDialog.show();
            }
        }
        else {
	        if (frame.getFrameType() == Frame.FRAMETYPEVIDEO && !frameLayout.isExistVideoFrame() && 
	        		frameID != FramesLayout.FRAMEID_EAT && frameID != FramesLayout.FRAMEID_HAPPYBIRTHDAY) {
				mDialog = new DlgWindow5(getActivity(), R.style.CustomDialog, "加入照片", "加入影片", 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								startGalleryIntent();
								mDialog.dismiss();
							}
			            }, new OnClickListener() {
								
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								startVideoPickerIntent();
								mDialog.dismiss();
							}
			    });
				mDialog.show();
	        }
	        else {
				mDialog = new DlgWindow7(getActivity(), R.style.CustomDialog, "加入照片", 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								startGalleryIntent();
								mDialog.dismiss();
							}
			    });
				mDialog.show();
	        }
        }
	}

	void callEdit(Uri image) {
		// chioceImage = image;
		if (CheckPackageExist(getActivity(), "com.google.android.apps.plus")) {
			Intent editIntent = new Intent(Intent.ACTION_EDIT);
			editIntent.setDataAndType(image, "image/*");
			editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivityForResult(Intent.createChooser(editIntent, null),EDIT_FROM_GOOGLE);
		}
		else {
			String urlPath = getRealPathFromURI(getActivity(), image);
			Log.e("urlPath", urlPath);
			
	        try {
	            frameLayout.addBitmap(urlPath, error);
	            
	            if(urlPath.substring(urlPath.lastIndexOf("."), urlPath.length()).contains("gif")) {
//	                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
//	                builder.setMessage(R.string.gif_warning).setPositiveButton("Ok", null);
//	                builder.create();
//	                builder.show();
	            }
	        }
	        catch(Throwable throwable4) {
	            nActivityResultValue = SELECT_PHOTO;
	            strActivityResultPath = urlPath;
	            
	            throwable4.printStackTrace();
	        }
		}
    }
	
	public static boolean CheckPackageExist(Context context, String PackageName) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(PackageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public static String getRealPathFromURI(Activity context, Uri contentUri) {
		if (context != null && contentUri != null) {
			String[] proj = { MediaColumns.DATA };
			Cursor cursor = context.getContentResolver().query(contentUri,
					proj, null, null, null);
			int s = contentUri.getPath().indexOf("http");
			if (s > -1) {
				return contentUri.getPath().substring(s,
						contentUri.getPath().length());
			} else if (cursor != null) {
				cursor.moveToFirst();
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				return cursor.getString(column_index);
			} else {
				// Utils.debug("AiOut","cursor null",3);
				return contentUri.toString().replace("file://", "");
			}
		} else {
			return "";
		}
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		String path = "";
		try{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		path = Images.Media.insertImage(inContext.getContentResolver(),
				inImage, "Title", null);
		return Uri.parse(path);
		}catch (Exception e){
			e.printStackTrace();
			return null;
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
    
    public boolean isExistContent() {
    	if (frameLayout != null) {
    		if (frameLayout.isExistContent())
    			return true;
    	}

    	return false;
    }

    public String getVideoPath() {
    	if (frame.getFrameType() != Frame.FRAMETYPEVIDEO)
    		return null;
    			
    	return frameLayout.getVideoPath();
    }
    
    public int getFrameID() {
    	Log.e("frameID", "frameID="+frameID);
    	return frameID;
    }
    
    public void setFrameBitmap(Bitmap bmp, int index) {
    	frameLayout.setFrameBitmap(bmp, index);
    }


    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private int mFrameCount;
        private int itemWidth;
        private int itemHeight;

        public ImageAdapter(Context c, int count, int w, int h) {
            mContext = c;
            mFrameCount = count;
            itemWidth = w;
            itemHeight = h;
        }

        public void setFrameCount(int count) {
        	mFrameCount = count;
        }
        
        public int getCount() {
        	
        	if (mFrameCount == -1)
        		return 0;
        	
        	int count = 0;
        	
        	if (frameID < 18) {
	        	for (int i = 0; i < frames.getFramesCount1(); i++) {
	        		if (frames.getFrameWithId1(i).getParts().size() == mFrameCount)
	        			count++;
	        	}
        	}
        	else {
            	for (int i = 0; i < frames.getFramesCount2(); i++) {
            		if (frames.getFrameWithId2(i).getParts().size() == mFrameCount)
            			count++;
            	}
        	}
        	
        	return count;
        }

        public int getFrameCount() {
        	return mFrameCount;
        }
        
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	int count = -1;
        	int i;
        	if (frameID < 18) {
	        	for (i = 0; i < frames.getFramesCount1(); i++) {
	        		if (frames.getFrameWithId1(i).getParts().size() == mFrameCount)
	        			count++;
	        		
	        		if (count == position)
	        			break;
	        	}
        	}
        	else {
            	for (i = 0; i < frames.getFramesCount2(); i++) {
            		if (frames.getFrameWithId2(i).getParts().size() == mFrameCount)
            			count++;
            		
            		if (count == position)
            			break;
            	}
            	
            	i += 18;
        	}

            final int realPos = i;
            Frame frame;
            frame = frames.getFrameWithId(i);
            
            RelativeLayout frmView = (RelativeLayout) ((Activity)mContext).getLayoutInflater().inflate(R.layout.frame_view, null);
            
            ImageView imgFrameThumb = (ImageView) frmView.findViewById(R.id.imgframethumb);

            imgFrameThumb.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
//    		        SharedPreferences sp = mContext.getSharedPreferences("iDragon", Context.MODE_PRIVATE);
//    		        boolean bFacebookLiked = sp.getBoolean("isLike", false);
//    		        int nLock;
//    		        nLock = frames.getFrameWithId(realPos).getLockFlag();
//
//    		        if (!bFacebookLiked && nLock == Frame.FRAMELOCK) {
//    					mDialog = new DlgWindow1(mContext, R.style.CustomDialog, "驗證SK2U粉絲團", "取消", "前往解鎖", new OnClickListener() {
//
//    						@Override
//    						public void onClick(View v) {
//    							// TODO Auto-generated method stub
//    							mDialog.dismiss();
//    						}
//    		            }, new OnClickListener() {
//
//    						@Override
//    						public void onClick(View v) {
//    							// TODO Auto-generated method stub
//    							((MainActivity)mContext).onClickLock();
//    							mDialog.dismiss();
//    						}
//    		            });
//    					mDialog.show();
//    		        	return;
//    		        }
//    		        else 
//    		        	((MainActivity)mContext).selectFrameFragment(realPos);
    			}
            	
            });
            
            SharedPreferences sp = mContext.getSharedPreferences("iDragon", Context.MODE_PRIVATE);
            boolean bFacebookLiked = sp.getBoolean("isLike", false);

            boolean lockflag = (!bFacebookLiked && frames.getFrameWithId(realPos).getLockFlag() == Frame.FRAMELOCK) ? true : false;
            			
	        imgFrameThumb.setLayoutParams(new RelativeLayout.LayoutParams(itemWidth, itemHeight));										//itemHeight * 72 / 128
	        imgFrameThumb.setImageBitmap(BitmapUtil.getBitmapFromAsset(mContext, "Frames/"+frame.getFrameName()+"-thumb.png", itemHeight * 72 / 128, itemHeight, lockflag,0));
	        
	        frmView.setDescendantFocusability(0x20000);
	        frmView.setLayoutParams(new android.widget.Gallery.LayoutParams((int)(itemHeight * 0.84999999999999998D), itemHeight));
            
            return frmView;
        }
    }
}
