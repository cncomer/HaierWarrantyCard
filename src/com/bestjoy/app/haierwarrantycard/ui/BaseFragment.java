package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.actionbarsherlock.app.SherlockFragment;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.ImageHelper;
import com.shwy.bestjoy.utils.InfoInterface;
import com.shwy.bestjoy.utils.Intents;

public class BaseFragment extends SherlockFragment{

	/**请求扫描条码*/
	public static final int REQUEST_SCAN = 10000;
	
	private static final int CurrentPictureGalleryRequest = 101000;
	private static final int CurrentPictureCameraRequest = 101001;
	
	public static final int DIALOG_PICTURE_CHOOSE_CONFIRM = 101002;
	//add by chenkai, 20131208, for updating check
	/**SD不可用*/
	protected static final int DIALOG_MEDIA_UNMOUNTED = 101003;
	
	public static final int DIALOG_DATA_NOT_CONNECTED = 101006;//数据连接不可用
	public static final int DIALOG_MOBILE_TYPE_CONFIRM = 101007;//
	public static final int DIALOG_PROGRESS = 101008;
	private ProgressDialog mProgressDialog;
	
	private int mCurrentPictureRequest = -1;
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
   		super.onActivityResult(requestCode, resultCode, data);
   		if (resultCode == Activity.RESULT_OK) {
   			if (CurrentPictureGalleryRequest == requestCode) {
   				onPickFromGalleryFinish(data.getData());
   			} else if (CurrentPictureCameraRequest == requestCode) {
   				onPickFromCameraFinish();
   				
   			} else if (requestCode == REQUEST_SCAN) {
   			   //识别到了信息
			   setScanObjectAfterScan(getScanObjectAfterScan());
   			}
   		}
   	}
    /**
     * 请求条码扫描
     */
    public void startScan() {
		Intent scanIntent = new Intent(getActivity(), CaptureActivity.class);
		scanIntent.putExtra(Intents.EXTRA_SCAN_TASK, true);
		startActivityForResult(scanIntent, REQUEST_SCAN);
	}
    
    /**
	 * 当使用条码识别扫描返回了识别对象，会调用该方法，子类需要条码识别功能的话，需要覆盖该方法自行处理结果
	 * @param baoxiuCardObject
	 */
	public void setScanObjectAfterScan(InfoInterface barCodeObject) {
		
	}
	/**
	 * 子类将实现该方法返回条码识别后能够得到的对象，将在setScanObjectAfterScan()方法中使用
	 * @return
	 */
	public InfoInterface getScanObjectAfterScan() {
		return null;
	}
       
   	public Dialog onCreateDialog(int id) {
   		switch(id) {
   		case DIALOG_PICTURE_CHOOSE_CONFIRM:
   			return new AlertDialog.Builder(getActivity())
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
   			return new AlertDialog.Builder(getActivity())
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
   	    	  return ComConnectivityManager.getInstance().onCreateNoNetworkDialog();
   	      case DIALOG_PROGRESS:
   	    	  mProgressDialog = new ProgressDialog(this.getActivity());
   	    	  mProgressDialog.setMessage(getString(R.string.msg_progressdialog_wait));
   	    	  mProgressDialog.setCancelable(false);
   	    	  return mProgressDialog;
   		}
   		return null;
   	}

    protected ProgressDialog getProgressDialog() {
 	   return mProgressDialog;
    }
    
   	public void showDialog(int id) {
   		Dialog dialog = onCreateDialog(id);
   		if (dialog != null) {
   			dialog.show();
   		}
   	}
}
