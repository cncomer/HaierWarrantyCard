package com.bestjoy.app.haierwarrantycard.ui;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
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


public class RegisterConfirmActivity extends BaseActionbarActivity{
	private static final String TAG = "RegisterActivity";

	private ProCityDisEditView mPcdEditView;

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
		mPcdEditView = new ProCityDisEditView(this, R.id.edit_province, R.id.edit_city, R.id.edit_district);
	}

	public static void startIntent(Context context) {
		Intent intent = new Intent(context, RegisterConfirmActivity.class);
		context.startActivity(intent);
	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }
}
