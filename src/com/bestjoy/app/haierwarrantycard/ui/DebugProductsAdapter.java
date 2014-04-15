package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.R;
import com.shwy.bestjoy.utils.Intents;

public class DebugProductsAdapter extends BaseAdapter implements View.OnClickListener{
	private Context mContext;
	private String[] mProducts = null;
	private String[] mProductsDeadline = null;
	private int mCount = 0;
	private static final int POSITION_DATA = 0;
	private static final int POSITION_NEW = 1;

	public DebugProductsAdapter(Context context) {
		mContext = context;
		mProducts = context.getResources().getStringArray(R.array.debug_products);
		mProductsDeadline = context.getResources().getStringArray(R.array.debug_products_deadline);
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
				convertView = LayoutInflater.from(mContext).inflate(R.layout.product_list_item, parent, false);
				holder = new ViewHolder();
				holder._flag = (ImageView) convertView.findViewById(R.id.flag);
				holder._name = (TextView) convertView.findViewById(R.id.name);
				holder._deadline = (TextView) convertView.findViewById(R.id.deadline);
				break;
			case POSITION_NEW:
				break;
		    }
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder._name.setText(mProducts[position]);
		holder._deadline.setText(getValidatedDays(position));
		holder._flag.setTag(position);
		holder._flag.setOnClickListener(this);
		
		return convertView;
	}
	
	private CharSequence getValidatedDays(int position) {
		String s = mProductsDeadline[position];
		int len = s.length();
		SpannableStringBuilder ssb = new SpannableStringBuilder(mContext.getString(R.string.title_unit_day_format, s));
		ssb.setSpan(new RelativeSizeSpan(2), 0, len, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#ff52c141")), 0, len, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		return ssb;
	}
	
	private class ViewHolder {
		private ImageView _flag;
		private TextView _name, _deadline;
	}

	@Override
	public void onClick(View v) {
		int pos = (Integer) v.getTag();
		Intent intent = RepairActivity.createIntnet(mContext);
		intent.putExtra(Intents.EXTRA_NAME, mProducts[pos]);
		mContext.startActivity(intent);
		
	}

}
