package com.bestjoy.app.haierwarrantycard.ui;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.bestjoy.app.haierwarrantycard.utils.SQLiteDatabaseHelper;

public class RegisterConfirmActivity extends BaseActionbarActivity implements OnTouchListener{
	private static final String TAG = "RegisterActivity";

	private EditText mProEditView;
	private EditText mCityEditView;
	private EditText mDisEditView;
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

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return ;
		}
		setContentView(R.layout.activity_register);
		this.initViews();
	}

	private void initViews() {
		mProEditView = (EditText) findViewById(R.id.edit_province);
		mCityEditView = (EditText) findViewById(R.id.edit_city);
		mDisEditView = (EditText) findViewById(R.id.edit_district);
		
		mProEditView.setInputType(InputType.TYPE_NULL);
		mProEditView.setOnTouchListener(this);
		mCityEditView.setInputType(InputType.TYPE_NULL);
		mCityEditView.setOnTouchListener(this);
		mDisEditView.setInputType(InputType.TYPE_NULL);
		mDisEditView.setOnTouchListener(this);
		
		database = SQLiteDatabaseHelper.openDatabase(this);
		mSqlProvince = "select * from T_Province";
		mSqlCity = "select * from T_City";
		mSqlDistrict = "select * from T_District";
		mCursor = database.rawQuery(mSqlProvince, null);
		
		popupView = getLayoutInflater().inflate(R.layout.layout_popupwindow, null);
		
		mAdapter = new AddressAdapter(this, mCursor);
		gridView = (GridView) popupView.findViewById(R.id.gridview);
		gridView.setAdapter(mAdapter);

		final Display display = this.getWindow().getWindowManager().getDefaultDisplay();
		if (display != null)
		{
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
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setAnimationStyle(R.style.AnimationPreview);  
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
	}
	private OnItemClickListener gridItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			switch (mEditMode) {
				case MODE_PROVINCE:
					if(position < mCursor.getCount()){
						mCursor.moveToPosition(position);
						mProName = mCursor.getString(mCursor.getColumnIndex("ProName"));
						mProID = mCursor.getString(mCursor.getColumnIndex("ProID"));
						mProEditView.setText(mProName);
					}
					break;
				case MODE_CITY:
					if(position < mCursor.getCount()){
						mCursor.moveToPosition(position);
						mCityName = mCursor.getString(mCursor.getColumnIndex("CityName"));
						mCityID = mCursor.getString(mCursor.getColumnIndex("CityID"));
						mCityEditView.setText(mCityName);
					}
					break;
				case MODE_DISTRICT:
					if(position < mCursor.getCount()){
						mCursor.moveToPosition(position);
						mDisName = mCursor.getString(mCursor.getColumnIndex("DisName"));
						mDisID = mCursor.getString(mCursor.getColumnIndex("Id"));
						mDisEditView.setText(mDisName);
					}
					break;
			}
			mPopupWindow.dismiss();
		}
	};
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, RegisterConfirmActivity.class);
		context.startActivity(intent);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (view.getId()) {
			case R.id.edit_province:
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mEditMode = MODE_PROVINCE;
						mCursor = database.rawQuery(mSqlProvince, null);
						mAdapter = new AddressAdapter(this, mCursor);
						gridView.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
						break;
					case MotionEvent.ACTION_UP:
						mPopupWindow.showAsDropDown(view);
						break;
				}
				break;
			case R.id.edit_city:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mEditMode = MODE_CITY;
					if(mProName != null) {
						mSqlCity = "select * from T_City where proID = '" + mProID + "'";
						mCursor = database.rawQuery(mSqlCity, null);
						mAdapter = new AddressAdapter(this, mCursor);
						gridView.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
					}
					break;
				case MotionEvent.ACTION_UP:
					if(mProName != null) {						
						mPopupWindow.showAsDropDown(view);
					} else {
						Toast.makeText(RegisterConfirmActivity.this, R.string.input_province_tips, Toast.LENGTH_SHORT).show();
					}
					break;
			}
				break;
			case R.id.edit_district:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mEditMode = MODE_DISTRICT;
					if(mCityName != null) {
						mSqlDistrict = "select * from T_District where CityID = '" + mCityID + "'";
						mCursor = database.rawQuery(mSqlDistrict, null);
						mAdapter = new AddressAdapter(this, mCursor);
						gridView.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
					}
					break;
				case MotionEvent.ACTION_UP:
					if(mCityName != null) {					
						mPopupWindow.showAsDropDown(view);
					} else {
						Toast.makeText(RegisterConfirmActivity.this, R.string.input_city_tips, Toast.LENGTH_SHORT).show();
					}
					break;
			}
				break;
		}
		return false;
	}
	class AddressAdapter extends BaseAdapter {
		Context context;
		Cursor cursor;
		LayoutInflater mInflater = null;

		public AddressAdapter(Context context, Cursor maps) {
			this.context = context;
			this.cursor = maps;
			mInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.grid_item, null);
				convertView.setLayoutParams(new GridView.LayoutParams(70, 40));
			}
			
			if(mEditMode == MODE_PROVINCE) {
				if(position < cursor.getCount()) {
					cursor.moveToPosition(position);
					TextView tv = (TextView)convertView.findViewById(R.id.item);
					tv.setText(cursor.getString(cursor.getColumnIndex("ProName")));
					tv.setTextColor(Color.RED);
				}
			} else if(mEditMode == MODE_CITY) {
				if(position < cursor.getCount() && mProName != null) {
					cursor.moveToPosition(position);
					TextView tv = (TextView)convertView.findViewById(R.id.item);
					tv.setText(cursor.getString(cursor.getColumnIndex("CityName")));
					tv.setTextColor(Color.RED);
				}
			} else if (mEditMode == MODE_DISTRICT) {
				if(position < cursor.getCount() && mCityName != null) {
					cursor.moveToPosition(position);
					TextView tv = (TextView)convertView.findViewById(R.id.item);
					tv.setText(cursor.getString(cursor.getColumnIndex("DisName")));
					tv.setTextColor(Color.RED);
				}
			}
			return convertView;

		}

		public int getCount() {
			return cursor.getCount();
		}

		public Object getItem(int position) {
			return cursor.getColumnName(position);
		}

		public long getItemId(int position) {
			return position;
		}
	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }
}
