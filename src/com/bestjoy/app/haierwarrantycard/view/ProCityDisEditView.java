package com.bestjoy.app.haierwarrantycard.view;

import java.lang.reflect.Field;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

public class ProCityDisEditView extends LinearLayout{
	private static final String TAG = "ProCityDisEditView";
	private HomeObject mHomeObject;
	private Context mContext;
	private EditText mHomeName;
	private AutoCompleteTextView mProEditView;
	private AutoCompleteTextView mCityEditView;
	private AutoCompleteTextView mDisEditView;
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
	
	public void updateHomeObject() {
		if (mHomeObject == null ) {
			return;
		}
		mHomeObject.mHomeProvince = mProEditView.getText().toString();
		mHomeObject.mHomeCity = mCityEditView.getText().toString();
		mHomeObject.mHomeDis = mDisEditView.getText().toString();
		mHomeObject.mHomePlaceDetail = mAddressEditView.getText().toString();
	}
	
	public HomeObject getHomeObject() {
		if (mHomeObject == null ) {
			mHomeObject = new HomeObject();
		}
		mHomeObject.mHomeProvince = mProEditView.getText().toString();
		mHomeObject.mHomeCity = mCityEditView.getText().toString();
		mHomeObject.mHomeDis = mDisEditView.getText().toString();
		mHomeObject.mHomePlaceDetail = mAddressEditView.getText().toString();
		if (mHomeObject.hasValidateAddress()) {
			return null;
		}
		return mHomeObject;
	}
	
	public void setHomeObject(HomeObject homeObject) {
		 mHomeObject = homeObject;
		 initHomeView(homeObject);
	}

	public void setHomeEditVisiable(int visibility) {
		mHomeName.setVisibility(visibility);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mCr = mContext.getContentResolver();
		mProEditView = (AutoCompleteTextView) findViewById(R.id.input_province);
		mCityEditView = (AutoCompleteTextView) findViewById(R.id.input_city);
		mDisEditView = (AutoCompleteTextView) findViewById(R.id.input_district);
		
		try {
			Field feild =  AutoCompleteTextView.class.getDeclaredField("mThreshold");
			feild.setAccessible(true);
			feild.set(mProEditView, 0);
			feild.set(mCityEditView, 0);
			feild.set(mDisEditView, 0);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		mProEditView.setDropDownBackgroundResource(R.color.default_pop_background);
		mCityEditView.setDropDownBackgroundResource(R.color.default_pop_background);
		mDisEditView.setDropDownBackgroundResource(R.color.default_pop_background);
		
		mProEditView.setAdapter(new AddressAdapter(mContext, MODE_PROVINCE, true));
		mCityEditView.setAdapter(new AddressAdapter(mContext, MODE_CITY, true));
		mDisEditView.setAdapter(new AddressAdapter(mContext, MODE_DISTRICT, true));

		
		mAddressEditView = (EditText) findViewById(R.id.address);
		mHomeName = (EditText) findViewById(R.id.homeName);
	}
	
	private void initHomeView(HomeObject homeObject) {
		if (homeObject == null) {
			mHomeName.getText().clear();
			mProEditView.getText().clear();
			mCityEditView.getText().clear();
			mDisEditView.getText().clear();
			mAddressEditView.getText().clear();
		} else {
			if (!TextUtils.isEmpty(homeObject.mHomeName)) {
				mHomeName.setText(homeObject.mHomeName);
			}
			
			mProEditView.setText(homeObject.mHomeProvince);
			mCityEditView.setText(homeObject.mHomeCity);
			mDisEditView.setText(homeObject.mHomeDis);
			mAddressEditView.setText(homeObject.mHomePlaceDetail);
		}
		
	}
	
	private class AddressAdapter extends CursorAdapter {
		private int mode;

		public AddressAdapter(Context context, int mode, boolean autoRequery) {
			super(context, null, autoRequery);
			this.mode = mode;
		}
		
		
		
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
			}
			return convertView;
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

		@Override
		public CharSequence convertToString(Cursor cursor) {
			DebugUtils.logD(TAG, "convertToString");
			switch(mode){
			case MODE_PROVINCE:
			case MODE_CITY:
			case MODE_DISTRICT:
				break;
			}
			return cursor.getString(1);
		}

		public long getItemId(int position) {
			switch(mode){
			case MODE_PROVINCE:
				break;
			case MODE_CITY:
				break;
			case MODE_DISTRICT:
				break;
			}
			return ((Cursor) getItem(position)).getLong(0);
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
}
