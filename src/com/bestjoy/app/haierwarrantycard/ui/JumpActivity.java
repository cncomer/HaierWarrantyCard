package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.update.ServiceAppInfo;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.FilesUtils;
import com.shwy.bestjoy.utils.Intents;

/**
 * 应用入口，对手机系统版本进行判断，从而选择适合的组件，出于向前兼容1.5版本的考虑
 * 
 * @author chenkai
 * 
 */
public class JumpActivity extends Activity {
	private String TAG = "JumpActivity";

	private static final int DIALOG_MUST_INSTALL = 100001;
	private static final int DIALOG_CONFIRM_INSTALL = 100002;
	
	private ServiceAppInfo mServiceAppInfo;
	private Context mContext;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mContext = this;
		setContentView(R.layout.splash);
		mServiceAppInfo = ServiceAppInfo.read();
		showHelpOnFirstLaunch();
		
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	/**
	 * We want the help screen to be shown automatically the first time a new
	 * version of the app is run. The easiest way to do this is to check
	 * android:versionCode from the manifest, and compare it to a value stored
	 * as a preference.
	 */
	private void showHelpOnFirstLaunch() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			int currentVersion = info.versionCode;
			String currentVersionCodeName = info.versionName;

			int lastVersion = MyApplication.getInstance().mPreferManager.getInt(PreferencesActivity.KEY_LATEST_VERSION, 0);
			if (currentVersion != lastVersion) {// 安装好后第一次启动
				// 设置版本号
				DebugUtils.logD(TAG, "showHelpOnFirstLaunch");
				SharedPreferences.Editor edit = MyApplication.getInstance().mPreferManager.edit();
				edit.putInt(PreferencesActivity.KEY_LATEST_VERSION, currentVersion);
				edit.putString(PreferencesActivity.KEY_LATEST_VERSION_CODE_NAME, currentVersionCodeName);
				
				edit.putBoolean(PreferencesActivity.KEY_LATEST_VERSION_INSTALL, true);
				edit.putLong(PreferencesActivity.KEY_LATEST_VERSION_LEVEL, 0);
				edit.commit();

				//删除下载更新的临时目录，确保没有其他的安装包了
				FilesUtils.deleteFile(TAG, MyApplication.getInstance().getExternalStorageRoot(".download"));

				launchMainActivity();
			} else {// 不是第一次启动
					// 是否完成上次下载的更新的安装
				DebugUtils.logD(TAG, "not FirstLaunch");
				if (mServiceAppInfo == null) {
					DebugUtils.logD(TAG, "mServiceAppInfo is null, maybe we do not start to updating check");
				} else {
					File localApkFile = MyApplication.getInstance().buildLocalDownloadAppFile(mServiceAppInfo.mVersionCode);
					//如果更新包存在，并且更新包的版本高于当前版本，我们认为是下载了更新包当是没有安装
					if (localApkFile.exists() && mServiceAppInfo.mVersionCode > currentVersion) {
						if (!MyApplication.getInstance().mPreferManager.getBoolean(PreferencesActivity.KEY_LATEST_VERSION_INSTALL, true)) {
							// 是否放弃安装，如果放弃，且重要程度为1则不在进行提示，否则必须安装
							if (MyApplication.getInstance().mPreferManager.getLong(PreferencesActivity.KEY_LATEST_VERSION_LEVEL, ServiceAppInfo.IMPORTANCE_OPTIONAL) == ServiceAppInfo.IMPORTANCE_OPTIONAL) {
								showDialog(DIALOG_CONFIRM_INSTALL);
							} else {
								showDialog(DIALOG_MUST_INSTALL);
							}
							return;
						}
					}
				}
				launchMainActivity();
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void launchMainActivity() {
		MyApplication.getInstance().postDelay(new Runnable() {

			@Override
			public void run() {
				MainActivity.startActivityForTop(mContext);
				finish();
			}
			
		}, 1000);
		
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_CONFIRM_INSTALL:
		case DIALOG_MUST_INSTALL:
			
			AlertDialog.Builder builder =  new AlertDialog.Builder(JumpActivity.this)
			.setTitle(R.string.app_update_title)
			.setCancelable(false)
			.setMessage(R.string.app_update_not_install)
			.setPositiveButton(R.string.button_update_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							File localApk = MyApplication.getInstance().buildLocalDownloadAppFile(mServiceAppInfo.mVersionCode);
							Intents.install(JumpActivity.this, localApk);
						}
					});
			if (id == DIALOG_CONFIRM_INSTALL) {
				builder.setNegativeButton(R.string.button_update_no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								MyApplication.getInstance().mPreferManager.edit().putBoolean(PreferencesActivity.KEY_LATEST_VERSION_INSTALL, true).commit();
								launchMainActivity();
							}
						});
			} else {
				builder.setNegativeButton(R.string.button_update_no,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int which) {
								finish();
							}
						});
			}
			return builder.create();
			default:
				return super.onCreateDialog(id);
		}
		
	}

}
