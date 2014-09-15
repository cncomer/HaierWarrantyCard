package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.ImageHelper;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseNoActionBarActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "BaseActivity";

	private static final int CurrentPictureGalleryRequest = 11000;
	private static final int CurrentPictureCameraRequest = 11001;
	private int mCurrentPictureRequest;
	protected Context mContext;
	
	private ImageView mHomeBtn, mMoreBtn;
	private TextView mTitleView;
	private LinearLayout mTitleBar;
	private FrameLayout mTitleLayout;
	private FrameLayout mContent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (!checkIntent(getIntent())) {
			finish();
			DebugUtils.logD(TAG, "checkIntent() failed, finish this activiy");
			return;
		}
		mContext = this;
		
	}
	//add by chenkai, 兼容ActionBar和自定义的居中TitleBar on
	private void initTitleBar() {
		if (mTitleView == null) {
			super.setContentView(R.layout.title_bar);
			mTitleBar = (LinearLayout) super.findViewById(R.id.cncom_title_bar);
			mTitleLayout = (FrameLayout) super.findViewById(R.id.cncom__title_layout);
			mTitleView = (TextView) super.findViewById(R.id.cncom__title);
			mContent = (FrameLayout) super.findViewById(R.id.cncom_content);
			mHomeBtn = (ImageView) super.findViewById(R.id.cncom__up);
			mMoreBtn = (ImageView) super.findViewById(R.id.menu_more);
			mTitleView.setText(getTitle());
			mHomeBtn.setOnClickListener(BaseNoActionBarActivity.this);
			mMoreBtn.setOnClickListener(BaseNoActionBarActivity.this);
		}
		
		
	}
	@Override
	public void setContentView(int layoutResId) {
		initTitleBar();
		LayoutInflater.from(mContext).inflate(layoutResId, mContent, true);
	}
	
	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}
	
    public void setTitle(CharSequence title) {
    	initTitleBar();
		mTitleView.setText(title);
		super.setTitle(title);
    }
    /**返回TitleBar*/
    public View getTitleBar() {
    	return mTitleBar;
    }
    /**返回TitleBar上的Title*/
    public View getTitleLayout() {
    	return mTitleLayout;
    }
    public void setTitleColor(int textColor) {
    	initTitleBar();
		mTitleView.setTextColor(textColor);
		super.setTitleColor(textColor);
    }
	//add by chenkai, 兼容ActionBar和自定义的居中TitleBar off
	
	//add by chenkai, 20140726 增加youmeng统计时长 begin
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	//add by chenkai, 20140726 增加youmeng统计时长 end
    protected abstract boolean checkIntent(Intent intent);
	
	public static final int DIALOG_PICTURE_CHOOSE_CONFIRM = 10002;
	//add by chenkai, 20131208, for updating check
	/**SD不可�?*/
	protected static final int DIALOG_MEDIA_UNMOUNTED = 10003;
	
	public static final int DIALOG_DATA_NOT_CONNECTED = 10006;//数据连接不可�?
	public static final int DIALOG_MOBILE_TYPE_CONFIRM = 10007;//
	
	
	public static final int DIALOG_PROGRESS = 10008;
	private ProgressDialog mProgressDialog;
	/**
	 * @param uri 选择的图库的图片的Uri
	 * @return
	 */
	protected void onPickFromGalleryFinish(Uri uri) {
	}
    protected void onPickFromCameraFinish() {
	}
    protected void onPickFromGalleryStart() {
	}
    protected void onPickFromCameraStart() {
	}
    protected void onMediaUnmountedConfirmClick() {
   	}
    protected void onDialgClick(int id, DialogInterface dialog, boolean ok, int witch) {
   	}
	/**
	 * pick avator from local gallery app.
	 * @return
	 */
    protected void pickFromGallery() {
    	if (!MyApplication.getInstance().hasExternalStorage()) {
			MyApplication.getInstance().showMessage(R.string.msg_no_sdcard);
			return;
		}
    	Intent intent = ImageHelper.createGalleryIntent();
    	startActivityForResult(intent, CurrentPictureGalleryRequest);
	}
	/**
	 * pick avator by camera
	 * @param savedFile
	 */
    protected void pickFromCamera(File savedFile) {
    	if (!MyApplication.getInstance().hasExternalStorage()) {
			MyApplication.getInstance().showMessage(R.string.msg_no_sdcard);
			return;
		}
		Intent intent = ImageHelper.createCaptureIntent(Uri.fromFile(savedFile));
		startActivityForResult(intent, CurrentPictureCameraRequest);
	}
    
    /**
	 * pick avator from local gallery app.
	 * @return
	 */
    protected void pickFromGallery(int questCode) {
    	if (!MyApplication.getInstance().hasExternalStorage()) {
			MyApplication.getInstance().showMessage(R.string.msg_no_sdcard);
			return;
		}
    	Intent intent = ImageHelper.createGalleryIntent();
    	startActivityForResult(intent, questCode);
	}
	/**
	 * pick avator by camera
	 * @param savedFile
	 */
    protected void pickFromCamera(File savedFile, int questCode) {
    	if (!MyApplication.getInstance().hasExternalStorage()) {
			MyApplication.getInstance().showMessage(R.string.msg_no_sdcard);
			return;
		}
		Intent intent = ImageHelper.createCaptureIntent(Uri.fromFile(savedFile));
		startActivityForResult(intent, questCode);
	}
    
    public int getCurrentPictureRequest() {
    	return mCurrentPictureRequest;
    }
    
    @Override
   	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   		super.onActivityResult(requestCode, resultCode, data);
   		if (resultCode == Activity.RESULT_OK) {
   			if (CurrentPictureGalleryRequest == requestCode) {
   				onPickFromGalleryFinish(data.getData());
   			} else if (CurrentPictureCameraRequest == requestCode) {
   				onPickFromCameraFinish();
   				
   			}
   		}
   	}
       
       @Override
   	public Dialog onCreateDialog(int id) {
   		switch(id) {
   		case DIALOG_PICTURE_CHOOSE_CONFIRM:
   			return new AlertDialog.Builder(this)
   			.setItems(this.getResources().getStringArray(R.array.picture_op_items), new DialogInterface.OnClickListener() {
   				
   				@Override
   				public void onClick(DialogInterface dialog, int which) {
   					switch(which) {
   					case 0: //Gallery
   						mCurrentPictureRequest = CurrentPictureGalleryRequest;
   						onPickFromGalleryStart();
   						break;
   					case 1: //Camera
   						mCurrentPictureRequest = CurrentPictureCameraRequest;
   						onPickFromCameraStart();
   						break;
   					}
   					
   				}
   			})
   			.setNegativeButton(android.R.string.cancel, null)
   			.create();
   		case DIALOG_MEDIA_UNMOUNTED:
   			return new AlertDialog.Builder(this)
   			.setMessage(R.string.dialog_msg_media_unmounted)
   			.setCancelable(false)
   			.setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
   				
   				@Override
   				public void onClick(DialogInterface dialog, int which) {
   					onMediaUnmountedConfirmClick();
   					
   				}
   			})
   			.create();
   			 //add by chenkai, 20131201, add network check
   	      case DIALOG_DATA_NOT_CONNECTED:
   	    	  return ComConnectivityManager.getInstance().onCreateNoNetworkDialog(mContext);
   	      case DIALOG_PROGRESS:
   	    	  mProgressDialog = new ProgressDialog(this);
   	    	  mProgressDialog.setMessage(getString(R.string.msg_progressdialog_wait));
   	    	  mProgressDialog.setCancelable(false);
   	    	  return mProgressDialog;
   		}
   		return super.onCreateDialog(id);
   	}
       
       protected ProgressDialog getProgressDialog() {
    	   return mProgressDialog;
       }
       
     public void setShowHomeUp(boolean show) {
    	 mHomeBtn.setVisibility(show?View.VISIBLE:View.INVISIBLE);
     }
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.cncom__up:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
     	   if (upIntent == null) {
     		   // If we has configurated parent Activity in AndroidManifest.xml, we just finish current Activity.
     		   finish();
     		   return;
     	   }
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
            }
			break;
		case R.id.menu_more:
			break;
		}
	}
       
}