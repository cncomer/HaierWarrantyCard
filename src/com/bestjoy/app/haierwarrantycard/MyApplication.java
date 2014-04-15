package com.bestjoy.app.haierwarrantycard;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bestjoy.app.haierwarrantycard.utils.BitmapUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.DeviceStorageUtils;
import com.shwy.bestjoy.utils.DevicesUtils;

public class MyApplication extends Application{
	
	private static final String TAG ="BJfileApp";
	private Handler mHandler;
	private static MyApplication mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler();
		mInstance = this;
		// init all preference default values.
//		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		//初始化设备工具类，用于得到设备信息
		DevicesUtils.getInstance().setContext(this);
		DeviceStorageUtils.getInstance().setContext(this);
//		//初始化账号管理器
//		BjnoteAccountsManager.getInstance().setContext(this);
//		IncomingCallCallbackImp.getInstance().setContext(this);
//		OutgoingCallCallbackImp.getInstance().setContext(this);
//		IncomingSmsCallbackImp.getInstance().setContext(this);
//		initMonitorService();
//		ModuleSettings.getInstance().setContext(this);
		//初始化照片管理对象,现在由PhotoManagerService负责维护
//		PhotoManagerUtils.getInstance().setContext(this);
//		startService(PhotoManagerService.getServiceIntent(this));
		
		DateUtils.getInstance().setContext(this);
//		VcfAsyncDownloadUtils.getInstance().setContext(this);
//		BeepAndVibrate.getInstance().setContext(this);
//		AddrBookUtils.getInstance().setContext(this);
//		GoodsManager.getInstance().setContext(this);
//		
//		//add by chenkai, 2013-07-21
//		MyLifeManager.getInstance().setContext(this);
//		ContactBackupManager.getInstance().setContext(this);
//		
//		Contact.init(this);
		//add by chenkai, 20131201, 增加网络状态管理器
		ComConnectivityManager.getInstance().setContext(this);
		
		BitmapUtils.getInstance().setContext(this);
	}
	
	public synchronized static MyApplication getInstance() {
		return mInstance;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		//add by chenkai, 20131201, 增加网络状态管理器
		ComConnectivityManager.getInstance().endConnectivityMonitor();
	}
	
	
	public boolean hasExternalStorage() {
	    	return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public void showMessageAsync(final int resId) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mInstance, resId, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void showMessageAsync(final String msg) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mInstance, msg, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void showMessageAsync(final int resId, final int length) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mInstance, resId, resId).show();
			}
		});
	}
	
	public void showMessageAsync(final String msg, final int length) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mInstance, msg, length).show();
			}
		});
	}
	
	public void showShortMessageAsync(final int msgId, final int toastId) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mInstance, msgId, toastId).show();
			}
		});
	}
	
	public void showMessage(int resId) {
		Toast.makeText(mInstance, resId, Toast.LENGTH_LONG).show();
	}
	
	public void showMessage(String msg) {
		Toast.makeText(mInstance, msg, Toast.LENGTH_LONG).show();
	}
	public void showMessage(String msg, int length) {
		Toast.makeText(mInstance, msg, length).show();
	}
	
	public void showMessage(int resId, int length) {
		Toast.makeText(mInstance, resId, length).show();
	}
	
	public void showShortMessage(int resId) {
		showMessage(resId, Toast.LENGTH_SHORT);
	}
	
	public void postAsync(Runnable runnable){
		mHandler.post(runnable);
	}
	public void postDelay(Runnable runnable, long delayMillis){
		mHandler.postDelayed(runnable, delayMillis);
	}
	
	
	public void showUnsupportMessage() {
    	showMessage(R.string.msg_unsupport_operation);
    }
}
