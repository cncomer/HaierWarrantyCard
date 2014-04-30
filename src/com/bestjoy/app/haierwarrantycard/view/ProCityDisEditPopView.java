package com.bestjoy.app.haierwarrantycard.view;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;

public class ProCityDisEditPopView implements OnTouchListener {
	private static final String TAG = "ProCityDisEditView";
	private Context mContext;
	private EditText mProEditView;
	private EditText mCityEditView;
	private EditText mDisEditView;
	private EditText mPlaceDetail;
	private View popupView;
	private PopupWindow mPopupWindow;
	private GridView gridView;
	private int screenWidth;
	private int screenHeight;
	private Cursor mCursor;
	private String mProID;
	private String mCityID;
	private int mEditMode;
	private HomeObject mHomeObject;
	private AddressAdapter mAddressAdapter;
	private static final int MODE_PROVINCE = 1;
	private static final int MODE_CITY = MODE_PROVINCE + 1;
	private static final int MODE_DISTRICT = MODE_CITY + 1;

	private static final String[] PRO_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_PRO_ID,
		HaierDBHelper.DEVICE_PRO_NAME,
	};
	
	private static final String[] CITY_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_CITY_ID,
		HaierDBHelper.DEVICE_CITY_NAME,
		HaierDBHelper.DEVICE_CITY_PID,
	};
	
	private static final String[] DIS_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_DIS_ID,
		HaierDBHelper.DEVICE_DIS_NAME,
		HaierDBHelper.DEVICE_DIS_CID,
	};
	public ProCityDisEditPopView(Context context, View view) {
		mContext = context;
		mHomeObject = new HomeObject();
		initViews(view);
		intiData();
	}
	public ProCityDisEditPopView(Context context) {
		mContext = context;
		mHomeObject = new HomeObject();
		initViews(context);
		intiData();
	}
	
	private void intiData() {
		popupView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_popupwindow, null);
		
		mAddressAdapter = new AddressAdapter();
		gridView = (GridView) popupView.findViewById(R.id.gridview);
		gridView.setAdapter(mAddressAdapter);

		final Display display = ((Activity) mContext).getWindow().getWindowManager().getDefaultDisplay();
		if (display != null) {
			screenWidth = display.getWidth();
			screenHeight = display.getHeight();
		}
		
		int size = screenWidth > screenHeight ? screenWidth : screenHeight;
		
		gridView.setHorizontalSpacing(((int) (size * 0.01)));
		gridView.setVerticalSpacing(((int) (size * 0.01)));
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setNumColumns(GridView.AUTO_FIT);
		gridView.setColumnWidth(((int) (size * 0.15)));
		gridView.setOnItemClickListener(gridItemClickListener);
		mAddressAdapter = new AddressAdapter();
		gridView.setAdapter(mAddressAdapter);
	}

	public String getProName() {
		return mHomeObject.mHomeProvince;
	}

	public String getCityName() {
		return mHomeObject.mHomeCity;
	}

	public String getDisName() {
		return mHomeObject.mHomeDis;
	}

	private void initViews(Context context) {
		mProEditView = (EditText) ((Activity) context).findViewById(R.id.edit_province);
		mCityEditView = (EditText) ((Activity) context).findViewById(R.id.edit_city);
		mDisEditView = (EditText) ((Activity) context).findViewById(R.id.edit_district);
		mPlaceDetail = (EditText) ((Activity) context).findViewById(R.id.edit_place_detail);

		mProEditView.setOnTouchListener(this);
		mCityEditView.setOnTouchListener(this);
		mDisEditView.setOnTouchListener(this);
		mProEditView.setInputType(InputType.TYPE_NULL);
		mCityEditView.setInputType(InputType.TYPE_NULL);
		mDisEditView.setInputType(InputType.TYPE_NULL);
	}
	private void initViews(View view) {
		mProEditView = (EditText) view.findViewById(R.id.edit_province);
		mCityEditView = (EditText) view.findViewById(R.id.edit_city);
		mDisEditView = (EditText) view.findViewById(R.id.edit_district);
		mPlaceDetail = (EditText) view.findViewById(R.id.edit_place_detail);
		
		mProEditView.setOnTouchListener(this);
		mCityEditView.setOnTouchListener(this);
		mDisEditView.setOnTouchListener(this);
		mProEditView.setInputType(InputType.TYPE_NULL);
		mCityEditView.setInputType(InputType.TYPE_NULL);
		mDisEditView.setInputType(InputType.TYPE_NULL);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (view.getId() == mProEditView.getId()) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mEditMode = MODE_PROVINCE;
					mCursor = mContext.getContentResolver().query(BjnoteContent.Province.CONTENT_URI, PRO_PROJECTION, null, null, null);
					mAddressAdapter.changeAddressData(mCursor);
					break;
				case MotionEvent.ACTION_UP:
					initPopWindow(view);
					break;
			}
		} else if (view.getId() == mCityEditView.getId()) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mEditMode = MODE_CITY;
					if (mHomeObject.mHomeProvince != null) {
						if(mProID == null) break;
						mCursor = mContext.getContentResolver().query(
								BjnoteContent.City.CONTENT_URI, CITY_PROJECTION,
								HaierDBHelper.DEVICE_PRO_ID + "=?",
								new String[] { mProID, }, null);
						mAddressAdapter.changeAddressData(mCursor);
					}
					break;
				case MotionEvent.ACTION_UP:
					if (mHomeObject.mHomeProvince != null) {
						initPopWindow(view);
					} else {
						Toast.makeText(mContext, R.string.input_province_tips,
								Toast.LENGTH_SHORT).show();
					}
					break;
			}
		} else if (view.getId() == mDisEditView.getId()) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mEditMode = MODE_DISTRICT;
					if (mHomeObject.mHomeCity != null) {
						if(mCityID == null) break;
						mCursor = mContext.getContentResolver().query(
								BjnoteContent.District.CONTENT_URI, DIS_PROJECTION,
								HaierDBHelper.DEVICE_CITY_ID + "=?",
								new String[] { mCityID, }, null);
						mAddressAdapter.changeAddressData(mCursor);
					}
					break;
				case MotionEvent.ACTION_UP:
					if (mHomeObject.mHomeCity != null) {
						initPopWindow(view);
					} else {
						Toast.makeText(mContext, R.string.input_city_tips, Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
		return false;
	}
	
	private void initPopWindow(View view) {
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, 150, true);
		mPopupWindow.setAnimationStyle(R.style.AnimationPreview);  
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
		mPopupWindow.showAsDropDown(view, 0, 0);
		
	}

	private OnItemClickListener gridItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			switch (mEditMode) {
				case MODE_PROVINCE:
					if (position < mCursor.getCount()) {
						mCursor.moveToPosition(position);
						mHomeObject.mHomeProvince = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_PRO_NAME));
						mProID = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_PRO_ID));
						mProEditView.setText(mHomeObject.mHomeProvince);
						mCityEditView.getText().clear();
						mDisEditView.getText().clear();
					}
					break;
				case MODE_CITY:
					if (position < mCursor.getCount()) {
						mCursor.moveToPosition(position);
						mHomeObject.mHomeCity = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_CITY_NAME));
						mCityID = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_CITY_ID));
						mCityEditView.setText(mHomeObject.mHomeCity);
						mDisEditView.getText().clear();
					}
					break;
				case MODE_DISTRICT:
					if (position < mCursor.getCount()) {
						mCursor.moveToPosition(position);
						mHomeObject.mHomeDis = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_DIS_NAME));
						mDisEditView.setText(mHomeObject.mHomeDis);
					}
					break;
			}
			mPopupWindow.dismiss();
		}
	};
	class AddressAdapter extends BaseAdapter {
		Cursor cursor;
		LayoutInflater mInflater = null;

		public AddressAdapter() {
			mInflater = LayoutInflater.from(mContext);
		}
		public void changeAddressData(Cursor cr) {
			this.cursor = cr;
			notifyDataSetChanged();
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder._title = (TextView) convertView;
				convertView .setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(mEditMode == MODE_PROVINCE) {
				if (position < cursor.getCount()) {
					cursor.moveToPosition(position);
					int index = cursor.getColumnIndex(HaierDBHelper.DEVICE_PRO_NAME);
					if(index >= 0)
					viewHolder._title.setText(cursor.getString(index));
				}
			} else if(mEditMode == MODE_CITY) {
				if (position < cursor.getCount() && mHomeObject.mHomeProvince != null) {
					cursor.moveToPosition(position);
					int index = cursor.getColumnIndex(HaierDBHelper.DEVICE_CITY_NAME);
					if(index >= 0)
					viewHolder._title.setText(cursor.getString(index));
				}
			} else if (mEditMode == MODE_DISTRICT) {
				if (position < cursor.getCount() && mHomeObject.mHomeCity != null) {
					cursor.moveToPosition(position);
					int index = cursor.getColumnIndex(HaierDBHelper.DEVICE_DIS_NAME);
					if(index >= 0)
					viewHolder._title.setText(cursor.getString(index));
				}
			}
			return convertView;

		}

		public int getCount() {
			return cursor != null ? cursor.getCount() :0;
		}

		public Object getItem(int position) {
			return cursor.getColumnName(position);
		}

		public long getItemId(int position) {
			return position;
		}
	}
	
	private static class ViewHolder {
		private TextView _title;
	}

	public void setHomeObject(HomeObject homeObject) {
		if(homeObject == null) {
			homeObject = new HomeObject();
		}
		mHomeObject = homeObject;
		updateHomeView();
	}
	
	public void updateHomeView() {
		MyApplication.getInstance().showMessage("updateHomeView");
		mProEditView.setText(mHomeObject.mHomeProvince);
		mCityEditView.setText(mHomeObject.mHomeCity);
		mDisEditView.setText(mHomeObject.mHomeDis);
		mPlaceDetail.setText(mHomeObject.mHomePlaceDetail);
		if(mHomeObject.mHomeAid > 0) {
			mProEditView.setEnabled(false);
			mCityEditView.setEnabled(false);
			mDisEditView.setEnabled(false);
		} else {
			mProEditView.setEnabled(true);
			mCityEditView.setEnabled(true);
			mDisEditView.setEnabled(true);
		}
	}

	public HomeObject getHomeObject() {
		if(mHomeObject == null) {
			mHomeObject = new HomeObject();
		}
		mHomeObject.mHomeProvince = mProEditView.getText().toString().trim();
		mHomeObject.mHomeCity = mCityEditView.getText().toString().trim();
		mHomeObject.mHomeDis = mDisEditView.getText().toString().trim();
		mHomeObject.mHomePlaceDetail = mPlaceDetail.getText().toString().trim();
		
		return mHomeObject;
	}
}
