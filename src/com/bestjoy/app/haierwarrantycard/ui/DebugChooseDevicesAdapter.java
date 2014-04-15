package com.bestjoy.app.haierwarrantycard.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.R;

public class DebugChooseDevicesAdapter extends BaseAdapter implements ListView.OnItemClickListener{
	private Activity mActivity;
	private int mChooseId = R.id.model_my_card;
	private String[] mProducts = null;
	private String[] mProductsType = null;
	private String[] mProductsDeadline = null;
	private int[] mProductsAvator = null;
	private String[] mProductsBrand = null;
	private String[] mProductsModel = null;
	private int mCount = 0;
	private static final int POSITION_DATA = 0;
	private static final int POSITION_NEW = 1;
	
	public static void addAdapter(Activity activity, ListView listview, int chooseType) {
		DebugChooseDevicesAdapter adapter = new DebugChooseDevicesAdapter(activity, chooseType);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(adapter);
	}

	public DebugChooseDevicesAdapter(Activity activity, int chooseType) {
		mActivity = activity;
		mChooseId = chooseType;
		mProducts = activity.getResources().getStringArray(R.array.debug_products);
		mProductsType = activity.getResources().getStringArray(R.array.debug_products_type);
		mProductsDeadline = activity.getResources().getStringArray(R.array.debug_products_deadline);
		mProductsBrand = activity.getResources().getStringArray(R.array.debug_products_brand);
		mProductsModel = activity.getResources().getStringArray(R.array.debug_products_model);
		mProductsAvator = activity.getResources().getIntArray(R.array.debug_products_avator);
		mCount = mProducts.length;
	}
	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	

	@Override
	public int getItemViewType(int position) {
		if (position < mCount) {
			return POSITION_DATA;
		}
		return POSITION_NEW;
	}
	@Override
	public int getViewTypeCount() {
		return 1;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewHolder holder = null;
		if (convertView == null) {
			switch(type){
			case POSITION_DATA:
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.devices_list_item, parent, false);
				holder = new ViewHolder();
				holder._avator = (ImageView) convertView.findViewById(R.id.avator);
				holder._name = (TextView) convertView.findViewById(R.id.name);
				holder._brand = (TextView) convertView.findViewById(R.id.brand);
				holder._model = (TextView) convertView.findViewById(R.id.model);
				holder._deadline = convertView.findViewById(R.id.deadline);
				holder._day = (TextView) convertView.findViewById(R.id.day);
				break;
			case POSITION_NEW:
				break;
		    }
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		switch(mChooseId){
		case R.id.model_install:
		case R.id.model_repair:
			holder._deadline.setVisibility(View.INVISIBLE);
			break;
			default:
				if (Integer.valueOf(mProductsDeadline[position]) > 0) {
					holder._day.setText(mActivity.getString(R.string.title_unit_day_format, mProductsDeadline[position]));
					holder._deadline.setVisibility(View.VISIBLE);
				} else {
					holder._deadline.setVisibility(View.INVISIBLE);
				}
				break;
		}
		
		holder._name.setText(mProducts[position]);
		holder._brand.setText(mProductsBrand[position]);
		holder._model.setText(mProductsModel[position]);
		holder._avator.setImageResource(mProductsAvator[position]);
		return convertView;
	}
	
	private CharSequence getValidatedDays(int position) {
		String s = mProductsDeadline[position];
		int len = s.length();
		SpannableStringBuilder ssb = new SpannableStringBuilder(mActivity.getString(R.string.title_unit_day_format, s));
		ssb.setSpan(new RelativeSizeSpan(2), 0, len, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#ff52c141")), 0, len, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		return ssb;
	}
	
	private class ViewHolder {
		private ImageView _avator;
		private TextView _name, _brand, _model, _day;
		private View _deadline;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch(mChooseId) {
		case R.id.model_install:
			break;
		case R.id.model_repair:
			break;
		case R.id.model_my_card:
			NewCardActivity.startIntent(mActivity);
			break;
		}
		
		mActivity.finish();
	}

}
