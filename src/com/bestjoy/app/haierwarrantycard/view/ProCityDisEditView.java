package com.bestjoy.app.haierwarrantycard.view;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;

public class ProCityDisEditView extends LinearLayout implements AdapterView.OnItemClickListener {
	private static final String TAG = "ProCityDisEditView";
	private Context mContext;
	private TextView mHomeName;
	private AddressCompleteEditText mProEditView;
	private AddressCompleteEditText mCityEditView;
	private AddressCompleteEditText mDisEditView;
	private EditText mAddressEditView;
	
	private ContentResolver mCr;
	private static final int MODE_PROVINCE = 1;
	private static final int MODE_CITY = MODE_PROVINCE + 1;
	private static final int MODE_DISTRICT = MODE_CITY + 1;


	public ProCityDisEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public ProCityDisEditView(Context context) {
		super(context);
		mContext = context;
	}
	
	public HomeObject getHomeObject() {
		HomeObject homeObject = new HomeObject();
		homeObject.mHomeProvince = mProEditView.getText().toString();
		homeObject.mHomeCity = mCityEditView.getText().toString();
		homeObject.mHomeDis = mDisEditView.getText().toString();
		homeObject.mHomePlaceDetail = mAddressEditView.getText().toString();
		return homeObject;
	}
	
	public void setHomeObject(HomeObject homeObject) {
		 initHomeView(homeObject);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mCr = mContext.getContentResolver();
		mProEditView = (AddressCompleteEditText) findViewById(R.id.input_province);
		mCityEditView = (AddressCompleteEditText) findViewById(R.id.input_city);
		mDisEditView = (AddressCompleteEditText) findViewById(R.id.input_district);
		
		mProEditView.setOnItemClickListener(this);
		mProEditView.setAdapter(new AddressAdapter(mContext, MODE_PROVINCE, true));
		mCityEditView.setOnItemClickListener(this);
		mCityEditView.setAdapter(new AddressAdapter(mContext, MODE_CITY, true));
		mDisEditView.setAdapter(new AddressAdapter(mContext, MODE_DISTRICT, true));
		
//		mProEditView.setAdapter(new AddressAdapter());
		
		mAddressEditView = (EditText) findViewById(R.id.address);
		mHomeName = (TextView) findViewById(R.id.homeName);
	}
	
	private void initHomeView(HomeObject homeObject) {
		mHomeName.setText(homeObject.mHomeName);
		mProEditView.setText(homeObject.mHomeProvince, false);
		mCityEditView.setText(homeObject.mHomeCity, false);
		mDisEditView.setText(homeObject.mHomeDis, false);
		mAddressEditView.setText(homeObject.mHomePlaceDetail);
	}
	
	private class AddressAdapter extends CursorAdapter {
		private int mode;

		public AddressAdapter(Context context, int mode, boolean autoRequery) {
			super(context, null, autoRequery);
			this.mode = mode;
		}
		
		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			String name = null;
			if (TextUtils.isEmpty(constraint)) {
				name = null;
			} else {
				name = constraint.toString().trim();
			}
			
			switch(mode){
			case MODE_PROVINCE:
				return HomeObject.getProvincesLike(mCr,name);
			case MODE_CITY: {
				long proId = HomeObject.getProvinceId(mCr, mProEditView.getText().toString().trim());
				if (proId != -1) {
					return HomeObject.getCitiesLike(mCr, proId, name);
				} else {
					return null;
				}
			}
			case MODE_DISTRICT: {
				long cityId = HomeObject.getCityId(mCr, mCityEditView.getText().toString().trim());
				if (cityId != -1) {
					return HomeObject.getDistrictsLike(mCr, cityId, name);
				}
				return null;
			}
			}
			
			return super.runQueryOnBackgroundThread(constraint);
		}

		/**返回name*/
		public Object getItem(int position) {
			Cursor c = getCursor();
			c.moveToPosition(position);
			switch(mode){
			case MODE_PROVINCE:
			case MODE_CITY:
			case MODE_DISTRICT:
			}
			return c.getString(1);
		}

		public long getItemId(int position) {
			Cursor c = getCursor();
			c.moveToPosition(position);
			switch(mode){
			case MODE_PROVINCE:
				break;
			case MODE_CITY:
				break;
			case MODE_DISTRICT:
				break;
			}
			return c.getLong(0);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((TextView) view).setText(cursor.getString(1));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.grid_item, null);
		}
	}
	
	private static class ViewHolder {
		private TextView _title;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
