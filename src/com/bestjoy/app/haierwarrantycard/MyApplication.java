package com.bestjoy.app.haierwarrantycard;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.bestjoy.app.haierwarrantycard.service.PhotoManagerService;
import com.bestjoy.app.haierwarrantycard.ui.PreferencesActivity;
import com.bestjoy.app.haierwarrantycard.utils.BeepAndVibrate;
import com.bestjoy.app.haierwarrantycard.utils.BitmapUtils;
import com.bestjoy.app.haierwarrantycard.utils.InstallFileUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.DeviceStorageUtils;
import com.shwy.bestjoy.utils.DevicesUtils;
import com.shwy.bestjoy.utils.SecurityUtils.SecurityKeyValuesObject;

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
		//��ʼ���豸�����࣬���ڵõ��豸��Ϣ
		DevicesUtils.getInstance().setContext(this);
		DeviceStorageUtils.getInstance().setContext(this);
//		//��ʼ���˺Ź�����
//		BjnoteAccountsManager.getInstance().setContext(this);
//		IncomingCallCallbackImp.getInstance().setContext(this);
//		OutgoingCallCallbackImp.getInstance().setContext(this);
//		IncomingSmsCallbackImp.getInstance().setContext(this);
//		initMonitorService();
//		ModuleSettings.getInstance().setContext(this);
		//��ʼ����Ƭ�������,������PhotoManagerService����ά��
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
		//add by chenkai, 20131201, 网络监听
		ComConnectivityManager.getInstance().setContext(this);
		BeepAndVibrate.getInstance().setContext(this);
		
		BitmapUtils.getInstance().setContext(this);
		
		HaierAccountManager.getInstance().setContext(this);
		
		//add by chenkai, 20140419, 图片异步加载服务
		startService(PhotoManagerService.getServiceIntent(this));
		//第一次的时候我们需要拷贝数据库
		SharedPreferences sharePrefers = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharePrefers.getBoolean(PreferencesActivity.KEY_FIRST_STARTUP, true)) {
			InstallFileUtils.installDatabaseFiles(MyApplication.this, HaierDBHelper.DB_DEVICE_NAME);
			sharePrefers.edit().putBoolean(PreferencesActivity.KEY_FIRST_STARTUP, false).commit();
		}
		
	}
	
	public synchronized static MyApplication getInstance() {
		return mInstance;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
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
	
	//add by chenkai, 20131123, security support begin
    private SecurityKeyValuesObject mSecurityKeyValuesObject;
    public SecurityKeyValuesObject getSecurityKeyValuesObject() {
    	if (mSecurityKeyValuesObject == null) {
    		//Here, we need to notice.
    		new Exception("warnning getSecurityKeyValuesObject() return null").printStackTrace();
    	}
    	return mSecurityKeyValuesObject;
    }
    public void setSecurityKeyValuesObject(SecurityKeyValuesObject securityKeyValuesObject) {
    	mSecurityKeyValuesObject = securityKeyValuesObject;
    }
}
