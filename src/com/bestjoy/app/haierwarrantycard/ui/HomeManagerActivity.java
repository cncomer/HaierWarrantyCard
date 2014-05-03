package com.bestjoy.app.haierwarrantycard.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;

public class HomeManagerActivity extends BaseActionbarActivity{

	private static final String TAG = "HomeManagerActivity";
	ListView mHomeListView;
	
	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_manager_main);

		mHomeListView = (ListView) findViewById(R.id.home_listview);
		HomeManagerAdapter adapter = new HomeManagerAdapter(this);
		mHomeListView.setAdapter(adapter);
		mHomeListView.setOnItemClickListener(adapter);
		mHomeListView.setAdapter(adapter);
	}

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, HomeManagerActivity.class);
		context.startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem newHomeItem = menu.add(R.string.menu_create, R.string.menu_create, 0, R.string.menu_create);
		MenuItem homeItem = menu.add(R.string.menu_delete, R.string.menu_delete, 0, R.string.menu_delete);
		homeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		newHomeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.string.menu_create:
			NewHomeActivity.startActivit(this);
			break;
		case R.string.menu_delete:
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(_context).inflate(R.layout.home_list_item, parent, false);
				holder = new ViewHolder();
				holder._name = (TextView) convertView.findViewById(R.id.home_name);
				holder._check = (CheckBox) convertView.findViewById(R.id.home_checkbox);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String name = HaierAccountManager.getInstance().getAccountObject().mAccountHomes.get(position).mHomeName;
			if(TextUtils.isEmpty(name)) name = _context.getString(R.string.my_home);
			holder._name.setText(name);
			return convertView;
		}
		
		private class ViewHolder {
			private CheckBox _check;
			private TextView _name;
			private ImageView _flag;
		}

		@Override
		public void onItemClick(AdapterView<?> listView, View view, int pos, long arg3) {
			Bundle bundle = new Bundle();
			switch(pos) {
			case R.id.model_my_card:
				break;
			case R.id.model_install:
				break;
			case R.id.model_repair:
				return;
			case R.id.model_feedback:
				BrowserActivity.startActivity(_context, "http://m.rrs.com/rrsm/track/verify.html");
				return;
			}
			
			
		}
		
	}
}
