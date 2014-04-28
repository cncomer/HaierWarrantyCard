package com.bestjoy.app.haierwarrantycard.ui.model;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.ui.BrowserActivity;
import com.bestjoy.app.haierwarrantycard.ui.InstallActivity;
import com.bestjoy.app.haierwarrantycard.ui.MyChooseDevicesActivity;
import com.bestjoy.app.haierwarrantycard.ui.NewCardActivity;
import com.bestjoy.app.haierwarrantycard.ui.RepairActivity;
import com.shwy.bestjoy.utils.Intents;

public class ModleSettings {
	
	private static final int[] MODLE_TITLE = new int[]{
		R.string.model_my_card,
		R.string.model_install,
		R.string.model_repair,
		R.string.model_feedback,
	};
	
	private static final int[] MODLE_ICON = new int[]{
		R.drawable.model_my_card,
		R.drawable.model_install,
		R.drawable.model_repair,
		R.drawable.model_feedback,
	};
	
	private static final int[] MODLE_ID = new int[]{
		R.id.model_my_card,
		R.id.model_install,
		R.id.model_repair,
		R.id.model_feedback,
	};
	
	public static void addModelsAdapter(Context context, ListView listView) {
		ModelsAdapter adapter = new ModelsAdapter(context);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
	}
	
	public static class ModelsAdapter extends BaseAdapter implements ListView.OnItemClickListener{

		private Context _context;
		private int _count = 0;
		private ModelsAdapter (Context context) {
			_context = context;
			_count = MODLE_TITLE.length;
		}
		@Override
		public int getCount() {
			return _count;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		private int getModelId(int position) {
			return MODLE_ID[position];
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(_context).inflate(R.layout.model_list_item, parent, false);
				holder = new ViewHolder();
				holder._name = (TextView) convertView.findViewById(R.id.model_name);
				holder._icon = (ImageView) convertView.findViewById(R.id.model_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder._name.setText(MODLE_TITLE[position]);
			holder._icon.setImageResource(MODLE_ICON[position]);
			
			return convertView;
		}
		
		private class ViewHolder {
			private ImageView _icon, _flag;
			private TextView _name;
		}

		@Override
		public void onItemClick(AdapterView<?> listView, View view, int pos, long arg3) {
			Bundle bundle = new Bundle();
			int id = getModelId(pos);
			switch(id) {
			case R.id.model_my_card:
				if (HaierAccountManager.getInstance().hasBaoxiuCards()) {
					MyChooseDevicesActivity.startIntent(_context, createMyCardDefaultBundle(_context));
				} else {
//					NewCardActivity.startIntent(_context, createMyCardDefaultBundle(_context));
					NewCardActivity.startIntent(_context, createMyCardDefaultBundle(_context));
				}
				break;
			case R.id.model_install:
				if (HaierAccountManager.getInstance().hasBaoxiuCards()) {
					MyChooseDevicesActivity.startIntent(_context, createMyInstallDefaultBundle(_context));
				} else {
//					InstallActivity.startIntent(_context, createMyInstallDefaultBundle(_context));
					NewCardActivity.startIntent(_context, createMyInstallDefaultBundle(_context));
				}
				break;
			case R.id.model_repair:
				//bundle.putString(Intents.EXTRA_NAME, _context.getString(R.string.activity_title_choose_device_repair));
				//break;
				if (HaierAccountManager.getInstance().hasBaoxiuCards()) {
					MyChooseDevicesActivity.startIntent(_context, createMyRepairDefaultBundle(_context));
				} else {
					NewCardActivity.startIntent(_context, createMyRepairDefaultBundle(_context));
//					RepairActivity.startIntent(_context, createMyRepairDefaultBundle(_context));
				}
				return;
			case R.id.model_feedback:
				BrowserActivity.startActivity(_context, "http://m.rrs.com/rrsm/track/verify.html");
				return;
			}
			
			
		}
		
	}
	
	/**
	 * 处理设备选择后的回调,注意的是我的保修卡选择后要进入到保修卡详细界面
	 * @param type
	 */
	public static void doChoose(Context context, Bundle bundle) {
		int type = ModleSettings.getModelIdFromBundle(bundle);
		switch(type) {
		case R.id.model_my_card:
		case R.id.model_install:
		case R.id.model_repair:
			NewCardActivity.startIntent(context, bundle);
		case R.id.model_feedback:
			break;
		}
		
	}
	
	public static boolean createActionBarMenu(Menu menu, Bundle bundle) {
		if (bundle == null || bundle.getInt(Intents.EXTRA_TYPE) == 0) {
			return false;
		}
		int type = bundle.getInt(Intents.EXTRA_TYPE);
		switch(type) {
		case R.id.model_my_card:{
			MenuItem item = menu.add(0, type, 0, R.string.menu_new_card);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		}
		case R.id.model_install:{
			MenuItem item = menu.add(0, type, 0, R.string.menu_new_install);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		}
		case R.id.model_repair:{
			MenuItem item = menu.add(0, type, 0, R.string.menu_new_repair);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			break;
		}
		}
		return true;
	}
	
	public static boolean onActionBarMenuSelected(MenuItem item, Context context, Bundle bundle) {
		switch(item.getItemId()) {
		case R.id.model_my_card:
		case R.id.model_install:
		case R.id.model_repair:
			ModleSettings.doChoose(context, bundle);
			return true;
		}
		return false;
	}
	/**
	 * 可以使用这个来创建选择设备的Bundle数据
	 * @param context
	 * @return
	 */
	public static Bundle createMyCardDefaultBundle(Context context) {
		Bundle bundle = new Bundle();
		bundle.putInt(Intents.EXTRA_TYPE, R.id.model_my_card);
		bundle.putString(Intents.EXTRA_NAME, context.getString(R.string.activity_title_choose_device_general));
		return bundle;
	}
	
	/**
	 * 可以使用这个来创建选择安装的Bundle数据
	 * @param context
	 * @return
	 */
	public static Bundle createMyInstallDefaultBundle(Context context) {
		Bundle bundle = new Bundle();
		bundle.putInt(Intents.EXTRA_TYPE, R.id.model_install);
		bundle.putString(Intents.EXTRA_NAME, context.getString(R.string.activity_title_choose_device_general));
		return bundle;
	}
	
	/**
	 * 可以使用这个来创建维修的Bundle数据
	 * @param context
	 * @return
	 */
	public static Bundle createMyRepairDefaultBundle(Context context) {
		Bundle bundle = new Bundle();
		bundle.putInt(Intents.EXTRA_TYPE, R.id.model_repair);
		bundle.putString(Intents.EXTRA_NAME, context.getString(R.string.activity_title_choose_device_general));
		return bundle;
	}

	
	public static int getModelIdFromBundle(Bundle modelBundle) {
		if (modelBundle == null) {
			return -1;
		}
		return modelBundle.getInt(Intents.EXTRA_TYPE, -1);
	}
}
