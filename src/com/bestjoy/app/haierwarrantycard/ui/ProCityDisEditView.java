package com.bestjoy.app.haierwarrantycard.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;

public class ProCityDisEditView implements OnTouchListener {
	private static final String TAG = "ProCityDisEditView";
	private Context mContext;
	private EditText mProEditView;
	private EditText mCityEditView;
	private EditText mDisEditView;
	private int mProEditViewId;
	private int mCityEditViewId;
	private int mDisEditViewId;
	private SQLiteDatabase database;
	private View popupView;
	private PopupWindow mPopupWindow;
	private GridView gridView;
	private int screenWidth;
	private int screenHeight;
	private Cursor mCursor;
	private String mProName;
	private String mProID;
	private String mCityName;
	private String mCityID;
	private String mDisName;
	private String mDisID;
	private String mSqlProvince;
	private String mSqlCity;
	private String mSqlDistrict;
	private int mEditMode;
	private AddressAdapter mAdapter;
	private static final int MODE_PROVINCE = 1;
	private static final int MODE_CITY = MODE_PROVINCE + 1;
	private static final int MODE_DISTRICT = MODE_CITY + 1;

	public ProCityDisEditView(Context context, int editProvince, int editCity, int editDistrict) {
		mContext = context;
		initViews(editProvince, editCity, editDistrict);
	}
	
	public String getProName() {
		return this.mProName;
	}

	public String getCityName() {
		return this.mCityName;
	}

	public String getDisName() {
		return this.mDisName;
	}
	private void initViews(int editProvince, int editCity, int editDistrict) {
		mProEditViewId = editProvince;
		mCityEditViewId = editCity;
		mDisEditViewId = editDistrict;
		mProEditView = (EditText) ((Activity) mContext).findViewById(mProEditViewId);
		mCityEditView = (EditText) ((Activity) mContext).findViewById(mCityEditViewId);
		mDisEditView = (EditText) ((Activity) mContext).findViewById(mDisEditViewId);
		
		mProEditView.setInputType(InputType.TYPE_NULL);
		mProEditView.setOnTouchListener(this);
		mCityEditView.setInputType(InputType.TYPE_NULL);
		mCityEditView.setOnTouchListener(this);
		mDisEditView.setInputType(InputType.TYPE_NULL);
		mDisEditView.setOnTouchListener(this);
		
		database = SQLiteDatabase.openOrCreateDatabase(mContext.getDatabasePath(HaierDBHelper.DB_DEVICE_NAME), null);
		
		popupView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_popupwindow, null);
		
		mAdapter = new AddressAdapter(mCursor);
		gridView = (GridView) popupView.findViewById(R.id.gridview);
		gridView.setAdapter(mAdapter);

		final Display display = ((Activity) mContext).getWindow().getWindowManager().getDefaultDisplay();
		if (display != null) {
			// 获取屏幕大小
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
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (view.getId() == mProEditViewId) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mEditMode = MODE_PROVINCE;
//					mSqlProvince = "select * from T_Province where proID = '" + mProID + "'";
					mSqlProvince = "select * from T_Province";
					mCursor = database.rawQuery(mSqlProvince, null);
					mAdapter = new AddressAdapter(mCursor);
					gridView.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					break;
				case MotionEvent.ACTION_UP:
					mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, 300, true);
					mPopupWindow.setAnimationStyle(R.style.AnimationPreview);  
					mPopupWindow.setTouchable(true);
					mPopupWindow.setOutsideTouchable(true);
					mPopupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
					mPopupWindow.showAsDropDown(view, 10, 10);
					break;
			}
		} else if (view.getId() == mCityEditViewId) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mEditMode = MODE_CITY;
					if (mProName != null) {
						mSqlCity = "select * from T_City where proID = '" + mProID + "'";
						mCursor = database.rawQuery(mSqlCity, null);
						mAdapter = new AddressAdapter(mCursor);
						gridView.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (mProName != null) {
						mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, 300, true);
						mPopupWindow.setAnimationStyle(R.style.AnimationPreview);  
						mPopupWindow.setTouchable(true);
						mPopupWindow.setOutsideTouchable(true);
						mPopupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
						mPopupWindow.showAsDropDown(view, 0, 0);
					} else {
						Toast.makeText(mContext, R.string.input_province_tips,
								Toast.LENGTH_SHORT).show();
					}
					break;
			}
		} else if (view.getId() == mDisEditViewId) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mEditMode = MODE_DISTRICT;
					if (mCityName != null) {
						mSqlDistrict = "select * from T_District where CityID = '" + mCityID + "'";
						mCursor = database.rawQuery(mSqlDistrict, null);
						mAdapter = new AddressAdapter(mCursor);
						gridView.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (mCityName != null) {
						mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, 300, true);
						mPopupWindow.setAnimationStyle(R.style.AnimationPreview);  
						mPopupWindow.setTouchable(true);
						mPopupWindow.setOutsideTouchable(true);
						mPopupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
						mPopupWindow.showAsDropDown(view, 0, 0);
					} else {
						Toast.makeText(mContext, R.string.input_city_tips, Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
		return false;
	}
	
	private OnItemClickListener gridItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			switch (mEditMode) {
				case MODE_PROVINCE:
					if (position < mCursor.getCount()) {
						mCursor.moveToPosition(position);
						mProName = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_PRO_NAME));
						mProID = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_PRO_ID));
						mProEditView.setText(mProName);
					}
					break;
				case MODE_CITY:
					if (position < mCursor.getCount()) {
						mCursor.moveToPosition(position);
						mCityName = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_CITY_NAME));
						mCityID = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_CITY_ID));
						mCityEditView.setText(mCityName);
					}
					break;
				case MODE_DISTRICT:
					if (position < mCursor.getCount()) {
						mCursor.moveToPosition(position);
						mDisName = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_DIS_NAME));
						mDisID = mCursor.getString(mCursor.getColumnIndex(HaierDBHelper.DEVICE_DIS_ID));
						mDisEditView.setText(mDisName);
					}
					break;
			}
			mPopupWindow.dismiss();
		}
	};
	class AddressAdapter extends BaseAdapter {
		Cursor cursor;
		LayoutInflater mInflater = null;

		public AddressAdapter(Cursor maps) {
			this.cursor = maps;
			mInflater = LayoutInflater.from(mContext);
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
					viewHolder._title.setText(cursor.getString(cursor.getColumnIndex(HaierDBHelper.DEVICE_PRO_NAME)));
				}
			} else if(mEditMode == MODE_CITY) {
				if (position < cursor.getCount() && mProName != null) {
					cursor.moveToPosition(position);
					viewHolder._title.setText(cursor.getString(cursor.getColumnIndex(HaierDBHelper.DEVICE_CITY_NAME)));
				}
			} else if (mEditMode == MODE_DISTRICT) {
				if (position < cursor.getCount() && mCityName != null) {
					cursor.moveToPosition(position);
					viewHolder._title.setText(cursor.getString(cursor.getColumnIndex(HaierDBHelper.DEVICE_DIS_NAME)));
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
}
