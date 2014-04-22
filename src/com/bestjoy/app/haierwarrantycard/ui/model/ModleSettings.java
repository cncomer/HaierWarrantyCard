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

import com.bestjoy.app.haierwarrantycard.R;
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
			bundle.putInt(Intents.EXTRA_TYPE, id);
			switch(id) {
			case R.id.model_my_card:
				bundle.putString(Intents.EXTRA_NAME, _context.getString(R.string.activity_title_choose_device_general));
				if (HaierAccountManager.getInstance().hasBaoxiuCards()) {
					MyChooseDevicesActivity.startIntent(_context, bundle);
				} else {
					NewCardActivity.startIntent(_context, bundle);
				}
				break;
			case R.id.model_install:
				bundle.putString(Intents.EXTRA_NAME, _context.getString(R.string.activity_title_choose_device_install));
				InstallActivity.startIntnet(_context);
				break;
			case R.id.model_repair:
				//bundle.putString(Intents.EXTRA_NAME, _context.getString(R.string.activity_title_choose_device_repair));
				//break;
				if (HaierAccountManager.getInstance().hasBaoxiuCards()) {
					MyChooseDevicesActivity.startIntent(_context, bundle);
				} else {
					RepairActivity.startIntent(_context, bundle);
				}
				return;
			case R.id.model_feedback:
				BrowserActivity.startActivity(_context, "http://m.rrs.com/rrsm/track/verify.html");
//				Intents.openURL(_context, "http://m.rrs.com/rrsm/track/verify.html");
				return;
			}
			
			
		}
		
	}

}
