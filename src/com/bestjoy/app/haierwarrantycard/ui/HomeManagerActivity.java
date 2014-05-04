package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.HaierServiceObject.HaierResultObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.SecurityUtils;

public class HomeManagerActivity extends BaseActionbarActivity{

	private static final String TAG = "HomeManagerActivity";
	private ListView mHomeListView;
	private HomeManagerAdapter mHomeManagerAdapter;
	private MenuItem deleteItem;
	private MenuItem editItem;
	private static boolean mIsEditMode;
	private static ArrayList<String> deleteHomeIDList = new ArrayList<String>();
	
	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_manager_main);

		mHomeListView = (ListView) findViewById(R.id.home_listview);
		mHomeManagerAdapter = new HomeManagerAdapter(this);
		mHomeListView.setAdapter(mHomeManagerAdapter);
		mHomeListView.setOnItemClickListener(mHomeManagerAdapter);
		mHomeListView.setAdapter(mHomeManagerAdapter);
	}

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, HomeManagerActivity.class);
		context.startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem newHomeItem = menu.add(R.string.menu_create, R.string.menu_create, 0, R.string.menu_create);
		deleteItem = menu.add(R.string.menu_delete, R.string.menu_delete, 0, R.string.menu_delete);
		editItem = menu.add(R.string.menu_edit, R.string.menu_edit, 0, R.string.menu_edit);
		deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		newHomeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		editItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		editItem.setVisible(true);
		deleteItem.setVisible(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.string.menu_create:
			NewHomeActivity.startActivity(this);
			break;
		case R.string.menu_delete:
			if(deleteHomeIDList.size() <= 0) {
				MyApplication.getInstance().showMessage(R.string.none_select_tips);
			} else {				
				doDeleteHomeAsync();
			}
			break;
		case R.string.menu_edit:
			editItem.setVisible(false);
			deleteItem.setVisible(true);
			mHomeManagerAdapter.notifyDataSetChanged();
			mIsEditMode = true;
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	DeleteHomeAsyncTask mDeleteHomeAsyncTask;
	private void doDeleteHomeAsync() {
		AsyncTaskUtils.cancelTask(mDeleteHomeAsyncTask);
		showDialog(DIALOG_PROGRESS);
		mDeleteHomeAsyncTask = new DeleteHomeAsyncTask();
		mDeleteHomeAsyncTask.execute();
		
	}
	
	private class DeleteHomeAsyncTask extends AsyncTask<Integer, Void, Boolean> {
		private String mError;
		@Override
		protected Boolean doInBackground(Integer... param) {
			mError = null;
			for(String mAID : deleteHomeIDList) {
				delete(mAID);
			}
			return false;
		}

		private synchronized void delete(String AID) {
			InputStream is = null;
			final int LENGTH = 2;
			String[] urls = new String[LENGTH];
			String[] paths = new String[LENGTH];
			urls[0] = HaierServiceObject.HOME_DELETE_URL + "AID=";
			paths[0] = AID;
			urls[1] = "&key=";
			paths[1] = SecurityUtils.MD5.md5(HaierAccountManager.getInstance().getAccountObject().mAccountTel + HaierAccountManager.getInstance().getAccountObject().mAccountPwd);
			DebugUtils.logD(TAG, "urls = " + Arrays.toString(urls));
			DebugUtils.logD(TAG, "paths = " + Arrays.toString(paths));
			try {
				is = NetworkUtils.openContectionLocked(urls, paths, MyApplication.getInstance().getSecurityKeyValuesObject());
				if (is != null) {
					String content = NetworkUtils.getContentFromInput(is);
					HaierResultObject resultObject = HaierResultObject.parse(content);
					MyApplication.getInstance().showMessageAsync(resultObject.mStatusMessage);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				mError = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();

				mError = e.getMessage();
			} finally {
				NetworkUtils.closeInputStream(is);
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(mError != null) {
				MyApplication.getInstance().showMessageAsync(mError);
			}
			dismissDialog(DIALOG_PROGRESS);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissDialog(DIALOG_PROGRESS);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if(mIsEditMode) {
				mIsEditMode = false;
				editItem.setVisible(true);
				deleteItem.setVisible(false);
				mHomeManagerAdapter.notifyDataSetChanged();
				if(deleteHomeIDList != null) deleteHomeIDList.clear();
				return true;
			}
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	public static class HomeManagerAdapter extends BaseAdapter implements ListView.OnItemClickListener{

		private Context _context;
		private HomeManagerAdapter (Context context) {
			_context = context;
		}
		@Override
		public int getCount() {
			return HaierAccountManager.getInstance().getAccountObject().mAccountHomeCount;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int index = position;
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(_context).inflate(R.layout.home_list_item, parent, false);
				holder = new ViewHolder();
				holder._name = (TextView) convertView.findViewById(R.id.home_name);
				holder._check = (CheckBox) convertView.findViewById(R.id.home_checkbox);
				holder._home_detail = (TextView) convertView.findViewById(R.id.home_detail);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if(mIsEditMode) {
				holder._check.setVisibility(View.VISIBLE);
			} else {
				holder._check.setVisibility(View.INVISIBLE);
			}
			holder._check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						deleteHomeIDList.add(String.valueOf(HaierAccountManager.getInstance().getAccountObject().mAccountHomes.get(index).mHomeAid));
					}else{
						deleteHomeIDList.remove(String.valueOf(HaierAccountManager.getInstance().getAccountObject().mAccountHomes.get(index).mHomeAid));
					}
				}
			});
			if(deleteHomeIDList.contains(String.valueOf(HaierAccountManager.getInstance().getAccountObject().mAccountHomes.get(index).mHomeAid))) {
				holder._check.setChecked(true);
			} else {
				holder._check.setChecked(false);
			}
			HomeObject homeObject = HaierAccountManager.getInstance().getAccountObject().mAccountHomes.get(position);
			String name = homeObject.mHomeName;
			String nameDtail = homeObject.mHomeProvince + homeObject.mHomeCity + homeObject.mHomeDis;
			if(TextUtils.isEmpty(name)) name = _context.getString(R.string.my_home);
			holder._name.setText(name);
			holder._home_detail.setText(nameDtail);
			return convertView;
		}
		
		private class ViewHolder {
			private CheckBox _check;
			private TextView _name, _home_detail;
			private ImageView _flag;
		}

		@Override
		public void onItemClick(AdapterView<?> listView, View view, int pos, long arg3) {
			if(mIsEditMode) {
				CheckBox cb = (CheckBox) view.findViewById(R.id.home_checkbox);
				cb.setChecked(!cb.isChecked());
			} else {
				NewHomeActivity.startActivity(listView.getContext(), pos);
			}
		}
		
	}
}
