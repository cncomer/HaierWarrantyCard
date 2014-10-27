package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.MyAccountManager;
import com.bestjoy.app.haierwarrantycard.im.IMHelper;
import com.bestjoy.app.haierwarrantycard.service.IMService;
import com.bestjoy.app.haierwarrantycard.update.UpdateService;
import com.bestjoy.app.haierwarrantycard.utils.MenuHandlerUtils;
import com.bestjoy.app.haierwarrantycard.utils.YouMengMessageHelper;
import com.bestjoy.app.haierwarrantycard.view.ModuleViewUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.FilesUtils;

public class BlueStyleMainActivity extends BaseNoActionBarActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_blue_style);
		setShowHomeUp(false);
		ModuleViewUtils.getInstance().setContext(mContext);
		ModuleViewUtils.getInstance().initModules(this);
		
		UpdateService.startUpdateServiceOnAppLaunch(mContext);
		YouMengMessageHelper.getInstance().startCheckDeviceTokenAsync();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}
	
	@Override
    public boolean onCreateTitleBarOptionsMenu(Menu menu) {
		 MenuHandlerUtils.onCreateOptionsMenu(menu);
		 return true;
    }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.string.menu_exit);
		if (item != null) {
			item.setVisible(MyAccountManager.getInstance().hasLoginned());
		}
		item = menu.findItem(R.string.menu_refresh);
		if (item != null) {
			item.setVisible(MyAccountManager.getInstance().hasLoginned());
		}
		
		item = menu.findItem(R.string.menu_setting);
		if (item != null) {
			item.setVisible(MyAccountManager.getInstance().hasLoginned());
		}
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!MenuHandlerUtils.onOptionsItemSelected(item, mContext)) {
			switch(item.getItemId()){
			case R.string.menu_exit:
				 new AlertDialog.Builder(mContext)
					.setMessage(R.string.msg_existing_system_confirm)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteAccountAsync();
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.show();
				 return true;
			 case R.string.menu_refresh:
				 if (MyAccountManager.getInstance().hasLoginned()) {
					 //做一次登陆操作
					 //目前只删除本地的所有缓存文件
					 File dir = MyApplication.getInstance().getCachedXinghaoInternalRoot();
					 FilesUtils.deleteFile("Updating ", dir);
					 
					 dir = MyApplication.getInstance().getCachedXinghaoExternalRoot();
					 if (dir != null) {
						 FilesUtils.deleteFile("Updating ", dir);
					 }
				 }
				 break;
			}
		}
		return true;
	}
	
	private DeleteAccountTask mDeleteAccountTask;
	 private void deleteAccountAsync() {
		 AsyncTaskUtils.cancelTask(mDeleteAccountTask);
		 showDialog(DIALOG_PROGRESS);
		 mDeleteAccountTask = new DeleteAccountTask();
		 mDeleteAccountTask.execute();
	 }
	 private class DeleteAccountTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			IMService.disconnectIMService(mContext, MyAccountManager.getInstance().getAccountObject());
			//删除所有的账户相关的即时通信信息
			IMHelper.deleteAllMessages(getContentResolver(), MyAccountManager.getInstance().getCurrentAccountId());
			MyAccountManager.getInstance().deleteDefaultAccount();
			MyAccountManager.getInstance().saveLastUsrTel("");
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_PROGRESS);
			invalidateOptionsMenu();
			MyApplication.getInstance().showMessage(R.string.msg_op_successed);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			invalidateOptionsMenu();
			dismissDialog(DIALOG_PROGRESS);
		}
		
		
		 
	 }
	
	

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
	
	/**
	 * 回到主界面
	 * @param context
	 */
	public static void startActivityForTop(Context context) {
		Intent intent = new Intent(context, BlueStyleMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}


}
