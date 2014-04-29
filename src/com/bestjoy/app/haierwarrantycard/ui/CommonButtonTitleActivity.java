package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.ImageHelper;

public abstract class CommonButtonTitleActivity extends FragmentActivity implements View.OnClickListener{
    private static final String TAG = "CommonButtonTitleActivity";
    /**ProgressBar����*/
	protected ProgressBar mRefreshing;
	protected ImageView mBackBtn;
	protected TextView mTitle;
	protected Context mContext;
	
	private static final int CurrentPictureGalleryRequest = 11000;
	private static final int CurrentPictureCameraRequest = 11001;
	private int mCurrentPictureRequest;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (!checkIntent(getIntent())) {
			finish();
			DebugUtils.logD(TAG, "checkIntent() failed, finish this activiy");
			return;
		}
		mContext = this;
		this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
	}
	
	protected abstract boolean checkIntent(Intent intent);
	
	public static final int DIALOG_PICTURE_CHOOSE_CONFIRM = 10002;
	//add by chenkai, 20131208, for updating check
	/**��ʾû��SD����Ҫ���û�ȷ��*/
	protected static final int DIALOG_MEDIA_UNMOUNTED = 10003;
	
	public static final int DIALOG_DATA_NOT_CONNECTED = 10006;//��ǰû������Ի���
	public static final int DIALOG_MOBILE_TYPE_CONFIRM = 10007;//��������ʹ���ƶ�������ʾ�Ի���
	/**
	 * ͼ���ȡ������Ƭ
	 * @param uri ���ػ�ȡ����ͼƬ��Uri
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
	 * @param savedFile ������Ƭ��·��
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
	 * @param savedFile ������Ƭ��·��
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
				//ͼ���ȡ������Ƭ
				onPickFromGalleryFinish(data.getData());
			} else if (CurrentPictureCameraRequest == requestCode) {
				//��������Ƭ
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
		}
		return super.onCreateDialog(id);
	}
	
	public void initTitleBar() {
		if (mTitle == null) {
			DebugUtils.logD(TAG, "initTitleBar()");
			mTitle = (TextView) findViewById(R.id.title);
			mTitle.setText(getTitle());
			
			mBackBtn = (ImageView) findViewById(R.id.button_back);
			mBackBtn.setOnClickListener(this);
			
		}
		
	}
	
	@Override
	public void setContentView(int layoutId) {
		super.setContentView(layoutId);
		DebugUtils.logD(TAG, "setContentView()");
		initTitleBar();
	}
	
	@Override
	public void setTitle(int resId) {
		DebugUtils.logD(TAG, "setTitle()");
		initTitleBar();
		mTitle.setText(resId);
	}
	
	public void setTitle(String title) {
		initTitleBar();
		mTitle.setText(title);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_back:
			onBackPressed();
			break;
		}
		
	}
	
}
